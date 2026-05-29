package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 征信报告查询原因
 *
 * @date 2023/2/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum CreditReportQueryReasonEnum implements PullDown {

    PRE_REVIEW("贷前(保前)审查"),
    LOAN_PROCESS("贷中操作"),
    AFTER_MANAGEMENT("贷后(在保)管理"),
    RELATION_QUERY("关联查询"),
    APPROVAL_QUOTA("额度审批"),
    GUARANTEE_REVIEW("担保审查"),
    OTHER("其他原因"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportQueryReasonEnum finaByName(String name) {
        for (CreditReportQueryReasonEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
