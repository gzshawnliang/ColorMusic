package com.example.springdemo.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;


@Controller
//@RequestMapping(value="/upload/")
public class UploadController
{
    @Value("${upload.path}")
    private String globalUploadPath;

    private String userUploadPath;
    private void init() {
        try {
            Files.createDirectories(Paths.get(userUploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    private String GetUserUploadPath() {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        System.err.println(sessionId);
        return Paths.get(globalUploadPath).resolve(sessionId).toString();
        //return  globalUploadPath + "/" + sessionId;

    }

    public void deleteAll() {
        Path root = Paths.get(globalUploadPath);
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public String index() {
        return "home";
    }

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    //@RequestParam List<MultipartFile> files
    @ResponseBody
    public String save(@RequestParam("photo") MultipartFile photo)  {
        // Save the files
        // for (MultipartFile file : files) {
        // }

        // Return an empty string to signify success
        try
        {
            System.err.println(new FileSystemResource("").getFile().getAbsolutePath());
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            System.err.println(sessionId);
            userUploadPath = GetUserUploadPath();
            Path root = Paths.get(userUploadPath);
            if (!Files.exists(root)) {
                init();
            }

            Files.copy(photo.getInputStream(), root.resolve(photo.getOriginalFilename()));

            //photo.transferTo(new File("C:\\iso" + photo.getOriginalFilename()));
        }
        catch (IOException exception)
        {
            //throw exception;
            System.err.println(exception.toString());
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }

        return "";
    }

    @RequestMapping(value = "/file/remove", method = RequestMethod.POST)
    public @ResponseBody String remove(@RequestParam String[] fileNames) {
        // Remove the files
        userUploadPath = GetUserUploadPath();
        Path root = Paths.get(userUploadPath);
         for (String fileName : fileNames) {
                try {
                    Files.deleteIfExists(root.resolve(fileName)) ;
                }
                catch (Exception e)
                {
                    System.err.println(e.toString());
                    throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                }
         }
        // Return an empty string to signify success
        return "";
    }
}
