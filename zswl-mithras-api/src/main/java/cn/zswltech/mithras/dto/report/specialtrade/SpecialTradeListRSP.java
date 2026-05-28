package cn.zswltech.mithras.dto.report.specialtrade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 征信报送-特定交易表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@ApiModel("征信报送-特定交易表返回值")
@Data
public class SpecialTradeListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务标识")
    private String businessKey;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * {@link cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum}
     */
    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty("借据编号")
    private String paymentApplyCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("交易类型")
    private String tradeType;

    @ApiModelProperty("交易日期")
    private LocalDate tradeDate;

    @ApiModelProperty("交易金额")
    private Long tradeAmount;

    @ApiModelProperty("交易变更月数(月)")
    private Integer changeMonthCount;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

}
