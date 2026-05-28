package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-质押明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-质押明细编辑-请求体")
public class FundDirectFinancingPledgeInfoModifyREQ {

    @ApiModelProperty("质押id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;

    /**
     * 业务部门id
     */
    @ApiModelProperty("业务部门id")
    @NotNull(message = "业务部门id不能为空")
    private Long bizDeptId;
    /**
     * 项目评审id
     */
    @ApiModelProperty("项目评审id")
    @NotNull(message = "项目评审id不能为空")
    private Long projReviewId;
    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    @NotNull(message = "项目名称不能为空")
    private String projName;
    /**
     * 合同id
     */
    @ApiModelProperty("合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;
    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    @NotNull(message = "合同编号不能为空")
    private String contractCode;
    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    @NotNull(message = "业务类型不能为空")
    private String bizType;
    /**
     * 合同金额
     */
    @ApiModelProperty("合同金额")
    @NotNull(message = "合同金额不能为空")
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

    @NotBlank(message = "银行账号不能为空")
    @ApiModelProperty("银行账号")
    private String accountNumber;

    @NotBlank(message = "开户银行不能为空")
    @ApiModelProperty("开户银行")
    private String accountBank;

    @ApiModelProperty("是否质押")
    private Boolean isPledge;

    @ApiModelProperty("是否监管")
    private Boolean isSupervise;

}
