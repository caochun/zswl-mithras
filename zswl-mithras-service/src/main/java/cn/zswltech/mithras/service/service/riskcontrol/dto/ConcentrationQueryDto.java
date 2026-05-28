package cn.zswltech.mithras.service.service.riskcontrol.dto;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/2 16:02
 */
@Data
public class ConcentrationQueryDto {
    private String clientName;
    private String groupName;
    private String state;
    private String dataTimePoint;
    private String riskControlIndustryClassify;
    private Integer isRelated;
    private String province;
}
