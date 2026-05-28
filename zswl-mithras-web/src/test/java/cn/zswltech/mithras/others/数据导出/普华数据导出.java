package cn.zswltech.mithras.others.数据导出;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luyi
 */
public class 普华数据导出 {

    public static void main(String[] args) {
        Map<String, List<String>> map = new HashMap<>();
        Path path = Paths.get(System.getProperty("user.home"), "Desktop", "浙商租赁数据表提取需求_20231019.xlsx");
        ExcelReader reader = ExcelUtil.getReader(path.toFile());
        for (int i = 2; i < 124; i++) {
            List<Object> objects = reader.readRow(i);
            String tableName = ((String) objects.get(1)).trim();
            String fieldName = ((String) objects.get(2)).trim();
            boolean isExtract = objects.get(5).toString().trim().equals("是");
            if (isExtract) {
                map.putIfAbsent(tableName, new ArrayList<>());
                map.get(tableName).add(fieldName);
            }
        }
        StringBuilder builder = new StringBuilder();
        for (String tableName : map.keySet()) {
            builder.append("select ");
            for (String fieldName : map.get(tableName)) {
                builder.append(fieldName).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(" from ").append(tableName).append(";");
            builder.append(System.lineSeparator());
        }
        System.out.println(builder);
    }
}
