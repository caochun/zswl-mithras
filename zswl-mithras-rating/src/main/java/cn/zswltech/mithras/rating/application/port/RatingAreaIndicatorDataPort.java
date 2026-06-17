package cn.zswltech.mithras.rating.application.port;

import cn.zswltech.mithras.rating.application.port.model.RatingAreaIndicatorData;
import cn.zswltech.mithras.rating.application.port.model.RatingRegionScoreData;

import java.util.List;

public interface RatingAreaIndicatorDataPort {

    List<RatingAreaIndicatorData> queryAreaIndicators(Long areaUniCode, int year);

    List<RatingRegionScoreData> queryRegionScores(Long areaUniCode);
}
