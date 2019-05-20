package com.manage_system.ui.inter;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private String gid;
    private List<String> gtList = new ArrayList<>();

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
}
