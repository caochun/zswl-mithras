package cn.zswltech.mithras.report.mapper.draft.model;

import cn.zswltech.mithras.report.mapper.base.model.CrRepayPlanBase;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;


/**
 * @description 征信报送-还款计划表
 * @author wang
 * @date 2022-10-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrRepayPlanDraft extends CrRepayPlanBase {

    /**
     * 审批流对应的business_key
     * 用于判断是否处于审批中 和 圈定一次审批所属内容
     */
    @TableField(value = "proc_business_key", updateStrategy = FieldStrategy.IGNORED)
    private Long procBusinessKey;

    /**
     * 报送状态
     * 待报送、已报送
     */
    @TableField("report_state")
    private String reportState;

    /**
     * 审批状态
     */
    @TableField("approval_status")
    private String approvalStatus;

    /**
     * 计划收取罚息
     */
    @TableField("plan_penalty_interest")
    private Long planPenaltyInterest;

    /**
     * 已收罚息
     */
    @TableField("already_penalty_interest")
    private Long alreadyPenaltyInterest;

    /**
     * 待报送展示标志，默认1，0：否， 1：是
     */
    @TableField("is_show")
    private Integer isShow;

    @Override
    public Set<String> ignoreCompareFieldNames() {
        Set<String> set = new HashSet<>();
        set.add("gracePeriod");
        set.add("procBusinessKey");
        set.add("reportState");
        set.add("approvalStatus");
        set.addAll(super.ignoreCompareFieldNames());
        return set;
    }

}
