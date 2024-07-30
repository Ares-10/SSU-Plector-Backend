package ssuPlector.service.developer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ssuPlector.aws.s3.AmazonS3Manager;
import ssuPlector.converter.DeveloperConverter;
import ssuPlector.converter.ImageConverter;
import ssuPlector.domain.Developer;
import ssuPlector.domain.Image;
import ssuPlector.domain.Uuid;
import ssuPlector.domain.category.DevLanguage;
import ssuPlector.domain.category.DevTools;
import ssuPlector.domain.category.TechStack;
import ssuPlector.dto.request.DeveloperDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperListRequestDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperMatchingDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperRequestDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperUpdateRequestDTO;
import ssuPlector.dto.response.DeveloperDTO.DeveloperSearchDTO;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;
import ssuPlector.redis.service.DeveloperHitsService;
import ssuPlector.repository.UuidRepository;
import ssuPlector.repository.developer.DeveloperRepository;
import ssuPlector.service.BaseMethod;

@Service
@RequiredArgsConstructor
public class DeveloperServiceImpl implements DeveloperService {
    private final DeveloperRepository developerRepository;
    private final DeveloperHitsService developerHitsService;
    private final BaseMethod baseMethod;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    @Value("${ai.url}")
    private String aiUrl;

    @Override
    @Transactional
    public Long createDeveloper(String email, DeveloperRequestDTO requestDTO) {
        Developer startDeveloper =
                developerRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.DEVELOPER_NOT_FOUND));
        ArrayList<DevLanguage> newLanguage = baseMethod.fillList(requestDTO.getLanguageList());
        ArrayList<DevTools> newDevTool = baseMethod.fillList(requestDTO.getDevToolList());
        ArrayList<TechStack> newTechStack = baseMethod.fillList(requestDTO.getTechStackList());

        startDeveloper.setStartDeveloper(requestDTO, newLanguage, newDevTool, newTechStack);

        return startDeveloper.getId();
    }

    @Override
    @Transactional
    public Long updateDeveloper(Long id, DeveloperUpdateRequestDTO requestDTO) {
        Developer developer =
                developerRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.DEVELOPER_NOT_FOUND));
        ArrayList<DevLanguage> newLanguage = baseMethod.fillList(requestDTO.getLanguageList());
        ArrayList<DevTools> newDevTool = baseMethod.fillList(requestDTO.getDevToolList());
        ArrayList<TechStack> newTechStack = baseMethod.fillList(requestDTO.getTechStackList());

        developer.updateDeveloper(requestDTO, newLanguage, newDevTool, newTechStack);

        return developer.getId();
    }

    @Override
    public Developer getDeveloper(Long id, boolean isHit) {
        Developer developer =
                developerRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.DEVELOPER_NOT_FOUND));
        if (isHit) developerHitsService.incrementHits(id);
        return developer;
    }

    @Override
    public boolean existsByDeveloperId(Long id) {
        return developerRepository.existsById(id);
    }

    @Override
    @Transactional
    public void updateDeveloperHits(Long developerId, Long hit) {
        developerRepository.updateHitsById(developerId, hit);
    }

    @Override
    public List<Long> getUpdateTargetDeveloperIds(List<Long> developerIdList) {
        return developerRepository.findAllByIdIn(developerIdList).stream()
                .map(Developer::getId)
                .toList();
    }

    @Override
    public Page<Developer> getDeveloperList(DeveloperListRequestDTO requestDTO, int page) {
        Pageable pageable = PageRequest.of(page, 6);
        return developerRepository.findDevelopers(
                requestDTO.getSortType(), requestDTO.getPart(), pageable);
    }

    @Override
    @Transactional
    public Long createDummyDeveloper(
            DeveloperDTO.DummyDeveloperRequestDTO requestDTO, MultipartFile image) {
        if (developerRepository.findByEmail(requestDTO.getEmail()).isPresent())
            throw new GlobalException(GlobalErrorCode.DEVELOPER_DUPLICATE);

        Developer dummyDeveloper = DeveloperConverter.toDeveloper(requestDTO);

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());

        String developerImageUrl =
                s3Manager.uploadFile(s3Manager.generateProjectKeyName(savedUuid), image);
        Image developerImage = ImageConverter.toImage(developerImageUrl);
        dummyDeveloper.addImage(developerImage);

        return developerRepository.save(dummyDeveloper).getId();
    }

    @Override
    public List<DeveloperSearchDTO> searchDeveloper(String developerName) {
        List<Developer> developers = developerRepository.searchDeveloper(developerName);
        return developers.stream().map(DeveloperConverter::toDeveloperSearchDTO).toList();
    }

    @Override
    public List<DeveloperSearchDTO> matchDeveloper(
            String developerInfo, DeveloperMatchingDTO requestDTO) {

        // 필수조건 만족 확인
        List<Developer> essentialDeveloper =
                developerRepository.essentialMatchDeveloper(requestDTO);
        if (essentialDeveloper.size() == 0) return new ArrayList<>();

        // 선택조건 만족 확인
        Map<Long, Double> weight =
                developerRepository.matchDeveloper(essentialDeveloper, requestDTO);

        // request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody =
                String.format(
                        "{\"part\":\"%s\", \"request\":\"%s\"}",
                        requestDTO.getPart(), developerInfo);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(aiUrl, HttpMethod.POST, requestEntity, String.class);

        // response
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Long, Double> developers = new HashMap<>();

        try {
            JsonNode developersNode = objectMapper.readTree(response.getBody()).path("developers");
            Iterator<JsonNode> elements = developersNode.elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                Long developerId = element.path("developer_id").asLong();
                Double similarity = element.path("similarity").asDouble();
                developers.put(developerId, similarity);
            }

        } catch (JsonProcessingException e) {
            throw new GlobalException(GlobalErrorCode.INVALID_REQUEST_INFO);
        }

        // AI 서버에서 받은 값과 조건 계산 값 합산
        for (Long developerId : developers.keySet()) {
            Developer developer =
                    developerRepository
                            .findById(developerId)
                            .orElseThrow(
                                    () -> new GlobalException(GlobalErrorCode.DEVELOPER_NOT_FOUND));

            weight.putIfAbsent(developer.getId(), 0.0);
            weight.put(
                    developer.getId(), weight.get(developer.getId()) + developers.get(developerId));
        }

        // sort
        List<Pair<Long, Double>> sortedDeveloper =
                weight.entrySet().stream()
                        .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                        .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                        .toList();

        List<Developer> developerList =
                sortedDeveloper.stream()
                        .limit(3)
                        .map(
                                m -> {
                                    Optional<Developer> optionalDeveloper =
                                            developerRepository.findById(m.getLeft());
                                    return optionalDeveloper.orElseThrow(
                                            () ->
                                                    new GlobalException(
                                                            GlobalErrorCode.DEVELOPER_NOT_FOUND));
                                })
                        .toList();

        return developerList.stream().map(DeveloperConverter::toDeveloperSearchDTO).toList();
    }
}
