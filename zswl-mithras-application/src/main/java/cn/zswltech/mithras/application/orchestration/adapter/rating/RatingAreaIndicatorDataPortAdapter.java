package cn.zswltech.mithras.application.orchestration.adapter.rating;

import cn.zswltech.mithras.rating.service.port.RatingAreaIndicatorDataPort;
import cn.zswltech.mithras.rating.service.port.model.RatingAreaIndicatorData;
import cn.zswltech.mithras.rating.service.port.model.RatingRegionScoreData;
import cn.zswltech.mithras.third.dataminer.client.DataMinerClient;
import cn.zswltech.mithras.third.dataminer.client.req.QueryDmIndicatorReq;
import cn.zswltech.mithras.third.dataminer.client.req.QueryDmRegionScoreReq;
import cn.zswltech.mithras.third.dataminer.client.resp.DataMinerRsp;
import cn.zswltech.mithras.third.dataminer.client.resp.QueryDmIndicatorRsp;
import cn.zswltech.mithras.third.dataminer.client.resp.QueryDmRegionScoreRsp;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RatingAreaIndicatorDataPortAdapter implements RatingAreaIndicatorDataPort {

    @Resource
    private DataMinerClient dataMinerClient;

    @Override
    public List<RatingAreaIndicatorData> queryAreaIndicators(Long areaUniCode, int year) {
        QueryDmIndicatorReq indicatorReq = new QueryDmIndicatorReq();
        indicatorReq.setYear(year);
        indicatorReq.setAreaUniCode(areaUniCode);
        DataMinerRsp<QueryDmIndicatorRsp> result = dataMinerClient.doRequest(indicatorReq, QueryDmIndicatorRsp.class);
        if (result.getDataList() == null) {
            return Collections.emptyList();
        }
        return result.getDataList().stream()
                .map(this::toAreaIndicatorData)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingRegionScoreData> queryRegionScores(Long areaUniCode) {
        QueryDmRegionScoreReq scoreReq = new QueryDmRegionScoreReq();
        scoreReq.setAreaUniCode(areaUniCode);
        DataMinerRsp<QueryDmRegionScoreRsp> result = dataMinerClient.doRequest(scoreReq, QueryDmRegionScoreRsp.class);
        if (result.getDataList() == null) {
            return Collections.emptyList();
        }
        return result.getDataList().stream()
                .map(this::toRegionScoreData)
                .collect(Collectors.toList());
    }

    private RatingAreaIndicatorData toAreaIndicatorData(QueryDmIndicatorRsp source) {
        RatingAreaIndicatorData data = new RatingAreaIndicatorData();
        data.setYear(source.getYear());
        data.setIndicatorCode(source.getIndicatorCode());
        data.setIndicatorValue(source.getIndicatorValue());
        return data;
    }

    private RatingRegionScoreData toRegionScoreData(QueryDmRegionScoreRsp source) {
        RatingRegionScoreData data = new RatingRegionScoreData();
        data.setFinalScore(source.getFinalScore());
        return data;
    }
}
