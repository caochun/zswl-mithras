package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 资产负债表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("资产负债表(流程节点记录版本表)编辑-请求体")
public class AssociationBalanceSheetPartialLibModifyREQ {

    /**
    * 自增主键
    */
    @ApiModelProperty(value = "自增主键")
    private Long id;

    /**
    * 行号 | 同一批次数据从1开始递增
    */
    @ApiModelProperty(value = "行号 | 同一批次数据从1开始递增")
    private Integer rowNum;

    /**
    * 企业统一社会信用代码
    */
    @ApiModelProperty(value = "企业统一社会信用代码")
    private String unifSociCredCode;

    /**
    * 货币资金_年初数(元)
    */
    @ApiModelProperty(value = "货币资金_年初数(元)")
    private BigDecimal crcpAboy;

    /**
    * 交易性金融资产_年初数(元)
    */
    @ApiModelProperty(value = "交易性金融资产_年初数(元)")
    private BigDecimal trdFinlAstAboy;

    /**
    * 衍生金融资产_年初数(元)
    */
    @ApiModelProperty(value = "衍生金融资产_年初数(元)")
    private BigDecimal devdFinlAstAboy;

    /**
    * 应收票据_年初数(元)
    */
    @ApiModelProperty(value = "应收票据_年初数(元)")
    private BigDecimal recvBillAboy;

    /**
    * 应收账款_年初数(元)
    */
    @ApiModelProperty(value = "应收账款_年初数(元)")
    private BigDecimal recvAmtAboy;

    /**
    * 应收款项融资_年初数(元)
    */
    @ApiModelProperty(value = "应收款项融资_年初数(元)")
    private BigDecimal recvAmtFinAboy;

    /**
    * 预付账款_年初数(元)
    */
    @ApiModelProperty(value = "预付账款_年初数(元)")
    private BigDecimal piaAmtAboy;

    /**
    * 其他应收款总计_年初数(元)
    */
    @ApiModelProperty(value = "其他应收款总计_年初数(元)")
    private BigDecimal othRecvAmtTotAboy;

    /**
    * 应收股利_年初数(元)
    */
    @ApiModelProperty(value = "应收股利_年初数(元)")
    private BigDecimal recvDivdAboy;

    /**
    * 应收利息_年初数(元)
    */
    @ApiModelProperty(value = "应收利息_年初数(元)")
    private BigDecimal recvIntrAboy;

    /**
    * 其他应收款_年初数(元)
    */
    @ApiModelProperty(value = "其他应收款_年初数(元)")
    private BigDecimal othRecvAmtAboy;

    /**
    * 存货_年初数(元)
    */
    @ApiModelProperty(value = "存货_年初数(元)")
    private BigDecimal invAboy;

    /**
    * 合同资产_年初数(元)
    */
    @ApiModelProperty(value = "合同资产_年初数(元)")
    private BigDecimal agmtAstAboy;

    /**
    * 持有待售资产_年初数(元)
    */
    @ApiModelProperty(value = "持有待售资产_年初数(元)")
    private BigDecimal holdSaleAstAboy;

    /**
    * 一年内到期的非流动资产_年初数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动资产_年初数(元)")
    private BigDecimal ncoyAboy;

    /**
    * 其他流动资产_年初数(元)
    */
    @ApiModelProperty(value = "其他流动资产_年初数(元)")
    private BigDecimal othLiqdAstAboy;

    /**
    * 流动资产合计_年初数(元)
    */
    @ApiModelProperty(value = "流动资产合计_年初数(元)")
    private BigDecimal liqdAstTotAboy;

    /**
    * 非流动资产_年初数(元)
    */
    @ApiModelProperty(value = "非流动资产_年初数(元)")
    private BigDecimal noLiqdAstTotAboy;

    /**
    * 债权投资_年初数(元)
    */
    @ApiModelProperty(value = "债权投资_年初数(元)")
    private BigDecimal clamIvsmAboy;

    /**
    * 其他债权投资_年初数(元)
    */
    @ApiModelProperty(value = "其他债权投资_年初数(元)")
    private BigDecimal othClamIvsmAboy;

    /**
    * 长期应收款_年初数(元)
    */
    @ApiModelProperty(value = "长期应收款_年初数(元)")
    private BigDecimal longRecvAmtAboy;

