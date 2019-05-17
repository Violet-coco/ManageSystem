package com.manage_system.ui.inter;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private String gid;
    private List<String> gtList = new ArrayList<>();
//    "groupSize":25,                 (答辩组学生人数限制)
////    "defDate":"2019-05-11"
////    "defClass":"6a-151",            (答辩教师)
////    "teaIdList":[
////    {"tid":12345678900,"leader":0},
////    {"tid":12000001900,"leader":1},
////    {"tid":12000002100,"leader":0}]
    private String groupSize;
    private String defDate;
    private String defClass;
    private String tid;
    private String leader;
    private List<Object> teaIdList = new ArrayList<>();

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public List<String> getGtList() {
        return gtList;
    }

    public void setGtList(List<String> gtList) {
        this.gtList = gtList;
    }

    public String getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(String groupSize) {
        this.groupSize = groupSize;
    }

    public String getDefDate() {
        return defDate;
    }

    public void setDefDate(String defDate) {
        this.defDate = defDate;
    }

    public String getDefClass() {
        return defClass;
    }

    public void setDefClass(String defClass) {
        this.defClass = defClass;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public List<Object> getTeaIdList() {
        return teaIdList;
    }

    public void setTeaIdList(List<Object> teaIdList) {
        this.teaIdList = teaIdList;
    }
}
