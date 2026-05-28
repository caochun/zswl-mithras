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
public class AssociationDetailBalanceSheetPartialRSP extends AssociationDetailBaseRSP {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("货币资金_年初数(元)")
    private BigDecimal crcpAboy;
    
    @ApiModelProperty("交易性金融资产_年初数(元)")
    private BigDecimal trdFinlAstAboy;
    
    @ApiModelProperty("衍生金融资产_年初数(元)")
    private BigDecimal devdFinlAstAboy;
    
    @ApiModelProperty("应收票据_年初数(元)")
    private BigDecimal recvBillAboy;
    
    @ApiModelProperty("应收账款_年初数(元)")
    private BigDecimal recvAmtAboy;
    
    @ApiModelProperty("应收款项融资_年初数(元)")
    private BigDecimal recvAmtFinAboy;
    
    @ApiModelProperty("预付账款_年初数(元)")
    private BigDecimal piaAmtAboy;

    @ApiModelProperty("其他应收款总计_年初数(元)")
    private BigDecimal othRecvAmtTotAboy;
    
    @ApiModelProperty("应收股利_年初数(元)")
    private BigDecimal recvDivdAboy;

    @ApiModelProperty("应收利息_年初数(元)")
    private BigDecimal recvIntrAboy;
    
    @ApiModelProperty("其他应收款_年初数(元)")
    private BigDecimal othRecvAmtAboy;
    
    @ApiModelProperty("存货_年初数(元)")
    private BigDecimal invAboy;
    
    @ApiModelProperty("合同资产_年初数(元)")
    private BigDecimal agmtAstAboy;

    @ApiModelProperty("持有待售资产_年初数(元)")
    private BigDecimal holdSaleAstAboy;

    @ApiModelProperty("一年内到期的非流动资产_年初数(元)")
    private BigDecimal ncoyAboy;

    @ApiModelProperty("其他流动资产_年初数(元)")
    private BigDecimal othLiqdAstAboy;
    
    @ApiModelProperty("流动资产合计_年初数(元)")
    private BigDecimal liqdAstTotAboy;

    @ApiModelProperty("非流动资产_年初数(元)")
    private BigDecimal NoLiqdAstTotAboy;
    
    @ApiModelProperty("债权投资_年初数(元)")
    private BigDecimal clamIvsmAboy;

    @ApiModelProperty("其他债权投资_年初数(元)")
    private BigDecimal othClamIvsmAboy;

    @ApiModelProperty("长期应收款_年初数(元)")
    private BigDecimal longRecvAmtAboy;

    @ApiModelProperty("长期股权投资_年初数(元)")
    private BigDecimal lsriAboy;

    @ApiModelProperty("其他权益工具投资_年初数(元)")
    private BigDecimal othEquiInstIvsmAboy;
    
    @ApiModelProperty("其他非流动金融资产_年初数(元)")
    private BigDecimal othNocrFinlAstAboy;

    @ApiModelProperty("投资性房地产_年初数(元)")
    private BigDecimal ivsmEsttAboy;

    @ApiModelProperty("固定资产_年初数(元)")
    private BigDecimal fixAstAboy;

    @ApiModelProperty("在建工程_年初数(元)")
    private BigDecimal udcsProjAboy;

    @ApiModelProperty("生产性生物资产_年初数(元)")
    private BigDecimal prodBiolMatrAboy;

    @ApiModelProperty("油气资产_年初数(元)")
    private BigDecimal olgsAstAboy;

    @ApiModelProperty("使用权资产_年初数(元)")
    private BigDecimal useAstAboy;

    @ApiModelProperty("无形资产_年初数(元)")
    private BigDecimal imtrAstAboy;
    
    @ApiModelProperty("开发支出_年初数(元)")
    private BigDecimal devPayAboy;

    @ApiModelProperty("商誉_年初数(元)")
    private BigDecimal gdwlAboy;

    @ApiModelProperty("长期待摊费用_年初数(元)")
    private BigDecimal longTbatFeeAboy;
    
    @ApiModelProperty("递延所得税资产_年初数(元)")
    private BigDecimal defrTaxAstAboy;
    
    @ApiModelProperty("其他非流动资产_年初数(元)")
    private BigDecimal othNocrAstAboy;

    @ApiModelProperty("非流动资产合计_年初数(元)")
    private BigDecimal nocrAstTotAboy;
    
    @ApiModelProperty("资产总计_年初数(元)")
    private BigDecimal astTotAboy;
    
