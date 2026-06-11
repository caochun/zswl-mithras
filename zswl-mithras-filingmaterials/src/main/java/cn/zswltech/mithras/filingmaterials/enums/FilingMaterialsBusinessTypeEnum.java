package cn.zswltech.mithras.filingmaterials.enums;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 资料归档归档类型
 * @author: lllin
 * @date: 2025-12-04
 */
public enum FilingMaterialsBusinessTypeEnum {
    FILING_BUSINESS_CLIENT("FILING_BUSINESS_CLIENT", "", "", "BASIC_MATERIALS", "基础资料"),
    FILING_BUSINESS_INNER_OPERATION("FILING_BUSINESS_INNER_OPERATION", "内部操作归档资料", "FILING_INNER_OPERATION", "INNER_OPERATION_MATERIALS", "内部操作资料"),
    FILING_BUSINESS_PAYMENT("FILING_BUSINESS_PAYMENT", "合同资料归档资料", "FILING_CONTRACT", "CONTRACT_MATERIALS", "合同资料"),
    FILING_BUSINESS_LEASEHOLD("FILING_BUSINESS_LEASEHOLD", "租赁物归档资料", "FILING_LEASEHOLD", "LEASEHOLD_MATERIALS", "租赁物资料"),
    FILING_BUSINESS_COLLATERALIZATION("FILING_BUSINESS_COLLATERALIZATION", "抵质押归档资料", "FILING_COLLATERALIZATION", "COLLATERALIZATION_MATERIALS", "抵质押资料");;

    FilingMaterialsBusinessTypeEnum(String code, String display, String groupCode, String tabCode, String tabName) {
        this.code = code;
        this.display = display;
        this.groupCode = groupCode;
        this.tabCode = tabCode;
        this.tabName = tabName;
    }

    public final String code;
    public final String display;
    public final String groupCode;
    public final String tabCode;
    public final String tabName;


    public static Set<String> getTabName(List<String> codeList) {
        Set<String> tabNameList = new HashSet<>();
        for (FilingMaterialsBusinessTypeEnum value : FilingMaterialsBusinessTypeEnum.values()) {
            if (codeList.contains(value.getCode())) {
                tabNameList.add(value.getTabName());
            }
        }
        return tabNameList;
    }


    public static FilingMaterialsBusinessTypeEnum of(String tabCode) {
        for (FilingMaterialsBusinessTypeEnum value : FilingMaterialsBusinessTypeEnum.values()) {
            if (value.getTabCode().equals(tabCode)) {
                return value;
            }
        }
        return null;
    }

    public static List<String> getBusinessTypeAll() {
        return Arrays.stream(values())
                .map(FilingMaterialsBusinessTypeEnum::getCode)
                .collect(Collectors.toList());
    }


    public String getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }

    public String getTabCode() {
        return tabCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getTabName() {
        return tabName;
    }
}
