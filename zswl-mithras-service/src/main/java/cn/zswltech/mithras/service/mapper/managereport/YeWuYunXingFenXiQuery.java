package cn.zswltech.mithras.service.mapper.managereport;

import cn.zswltech.mithras.service.mapper.query.PageQuery;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
@Data
public class YeWuYunXingFenXiQuery extends PageQuery {
    private List<String> processModelTypeList;
    private LocalDateTime processStartTimeFrom;
    private LocalDateTime processStartTimeTo;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private List<String> riskControlIndustryClassifyList;
    private List<Integer> processStatusList;
    private String leaseType;
    private String groupType;
}