    @ApiModelProperty("短期借款_年初数(元)")
    private BigDecimal shttLoanAboy;
    
    @ApiModelProperty("交易性金融负债_年初数(元)")
    private BigDecimal trdFinlLiabAboy;

    @ApiModelProperty("衍生金融负债_年初数(元)")
    private BigDecimal devdFinlLiabAboy;

    @ApiModelProperty("应付票据_年初数(元)")
    private BigDecimal paybBillAboy;
    
    @ApiModelProperty("应付账款_年初数(元)")
    private BigDecimal paybAmtAboy;

    @ApiModelProperty("预收账款_年初数(元)")
    private BigDecimal ciadAmtAboy;
    
    @ApiModelProperty("合同负债_年初数(元)")
    private BigDecimal agmtLiabAboy;

    @ApiModelProperty("应付职工薪酬_年初数(元)")
    private BigDecimal paybEmpCmpsAboy;
    
    @ApiModelProperty("应交税费_年初数(元)")
    private BigDecimal paycTaxFeeAboy;
    
    @ApiModelProperty("其他应付款总计_年初数(元)")
    private BigDecimal othPaybAmtTotAboy;
    
    @ApiModelProperty("应付利息_年初数(元)")
    private BigDecimal paybIntrAboy;
    
    @ApiModelProperty("应付股利_年初数(元)")
    private BigDecimal paybDivdAboy;
    
    @ApiModelProperty("其他应付款_年初数(元)")
    private BigDecimal othPaybAmtAboy;
    
    @ApiModelProperty("持有待售负债_年初数(元)")
    private BigDecimal holdSaleLiabAboy;

    @ApiModelProperty("一年内到期的非流动负债_年初数(元)")
    private BigDecimal oneyNocrLiabAboy;
    
    @ApiModelProperty("其他流动负债_年初数(元)")
    private BigDecimal othLiqdLiabAboy;

    @ApiModelProperty("流动负债合计_年初数(元)")
    private BigDecimal liqdLiabTotAboy;
    
    @ApiModelProperty("长期借款_年初数(元)")
    private BigDecimal longLoanAboy;
    
    @ApiModelProperty("应付债券_年初数(元)")
    private BigDecimal paybBondAboy;
    
    @ApiModelProperty("长期应付款_年初数(元)")
    private BigDecimal longPaybAmtAboy;

    @ApiModelProperty("长期应付职工薪酬_年初数(元)")
    private BigDecimal longPaybEmpCmpsAboy;

    @ApiModelProperty("租赁负债_年初数(元)")
    private BigDecimal leasLiabAboy;
    
    @ApiModelProperty("预计负债_年初数(元)")
    private BigDecimal expeLiabAboy;

    @ApiModelProperty("递延收益_年初数(元)")
    private BigDecimal defrPayfAboy;
    
    @ApiModelProperty("递延所得税负债_年初数(元)")
    private BigDecimal defrInctLiabAboy;

    @ApiModelProperty("其他非流动负债_年初数(元)")
    private BigDecimal othNocrLiabAboy;

    @ApiModelProperty("非流动负债合计_年初数(元)")
    private BigDecimal nocrLiabTotAboy;
    
    @ApiModelProperty("负债合计_年初数(元)")
    private BigDecimal liabTotAboy;
    
    @ApiModelProperty("实收资本_年初数(元)")
    private BigDecimal paidCptlAboy;

    @ApiModelProperty("其他权益工具_年初数(元)")
    private BigDecimal othEquiInstAboy;
    
    @ApiModelProperty("资本公积_年初数(元)")
    private BigDecimal cptlRsrvAboy;
    
    @ApiModelProperty("其他综合收益_年初数(元)")
    private BigDecimal othCmphPayfAboy;

    @ApiModelProperty("专项储备_年初数(元)")
    private BigDecimal spclRsrvAboy;

    @ApiModelProperty("盈余公积_年初数(元)")
    private BigDecimal surpRsrvAboy;

    @ApiModelProperty("一般风险准备_年初数(元)")
    private BigDecimal riskPrepAboy;

    @ApiModelProperty("未分配利润_年初数(元)")
    private BigDecimal noAssnProfAboy;
    
    @ApiModelProperty("归属母公司所有者权益合计_年初数(元)")
    private BigDecimal attrPrnCponEquiAboy;

    @ApiModelProperty("少数股东权益_年初数(元)")
    private BigDecimal mishEquiAboy;

    @ApiModelProperty("所有者权益合计_年初数(元)")
    private BigDecimal toeqAboy;
    
