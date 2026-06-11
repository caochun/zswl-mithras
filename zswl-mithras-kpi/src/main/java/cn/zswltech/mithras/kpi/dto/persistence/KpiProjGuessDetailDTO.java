package cn.zswltech.mithras.kpi.dto.persistence;

import cn.zswltech.mithras.kpi.model.KpiProjGuessDivide;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDetailDTO {

    private Long id;

    private Long projectDistributionId;

    //合同编号
    private String contractCode;

    private Long contractId;

    private Long receiptId;

    //项目名称
    private String projName;

    //项目类型
    private String projClassify;

    //项目来源
    private String projSource;

    //投放日
    private String contractStartDate;

    //所属部门id
    private Long belongDeptId;

    //提奖比例
    private Long awardRatio;

    //主办id
    private Long sponsorUserId;

    //核算年份
    private Integer calculateDateYear;

    //核算月份
    private Integer calculateDateMonth;

    //利润-当期值
    private Long profitCurrent;

    //利润-累计值
    private Long profitTotal;

    //奖金-当期值
    private Long bonusCurrent;

    //奖金-累计值
    private Long bonusTotal;

    private String version;

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

    @ApiModelProperty("项目本年累计投放金额")
    private Long projPaymentYearAmount;

    @ApiModelProperty("合同本月累计投放金额")
    private Long contractPaymentMonthAmount;

    //分配详情
    List<KpiProjGuessDivide> kpiProjGuessDivides;

}
