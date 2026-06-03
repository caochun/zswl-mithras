package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author yibin
 */
@Data
@Accessors(chain = true)
public class ClientListParam {

    private String clientCode;
    private String clientName;
    private String clientType;
    private String industryType;
    private LocalDateTime createFrom;
    private LocalDateTime createTo;
    private LocalDateTime updateFrom;
    private LocalDateTime updateTo;
    private String clientStatus;
    private String processStatus;
    private Long createById;
    private Long createByDeptId;
    private Boolean effected;
    private Boolean released;

    //可查看的
    private Long belongDeptId;
    private Long belongSponsorId;

    // 客户全周期新增
    private Set<Long> targetClientIds;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String enterpriseNature;
    private String riskControlIndustryClassify;

    private Long projManagerId;
    private Boolean isGroup;
    private List<Integer> levels;

    // 客户列表输入名称查询时的特殊逻辑条件
    private List<Long> queryConditionDeptIds;
    private Long queryConditionUserId;
}
