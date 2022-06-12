package com.example.demo.controller;


import com.example.demo.service.DocPdfService;
import com.example.demo.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class DocPdfController {
    Map<String, Map<String, Object>> map = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    Map<String, Object> str = new HashMap<>();
    List<String> variablesList = new ArrayList<>();

    String tempo = "";


    @Autowired
    S3Service s3Service;

    @Autowired
    DocPdfService generateService;

    String UPLOAD_DIR = "C:\\Users\\Shubham Srivastava\\IdeaProjects\\DocPdf\\src\\main\\resources\\templates";

    @PostMapping("/register")
    public String getFile(@RequestParam("template") MultipartFile multipartFile) throws IOException {

        tempo = multipartFile.getOriginalFilename();

        String uploaded = s3Service.uploadToBucket(multipartFile);
        s3Service.downloadFromBucket(tempo);

        //Files.copy(multipartFile.getInputStream() ,
        //Paths.get(UPLOAD_DIR+ File.separator+multipartFile.getOriginalFilename()) , StandardCopyOption.REPLACE_EXISTING);



        variablesList.clear();
        str.clear();
        data.clear();
        map.clear();

        return "uploaded";
    }


    //to get all variables of that particular file
    @GetMapping("/register")
    public Map<String, Map<String, Object>> getData() throws IOException {

        String fileName = UPLOAD_DIR + File.separator + tempo;
        Path filePath = Paths.get(fileName);
        byte[] fileBytes = Files.readAllBytes(filePath);
        String content = new String(fileBytes, StandardCharsets.UTF_8);


        Matcher m = Pattern.compile("\\$\\{(.*?)}").matcher(content);

        while (m.find()) {
            String variableName = m.group().substring(2, m.group().length() - 1);
            if (!variablesList.contains(variableName)) {
                variablesList.add(variableName);
            }
        }


        str.clear();
        data.clear();
        map.clear();


        for (String s : variablesList) {
            data.put(s, new String());
        }


        str.put("template", tempo);
        map.put("filename", str);
        map.put("data", data);

        variablesList.clear();

        return map;
    }


    @PostMapping("/generate")
    public String postData(@RequestBody List<Map<String, Map<String, Object>>> receivedData) throws FileNotFoundException {

        for (Map<String, Map<String, Object>> receivedDatum : receivedData) {
            Map<String, Object> s = receivedDatum.get("data");

            String temp = String.valueOf(receivedDatum.get("filename").get("template"));
            //String  = rep.substring(0,rep.length()-5);
            System.out.println("filename " + temp);

            generateService.generateFile(temp, s);
        }

        generateService.finishGenerating();

        return "Posted it";

    }
}
