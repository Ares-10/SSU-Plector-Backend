package ssuPlector.converter;

import java.util.Collections;

import org.springframework.stereotype.Component;

import ssuPlector.domain.Developer;
import ssuPlector.domain.Image;
import ssuPlector.domain.category.SocialType;
import ssuPlector.dto.KakaoProfile;
import ssuPlector.dto.response.AuthResponseDTO.OAuthResponse;
import ssuPlector.dto.response.AuthResponseDTO.TokenRefreshResponse;

@Component
public class AuthConverter {
    public static OAuthResponse toOAuthResponse(
            String accessToken, String refreshToken, Boolean isLogin, Developer developer) {
        return OAuthResponse.builder()
                .developerId(developer.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isLogin(isLogin)
                .build();
    }

    public static Developer toDeveloper(KakaoProfile kakaoProfile) {
        Image image =
                Image.builder().imagePath(kakaoProfile.getProperties().getProfile_image()).build();
        return Developer.builder()
                .email(kakaoProfile.getKakao_account().getEmail())
                .name(kakaoProfile.getProperties().getNickname())
                .imageList(Collections.singletonList(image))
                .socialType(SocialType.KAKAO)
                .build();
    }

    public static TokenRefreshResponse tokenRefreshResponse(
            String accessToken, String refreshToken) {
        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
