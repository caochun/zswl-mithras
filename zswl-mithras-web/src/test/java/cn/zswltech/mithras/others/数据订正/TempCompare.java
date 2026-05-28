package cn.zswltech.mithras.others.数据订正;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author luyi
 */
public class TempCompare {

    @Test
    public void run() {
        ExcelReader r = ExcelUtil.getReader(new File("/Users/luyi/Desktop/contract_client.xlsx"));
        Set<String> set = new HashSet<>();
        for (int i = 0; i < r.getRowCount(); i++) {
            set.add((String) r.readRow(i).get(0));
        }
        System.out.println(set);

        Set<String> list = new HashSet<>();
        ExcelReader r1 = ExcelUtil.getReader(new File("/Users/luyi/Desktop/1.xlsx"));
        ExcelWriter w = ExcelUtil.getWriter(new File("/Users/luyi/Desktop/1.xlsx"));

        int rowCount = r1.getRowCount();
        for (int i = 1; i < rowCount; i++) {
            String uscCode = (String) r1.readRow(i).get(2);
            if (set.contains(uscCode.trim())) {
                list.add(uscCode.trim());
                try (ExcelWriter writer = r1.getWriter()) {
                    writer.writeCellValue(6, i, "是");
                }
            }
        }
        System.out.println(list);

    }
}