    /**
    * 长期股权投资_年初数(元)
    */
    @ApiModelProperty(value = "长期股权投资_年初数(元)")
    private BigDecimal lsriAboy;

    /**
    * 其他权益工具投资_年初数(元)
    */
    @ApiModelProperty(value = "其他权益工具投资_年初数(元)")
    private BigDecimal othEquiInstIvsmAboy;

    /**
    * 其他非流动金融资产_年初数(元)
    */
    @ApiModelProperty(value = "其他非流动金融资产_年初数(元)")
    private BigDecimal othNocrFinlAstAboy;

    /**
    * 投资性房地产_年初数(元)
    */
    @ApiModelProperty(value = "投资性房地产_年初数(元)")
    private BigDecimal ivsmEsttAboy;

    /**
    * 固定资产_年初数(元)
    */
    @ApiModelProperty(value = "固定资产_年初数(元)")
    private BigDecimal fixAstAboy;

    /**
    * 在建工程_年初数(元)
    */
    @ApiModelProperty(value = "在建工程_年初数(元)")
    private BigDecimal udcsProjAboy;

    /**
    * 生产性生物资产_年初数(元)
    */
    @ApiModelProperty(value = "生产性生物资产_年初数(元)")
    private BigDecimal prodBiolMatrAboy;

    /**
    * 油气资产_年初数(元)
    */
    @ApiModelProperty(value = "油气资产_年初数(元)")
    private BigDecimal olgsAstAboy;

    /**
    * 使用权资产_年初数(元)
    */
    @ApiModelProperty(value = "使用权资产_年初数(元)")
    private BigDecimal useAstAboy;

    /**
    * 无形资产_年初数(元)
    */
    @ApiModelProperty(value = "无形资产_年初数(元)")
    private BigDecimal imtrAstAboy;

    /**
    * 开发支出_年初数(元)
    */
    @ApiModelProperty(value = "开发支出_年初数(元)")
    private BigDecimal devPayAboy;

    /**
    * 商誉_年初数(元)
    */
    @ApiModelProperty(value = "商誉_年初数(元)")
    private BigDecimal gdwlAboy;

    /**
    * 长期待摊费用_年初数(元)
    */
    @ApiModelProperty(value = "长期待摊费用_年初数(元)")
    private BigDecimal longTbatFeeAboy;

    /**
    * 递延所得税资产_年初数(元)
    */
    @ApiModelProperty(value = "递延所得税资产_年初数(元)")
    private BigDecimal defrTaxAstAboy;

    /**
    * 其他非流动资产_年初数(元)
    */
    @ApiModelProperty(value = "其他非流动资产_年初数(元)")
    private BigDecimal othNocrAstAboy;

    /**
    * 非流动资产合计_年初数(元)
    */
    @ApiModelProperty(value = "非流动资产合计_年初数(元)")
    private BigDecimal nocrAstTotAboy;

    /**
    * 资产总计_年初数(元)
    */
    @ApiModelProperty(value = "资产总计_年初数(元)")
    private BigDecimal astTotAboy;

    /**
    * 短期借款_年初数(元)
    */
    @ApiModelProperty(value = "短期借款_年初数(元)")
    private BigDecimal shttLoanAboy;

    /**
    * 交易性金融负债_年初数(元)
    */
    @ApiModelProperty(value = "交易性金融负债_年初数(元)")
    private BigDecimal trdFinlLiabAboy;

    /**
    * 衍生金融负债_年初数(元)
    */
    @ApiModelProperty(value = "衍生金融负债_年初数(元)")
    private BigDecimal devdFinlLiabAboy;

    /**
    * 应付票据_年初数(元)
    */
    @ApiModelProperty(value = "应付票据_年初数(元)")
    private BigDecimal paybBillAboy;

    /**
    * 应付账款_年初数(元)
    */
    @ApiModelProperty(value = "应付账款_年初数(元)")
    private BigDecimal paybAmtAboy;

    /**
    * 预收账款_年初数(元)
    */
    @ApiModelProperty(value = "预收账款_年初数(元)")
    private BigDecimal ciadAmtAboy;

    /**
    * 合同负债_年初数(元)
    */
    @ApiModelProperty(value = "合同负债_年初数(元)")
    private BigDecimal agmtLiabAboy;

