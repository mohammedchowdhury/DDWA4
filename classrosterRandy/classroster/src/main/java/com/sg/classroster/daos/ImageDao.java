/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.classroster.daos;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author rhash
 */
public interface ImageDao {
    public String saveImage(MultipartFile file, String fileName, String directory);
    
    public String updateImage(MultipartFile file, String fileName, String directory);
    
    public boolean deleteImage(String fileName);
}
