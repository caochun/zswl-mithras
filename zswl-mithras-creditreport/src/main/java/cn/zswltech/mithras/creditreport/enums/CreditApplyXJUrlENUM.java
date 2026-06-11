package cn.zswltech.mithras.creditreport.enums;

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
public enum CreditApplyXJUrlENUM {

    //4.1新增档案信息
    CREDIT_REPORT_QUERY_ENT_FOUR_ELE_AUTH("/sugar/webApi/bsQueryService/queryEntFourEleAuth"),
    CREDIT_REPORT_QUERY_REPORT("/sugar/webApi/bsQueryService/queryReport"),
    CREDIT_REPORT_OBTAIN_RESULT_JSON("/sugar/webApi/bsQueryService/obtainResultJSON"),
    CREDIT_REPORT_OBTAIN_RESULT_PDF("/sugar/webApi/bsQueryService/obtainResultPDF"),
    ;
    CreditApplyXJUrlENUM(String url) {
        this.url = url;
    }

    public final String url;

    private static Map<String, CreditApplyXJUrlENUM> map;

    static {
        map = Stream.of(CreditApplyXJUrlENUM.values()).collect(Collectors.toMap(CreditApplyXJUrlENUM::name, c -> c));
    }

    public static CreditApplyXJUrlENUM of (String name) {
        return map.get(name);
    }

}
