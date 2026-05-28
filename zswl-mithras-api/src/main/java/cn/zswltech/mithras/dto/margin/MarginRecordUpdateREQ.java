package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-08-16
 **/
@Data
public class MarginRecordUpdateREQ {

    @NotNull
    @ApiModelProperty("保证金id")
    private Long marginId;

    @NotNull
    @ApiModelProperty("记录id")
    private Long id;

    @ApiModelProperty("核销")
    private String writeOff;

    @ApiModelProperty("复核")
    private String review;


}
