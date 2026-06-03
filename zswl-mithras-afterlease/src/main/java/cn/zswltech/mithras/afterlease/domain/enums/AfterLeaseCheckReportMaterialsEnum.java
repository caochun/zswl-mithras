package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/8/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum AfterLeaseCheckReportMaterialsEnum implements PullDown, IMaterialsTypeConvert {
    ZXYQCWBB("最新一期财务报表", 1),
    FRQYZXBG("法人企业征信报告", 2),
    ZLWZP("租赁物照片", 3),
    KHHY("与客户工作人员厂区（办公区）合影", 4),
    ZZSNSSBB("增值税纳税申报表", 5),
    RZKSYPZ_MXB_JHB("融资款使用凭证或相关银行流水、融资明细表/还款计划表", 6),
    SCBB("生产型企业生产报表", 7),
    DFPZHMX("生产型企业电费凭证或明细", 8),
    GXZL("主体资格及章程变更等更新资料", 9),
    ZRRZXBG("自然人担保人征信报告", 10),
    OTHER("其他资料", 11),
    ANALYSIS_REPORT("租后检查分析报告", 12)
    ;

    private final String display;
    private final int sort;

    @Override
    public String businessModule() {
        return "NEW_AFTER_LEASE_CHECK_REPORT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
