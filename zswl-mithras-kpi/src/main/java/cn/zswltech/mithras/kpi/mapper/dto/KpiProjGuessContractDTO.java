package cn.zswltech.mithras.kpi.mapper.dto;

import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessContractDTO {

    private Long id;

    private String contractId;

    //合同编号
    private String contractCode;

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

    //利润-当期值
    private Long profitCurrent;

    //利润-累计值
    private Long profitTotal;

    //奖金-当期值
    private Long bonusCurrent;

    //奖金-累计值
    private Long bonusTotal;

    //主办id
    private Long sponsorUserId;

    //核算年份
    private Integer calculateDateYear;

    //核算月份
    private Integer calculateDateMonth;

    //@ApiModelProperty("投放-当期值")
    private Long paymentCurrent;

    //@ApiModelProperty("投放-累计值")
    private Long paymentTotal;

    private Long profitAdjust;

}
