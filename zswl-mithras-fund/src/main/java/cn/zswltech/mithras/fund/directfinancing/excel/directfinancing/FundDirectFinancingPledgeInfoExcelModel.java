package cn.zswltech.mithras.fund.directfinancing.excel.directfinancing;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 10:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundDirectFinancingPledgeInfoExcelModel extends ExcelModel {
    /**
     * 质押编号
     */
    @SimpleExcelHeader(headerName = "质押编号", headerOrder = 10)
    private String pledgeCode;
    /**
     * 项目名称
     */
    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;
    /**
     * 合同编号
     */
    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 30)
    private String contractCode;
    /**
     * 合同金额
     */
    @SimpleExcelHeader(headerName = "合同金额", headerOrder = 40)
    private Long contractAmount;
    /**
     * 合同开始日期
     */
    @SimpleExcelHeader(headerName = "合同开始日期", headerOrder = 50)
    private LocalDate contractStartDate;
    /**
     * 合同结束日期
     */
    @SimpleExcelHeader(headerName = "合同结束日期", headerOrder = 60)
    private LocalDate contractEndDate;
    /**
     * 剩余未还本金
     */
    @SimpleExcelHeader(headerName = "剩余未还本金", headerOrder = 70)
    private Long remainingUnpaidPrincipal;

}
