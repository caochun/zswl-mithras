package cn.zswltech.mithras.ftp.oldftp;

import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/13 22:29
 */
public interface FtpGuidance {

    default int getNumericCellValue(Row row, int column) {
        Cell cell = row.getCell(column);
        if (null == cell || cell.getCellType().equals(CellType.BLANK)) {
            throw new MithrasException("导入失败，表格中存在未填写的单元格!");
        }
        try {
            double numericCellValue = cell.getNumericCellValue();
            double v = (double) Math.round(numericCellValue * 100) / 100;
            return (int) (v * 10000);
        }catch (NumberFormatException e){
            throw new MithrasException("导入失败，表格中存在格式不正确的数据");
        }
    }
}
