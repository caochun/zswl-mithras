package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@Data
@ApiModel("租后检查报告-基本信息-请求体")
public class AfterLeaseCheckReportBaseREQ {

    @ApiModelProperty("基本信息id")
    private Long id;

    @NotNull(message = "检查计划客户id不能为空")
    @ApiModelProperty("检查计划中的客户记录id")
    private Long checkPlanClientId;

    @ApiModelProperty("检查时段开始 yyyy-MM-dd")
    private String checkPeriodStart;

    @ApiModelProperty("检查时段结束 yyyy-MM-dd")
    private String checkPeriodEnd;

    @ApiModelProperty("检查日期")
    private LocalDate checkTime;

    @ApiModelProperty("主要受访人员")
    private String mainPerson;

    @ApiModelProperty("职务")
    private String mainPersonJob;

    @ApiModelProperty("联系方式")
    private String mainPersonContactWay;

    @ApiModelProperty("合同金额")
    private Long contractAmount;

    @ApiModelProperty("风险敞口")
    private Long riskExposure;

    @ApiModelProperty("剩余本金")
    private Long remainingPrincipal;

    @ApiModelProperty("到期日 yyyy-MM-dd")
    private String deadline;

    @ApiModelProperty("下次还款日")
    private String nextRepayDate;

    @ApiModelProperty("下次还款金额")
    private Long nextRepayAmount;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("行业")
    private String industry;

    //检查填报时间
    @ApiModelProperty("检查填报时间")
    private LocalDate checkFillTime;

    private Boolean isNotCheck;
}
