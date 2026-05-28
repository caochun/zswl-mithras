package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class AppCalendarDetailREQ {


    @ApiModelProperty("收款明细id")
    private Long id;

}