    /**
    * 应付职工薪酬_年初数(元)
    */
    @ApiModelProperty(value = "应付职工薪酬_年初数(元)")
    private BigDecimal paybEmpCmpsAboy;

    /**
    * 应交税费_年初数(元)
    */
    @ApiModelProperty(value = "应交税费_年初数(元)")
    private BigDecimal paycTaxFeeAboy;

    /**
    * 其他应付款总计_年初数(元)
    */
    @ApiModelProperty(value = "其他应付款总计_年初数(元)")
    private BigDecimal othPaybAmtTotAboy;

    /**
    * 应付利息_年初数(元)
    */
    @ApiModelProperty(value = "应付利息_年初数(元)")
    private BigDecimal paybIntrAboy;

    /**
    * 应付股利_年初数(元)
    */
    @ApiModelProperty(value = "应付股利_年初数(元)")
    private BigDecimal paybDivdAboy;

    /**
    * 其他应付款_年初数(元)
    */
    @ApiModelProperty(value = "其他应付款_年初数(元)")
    private BigDecimal othPaybAmtAboy;

    /**
    * 持有待售负债_年初数(元)
    */
    @ApiModelProperty(value = "持有待售负债_年初数(元)")
    private BigDecimal holdSaleLiabAboy;

    /**
    * 一年内到期的非流动负债_年初数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动负债_年初数(元)")
    private BigDecimal oneyNocrLiabAboy;

    /**
    * 其他流动负债_年初数(元)
    */
    @ApiModelProperty(value = "其他流动负债_年初数(元)")
    private BigDecimal othLiqdLiabAboy;

    /**
    * 流动负债合计_年初数(元)
    */
    @ApiModelProperty(value = "流动负债合计_年初数(元)")
    private BigDecimal liqdLiabTotAboy;

    /**
    * 长期借款_年初数(元)
    */
    @ApiModelProperty(value = "长期借款_年初数(元)")
    private BigDecimal longLoanAboy;

    /**
    * 应付债券_年初数(元)
    */
    @ApiModelProperty(value = "应付债券_年初数(元)")
    private BigDecimal paybBondAboy;

    /**
    * 长期应付款_年初数(元)
    */
    @ApiModelProperty(value = "长期应付款_年初数(元)")
    private BigDecimal longPaybAmtAboy;

    /**
    * 长期应付职工薪酬_年初数(元)
    */
    @ApiModelProperty(value = "长期应付职工薪酬_年初数(元)")
    private BigDecimal longPaybEmpCmpsAboy;

    /**
    * 租赁负债_年初数(元)
    */
    @ApiModelProperty(value = "租赁负债_年初数(元)")
    private BigDecimal leasLiabAboy;

    /**
    * 预计负债_年初数(元)
    */
    @ApiModelProperty(value = "预计负债_年初数(元)")
    private BigDecimal expeLiabAboy;

    /**
    * 递延收益_年初数(元)
    */
    @ApiModelProperty(value = "递延收益_年初数(元)")
    private BigDecimal defrPayfAboy;

    /**
    * 递延所得税负债_年初数(元)
    */
    @ApiModelProperty(value = "递延所得税负债_年初数(元)")
    private BigDecimal defrInctLiabAboy;

    /**
    * 其他非流动负债_年初数(元)
    */
    @ApiModelProperty(value = "其他非流动负债_年初数(元)")
    private BigDecimal othNocrLiabAboy;

    /**
    * 非流动负债合计_年初数(元)
    */
    @ApiModelProperty(value = "非流动负债合计_年初数(元)")
    private BigDecimal nocrLiabTotAboy;

    /**
    * 负债合计_年初数(元)
    */
    @ApiModelProperty(value = "负债合计_年初数(元)")
    private BigDecimal liabTotAboy;

    /**
    * 实收资本_年初数(元)
    */
    @ApiModelProperty(value = "实收资本_年初数(元)")
    private BigDecimal paidCptlAboy;

    /**
    * 其他权益工具_年初数(元)
    */
    @ApiModelProperty(value = "其他权益工具_年初数(元)")
    private BigDecimal othEquiInstAboy;

    /**
    * 资本公积_年初数(元)
    */
    @ApiModelProperty(value = "资本公积_年初数(元)")
    private BigDecimal cptlRsrvAboy;

