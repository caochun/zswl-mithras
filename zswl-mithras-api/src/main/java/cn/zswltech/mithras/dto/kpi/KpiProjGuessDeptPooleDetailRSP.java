package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractIndexRSP
 * @Description 项目绩效测算表-合同维度-返回体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDeptPooleDetailRSP {

    private Long deptId;

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

    //分润-当期值
    @ApiModelProperty("利润-部门池")
    private Long profitTotal;

    //奖金-当期值
    @ApiModelProperty("奖金-部门池")
    private Long bonusTotal;

    //奖金-累计值
    @ApiModelProperty("投放奖金-部门池")
    private Long paymentTotal;

    //奖金-累计值
    @ApiModelProperty("投放额-部门池")
    private Long paymentAwardTotal;

    //奖金-累计值
    @ApiModelProperty("合计奖金-部门池")
    private Long amount;

}
