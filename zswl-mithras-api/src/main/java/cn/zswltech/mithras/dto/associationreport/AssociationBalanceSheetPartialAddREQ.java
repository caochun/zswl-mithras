package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 资产负债表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("资产负债表新增-请求体")
public class AssociationBalanceSheetPartialAddREQ {

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
    private Double crcpAboy;

    /**
    * 交易性金融资产_年初数(元)
    */
    @ApiModelProperty(value = "交易性金融资产_年初数(元)")
    private Double trdFinlAstAboy;

    /**
    * 衍生金融资产_年初数(元)
    */
    @ApiModelProperty(value = "衍生金融资产_年初数(元)")
    private Double devdFinlAstAboy;

    /**
    * 应收票据_年初数(元)
    */
    @ApiModelProperty(value = "应收票据_年初数(元)")
    private Double recvBillAboy;

    /**
    * 应收账款_年初数(元)
    */
    @ApiModelProperty(value = "应收账款_年初数(元)")
    private Double recvAmtAboy;

    /**
    * 应收款项融资_年初数(元)
    */
    @ApiModelProperty(value = "应收款项融资_年初数(元)")
    private Double recvAmtFinAboy;

    /**
    * 预付账款_年初数(元)
    */
    @ApiModelProperty(value = "预付账款_年初数(元)")
    private Double piaAmtAboy;

    /**
    * 其他应收款总计_年初数(元)
    */
    @ApiModelProperty(value = "其他应收款总计_年初数(元)")
    private Double othRecvAmtTotAboy;

    /**
    * 应收股利_年初数(元)
    */
    @ApiModelProperty(value = "应收股利_年初数(元)")
    private Double recvDivdAboy;

    /**
    * 应收利息_年初数(元)
    */
    @ApiModelProperty(value = "应收利息_年初数(元)")
    private Double recvIntrAboy;

    /**
    * 其他应收款_年初数(元)
    */
    @ApiModelProperty(value = "其他应收款_年初数(元)")
    private Double othRecvAmtAboy;

    /**
    * 存货_年初数(元)
    */
    @ApiModelProperty(value = "存货_年初数(元)")
    private Double invAboy;

    /**
    * 合同资产_年初数(元)
    */
    @ApiModelProperty(value = "合同资产_年初数(元)")
    private Double agmtAstAboy;

    /**
    * 持有待售资产_年初数(元)
    */
    @ApiModelProperty(value = "持有待售资产_年初数(元)")
    private Double holdSaleAstAboy;

    /**
    * 一年内到期的非流动资产_年初数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动资产_年初数(元)")
    private Double ncoyAboy;

    /**
    * 其他流动资产_年初数(元)
    */
    @ApiModelProperty(value = "其他流动资产_年初数(元)")
    private Double othLiqdAstAboy;

    /**
    * 流动资产合计_年初数(元)
    */
    @ApiModelProperty(value = "流动资产合计_年初数(元)")
    private Double liqdAstTotAboy;

    /**
    * 债权投资_年初数(元)
    */
    @ApiModelProperty(value = "债权投资_年初数(元)")
    private Double clamIvsmAboy;

    /**
    * 其他债权投资_年初数(元)
    */
    @ApiModelProperty(value = "其他债权投资_年初数(元)")
    private Double othClamIvsmAboy;

    /**
    * 长期应收款_年初数(元)
    */
    @ApiModelProperty(value = "长期应收款_年初数(元)")
    private Double longRecvAmtAboy;

    /**
    * 长期股权投资_年初数(元)
    */
    @ApiModelProperty(value = "长期股权投资_年初数(元)")
    private Double lsriAboy;

    /**
    * 其他权益工具投资_年初数(元)
    */
    @ApiModelProperty(value = "其他权益工具投资_年初数(元)")
    private Double othEquiInstIvsmAboy;

    /**
    * 其他非流动金融资产_年初数(元)
    */
    @ApiModelProperty(value = "其他非流动金融资产_年初数(元)")
    private Double othNocrFinlAstAboy;

    /**
    * 投资性房地产_年初数(元)
    */
    @ApiModelProperty(value = "投资性房地产_年初数(元)")
    private Double ivsmEsttAboy;

    /**
    * 固定资产_年初数(元)
    */
    @ApiModelProperty(value = "固定资产_年初数(元)")
    private Double fixAstAboy;

