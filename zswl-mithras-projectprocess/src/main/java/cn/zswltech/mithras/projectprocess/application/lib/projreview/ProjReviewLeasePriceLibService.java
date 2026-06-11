package cn.zswltech.mithras.projectprocess.application.lib.projreview;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePriceLib;
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
