package cn.zswltech.mithras.fund.domain.enums.financing;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN;

/**
 * 融资管理
 *
 * @author wangchuanhao
 * @date 2022/9/21 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum FundFinancingMaterialsEnum implements PullDown, IMaterialsTypeConvert {
    /**
     * 流动资金贷款、项目贷款
     */
    /*CURRENT_LOAN_CONTRACT("流贷合同", 1),
    RECEIVABLE_PLEDGE_CONTRACT("应收账款质押合同（若有质押，需上传）", 2),
    ACCOUNT_SUPERVISION_AGREEMENT("账户监管协议（若有监管，需上传）", 3),*/
    LOAN_CONTRACT("借款合同", 1),
    LOAN_RECEIPT("借款借据", 2),

    /**
     * 保理融资
     */
    FACTORING_CONTRACT("保理主合同", 4),
    RECEIVABLE_ASSIGNMENT_NOTICE("应收账款转让通知书", 5),
    FACTORING_CREDIT_PAY_NOTICE("保理额度支用通知书", 6),

    /**
     * 信用证
     */
    DOMESTIC_CREDIT_FINANCING_AGREEMENT("国内信用证融资主协议", 7),
    EXPENSES_BILL("费用清单", 8),

    /**
     * 银行承兑汇票
     */
    BANK_ACCEPTANCE_AGREEMENT("银行承兑汇票协议", 9),

    /**
     * 商业承兑汇票
     */
    ACCEPTANCE_DISCOUNT_AGREEMENT("承兑汇票贴现协议", 10),

    /**
     * 通用类型
     */
    GUARANTEE_CONTRACT("担保合同", 11),
    OTHER("其他", 12),

    ;

    private final String display;

    /**
     * 排序优先级
     */
    private Integer sort;

    private static final Map<String, FundFinancingMaterialsEnum> map;

    static {
        map = Stream.of(FundFinancingMaterialsEnum.values()).collect(Collectors.toMap(FundFinancingMaterialsEnum::name, e -> e));
    }

    public static List<FundFinancingMaterialsEnum> getMaterialTypeByFinancingType(String bizType){
        FundFinancingBizTypeEnum bizTypeEnum = FundFinancingBizTypeEnum.finaByName(bizType);
        if(bizTypeEnum == null){
            return Collections.emptyList();
        }
        switch (bizTypeEnum){
            case WORKING_CAPITAL_LOAN:
            case PROJECT_LOAN:
                return Arrays.asList(FundFinancingMaterialsEnum.LOAN_CONTRACT,
                        FundFinancingMaterialsEnum.LOAN_RECEIPT,
                        /*FundFinancingMaterialsEnum.CURRENT_LOAN_CONTRACT,
                        FundFinancingMaterialsEnum.RECEIVABLE_PLEDGE_CONTRACT,
                        FundFinancingMaterialsEnum.ACCOUNT_SUPERVISION_AGREEMENT,*/
                        FundFinancingMaterialsEnum.GUARANTEE_CONTRACT,
                        FundFinancingMaterialsEnum.OTHER);
            case FACTORING_FINANCING:
                return Arrays.asList(FundFinancingMaterialsEnum.FACTORING_CONTRACT,
                        FundFinancingMaterialsEnum.RECEIVABLE_ASSIGNMENT_NOTICE,
                        FundFinancingMaterialsEnum.FACTORING_CREDIT_PAY_NOTICE,
                        FundFinancingMaterialsEnum.GUARANTEE_CONTRACT,
                        FundFinancingMaterialsEnum.OTHER);
            case LETTER_OF_CREDIT:
                return Arrays.asList(FundFinancingMaterialsEnum.DOMESTIC_CREDIT_FINANCING_AGREEMENT,
                        FundFinancingMaterialsEnum.EXPENSES_BILL,
                        FundFinancingMaterialsEnum.GUARANTEE_CONTRACT,
                        FundFinancingMaterialsEnum.OTHER);
            case BANK_ACCEPTANCE:
                return Arrays.asList(FundFinancingMaterialsEnum.BANK_ACCEPTANCE_AGREEMENT,
                        FundFinancingMaterialsEnum.OTHER);
            case COMMERCE_ACCEPTANCE:
                return Arrays.asList(FundFinancingMaterialsEnum.ACCEPTANCE_DISCOUNT_AGREEMENT,
                        FundFinancingMaterialsEnum.OTHER);
            case SYNDICATIONS:
                return Collections.singletonList(FundFinancingMaterialsEnum.OTHER);
            default:
                return Collections.emptyList();
        }
    }

    public static FundFinancingMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "FUND_FINANCING";
    }

    @Override
    public String display() {
        return display;
    }
}
