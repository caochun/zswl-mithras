package cn.zswltech.mithras.service.service.lib.projreview;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/8/2 10:34 上午
 **/
public interface ProjReviewFactoringPriceLibService extends IService<ProjReviewFactoringPriceLib> {

    ProjReviewFactoringPriceRSP getOldEdition(Long projId);

    ProjReviewFactoringPriceLib getByProjReviewIdAndVersion(Long projReviewId, String version);

    List<ProjReviewFactoringPriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds);
}
