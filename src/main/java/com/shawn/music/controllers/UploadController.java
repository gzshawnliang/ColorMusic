package com.shawn.music.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


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

    public void deleteAll(String path) {
        Path root = Paths.get(path);
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public String index() {
        return "home";
    }

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    //@RequestParam List<MultipartFile> files

    public @ResponseBody String save(@RequestParam("photo") MultipartFile photo,@RequestParam("newName") String newName)  {
        try
        {
            //System.err.println(new FileSystemResource("").getFile().getAbsolutePath());
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            //System.out.println(sessionId);

            userUploadPath = GetUserUploadPath();
            deleteAll(userUploadPath);

            Path root = Paths.get(userUploadPath);
            if (!Files.exists(root)) {
                init();
            }

            //FilenameUtils.getExtension photo.getName().
            var originalName=photo.getOriginalFilename();
            var ext=Optional.ofNullable(originalName).filter(f -> f.contains("."))
                .map(f -> f.substring(originalName.lastIndexOf(".") + 1));

            var newFile=root.resolve(newName + "." + ext.get().toString());
            //var newFile=root.resolve(photo.getOriginalFilename());
            //var newFile=root.resolve(newFileName);

            Files.copy(photo.getInputStream(), newFile);

            MusicService MusicService1 =new MusicService(newFile.toString());

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

        //return ResponseEntity.status(HttpStatus.OK).body("aaaaaaaa");

        return "";
    }

    @RequestMapping(value = "/file/remove", method = RequestMethod.POST)
    public @ResponseBody String remove(@RequestParam String[] fileNames) {
        // Remove the files
        userUploadPath = GetUserUploadPath();
        deleteAll(userUploadPath);

//        Path root = Paths.get(userUploadPath);
//         for (String fileName : fileNames) {
//                try {
//                    Files.deleteIfExists(root.resolve(fileName)) ;
//                }
//                catch (Exception e)
//                {
//                    System.err.println(e.toString());
//                    throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
//                }
//         }
        // Return an empty string to signify success
        return "";
    }

    @RequestMapping(value = "/file/wav", method = RequestMethod.GET)
    public @ResponseBody void downloadWav(HttpServletResponse response,@RequestParam("file") String wavFile ) throws IOException {

        userUploadPath = GetUserUploadPath();
        Path root = Paths.get(userUploadPath);

        File file = new File(root.resolve(wavFile).toString());
        InputStream in = new FileInputStream(file);

        response.setContentType("audio/wav");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }


    @RequestMapping(value = "/file/midi", method = RequestMethod.GET)
    public @ResponseBody void downloadMidi(HttpServletResponse response,@RequestParam("file") String midiFile ) throws IOException {

        userUploadPath = GetUserUploadPath();
        Path root = Paths.get(userUploadPath);

        File file = new File(root.resolve(midiFile).toString());
        InputStream in = new FileInputStream(file);

        response.setContentType("audio/midi");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }
}
