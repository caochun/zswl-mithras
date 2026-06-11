package cn.zswltech.mithras.third.overduereport.enums;

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
public enum OverdueReportUrlENUM {

    OVERDUE_REPORT_APP_TOKEN("/fin-http/fss/ierp/getAppToken", "com.cncico.esb.fin.fss.getAppToken.ierp"),
    OVERDUE_REPORT_ACCESS_TOKEN("/fin-http/fss/ierp/login", "com.cncico.esb.fin.fss.login.ierp"),
    OVERDUE_REPORT_BAT_SAVE("/fin-http/fss/ovedue/batsave", "com.cncico.esb.fin.fss.batsave.ovedue"),
    OVERDUE_REPORT_SET_BAT_SAVE("/fin-http/fss/set/batsave", "com.cncico.esb.fin.fss.set.batsave"),
    OVERDUE_REPORT_UNAUDIT("/fin-http/fss/ovedue/unaudit", "com.cncico.esb.fin.fss.unaudit.ovedue"),
    OVERDUE_REPORT_DEL("/fin-http/fss/ovedue/del", "com.cncico.esb.fin.fss.del.ovedue"),
    OVERDUE_REPORT_REAPP("/fin-http/fss/cico_set_corpus_ar/cico_set_reapp", "com.cncico.esb.fin.fss.cico_set_reapp.cico_set_corpus_ar"),
    OVERDUE_REPORT_SET_DEL("/fin-http/fss/cico_set_corpus_ar/cico_set_del", "com.cncico.esb.fin.fss.cico_set_del.cico_set_corpus_ar"),
    ;
    OverdueReportUrlENUM(String url, String operate) {
        this.url = url;
        this.operate = operate;
    }

    public final String url;


    public final String operate;

    private static Map<String, OverdueReportUrlENUM> map;

    static {
        map = Stream.of(OverdueReportUrlENUM.values()).collect(Collectors.toMap(OverdueReportUrlENUM::name, c -> c));
    }

    public static OverdueReportUrlENUM of (String name) {
        return map.get(name);
    }

}
