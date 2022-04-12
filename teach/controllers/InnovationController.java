package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Innovation;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.InnovationRepository;
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

public class InnovationController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private InnovationRepository innovationRepository;



    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getInnovationMapList(String numName) {
        List dataList = new ArrayList();
        List<Innovation> sList = innovationRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Innovation s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("socialPractice",s.getSocialPractice());
            m.put("competition",s.getCompetition());
            m.put("achievements",s.getAchievements());
            m.put("seminars",s.getSeminars());
            m.put("project",s.getProject());
            m.put("practice",s.getPractice());
            dataList.add(m);
        }
        return dataList;
    }


    @PostMapping("/innovationInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse innovationInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getInnovationMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/innovationQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse innovationQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getInnovationMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/innovationEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse innovationEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Innovation s= null;
        Optional<Innovation> op;
        if(id != null) {
            op= innovationRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if(s != null) {
            form.put("id",s.getId());
            form.put("studentNum",s.getStudentNum());
            form.put("studentName",s.getStudentName());
            form.put("socialPractice",s.getSocialPractice());
            form.put("competition",s.getCompetition());
            form.put("achievements",s.getAchievements());
            form.put("seminars",s.getSeminars());
            form.put("project",s.getProject());
            form.put("practice",s.getPractice());
        }
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/innovationEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse innovationEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String studentNum = CommonMethod.getString(form,"studentNum");  //Map 获取属性的值
        String studentName = CommonMethod.getString(form,"studentName");
        String socialPractice = CommonMethod.getString(form,"socialPractice");
        String competition = CommonMethod.getString(form,"competition");
        String achievements = CommonMethod.getString(form,"achievements");
        String seminars = CommonMethod.getString(form,"seminars");
        String project = CommonMethod.getString(form, "project");
        String practice = CommonMethod.getString(form,"practice");
        Innovation s= null;
        Optional<Innovation> op;
        if(id != null) {
            op= innovationRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Innovation();   //不存在 创建实体对象
            id = innovationRepository.getMaxId();  // 查询最大的id
            if(id == null)
                id = 1;
            else
                id = id+1;
            s.setId(id);  //设置新的id
        }
        s.setStudentNum(studentNum);  //设置属性
        s.setStudentName(studentName);
        s.setAchievements(achievements);
        s.setCompetition(competition);
        s.setPractice(practice);
        s.setProject(project);
        s.setSeminars(seminars);
        s.setSocialPractice(socialPractice);
        innovationRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }


    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/innovationDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse innovationDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Innovation s= null;
        Optional<Innovation> op;
        if(id != null) {
            op= innovationRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            innovationRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}

