package cn.zswltech.mithras.foundation.excel;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;

import java.io.OutputStream;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/19
 * @description
 */
public interface SimpleExcelExporter<T extends ExcelModel> {
    void exportExcel(List<T> dataList, OutputStream outputStream) throws Exception;
}
