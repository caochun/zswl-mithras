package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.service.others.MithrasException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/11/14
 * @description
 */
@Getter
@AllArgsConstructor
public enum AfterLeaseCheckReportTemplateVersionEnum {
    PUBLIC_V1(AfterLeaseCheckReportTypeEnum.PUBLIC.name(), "V1", "租后-租后检查报告", "租后管理_租后检查报告_公用事业类.xlsx"),
    PUBLIC_V2(AfterLeaseCheckReportTypeEnum.PUBLIC.name(), "V2", "租后-租后检查报告", "租后管理_租后检查报告_公用事业类.xlsx"),
    PUBLIC_V3(AfterLeaseCheckReportTypeEnum.PUBLIC.name(), "V3", "租后-租后检查报告", "租后管理_租后检查报告_公用事业类.xlsx"),

    NON_PUBLIC_V1(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), "V1", "租后-租后检查报告", "租后管理_租后检查报告_产业类.docx"),
    NON_PUBLIC_V2(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), "V2", "租后-租后检查报告", "租后管理_租后检查报告_产业类_V2.docx"),
    NON_PUBLIC_V3(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), "V3", "租后-租后检查报告", "租后管理_租后检查报告_产业类_V2.docx"),

    LOW_RISK_V1(AfterLeaseCheckReportTypeEnum.LOW_RISK.name(), "V1", "租后-租后检查报告", "租后管理_租后检查报告_低风险业务类.docx"),
    LOW_RISK_V3(AfterLeaseCheckReportTypeEnum.LOW_RISK.name(), "V3", "租后-租后检查报告", "租后管理_租后检查报告_低风险业务类.docx"),

    BUS_V1(AfterLeaseCheckReportTypeEnum.BUS.name(), "V1", "租后-租后检查报告", "租后管理_租后检查报告_公交类_V1.docx"),
    BUS_V2(AfterLeaseCheckReportTypeEnum.BUS.name(), "V2", "租后-租后检查报告", "租后管理_租后检查报告_公交类_V2.docx"),
    BUS_V3(AfterLeaseCheckReportTypeEnum.BUS.name(), "V3", "租后-租后检查报告", "租后管理_租后检查报告_公交类_V3.docx"),

    STATE_OWNED_ASSET_V1(AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(), "V1", "租后-租后检查报告", "租后管理_租后检查报告_国有资产类_V1.docx"),
    STATE_OWNED_ASSET_V2(AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(), "V2", "租后-租后检查报告", "租后管理_租后检查报告_国有资产类_V2.docx"),
    STATE_OWNED_ASSET_V3(AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(), "V3", "租后-租后检查报告", "租后管理_租后检查报告_国有资产类_V3.docx");


    private final String reportType;
    private final String version;
    private final String templateType;
    private final String templateFileName;

    public static AfterLeaseCheckReportTemplateVersionEnum findByTypeVersion(String reportType, String version) {
        for (AfterLeaseCheckReportTemplateVersionEnum item : values()) {
            if (Objects.equals(reportType, item.reportType) && Objects.equals(version, item.version)) {
                return item;
            }
        }
        return null;
    }

    public static String getLatestVersion(String reportType) {
        if (Objects.equals(reportType, AfterLeaseCheckReportTypeEnum.PUBLIC.name())) {
            return PUBLIC_V3.getVersion();
        } else if (Objects.equals(reportType, AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
            return NON_PUBLIC_V3.getVersion();
        } else if (Objects.equals(reportType, AfterLeaseCheckReportTypeEnum.LOW_RISK.name())) {
            return LOW_RISK_V3.getVersion();
        } else if (Objects.equals(reportType, AfterLeaseCheckReportTypeEnum.BUS.name())) {
            return BUS_V3.getVersion();
        } else if (Objects.equals(reportType, AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name())) {
            return STATE_OWNED_ASSET_V3.getVersion();
        } else {
            throw new MithrasException("未定义的检查报告类型");
        }
    }
}