    /**
    * 在建工程_年初数(元)
    */
    @ApiModelProperty(value = "在建工程_年初数(元)")
    private Double udcsProjAboy;

    /**
    * 生产性生物资产_年初数(元)
    */
    @ApiModelProperty(value = "生产性生物资产_年初数(元)")
    private Double prodBiolMatrAboy;

    /**
    * 油气资产_年初数(元)
    */
    @ApiModelProperty(value = "油气资产_年初数(元)")
    private Double olgsAstAboy;

    /**
    * 使用权资产_年初数(元)
    */
    @ApiModelProperty(value = "使用权资产_年初数(元)")
    private Double useAstAboy;

    /**
    * 无形资产_年初数(元)
    */
    @ApiModelProperty(value = "无形资产_年初数(元)")
    private Double imtrAstAboy;

    /**
    * 开发支出_年初数(元)
    */
    @ApiModelProperty(value = "开发支出_年初数(元)")
    private Double devPayAboy;

    /**
    * 商誉_年初数(元)
    */
    @ApiModelProperty(value = "商誉_年初数(元)")
    private Double gdwlAboy;

    /**
    * 长期待摊费用_年初数(元)
    */
    @ApiModelProperty(value = "长期待摊费用_年初数(元)")
    private Double longTbatFeeAboy;

    /**
    * 递延所得税资产_年初数(元)
    */
    @ApiModelProperty(value = "递延所得税资产_年初数(元)")
    private Double defrTaxAstAboy;

    /**
    * 其他非流动资产_年初数(元)
    */
    @ApiModelProperty(value = "其他非流动资产_年初数(元)")
    private Double othNocrAstAboy;

    /**
    * 非流动资产合计_年初数(元)
    */
    @ApiModelProperty(value = "非流动资产合计_年初数(元)")
    private Double nocrAstTotAboy;

    /**
    * 资产总计_年初数(元)
    */
    @ApiModelProperty(value = "资产总计_年初数(元)")
    private Double astTotAboy;

    /**
    * 短期借款_年初数(元)
    */
    @ApiModelProperty(value = "短期借款_年初数(元)")
    private Double shttLoanAboy;

    /**
    * 交易性金融负债_年初数(元)
    */
    @ApiModelProperty(value = "交易性金融负债_年初数(元)")
    private Double trdFinlLiabAboy;

    /**
    * 衍生金融负债_年初数(元)
    */
    @ApiModelProperty(value = "衍生金融负债_年初数(元)")
    private Double devdFinlLiabAboy;

    /**
    * 应付票据_年初数(元)
    */
    @ApiModelProperty(value = "应付票据_年初数(元)")
    private Double paybBillAboy;

    /**
    * 应付账款_年初数(元)
    */
    @ApiModelProperty(value = "应付账款_年初数(元)")
    private Double paybAmtAboy;

    /**
    * 预收账款_年初数(元)
    */
    @ApiModelProperty(value = "预收账款_年初数(元)")
    private Double ciadAmtAboy;

    /**
    * 合同负债_年初数(元)
    */
    @ApiModelProperty(value = "合同负债_年初数(元)")
    private Double agmtLiabAboy;

    /**
    * 应付职工薪酬_年初数(元)
    */
    @ApiModelProperty(value = "应付职工薪酬_年初数(元)")
    private Double paybEmpCmpsAboy;

    /**
    * 应交税费_年初数(元)
    */
    @ApiModelProperty(value = "应交税费_年初数(元)")
    private Double paycTaxFeeAboy;

    /**
    * 其他应付款总计_年初数(元)
    */
    @ApiModelProperty(value = "其他应付款总计_年初数(元)")
    private Double othPaybAmtTotAboy;

    /**
    * 应付利息_年初数(元)
    */
    @ApiModelProperty(value = "应付利息_年初数(元)")
    private Double paybIntrAboy;

    /**
    * 应付股利_年初数(元)
    */
    @ApiModelProperty(value = "应付股利_年初数(元)")
    private Double paybDivdAboy;

    /**
    * 其他应付款_年初数(元)
    */
    @ApiModelProperty(value = "其他应付款_年初数(元)")
    private Double othPaybAmtAboy;

    /**
    * 持有待售负债_年初数(元)
    */
    @ApiModelProperty(value = "持有待售负债_年初数(元)")
    private Double holdSaleLiabAboy;

