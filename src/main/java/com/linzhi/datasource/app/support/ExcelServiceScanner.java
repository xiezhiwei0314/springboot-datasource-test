package com.linzhi.datasource.app.support;


import com.linzhi.datasource.app.service.ExcelServiceDispatcher;
import com.linzhi.datasource.app.service.IExcelService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
@Data
public class ExcelServiceScanner implements BeanPostProcessor {

	private ExcelServiceRigistry excelServiceRigistry;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ExcelServiceDispatcher) {
			log.warn("Excel 服务注册忽略ExcleServiceDispatcher, bean={}", bean);
			return bean;
		}

		if (bean instanceof IExcelService) {
			//Class<?> targetClass = AopUtils.getTargetClass(bean);
			/*if (AnnotationUtils.findAnnotation(targetClass, Service.class) != null) {
				log.warn("支付服务注册忽略dubbo 服务, bean={}", bean);
				return bean;
			}*/

			IExcelService excelService = (IExcelService) bean;
			excelServiceRigistry.register(excelService);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

}
