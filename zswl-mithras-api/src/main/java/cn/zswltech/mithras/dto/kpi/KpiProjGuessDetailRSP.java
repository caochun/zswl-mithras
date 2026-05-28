package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-详情-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDetailRSP {

    @ApiModelProperty("id")
    private Long id;

    //合同编号
    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("合同id")
    private String contractId;

    @ApiModelProperty("借据ID")
    private Long receiptId;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    //项目名称
    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("占比")
    private Integer divideWeight;

    //项目类型
    @ApiModelProperty("项目类别")
    private String projClassify;


    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty("业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    @ApiModelProperty("租赁类型。直租、回租、经营性租赁")
    private String leaseType;

    @ApiModelProperty("保理类型 {@link FactoringType#name()}")
    private String factoringType;


    @ApiModelProperty("转让类型 {@link ZrType#name()}")
    private String zrType;


    //项目来源
    @ApiModelProperty("项目来源")
    private String projSource;

    //合同开始日期（投放日期）
    @ApiModelProperty("合同开始日期（投放日期）")
    private String contractStartDate;

    //合同终止时间
    @ApiModelProperty("合同终止时间")
    private String contractEndDate;

    //所属部门id
    @ApiModelProperty("所属部门id")
    private Long belongDeptId;

    //所属部门名称
    @ApiModelProperty("所属部门名称")
    private String belongDeptName;

    /**
     * 提奖比例
     */
    @ApiModelProperty("提奖比例")
    private Long awardRatio;

    //利润-当期值
    @ApiModelProperty("利润-当期值")
    private Long profitCurrent;

    //利润-累计值
    @ApiModelProperty("利润-累计值")
    private Long profitTotal;

    @ApiModelProperty("项目本年累计投放金额")
    private Long projPaymentYearAmount;

    @ApiModelProperty("合同本月累计投放金额")
    private Long contractPaymentMonthAmount;

    //核算月份
    @ApiModelProperty("核算月份")
    private String calculateDate;

    //核算年份
    private Integer calculateDateYear;

    //核算月份
    private Integer calculateDateMonth;


    @ApiModelProperty("分润比")
    private List<KpiProjectGuessWeightInfo> weightInfoList;
}
