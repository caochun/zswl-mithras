package cn.zswltech.mithras.service.service.third.dataminer.req;

import cn.zswltech.mithras.service.service.third.dataminer.DataMinerApiInfoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/3/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryDmRegionScoreReq extends DataMinerBasicReq {
    private Long areaUniCode;

    @Override
    public DataMinerApiInfoEnum dataMinerApiInfo() {
        return DataMinerApiInfoEnum.QUERY_DM_REGION_SCORE;
    }
}
