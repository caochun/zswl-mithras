package cn.zswltech.mithras.dto.incomesharing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


@Data
public class ReceiptConditionResult {

    /**
     * 借据id
     */
    private Long receiptId;

    /**
     * 是否逾期
     */
    private String overdueType;

    /**
     * 逾期开始时间
     */
    private LocalDate overdueStartTime;

    /**
     * 起租日期
     */
    private LocalDate startRentTime;

    /**
     * 借据编号
     */
    private String receiptCode;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 项目名称
     */
    private String projName;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 合同状态
     */
    private String contractStatus;

    /**
     * 收入分摊方式
     */
    private String incomeConfirmType;


    @ApiModelProperty(value = "业务分类")
    private String businessType;

    @ApiModelProperty(value = "项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty(value = "项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty(value = "所属部门id")
    private Long belongDeptId;

    @ApiModelProperty(value = "所属部门名称")
    private String belongDeptName;

    @ApiModelProperty(value = "行业分类")
    private String riskControlIndustryClassify;
}
