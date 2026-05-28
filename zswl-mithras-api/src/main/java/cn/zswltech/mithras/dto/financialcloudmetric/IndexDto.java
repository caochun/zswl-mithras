package cn.zswltech.mithras.dto.financialcloudmetric;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/16 19:56
 */
@Data
public class IndexDto {
    private static final long serialVersionUID = 1L;
    private String oneLevelType;
    private String twoLevelType;
    private String metrics;
    private String metricsFirstType;
    private String metricsSecondType;
    private String value;
    private String unit;
    /**
     * 202304
     */
    private String month;
    private String frequency;
}
