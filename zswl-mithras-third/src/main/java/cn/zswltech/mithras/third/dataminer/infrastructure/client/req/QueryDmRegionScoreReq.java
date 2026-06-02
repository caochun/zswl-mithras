package cn.zswltech.mithras.third.dataminer.infrastructure.client.req;

import cn.zswltech.mithras.third.dataminer.infrastructure.client.DataMinerApiInfoEnum;
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
