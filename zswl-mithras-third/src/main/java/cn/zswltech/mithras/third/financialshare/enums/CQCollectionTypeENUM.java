package cn.zswltech.mithras.third.financialshare.enums;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 收款单-收款类型
 **/
@Getter
public enum CQCollectionTypeENUM {
    SCENARIO1("收保证金（包括客户保证金、厂商质保金）", "SK03.004", "收取租赁保证金", "CF1.02.01.01.03", "收到其他与经营活动有关的现金", "-"),
    SCENARIO2("收租金（本金、利息、罚息）", "SK01.017", "其他租赁收款", "CF1.02.01.01.01.02", "提供劳务收到的现金", "收到除本金外的利息等"),
    SCENARIO3("收租金（本金、利息、罚息）", "SK01.017", "其他租赁收款", "CF1.02.02.01.01", "收回投资收到的现金", "收到本金"),
    SCENARIO4("收其他：首期利息、首期租金、手续费、咨询费/服务费、名义价款、租前息、提前终止补偿金、违约金", "SK01.017", "其他租赁收款", "CF1.02.01.01.01.02", "提供劳务收到的现金", "-"),
    SCENARIO5("银行授信款", "SK08.001", "收到短期借款本金", "CF1.02.03.01.02", "取得借款收到的现金", "-"),
    SCENARIO6("银行授信款", "SK08.002", "收到其他短期借款本金", "CF1.02.03.01.02", "取得借款收到的现金", "-"),
    SCENARIO7("银行授信款", "SK08.007", "收到长期借款本金", "CF1.02.03.01.02", "取得借款收到的现金", "-"),
    SCENARIO8("银行授信款", "SK08.011", "收到其他长期借款本金", "CF1.02.03.01.02", "取得借款收到的现金", "-"),
    SCENARIO9("银行退保证金", "SK03.007", "收取其他保证金", "CF1.02.01.01.03", "收到其他与经营活动有关的现金", "-"),
    SCENARI10("咨询业务收款", "SK01.046", "咨询业务收款", "CF1.02.01.01.01.02", "咨询业务收款", "-"),
    SCENARI11("ABS", "SK08.023", "收到资产支持证券本金", "CF1.02.03.01.02", "收到资产支持证券本金", "-"),
    SCENARI12("MTN", "SK08.014", "收到中期票据本金", "CF1.02.03.01.02", "收到中期票据本金", "-"),
    SCENARI13("公司债", "SK08.012", "收到公司债本金", "CF1.02.03.01.02", "收到公司债本金", "-"),
    SCENARI14("SCP", "SK08.004", "收到短期融资券本金", "CF1.02.03.01.02", "收到短期融资券本金", "-"),
    ;

    private final String scenario;
    private final String code;
    private final String paymentType;
    private final String channelCode;
    private final String channelName;
    private final String remark;

    private static Map<String, CQCollectionTypeENUM> map;

    static {
        map = Stream.of(CQCollectionTypeENUM.values()).collect(Collectors.toMap(CQCollectionTypeENUM::name, e -> e, (a, b) -> b));
    }

    public static CQCollectionTypeENUM of(String exName) {
        return map.get(exName);
    }

    public static CQCollectionTypeENUM ofCode(String code) {
        for(CQCollectionTypeENUM typeENUM : CQCollectionTypeENUM.values()){
            if(ObjectUtil.equal(code, typeENUM.code)){
                return typeENUM;
            }
        }
        return null;
    }


    CQCollectionTypeENUM(String scenario, String code, String paymentType, String channelCode, String channelName, String remark) {
        this.scenario = scenario;
        this.code = code;
        this.paymentType = paymentType;
        this.channelCode = channelCode;
        this.channelName = channelName;
        this.remark = remark;
    }

    public static CQCollectionTypeENUM getCQTypeByBusiness(CashFlowItemEnum cashFlowItem, ProjectBizType bizType, boolean isPrincipal) {
        switch (cashFlowItem) {
            case RETENTION_MONEY:
            case EARNEST_MONEY:
                return SCENARIO1;
            case RENT:
                if (isPrincipal) {
                    return SCENARIO3;
                } else {
                    return SCENARIO2;
                }
            case FIRST_RENT:
                return SCENARIO3;
            case OTHERAMOUNT:
                return SCENARI10;
            default:
                return SCENARIO4;
        }
    }

    public static CQCollectionTypeENUM getCQTypeByCapital(String timeLimitType, boolean isPrincipal) {
        if (FundFinancingTimeLimitTypeEnum.LONG_TERM_LOAN.name().equals(timeLimitType)) {
            if (isPrincipal) {
                return SCENARIO7;
            } else {
                return SCENARIO8;
            }
        } else {
            if (isPrincipal) {
                return SCENARIO5;
            } else {
                return SCENARIO6;
            }
        }
    }
}