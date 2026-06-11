package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 * @deprecated 需求变更，文件材料类型改动
 */
@Deprecated
@AllArgsConstructor
@Getter
public enum AfterLeaseReportNonPublicMaterialsEnum implements PullDown, IMaterialsTypeConvert {
    CWBB_FRZXBG("最新一期财务报表、法人企业征信报告", 1),
    ZZSNSSBB("增值税纳税申报表", 2),
    ZRRZXBG("自然人征信报告", 3),
    SCBB("制造型企业提供生产报表", 4),
    GXZL("主体资格及章程变更等更新资料", 5),
    ZLW_XBZL("租赁物续保保险资料",6 ),
    ZDQKZL("反映重大情况的资料", 7),
    FLSS_QYXY("法律诉讼查询信息、企业信用查询信息", 8),
    OTHER("其他", 9);

    private final String display;
    private final int sort;

    @Override
    public String businessModule() {
        return "AFTER_LEASE_CHECK_PROJECT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
