package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 14:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractExcelModel extends ExcelModel{
    @SimpleExcelHeader(headerName = "主键", headerOrder = 10)
    private Long id;

    private String bizType;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 20)
    private String bizTypeDisplay;

    private String projectType;

    @SimpleExcelHeader(headerName = "项目类型", headerOrder = 30)
    private String projectTypeDisplay;

    private Long projSponsorUserId;

    @SimpleExcelHeader(headerName = "主办", headerOrder = 40)
    private String projSponsorUserName;

    private Long bizDeptId;

    @SimpleExcelHeader(headerName = "部门", headerOrder = 50)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 60)
    private String contractCode;

    private Long clientId;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 70)
    private String clientName;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 80)
    private String guarantors;

    @SimpleExcelHeader(headerName = "已投放金额（万元）", headerOrder = 90)
    private String paidAmount;

    @SimpleExcelHeader(headerName = "投放日", headerOrder = 100)
    private String firstPaidDate;

    @SimpleExcelHeader(headerName = "到期日", headerOrder = 110)
    private String dueDate;

    @SimpleExcelHeader(headerName = "利率类型", headerOrder = 120)
    private String rateType;

    @SimpleExcelHeader(headerName = "合同利率", headerOrder = 130)
    private String ratePercent;

    @SimpleExcelHeader(headerName = "IRR", headerOrder = 140)
    private String irrPercent;

    @SimpleExcelHeader(headerName = "合同期限（月）", headerOrder = 150)
    private Integer creditTerm;

    @SimpleExcelHeader(headerName = "租赁物账面原值（元）", headerOrder = 160)
    private String originalBookValue;

    @SimpleExcelHeader(headerName = "租赁物账面净值（元）", headerOrder = 170)
    private String originalBookNetValue;

    @SimpleExcelHeader(headerName = "租赁物评估原值（元）", headerOrder = 180)
    private String assessedValue;

    @SimpleExcelHeader(headerName = "租赁物评估净值（元）", headerOrder = 190)
    private String assessedNetValue;

    @SimpleExcelHeader(headerName = "保证金（万元）", headerOrder = 200)
    private String earnestMoney;

    @SimpleExcelHeader(headerName = "剩余本金（元）", headerOrder = 210)
    private String remainPrincipal;

    @SimpleExcelHeader(headerName = "剩余利息（元）", headerOrder = 220)
    private String remainInterest;
}
