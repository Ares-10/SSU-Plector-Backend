package ssuPlector.ai.naverCloud.service;

import java.io.File;

public interface ClovaService {

    String soundToText(File file);

    String summarizeText(String text);
}