    /**
    * 其他综合收益_年初数(元)
    */
    @ApiModelProperty(value = "其他综合收益_年初数(元)")
    private BigDecimal othCmphPayfAboy;

    /**
    * 专项储备_年初数(元)
    */
    @ApiModelProperty(value = "专项储备_年初数(元)")
    private BigDecimal spclRsrvAboy;

    /**
    * 盈余公积_年初数(元)
    */
    @ApiModelProperty(value = "盈余公积_年初数(元)")
    private BigDecimal surpRsrvAboy;

    /**
    * 一般风险准备_年初数(元)
    */
    @ApiModelProperty(value = "一般风险准备_年初数(元)")
    private BigDecimal riskPrepAboy;

    /**
    * 未分配利润_年初数(元)
    */
    @ApiModelProperty(value = "未分配利润_年初数(元)")
    private BigDecimal noAssnProfAboy;

    /**
    * 归属母公司所有者权益合计_年初数(元)
    */
    @ApiModelProperty(value = "归属母公司所有者权益合计_年初数(元)")
    private BigDecimal attrPrnCponEquiAboy;

    /**
    * 少数股东权益_年初数(元)
    */
    @ApiModelProperty(value = "少数股东权益_年初数(元)")
    private BigDecimal mishEquiAboy;

    /**
    * 所有者权益合计_年初数(元)
    */
    @ApiModelProperty(value = "所有者权益合计_年初数(元)")
    private BigDecimal toeqAboy;

    /**
    * 负债及所有者权益合计_年初数(元)
    */
    @ApiModelProperty(value = "负债及所有者权益合计_年初数(元)")
    private BigDecimal liabToeqAboy;

    /**
    * 货币资金_期末数(元)
    */
    @ApiModelProperty(value = "货币资金_期末数(元)")
    private BigDecimal crcpAeop;

    /**
    * 交易性金融资产_期末数(元)
    */
    @ApiModelProperty(value = "交易性金融资产_期末数(元)")
    private BigDecimal trdFinlAstAeop;

    /**
    * 衍生金融资产_期末数(元)
    */
    @ApiModelProperty(value = "衍生金融资产_期末数(元)")
    private BigDecimal devdFinlAstAeop;

    /**
    * 应收票据_期末数(元)
    */
    @ApiModelProperty(value = "应收票据_期末数(元)")
    private BigDecimal recvBillAeop;

    /**
    * 应收账款_期末数(元)
    */
    @ApiModelProperty(value = "应收账款_期末数(元)")
    private BigDecimal recvAmtAeop;

    /**
    * 应收款项融资_期末数(元)
    */
    @ApiModelProperty(value = "应收款项融资_期末数(元)")
    private BigDecimal recvAmtFinAeop;

    /**
    * 预付账款_期末数(元)
    */
    @ApiModelProperty(value = "预付账款_期末数(元)")
    private BigDecimal piaAmtAeop;

    /**
    * 其他应收款总计_期末数(元)
    */
    @ApiModelProperty(value = "其他应收款总计_期末数(元)")
    private BigDecimal othRecvAmtTotAeop;

    /**
    * 应收股利_期末数(元)
    */
    @ApiModelProperty(value = "应收股利_期末数(元)")
    private BigDecimal recvDivdAeop;

    /**
    * 应收利息_期末数(元)
    */
    @ApiModelProperty(value = "应收利息_期末数(元)")
    private BigDecimal recvIntrAeop;

    /**
    * 其他应收款_期末数(元)
    */
    @ApiModelProperty(value = "其他应收款_期末数(元)")
    private BigDecimal othRecvAmtAeop;

    /**
    * 存货_期末数(元)
    */
    @ApiModelProperty(value = "存货_期末数(元)")
    private BigDecimal invAeop;

    /**
    * 合同资产_期末数(元)
    */
    @ApiModelProperty(value = "合同资产_期末数(元)")
    private BigDecimal agmtAstAeop;

    /**
    * 持有待售资产_期末数(元)
    */
    @ApiModelProperty(value = "持有待售资产_期末数(元)")
    private BigDecimal holdSaleAstAeop;

    /**
    * 一年内到期的非流动资产_期末数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动资产_期末数(元)")
    private BigDecimal ncoyAeop;

    /**
    * 其他流动资产_期末数(元)
    */
    @ApiModelProperty(value = "其他流动资产_期末数(元)")
    private BigDecimal othLiqdAstAeop;