    /**
    * 一年内到期的非流动负债_年初数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动负债_年初数(元)")
    private Double oneyNocrLiabAboy;

    /**
    * 其他流动负债_年初数(元)
    */
    @ApiModelProperty(value = "其他流动负债_年初数(元)")
    private Double othLiqdLiabAboy;

    /**
    * 流动负债合计_年初数(元)
    */
    @ApiModelProperty(value = "流动负债合计_年初数(元)")
    private Double liqdLiabTotAboy;

    /**
    * 长期借款_年初数(元)
    */
    @ApiModelProperty(value = "长期借款_年初数(元)")
    private Double longLoanAboy;

    /**
    * 应付债券_年初数(元)
    */
    @ApiModelProperty(value = "应付债券_年初数(元)")
    private Double paybBondAboy;

    /**
    * 长期应付款_年初数(元)
    */
    @ApiModelProperty(value = "长期应付款_年初数(元)")
    private Double longPaybAmtAboy;

    /**
    * 长期应付职工薪酬_年初数(元)
    */
    @ApiModelProperty(value = "长期应付职工薪酬_年初数(元)")
    private Double longPaybEmpCmpsAboy;

    /**
    * 租赁负债_年初数(元)
    */
    @ApiModelProperty(value = "租赁负债_年初数(元)")
    private Double leasLiabAboy;

    /**
    * 预计负债_年初数(元)
    */
    @ApiModelProperty(value = "预计负债_年初数(元)")
    private Double expeLiabAboy;

    /**
    * 递延收益_年初数(元)
    */
    @ApiModelProperty(value = "递延收益_年初数(元)")
    private Double defrPayfAboy;

    /**
    * 递延所得税负债_年初数(元)
    */
    @ApiModelProperty(value = "递延所得税负债_年初数(元)")
    private Double defrInctLiabAboy;

    /**
    * 其他非流动负债_年初数(元)
    */
    @ApiModelProperty(value = "其他非流动负债_年初数(元)")
    private Double othNocrLiabAboy;

    /**
    * 非流动负债合计_年初数(元)
    */
    @ApiModelProperty(value = "非流动负债合计_年初数(元)")
    private Double nocrLiabTotAboy;

    /**
    * 负债合计_年初数(元)
    */
    @ApiModelProperty(value = "负债合计_年初数(元)")
    private Double liabTotAboy;

    /**
    * 实收资本_年初数(元)
    */
    @ApiModelProperty(value = "实收资本_年初数(元)")
    private Double paidCptlAboy;

    /**
    * 其他权益工具_年初数(元)
    */
    @ApiModelProperty(value = "其他权益工具_年初数(元)")
    private Double othEquiInstAboy;

    /**
    * 资本公积_年初数(元)
    */
    @ApiModelProperty(value = "资本公积_年初数(元)")
    private Double cptlRsrvAboy;

    /**
    * 其他综合收益_年初数(元)
    */
    @ApiModelProperty(value = "其他综合收益_年初数(元)")
    private Double othCmphPayfAboy;

    /**
    * 专项储备_年初数(元)
    */
    @ApiModelProperty(value = "专项储备_年初数(元)")
    private Double spclRsrvAboy;

    /**
    * 盈余公积_年初数(元)
    */
    @ApiModelProperty(value = "盈余公积_年初数(元)")
    private Double surpRsrvAboy;

    /**
    * 一般风险准备_年初数(元)
    */
    @ApiModelProperty(value = "一般风险准备_年初数(元)")
    private Double riskPrepAboy;

    /**
    * 未分配利润_年初数(元)
    */
    @ApiModelProperty(value = "未分配利润_年初数(元)")
    private Double noAssnProfAboy;

    /**
    * 归属母公司所有者权益合计_年初数(元)
    */
    @ApiModelProperty(value = "归属母公司所有者权益合计_年初数(元)")
    private Double attrPrnCponEquiAboy;

    /**
    * 少数股东权益_年初数(元)
    */
    @ApiModelProperty(value = "少数股东权益_年初数(元)")
    private Double mishEquiAboy;

    /**
    * 所有者权益合计_年初数(元)
    */
    @ApiModelProperty(value = "所有者权益合计_年初数(元)")
    private Double toeqAboy;

    /**
    * 负债及所有者权益合计_年初数(元)
    */
    @ApiModelProperty(value = "负债及所有者权益合计_年初数(元)")
    private Double liabToeqAboy;

