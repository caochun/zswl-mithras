package cn.zswltech.mithras.service.util;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListRSP;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @ClassName DashboardHelp
 * @Description 工作台辅助类
 * @Author jackerhe
 * @Date 2024/7/3 9:02 上午
 * @Version 1.0
 **/
public class DashboardHelpUtil {

    public static final String RECORDS = "records";

    public static final String SUM_DATE = "sumData";

    public static final String AVERAGE_DATA = "averageData";

    public static <T> T countValueUnitDTO(List<T> list, T result) throws Exception {
        return countValueUnitDTO(list, result, null);
    }

    public static <T> T countValueUnitDTO(List<T> list, T result, Set<String> ignoreFieldSet) throws Exception {
        if (list == null || list.isEmpty()) {
            return null;
        }
        // 获取第一个对象的类类型
        Class<?> clazz = list.get(0).getClass();
        // 遍历所有字段
        for (Field field : clazz.getDeclaredFields()) {
            if (!CollectionUtils.isEmpty(ignoreFieldSet) && ignoreFieldSet.contains(field.getName())) {
                continue;
            }
            if (field.getType().equals(ValueUnitDTO.class)) {
                field.setAccessible(true);
                ValueUnitDTO rsp = new ValueUnitDTO();
                BigDecimal sum = BigDecimal.ZERO;
                for (T item : list) {
                    ValueUnitDTO dto = (ValueUnitDTO) field.get(item);
                    if (ObjectUtil.isEmpty(dto)) {
                        continue;
                    }
                    BigDecimal value = new BigDecimal(ObjectUtil.isEmpty(dto.getValue()) ? "0" : dto.getValue());
                    if (ObjectUtil.isEmpty(rsp.getUnit())) {
                        rsp.setUnit(dto.getUnit());
                    }
                    if (ObjectUtil.isNotEmpty(value)) {
                        sum = sum.add(value);
                    }
                }
                rsp.setValue(sum.toPlainString());
                field.set(result, rsp);
            }
        }
        return result;
    }

    public static <T> T countString(List<T> list, Set<String> countFieldSet, T result) throws Exception {
        if (list == null || list.isEmpty() || countFieldSet == null) {
            return result;
        }
        // 获取第一个对象的类类型
        Class<?> clazz = list.get(0).getClass();
        // 遍历所有字段
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(String.class) && countFieldSet.contains(field.getName())) {
                field.setAccessible(true);
                BigDecimal sum = BigDecimal.ZERO;
                for (T item : list) {
                    Object o = field.get(item);
                    BigDecimal value = new BigDecimal(ObjectUtil.isEmpty(o) ? "0" : (String) o);
                    if (ObjectUtil.isNotEmpty(value)) {
                        sum = sum.add(value);
                    }
                }
                field.set(result, sum.toPlainString());
            }
        }
        return result;
    }

    public static <T> T averageValue(List<T> list, T sumData, T result) throws Exception{
        if (list == null || list.isEmpty()) {
            return null;
        }
        int size = list.size();
        Class<?> clazz = sumData.getClass();
        // 遍历所有字段
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object obj = field.get(sumData);
            if(obj == null){
                continue;
            }
            if (field.getType().equals(ValueUnitDTO.class)) {
                ValueUnitDTO rsp = new ValueUnitDTO();
                ValueUnitDTO dto = (ValueUnitDTO) obj;
                BigDecimal value = new BigDecimal(ObjectUtil.isEmpty(dto.getValue()) ? "0" : dto.getValue());
                BigDecimal average = value.divide(new BigDecimal(size), 2, RoundingMode.HALF_UP);
                rsp.setUnit(dto.getUnit());
                rsp.setValue(average.toPlainString());
                field.set(result, rsp);
            }
            if (field.getType().equals(String.class)) {
                BigDecimal value = new BigDecimal(ObjectUtil.isEmpty(obj) ? "0" : (String) obj);
                BigDecimal average = value.divide(new BigDecimal(size), 2, RoundingMode.HALF_UP);
                field.set(result, average.toPlainString());
            }
        }
        return result;
    }
}

