package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CollectionFlowCenterBusinessPaymentSettleDetailRSP {

    @ApiModelProperty("现金流项目")
    @NotNull(message = "付款ID不能为空")
    private Long id;

    /**
     * 付款方式
     */
    @ApiModelProperty("付款方式")
    @NotNull(message = "付款方式不能为空")
    private String paymentMethod;

    /**
     * 实付金额
     */
    @ApiModelProperty("实付金额")
    @NotNull(message = "实付金额不能为空")
    private Long paidInAmount;

    @ApiModelProperty("实付日期")
    private LocalDate paidInDate;

    @ApiModelProperty(value = "票据code")
    @NotNull(message = "票据code不能为空")
    private String billCode;

    /**
     * 票据金额
     */
    @ApiModelProperty(value = "票据金额")
    @NotNull(message = "票据金额不能为空")
    private Long billAmount;

    /**
     * 票据到期日期
     */
    @ApiModelProperty(value = "票据到期日期")
    @NotNull(message = "票据到期日期不能为空")
    private LocalDate billExpireDate;

    @ApiModelProperty("核销方式")
    private String writeOffType;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人id")
    private Long updateBy;

    @ApiModelProperty("创建人名称")
    private String updateByName;

    @ApiModelProperty("交易明细编号")
    private String bankDetailNo;

    @ApiModelProperty(value = "租金现金流编号 抵扣现金流编号")
    private String rentCollectionCode;
    @ApiModelProperty("合同编号")
    private String contractCode;
}
