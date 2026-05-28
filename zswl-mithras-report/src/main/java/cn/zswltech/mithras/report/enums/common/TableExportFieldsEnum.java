package cn.zswltech.mithras.report.enums.common;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author bigbear
 * @date 2025/5/27 15:23
 * @description 表导出字段集合枚举
 */
@Getter
@AllArgsConstructor
public enum TableExportFieldsEnum {
    CLIENT(ListUtil.of(
            Pair.of("clientCode", "客户编号"),
            Pair.of("clientName", "客户名称"),
            Pair.of("zhongZhengCode", "中征码"),
            Pair.of("continuousStatus", "存续状态"),
            Pair.of("orgType", "机构类型"),
            Pair.of("registerAddress", "注册地址"),
            Pair.of("regionCode", "行政区划"),
            Pair.of("establishDate", "成立日期"),
            Pair.of("bizLicenseEndDate", "营业执照到期日"),
            Pair.of("bizScope", "经营范围"),
            Pair.of("industryTypeName", "行业类型"),
            Pair.of("economyType", "经济类型"),
            Pair.of("orgScale", "企业规模"),
            Pair.of("registerCurrencyType", "注册资本币种"),
            Pair.of("registerCapital", "注册资本(元)"),
            Pair.of("corpRepresent", "法人代表"),
            Pair.of("corpCertType", "法人证件类型"),
            Pair.of("corpCertCode", "法人证件号码"),
            Pair.of("effectDate", "数据更新日期")
    )),

    MORTGAGE(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("applyPaymentAmount", "借据本金(元)"),
            Pair.of("mortgageContractCode", "抵押合同编号"),
            Pair.of("mortgageContractCode2", "抵押合同编号(简)"),
            Pair.of("mortgageType", "抵押人类型"),
            Pair.of("mortgageName", "抵押人名称"),
            Pair.of("mortgageIdType", "抵押人身份标识类型"),
            Pair.of("mortgageId", "抵押人身份标识号码"),
            Pair.of("maxFlag", "最高额担保标识"),
            Pair.of("appraisalCompanyType", "评估机构类型"),
            Pair.of("assessedDate", "评估日期"),
            Pair.of("mortgageDescribe", "抵押物描述"),
            Pair.of("sequence", "序号"),
            Pair.of("type", "抵押物种类"),
            Pair.of("modelType", "抵押物识别号类型"),
            Pair.of("model", "抵押物唯一识别号"),
            Pair.of("assessedValue", "评估价值")
    )),

    PLEDGE(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("applyPaymentAmount", "借据本金"),
            Pair.of("pledgeContractCode", "质押合同编号"),
            Pair.of("pledgeContractCode2", "质押合同编号(简)"),
            Pair.of("pledgeType", "出质人类型"),
            Pair.of("pledgeName", "出质人名称"),
            Pair.of("pledgeIdType", "出质人身份标识类型"),
            Pair.of("pledgeId", "出质人身份标识号码"),
            Pair.of("maxFlag", "最高额担保标识"),
            Pair.of("sequence", "序号"),
            Pair.of("type", "质押物种类"),
            Pair.of("assessedValue", "质物价值")
    )),


    GUARANTOR(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("guaranteContractCode", "保证合同编号"),
            Pair.of("guaranteContractCode2", "保证合同编号(简)"),
            Pair.of("clientName", "客户名称"),
            Pair.of("clientType", "客户分类"),
            Pair.of("guarantorIdType", "身份标识类型"),
            Pair.of("guarantorId", "身份标识号码"),
            Pair.of("clientClass", "客户类型"),
            Pair.of("repayLiabilityAmount", "还款责任金额(元)"),
            Pair.of("jointGuarantorFlag", "联保标志")
    )),

    ACCOUNT(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("clientName", "客户名称"),
            Pair.of("bizType", "业务类型"),
            Pair.of("rentalCalcType", "租金计算方式"),
            Pair.of("repayRate", "还款频率"),
            Pair.of("paymentAmount", "借款金额(元)"),
            Pair.of("earnestMoney", "保证金金额(元)"),
            Pair.of("projLeaseMonthCount", "借款期限(月)"),
            Pair.of("lendingDate", "放款日期"),
            Pair.of("closedDate", "结清日期"),
            Pair.of("expirationDate", "到期日期")
    )),

    REPAY(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("clientName", "客户名称"),
            Pair.of("phase", "期项"),
            Pair.of("cashFlowDate", "应收日期"),
            Pair.of("gracePeriod", "宽限期(天)"),
            Pair.of("rent", "应收租金"),
            Pair.of("principal", "应收本金"),
            Pair.of("payDate", "收款日期"),
            Pair.of("collectionAmount", "收款金额(元)"),
            Pair.of("collectionPrincipal", "实收本金(元)")
            )),


    SPECIAL_TRADE(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("clientName", "客户名称"),
            Pair.of("tradeType", "交易类型"),
            Pair.of("tradeDate", "交易日期"),
            Pair.of("tradeAmount", "交易金额(元)"),
            Pair.of("changeMonthCount", "到期日变更月数")
    )),


    OVERDUE_RECORD(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("clientName", "客户名称"),
            Pair.of("overduePrincipal", "逾期本金(元)"),
            Pair.of("overdueDay", "逾期天数(天)"),
            Pair.of("overdueTotal", "逾期总额(元)"),
            Pair.of("overdueChangeDate", "逾期改变日期")
    )),

    FIVE_CLASS(ListUtil.of(
            Pair.of("paymentApplyCode", "编号"),
            Pair.of("contractCode", "合同编号"),
            Pair.of("clientName", "客户名称"),
            Pair.of("fiveClass", "五级分类"),
            Pair.of("identificationDate", "五级分类认定日期")
    ));

    /**
     * 表字段集合 key:表字段 value:表头字段
     */
    private final List<Pair<String, String>> fields;

    /**
     * 根据名称获取
     */
    public static TableExportFieldsEnum getByName(String name) {
        for (TableExportFieldsEnum value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
