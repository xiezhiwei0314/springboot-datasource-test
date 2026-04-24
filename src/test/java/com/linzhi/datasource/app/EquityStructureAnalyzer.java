package com.linzhi.datasource.app;
import java.util.*;
/**
 * @author: xiezhiwei
 * @date: 2024/4/25 11:54
 **/

/**
 * 在Java中，如果我们需要上翻查找股权结构投资层级关联，即逆向查找从某个公司向上追溯其所有股东（包括间接股东），
 * 我们通常需要深度优先搜索（DFS）或广度优先搜索（BFS）的算法。下面是一个使用深度优先搜索的简化示例，演示如何上翻查找股权结构投资层级关联：
 *
 * 首先，我们需要定义一些基本的数据结构来表示公司和股东：
 */
public class EquityStructureAnalyzer {

    class CompanyTest {
        String name;
        List<ShareholderTest> shareholders;
        Map<ShareholderTest, Double> ownershipPercentages;

        public CompanyTest(String name) {
            this.name = name;
            this.shareholders = new ArrayList<>();
            this.ownershipPercentages = new HashMap<>();
        }

        public void addShareholder(ShareholderTest shareholder, double percentage) {
            shareholders.add(shareholder);
            ownershipPercentages.put(shareholder, percentage);
        }

        // Getter methods
        public List<ShareholderTest> getShareholders() {
            return shareholders;
        }

        public Map<ShareholderTest, Double> getOwnershipPercentages() {
            return ownershipPercentages;
        }
    }

    class ShareholderTest {
        String name;
        boolean isCompany; // Flag to indicate if the shareholder is a company or a natural person

        public ShareholderTest(String name, boolean isCompany) {
            this.name = name;
            this.isCompany = isCompany;
        }

        // Getter methods
        public String getName() {
            return name;
        }

        public boolean isCompany() {
            return isCompany;
        }
    }



    public static void main(String[] args) {
        EquityStructureAnalyzer equityStructureAnalyzer = new EquityStructureAnalyzer();
        equityStructureAnalyzer.createCompay();
    }


    public void createCompay(){
        // 创建示例股权结构
        CompanyTest aa = new CompanyTest("AAA");
        CompanyTest companyA = new CompanyTest("CompanyTest A");
        CompanyTest companyB = new CompanyTest("CompanyTest B");
        CompanyTest companyC = new CompanyTest("CompanyTest C");
        ShareholderTest shareholder1 = new ShareholderTest("Shareholder 1", false);
        ShareholderTest shareholder2 = new ShareholderTest("Shareholder 2", false);
        ShareholderTest shareholderCompanyB = new ShareholderTest("Company B", true); // 作为股东的Company B

        // 构建股权关系
        companyA.addShareholder(shareholder1, 50.0);
        companyA.addShareholder(shareholderCompanyB, 50.0); // Company A has a share from Company B
        companyB.addShareholder(shareholder2, 100.0); // Company B is fully owned by Shareholder 2

        // 假设Company C间接持有Company A的股份
        ShareholderTest shareholderCompanyA = new ShareholderTest("Company A", true);
        companyC.addShareholder(shareholderCompanyA, 100.0); // Company C fully owns Company A

        // 从Company C开始，查找其所有的上级股东
        List<String> shareholdersChain = findUpstreamShareholders(companyC);
        System.out.println("Upstream shareholders of Company C:");
        for (String shareholder : shareholdersChain) {
            System.out.println(shareholder);
        }
    }

    public static List<String> findUpstreamShareholders(CompanyTest company) {
        List<String> upstreamShareholders = new ArrayList<>();
        findUpstreamShareholdersRecursive(company, upstreamShareholders);
        return upstreamShareholders;
    }

    private static void findUpstreamShareholdersRecursive(CompanyTest company, List<String> upstreamShareholders) {
        for (ShareholderTest shareholder : company.getShareholders()) {
            if (shareholder.isCompany()) {
                // 如果股东是公司，则递归查找其上级股东
                //TODO 强转异常
                //findUpstreamShareholdersRecursive((CompanyTest) shareholder, upstreamShareholders);
            } else {
                // 如果股东是个人，则添加到列表中
                upstreamShareholders.add(shareholder.getName());
            }
        }
    }

}
