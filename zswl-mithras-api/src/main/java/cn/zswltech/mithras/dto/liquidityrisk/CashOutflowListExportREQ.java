package cn.zswltech.mithras.dto.liquidityrisk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author wwj
 */
@Data
@ApiModel("现金流流出列表-导出excel")
public class CashOutflowListExportREQ {
    @ApiModelProperty("时间范围-从")
    private LocalDateTime timeFrom;

    @ApiModelProperty("时间范围-到")
    private LocalDateTime timeTo;

    @ApiModelProperty("页数")
    private Integer page;

}
