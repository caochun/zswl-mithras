package cn.zswltech.mithras.projectprocess.service.lib.projreview;

import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:39 上午
 **/
public interface ProjReviewBaseInfoLibService extends IService<ProjReviewBaseInfoLib> {

    ProjReviewBaseInfo getOldEdition(Long projId);

    ProjReviewBaseInfoDetailRSP detail(Long id, String version);

    ProjReviewBaseInfoLib getByOriginIdAndVersion(Long originId, String version);

    List<ProjReviewBaseInfoLib> listNewestReviews();

    ProjReviewBaseInfoLib getEffectLatestOne(Long originId);
}
