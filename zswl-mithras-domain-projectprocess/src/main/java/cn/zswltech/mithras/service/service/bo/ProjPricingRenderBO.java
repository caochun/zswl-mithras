package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjPricingRenderBO {
    private boolean isMultiparty;
    private ProjPricingBaseInfo projPricingBaseInfo;
    private ProjPricingMaterialsEnum projPricingMaterialsEnum;
}
