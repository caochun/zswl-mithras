package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @ClassName AssetInflowDetailREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/5/15 4:23 下午
 * @Version 1.0
 **/
@Data
@ApiModel("流动性风险-流入明细-请求体")
public class AssetInflowDetailREQ extends PageReq {

    //近x日
   /* @ApiModelProperty("近x日")
    @NotNull(message = "查询时间不能为空")
    private Integer days;*/

    @ApiModelProperty("时间范围-从")
    @NotNull(message = "查询开始时间不能为空")
    private LocalDateTime timeFrom;

    @ApiModelProperty("时间范围-到")
    @NotNull(message = "查询结束时间不能为空")
    private LocalDateTime timeTo;

    //预估逾期率
    @ApiModelProperty("预估逾期率")
    private Float estimatedOverdueRate;

    //是否需要分页，ture 正常分页，false 不分页
    private Boolean needPage = true;

}
