package cn.zswltech.mithras.fund.excel.model;

import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 资金收付款批量审批界面 付款列表
 *
 * @author wangchuanhao
 * @date 2022/8/23 3:31 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundReceiptRepayBatchPlanExcelModel extends ExcelModel {

    /**
     * 收付款编号
     */
    @SimpleExcelHeader(headerName = "现金流编号", headerOrder = 10)
    private String receiptRepayCode;

    /**
     * 融资机构名称
     */
    @SimpleExcelHeader(headerName = "融资机构", headerOrder = 20)
    private String financingOrgName;

    /**
     * 融资金额(元)
     */
    @SimpleExcelHeader(headerName = "融资金额", headerOrder = 30)
    private String financingAmount;

    /**
     * 累计已还本金(元)
     */
    @SimpleExcelHeader(headerName = "累计已还本金(元)", headerOrder = 40)
    private String paidPrincipal;

    /**
     * 累计已还利息(元)
     */
    @SimpleExcelHeader(headerName = "累计已还利息(元)", headerOrder = 50)
    private String paidInterest;

    /**
     * 配套项目名称
     */
    @SimpleExcelHeader(headerName = "配套项目名称", headerOrder = 60)
    private String projNameList;

    /**
     * 质押合同编号
     */
    @SimpleExcelHeader(headerName = "质押合同编号", headerOrder = 70)
    private String pledgeContractCodeList;

    /**
     * 借款日期
     */
    @SimpleExcelHeader(headerName = "借款日期", headerOrder = 80)
    private String borrowingDate;

    /**
     * 到期日期
     */
    @SimpleExcelHeader(headerName = "到期日期", headerOrder = 90)
    private String expirationDate;

    /**
     * 本月计划还款合计(元)
     */
    @SimpleExcelHeader(headerName = "本月计划还款合计(元)", headerOrder = 100)
    private String planedRepayAmount;

    /**
     * 本月计划还款本金(元)
     */
    @SimpleExcelHeader(headerName = "本月计划还款本金(元)", headerOrder = 110)
    private String planedRepayPrincipal;

    /**
     * 本月计划还款利息(元)
     */
    @SimpleExcelHeader(headerName = "本月计划还款利息(元)", headerOrder = 130)
    private String planedRepayInterest;

    /**
     * 计划还本日
     */
    @SimpleExcelHeader(headerName = "计划还本日", headerOrder = 120)
    private String planedRepayPrincipleDate;

    /**
     * 计划还息日
     */
    @SimpleExcelHeader(headerName = "计划还息日", headerOrder = 140)
    private String planedRepayInterestDate;

    private Long receiptRepayId;

}
