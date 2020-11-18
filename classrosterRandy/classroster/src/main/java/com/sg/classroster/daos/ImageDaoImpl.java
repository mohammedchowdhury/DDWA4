/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.classroster.daos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author rhash
 */
@Repository
public class ImageDaoImpl implements ImageDao {

    private final String RESOURCE_ROOT = "/classroster/src/main/resources/static/";

    private final String UPLOAD_DIRECTORY = "images/uploads/";

    @Override
    public String saveImage(MultipartFile file, String fileName, String directory) {
        String savedFileName = "";

        String mimetype = file.getContentType();
        if (mimetype != null && mimetype.split("/")[0].equals("image")) {
            String originalName = file.getOriginalFilename();
            String[] parts = originalName.split("[.]");
            fileName = fileName + "." + parts[parts.length - 1];

            try {
                String fullPath = RESOURCE_ROOT + UPLOAD_DIRECTORY + directory + "/";
                File dir = new File(fullPath);

                //If the directory doesn't exist
                if (!dir.exists()) {
                    //Make all directories
                    dir.mkdirs();
                }

                Path path = Paths.get(fullPath + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                savedFileName = UPLOAD_DIRECTORY + directory + "/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return savedFileName;
    }

    @Override
    public String updateImage(MultipartFile file, String fileName, String directory) {
        String savedFileName = "";

        if (fileName != null && !fileName.isEmpty()) {
            File oldFile = new File(RESOURCE_ROOT + fileName);
            oldFile.delete();

            String[] fileNameParts = fileName.split("[/]");
            fileName = fileNameParts[fileNameParts.length - 1].split("[.]")[0];
        } else {
            fileName = Long.toString(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        }

        String mimetype = file.getContentType();
        if (mimetype != null && mimetype.split("/")[0].equals("image")) {
            String originalName = file.getOriginalFilename();
            String[] parts = originalName.split("[.]");
            fileName = fileName + "." + parts[parts.length - 1];

            try {
                String fullPath = RESOURCE_ROOT + UPLOAD_DIRECTORY + directory + "/";
                File dir = new File(fullPath);

                //If the directory doesn't exist
                if (!dir.exists()) {
                    //Make all directories
                    dir.mkdirs();
                }

                Path path = Paths.get(fullPath + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                savedFileName = UPLOAD_DIRECTORY + directory + "/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return savedFileName;
    }

    @Override
    public boolean deleteImage(String fileName) {
        File oldFile = new File(RESOURCE_ROOT + fileName);
        return oldFile.delete();
    }

}
