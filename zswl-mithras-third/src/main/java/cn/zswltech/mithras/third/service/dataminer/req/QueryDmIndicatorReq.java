package cn.zswltech.mithras.third.service.dataminer.req;

import cn.zswltech.mithras.third.service.dataminer.DataMinerApiInfoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryDmIndicatorReq extends DataMinerBasicReq {
    // 年份
    private int year;
    // 区域编码
    private Long areaUniCode;

    @Override
    public DataMinerApiInfoEnum dataMinerApiInfo() {
        return DataMinerApiInfoEnum.QUERY_DM_INDICATOR;
    }
}
