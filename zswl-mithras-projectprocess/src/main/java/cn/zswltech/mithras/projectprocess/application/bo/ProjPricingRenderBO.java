package cn.zswltech.mithras.projectprocess.application.bo;

import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjPricingRenderBO {
    private boolean isMultiparty;
    private ProjPricingBaseInfo projPricingBaseInfo;
    private ProjPricingMaterialsEnum projPricingMaterialsEnum;
}
