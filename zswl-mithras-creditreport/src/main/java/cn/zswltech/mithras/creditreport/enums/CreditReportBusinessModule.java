package cn.zswltech.mithras.creditreport.enums;

import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
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
    CREDIT_REPORT_SELECT(Collections.singletonList(ProcessModelTypeEnum.CreditReportSelectFlow.name()));

    private final List<String> modelKeyList;
}
