package com.linzhi.datasource.app;
import java.util.*;

/**
 * @author: xiezhiwei
 * @date: 2024/4/25 11:47
 **/
/*public class CompanyOwnershipStructure {*/



    class Company {
        String name;
        Map<Shareholder, Double> shareholders;

        public Company(String name) {
            this.name = name;
            this.shareholders = new HashMap<>();
        }

        public void addShareholder(Shareholder shareholder, double percentage) {
            shareholders.put(shareholder, percentage);
        }

        // 可以添加更多方法来处理公司相关的操作
    }

    class Shareholder {
        String name;

        public Shareholder(String name) {
            this.name = name;
        }

        // 可以添加更多方法来处理股东相关的操作
    }

    public class CompanyOwnershipStructure {
        public static void main(String[] args) {
            // 创建公司和股东实例，并构建股权关系
            Company companyA = new Company("Company A");
            Company companyB = new Company("Company B");
            Shareholder shareholder1 = new Shareholder("Shareholder 1");
            Shareholder shareholder2 = new Shareholder("Shareholder 2");

            companyA.addShareholder(shareholder1, 50.0);
            companyA.addShareholder(shareholder2, 50.0);
            companyB.addShareholder(shareholder1, 100.0); // 假设Shareholder 1完全拥有Company B

            // 分析股权结构
            analyzeEquityStructure(companyA, 0); // 从Company A开始分析，层级深度初始为0
        }

        private static void analyzeEquityStructure(Company company, int depth) {
            System.out.println(String.format("Level %d: %s", depth, company.name));

            for (Map.Entry<Shareholder, Double> entry : company.shareholders.entrySet()) {
                Shareholder shareholder = entry.getKey();
                double percentage = entry.getValue();
                System.out.println(String.format("  Shareholder: %s, Percentage: %.2f%%", shareholder.name, percentage * 100));

                // 如果股东也是某个公司的股东，则递归分析该公司
       /*         if (shareholder instanceof Company) {
                    analyzeEquityStructure((Company) shareholder, depth + 1);
                }*/
            }
        }
    }
/*}*/
