package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.StuInCourse;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.StuInCourseRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class ShowStudentsController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StuInCourseRepository stuInCourseRepository;
    @Autowired
    private IntroduceService introduceService;

    private static Integer tempCourse;


    @PostMapping("/showStudentsInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse showStudentsInit(@Valid @RequestBody DataRequest dataRequest) {
        tempCourse = dataRequest.getInteger("id");
        Integer courseId =tempCourse;
        Course c= null;
        Optional<Course> op;
        if(courseId != null) {
            op= courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();
            }
        }
        List dataList = new ArrayList();
        List<Student> students= c.getStudentList();
        Student s;
        String studentNameParas;
        Map m;
        for(int i = 0; i < students.size();i++) {
            s = students.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudentNum());
            studentNameParas = "model=studentEdit&id=" + s.getId();
            m.put("studentName",s.getStudentName());
            m.put("studentNameParas",studentNameParas);
            m.put("dept",s.getDept());
            Optional<String> op2;
            String score = null;
            op2 = courseRepository.findScoresByStudentCourseId(courseId,s.getId());
            if(op2.isPresent()) {
                score = op2.get();
            }
            if(score!=null)
                m.put("scores", score);
            else
                m.put("scores", "暂无");

            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/showStudentsEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse showStudentsEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer stuId= dataRequest.getInteger("id");
        Integer courseId = tempCourse;
        Student s;
        Course c;
        Optional<Course> op;
        op=courseRepository.findById(courseId);
        c=op.get();
        Optional<Student> op2;
        op2=studentRepository.findById(stuId);
        s=op2.get();
        Optional<String> op3;
        String score = null;
        op3 = courseRepository.findScoresByStudentCourseId(courseId,stuId);
        if(op3.isPresent())
            score = op3.get();



        Map form = new HashMap();
        form.put("studentNum",s.getStudentNum());
        form.put("studentName",s.getStudentName());
        form.put("dept",s.getDept());
        form.put("scores", score);


        return CommonMethod.getReturnData(form);
    }



    @PostMapping("/showStudentsEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse showStudentsEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        String studentNum = CommonMethod.getString(form,"studentNum");
        String score = CommonMethod.getString(form,"scores");
        Integer courseId = tempCourse;
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
            Optional<StuInCourse> op3;
            StuInCourse sc=null;
            op3 = stuInCourseRepository.findStudentCourseRelation(c.getId(),s.getId());
            if(op3.isPresent()) {
                sc = op3.get();
                sc.setScores(score);
                stuInCourseRepository.save(sc);
            }
        }
        return CommonMethod.getReturnData(s.getId());
    }


    @PostMapping("/showStudentsDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse showStudentsDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = tempCourse;
        Integer studentId = dataRequest.getInteger("id");


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
