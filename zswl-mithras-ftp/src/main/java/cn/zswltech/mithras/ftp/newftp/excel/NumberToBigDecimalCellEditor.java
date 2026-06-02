package cn.zswltech.mithras.ftp.newftp.excel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/21 13:14
 */
public class NumberToBigDecimalCellEditor implements CellEditor {
    @Override
    public Object edit(Cell cell, Object value) {
        if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
            BigDecimal bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.setScale(4, RoundingMode.HALF_UP);
        }
        if (value instanceof String) {
            if (StrUtil.isBlank(value.toString())) {
                return null;
            }
        }
        return value;
    }
}
