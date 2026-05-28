package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("保单台账-列表-请求体")
public class PolicyMaintenanceREQ {

    public final static String notOverdue = "NOT_OVERDUE";
    public final static String overdue = "OVERDUE";

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    @ApiModelProperty(value = "保险起始日")
    private LocalDate insuranceStartDateFrom;

    @ApiModelProperty(value = "保险起始日")
    private LocalDate insuranceStartDateTo;

    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDateFrom;

    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDateTo;

    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    @ApiModelProperty(value = "将到期保单/逾期保单 NOT_OVERDUE/OVERDUE")
    private String policyOverdueType;

}
