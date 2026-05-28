package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-详情-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessPeopleDetailRSP extends PageReq {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("kpiProjGuessId")
    private Long kpiProjGuessId;

    //分配类型
    @ApiModelProperty("分配类型")
    private String divideType;

    //分配类型名称
    @ApiModelProperty("分配类型名称")
    private String divideTypeName;

    //分配目标id
    @ApiModelProperty("分配目标id")
    private Long divideTarget;

    //分配目标名称
    @ApiModelProperty("分配目标名称")
    private String divideTargetName;

    //分润-当期值
    @ApiModelProperty("分润-当期值")
    private Long profitCurrent;

    //分润-累计值
    @ApiModelProperty("分润-累计值")
    private Long profitTotal;

    //奖金-当期值
    @ApiModelProperty("奖金-当期值")
    private Long bonusCurrent;

    //奖金-累计值
    @ApiModelProperty("奖金-累计值")
    private Long bonusTotal;

    //核算月份
    @ApiModelProperty("核算月份")
    private String calculateDate;

    /**
     * 分配年
     */
    @ApiModelProperty("分配年")
    private Integer divideYear;

    /**
     * 分配月
     */
    @ApiModelProperty("分配月")
    private Integer divideMonth;
}
