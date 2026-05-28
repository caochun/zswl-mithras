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
public class KpiProjGuessProjManageDetailRSP {


    //所属部门
    @ApiModelProperty("所属部门")
    private Long deptId;

    @ApiModelProperty("所属部门名称")
    private String deptName;

    //分配比重类型
    @ApiModelProperty("分配比重类型 KpiProjectWeightTypeEnum")
    private String divideType;

    //人员
    private Long divideTargetId;

    @ApiModelProperty("人员/部门名称")
    private String divideTargetName;

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

    @ApiModelProperty("合计")
    private Long amount;



}
