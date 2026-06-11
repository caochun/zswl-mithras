package cn.zswltech.mithras.kpi.enums.config;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description 税率维护参数值枚举
 */
@Getter
@AllArgsConstructor
public enum TaxRateEnum implements PullDown {
    ZZS_ZL_ZZ("ZZS", "ZL_ZZ", "增值税-直租"),
    ZZS_ZL_HZ("ZZS", "ZL_HZ", "增值税-回租"),
    ZZS_ZL_JYX("ZZS", "ZL_JYX", "增值税-经营性租赁"),
    ZZS_BL("ZZS", "BL", "增值税-保理"),
    ZZS_ZR("ZZS", "ZR", "增值税-债权转让"),
    CJS("CJS", null, "城建税"),
    JYFJS("JYFJS", null, "教育附加税"),
    DFJYFJS("DFJYFJS", null, "地方教育附加税"),
    YHS_RZZLHT("YHS", "RZZLHT", "印花税-融资租赁合同"),
    YHS_ZXHT("YHS", "ZXHT", "印花税-咨询合同"),
    YHS_CGJXSHT("YHS", "CGJXSHT", "印花税-采购及销售合同"),
    YHS_JYZLHT("YHS", "JYZLHT", "印花税-经营租赁合同"),
    XMS_ZL_ZZ("XMS", "ZL_ZZ", "项目税-直租"),
    XMS_ZL_HZ("XMS", "ZL_HZ", "项目税-回租"),
    ZXS_ZL_JYX("ZXS", "ZL_JYX", "咨询费税-经营性"),
    ;

    private final String taxType;
    private final String bizType;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
