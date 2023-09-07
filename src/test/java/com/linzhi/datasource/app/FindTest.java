package com.linzhi.datasource.app;

import java.util.*;
/**
 * @author: xiezhiwei
 * @date: 2023/4/22 17:48
 **/
public class FindTest {



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

            // 动态查找A在各个基金主体中的层级，并返回层级
            System.out.println(getInvestorLevel(A, B));
            System.out.println(getInvestorLevel(A, C));
            System.out.println(getInvestorLevel(A, D));
        }

        // 动态查找投资者A在基金主体B中的层级，并返回层级
        public static String getInvestorLevel(Investor A, Fund B) {
            int level = findInvestorLevel(A, B);
            if (level == 0) {
                return A.getName() + " is not an investor of " + B.getName();
            } else {
                return A.getName() + " is a " + level + " level investor of " + B.getName();
            }
        }

        // 递归查找投资者A在基金主体B中的层级
        public static int findInvestorLevel(Investor A, Fund B) {
            //新定义变量
            int index=1;
            // 如果A是B的一级投资者，直接返回1
            if (B.getInvestors().contains(A)) {
                return index;
            }
            // 否则，遍历B的所有一级投资者
            for (Investor investor : B.getInvestors()) {
                // 如果investor是基金主体，递归查找A是不是investor的一级投资者
                if (investor instanceof Fund && ((Fund) investor).getInvestors().contains(A)) {
                    //return 2 + findInvestorLevel(investor, B);
                    return index + findInvestorLevel(investor, B);
                }else{
                    //新增
                    index++;
                }
            }
            // 如果A不是B的一级或二级投资者，返回0
            return 0;
        }
    }

    // 投资者类
    class InvestorT {
        private String name;

        public InvestorT(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // 基金主体类
    class FundT extends InvestorT {
        private List<InvestorT> investors;

        public FundT(String name) {
            super(name);
            investors = new ArrayList<>();
        }

        public void addInvestor(InvestorT investor) {
            investors.add(investor);
        }

        public List<InvestorT> getInvestors() {
            return investors;
        }
    }





