package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/20 10:34
 */
@Data
@ApiModel("财报信息返回体")
public class FRIRsp {
    @ApiModelProperty("财报时间")
    private LocalDate friDate;
    @ApiModelProperty("财报导入时间")
    private LocalDateTime importDate;
}
