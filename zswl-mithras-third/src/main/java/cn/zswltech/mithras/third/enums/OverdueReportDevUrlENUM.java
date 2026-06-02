package cn.zswltech.mithras.third.enums;

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
public enum OverdueReportDevUrlENUM {

    OVERDUE_REPORT_APP_TOKEN("/ierp/api/getAppToken.do", ""),
    OVERDUE_REPORT_ACCESS_TOKEN("/ierp/api/login.do", ""),
    OVERDUE_REPORT_BAT_SAVE("/ierp/kapi/v2/cico/cico_ar_ext/ovedue/batsave", ""),
    OVERDUE_REPORT_SET_BAT_SAVE("/ierp/kapi/v2/cico/cico_ar_ext/ovedue/set/batsave", ""),
    OVERDUE_REPORT_UNAUDIT("/ierp/kapi/v2/cico/cico_ar_ext/cico_ovedue_corpus_ar/ovedue/unaudit", ""),
    OVERDUE_REPORT_DEL("/ierp/kapi/v2/cico/ar/cico_ovedue_corpus_ar/ovedue/del", ""),
    OVERDUE_REPORT_REAPP("/ierp/kapi/v2/cico/ar/cico_set_corpus_ar/cico_set_reapp", ""),
    OVERDUE_REPORT_SET_DEL("/ierp/kapi/v2/cico/ar/cico_set_corpus_ar/cico_set_del", ""),
    ;
    OverdueReportDevUrlENUM(String url, String operate) {
        this.url = url;
        this.operate = operate;
    }

    public final String url;


    public final String operate;

    private static Map<String, OverdueReportDevUrlENUM> map;

    static {
        map = Stream.of(OverdueReportDevUrlENUM.values()).collect(Collectors.toMap(OverdueReportDevUrlENUM::name, c -> c));
    }

    public static OverdueReportDevUrlENUM of (String name) {
        return map.get(name);
    }

}
