package com.manage_system.ui.inter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Notice {
    private String content;
    private List<BigInteger> recvIds = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<BigInteger> getRecvIds() {
        return recvIds;
    }

    public void setRecvIds(List<BigInteger> recvIds) {
        this.recvIds = recvIds;
    }
}
