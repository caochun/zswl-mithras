package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 保单信息版本返回体
 */
@Data
@ApiModel("保单信息版本返回体")
public class PolicyInfoVersionListRSP extends CommonVersionListRSP {

    @ApiModelProperty("保单编号")
    private String policyCode;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("变更时间")
    private LocalDateTime gmtModify;

}
