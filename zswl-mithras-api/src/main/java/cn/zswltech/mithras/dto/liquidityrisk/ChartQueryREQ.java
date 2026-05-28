package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 流动性风险-图表-折线图 柱状图查询-请求体
 * @author: jackerhe
 * @date: 2023/5/16 10:03 上午
 **/
@Data
@ApiModel("流动性风险-图表-折线图 柱状图查询-请求体")
public class ChartQueryREQ extends PageReq {

    @ApiModelProperty("时间范围-从")
    @NotNull(message = "查询开始时间不能为空")
    private LocalDateTime timeFrom;

    @ApiModelProperty("时间范围-到")
    @NotNull(message = "查询结束时间不能为空")
    private LocalDateTime timeTo;

    //预估逾期率
    @ApiModelProperty("预估逾期率")
    private Float estimatedOverdueRate;

}