    /**
    * 货币资金_期末数(元)
    */
    @ApiModelProperty(value = "货币资金_期末数(元)")
    private Double crcpAeop;

    /**
    * 交易性金融资产_期末数(元)
    */
    @ApiModelProperty(value = "交易性金融资产_期末数(元)")
    private Double trdFinlAstAeop;

    /**
    * 衍生金融资产_期末数(元)
    */
    @ApiModelProperty(value = "衍生金融资产_期末数(元)")
    private Double devdFinlAstAeop;

    /**
    * 应收票据_期末数(元)
    */
    @ApiModelProperty(value = "应收票据_期末数(元)")
    private Double recvBillAeop;

    /**
    * 应收账款_期末数(元)
    */
    @ApiModelProperty(value = "应收账款_期末数(元)")
    private Double recvAmtAeop;

    /**
    * 应收款项融资_期末数(元)
    */
    @ApiModelProperty(value = "应收款项融资_期末数(元)")
    private Double recvAmtFinAeop;

    /**
    * 预付账款_期末数(元)
    */
    @ApiModelProperty(value = "预付账款_期末数(元)")
    private Double piaAmtAeop;

    /**
    * 其他应收款总计_期末数(元)
    */
    @ApiModelProperty(value = "其他应收款总计_期末数(元)")
    private Double othRecvAmtTotAeop;

    /**
    * 应收股利_期末数(元)
    */
    @ApiModelProperty(value = "应收股利_期末数(元)")
    private Double recvDivdAeop;

    /**
    * 应收利息_期末数(元)
    */
    @ApiModelProperty(value = "应收利息_期末数(元)")
    private Double recvIntrAeop;

    /**
    * 其他应收款_期末数(元)
    */
    @ApiModelProperty(value = "其他应收款_期末数(元)")
    private Double othRecvAmtAeop;

    /**
    * 存货_期末数(元)
    */
    @ApiModelProperty(value = "存货_期末数(元)")
    private Double invAeop;

    /**
    * 合同资产_期末数(元)
    */
    @ApiModelProperty(value = "合同资产_期末数(元)")
    private Double agmtAstAeop;

    /**
    * 持有待售资产_期末数(元)
    */
    @ApiModelProperty(value = "持有待售资产_期末数(元)")
    private Double holdSaleAstAeop;

    /**
    * 一年内到期的非流动资产_期末数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动资产_期末数(元)")
    private Double ncoyAeop;

    /**
    * 其他流动资产_期末数(元)
    */
    @ApiModelProperty(value = "其他流动资产_期末数(元)")
    private Double othLiqdAstAeop;

    /**
    * 流动资产合计_期末数(元)
    */
    @ApiModelProperty(value = "流动资产合计_期末数(元)")
    private Double liqdAstTotAeop;

    /**
    * 债权投资_期末数(元)
    */
    @ApiModelProperty(value = "债权投资_期末数(元)")
    private Double clamIvsmAeop;

    /**
    * 其他债权投资_期末数(元)
    */
    @ApiModelProperty(value = "其他债权投资_期末数(元)")
    private Double othClamIvsmAeop;

    /**
    * 长期应收款_期末数(元)
    */
    @ApiModelProperty(value = "长期应收款_期末数(元)")
    private Double longRecvAmtAeop;

    /**
    * 长期股权投资_期末数(元)
    */
    @ApiModelProperty(value = "长期股权投资_期末数(元)")
    private Double lsriAeop;

    /**
    * 其他权益工具投资_期末数(元)
    */
    @ApiModelProperty(value = "其他权益工具投资_期末数(元)")
    private Double othEquiInstIvsmAeop;

    /**
    * 其他非流动金融资产_期末数(元)
    */
    @ApiModelProperty(value = "其他非流动金融资产_期末数(元)")
    private Double othNocrFinlAstAeop;

    /**
    * 投资性房地产_期末数(元)
    */
    @ApiModelProperty(value = "投资性房地产_期末数(元)")
    private Double ivsmEsttAeop;

    /**
    * 固定资产_期末数(元)
    */
    @ApiModelProperty(value = "固定资产_期末数(元)")
    private Double fixAstAeop;

    /**
    * 在建工程_期末数(元)
    */
    @ApiModelProperty(value = "在建工程_期末数(元)")
    private Double udcsProjAeop;

