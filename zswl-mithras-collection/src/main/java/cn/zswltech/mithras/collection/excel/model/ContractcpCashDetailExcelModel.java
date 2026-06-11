package cn.zswltech.mithras.collection.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合同收付款 详情列表
 *
 * @author wangchuanhao
 * @date 2022/8/22 4:38 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractcpCashDetailExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "状态", headerOrder = 10)
    private String writeOffStatus;

    @SimpleExcelHeader(headerName = "编号", headerOrder = 20)
    private String rentCode;

    @SimpleExcelHeader(headerName = "日期", headerOrder = 30)
    private String rentDate;

    @SimpleExcelHeader(headerName = "期项", headerOrder = 40)
    private Integer phase;

    @SimpleExcelHeader(headerName = "现金流项目", headerOrder = 50)
    private String cashItem;

    @SimpleExcelHeader(headerName = "现金流金额", headerOrder = 60)
    private String cashFlowAmount;

    @SimpleExcelHeader(headerName = "本金", headerOrder = 70)
    private String principal;

    @SimpleExcelHeader(headerName = "利息", headerOrder = 80)
    private String interest;

    @SimpleExcelHeader(headerName = "罚息", headerOrder = 90)
    private String penaltyInterest;

}
