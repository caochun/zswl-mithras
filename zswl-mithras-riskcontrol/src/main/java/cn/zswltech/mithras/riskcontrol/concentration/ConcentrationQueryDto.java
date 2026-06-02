package cn.zswltech.mithras.riskcontrol.concentration;

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
