package cn.zswltech.mithras.service.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/11/2
 * @description
 */
@Service
public class LeaseItemCommonService {
    public List<String> preHandleHeader(List<String> oldHeaderList) {
        // 兼容处理，需求变更，需要忽略模板中的序号字段，为了统一新旧数据逻辑，这里统一先移除再手动加上
        oldHeaderList.removeIf(e -> Objects.equals(e, "序号"));
        List<String> newHeaderList = new LinkedList<>();
        newHeaderList.add("序号");
        newHeaderList.addAll(oldHeaderList);
        return newHeaderList;
    }

    public void preHandle(Map<String, Object> map) {
        map.remove("序号");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String headerName = entry.getKey();
            Object value = entry.getValue();
            if (Objects.isNull(value)) {
                continue;
            }
            if (StrUtil.equalsAny(headerName, "经度", "纬度")) {
                continue;
            }
            // 如果是四个金额类的先预处理一下
            if (StrUtil.equalsAny(headerName, "账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）")) {
                map.put(headerName, value.toString().replace(",", ""));
            }
            if (value instanceof DateTime) {
                map.put(headerName, LocalDateTimeUtil.format(((DateTime) value).toLocalDateTime(), DatePattern.NORM_DATE_PATTERN));
            }
            if (value instanceof Double) {
                map.put(headerName, BigDecimal.valueOf((Double) value).setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
            if (value instanceof Float) {
                map.put(headerName, BigDecimal.valueOf((Float) value).setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
        }
    }
}
