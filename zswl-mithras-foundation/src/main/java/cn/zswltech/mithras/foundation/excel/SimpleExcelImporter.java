package cn.zswltech.mithras.foundation.excel;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;

import java.io.InputStream;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
public interface SimpleExcelImporter<T extends ExcelModel> {
    List<T> parse(InputStream inputStream);
}
