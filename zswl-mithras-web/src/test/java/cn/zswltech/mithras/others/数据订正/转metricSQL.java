package cn.zswltech.mithras.others.数据订正;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

/**
 * @author luyi
 */
public class 转metricSQL {

    @Test
    public void test() {
        ExcelReader reader = ExcelUtil.getReader(Paths.get("/Users/luyi/Downloads/4.xlsx").toFile());
        String format = "update financial_cloud_metric_value set metric_value = '%s'" +
                " where data_time = '2023-10-01' and metric_code='%s';";
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < reader.getRowCount(); i++) {
            String metricCode = reader.getCell(0, i).getStringCellValue();
            String metricValue = reader.getCell(1, i).getStringCellValue();
            builder.append(String.format(format, metricValue, metricCode))
                    .append(System.lineSeparator());
        }
        System.out.println(builder.toString());


    }
}
