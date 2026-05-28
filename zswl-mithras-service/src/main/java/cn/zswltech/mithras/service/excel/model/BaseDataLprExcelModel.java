package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDataLprExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "LPR报价日")
    private LocalDate lprDate;

    @SimpleExcelHeader(headerName = "1年期")
    private Double oneYear;

    @SimpleExcelHeader(headerName = "5年期")
    private Double fiveYear;
}
