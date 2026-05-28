package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/1/4
 * @description
 */
@Data
@ApiModel("风控管理-资产分类客户详情-请求体")
public class AssetClassifyClientDetailRSP extends ListBaseRSP {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("在租合同编号")
    private List<String> startRentContractCodes;

    @ApiModelProperty("在租合同剩余本金")
    private List<Long> startRentContractRemainingPrincipal;

    @ApiModelProperty("存量风险敞口")
    private Long stockRiskExposure;

    @ApiModelProperty("资产余额（亿元）")
    private Long assetBalance;

    @ApiModelProperty("还款情况")
    private String repayment;

    @ApiModelProperty("逾期金额")
    private Long overdueAmount;

    @ApiModelProperty("逾期天数")
    private Integer overdueDays;

    @ApiModelProperty("初分分类结果")
    private String initClassifyResult;

    /**
     * 本次分类结果
     */
    @ApiModelProperty("本次分类结果")
    private String classifyResult;

    @ApiModelProperty("1.调整 0不调整")
    private int suggestFlag;

    /**
     * 建议分类结果
     */
    @ApiModelProperty("建议分类结果")
    private String suggestResult;

    /**
     * 建议原因
     */
    @ApiModelProperty("建议原因")
    private String suggestReason;

    /**
     * 备注
     **/
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("租后检查报告名称")
    private String checkName;

    @ApiModelProperty("租后检查报告名称id")
    private Long checkId;

    /**
     * 剩余租期
     */
    @ApiModelProperty("剩余租期")
    private Integer remainingTerm;

    @ApiModelProperty("合同到期日")
    private LocalDate contractExpirationDate;

    /**
     * 客户归属部门id
     */
    @ApiModelProperty("客户归属部门id")
    private Long belongDeptId;

    /**
     * 客户归属主办id
     */
    @ApiModelProperty("客户归属主办id")
    private Long belongSponsorId;

    /**
     *节点所处状态
     **/
    @ApiModelProperty("节点所处状态")
    private String nodeStatue;

    @ApiModelProperty("复核状态")
    private String reviewStatus;

    @ApiModelProperty("拨备计提比例")
    private Long awardRatio;

    @ApiModelProperty("合同维度")
    private List<AssetClassifyClientWithdrawalRatioListRsp> withdrawalRatioRsps;
}