    @ApiModelProperty("负债及所有者权益合计_年初数(元)")
    private BigDecimal liabToeqAboy;
    
    @ApiModelProperty("货币资金_期末数(元)")
    private BigDecimal crcpAeop;
    
    @ApiModelProperty("交易性金融资产_期末数(元)")
    private BigDecimal trdFinlAstAeop;
    
    @ApiModelProperty("衍生金融资产_期末数(元)")
    private BigDecimal devdFinlAstAeop;

    @ApiModelProperty("应收票据_期末数(元)")
    private BigDecimal recvBillAeop;
    
    @ApiModelProperty("应收账款_期末数(元)")
    private BigDecimal recvAmtAeop;

    @ApiModelProperty("应收款项融资_期末数(元)")
    private BigDecimal recvAmtFinAeop;

    @ApiModelProperty("预付账款_期末数(元)")
    private BigDecimal piaAmtAeop;
    
    @ApiModelProperty("其他应收款总计_期末数(元)")
    private BigDecimal othRecvAmtTotAeop;
    
    @ApiModelProperty("应收股利_期末数(元)")
    private BigDecimal recvDivdAeop;
    
    @ApiModelProperty("应收利息_期末数(元)")
    private BigDecimal recvIntrAeop;
    
    @ApiModelProperty("其他应收款_期末数(元)")
    private BigDecimal othRecvAmtAeop;

    @ApiModelProperty("存货_期末数(元)")
    private BigDecimal invAeop;

    @ApiModelProperty("合同资产_期末数(元)")
    private BigDecimal agmtAstAeop;
    
    @ApiModelProperty("持有待售资产_期末数(元)")
    private BigDecimal holdSaleAstAeop;
    
    @ApiModelProperty("一年内到期的非流动资产_期末数(元)")
    private BigDecimal ncoyAeop;
    
    @ApiModelProperty("其他流动资产_期末数(元)")
    private BigDecimal othLiqdAstAeop;
    
    @ApiModelProperty("流动资产合计_期末数(元)")
    private BigDecimal liqdAstTotAeop;
    
    @ApiModelProperty("债权投资_期末数(元)")
    private BigDecimal clamIvsmAeop;

    @ApiModelProperty("其他债权投资_期末数(元)")
    private BigDecimal othClamIvsmAeop;
    
    @ApiModelProperty("长期应收款_期末数(元)")
    private BigDecimal longRecvAmtAeop;

    @ApiModelProperty("长期股权投资_期末数(元)")
    private BigDecimal lsriAeop;

    @ApiModelProperty("其他权益工具投资_期末数(元)")
    private BigDecimal othEquiInstIvsmAeop;

    @ApiModelProperty("其他非流动金融资产_期末数(元)")
    private BigDecimal othNocrFinlAstAeop;
    
    @ApiModelProperty("投资性房地产_期末数(元)")
    private BigDecimal ivsmEsttAeop;

    @ApiModelProperty("固定资产_期末数(元)")
    private BigDecimal fixAstAeop;

    @ApiModelProperty("在建工程_期末数(元)")
    private BigDecimal udcsProjAeop;

    @ApiModelProperty("生产性生物资产_期末数(元)")
    private BigDecimal prodBiolMatrAeop;

    @ApiModelProperty("油气资产_期末数(元)")
    private BigDecimal olgsAstAeop;

    @ApiModelProperty("使用权资产_期末数(元)")
    private BigDecimal useAstAeop;

    @ApiModelProperty("无形资产_期末数(元)")
    private BigDecimal imtrAstAeop;
    
    @ApiModelProperty("开发支出_期末数(元)")
    private BigDecimal devPayAeop;

    @ApiModelProperty("商誉_期末数(元)")
    private BigDecimal gdwlAeop;
    
    @ApiModelProperty("长期待摊费用_期末数(元)")
    private BigDecimal longTbatFeeAeop;
    
    @ApiModelProperty("递延所得税资产_期末数(元)")
    private BigDecimal defrTaxAstAeop;
    
    @ApiModelProperty("其他非流动资产_期末数(元)")
    private BigDecimal othNocrAstAeop;
    
    @ApiModelProperty("非流动资产合计_期末数(元)")
    private BigDecimal nocrAstTotAeop;
    
    @ApiModelProperty("资产总计_期末数(元)")
    private BigDecimal astTotAeop;
    
    @ApiModelProperty("短期借款_期末数(元)")
    private BigDecimal shttLoanAeop;

    @ApiModelProperty("交易性金融负债_期末数(元)")
    private BigDecimal trdFinlLiabAeop;

