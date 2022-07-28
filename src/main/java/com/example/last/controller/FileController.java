package com.example.last.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

// 단일 파일 전송 api
@RestController
public class FileController {

    @PostMapping("/api/fileData")
    @ApiOperation(value = "파일 업로드", notes = "파일을 업로드하는 API입니다.")
    public String upload(@RequestParam MultipartFile fileData) throws Exception {

        final String ABSOLUTE_PATH = "/Users/sopp/study/last/src/main/resources/upload";

        String fileName = fileData.getOriginalFilename();
        File destinationFile = new File(ABSOLUTE_PATH + "/" + fileName);
        System.out.println(destinationFile);

        destinationFile.getParentFile().mkdir();
        fileData.transferTo(destinationFile);
        System.out.println(fileName);
        return fileName;
    }
}