package cn.zswltech.mithras.report.enums.common;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批状态
 *
 * @author wangchuanhao
 * @date 2023/1/12 9:52 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crApprovalStatus")
public enum ApprovalStatus implements PullDown {

    UN_SUBMIT("-"),
    UNDER_APPROVAL("审批中"),
    ;

    private String display;

    @Override
    public String display() {
        return display;
    }

}
