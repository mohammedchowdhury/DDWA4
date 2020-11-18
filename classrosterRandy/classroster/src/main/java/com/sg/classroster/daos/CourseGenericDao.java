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
public class CourseGenericDao implements Dao<Course>{
    
    @Autowired
    JdbcTemplate jdbc;

    @Override
    public Course Create(Course object) {
        final String INSERT_COURSE = "INSERT INTO course(name, description, teacherId) "
                + "VALUES(?,?,?)";
        jdbc.update(INSERT_COURSE,
                object.getName(),
                object.getDescription(),
                object.getTeacher().getId());

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        object.setId(newId);
        insertCourseStudent(object);
        return object;
    }

    @Override
    public Course ReadById(int id) {
        try {
            final String SELECT_COURSE_BY_ID = "SELECT * FROM course WHERE id = ?";
            Course course = jdbc.queryForObject(SELECT_COURSE_BY_ID, new CourseDaoDB.CourseMapper(), id);
            course.setTeacher(getTeacherForCourse(id));
            course.setStudents(getStudentsForCourse(id));
            return course;
        } catch(DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Course> ReadAll() {
        final String SELECT_ALL_COURSES = "SELECT * FROM course";
        List<Course> courses = jdbc.query(SELECT_ALL_COURSES, new CourseDaoDB.CourseMapper());
        associateTeacherAndStudents(courses);
        return courses;
    }

    @Override
    public void Update(Course object) {
        final String UPDATE_COURSE = "UPDATE course SET name = ?, description = ?, "
                + "teacherId = ? WHERE id = ?";
        jdbc.update(UPDATE_COURSE, 
                object.getName(), 
                object.getDescription(), 
                object.getTeacher().getId(),
                object.getId());
        
        final String DELETE_COURSE_STUDENT = "DELETE FROM course_student WHERE courseId = ?";
        jdbc.update(DELETE_COURSE_STUDENT, object.getId());
        insertCourseStudent(object);
    }

    @Override
    public void Delete(int id) {
        final String DELETE_COURSE_STUDENT = "DELETE FROM course_student WHERE courseId = ?";
        jdbc.update(DELETE_COURSE_STUDENT, id);
        
        final String DELETE_COURSE = "DELETE FROM course WHERE id = ?";
        jdbc.update(DELETE_COURSE, id);
    }

    @Override
    public List<Course> GetAllByForeignId(Class foreignObject, int foreignId) {
        if(foreignObject == Teacher.class){
            final String SELECT_COURSES_FOR_TEACHER = "SELECT * FROM course WHERE teacherId = ?";
            
            List<Course> courses = jdbc.query(SELECT_COURSES_FOR_TEACHER, 
                new CourseDaoDB.CourseMapper(), foreignId);
            associateTeacherAndStudents(courses);
            
            return courses;
            
        } else if (foreignObject == Student.class){
            final String SELECT_COURSES_FOR_STUDENT = "SELECT c.* FROM course c "
                + "JOIN course_student cs ON cs.courseId = c.Id "
                + "WHERE cs.studentId = ?";
            
            List<Course> courses = jdbc.query(SELECT_COURSES_FOR_STUDENT, 
                    new CourseDaoDB.CourseMapper(), foreignId);
            associateTeacherAndStudents(courses);
            
            return courses;
        
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    //Helper Methods
    private Teacher getTeacherForCourse(int id) {
        final String SELECT_TEACHER_FOR_COURSE = "SELECT t.* FROM teacher t "
                + "JOIN course c ON c.teacherId = t.id WHERE c.id = ?";
        return jdbc.queryForObject(SELECT_TEACHER_FOR_COURSE, new TeacherDaoDB.TeacherMapper(), id);
    }

    private List<Student> getStudentsForCourse(int id) {
        final String SELECT_STUDENTS_FOR_COURSE = "SELECT s.* FROM student s "
                + "JOIN course_student cs ON cs.studentId = s.id WHERE cs.courseId = ?";
        return jdbc.query(SELECT_STUDENTS_FOR_COURSE, new StudentDaoDB.StudentMapper(), id);
    }
    
    private void associateTeacherAndStudents(List<Course> courses) {
        for (Course course : courses) {
            course.setTeacher(getTeacherForCourse(course.getId()));
            course.setStudents(getStudentsForCourse(course.getId()));
        }
    }
    
    private void insertCourseStudent(Course course) {
        final String INSERT_COURSE_STUDENT = "INSERT INTO "
                + "course_student(courseId, studentId) VALUES(?,?)";
        for(Student student : course.getStudents()) {
            jdbc.update(INSERT_COURSE_STUDENT, 
                    course.getId(),
                    student.getId());
        }
    }
    
}
