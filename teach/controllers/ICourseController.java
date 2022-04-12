package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.StuInCourse;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.StuInCourseRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import javax.validation.Valid;
import java.util.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class ICourseController {
    private static Integer tempStu;


    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StuInCourseRepository stuInCourseRepository;


    @PostMapping("/icourseInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse icourseInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer stuId = dataRequest.getInteger("studentId");
        tempStu=stuId;
        Student s=null;
        List<Course> courseL =null;
        Optional<Student> op;
        if(stuId != null) {
            op= studentRepository.findById(stuId);
            if(op.isPresent()) {
                s = op.get();
            }
            courseL=s.getCourseList();
        }
        List dataList = new ArrayList();
        Course c;
        Map m;
        for(int i = 0; i < courseL.size();i++) {
            c = courseL.get(i);
            m = new HashMap();
            m.put("id", c.getId());
            m.put("studentId",stuId);
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("teacher",c.getTeacher());
            m.put("classroom",c.getClassroom());
            if("1".equals(c.getMeans()))
                m.put("means","线下");
            else if("2".equals(c.getMeans()))
                m.put("means","线上");
            else
                m.put("means","线上、线下结合");
            m.put("hours",c.getHours());
            m.put("credits",c.getCredits());
            Optional<String> scoresL;
            String score = null;
            scoresL = courseRepository.findScoresByStudentCourseId(c.getId(),stuId);
            if(scoresL.isPresent()) {
                score = scoresL.get();
            }
            if(score!=null)
                m.put("scores", score);
            else
                m.put("scores", "暂无");
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/icourseEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse icourseEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer stuId = tempStu;
        Student s;
        Course c;
        Optional<Student> op;
        op=studentRepository.findById(stuId);
        s=op.get();
        Map m;
        int i;
        List courseIdList = new ArrayList();
        /*
        List<Integer> cIdList =courseRepository.findCourseIdListExceptStudent(stuId);
        for(i = 0; i <cIdList.size();i++) {
            c =courseRepository.findCourseById(cIdList.get(i));
            m = new HashMap();
            m.put("label",c.getCourseName());
            m.put("value",c.getId());
            courseIdList.add(m);
        }*/
        List<Course> cList = courseRepository.findAll();
        List<Course> cList2 = s.getCourseList();
        cList.removeAll(cList2);
        for(i = 0; i <cList.size();i++) {
            c =cList.get(i);
            m = new HashMap();
            m.put("label",c.getCourseName());
            m.put("value",c.getId());
            courseIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentNum",s.getStudentNum());
        form.put("studentName",s.getStudentName());
        form.put("courseId","");
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象


        //Map form = new HashMap();
        //  return CommonMethod.getReturnData(form);
    }


    @PostMapping("/icourseEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse icourseEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        String studentNum = CommonMethod.getString(form,"studentNum");
        String studentName = CommonMethod.getString(form,"studentName");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        Course c= null;
        Student s=null;
        Optional<Course> op;
        Optional<Student> op2;
        if(courseId != null) {
            op= courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();
            }
        }
        if(studentNum != null) {
            op2= studentRepository.findByStudentNum(studentNum);
            if(op2.isPresent()) {
                s = op2.get();
            }
        }
        if(c!=null&&s!=null) {
            c.addStu(s);
            //StuInCourse sc = new StuInCourse(s,c);
            //stuInCourseRepository.save(sc);
            courseRepository.save(c);
            studentRepository.save(s);
        }
        return CommonMethod.getReturnData(s.getId());
    }


    @PostMapping("/icourseDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse icourseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = tempStu;
        Integer courseId = dataRequest.getInteger("id");
        Optional<Course> op1;
        Course c= null;
        if(courseId != null) {
            op1= courseRepository.findById(courseId);
            if(op1.isPresent()) {
                c = op1.get();
            }
        }


        Student s=null;
        Optional<Student> op2;
        if(studentId != null) {
            op2= studentRepository.findById(studentId);
            if(op2.isPresent()) {
                s = op2.get();
            }
        }


        if(c!=null&&s!=null) {



            List<Student> studentList =c.getStudentList();
            for(int i=0;i<studentList.size();i++){
                if(studentList.get(i).getId()==s.getId()) {
                    studentList.remove(i);
                    c.setStudentList(studentList);
                    break;
                }
            }
            courseRepository.saveAndFlush(c);
            //studentRepository.saveAndFlush(s);
        }
        courseRepository.DeleteStudentCourseRelation(courseId,studentId);

        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}
