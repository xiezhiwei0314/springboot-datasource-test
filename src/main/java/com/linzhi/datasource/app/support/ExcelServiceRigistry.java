package com.linzhi.datasource.app.support;


import com.linzhi.datasource.app.enums.ExcelType;
import com.linzhi.datasource.app.service.IExcelService;

public interface ExcelServiceRigistry {

	 void register(IExcelService excelService);

	 IExcelService lookup(ExcelType excelType);
}
