package cn.zswltech.mithras.service.service.third.dataminer.rsp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/3/18
 * @description
 */
@Data
public class QueryDmRegionScoreRsp {
    private Long areaUniCode;
    private String province;
    private String city;
    private String areaName;
    private BigDecimal finalScore;
}
