package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/17 15:10
 */
@ApiModel("直接融资-质押明细-详情-返回体")
@Data
public class FundDirectFinancingPledgeInfoDetailRSP {
    @ApiModelProperty("id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;

    /**
     * 业务部门id
     */
    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    /**
     * 项目评审id
     */
    @ApiModelProperty("项目评审id")
    private Long projReviewId;
    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    private String projName;
    /**
     * 合同id
     */
    @ApiModelProperty("合同id")
    private Long contractId;
    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户id")
    private Long clientId;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String clientName;

    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    private String bizType;
    /**
     * 合同金额
     */
    @ApiModelProperty("合同金额")
    private Long contractAmount;
    /**
     * 合同开始日期
     */
    @ApiModelProperty("合同开始日期")
    private LocalDate contractStartDate;
    /**
     * 合同结束日期
     */
    @ApiModelProperty("合同结束日期")
    private LocalDate contractEndDate;

    /**
     * 剩余未还本金
     */
    @ApiModelProperty("剩余未还本金")
    private Long remainingUnpaidPrincipal;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("银行账号")
    private String accountNumber;

    @ApiModelProperty("开户银行")
    private String accountBank;

    @ApiModelProperty("是否质押")
    private Boolean isPledge;

    @ApiModelProperty("是否监管")
    private Boolean isSupervise;

}
