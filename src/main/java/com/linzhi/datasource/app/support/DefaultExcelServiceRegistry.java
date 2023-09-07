package com.linzhi.datasource.app.support;

import com.linzhi.datasource.app.enums.ExcelType;
import com.linzhi.datasource.app.service.IExcelService;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultExcelServiceRegistry implements ExcelServiceRigistry {

	private Map<ExcelType, IExcelService> cacheMap = new ConcurrentHashMap<>();

	@Override
	public void register(IExcelService excelService) {
		if (excelService != null) {
			for (ExcelType excelType : excelService.getSupportExcelTypes()) {
				if (cacheMap.containsKey(excelType)) {
					log.warn("同一支付类型， 重复注册。payType = {}, exist = {}, new={}", excelType, cacheMap.get(excelType),
							excelService);
				}
				cacheMap.put(excelType, excelService);
			}
		}
	}

	@Override
	public IExcelService lookup(ExcelType excelType) {
		return cacheMap.get(excelType);
	}
}
