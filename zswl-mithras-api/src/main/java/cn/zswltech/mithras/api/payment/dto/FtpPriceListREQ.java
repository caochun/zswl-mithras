package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author yangxiong
 * @date 2024/8/12/17:06
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FtpPriceListREQ extends PageReq {

    @ApiModelProperty(value = "记录日期开始")
    private LocalDate recordDateFrom;

    @ApiModelProperty(value = "记录日期结束")
    private LocalDate recordDateTo;

    @ApiModelProperty(value = "成本是否已确认")
    private Integer costIsConfirmed;

    @ApiModelProperty(value = "借据编码")
    private String receiptCode;
}
