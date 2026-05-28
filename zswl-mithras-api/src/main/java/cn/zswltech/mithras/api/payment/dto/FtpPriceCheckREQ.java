package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yangxiong
 * @date 2024/8/13/09:09
 * @description
 */
@Data
public class FtpPriceCheckREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "日期")
    @NotNull(message = "日期不能为空")
    private LocalDate recordDate;
}
