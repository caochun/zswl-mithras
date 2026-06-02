package cn.zswltech.mithras.ftp.newftp.excel;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 1年期SHIBOR利率
 * @date 2023-05-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NewFtpShiborInterestRateExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "指标名称", headerOrder = 10)
    private LocalDate date;

    @SimpleExcelHeader(headerName = "SHIBOR:1年", headerOrder = 20)
    private BigDecimal value;

}
