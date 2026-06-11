package cn.zswltech.mithras.leaseholdproperty.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yangxiong
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaseholdPropertyExcelModel extends ExcelModel implements Serializable {
    private static final long serialVersionUID = -545736935183734585L;

    @SimpleExcelHeader(headerName = "合同编号")
    private String contractCode;

    @SimpleExcelHeader(headerName = "租赁物类型")
    private String type;
}
