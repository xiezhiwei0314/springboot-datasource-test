package com.linzhi.datasource.app;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiezhiwei
 * @date: 2023/4/22 17:37
 **/
public class FundEntityThree {

    private String name;
    private List<InvestorThree> firstLevelInvestors;

    public FundEntityThree(String name) {
        this.name = name;
        this.firstLevelInvestors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<InvestorThree> getFirstLevelInvestors() {
        return firstLevelInvestors;
    }

    public void addFirstLevelInvestor(InvestorThree investor) {
        firstLevelInvestors.add(investor);
    }
}
