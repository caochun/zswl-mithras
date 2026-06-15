package cn.zswltech.mithras.rating.service.port;

import cn.zswltech.mithras.rating.service.port.model.RatingAreaIndicatorData;
import cn.zswltech.mithras.rating.service.port.model.RatingRegionScoreData;

import java.util.List;

public interface RatingAreaIndicatorDataPort {

    List<RatingAreaIndicatorData> queryAreaIndicators(Long areaUniCode, int year);

    List<RatingRegionScoreData> queryRegionScores(Long areaUniCode);
}
