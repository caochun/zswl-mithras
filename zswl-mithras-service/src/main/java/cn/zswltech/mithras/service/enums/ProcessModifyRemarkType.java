package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;

/**
 * @author yibin
 */
@PullDownExt("processModifyRemarkType")
public enum ProcessModifyRemarkType implements PullDown {

    MODIFY("变更"), RECONSIDER("复议"), APPRAISAL_COMPANY_WHITELIST_OUT("评估机构出库");


    public String display;

    ProcessModifyRemarkType(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
