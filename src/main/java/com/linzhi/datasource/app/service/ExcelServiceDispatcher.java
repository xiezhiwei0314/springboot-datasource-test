package com.linzhi.datasource.app.service;

import com.linzhi.datasource.app.dto.ExcelImportDto;
import com.linzhi.datasource.app.enums.ExcelType;
import com.linzhi.datasource.app.support.ExcelServiceRigistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Slf4j
public abstract class ExcelServiceDispatcher implements IExcelService{


    @Autowired
    private ExcelServiceRigistry excelServiceRigistry;
    @Override
    public void importExcelData(ExcelImportDto excelImportDto) {

        try {
            ExcelType excelType = excelImportDto.getExcelType();
            log.info("excel request: {} with payType:{} ", excelImportDto, excelType);
            IExcelService excelService = route(excelType);
            excelService.importExcelData(excelImportDto);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public abstract  List<ExcelType> getSupportExcelTypes();


    private IExcelService route(ExcelType excelType) throws Exception {
        List<ExcelType> supportPayTypes = getSupportExcelTypes();
        if (supportPayTypes != null && supportPayTypes.contains(excelType)) {
            IExcelService excelService = excelServiceRigistry.lookup(excelType);
            log.info("route to the excelService service: {} with excelType: {}", excelService, excelType);
            if (excelService == null) {
                throw new Exception("Excel类型不支持");
            }
            return excelService;
        } else {
            throw new Exception("Excel类型不支持");
        }
    }
}
