package com.linzhi.datasource.app.controller;


import com.linzhi.datasource.app.enums.ExcelType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.linzhi.datasource.app.constants.SecurityConstants.API_PREFIX;

@RestController
@RequestMapping(API_PREFIX+"/rf/excel")
public class ExcelController {



    @PostMapping("/import")
    @ApiOperation(value = "数据导入",notes = "INVESTMENT_DETAILS——>投资业务明细表 \\\\n\\\" +\\n\" +\n" +
            "            \"            \\\"SPV_INFO——>SPV信息表\\\\n\\\" +\\n\" +\n" +
            "            \"            \\\"RISK_MITIGATION——>风险缓释表\\\\n\\\" +\\n\" +\n" +
            "            \"            \\\"POTENTIAL_RISK_EXPOSURE——>潜在风险暴露表 \\\\n\\\" +\\n\" +\n" +
            "            \"            \\\"SPV_CAPITAL_SOURCE——>SPV资金来源\\\")")
    public void importRiskConcentrationExcel(//@ApiParam(value = "上传文件", required = true) @RequestParam MultipartFile multipartFiles,
                                             @ApiParam(value ="枚举值",required = true) @RequestParam String excelType) throws Exception{
        ExcelType type=ExcelType.findEnumByName(excelType);
       /* ExcelFacde excelFacde = excelServiceFactory.getExcelTypeFacde(type);
        excelFacde.importExcelData(multipartFiles);*/
    }

}
