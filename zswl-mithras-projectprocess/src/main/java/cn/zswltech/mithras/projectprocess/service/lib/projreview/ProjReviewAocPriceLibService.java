package cn.zswltech.mithras.projectprocess.service.lib.projreview;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:34 上午
 **/
public interface ProjReviewAocPriceLibService extends IService<ProjReviewAocPriceLib> {

    ProjReviewAocPriceRSP getOldEdition(Long projId);

    ProjReviewAocPriceLib getByProjReviewIdAndVersion(Long projReviewId, String version);

    List<ProjReviewAocPriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds);

}
