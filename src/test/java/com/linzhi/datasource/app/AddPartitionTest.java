package com.linzhi.datasource.app;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.linzhi.datasource.app.utils.EncryptorSecuriyUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: xiezhiwei
 * @date: 2023/3/6 17:08
 **/
public class AddPartitionTest {


    public static void main(String[] args) throws Exception{
        Integer a = new Integer(1);
        String str1="通话";
        String str2="重地";
        System.out.println(String.format("str1:%d|str2:%d",str1.hashCode(),str2.hashCode()));
        System.out.println(str1.equals(str2));


        LocalDate localDate = LocalDate.now();
        LocalDate start = localDate.withDayOfMonth(1);
        LocalDate end = localDate.plusMonths(6);

        //generateSQL(start, end);

        //epccOrgCodeTypeSQL();
        //epccCarbinSQL();

        //carBin匹配机构代码
        //carBinMatching();

        //更新carbin表
        //updateCarBin();

        //carbin机构对应临时表
        //addInsert();

        //网联机构代码
        //addEpccCodeInsert();

        //orgCodeMatchingEpccCode();

        //路由表新增网联路由
        //routerConfigInsert();

        //产业基金集团内数据脚本
        //cyjjDataProcess();

        //产业基金OA用户ID
        //cyjjOAUserId();

        //投后项目管理人员数据解析

       //cyjjAfterUserInfo();

       cyjjUserInfoEncryytor();

   /*     String s="f-0808879f090f";

       System.out.println( "---"+s.startsWith("f"));
        System.out.println(s.substring(1,s.length()));*/


    }

