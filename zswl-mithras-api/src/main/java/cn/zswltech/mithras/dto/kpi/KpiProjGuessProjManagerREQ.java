package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessProjManagerREQ {


    /**
     * 分配年
     */
    @ApiModelProperty("分配年")
    private Integer calculateDateYear;

    /**
     * 分配月
     */
    @ApiModelProperty("分配月")
    private Integer calculateDateMonth;

}
