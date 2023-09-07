package com.linzhi.datasource.app.service;

import com.linzhi.datasource.app.dto.ExcelImportDto;
import com.linzhi.datasource.app.enums.ExcelType;

import java.util.List;

public interface IExcelService {


     void importExcelData(ExcelImportDto excelImportDto);

     List<ExcelType> getSupportExcelTypes();
}