    private static void generateSQL(LocalDate startDate, LocalDate endDate) {
        List<String> tables = new ArrayList<>();
        tables.add("GW_PAYMENT_REQUEST");
        tables.add("GW_PAYMENT_ORDER");
        tables.add("GW_PAYMENT_RESPONSE");
        tables.add("GW_CUSTOMS_ORDER");
        tables.add("GW_PAYMENT_BATCH_DTL");
        tables.add("GW_TRANSACTION_REQUEST");
        tables.add("GW_TRANSACTION_RESPONSE");

        List<LocalDate> dates = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate)).collect(Collectors.toList());

        dates.forEach(date -> {
            String partition = "P" + date.format(DateTimeFormatter.BASIC_ISO_DATE);
            String time = date.plusDays(1) + " 00:00:00";
            tables.forEach(t -> {
                StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE ").append(t).append(" ADD PARTITION \"").append(partition)
                        .append("\" VALUES LESS THAN (TIMESTAMP '").append(time)
                        .append("') PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT) TABLESPACE \"DATSPACE01\" NOCOMPRESS ;");
                System.out.println(sb);
            });
        });
    }


    /**
     * 网联金融机构编码脚本
     */
    private static void epccOrgCodeTypeSQL(){

        //File file = new File("D:/1111code.txt");
        //File file = new File("D:/2222code.txt");
        File file = new File("D:/3333code.txt");
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件

            List<String> list = new ArrayList<>();
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                list.add(s);
            }
            br.close();
            String fileSqlPath="D:/orgCodeEpcc0831_1.sql";
            list.forEach(str->{
                StringBuilder sb = new StringBuilder();
                String[] strs=str.split(",",-1);
                sb.append("INSERT INTO GW_MAPPING_VARIABLE(ROW_ID,CHANNEL_ID,TRANSACTION_TYPE,VAR_NAME,CNL_VAR_VALUE,ITS_VAR_VALUE,CREATE_DATETIME,CREATE_OPERATOR,LAST_UPDATE_DATETIME,LAST_UPDATE_OPERATOR)VALUES(SEQ_GW_MAPPING_VARIABLE.NEXTVAL,'epcc',NULL,'issrId','"+strs[1]+"','"+strs[0]+"',SYSTIMESTAMP,'2454',SYSTIMESTAMP,'2454');");
                //文件写入
                try {
                    fileWriterMethod(fileSqlPath,sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 卡bin脚本
     */
    private static void epccCarbinSQL(){


       /* ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:/car300/最后一批数据.xls"));
        List<Map<String, Object>> listAll = excelReader.readAll();
        String fileSqlPath="D:/carBinToOrgCode_0827_20.sql";
        listAll.forEach(strMap->{
            StringBuilder sb = new StringBuilder();
            String carLength = strMap.get("carLength").toString();
            String carBin = strMap.get("carBin").toString();
            String carType = strMap.get("carType").toString();
            String orgCode = strMap.get("orgCode").toString();
            String orgName = strMap.get("orgName").toString();
            String carStr = strMap.get("carType").toString();
            if (carType.equals("贷记卡")){
                carType="00";
            }else if(carType.equals("借记卡")){
                carType="01";
            }else if(carType.equals("准贷记卡")){
                carType="00";
            }
            sb.append("INSERT INTO SYS_BANK_BIN_INF(ROW_ID,INS_ID_CD,ORG_ID,ACC1_OFFSET,ACC1_LEN,ACC1_TNUM,ACC2_OFFSET,ACC2_LEN,ACC2_TNUM,BIN_OFFSET,BIN_LEN,BIN_STA_NO,BIN_END_NO,BIN_TNUM,CARD_TP,CARD_DIS,CREATE_DATETIME,CREATE_OPERATOR,LAST_UPDATE_DATETIME,LAST_UPDATE_OPERATOR,INS_DESC) VALUES( SEQ_FRAMEWORK_OPERATION_LOG.NEXTVAL , '"+orgCode+"' , '"+orgCode+"' , '2 ' , '"+carLength+"' , '2' , '' , '' , '' ,  '' , '"+carBin.length()+"' , '"+carBin+"' , '"+carBin+"' , '2' , '"+carType+"' , '"+orgName+"-"+carStr+"' , NULL , '2454' , NULL , '2454', NULL  );");
            //文件写入
            try {
                fileWriterMethod(fileSqlPath,sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/


        File file = new File("D:/carbin.txt");
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件

            List<String> list = new ArrayList<>();
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                list.add(s);
            }
            br.close();

            list.forEach(str->{
                StringBuilder sb = new StringBuilder();
                String[] strs=str.split(",",-1);
                String carType = strs[5];
                if (carType.equals("贷记卡")){
                    carType="00";
                }else if(carType.equals("借记卡")){
                    carType="01";
                }else if(carType.equals("准贷记卡")){
                    carType="00";
                }
                sb.append("INSERT INTO SYS_BANK_BIN_INF(ROW_ID,INS_ID_CD,ORG_ID,ACC1_OFFSET,ACC1_LEN,ACC1_TNUM,ACC2_OFFSET,ACC2_LEN,ACC2_TNUM,BIN_OFFSET,BIN_LEN,BIN_STA_NO,BIN_END_NO,BIN_TNUM,CARD_TP,CARD_DIS,CREATE_DATETIME,CREATE_OPERATOR,LAST_UPDATE_DATETIME,LAST_UPDATE_OPERATOR,INS_DESC) VALUES( SEQ_FRAMEWORK_OPERATION_LOG.NEXTVAL , '"+strs[6]+"' , '"+strs[6]+"' , '2 ' , '"+strs[0]+"' , '2' , '' , '' , '' ,  '' , '"+strs[2]+"' , '"+strs[1]+"' , '"+strs[1]+"' , '2' , '"+carType+"' , '"+strs[3]+"-"+strs[5]+"' , NULL , '2454' , NULL , '2454', NULL  );");

                String fileSqlPath="D:/epccCarbin.sql";
                //文件写入
                try {
                    fileWriterMethod(fileSqlPath,sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 机构代码脚本
     */
    private static void epccOrgSQL(){

        File file = new File("D:/organization.txt");
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件

            List<String> list = new ArrayList<>();
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                list.add(s);
            }
            br.close();
            list.forEach(str->{
                StringBuilder sb = new StringBuilder();
                String[] strs=str.split(",",-1);
                sb.append("INSERT INTO SYS_ORGANIZATION( ORG_ID,ORG_TYPE,NAME_ZH,NAME_EN,DESCRIPTION,CREATE_DATETIME,CREATE_OPERATOR,LAST_UPDATE_DATETIME,LAST_UPDATE_OPERATOR)VALUES('"+strs[0]+"',2,'"+strs[1]+"','"+strs[0]+"','',SYSTIMESTAMP,'2454',SYSTIMESTAMP,'2454');");
                System.out.println(sb);
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 卡bin匹配银行
     */
    public static void carBinMatching(){

        //银联提供的卡


        ExcelReader excelEpccReader = ExcelUtil.getReader(ResourceUtil.getStream("D:\\car300\\carBinOrg.xls"));
        List<Map<String, Object>> listEpccAll = excelEpccReader.readAll();

        for (int i=0;i<listEpccAll.size();i++){
            Map<String, Object> mapEpcc = listEpccAll.get(i);
            String epccCode = mapEpcc.get("carBin").toString();
            String orgName = mapEpcc.get("orgName").toString();
            String orgCode = mapEpcc.get("orgCode").toString();

            ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:\\car300\\car.xls"));
            List<Map<String, Object>> listAll = excelReader.readAll();


            for (int j=0;j<listAll.size();j++){
                Map<String, Object> mapCar=listAll.get(j);
                String carBin = mapCar.get("carBin").toString();
                if(!carBin.equals(epccCode)){
                    System.out.println("carBin="+carBin +"-->" +"机构名称="+orgName+"-->"+"机构号"+"'"+orgCode+"'");
                    continue;
                }
            }
        }

/*
        listAll.forEach(orgAll ->{
            try {
                String carBin = orgAll.get("carBin").toString();
                ExcelReader excelEpccReader = ExcelUtil.getReader(ResourceUtil.getStream("D:\\car300\\carBinOrg.xls"));
                List<Map<String, Object>> listEpccAll = excelEpccReader.readAll();
                listEpccAll.forEach(epcCodeAll -> {
                    String epccCode = epcCodeAll.get("carBin").toString();
                    String orgName = epcCodeAll.get("orgName").toString();
                    String orgCode = epcCodeAll.get("orgCode").toString();
                    if(!carBin.equals(epccCode)){
                        System.out.println("carBin="+carBin +"-->" +"机构名称="+orgName+"-->"+"机构号"+"'"+orgCode+"'");
                    }
                });
            }catch (Exception e){
                //e.printStackTrace();
            }
        });
*/


        //System.out.println((listAll.toString()));

        //orgCode_null.txt
/*
        File file = new File("D:/orgCode_null_carBin.txt");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件
            List<String> list = new ArrayList<>();
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                list.add(s);
            }
            br.close();
            list.forEach(orgCar->{
                listAll.forEach(carAll ->{
                    String carNo = carAll.get("carNo").toString();
                    if(orgCar.equals(carNo)){
                        String bankName = carAll.get("bankName").toString();
                        String[] bankNames = bankName.split("\n");
                        System.out.println("carBin="+orgCar +"-->" +"机构名称="+bankNames[0]+"-->"+"机构号"+"'"+bankNames[1]+"'");
                    }
                });
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        */

    }

    /**
     * 更新carbin表 机构代码
     */
    public static void updateCarBin(){

        //carBinToOrgCode.xls

        //UPDATE SYS_BANK_BIN_INF SET ORG_ID='10030000' WHERE BIN_STA_NO='625153' and BIN_END_NO ='625153'

        ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:/carALLOrg.xls"));
        List<Map<String, Object>> listAll = excelReader.readAll();

        String fileSqlPath="D:/carBinToOrgCode.sql";
        listAll.forEach(strMap->{
            StringBuilder sb = new StringBuilder();
            String carBin = strMap.get("carBin").toString();
            String orgCode = strMap.get("orgCode").toString();
            sb.append("UPDATE SYS_BANK_BIN_INF SET ORG_ID="+orgCode+" WHERE BIN_STA_NO='"+carBin+"' and BIN_END_NO ='"+carBin+"' and ORG_ID IS NULL;");
            //文件写入
            try {
                fileWriterMethod(fileSqlPath,sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



    }


    public static void addInsert(){
        ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:\\car300\\插入临时表.xls"));
        List<Map<String, Object>> listAll = excelReader.readAll();

        listAll.forEach(carAll ->{
            String carNo = carAll.get("carBin").toString();
            String orgName = carAll.get("orgName").toString();
            String orgCode = carAll.get("orgCode").toString();
            StringBuilder sb = new StringBuilder();
            sb.append("insert into SYS_CAR_BIN_TEMP_LAST(car_code,bank_name,org_code) values('"+carNo+"','"+orgName+"',"+orgCode+");");
            System.out.println(sb);
        });
    }


    public static void addEpccCodeInsert(){


        ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:/epcc-code.xls"));
        List<Map<String, Object>> listAll = excelReader.readAll();

        listAll.forEach(epccAll ->{
            String epccCode = epccAll.get("epccCode").toString();
            String epccName = epccAll.get("epccName").toString();


            StringBuilder sb = new StringBuilder();
            sb.append("insert into SYS_EPCC_CODE_TMP(epcc_code,epcc_name) values('"+epccCode+"','"+epccName+"');");
            System.out.println(sb);


        });

    }


    /**
     * 机构和网联匹配
     */

    public static void orgCodeMatchingEpccCode(){
        //机构代码
        //ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:/xzwqq.xls"));
        // ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:/剩下未匹配的.xls"));
        ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("D:\\car300\\插入临时表.xls"));
        List<Map<String, Object>> listAll = excelReader.readAll();
        listAll.forEach(orgAll ->{
            try {
                String orgCode = orgAll.get("orgCode").toString();

                String orgName = orgAll.get("orgName").toString().trim().substring(0,3);


                //System.out.println(orgName);

                ExcelReader excelEpccReader = ExcelUtil.getReader(ResourceUtil.getStream("D:/epcc-code.xls"));
                List<Map<String, Object>> listEpccAll = excelEpccReader.readAll();

                listEpccAll.forEach(epcCodeAll -> {
                    String epccCode = epcCodeAll.get("epccCode").toString();
                    String epccName = epcCodeAll.get("epccName").toString();
                    if (epccName.indexOf(orgName) != -1) {

                        StringBuilder sb = new StringBuilder();
                        sb.append("UPDATE SYS_CAR_BIN_TEMP_LAST set EPCC_CODE='" + epccCode + "' where org_code=" + orgCode + " and epcc_code is null;");
                        System.out.println(sb);
                    }

                });
            }catch (Exception e){
                //e.printStackTrace();
            }

        });


    }

    public static void routerConfigInsert(){

        File file = new File("D:/router.txt");
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件

            List<String> list = new ArrayList<>();
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                list.add(s);
            }
            br.close();
            String fileSqlPath="D:/router_02.sql";
            list.forEach(str->{
                StringBuilder sb = new StringBuilder();
                String[] strs=str.split(",",-1);
                //sb.append("INSERT INTO GW_TRANS_ROUTER(ROW_ID,ACCESS_ID,BUSINESS_TYPE,PAY_TYPE,TRANS_CODE,ISSUER_ID,ACCT_CAT,CARD_BIN,CHANNEL_ID,TRANSACTION_TYPE,WEIGHT,CREATE_DATETIME,CREATE_OPERATOR,LAST_UPDATE_DATETIME,LAST_UPDATE_OPERATOR) VALUES(SEQ_GW_TRANS_ROUTER.NEXTVAL,'NEWOPS180611001','0001','06','2002','"+strs[0]+"','01',NULL,600132,2002,1,SYSTIMESTAMP,NULL,SYSTIMESTAMP,'2454');");
                sb.append("INSERT INTO GW_TRANS_ROUTER(ROW_ID,ACCESS_ID,BUSINESS_TYPE,PAY_TYPE,TRANS_CODE,ISSUER_ID,ACCT_CAT,CARD_BIN,CHANNEL_ID,TRANSACTION_TYPE,WEIGHT,CREATE_DATETIME,CREATE_OPERATOR,LAST_UPDATE_DATETIME,LAST_UPDATE_OPERATOR) VALUES(SEQ_GW_TRANS_ROUTER.NEXTVAL,'NEWOPS180611001','0001','06','2002','"+strs[0]+"','02',NULL,600132,2002,1,SYSTIMESTAMP,NULL,SYSTIMESTAMP,'2454');");
                //文件写入
                try {
                    fileWriterMethod(fileSqlPath,sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 方法 1：使用 FileWriter 写文件
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     */
    public static void fileWriterMethod(String filepath, String content) throws IOException {
        File f= new File(filepath);
        BufferedWriter output = new BufferedWriter(new FileWriter(f,true));//true,则追加写入text文本
        output.write(content);
        output.write("\r\n");//换行
        output.flush();
        output.close();
    }

    /**
     * 集团内子公司脚本程序
     */
    public static void cyjjDataProcess() {


        /**
         * RelatedPartyType {
         *     JOINT_CONTROL("受金控共同控制"),
         *     JOINT_VENTURE("联营、合营企业");
         */

        /**
         *    MINORITY_SHAREHOLDER("附属机构的少数股东"),
         *     JOINT_VENTURE("母公司及附属机构的联营、合营企业");
         */

        //INSERT INTO `optimus_4.0_pevc`.`t_related_party` (`type`, `sub_type`, `relate_party_name`, `code`, `relate_explain`, `relate_party_type`, `direct_shareholding_ratio`, `subscribed_capital`, `paid_capital`, `remark`, `is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `tenant_id`) VALUES ('INNER', 'MINORITY_SHAREHOLDER', '内蒙古蒙牛乳业（集团）股份有限公司\r\n内蒙古蒙牛乳业（集团）股份有限公司\r\n内蒙古蒙牛乳业（集团）股份有限公司', '91330000143995391Q', '1', 'JOINT_CONTROL', '1.0000', NULL, NULL, NULL, '0', NULL, NULL, '10078', '2023-03-06 14:31:26', NULL);
        ExcelReader excelEpccReader = ExcelUtil.getReader(ResourceUtil.getStream("E:\\财信数科\\2023年线上发布\\2023-03-23上线准备\\xzw.xls"));

        List<Map<String, Object>> listEpccAll = excelEpccReader.readAll();

        for (int i = 0; i < listEpccAll.size(); i++) {


            String type = (String)listEpccAll.get(i).get("关联方类型1");
            String subType1 = (String)listEpccAll.get(i).get("子类型1");
            String subType2 = (String)listEpccAll.get(i).get("子类型2");
            String name=(String)listEpccAll.get(i).get("公司名称");
            String code=(String)listEpccAll.get(i).get("组织机构代码");
            String codeType=(String)listEpccAll.get(i).get("关联方类型");
            Object direct_shareholding_ratio= listEpccAll.get(i).get("直接持股比例");
            direct_shareholding_ratio = String.valueOf(direct_shareholding_ratio);
            String relate_explain= (String)listEpccAll.get(i).get("关系说明");
            //String paid_capital= (String)listEpccAll.get(i).get("实缴出资额（元）");
            //String subscribed_capital= (String)listEpccAll.get(i).get("认缴出资额（元）");
            String remark= (String)listEpccAll.get(i).get("备注");

            String typeValue="";
            if(type!=null&&type.equals("集团内关联方")){
                typeValue="INNER";
            }else{
                typeValue="OUTER";
            }
            String codeTypeValue="";
            if(codeType!=null){
                if(codeType.equals("受金控共同控制")) {
                    codeTypeValue = "JOINT_CONTROL";
                }else{
                    codeTypeValue = "JOINT_VENTURE";
                }
            }

            StringBuilder sb = new StringBuilder();
            if(typeValue.equals("INNER")) {
                sb.append("INSERT INTO `optimus_4.0_pevc`.`t_related_party` (`type`, `sub_type`, `relate_party_name`, `code`, `relate_explain`, `relate_party_type`, `direct_shareholding_ratio`, `subscribed_capital`, `paid_capital`, `remark`, `is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `tenant_id`) VALUES ('" + typeValue + "', null, '"+name+"', '"+code+"', '"+relate_explain+"', '"+codeTypeValue+"', '"+direct_shareholding_ratio+"', NULL, NULL, '"+remark+"', '0', '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), NULL);");
            }else{
                if((subType1==null || subType1.equals(""))&& (subType2==null || subType2.equals(""))){
                    continue;
                }else {
                   if((subType1!=null && !subType1.equals("")) && (subType2!=null && !subType2.equals(""))){
                       sb.append("INSERT INTO `optimus_4.0_pevc`.`t_related_party` (`type`, `sub_type`, `relate_party_name`, `code`, `relate_explain`, `relate_party_type`, `direct_shareholding_ratio`, `subscribed_capital`, `paid_capital`, `remark`, `is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `tenant_id`) VALUES ('" + typeValue + "', 'MINORITY_SHAREHOLDER', '"+name+"', '"+code+"', '"+relate_explain+"', '"+codeTypeValue+"', '"+direct_shareholding_ratio+"', NULL, NULL, '"+remark+"', '0', '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), NULL);");
                       sb.append("INSERT INTO `optimus_4.0_pevc`.`t_related_party` (`type`, `sub_type`, `relate_party_name`, `code`, `relate_explain`, `relate_party_type`, `direct_shareholding_ratio`, `subscribed_capital`, `paid_capital`, `remark`, `is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `tenant_id`) VALUES ('" + typeValue + "', 'JOINT_VENTURE', '"+name+"', '"+code+"', '"+relate_explain+"', '"+codeTypeValue+"', '"+direct_shareholding_ratio+"', NULL, NULL, '"+remark+"', '0', '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), NULL);");
                   }else{
                       if(subType1!=null && !subType1.equals("")){
                           sb.append("INSERT INTO `optimus_4.0_pevc`.`t_related_party` (`type`, `sub_type`, `relate_party_name`, `code`, `relate_explain`, `relate_party_type`, `direct_shareholding_ratio`, `subscribed_capital`, `paid_capital`, `remark`, `is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `tenant_id`) VALUES ('" + typeValue + "', 'JOINT_VENTURE', '"+name+"', '"+code+"', '"+relate_explain+"', '"+codeTypeValue+"', '"+direct_shareholding_ratio+"', NULL, NULL, '"+remark+"', '0', '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), NULL);");
                       }else{

                           if (subType2!=null && !subType2.equals("")){
                               sb.append("INSERT INTO `optimus_4.0_pevc`.`t_related_party` (`type`, `sub_type`, `relate_party_name`, `code`, `relate_explain`, `relate_party_type`, `direct_shareholding_ratio`, `subscribed_capital`, `paid_capital`, `remark`, `is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `tenant_id`) VALUES ('" + typeValue + "', 'MINORITY_SHAREHOLDER', '"+name+"', '"+code+"', '"+relate_explain+"', '"+codeTypeValue+"', '"+direct_shareholding_ratio+"', NULL, NULL, '"+remark+"', '0', '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '10078', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), NULL);");
                           }

                       }
                   }
                }
            }
            System.out.println(sb);

        }
    }

    public static void cyjjOAUserId() {

        ExcelReader excelEpccReader = ExcelUtil.getReader(ResourceUtil.getStream("F:\\OA_USER_ID.xls"));

        List<Map<String, Object>> listEpccAll = excelEpccReader.readAll();

        double a=201000.0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listEpccAll.size(); i++) {


            String oaUserId = (String) listEpccAll.get(i).get("oaUserId");
            String unifyId = (String) listEpccAll.get(i).get("unifyId");
            String userName = (String) listEpccAll.get(i).get("userName");
            userName = userName.substring(0,userName.indexOf("["));
            //String sql = ;
            sb.append("update `optimus_4.0_pevc`.`users` set oa_user_id='"+oaUserId+"',user_id='"+unifyId+"' where name='"+userName+"';\n");

        }
        System.out.println(sb);
    }


    public static void cyjjAfterUserInfo(){

        ExcelReader userInfoXls = ExcelUtil.getReader(ResourceUtil.getStream("F:\\userInfo.xls"));
        List<Map<String, Object>> userInfos = userInfoXls.readAll();
        Map<String,Object> resultMap=new HashMap<>();
        for (int i = 0; i < userInfos.size(); i++) {
            Long userId = (Long) userInfos.get(i).get("userId");
            String userName = (String) userInfos.get(i).get("userName");
            resultMap.put(userName,userId);
        }

        // 投后项目负责人
        // c74ea7a4409fb71634cf5ceca7e3c726

        //投后基金负责人
        //3f4b706e44e8606858a272cd91d57165

        // 项目成员
        // 1e5bfba52f5abce311c29bc9f8d021f7

        // 投后部人员
        //5568f08c0d80703558246324c810665a

        //ExcelReader afterProjectData = ExcelUtil.getReader(ResourceUtil.getStream("F:\\投后项目管理人员数据补录.xls"));
        ExcelReader afterProjectData = ExcelUtil.getReader(ResourceUtil.getStream("F:\\投后项目管理人员数据补录 -去重数据.xls"));
        List<Map<String, Object>> afterProjects = afterProjectData.readAll();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < afterProjects.size(); i++) {

            String fzrName = (String)afterProjects.get(i).get("负责人");
            String projectType = (String)afterProjects.get(i).get("项目类型");
            Long entityId = (Long)afterProjects.get(i).get("实体ID");
            String [] fzrNames= fzrName.split("、");
            for (int j=0;j<fzrNames.length;j++){
                String name= fzrNames[j];
                if("此项目暂未进入投后".equals(name)){
                   // System.out.println("不处理"+name);
                    continue;
                }
                Long userIdNew=(Long) resultMap.get(name);
                if(projectType.equals("基金项目") || projectType.equals("基金主体")){
                    //AFTER_FUND
                    sb.append("INSERT INTO `optimus_4.0_pevc`.`t_b_group_members` (`is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `business_id`, `business_type`, `user_id`, `role_id`, `tenant_id`) VALUES ( '0', '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+entityId+"', 'AFTER_FUND', '"+userIdNew+"', '3f4b706e44e8606858a272cd91d57165', NULL);\n");

                }else{

                    sb.append("INSERT INTO `optimus_4.0_pevc`.`t_b_group_members` (`is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `business_id`, `business_type`, `user_id`, `role_id`, `tenant_id`) VALUES ( '0', '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+entityId+"', 'AFTER_PROJECT', '"+userIdNew+"', 'c74ea7a4409fb71634cf5ceca7e3c726', NULL);\n");

                }
            }

            Object xmcyName = (Object) afterProjects.get(i).get("项目成员");
            if("0".equals(xmcyName.toString())){
               // System.out.println("项目成员:"+xmcyName);
                continue;
            }

            String [] xmcyNames= xmcyName.toString().split("、");
            for (int j=0;j<xmcyNames.length;j++){
                String name= xmcyNames[j];
                if("0".equals(name)){
                    //System.out.println("---用户为O不处理----");
                    continue;
                }
                Long userIdNew=(Long) resultMap.get(name);

                if(projectType.equals("基金项目") || projectType.equals("基金主体")){
                    sb.append("INSERT INTO `optimus_4.0_pevc`.`t_b_group_members` (`is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `business_id`, `business_type`, `user_id`, `role_id`, `tenant_id`) VALUES ( '0', '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+userIdNew+"',DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+entityId+"', 'AFTER_FUND', '"+userIdNew+"', '1e5bfba52f5abce311c29bc9f8d021f7', NULL);\n");

                }else{
                    sb.append("INSERT INTO `optimus_4.0_pevc`.`t_b_group_members` (`is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `business_id`, `business_type`, `user_id`, `role_id`, `tenant_id`) VALUES ( '0', '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+userIdNew+"',DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+entityId+"', 'AFTER_PROJECT', '"+userIdNew+"', '1e5bfba52f5abce311c29bc9f8d021f7', NULL);\n");

                }

            }

            Object thbName = (Object)afterProjects.get(i).get("投后部人员");
            if("0".equals(thbName.toString())){
                //System.out.println("项目成员:"+xmcyName);
                continue;
            }
            String [] thbNames= thbName.toString().split("、");
            for (int j=0;j<thbNames.length;j++){
                String name= thbNames[j];
                if("0".equals(name)){
                    //System.out.println("---用户为O不处理----");
                    continue;
                }
                Long userIdNew=(Long) resultMap.get(name);
                if(projectType.equals("基金项目") || projectType.equals("基金主体")){
                    sb.append("INSERT INTO `optimus_4.0_pevc`.`t_b_group_members` (`is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `business_id`, `business_type`, `user_id`, `role_id`, `tenant_id`) VALUES ( '0', '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+entityId+"', 'AFTER_FUND', '"+userIdNew+"', '5568f08c0d80703558246324c810665a', NULL);\n");

                }else{
                    sb.append("INSERT INTO `optimus_4.0_pevc`.`t_b_group_members` (`is_deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `business_id`, `business_type`, `user_id`, `role_id`, `tenant_id`) VALUES ( '0', '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+userIdNew+"', DATE_ADD(SYSDATE(), INTERVAL 8 HOUR), '"+entityId+"', 'AFTER_PROJECT', '"+userIdNew+"', '5568f08c0d80703558246324c810665a', NULL);\n");

                }

            }
        }
        System.out.println(sb);

    }

    public static void cyjjUserInfoEncryytor() {
        try {
            String str=null;
            List<String> nationalIndustryCode = JSON.parseArray(str, String.class);
            ExcelReader afterProjectData = ExcelUtil.getReader(ResourceUtil.getStream("E:\\财信数科\\2023年线上发布\\2023-09-06上线准备\\员工档案信息 - 副本.xls"));
            List<Map<String, Object>> afterProjects = afterProjectData.readAll();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < afterProjects.size(); i++) {

                String name = (String) afterProjects.get(i).get("姓名");
                String idCard = (String) afterProjects.get(i).get("身份证号码");
                if(name!=null && idCard!=null) {
                    String enstr = EncryptorSecuriyUtil.senstiveEncrypt(idCard);
                    sb.append("update `optimus_4.0_pevc`.`users` set id_card='" + enstr + "' where   name='" + name + "';\n");
                }
            }
            System.out.println(sb);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
