package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2023/1/4
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("风控管理-资产分类客户列表-返回体")
public class AssetClassifyClientListRSP extends ListBaseRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("资产五级分类id")
    private Long assetClassifyId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("投放金额")
    private Long amount;

    @ApiModelProperty("存量风险敞口")
    private Long stockRiskExposure;

    @ApiModelProperty("资产余额（亿元）")
    private Long assetBalance;

    @ApiModelProperty("剩余租期")
    private Integer remainingTerm;

    @ApiModelProperty("定性调整")
    private Integer qualitativeAdjust;

    @ApiModelProperty("上次分类结果")
    private String lastClassifyResult;

    @ApiModelProperty("本次分类结果")
    private String classifyResult;

    @ApiModelProperty("本次初分结果")
    private String initClassifyResult;

    @ApiModelProperty("最近一个有效版本分类结果")
    private String latestVersionClassifyResult;

    @ApiModelProperty("复核状态")
    private String reviewStatus;

    @ApiModelProperty("复核通过时间")
    private LocalDateTime reviewPassTime;

    @ApiModelProperty("客户归属部门id")
    private Long belongDeptId;

    @ApiModelProperty("客户归属部门名称")
    private String belongDeptName;

    @ApiModelProperty("客户归属主办id")
    private Long belongSponsorId;

    @ApiModelProperty("客户归属主办名称")
    private String belongSponsorName;

    @ApiModelProperty("拨备计提比例")
    private Long awardRatio;

    @ApiModelProperty("建议分类")
    private String suggestResult;
}
