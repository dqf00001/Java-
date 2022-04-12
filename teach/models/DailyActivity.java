package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "dailyactivity",
        uniqueConstraints = {
        })
public class DailyActivity {
    @Id
    private Integer id;

    private String PEactivity;

    @NotBlank
    @Size(max = 20)
    private String studentNum;

    @Size(max = 50)
    private String studentName;


    private String perform;

    private String travel;

    private String party;

    public String getPEactivity() {
        return PEactivity;
    }

    public String getPerform() {
        return perform;
    }

    public String getTravel() {return travel;}

    public String getParty() {
        return party;
    }

    public void setPEactivity(String PEactivity) {
        this.PEactivity = PEactivity;
    }

    public void setPerform(String perform) {
        this.perform = perform;
    }

    public void setTravel(String travel) {
        this.travel = travel;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNum() {return studentNum;}

    public void setStudentNum(String studentNum) {this.studentNum = studentNum;}

    public String getStudentName() {return studentName;}

    public void setStudentName(String studentName) {this.studentName = studentName;}
}
