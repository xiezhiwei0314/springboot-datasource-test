package com.linzhi.datasource.app;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiezhiwei
 * @date: 2023/4/22 17:53
 **/
public class InvestorHierarchy {
    public static void main(String[] args) {
        // 创建投资者A
        Investor A = new Investor("A");

        // 创建基金主体B及其投资者
        Fund B = new Fund("B");
        Investor B1 = new Investor("B1");
        Investor B2 = new Fund("B2");
        Investor B3 = new Investor("B3");
        B.addInvestor(B1);
        B.addInvestor(B2);
        B.addInvestor(B3);
        ((Fund) B2).addInvestor(A);

        // 创建基金主体C及其投资者
        Fund C = new Fund("C");
        Investor C1 = new Investor("C1");
        Investor C2 = new Fund("C2");
        Investor C3 = new Investor("C3");
        C.addInvestor(C1);
        C.addInvestor(C2);
        C.addInvestor(C3);
        ((Fund) C2).addInvestor(A);

        // 创建基金主体D及其投资者
        Fund D = new Fund("D");
        Investor D1 = new Investor("D1");
        Investor D2 = new Fund("D2");
        Investor D3 = new Fund("D3");
        Investor D4 = new Investor("D4");
        D.addInvestor(D1);
        D.addInvestor(D2);
        D.addInvestor(D3);
        ((Fund) D2).addInvestor(D4);
        ((Fund) D3).addInvestor(A);

        // 查找A在各个基金主体中的层级
        System.out.println("A is a " + getInvestorLevel(A, B) + " level investor of B");
        System.out.println("A is a " + getInvestorLevel(A, C) + " level investor of C");
        System.out.println("A is a " + getInvestorLevel(A, D) + " level investor of D");
    }

    // 查找投资者A在基金主体B中的层级
    public static String getInvestorLevel(Investor A, Fund B) {
        int level = findInvestorLevel(A, B);
        if (level == 0) {
            return "not an investor of " + B.getName();
        } else {
            return level + " level investor of " + B.getName();
        }
    }

    // 递归查找投资者A在基金主体B中的层级
    public static int findInvestorLevel(Investor A, Fund B) {
        // 如果A是B的一级投资者，直接返回1
        if (B.getInvestors().contains(A)) {
            return 1;
        }
        // 否则，遍历B的所有一级投资者
        for (Investor investor : B.getInvestors()) {
            // 如果investor是基金主体，递归查找A是不是investor的一级投资者
            if (investor instanceof Fund && ((Fund) investor).getInvestors().contains(A)) {
                return 2 + findInvestorLevel(investor, B);
            }
        }
        // 如果A不是B的一级或二级投资者，返回0
        return 0;
    }
}

// 投资者类
class Investor {
    private String name;

    public Investor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

// 基金主体类
class Fund extends Investor {
    private List<Investor> investors;

    public Fund(String name) {
        super(name);
        investors = new ArrayList<>();
    }

    public void addInvestor(Investor investor) {
        investors.add(investor);
    }

    public List<Investor> getInvestors() {
        return investors;
    }
}


