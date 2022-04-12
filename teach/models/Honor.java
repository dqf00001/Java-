package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "student",
        uniqueConstraints = {
        })
public class Honor {
    @Id
    private Integer id;

    @NotBlank
    @Size
    private String studentNum;
    private String studentName;
    private String honor1;
    private String honor2;
    private String honor3;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return studentName; }

    public void setName(String name) { this.studentName = name; }

    public String getHonor1() { return honor1; }

    public void setHonor1(String honor1) { this.honor1 = honor1; }

    public String getNum() { return studentNum; }

    public void setNum(String num) { this.studentNum = num; }

    public String getHonor2() {
        return honor2;
    }

    public void setHonor2(String honor2) {
        this.honor2 = honor2;
    }

    public String getHonor3() {
        return honor3;
    }

    public void setHonor3(String honor3) {
        this.honor3 = honor3;
    }
}

