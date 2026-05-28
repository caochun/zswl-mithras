package cn.zswltech.mithras.service.service.newftp.excel;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 10年期国债收益率导入对象
 * @date 2023-05-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NewFtpTreasuryBondYieldExcelModel extends ExcelModel {

    private static final long serialVersionUID = 1L;

    @SimpleExcelHeader(headerName = "指标名称", headerOrder = 10)
    private LocalDate date;

    @SimpleExcelHeader(headerName = "中国:10年期国债收益率", headerOrder = 20)
    private BigDecimal value;

}
