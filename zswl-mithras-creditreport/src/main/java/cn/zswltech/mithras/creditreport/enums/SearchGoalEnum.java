package cn.zswltech.mithras.creditreport.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 * 01    贷前(保前)审查
 * 02	贷中操作
 * 03	贷后(在保)管理
 * 04	其他原因
 * 05	关联查询
 * 17	额度审批
 * 18	担保审查
 */
@AllArgsConstructor
@Getter
public enum SearchGoalEnum implements PullDown {
    PRE_INSURANCE_REVIEW("贷前（保前）审查", "preInsuranceReview", "01"),
    POST_LOAN_DURING_GUARANTEE("贷后（在保）管理", "postLoanDuringGuarantee", "03"),
    LOAN_PROCESSING("贷中操作", "loanProcessing", "02"),
    CORRELATED_QUERY("关联查询", "correlatedQuery", "05"),
    ;

    private final String display;
    /**
     * 查询目的
     */
    private final String name;

    private final String xjCode;

    @Override
    public String display() {
        return this.display;
    }

    public static SearchGoalEnum finaByName(String name) {
        for (SearchGoalEnum item : values()) {
            if (Objects.equals(item.getName(), name)) {
                return item;
            }
        }
        return null;
    }
}
