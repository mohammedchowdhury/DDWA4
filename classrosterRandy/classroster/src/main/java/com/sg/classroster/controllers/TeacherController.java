/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.classroster.controllers;

import com.sg.classroster.daos.CourseDao;
import com.sg.classroster.daos.ImageDao;
import com.sg.classroster.daos.StudentDao;
import com.sg.classroster.daos.TeacherDao;
import com.sg.classroster.dtos.Teacher;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author rhash
 */
@Controller
public class TeacherController {

    @Autowired
    TeacherDao teacherDao;

    @Autowired
    StudentDao studentDao;

    @Autowired
    CourseDao courseDao;
    
    @Autowired
    ImageDao imageDao;
    
    private final String TEACHER_UPLOAD_DIR = "Teachers";

    Set<ConstraintViolation<Teacher>> violations = new HashSet<>();

    @GetMapping("teachers")
    public String displayTeachers(Model model) {
        List<Teacher> teachers = teacherDao.getAllTeachers();
        
        model.addAttribute("teachers", teachers);
        model.addAttribute("errors", violations);
        
        return "teachers";
    }

    @PostMapping("addTeacher")
    public String addTeacher(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String fileLocation = imageDao.saveImage(file, Long.toString(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)), TEACHER_UPLOAD_DIR);
        
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String specialty = request.getParameter("specialty");

        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setSpecialty(specialty);
        teacher.setPhotoFilename(fileLocation);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(teacher);

        if (violations.isEmpty()) {
            teacherDao.addTeacher(teacher);
        }
        
        return "redirect:/teachers";
    }

    @GetMapping("deleteTeacher")
    public String deleteTeacher(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        
        Teacher teacher = teacherDao.getTeacherById(id);
        imageDao.deleteImage(teacher.getPhotoFilename());
        
        teacherDao.deleteTeacherById(id);

        return "redirect:/teachers";
    }

    @GetMapping("editTeacher")
    public String editTeacher(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Teacher teacher = teacherDao.getTeacherById(id);

        model.addAttribute("teacher", teacher);
        return "editTeacher";
    }

    @PostMapping("editTeacher")
    public String performEditTeacher(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        int id = Integer.parseInt(request.getParameter("id"));
        Teacher teacher = teacherDao.getTeacherById(id);

        teacher.setFirstName(request.getParameter("firstName"));
        teacher.setLastName(request.getParameter("lastName"));
        teacher.setSpecialty(request.getParameter("specialty"));
        
        teacher.setPhotoFilename(imageDao.updateImage(file, teacher.getPhotoFilename(), TEACHER_UPLOAD_DIR));

        teacherDao.updateTeacher(teacher);

        return "redirect:/teachers";
    }

}
