package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后检查报告-基本信息-返回体")
public class AfterLeaseCheckReportBaseRSP extends ListBaseRSP {
    @ApiModelProperty("基本信息id")
    private Long id;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("检查形式")
    private String checkWay;

    /*//截止时间
    @ApiModelProperty("截止时间")
    private LocalDate deadLine;*/

    @ApiModelProperty("检查计划中的客户记录id")
    private Long checkPlanClientId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("行业")
    private String industry;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("主办id")
    private Long sponsorId;

    @ApiModelProperty("主办名称")
    private String sponsorName;

    @ApiModelProperty("检查时段开始 yyyy-MM-dd")
    private String checkPeriodStart;

    @ApiModelProperty("检查时段结束 yyyy-MM-dd")
    private String checkPeriodEnd;

    @ApiModelProperty("检查日期")
    private LocalDate checkTime;

    //检查填报时间
    @ApiModelProperty("检查填报时间")
    private LocalDate checkFillTime;

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

    @ApiModelProperty("下次付款日期")
    private String nextRepayDate;

    @ApiModelProperty("下次付款金额")
    private Long nextRepayAmount;

    @ApiModelProperty("协查风控经理")
    private Long riskManagerId;

    @ApiModelProperty("协查风控经理")
    private String riskManagerName;

    @ApiModelProperty("担保人ids")
    private List<Long> guaranteeIds;

    @ApiModelProperty("担保人名称")
    private List<String> guaranteeNames;

    private List<CheckReportContract> contractList;

    @Data
    public class CheckReportContract{
        private Long id;

        private String contractCode;

        @ApiModelProperty("合同金额")
        private Long applyCreditAmount;

        @ApiModelProperty("合同开始时间-期限为1的日期")
        private LocalDate startDate;

        @ApiModelProperty("合同截止时间-期项最后一期的日期")
        private LocalDate endDate;

        @ApiModelProperty("合同期限")
        private Integer leaseMonthCount;
    }

}
