package cn.zswltech.mithras.projectprocess.service.bo;

import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/9/13
 * @description
 */
@Data
@AllArgsConstructor
public class ProjReviewRenderBO {
    private boolean isMultiparty;
    private ProjReviewBaseInfo projReviewBaseInfo;
    private ProjReviewMaterialsEnum projReviewMaterialsEnum;
}
