package cn.zswltech.mithras.service.service.riskcontrol.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/24 20:35
 */
@Data
@Accessors(chain = true)
public class CorpCommerceInfoLibDto {

    private List<String> inRiskControlIndustryClassify;

    private List<String> notInRiskControlIndustryClassify;

    private Set<Long> notInClientIds;

    private Set<Long> inClientIds;

    private Integer isRelated;
}
