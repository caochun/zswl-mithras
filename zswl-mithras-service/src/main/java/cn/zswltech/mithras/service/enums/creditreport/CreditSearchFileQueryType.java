package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.service.enums.contract.ContractChangeMaterialEnum;
import cn.zswltech.mithras.service.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 合同文件查询类型
 *
 * @author wangchuanhao
 * @date 2023/2/6 3:14 PM
 */
@AllArgsConstructor
@Getter
public enum CreditSearchFileQueryType {

    BUSINESS_LICENSE("营业执照复印件", Arrays.asList(
            CreditReportMaterialSubTypeEnum.BUSINESS_LICENSE.name())),
    LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_ENTERPRISE("法人身份证复印件(正面)", Arrays.asList(
            CreditReportMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_ENTERPRISE.name())),

    LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_ENTERPRISE("法人身份证复印件(反面)", Arrays.asList(
            CreditReportMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_ENTERPRISE.name())),

    CREDIT_LETTER("征信授权书", Arrays.asList(CreditReportMaterialSubTypeEnum.CREDIT_LETTER.name())),

    LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_HANDLER("法人身份证复印件(正面)", Arrays.asList(CreditReportMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_HANDLER.name(),
            CreditReportMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_HANDLER.name())),
    LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_HANDLER("法人身份证复印件(反面)", Arrays.asList(
            CreditReportMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_HANDLER.name())),
    ;
    /**
     * 描述
     */
    private String desc;

    /**
     * 要查询的文件类型列表
     */
    private List<String> materialsTypeList;

    private static Map<String, CreditSearchFileQueryType> map;

    static {
        map = Stream.of(CreditSearchFileQueryType.values()).collect(Collectors.toMap(CreditSearchFileQueryType::name, e -> e));
    }

    public static CreditSearchFileQueryType of(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

}
