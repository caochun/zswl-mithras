package cn.zswltech.mithras.foundation.excel;

import cn.hutool.poi.excel.style.StyleUtil;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author dingqi
 * @date 2022/9/28
 * @description
 */
public class MyStyleUtil extends StyleUtil {
    public static CellStyle createMyMoneyCellStyle(Workbook workbook) {
        CellStyle moneyStyle = StyleUtil.createDefaultCellStyle(workbook);
        moneyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
        return moneyStyle;
    }

    public static CellStyle createMyDateCellStyle(Workbook workbook) {
        CellStyle dateStyle = StyleUtil.createDefaultCellStyle(workbook);
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
        return dateStyle;
    }
}