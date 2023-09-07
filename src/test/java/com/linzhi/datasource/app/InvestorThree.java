package com.linzhi.datasource.app;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiezhiwei
 * @date: 2023/4/22 17:36
 **/
public class InvestorThree {

    private String name;
    private List<InvestorThree> secondLevelInvestors;

    public InvestorThree(String name) {
        this.name = name;
        this.secondLevelInvestors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<InvestorThree> getSecondLevelInvestors() {
        return secondLevelInvestors;
    }

    public void addSecondLevelInvestor(InvestorThree investor) {
        secondLevelInvestors.add(investor);
    }
}
