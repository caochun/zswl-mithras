package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @create: 2022-08-22
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class MarginBaseInfoListExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "编号", headerOrder = 10)
    private String code;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 30)
    private String clientName;

    @SimpleExcelHeader(headerName = "收款日期", headerOrder = 40)
    private LocalDate collectionDate;

    @SimpleExcelHeader(headerName = "保证金金额", headerOrder = 50)
    private String marginAmount;

    @SimpleExcelHeader(headerName = "已退金额", headerOrder = 60)
    private String backAmount;

    @SimpleExcelHeader(headerName = "已抵扣金额", headerOrder = 70)
    private String deductAmount;

    @SimpleExcelHeader(headerName = "可退金额", headerOrder = 80)
    private String canBackAmount;
}
