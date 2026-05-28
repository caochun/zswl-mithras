package cn.zswltech.mithras.dto.liquidityrisk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2023-05-15
 **/

@Data
public class CapitalFlowSettingDetailReq {
    @ApiModelProperty("时间范围-从")
    private LocalDateTime timeFrom;

    @ApiModelProperty("时间范围-到")
    private LocalDateTime timeTo;

    @ApiModelProperty("预估逾期率")
    private Float overdueRate;
}