    /**
    * 生产性生物资产_期末数(元)
    */
    @ApiModelProperty(value = "生产性生物资产_期末数(元)")
    private Double prodBiolMatrAeop;

    /**
    * 油气资产_期末数(元)
    */
    @ApiModelProperty(value = "油气资产_期末数(元)")
    private Double olgsAstAeop;

    /**
    * 使用权资产_期末数(元)
    */
    @ApiModelProperty(value = "使用权资产_期末数(元)")
    private Double useAstAeop;

    /**
    * 无形资产_期末数(元)
    */
    @ApiModelProperty(value = "无形资产_期末数(元)")
    private Double imtrAstAeop;

    /**
    * 开发支出_期末数(元)
    */
    @ApiModelProperty(value = "开发支出_期末数(元)")
    private Double devPayAeop;

    /**
    * 商誉_期末数(元)
    */
    @ApiModelProperty(value = "商誉_期末数(元)")
    private Double gdwlAeop;

    /**
    * 长期待摊费用_期末数(元)
    */
    @ApiModelProperty(value = "长期待摊费用_期末数(元)")
    private Double longTbatFeeAeop;

    /**
    * 递延所得税资产_期末数(元)
    */
    @ApiModelProperty(value = "递延所得税资产_期末数(元)")
    private Double defrTaxAstAeop;

    /**
    * 其他非流动资产_期末数(元)
    */
    @ApiModelProperty(value = "其他非流动资产_期末数(元)")
    private Double othNocrAstAeop;

    /**
    * 非流动资产合计_期末数(元)
    */
    @ApiModelProperty(value = "非流动资产合计_期末数(元)")
    private Double nocrAstTotAeop;

    /**
    * 资产总计_期末数(元)
    */
    @ApiModelProperty(value = "资产总计_期末数(元)")
    private Double astTotAeop;

    /**
    * 短期借款_期末数(元)
    */
    @ApiModelProperty(value = "短期借款_期末数(元)")
    private Double shttLoanAeop;

    /**
    * 交易性金融负债_期末数(元)
    */
    @ApiModelProperty(value = "交易性金融负债_期末数(元)")
    private Double trdFinlLiabAeop;

    /**
    * 衍生金融负债_期末数(元)
    */
    @ApiModelProperty(value = "衍生金融负债_期末数(元)")
    private Double devdFinlLiabAeop;

    /**
    * 应付票据_期末数(元)
    */
    @ApiModelProperty(value = "应付票据_期末数(元)")
    private Double paybBillAeop;

    /**
    * 应付账款_期末数(元)
    */
    @ApiModelProperty(value = "应付账款_期末数(元)")
    private Double paybAmtAeop;

    /**
    * 预收账款_期末数(元)
    */
    @ApiModelProperty(value = "预收账款_期末数(元)")
    private Double ciadAmtAeop;

    /**
    * 合同负债_期末数(元)
    */
    @ApiModelProperty(value = "合同负债_期末数(元)")
    private Double agmtLiabAeop;

    /**
    * 应付职工薪酬_期末数(元)
    */
    @ApiModelProperty(value = "应付职工薪酬_期末数(元)")
    private Double paybEmpCmpsAeop;

    /**
    * 应交税费_期末数(元)
    */
    @ApiModelProperty(value = "应交税费_期末数(元)")
    private Double paycTaxFeeAeop;

    /**
    * 其他应付款总计_期末数(元)
    */
    @ApiModelProperty(value = "其他应付款总计_期末数(元)")
    private Double othPaybAmtTotAeop;

    /**
    * 应付利息_期末数(元)
    */
    @ApiModelProperty(value = "应付利息_期末数(元)")
    private Double paybIntrAeop;

    /**
    * 应付股利_期末数(元)
    */
    @ApiModelProperty(value = "应付股利_期末数(元)")
    private Double paybDivdAeop;

    /**
    * 其他应付款_期末数(元)
    */
    @ApiModelProperty(value = "其他应付款_期末数(元)")
    private Double othPaybAmtAeop;

    /**
    * 持有待售负债_期末数(元)
    */
    @ApiModelProperty(value = "持有待售负债_期末数(元)")
    private Double holdSaleLiabAeop;

    /**
    * 一年内到期的非流动负债_期末数(元)
    */
    @ApiModelProperty(value = "一年内到期的非流动负债_期末数(元)")
    private Double oneyNocrLiabAeop;

