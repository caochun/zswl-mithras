package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @create: 2023-05-15
 **/

@Data
public class CashOutflowListReq extends PageReq {

    @ApiModelProperty("时间范围-从")
    private LocalDateTime timeFrom;

    @ApiModelProperty("时间范围-到")
    private LocalDateTime timeTo;

    //是否需要分页，ture 正常分页，false 不分页
    private Boolean needPage = true;
}
