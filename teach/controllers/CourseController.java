package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
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

public class CourseController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用courseRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， courseRepository 相当于courseRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private IntroduceService introduceService;


    public List getCourseMapList(String courseNumName) {
        List dataList = new ArrayList();
        List<Course> cList = courseRepository.findCourseListByNumName(courseNumName);
        if(cList == null || cList.size() == 0)
            return dataList;
        Course c;
        Map m;
        String showStudentsParas;
        for(int i = 0; i < cList.size();i++) {
            c = cList.get(i);
            m = new HashMap();
            m.put("id", c.getId());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("teacher",c.getTeacher());
            m.put("classroom",c.getClassroom());
            // switch (s.getMeans()) {
            //     case"1": m.put("means","线下");
            //         break;
            //     case"2":  m.put("means","线上");
            //         break;
            //    case"3": m.put("means","线上、线下结合");
            //         break;
            //}
            if("1".equals(c.getMeans()))
                m.put("means","线下");
            else if("2".equals(c.getMeans()))
                m.put("means","线上");
            else
                m.put("means","线上、线下结合");
            m.put("hours",c.getHours());
            m.put("credits",c.getCredits());
            showStudentsParas = "model=showStudents&id=" + c.getId();
            m.put("showStudentsParas",showStudentsParas);

            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/courseInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getCourseMapList("");
        return CommonMethod.getReturnData(dataList);
    }



/*    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Student s;
        String joinCourse;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }
            m.put("age",s.getAge());
            m.put("dept",s.getDept());
            m.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("teleNum",s.getTeleNum());
            joinCourse = "model=joinCourse";
            m.put("joinCourse",joinCourse);
            dataList.add(m);
        }
        return dataList;
    }


    @PostMapping("/showStudentsEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse showStudentsEditInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getStudentMapList("");
        return CommonMethod.getReturnData(dataList);
    }*/

    @PostMapping("/courseQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseQuery(@Valid @RequestBody DataRequest dataRequest) {
        String courseNumName= dataRequest.getString("courseNumName");
        List dataList;
        if(courseNumName =="" || courseNumName == null)
            dataList = getCourseMapList("");
        else
            dataList = getCourseMapList(courseNumName);
        return CommonMethod.getReturnData(dataList);
    }
/*
    @PostMapping("/showStudentsEditQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse showStudentsEditQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList;
        if(numName =="" || numName == null)
            dataList = getStudentMapList("");
        else
            dataList = getCourseMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }*/

    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/courseEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Course c= null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);
            if(op.isPresent()) {
                c = op.get();
            }
        }
        Map form = new HashMap();
        if(c != null) {
            form.put("id",c.getId());
            form.put("courseNum",c.getCourseNum());
            form.put("courseName",c.getCourseName());
            form.put("teacher",c.getTeacher());
            form.put("classroom",c.getClassroom());
            if("1".equals(c.getMeans()))
                form.put("means","线下");
            else if("2".equals(c.getMeans()))
                form.put("means","线上");
            else
                form.put("means","线上、线下结合");
            form.put("hours", c.getHours());
            form.put("credits", c.getCredits());
        }
        return CommonMethod.getReturnData(form);
    }


    public synchronized Integer getNewSCourseId(){
        Integer id = courseRepository.getMaxId();
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    @PostMapping("/courseEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        Integer id = CommonMethod.getInteger(form,"id");
        String courseNum = CommonMethod.getString(form,"courseNum");
        String courseName = CommonMethod.getString(form,"courseName");
        String teacher = CommonMethod.getString(form,"teacher");
        String classroom = CommonMethod.getString(form,"classroom");
        String means = CommonMethod.getString(form,"means");
        Integer hours = CommonMethod.getInteger(form,"hours");
        Integer credits = CommonMethod.getInteger(form,"credits");
        Course c= null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);
            if(op.isPresent()) {
                c = op.get();
            }
        }
        if(c == null) {
            c = new Course();
            id = getNewSCourseId();
            c.setId(id);
        }
        c.setCourseNum(courseNum);
        c.setCourseName(courseName);
        c.setTeacher(teacher);
        c.setClassroom(classroom);
        c.setMeans(means);
        c.setHours(hours);
        c.setCredits(credits);
        courseRepository.save(c);
        return CommonMethod.getReturnData(c.getId());
    }


    @PostMapping("/courseDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Course c= null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);
            if(op.isPresent()) {
                c = op.get();
            }
        }
        if(c != null) {
            courseRepository.delete(c);
        }
        return CommonMethod.getReturnMessageOK();
    }


}
