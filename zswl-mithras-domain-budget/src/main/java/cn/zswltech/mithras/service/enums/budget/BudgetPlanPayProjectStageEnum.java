package cn.zswltech.mithras.service.enums.budget;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum BudgetPlanPayProjectStageEnum implements PullDown {
    YYQT("业务洽谈"),
    LX("立项"),
    JD("尽调"),
    ZXBG("撰写报告"),
    PSH("评审会"),
    DSH("董事会"),
    SLZLW("梳理租赁物"),
    LSPSYJ("落实评审意见"),
    HTSP("合同审批"),
    HTQD("合同签订"),
    FKSP("付款审批"),
    DDFK("等待放款"),
    FK("放款"),
    YSTK("营销提款"),
    ZWJZ("暂无进展");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
