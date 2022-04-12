package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HonorRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class HonorController {
    @Autowired
    private HonorRepository honorRepository;
    //荣誉
    public List getHonorMapList(String numName) {
        List dataList = new ArrayList();
        List<Honor> sList = honorRepository.findHonorListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Honor s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getNum());
            m.put("studentName",s.getName());
            m.put("honor1",s.getHonor1());
            m.put("honor2",s.getHonor2());
            m.put("honor3",s.getHonor3());
            dataList.add(m);
        }
        return dataList;
    }
    //荣誉
    @PostMapping("/honorInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getHonorMapList("");
        return CommonMethod.getReturnData(dataList);
    }
    //荣誉
    @PostMapping("/honorQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getHonorMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //荣誉
    @PostMapping("/honorEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Honor s= null;
        Optional<Honor> op;
        if(id != null) {
            op= honorRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if(s != null) {
            form.put("id",s.getId());
            form.put("studentNum",s.getNum());
            form.put("studentName",s.getName());
            form.put("honor1",s.getHonor1());
            form.put("honor2",s.getHonor2());
            form.put("honor3",s.getHonor3());
        }
        return CommonMethod.getReturnData(form);
    }
    //荣誉
    @PostMapping("/honorEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse honorEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String num = CommonMethod.getString(form,"studentNum");  //Map 获取属性的值
        String name = CommonMethod.getString(form,"studentName");
        String honor1 = CommonMethod.getString(form,"honor1");
        String honor2 = CommonMethod.getString(form,"honor2");
        String honor3 = CommonMethod.getString(form,"honor3");
        Honor s= null;
        Optional<Honor> op;
        if(id != null) {
            op= honorRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Honor();   //不存在 创建实体对象
            id = honorRepository.getMaxId();  // 查询最大的id
            if(id == null)
                id = 1;
            else
                id = id+1;
            s.setId(id);  //设置新的id
        }
        s.setNum(num);  //设置属性
        s.setName(name);
        s.setHonor1(honor1);
        s.setHonor2(honor2);
        s.setHonor3(honor3);
        honorRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }
    //荣誉
    @PostMapping("/honorDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse honorDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Honor s= null;
        Optional<Honor> op;
        if(id != null) {
            op= honorRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            honorRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}
