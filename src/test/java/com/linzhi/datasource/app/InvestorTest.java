package com.linzhi.datasource.app;

import java.util.Scanner;

/**
 * @author: xiezhiwei
 * @date: 2023/4/22 17:38
 **/
public class InvestorTest {

    public static void main(String[] args) {


        // 创建基金主体B
        FundEntityThree fundB = new FundEntityThree("B");

        // 创建一级投资者A1、A2、A3
        InvestorThree investorA1 = new InvestorThree("A1");
        InvestorThree investorA2 = new InvestorThree("A2");
        InvestorThree investorA3 = new InvestorThree("A3");

        // 将一级投资者A1、A2、A3添加到基金主体B中
        fundB.addFirstLevelInvestor(investorA1);
        fundB.addFirstLevelInvestor(investorA2);
        fundB.addFirstLevelInvestor(investorA3);

        // 创建二级投资者B1、B2、B3
        InvestorThree investorB1 = new InvestorThree("B1");
        InvestorThree investorB2 = new InvestorThree("B2");
        InvestorThree investorB3 = new InvestorThree("B3");

        // 将二级投资者B1、B2、B3添加到一级投资者A1中
        investorA1.addSecondLevelInvestor(investorB1);
        investorA1.addSecondLevelInvestor(investorB2);
        investorA1.addSecondLevelInvestor(investorB3);

        // 创建三级投资者C1、C2、C3
        InvestorThree investorC1 = new InvestorThree("C1");
        InvestorThree investorC2 = new InvestorThree("C2");
        InvestorThree investorC3 = new InvestorThree("C3");

        // 将三级投资者C1、C2、C3添加到二级投资者B1中
        investorB1.addSecondLevelInvestor(investorC1);
        investorB1.addSecondLevelInvestor(investorC2);
        investorB1.addSecondLevelInvestor(investorC3);

        // 创建二级投资者D1、D2、D3
        InvestorThree investorD1 = new InvestorThree("D1");
        InvestorThree investorD2 = new InvestorThree("D2");
        InvestorThree investorD3 = new InvestorThree("D3");

        // 将二级投资者D1、D2、D3添加到一级投资者A2中
        investorA2.addSecondLevelInvestor(investorD1);
        investorA2.addSecondLevelInvestor(investorD2);
        investorA2.addSecondLevelInvestor(investorD3);

        // 创建三级投资者E1、E2、E3
        InvestorThree investorE1 = new InvestorThree("E1");
        InvestorThree investorE2 = new InvestorThree("E2");
        InvestorThree investorE3 = new InvestorThree("E3");

        // 将三级投资者E1、E2、E3添加到二级投资者D1中
        investorD1.addSecondLevelInvestor(investorE1);
        investorD1.addSecondLevelInvestor(investorE2);
        investorD1.addSecondLevelInvestor(investorE3);

        // 输入要查询的投资者名称
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要查询的投资者名称：");
        String investorName = scanner.nextLine();

        // 查找投资者在基金主体B中的层数
        int levelInB = getInvestorLevel(investorName, fundB);
        if (levelInB > 0) {
            System.out.println(investorName + "是基金主体B的" + levelInB + "投资者");
        }

        // 创建基金主体C
        FundEntityThree fundC = new FundEntityThree("C");

        // 创建一级投资者F1、F2、F3
        InvestorThree investorF1 = new InvestorThree("F1");
        InvestorThree investorF2 = new InvestorThree("F2");
        InvestorThree investorF3 = new InvestorThree("F3");

        // 将一级投资者F1、F2、F3添加到基金主体C中
        fundC.addFirstLevelInvestor(investorF1);
        fundC.addFirstLevelInvestor(investorF2);
        fundC.addFirstLevelInvestor(investorF3);

        // 将二级投资者C1、C2、C3添加到一级投资者F1中
        investorF1.addSecondLevelInvestor(investorC1);
        investorF1.addSecondLevelInvestor(investorC2);
        investorF1.addSecondLevelInvestor(investorC3);

        // 查找投资者在基金主体C中的层数
        int levelInC = getInvestorLevel(investorName, fundC);
        if (levelInC > 0) {
            System.out.println(investorName + "是基金主体C的" + levelInC + "投资者");
        }

        // 创建基金主体D
        FundEntityThree fundD = new FundEntityThree("D");

        // 创建一级投资者G1、G2、G3
        InvestorThree investorG1 = new InvestorThree("G1");
        InvestorThree investorG2 = new InvestorThree("G2");
        InvestorThree investorG3 = new InvestorThree("G3");

        // 将一级投资者G1、G2、G3添加到基金主体D中
        fundD.addFirstLevelInvestor(investorG1);
        fundD.addFirstLevelInvestor(investorG2);
        fundD.addFirstLevelInvestor(investorG3);

        // 将二级投资者D1、D2、D3添加到一级投资者G1中
        investorG1.addSecondLevelInvestor(investorD1);
        investorG1.addSecondLevelInvestor(investorD2);
        investorG1.addSecondLevelInvestor(investorD3);

    }

    public static int getInvestorLevel(String investorA, FundEntityThree fundB) {
        if (fundB.getFirstLevelInvestors().contains(investorA)) {
            return 1;
        } else {
            int level = 0;
            for (InvestorThree firstLevelInvestor : fundB.getFirstLevelInvestors()) {
                if (firstLevelInvestor.getSecondLevelInvestors().contains(investorA)) {
                    level = 2;
                    break;
                } else if (firstLevelInvestor.getSecondLevelInvestors().size() > 0) {
                     int subLevel = getInvestorLevel(investorA, fundB);
                   if (subLevel > 0) {
                        level = subLevel + 1;
                        break;
                    }
                }
            }
            return level;
        }
    }
}
