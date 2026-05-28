package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yangxiong
 * @date 2024/8/12/17:02
 * @description
 */
@Data
public class FtpPriceUpdateREQ {
    
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "记录日期")
    private LocalDate recordDate;

    @ApiModelProperty(value = "指引价格")
    private Long guidePrice;

    @ApiModelProperty(value = "是否质押")
    private Long pledgePrice;

    @ApiModelProperty(value = "是否逾期")
    private Long overduePrice;

    @ApiModelProperty(value = "手工调整")
    private Long handAdjustment;

    @ApiModelProperty(value = "考核价格")
    private Long assessmentPrice;

    @ApiModelProperty(value = "备注")
    private String remark;
}
