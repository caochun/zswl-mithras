package cn.zswltech.mithras.monthly.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class MonthlyDutyProjExcelModel extends ExcelModel {


    @SimpleExcelHeader(headerName = "月份", headerOrder = 0)
    private String yearAndMonth;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 1)
    private String clientName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 2)
    private String contractCode;

    @SimpleExcelHeader(headerName = "本月计提印花税/元", headerOrder = 3)
    private BigDecimal stampDutyExcel;
    
}
