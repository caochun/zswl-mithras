package cn.zswltech.mithras.application.orchestration.adapter.rating;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.rating.application.RatingProjectReviewContextPort;
import cn.zswltech.mithras.rating.application.RatingProjectReviewSnapshot;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RatingProjectReviewContextPortAdapter implements RatingProjectReviewContextPort {

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    @Override
    public RatingProjectReviewSnapshot getById(Long projReviewId) {
        ProjReviewBaseInfo projectReview = projReviewBaseInfoMapper.selectById(projReviewId);
        if (projectReview == null) {
            return null;
        }
        return BeanUtil.copyProperties(projectReview, RatingProjectReviewSnapshot.class);
    }
}
