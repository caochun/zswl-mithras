package cn.zswltech.mithras.service.service.third.dataminer.rsp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description
 */
@Data
public class QueryDmIndicatorRsp {
    // 年份
    private int year;
    // 区域编码
    private Long areaUniCode;
    // 区域名称
    private String areaName;
    // 区域指标编码
    private String indicatorCode;
    // 区域指标名称
    private String indicatorName;
    // 区域指标数值
    private BigDecimal indicatorValue;
    // 数据时点（时间戳）
    private long dt;
}
