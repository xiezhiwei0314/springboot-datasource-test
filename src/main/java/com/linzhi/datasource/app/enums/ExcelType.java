package com.linzhi.datasource.app.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ExcelType {

    /**
     * 投资业务明细表.
     */
    INVESTMENT_DETAILS(1),
    /**
     * SPV信息表.
     */
    SPV_INFO(2),

    /**
     * 风险缓释表.
     */
    RISK_MITIGATION(3),

    /**
     * 潜在风险暴露表.
     */
    POTENTIAL_RISK_EXPOSURE(4),

    /**
     * SPV资金来源表
     */
    SPV_CAPITAL_SOURCE(5);


    int type;

    ExcelType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

    private static Map<Integer, ExcelType> lookupMap = new HashMap<>();

    static {
        for (ExcelType excelType : ExcelType.values()) {
            lookupMap.put(excelType.getValue(), excelType);
        }
    }

    public static ExcelType getStatusByValue(Integer code) {
        return lookupMap.get(code);
    }

    public static int getValue(ExcelType type) {
        return type.getValue();
    }

    /**
     * 根据name查找
     * @param name 枚举name
     * @return 枚举对象
     */
    public static ExcelType findEnumByName(String name) {
        for (ExcelType excelTypeEnum : ExcelType.values()) {
            if (excelTypeEnum.name().equals(name)) {
                //如果需要直接返回code则更改返回类型为String,return statusEnum.code;
                return excelTypeEnum;
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }

    public static void main(String[] args) {
        ExcelType excelType = findEnumByName("INVESTMENT_DETAILS");
        System.out.println(excelType);
    }

    public static List<ExcelType> getAllTypes() {
        ExcelType [] types = ExcelType.values();
        List<ExcelType> result = new ArrayList<ExcelType>();
        for (int i = 0; i < types.length; i++) {
            result.add(types[i]);
        }
        return result;
    }

    private static Map<Integer, String> desc = new HashMap<Integer, String>();
    static {
        desc.put(INVESTMENT_DETAILS.getValue(), "投资业务明细表");
        desc.put(SPV_INFO.getValue(), "SPV信息表");
        desc.put(RISK_MITIGATION.getValue(), "风险缓释表");
        desc.put(POTENTIAL_RISK_EXPOSURE.getValue(), "潜在风险暴露表");
        desc.put(SPV_CAPITAL_SOURCE.getValue(), "SPV资金来源表");

    }
}
