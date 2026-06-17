package cn.zswltech.mithras.rating.application.port;

import cn.zswltech.mithras.rating.application.port.model.RatingProjectReviewSnapshot;

public interface RatingProjectReviewContextPort {

    RatingProjectReviewSnapshot getById(Long projReviewId);
}
