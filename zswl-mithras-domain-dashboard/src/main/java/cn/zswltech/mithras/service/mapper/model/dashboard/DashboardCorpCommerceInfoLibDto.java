package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: zhouning
 * @date: 2023/2/24 20:35
 */
@Data
@Accessors(chain = true)
public class DashboardCorpCommerceInfoLibDto {

    private List<String> inRiskControlIndustryClassify;

    private Set<Long> inClientIds;

}
