package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/9/2
 * @description
 */
@Data
public class AppraisalCompanyWhitelistPageRSP {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String uscCode;

    @ApiModelProperty(value = "状态")
    private String recordStatus;

    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    @ApiModelProperty(value = "生效日期")
    private LocalDate recordEffectDate;

    @ApiModelProperty(value = "到期日")
    private LocalDate recordExpireDate;

    @ApiModelProperty(value = "部门id")
    private Long deptId;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "创建人id")
    private Long createBy;

    @ApiModelProperty(value = "创建人名称")
    private String createByName;

    @ApiModelProperty(value = "最后操作人id")
    private Long lastOperatorId;

    @ApiModelProperty(value = "最后操作人名称")
    private String lastOperatorName;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "评估机构池中的id")
    private Long companyId;
}
