package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailProfitRSP extends AssociationDetailBaseRSP{

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("主营业务收入_本季发生额(元)")
    private BigDecimal mainBusiIncmActm;

    @ApiModelProperty("主营业务收入_本年累计(元)")
    private BigDecimal mainBusiIncmTyag;

    @ApiModelProperty("主营业务收入_去年同期(元)")
    private BigDecimal mainBusiIncmCply;

    @ApiModelProperty("主营业务成本_本季发生额(元)")
    private BigDecimal mainBusiCostActm;

    @ApiModelProperty("主营业务成本_本年累计(元)")
    private BigDecimal mainBusiCostTyag;

    @ApiModelProperty("主营业务成本_去年同期(元)")
    private BigDecimal mainBusiCostCply;

    @ApiModelProperty("主营业务税金及附加_本季发生额(元)")
    private BigDecimal mainBusiTaxAddActm;

    @ApiModelProperty("主营业务税金及附加_本年累计(元)")
    private BigDecimal mainBusiTaxAddTyag;

    @ApiModelProperty("主营业务税金及附加_去年同期(元)")
    private BigDecimal mainBusiTaxAddCply;

    @ApiModelProperty("主营业务利润_本季发生额(元)")
    private BigDecimal mainBusiProfActm;

    @ApiModelProperty("主营业务利润_本年累计(元)")
    private BigDecimal mainBusiProfTyag;

    @ApiModelProperty("主营业务利润_去年同期(元)")
    private BigDecimal mainBusiProfCply;

    @ApiModelProperty("其他业务利润_本季发生额(元)")
    private BigDecimal othBusiProfActm;

    @ApiModelProperty("其他业务利润_本年累计(元)")
    private BigDecimal othBusiProfTyag;

    @ApiModelProperty("其他业务利润_去年同期(元)")
    private BigDecimal othBusiProfCply;

    @ApiModelProperty("营业费用_本季发生额(元)")
    private BigDecimal busiFeeActm;

    @ApiModelProperty("营业费用_本年累计(元)")
    private BigDecimal busiFeeTyag;

    @ApiModelProperty("营业费用_去年同期(元)")
    private BigDecimal busiFeeCply;

    @ApiModelProperty("管理费用_本季发生额(元)")
    private BigDecimal magFeeActm;

    @ApiModelProperty("管理费用_本年累计(元)")
    private BigDecimal magFeeTyag;

    @ApiModelProperty("管理费用_去年同期(元)")
    private BigDecimal magFeeCply;

    @ApiModelProperty("财务费用_本季发生额(元)")
    private BigDecimal finFeeActm;

    @ApiModelProperty("财务费用_本年累计(元)")
    private BigDecimal finFeeTyag;

    @ApiModelProperty("财务费用_去年同期(元)")
    private BigDecimal finFeeCply;

    @ApiModelProperty("资产减值损失_本季发生额(元)")
    private BigDecimal ipoaLossActm;

    @ApiModelProperty("资产减值损失_本年累计(元)")
    private BigDecimal ipoaLossTyag;

    @ApiModelProperty("资产减值损失_去年同期(元)")
    private BigDecimal ipoaLossCply;

    @ApiModelProperty("信用减值损失_本季发生额(元)")
    private BigDecimal credDecrLossActm;

    @ApiModelProperty("信用减值损失_本年累计(元)")
    private BigDecimal credDecrLossTyag;

    @ApiModelProperty("信用减值损失_去年同期(元)")
    private BigDecimal credDecrLossCply;

    @ApiModelProperty("营业利润_本季发生额(元)")
    private BigDecimal busiProfActm;

    @ApiModelProperty("营业利润_本年累计(元)")
    private BigDecimal busiProfTyag;

    @ApiModelProperty("营业利润_去年同期(元)")
    private BigDecimal busiProfCply;

    @ApiModelProperty("投资收益_本季发生额(元)")
    private BigDecimal ivsmPayfActm;

    @ApiModelProperty("投资收益_本年累计(元)")
    private BigDecimal ivsmPayfTyag;

    @ApiModelProperty("投资收益_去年同期(元)")
    private BigDecimal ivsmPayfCply;

    @ApiModelProperty("营业外收入_本季发生额(元)")
    private BigDecimal noprIncmActm;

    @ApiModelProperty("营业外收入_本年累计(元)")
    private BigDecimal noprIncmTyag;

    @ApiModelProperty("营业外收入_去年同期(元)")
    private BigDecimal noprIncmCply;

    @ApiModelProperty("营业外支出_本季发生额(元)")
    private BigDecimal noprPayActm;

    @ApiModelProperty("营业外支出_本年累计(元)")
    private BigDecimal noprPayTyag;

    @ApiModelProperty("营业外支出_去年同期(元)")
    private BigDecimal noprPayCply;

    @ApiModelProperty("利润总额_本季发生额(元)")
    private BigDecimal profGamtActm;

    @ApiModelProperty("利润总额_本年累计(元)")
    private BigDecimal profGamtTyag;

    @ApiModelProperty("利润总额_去年同期(元)")
    private BigDecimal profGamtCply;

    @ApiModelProperty("所得税费用_本季发生额(元)")
    private BigDecimal inctFeeActm;

    @ApiModelProperty("所得税费用_本年累计(元)")
    private BigDecimal inctFeeTyag;

    @ApiModelProperty("所得税费用_去年同期(元)")
    private BigDecimal inctFeeCply;

    @ApiModelProperty("净利润_本季发生额(元)")
    private BigDecimal netProfActm;

    @ApiModelProperty("净利润_本年累计(元)")
    private BigDecimal netProfTyag;

    @ApiModelProperty("净利润_去年同期(元)")
    private BigDecimal netProfCply;
}
