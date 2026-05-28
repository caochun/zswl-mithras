package cn.zswltech.mithras.others;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/8/23
 * @description
 */
@Slf4j
public class AddressImportTest extends ApplicationTest {
    @Test
    public void generateSqlTest() {
        String filePath = "/Users/mockorz/Downloads/area_info_副本.xlsx";
        List<Map<String, Object>> originDataList = ExcelUtil.getReader(filePath).read(0, 1, 3208);
        // 转对象
        List<DataObject> dataList = originDataList.stream().map(e -> {
            DataObject dataObject = new DataObject();
            dataObject.setAreaUniCode(e.get("area_uni_code").toString());
            dataObject.setAreaName(e.get("area_name").toString());
            dataObject.setParentAreaUniCode(e.get("parent_area_uni_code").toString());
            dataObject.setParentAreaName(e.get("parent_area_name").toString());
            dataObject.setAreaType(Integer.valueOf(e.get("area_type").toString()));
            dataObject.setGbCode(Long.valueOf(e.get("administrative_code").toString()));
            return dataObject;
        }).collect(Collectors.toList());
        // 按照unicode转map
        Map<String, DataObject> dataMap = dataList.stream().collect(Collectors.toMap(DataObject::getAreaUniCode, e -> e));
        // 循环生成sql脚本
        List<String> dynamicSqlList = new LinkedList<>();
        for (DataObject dataObject : dataList) {
            Long id = dataObject.getGbCode();
            String code = dataObject.getGbCode().toString();
            String display = dataObject.getAreaName();
            Long parentId;
            Integer level = dataObject.getAreaType() + 1;
            if (Objects.equals(dataObject.getAreaType(), 1)) {
                // 省份的父节点写死数据库的中国
                parentId = 46L;
            } else {
                // 从dataMap查找到父节点信息
                DataObject parent = dataMap.get(dataObject.getParentAreaUniCode());
                if (Objects.isNull(parent)) {
                    log.error(dataObject.getAreaUniCode(), dataObject.getAreaName());
                    continue;
                }
                parentId = parent.getGbCode();
            }
            dynamicSqlList.add(String.format("(%s, %s, '%s', %s, %s)", id, code, display, parentId, level));
        }
        String stringBuilder =
                "insert into address_dictionary_new \n" +
                "(id, code, display, parent_id, level) \n" +
                "values \n" +
                CharSequenceUtil.join(", \n", dynamicSqlList) +
                ";";
        // ！！！重要，生成的sql执行后不能直接使用，需要进行一定的加工
        // 比如Excel数据中，直辖市下的城市直接就是xx区，但是系统已有的老数据会在直辖市下挂一个市辖区的城市，所有的区挂在这个市辖区下（猜测是为了保证直辖市也有到区的三级）
        FileUtil.writeString(stringBuilder, "/Users/mockorz/init_area.sql", StandardCharsets.UTF_8);
    }

    @Data
    private static class DataObject {
        private String areaUniCode;
        private String areaName;
        private String parentAreaUniCode;
        private String parentAreaName;
        private Integer areaType;
        private Long gbCode;
    }
}
