package cn.zswltech.mithras.dto.process.prepare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author luyi
 */
@Data
public class FinancingRepayActualProcessDetailListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty(value = "融资id")
    private Long financingId;

    /**
     * 融资编号
     */
    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    /**
     * 融资机构
     */
    @ApiModelProperty(value = "融资机构")
    private String organizationName;

    /**
     * 融资类型：直融/间融
     */
    @ApiModelProperty(value = "融资类型：直融/间融")
    private String financingType;


    /**
     * 还款实际表id
     */
    @ApiModelProperty(value = "还款实际表id")
    private Long financingRepayActualId;

    /**
     * 融资金额
     */
    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    /**
     * 应还日期
     */
    @ApiModelProperty(value = "应还日期")
    private LocalDate repayDate;

    /**
     * 应还总额
     */
    @ApiModelProperty(value = "应还总额")
    private Long repayAmount;

    /**
     * 应还本金
     */
    @ApiModelProperty(value = "应还本金")
    private Long principleAmount;

    /**
     * 应还利息
     */
    @ApiModelProperty(value = "应还利息")
    private Long interestAmount;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String accountNumber;

    /**
     * 支行名称
     */
    @ApiModelProperty(value = "支行名称")
    private String accountBank;

    /**
     * 账户类别
     */
    @ApiModelProperty(value = "账户类别")
    private String fundFinancingAccountType;


    /**
     * 预备表id
     */
    @ApiModelProperty(value = "预备表id")
    private Long prepareId;

    /**
     * 确认状态是否已确认
     */
    @ApiModelProperty(value = "确认状态是否已确认")
    private Integer isConfirmed;

    /**
     * 还款状态是否已确认
     */
    @ApiModelProperty(value = "还款状态是否已确认")
    private Integer isPaid;



}

