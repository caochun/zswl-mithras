package cn.zswltech.mithras.service.enums.third;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName FinancialUrlENUM
 * @Description
 * @Author jackerhe
 * @Date 2022/10/21 10:40 上午
 * @Version 1.0
 **/
public enum ExceptionSourceENUM {

    BUSINESS_FLOW("业务流水"),
    BANK_FLOW("银行流水"),
    ASSET_SIDE_CONTRACT("资产端-合同"),
    ASSET_SIDE_KPI("资产端-KPI"),
    ASSET_SIDE_AIR_ACCOUNT("资产端-实际利率法"),
    ASSET_SIDE_PR_ACCOUNT("资产端-剩余本金法"),
    ASSET_SIDE_COST_DK("资金端-成本计提-间融"),
    ASSET_SIDE_COST_ZR("资金端-成本计提-直融"),
    ASSET_SIDE_COST_STAMP_DUTY("资产端-印花税"),
    FINANCE_SIDE_COST_STAMP_DUTY("资金端-印花税"),
    BUSINESS_MARGIN("项目端-保证金"),
    BUSINESS_WARRANTY("项目端-质保金"),
    FINANCE_SIDE("融资端"),
    FINANCE_SIDE_MARGIN("融资端-保证金"),
    FINANCE_SIDE_EXPENCE("融资端-还本付息"),
    ;
    ExceptionSourceENUM(String display) {
        this.display = display;
    }

    public final String display;



    private static Map<String, ExceptionSourceENUM> map;

    static {
        map = Stream.of(ExceptionSourceENUM.values()).collect(Collectors.toMap(ExceptionSourceENUM::name, c -> c, (m1,m2) -> m1));
    }

    public static ExceptionSourceENUM of (String name) {
        return map.get(name);
    }

    public static String getSourceName(String name) {
        ExceptionSourceENUM of = of(name);
        if (of == null){
            return null;
        }
        switch (of) {
            case FINANCE_SIDE_MARGIN:
            case FINANCE_SIDE_EXPENCE:
            case FINANCE_SIDE:
                return BUSINESS_FLOW.display;
            case BANK_FLOW:
            case BUSINESS_MARGIN:
            case BUSINESS_FLOW:
                return BANK_FLOW.display;
            case ASSET_SIDE_CONTRACT:
            case ASSET_SIDE_KPI:
            case ASSET_SIDE_AIR_ACCOUNT:
            case ASSET_SIDE_PR_ACCOUNT:
            case ASSET_SIDE_COST_STAMP_DUTY:
                return "资产端";
            case ASSET_SIDE_COST_DK:
            case ASSET_SIDE_COST_ZR:
            case FINANCE_SIDE_COST_STAMP_DUTY:
                return "资金端";
            default:
                return null;
        }
    }

}