    @ApiModelProperty("衍生金融负债_期末数(元)")
    private BigDecimal devdFinlLiabAeop;

    @ApiModelProperty("应付票据_期末数(元)")
    private BigDecimal paybBillAeop;

    @ApiModelProperty("应付账款_期末数(元)")
    private BigDecimal paybAmtAeop;

    @ApiModelProperty("预收账款_期末数(元)")
    private BigDecimal ciadAmtAeop;
    
    @ApiModelProperty("合同负债_期末数(元)")
    private BigDecimal agmtLiabAeop;

    @ApiModelProperty("应付职工薪酬_期末数(元)")
    private BigDecimal paybEmpCmpsAeop;
    
    @ApiModelProperty("应交税费_期末数(元)")
    private BigDecimal paycTaxFeeAeop;
    
    @ApiModelProperty("其他应付款总计_期末数(元)")
    private BigDecimal othPaybAmtTotAeop;
    
    @ApiModelProperty("应付利息_期末数(元)")
    private BigDecimal paybIntrAeop;
    
    @ApiModelProperty("应付股利_期末数(元)")
    private BigDecimal paybDivdAeop;
    
    @ApiModelProperty("其他应付款_期末数(元)")
    private BigDecimal othPaybAmtAeop;
    
    @ApiModelProperty("持有待售负债_期末数(元)")
    private BigDecimal holdSaleLiabAeop;
    
    @ApiModelProperty("一年内到期的非流动负债_期末数(元)")
    private BigDecimal oneyNocrLiabAeop;

    @ApiModelProperty("其他流动负债_期末数(元)")
    private BigDecimal othLiqdLiabAeop;
    
    @ApiModelProperty("流动负债合计_期末数(元)")
    private BigDecimal liqdLiabTotAeop;

    @ApiModelProperty("长期借款_期末数(元)")
    private BigDecimal longLoanAeop;
    
    @ApiModelProperty("应付债券_期末数(元)")
    private BigDecimal paybBondAeop;
    
    @ApiModelProperty("长期应付款_期末数(元)")
    private BigDecimal longPaybAmtAeop;

    @ApiModelProperty("长期应付职工薪酬_期末数(元)")
    private BigDecimal longPaybEmpCmpsAeop;
    
    @ApiModelProperty("租赁负债_期末数(元)")
    private BigDecimal leasLiabAeop;
    
    @ApiModelProperty("预计负债_期末数(元)")
    private BigDecimal expeLiabAeop;
    
    @ApiModelProperty("递延收益_期末数(元)")
    private BigDecimal defrPayfAeop;

    @ApiModelProperty("递延所得税负债_期末数(元)")
    private BigDecimal defrInctLiabAeop;
    
    @ApiModelProperty("其他非流动负债_期末数(元)")
    private BigDecimal othNocrLiabAeop;
    
    @ApiModelProperty("非流动负债合计_期末数(元)")
    private BigDecimal nocrLiabTotAeop;
    
    @ApiModelProperty("负债合计_期末数(元)")
    private BigDecimal liabTotAeop;
    
    @ApiModelProperty("实收资本_期末数(元)")
    private BigDecimal paidCptlAeop;
    
    @ApiModelProperty("其他权益工具_期末数(元)")
    private BigDecimal othEquiInstAeop;

    @ApiModelProperty("资本公积_期末数(元)")
    private BigDecimal cptlRsrvAeop;
    
    @ApiModelProperty("其他综合收益_期末数(元)")
    private BigDecimal othCmphPayfAeop;
    
    @ApiModelProperty("专项储备_期末数(元)")
    private BigDecimal spclRsrvAeop;

    @ApiModelProperty("盈余公积_期末数(元)")
    private BigDecimal surpRsrvAeop;
    
    @ApiModelProperty("一般风险准备_期末数(元)")
    private BigDecimal riskPrepAeop;

    @ApiModelProperty("未分配利润_期末数(元)")
    private BigDecimal noAssnProfAeop;
    
    @ApiModelProperty("归属母公司所有者权益合计_期末数(元)")
    private BigDecimal attrPrnCponEquiAeop;

    @ApiModelProperty("少数股东权益_期末数(元)")
    private BigDecimal mishEquiAeop;
    
    @ApiModelProperty("所有者权益合计_期末数(元)")
    private BigDecimal toeqAeop;

    @ApiModelProperty("负债及所有者权益合计_期末数(元)")
    private BigDecimal liabToeqAeop;
}
