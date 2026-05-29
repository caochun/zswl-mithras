package cn.zswltech.mithras.service.enums.materialsList;

import cn.zswltech.mithras.common.enums.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName FileAuthMethodEnum
 * @Description 模版管理，注意模版名称不能重复否则会覆盖
 * @Author jackerhe
 * @Date 2023/1/16 2:34 下午
 * @Version 1.0
 **/
public enum FileTemplateEnum implements PullDown {
    TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW("现金流测算表模板.xlsx"),
    TEMPLATE_OSS_NAME_ACTUAL_PAYMENT_ITEM("实际支付表模板.xlsx"),
    TEMPLATE_OSS_NAME_ESTIMATE_PAYMENT_ITEM("概算支付表模板.xlsx"),
    TEMPLATE_OSS_NAME_ACTUAL_RENT_ITEM("实际租金表模板.xlsx"),
    TEMPLATE_OSS_NAME_ESTIMATE_RENT_ITEM("概算租金表模版.xlsx"),
    TEMPLATE_OSS_NAME_CONTRACT_LEASE_ITEM("租赁物清单模板.xlsx"),
    TEMPLATE_OSS_NAME_MORTGAGE_ITEM("抵押清单模版.xlsx"),
    TEMPLATE_OSS_NAME_PLEDGE_ITEM("质押物清单模版.xlsx"),
    TEMPLATE_OSS_BASE_DATA_LPR("基础数据-LPR设置-模板.xlsx"),
    TEMPLATE_OSS_NAME_PAYMENT_POLICY_ITEM("保单信息模版.xlsx"),
    TEMPLATE_OSS_NAME_LEASED_PROPERTY_TYPE("租赁物类型模板.xlsx"),
    FTP_MONTHLY_GUIDANCE("月度FTP定价表单模版.xlsx"),
    FTP_QUARTERLY_GUIDANCE("季度最低收益率指导模版.xlsx"),
    TEMPLATE_OSS_NAME_FINANCING_REPAY("融资管理_还款表模板.xlsx"),
    LOAN_REVIEW("放款底稿模板.xlsx"),
    RELATED_TRANSACTION_SUBMISSION("关联交易报送导入模版.xlsx"),
    BUSINESS_TARGET_DATA("业务目标数据模版.xlsx"),
    LIQUIDITY_ACCOUNT_BALANCE("账户余额表明细导入模板.xlsx"),
    ECL_ADD_RECORD("减值导入模版.xlsx"),
    STAMP_DUTY_RECORD("印花税明细导入模板.xlsx"),
    ;

    public final String display;

    FileTemplateEnum(String display) {
        this.display = display;
    }

    private static Map<String, FileTemplateEnum> map;

    static {
        map = Stream.of(FileTemplateEnum.values()).collect(Collectors.toMap(FileTemplateEnum::name, e -> e));
    }

    public static FileTemplateEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
