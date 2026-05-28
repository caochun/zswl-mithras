package cn.zswltech.mithras.service.gendoc.render.afterlease;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/11/16
 * @description
 */
@Slf4j
public class AfterLeaseCheckReportRenderFactory {
    public static AbstractAfterLeaseCheckReportRender getInstance(String reportType, String templateVersion) {
        AfterLeaseCheckReportTemplateVersionEnum reportTemplateVersionEnum = AfterLeaseCheckReportTemplateVersionEnum.findByTypeVersion(reportType, templateVersion);
        if (Objects.isNull(reportTemplateVersionEnum)) {
            throw new MithrasException("未定义的租后检查报告模板版本");
        }
        switch (reportTemplateVersionEnum) {
            case PUBLIC_V1: return SpringUtil.getBean(AfterLeaseCheckReportPublicV1Render.class);
            case PUBLIC_V2: return SpringUtil.getBean(AfterLeaseCheckReportPublicV2Render.class);
            case PUBLIC_V3: return SpringUtil.getBean(AfterLeaseCheckReportPublicV3Render.class);
            case NON_PUBLIC_V1: return SpringUtil.getBean(AfterLeaseCheckReportNonPublicV1Render.class);
            case NON_PUBLIC_V2: return SpringUtil.getBean(AfterLeaseCheckReportNonPublicV2Render.class);
            case NON_PUBLIC_V3: return SpringUtil.getBean(AfterLeaseCheckReportNonPublicV3Render.class);
            case LOW_RISK_V1: return SpringUtil.getBean(AfterLeaseCheckReportLowRiskV1Render.class);
            case LOW_RISK_V3: return SpringUtil.getBean(AfterLeaseCheckReportLowRiskV3Render.class);
            case BUS_V3: return SpringUtil.getBean(AfterLeaseCheckReportBusV3Render.class);
            case STATE_OWNED_ASSET_V3: return SpringUtil.getBean(AfterLeaseCheckReportStateOwnedAssetV3Render.class);
            default: throw new MithrasException("没有找到合适的租后检查报告文档生成器");
        }
    }
}
