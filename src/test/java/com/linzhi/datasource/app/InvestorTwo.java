package com.linzhi.datasource.app;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiezhiwei
 * @date: 2023/4/22 0:08
 **/
public class InvestorTwo {

    private String name;
    private List<InvestorTwo> secondLevelInvestors;

    public InvestorTwo(String name) {
        this.name = name;
        this.secondLevelInvestors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<InvestorTwo> getSecondLevelInvestors() {
        return secondLevelInvestors;
    }

    public void addSecondLevelInvestor(InvestorTwo investor) {
        secondLevelInvestors.add(investor);
    }
}