    /**
    * 流动资产合计_期末数(元)
    */
    @ApiModelProperty(value = "流动资产合计_期末数(元)")
    private BigDecimal liqdAstTotAeop;

    /**
    * 债权投资_期末数(元)
    */
    @ApiModelProperty(value = "债权投资_期末数(元)")
    private BigDecimal clamIvsmAeop;

    /**
    * 其他债权投资_期末数(元)
    */
    @ApiModelProperty(value = "其他债权投资_期末数(元)")
    private BigDecimal othClamIvsmAeop;

    /**
    * 长期应收款_期末数(元)
    */
    @ApiModelProperty(value = "长期应收款_期末数(元)")
    private BigDecimal longRecvAmtAeop;

    /**
    * 长期股权投资_期末数(元)
    */
    @ApiModelProperty(value = "长期股权投资_期末数(元)")
    private BigDecimal lsriAeop;

    /**
    * 其他权益工具投资_期末数(元)
    */
    @ApiModelProperty(value = "其他权益工具投资_期末数(元)")
    private BigDecimal othEquiInstIvsmAeop;

    /**
    * 其他非流动金融资产_期末数(元)
    */
    @ApiModelProperty(value = "其他非流动金融资产_期末数(元)")
    private BigDecimal othNocrFinlAstAeop;

    /**
    * 投资性房地产_期末数(元)
    */
    @ApiModelProperty(value = "投资性房地产_期末数(元)")
    private BigDecimal ivsmEsttAeop;

    /**
    * 固定资产_期末数(元)
    */
    @ApiModelProperty(value = "固定资产_期末数(元)")
    private BigDecimal fixAstAeop;

    /**
    * 在建工程_期末数(元)
    */
    @ApiModelProperty(value = "在建工程_期末数(元)")
    private BigDecimal udcsProjAeop;

    /**
    * 生产性生物资产_期末数(元)
    */
    @ApiModelProperty(value = "生产性生物资产_期末数(元)")
    private BigDecimal prodBiolMatrAeop;

    /**
    * 油气资产_期末数(元)
    */
    @ApiModelProperty(value = "油气资产_期末数(元)")
    private BigDecimal olgsAstAeop;

    /**
    * 使用权资产_期末数(元)
    */
    @ApiModelProperty(value = "使用权资产_期末数(元)")
    private BigDecimal useAstAeop;

    /**
    * 无形资产_期末数(元)
    */
    @ApiModelProperty(value = "无形资产_期末数(元)")
    private BigDecimal imtrAstAeop;

    /**
    * 开发支出_期末数(元)
    */
    @ApiModelProperty(value = "开发支出_期末数(元)")
    private BigDecimal devPayAeop;

    /**
    * 商誉_期末数(元)
    */
    @ApiModelProperty(value = "商誉_期末数(元)")
    private BigDecimal gdwlAeop;

    /**
    * 长期待摊费用_期末数(元)
    */
    @ApiModelProperty(value = "长期待摊费用_期末数(元)")
    private BigDecimal longTbatFeeAeop;

    /**
    * 递延所得税资产_期末数(元)
    */
    @ApiModelProperty(value = "递延所得税资产_期末数(元)")
    private BigDecimal defrTaxAstAeop;

    /**
    * 其他非流动资产_期末数(元)
    */
    @ApiModelProperty(value = "其他非流动资产_期末数(元)")
    private BigDecimal othNocrAstAeop;

    /**
    * 非流动资产合计_期末数(元)
    */
    @ApiModelProperty(value = "非流动资产合计_期末数(元)")
    private BigDecimal nocrAstTotAeop;

    /**
    * 资产总计_期末数(元)
    */
    @ApiModelProperty(value = "资产总计_期末数(元)")
    private BigDecimal astTotAeop;

    /**
    * 短期借款_期末数(元)
    */
    @ApiModelProperty(value = "短期借款_期末数(元)")
    private BigDecimal shttLoanAeop;

    /**
    * 交易性金融负债_期末数(元)
    */
    @ApiModelProperty(value = "交易性金融负债_期末数(元)")
    private BigDecimal trdFinlLiabAeop;

