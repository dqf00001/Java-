package org.fatmansoft.teach.models;


import org.springframework.data.relational.core.sql.In;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name="stu_in_course")
public class StuInCourse {
    private int  id;
    private String  scores;
    private Student student;
    private Course  course;

    @Transient
    private static int maxId=0;

    public StuInCourse(){
        this.id=maxId+1;
        maxId++;
        this.scores="暂无";
    }

    public StuInCourse(Student s,Course c){
        this.id=maxId+1;
        maxId++;
        this.course=c;
        this.student=s;
        this.scores="暂无";
    }

    @Id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Size(max=12)
    public String getScores() {
        return scores;
    }
    public void setScores(String scores) {
        this.scores = scores;
    }

    @ManyToOne
    @JoinColumn(name="student_id")
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    @ManyToOne
    @JoinColumn(name="course_id")
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }

}
