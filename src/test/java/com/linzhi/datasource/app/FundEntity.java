package com.linzhi.datasource.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiezhiwei
 * @date: 2023/4/22 0:09
 **/
public class FundEntity {

    private String name;
    private List<InvestorTwo> firstLevelInvestors;

    public FundEntity(String name) {
        this.name = name;
        this.firstLevelInvestors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<InvestorTwo> getFirstLevelInvestors() {
        return firstLevelInvestors;
    }

    public void addFirstLevelInvestor(InvestorTwo investor) {
        firstLevelInvestors.add(investor);
    }


    public static void main(String[] args) {
        InvestorTwo investorA = new InvestorTwo("A");
        InvestorTwo investorB1 = new InvestorTwo("B1");
        InvestorTwo investorB2 = new InvestorTwo("B2");
        InvestorTwo investorB3 = new InvestorTwo("B3");
        InvestorTwo investorC1 = new InvestorTwo("C1");
        InvestorTwo investorC2 = new InvestorTwo("C2");
        InvestorTwo investorD1 = new InvestorTwo("D1");
        InvestorTwo investorD2 = new InvestorTwo("D2");
        InvestorTwo investorD3 = new InvestorTwo("D3");

        investorB1.addSecondLevelInvestor(investorA);
        investorB2.addSecondLevelInvestor(investorB1);
        investorB3.addSecondLevelInvestor(investorB2);

        investorC1.addSecondLevelInvestor(investorA);
        investorC2.addSecondLevelInvestor(investorC1);

        investorD1.addSecondLevelInvestor(investorD2);
        investorD2.addSecondLevelInvestor(investorD3);
        investorD3.addSecondLevelInvestor(investorA);

        FundEntity fundB = new FundEntity("B");
        fundB.addFirstLevelInvestor(investorB1);
        fundB.addFirstLevelInvestor(investorB2);
        fundB.addFirstLevelInvestor(investorB3);

        FundEntity fundC = new FundEntity("C");
        fundC.addFirstLevelInvestor(investorC1);
        fundC.addFirstLevelInvestor(investorC2);

        FundEntity fundD = new FundEntity("D");
        fundD.addFirstLevelInvestor(investorD1);

        List<FundEntity> fundList = new ArrayList<>();
        fundList.add(fundB);
        fundList.add(fundC);
        fundList.add(fundD);

       // Map<String, Integer> levelMap = getInvestorLevels(investorA, fundList);
        //System.out.println(levelMap);

    }


    public Map<String, Integer> getInvestorLevels(InvestorTwo investorA, List<FundEntity> fundList) {
        Map<String, Integer> levelMap = new HashMap<>();
        for (FundEntity fund : fundList) {
            if (fund.getFirstLevelInvestors().contains(investorA)) {
                levelMap.put(fund.getName(), 1);
            } else {
                int level = 0;
                for (InvestorTwo firstLevelInvestor : fund.getFirstLevelInvestors()) {
                    if (firstLevelInvestor.getSecondLevelInvestors().contains(investorA)) {
                        level = 2;
                        break;
                    } else if (firstLevelInvestor.getSecondLevelInvestors().size() > 0) {
                       // int subLevel = getInvestorLevel(investorA, firstLevelInvestor, fundList);
                       // int subLevel = getInvestorLevel(investorA, firstLevelInvestor);

                      /*  if (subLevel > 0) {
                            level = subLevel + 1;
                            break;
                        }*/
                    }
                }
                levelMap.put(fund.getName(), level);
            }
        }
        return levelMap;
    }


    public int getInvestorLevel(InvestorTwo investorA, FundEntity fundB) {
        if (fundB.getFirstLevelInvestors().contains(investorA)) {
            return 1;
        } else {
            int level = 0;
            for (InvestorTwo firstLevelInvestor : fundB.getFirstLevelInvestors()) {
                if (firstLevelInvestor.getSecondLevelInvestors().contains(investorA)) {
                    level = 2;
                    break;
                } else if (firstLevelInvestor.getSecondLevelInvestors().size() > 0) {
                   // int subLevel = getInvestorLevel(investorA, firstLevelInvestor);
                   /* if (subLevel > 0) {
                        level = subLevel + 1;
                        break;
                    }*/
                }
            }
            return level;
        }
    }

}
