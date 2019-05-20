package com.manage_system.ui.inter;

import java.util.ArrayList;
import java.util.List;

public class Notice {
    private String content;
    private List<String> recvIds = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRecvIds() {
        return recvIds;
    }

    public void setRecvIds(List<String> recvIds) {
        this.recvIds = recvIds;
    }
}
