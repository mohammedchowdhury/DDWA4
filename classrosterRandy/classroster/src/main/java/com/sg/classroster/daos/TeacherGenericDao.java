/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.classroster.daos;

import com.sg.classroster.dtos.Course;
import com.sg.classroster.dtos.Student;
import com.sg.classroster.dtos.Teacher;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author rhash
 */
public class TeacherGenericDao implements Dao<Teacher>{

    @Autowired
    JdbcTemplate jdbc;
    
    @Override
    public Teacher Create(Teacher object) {
        final String INSERT_TEACHER = "INSERT INTO teacher(firstName, lastName, specialty, photoFilename) "
                + "VALUES(?,?,?,?)";
        jdbc.update(INSERT_TEACHER,
                object.getFirstName(),
                object.getLastName(),
                object.getSpecialty(),
                object.getPhotoFilename());

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        object.setId(newId);
        return object;
    }

    @Override
    public Teacher ReadById(int id) {
        try {
            final String GET_TEACHER_BY_ID = "SELECT * FROM teacher WHERE id = ?";
            return jdbc.queryForObject(GET_TEACHER_BY_ID, new TeacherDaoDB.TeacherMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Teacher> ReadAll() {
        final String GET_ALL_TEACHERS = "SELECT * FROM teacher";
        return jdbc.query(GET_ALL_TEACHERS, new TeacherDaoDB.TeacherMapper());
    }

    @Override
    public void Update(Teacher object) {
       final String UPDATE_TEACHER = "UPDATE teacher SET firstName = ?, lastName = ?, "
                + "specialty = ?, photoFilename = ? WHERE id = ?";
        jdbc.update(UPDATE_TEACHER,
                object.getFirstName(),
                object.getLastName(),
                object.getSpecialty(),
                object.getPhotoFilename(),
                object.getId());
    }

    @Override
    public void Delete(int id) {
        final String DELETE_COURSE_STUDENT = "DELETE cs.* FROM course_student cs "
                + "JOIN course c ON cs.courseId = c.Id WHERE c.teacherId = ?";
        jdbc.update(DELETE_COURSE_STUDENT, id);

        final String DELETE_COURSE = "DELETE FROM course WHERE teacherId = ?";
        jdbc.update(DELETE_COURSE, id);

        final String DELETE_TEACHER = "DELETE FROM teacher WHERE id = ?";
        jdbc.update(DELETE_TEACHER, id);
    }

    @Override
    public List<Teacher> GetAllByForeignId(Class foreignObject, int foreignId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
