package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.relational.core.sql.In;

@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
public class Course {

    @Id
    private Integer id;

    @NotBlank
    @Size(max = 8)
    private String courseNum;

    @Size(max = 50)
    @NotBlank
    private String courseName;

    @Size(max = 50)
    private String teacher;

    @Size(max = 50)
    private String classroom;

    @Size(max = 10)
    private String means;

    private Integer hours;

    private Integer credits;

    // @ElementCollection
    // @CollectionTable(name = "stuInCourse", joinColumns = @JoinColumn(name = "student_id"))
    // @Column(name = "students")
    @ManyToMany(targetEntity = Student.class,cascade = {CascadeType.REFRESH,CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(name = "stu_in_course",joinColumns = @JoinColumn(name="course_id",referencedColumnName = "id"),inverseJoinColumns = @JoinColumn(name = "student_id",referencedColumnName = "id"))
    private List<Student> studentList = new ArrayList<>();




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getMeans() {
        return means;
    }

    public void setMeans(String means) {
        this.means = means;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public String addStu(Student s) {
        if(studentList.contains(s))
            return "Student already in course";
        else
            studentList.add(s);
        return "added";
    }

    public String removeStu(Student s) {
        if(studentList.contains(s)) {
            studentList.remove(s);
            return "removed";
        }
        else
            return "Student not exist in course";
    }

}