    /**
    * 其他流动负债_期末数(元)
    */
    @ApiModelProperty(value = "其他流动负债_期末数(元)")
    private Double othLiqdLiabAeop;

    /**
    * 流动负债合计_期末数(元)
    */
    @ApiModelProperty(value = "流动负债合计_期末数(元)")
    private Double liqdLiabTotAeop;

    /**
    * 长期借款_期末数(元)
    */
    @ApiModelProperty(value = "长期借款_期末数(元)")
    private Double longLoanAeop;

    /**
    * 应付债券_期末数(元)
    */
    @ApiModelProperty(value = "应付债券_期末数(元)")
    private Double paybBondAeop;

    /**
    * 长期应付款_期末数(元)
    */
    @ApiModelProperty(value = "长期应付款_期末数(元)")
    private Double longPaybAmtAeop;

    /**
    * 长期应付职工薪酬_期末数(元)
    */
    @ApiModelProperty(value = "长期应付职工薪酬_期末数(元)")
    private Double longPaybEmpCmpsAeop;

    /**
    * 租赁负债_期末数(元)
    */
    @ApiModelProperty(value = "租赁负债_期末数(元)")
    private Double leasLiabAeop;

    /**
    * 预计负债_期末数(元)
    */
    @ApiModelProperty(value = "预计负债_期末数(元)")
    private Double expeLiabAeop;

    /**
    * 递延收益_期末数(元)
    */
    @ApiModelProperty(value = "递延收益_期末数(元)")
    private Double defrPayfAeop;

    /**
    * 递延所得税负债_期末数(元)
    */
    @ApiModelProperty(value = "递延所得税负债_期末数(元)")
    private Double defrInctLiabAeop;

    /**
    * 其他非流动负债_期末数(元)
    */
    @ApiModelProperty(value = "其他非流动负债_期末数(元)")
    private Double othNocrLiabAeop;

    /**
    * 非流动负债合计_期末数(元)
    */
    @ApiModelProperty(value = "非流动负债合计_期末数(元)")
    private Double nocrLiabTotAeop;

    /**
    * 负债合计_期末数(元)
    */
    @ApiModelProperty(value = "负债合计_期末数(元)")
    private Double liabTotAeop;

    /**
    * 实收资本_期末数(元)
    */
    @ApiModelProperty(value = "实收资本_期末数(元)")
    private Double paidCptlAeop;

    /**
    * 其他权益工具_期末数(元)
    */
    @ApiModelProperty(value = "其他权益工具_期末数(元)")
    private Double othEquiInstAeop;

    /**
    * 资本公积_期末数(元)
    */
    @ApiModelProperty(value = "资本公积_期末数(元)")
    private Double cptlRsrvAeop;

    /**
    * 其他综合收益_期末数(元)
    */
    @ApiModelProperty(value = "其他综合收益_期末数(元)")
    private Double othCmphPayfAeop;

    /**
    * 专项储备_期末数(元)
    */
    @ApiModelProperty(value = "专项储备_期末数(元)")
    private Double spclRsrvAeop;

    /**
    * 盈余公积_期末数(元)
    */
    @ApiModelProperty(value = "盈余公积_期末数(元)")
    private Double surpRsrvAeop;

    /**
    * 一般风险准备_期末数(元)
    */
    @ApiModelProperty(value = "一般风险准备_期末数(元)")
    private Double riskPrepAeop;

    /**
    * 未分配利润_期末数(元)
    */
    @ApiModelProperty(value = "未分配利润_期末数(元)")
    private Double noAssnProfAeop;

    /**
    * 归属母公司所有者权益合计_期末数(元)
    */
    @ApiModelProperty(value = "归属母公司所有者权益合计_期末数(元)")
    private Double attrPrnCponEquiAeop;

    /**
    * 少数股东权益_期末数(元)
    */
    @ApiModelProperty(value = "少数股东权益_期末数(元)")
    private Double mishEquiAeop;

    /**
    * 所有者权益合计_期末数(元)
    */
    @ApiModelProperty(value = "所有者权益合计_期末数(元)")
    private Double toeqAeop;

    /**
    * 负债及所有者权益合计_期末数(元)
    */
    @ApiModelProperty(value = "负债及所有者权益合计_期末数(元)")
    private Double liabToeqAeop;

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
    * 版本号
    */
    @ApiModelProperty(value = "版本号")
    private String version;

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

}
