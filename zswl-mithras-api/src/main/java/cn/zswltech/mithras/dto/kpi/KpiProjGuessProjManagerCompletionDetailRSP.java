package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
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
public class KpiProjGuessProjManagerCompletionDetailRSP extends PageReq {

    private Long deptId;

    @ApiModelProperty("考核部门")
    private String deptName;

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

    @ApiModelProperty("本年存量产业类")
    private KpiProjGuessProjManagerCompletionDetailRSP.Body industryStock;

    @ApiModelProperty("本年存量公共事业类")
    private KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock;

    @ApiModelProperty("本年存量利润合计")
    private Long bonusAmountStock;

    @ApiModelProperty("本年存量投放合计")
    private Long paymentAmountStock;

    @ApiModelProperty("本年新增产业类")
    private KpiProjGuessProjManagerCompletionDetailRSP.Body industryAdd;

    @ApiModelProperty("本年新增公共事业类")
    private KpiProjGuessProjManagerCompletionDetailRSP.Body publicAdd;

    @ApiModelProperty("本年新增利润合计")
    private Long bonusAmountAdd;

    @ApiModelProperty("本年新增投放合计")
    private Long paymentAmountAdd;

    @Data
    public static class Body {
        //奖金-当期值
        @ApiModelProperty("主办奖金-当期值")
        private Long bonusCurrent;

        //奖金-累计值
        @ApiModelProperty("主办投放-当期值")
        private Long paymentCurrent;

        //奖金-当期值
        @ApiModelProperty("协办奖金-当期值")
        private Long bonusCurrentDeputy;

        //奖金-累计值
        @ApiModelProperty("主办投放-当期值")
        private Long paymentCurrentDeputy;

        //奖金-当期值
        @ApiModelProperty("推荐人奖金-当期值")
        private Long bonusCurrentReference;

        //奖金-累计值
        @ApiModelProperty("推荐人投放-当期值")
        private Long paymentCurrentReference;

    }

}
