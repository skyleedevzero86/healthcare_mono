package com.sleekydz86.web.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Component
public class ImagesUtil {

    private static String imgPath;
    private static String imgTmp;

    public ImagesUtil(@Value("${imgPath}") String IMGPATH,
            @Value("${imgTmp}") String IMGTMP) {
        imgPath = IMGPATH;
        imgTmp = IMGTMP;
    }

    public static String write(MultipartFile file) throws Exception {

        String projectPath = System.getProperty("user.dir") + imgPath;
        System.out.println("projectPath = " + projectPath);

        UUID uuid = UUID.randomUUID();

        String fileName = uuid + "_" + file.getOriginalFilename();

        File savedFile = new File(projectPath, fileName);

        file.transferTo(savedFile);
        return fileName;
    }

}
