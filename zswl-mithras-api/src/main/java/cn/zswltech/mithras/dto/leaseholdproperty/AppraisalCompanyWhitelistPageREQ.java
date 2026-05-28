package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/9/2
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppraisalCompanyWhitelistPageREQ extends PageReq {
    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

    @ApiModelProperty(value = "状态")
    private String recordStatus;

    @ApiModelProperty(value = "到期日-开始")
    private LocalDate recordExpireDateFrom;

    @ApiModelProperty(value = "到期日-结束")
    private LocalDate recordExpireDateTo;

    @ApiModelProperty(value = "创建人id")
    private Long createBy;

    @ApiModelProperty(value = "部门id")
    private Long deptId;

    @ApiModelProperty(value = "流程状态")
    private String processStatus;
}
