package cn.zswltech.mithras.creditreport.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public enum CreditReportBusinessModule {
    PROJ_ESTABLISH(Collections.emptyList()),
    PROJ_REVIEW(Collections.emptyList()),
    PAYMENT(Collections.emptyList()),
    GROUP_CREDIT_ESTABLISH(Collections.emptyList()),
    GROUP_CREDIT_REVIEW(Collections.emptyList()),
    CREDIT_REPORT_SELECT(Collections.singletonList("CreditReportSelectFlow"));

    public static final String CREDIT_REPORT_SELECT_FLOW_MODEL_KEY = "CreditReportSelectFlow";

    private final List<String> modelKeyList;

    public static String getModelDisplay(String modelKey) {
        if (CREDIT_REPORT_SELECT.modelKeyList.contains(modelKey)) {
            return "征信报告查询";
        }
        return modelKey;
    }
}