    /**
    * 衍生金融负债_期末数(元)
    */
    @ApiModelProperty(value = "衍生金融负债_期末数(元)")
    private BigDecimal devdFinlLiabAeop;

    /**
    * 应付票据_期末数(元)
    */
    @ApiModelProperty(value = "应付票据_期末数(元)")
    private BigDecimal paybBillAeop;

    /**
    * 应付账款_期末数(元)
    */
    @ApiModelProperty(value = "应付账款_期末数(元)")
    private BigDecimal paybAmtAeop;

    /**
    * 预收账款_期末数(元)
    */
    @ApiModelProperty(value = "预收账款_期末数(元)")
    private BigDecimal ciadAmtAeop;

    /**
    * 合同负债_期末数(元)
    */
    @ApiModelProperty(value = "合同负债_期末数(元)")
    private BigDecimal agmtLiabAeop;

    /**
    * 应付职工薪酬_期末数(元)
    */
    @ApiModelProperty(value = "应付职工薪酬_期末数(元)")
    private BigDecimal paybEmpCmpsAeop;

    /**
    * 应交税费_期末数(元)
    */
    @ApiModelProperty(value = "应交税费_期末数(元)")
    private BigDecimal paycTaxFeeAeop;

    /**
    * 其他应付款总计_期末数(元)
    */
    @ApiModelProperty(value = "其他应付款总计_期末数(元)")
    private BigDecimal othPaybAmtTotAeop;

    /**
    * 应付利息_期末数(元)
    */
    @ApiModelProperty(value = "应付利息_期末数(元)")
    private BigDecimal paybIntrAeop;

    /**
    * 应付股利_期末数(元)
    */
    @ApiModelProperty(value = "应付股利_期末数(元)")
    private BigDecimal paybDivdAeop;

    /**
    * 其他应付款_期末数(元)
    */
    @ApiModelProperty(value = "其他应付款_期末数(元)")
    private BigDecimal othPaybAmtAeop;

    /**
    * 持有待售负债_期末数(元)
    */
    @ApiModelProperty(value = "持有待售负债_期末数(元)")
    private BigDecimal holdSaleLiabAeop;

    /**
    * 一年内到期的非流动负债_期末数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动负债_期末数(元)")
    private BigDecimal oneyNocrLiabAeop;

    /**
    * 其他流动负债_期末数(元)
    */
    @ApiModelProperty(value = "其他流动负债_期末数(元)")
    private BigDecimal othLiqdLiabAeop;

    /**
    * 流动负债合计_期末数(元)
    */
    @ApiModelProperty(value = "流动负债合计_期末数(元)")
    private BigDecimal liqdLiabTotAeop;

    /**
    * 长期借款_期末数(元)
    */
    @ApiModelProperty(value = "长期借款_期末数(元)")
    private BigDecimal longLoanAeop;

    /**
    * 应付债券_期末数(元)
    */
    @ApiModelProperty(value = "应付债券_期末数(元)")
    private BigDecimal paybBondAeop;

    /**
    * 长期应付款_期末数(元)
    */
    @ApiModelProperty(value = "长期应付款_期末数(元)")
    private BigDecimal longPaybAmtAeop;

    /**
    * 长期应付职工薪酬_期末数(元)
    */
    @ApiModelProperty(value = "长期应付职工薪酬_期末数(元)")
    private BigDecimal longPaybEmpCmpsAeop;

    /**
    * 租赁负债_期末数(元)
    */
    @ApiModelProperty(value = "租赁负债_期末数(元)")
    private BigDecimal leasLiabAeop;

    /**
    * 预计负债_期末数(元)
    */
    @ApiModelProperty(value = "预计负债_期末数(元)")
    private BigDecimal expeLiabAeop;

    /**
    * 递延收益_期末数(元)
    */
    @ApiModelProperty(value = "递延收益_期末数(元)")
    private BigDecimal defrPayfAeop;

    /**
    * 递延所得税负债_期末数(元)
    */
    @ApiModelProperty(value = "递延所得税负债_期末数(元)")
    private BigDecimal defrInctLiabAeop;

    /**
    * 其他非流动负债_期末数(元)
    */
    @ApiModelProperty(value = "其他非流动负债_期末数(元)")
    private BigDecimal othNocrLiabAeop;

