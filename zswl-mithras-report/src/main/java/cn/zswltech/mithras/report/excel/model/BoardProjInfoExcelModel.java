package cn.zswltech.mithras.report.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @create: 2022-12-14
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class BoardProjInfoExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 20)
    private String clientName;

    @SimpleExcelHeader(headerName = "省份", headerOrder = 30)
    private String province;

    @SimpleExcelHeader(headerName = "部门", headerOrder = 40)
    private String dept;

    @SimpleExcelHeader(headerName = "授信金额", headerOrder = 50)
    private String applyCreditAmount;

    @SimpleExcelHeader(headerName = "放款金额", headerOrder = 60)
    private String applyPaymentAmount;

    @SimpleExcelHeader(headerName = "IRR", headerOrder = 70)
    private String irrPercent;

    @SimpleExcelHeader(headerName = "立项时间", headerOrder = 80)
    private String projestablishTime;
}
