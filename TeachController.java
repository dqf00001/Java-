package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.Dailyactivity;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.DailyactivityRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class TeachController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DailyactivityRepository dailyactivityRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        Student s;
        Map m;
        String courseParas, studentNameParas,dailyParas;
        for (int i = 0; i < sList.size(); i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum", s.getStudentNum());
            studentNameParas = "model=studentEdit&id=" + s.getId();
            m.put("studentName", s.getStudentName());
            m.put("studentNameParas", studentNameParas);
            if ("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex", "男");
            } else {
                m.put("sex", "女");
            }
            m.put("age", s.getAge());
            m.put("dept", s.getDept());
            m.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(), "yyyy-MM-dd"));  //时间格式转换字符串
            courseParas = "model=course&studentId=" + s.getId();
            m.put("course", "所学课程");
            m.put("courseParas", courseParas);
            dailyParas = "model=dailyactivityEdit&id=" + s.getId()+"&studentName="+s.getStudentName();
            m.put("daily","日常活动");
            m.put("dailyParas", dailyParas);
            dataList.add(m);
        }
        return dataList;
    }

    //student页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getStudentMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //student页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/studentDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Student s = null;
        Dailyactivity s1=null;
        Optional<Student> op;
        Optional<Dailyactivity> op1;
        if (id != null) {
            op = studentRepository.findById(id);   //查询获得实体对象
            op1= dailyactivityRepository.findById(id);
            if (op.isPresent()) {
                s = op.get();
                s1=op1.get();
            }
        }
        if (s != null) {
            studentRepository.delete(s);//数据库永久删除
            dailyactivityRepository.delete(s1);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/studentEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Student s = null;
        Optional<Student> op;
        if (id != null) {
            op = studentRepository.findById(id);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        List sexList = new ArrayList();
        Map m;
//        m = new HashMap();
//        m.put("label","男");
//        m.put("value","1");
//        sexList.add(m);
//        m = new HashMap();
//        m.put("label","女");
//        m.put("value","2");
//        sexList.add(m);
        Map form = new HashMap();
        if (s != null) {
            form.put("id", s.getId());
            form.put("studentNum", s.getStudentNum());
            form.put("studentName", s.getStudentName());
            form.put("sex", s.getSex());  //这里不需要转换
            form.put("age", s.getAge());
            form.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(), "yyyy-MM-dd")); //这里需要转换为字符串
        }
        //       form.put("sexList",sexList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewStudentId() {
        Integer
                id = studentRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;

    @PostMapping("/studentEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        String studentNum = CommonMethod.getString(form, "studentNum");  //Map 获取属性的值
        String studentName = CommonMethod.getString(form, "studentName");
        String sex = CommonMethod.getString(form, "sex");
        Integer age = CommonMethod.getInteger(form, "age");
        Date birthday = CommonMethod.getDate(form, "birthday");
        Student s = null;
        Dailyactivity s1= null;
        Optional<Student> op;
        Optional<Dailyactivity> op1;
        if (id != null) {
            op = studentRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            op1= dailyactivityRepository.findById(id);
            if (op.isPresent()) {
                s = op.get();
                s1=op1.get();
            }
        }
        if (s == null) {
            s = new Student();   //不存在 创建实体对象
            s1= new Dailyactivity();
            id = getNewStudentId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
            s1.setId(id);
        }
        s.setStudentNum(studentNum);  //设置属性
        s.setStudentName(studentName);
        if(s1!=null) {
            s1.setStudentNum(studentNum);  //设置属性
            s1.setStudentName(studentName);
            dailyactivityRepository.save(s1);
        }
        s.setSex(sex);
        s.setAge(age);
        s.setBirthday(birthday);
        studentRepository.save(s);//新建和修改都调用save方法

        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }



    @PostMapping("/getStudentIntroduceData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getStudentIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        Map data = introduceService.getIntroduceDataMap();
        return CommonMethod.getReturnData(data);  //返回前端个人简历数据
    }

    //  学生个人简历页面
    //在系统在主界面内点击个人简历，后台准备个人简历所需要的各类数据组成的段落数据，在前端显示
   /* @PostMapping("/getStudentIntroduceData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getStudentIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Map data = introduceService.getIntroduceDataMap(studentId);
        return CommonMethod.getReturnData(data);  //返回前端个人简历数据
    }

    public ResponseEntity<StreamingResponseBody> getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.useFastMode();
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:font/SourceHanSansSC-Regular.ttf");
            InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            StreamingResponseBody stream = outputStream -> {
                builder.toStream(outputStream);
                builder.run();
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }*/

    /*@PostMapping("/getStudentIntroducePdf")
    public ResponseEntity<StreamingResponseBody> getStudentIntroducePdf(Map dataRequest) {
        Integer studentId = CommonMethod.getInteger(dataRequest, "studentId");
        Map data = introduceService.getIntroduceDataMap(studentId);
        String content = "<!DOCTYPE html>";
        content += "<html>";
        content += "<head>";
        content += "<style>";
        content += "html { font-family: \"SourceHanSansSC\", \"Open Sans\";}";
        content += "</style>";
        content += "<meta charset='UTF-8' />";
        content += "<title>Insert title here</title>";
        content += "</head>";

        String myName = (String) data.get("myName");
        String overview = (String) data.get("overview");
        List<Map> attachList = (List) data.get("attachList");

//        content += getHtmlString();
        content += "<body>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        " + myName + " </tr>";
        content += "   </thead>";
        content += "   </table>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        " + overview + " </tr>";
        content += "   </thead>";
        content += "   </table>";

        content += "<table style='width: 100%;border-collapse: collapse;border: 1px solid black;'>";
        content += " <tbody>";

        for (int i = 0; i < attachList.size(); i++) {
            content += "     <tr style='text-align: center;border: 1px solid black;font-size: 14px;'>";
            content += "      " + attachList.get(i).get("title") + " ";
            content += "     </tr>";
            content += "     <tr style='text-align: center;border: 1px solid black; font-size: 14px;'>";
            content += "            " + attachList.get(i).get("content") + " ";
            content += "     </tr>";
        }
        content += " </tbody>";
        content += "   </table>";

        content += "</body>";
        content += "</html>";
        return getPdfDataFromHtml(content);
    }

    @PostMapping("/courseInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        String studentName = dataRequest.getString("studentName");
        List dataList = getStudentMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }*/
}



