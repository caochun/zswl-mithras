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
public class KpiProjGuessContractIndexRSP {
    @ApiModelProperty("id")
    private Long id;

    //合同id
    @ApiModelProperty("合同id")
    private String contractId;

    //合同编号
    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("借据ID")
    private Long receiptId;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    //项目名称
    @ApiModelProperty("项目名称")
    private String projName;

    //项目来源
    @ApiModelProperty("项目来源")
    private String projSource;

    //项目类型
    @ApiModelProperty("项目类型")
    private String projClassify;

    //投放日
    @ApiModelProperty("投放日")
    private String contractStartDate;

    //所属部门id
    @ApiModelProperty("所属部门id")
    private Long belongDeptId;

    //所属部门名称
    @ApiModelProperty("所属部门名称")
    private String belongDeptName;

    //主办id
    @ApiModelProperty("所属主办id")
    private Long sponsorUserId;

    //主办名称
    @ApiModelProperty("所属主办名称")
    private String sponsorUserName;

    //利润-当期值
    @ApiModelProperty("项目利润-当期值")
    private Long profitCurrent;

    //利润-累计值
    @ApiModelProperty("项目利润-累计值")
    private Long profitTotal;

    //利润-调整值
    @ApiModelProperty("项目利润-累计值")
    private Long profitAdjust;

    //奖金-当期值
    @ApiModelProperty("奖金-当期值")
    private Long bonusCurrent;

    @ApiModelProperty("奖金-调整值")
    private Long bonusAdjust;

    //奖金-累计值
    @ApiModelProperty("奖金-累计值")
    private Long bonusTotal;

    @ApiModelProperty("投放-当期值")
    private Long paymentCurrent;

    @ApiModelProperty("投放-累计值")
    private Long paymentTotal;

    //核算月份
    @ApiModelProperty("核算月份")
    private String calculateDate;

    //核算年份
    private Integer calculateDateYear;

    //核算月份
    private Integer calculateDateMonth;

}
