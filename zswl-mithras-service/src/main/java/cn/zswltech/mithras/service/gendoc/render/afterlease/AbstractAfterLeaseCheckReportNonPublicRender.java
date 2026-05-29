package cn.zswltech.mithras.service.gendoc.render.afterlease;

import cn.zswltech.mithras.common.constant.GlobalConstants;

/**
 * @author dingqi
 * @date 2023/11/16
 * @description
 */
public abstract class AbstractAfterLeaseCheckReportNonPublicRender extends AbstractAfterLeaseCheckReportRender {
    protected String getRenderFileName() {
        return "租后检查报告_产业类" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }
}