    /**
    * 非流动负债合计_期末数(元)
    */
    @ApiModelProperty(value = "非流动负债合计_期末数(元)")
    private BigDecimal nocrLiabTotAeop;

    /**
    * 负债合计_期末数(元)
    */
    @ApiModelProperty(value = "负债合计_期末数(元)")
    private BigDecimal liabTotAeop;

    /**
    * 实收资本_期末数(元)
    */
    @ApiModelProperty(value = "实收资本_期末数(元)")
    private BigDecimal paidCptlAeop;

    /**
    * 其他权益工具_期末数(元)
    */
    @ApiModelProperty(value = "其他权益工具_期末数(元)")
    private BigDecimal othEquiInstAeop;

    /**
    * 资本公积_期末数(元)
    */
    @ApiModelProperty(value = "资本公积_期末数(元)")
    private BigDecimal cptlRsrvAeop;

    /**
    * 其他综合收益_期末数(元)
    */
    @ApiModelProperty(value = "其他综合收益_期末数(元)")
    private BigDecimal othCmphPayfAeop;

    /**
    * 专项储备_期末数(元)
    */
    @ApiModelProperty(value = "专项储备_期末数(元)")
    private BigDecimal spclRsrvAeop;

    /**
    * 盈余公积_期末数(元)
    */
    @ApiModelProperty(value = "盈余公积_期末数(元)")
    private BigDecimal surpRsrvAeop;

    /**
    * 一般风险准备_期末数(元)
    */
    @ApiModelProperty(value = "一般风险准备_期末数(元)")
    private BigDecimal riskPrepAeop;

    /**
    * 未分配利润_期末数(元)
    */
    @ApiModelProperty(value = "未分配利润_期末数(元)")
    private BigDecimal noAssnProfAeop;

    /**
    * 归属母公司所有者权益合计_期末数(元)
    */
    @ApiModelProperty(value = "归属母公司所有者权益合计_期末数(元)")
    private BigDecimal attrPrnCponEquiAeop;

    /**
    * 少数股东权益_期末数(元)
    */
    @ApiModelProperty(value = "少数股东权益_期末数(元)")
    private BigDecimal mishEquiAeop;

    /**
    * 所有者权益合计_期末数(元)
    */
    @ApiModelProperty(value = "所有者权益合计_期末数(元)")
    private BigDecimal toeqAeop;

    /**
    * 负债及所有者权益合计_期末数(元)
    */
    @ApiModelProperty(value = "负债及所有者权益合计_期末数(元)")
    private BigDecimal liabToeqAeop;

    /**
    * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
    */
    @ApiModelProperty(value = "报表实例编号 uuid联合主键：(report_instance_id， row_num)")
    private String reportInstanceId;

    /**
    * 报表实例周期格式：yyyyqq
    */
    @ApiModelProperty(value = "报表实例周期格式：yyyyqq")
    private String reportInstancePeriod;

    /**
    * 批次号
    */
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    /**
    * 操作标识 新增数据为insert，更新数据记录时值为update
    */
    @ApiModelProperty(value = "操作标识 新增数据为insert，更新数据记录时值为update")
    private String op;

    /**
    * 上报时间
    */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportTime;

    /**
    * 写入时间
    */
    @ApiModelProperty(value = "写入时间")
    private LocalDateTime writeTime;

    /**
    * 逻辑删除标识
    */
    @ApiModelProperty(value = "逻辑删除标识")
    private Integer deleted;

    /**
    * 版本号
    */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
    * 临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写
    */
    @ApiModelProperty(value = "临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写")
    private Long originId;

    /**
    * data_create_time
    */
    @ApiModelProperty(value = "data_create_time")
    private LocalDateTime dataCreateTime;

    /**
    * data_create_by
    */
    @ApiModelProperty(value = "data_create_by")
    private Long dataCreateBy;

    /**
    * data_update_time
    */
    @ApiModelProperty(value = "data_update_time")
    private LocalDateTime dataUpdateTime;

    /**
    * data_update_by
    */
    @ApiModelProperty(value = "data_update_by")
    private Long dataUpdateBy;

    /**
    * 版本标志，0无效，1有效...业务自扩展
    */
    @ApiModelProperty(value = "版本标志，0无效，1有效...业务自扩展")
    private Integer versionType;

}
