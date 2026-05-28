package cn.zswltech.mithras.service.service.lib.projreview;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/8/2 10:34 上午
 **/
public interface ProjReviewLeasePriceLibService extends IService<ProjReviewLeasePriceLib> {

    ProjReviewLeasePriceRSP getOldEdition(Long projId);

    ProjReviewLeasePriceLib getByProjReviewIdAndVersion(Long projReviewId, String version);

    List<ProjReviewLeasePriceLib> listNewestByProjReviewIds(Set<Long> projReviewIds);
}
