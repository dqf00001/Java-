package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.DailyActivity;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.DailyactivityRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class DailyController {

    @Autowired
    private DailyactivityRepository dailyactivityRepository;

    @Autowired
    private IntroduceService introduceService;

    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<DailyActivity> sList = dailyactivityRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        DailyActivity s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("PEactivity",s.getPEactivity());
            m.put("perform", s.getPerform());
            m.put("travel",s.getTravel());
            m.put("party",s.getParty());
            dataList.add(m);
        }
        return dataList;
    }



    //daily页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/dailyactivityInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dailyactivityInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getStudentMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //dailyactivity页面点击查询按钮请求
    @PostMapping("/dailyactivityQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/dailyactivityEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dailyactivityEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        DailyActivity s= null;
        Optional<DailyActivity> op;
        if(id != null) {
            op= dailyactivityRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if(s != null) {
            form.put("id",s.getId());
            form.put("studentNum",s.getStudentNum());
            form.put("studentName",s.getStudentName());
            form.put("PEactivity",s.getPEactivity());
            form.put("perform", s.getPerform());
            form.put("travel",s.getTravel());
            form.put("party",s.getParty());
        }
        return CommonMethod.getReturnData(form);
    }
    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    @PostMapping("/dailyactivityEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse dailyactivityEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String studentNum = CommonMethod.getString(form,"studentNum");  //Map 获取属性的值
        String studentName = CommonMethod.getString(form,"studentName");
        String PEactivity = CommonMethod.getString(form,"PEactivity");
        String perform = CommonMethod.getString(form,"perform");
        String travel = CommonMethod.getString(form,"travel");
        String party = CommonMethod.getString(form,"party");
        DailyActivity s= null;
        Optional<DailyActivity> op;
        if(id != null) {
            op= dailyactivityRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new DailyActivity();   //不存在 创建实体对象
            id = dailyactivityRepository.getMaxId();  // 查询最大的id
            if(id == null)
                id = 1;
            else
                id = id+1;
            s.setId(id);  //设置新的id
        }
        s.setStudentNum(studentNum);  //设置属性
        s.setStudentName(studentName);
        s.setPEactivity(PEactivity);
        s.setPerform(perform);
        s.setParty(party);
        s.setTravel(travel);
        dailyactivityRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    //  学生信息删除方
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/dailyactivityDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse dailyactivityDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        DailyActivity s= null;
        Optional<DailyActivity> op;
        if(id != null) {
            op= dailyactivityRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            dailyactivityRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}





