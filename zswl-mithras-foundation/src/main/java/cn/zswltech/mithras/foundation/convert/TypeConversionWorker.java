package cn.zswltech.mithras.foundation.convert;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/8 11:07
 */
@Component
public class TypeConversionWorker {
    @Named("toStringForYYYYMMDD")
    public String toStringForYYYYMMDD(LocalDate localDate) {
        if (Objects.isNull(localDate)) {
            return null;
        }
        return LocalDateTimeUtil.format(localDate, DatePattern.NORM_DATE_PATTERN);
    }

    @Named("toLocalDateForYYYYMMDD")
    public LocalDate toLocalDateForYYYYMMDD(String dateStr) {
        if (StrUtil.isBlank(dateStr)) {
            return null;
        }
        return LocalDateTimeUtil.parse(dateStr, DatePattern.NORM_DATE_PATTERN).toLocalDate();
    }

    /**
     * 对象转json字符串
     *
     * @param obj
     * @return
     */
    @Named("toJsonString")
    public String toJsonString(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    @Named("jsonStringToObject")
    public <T> T jsonStringToObject(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        T res = JSON.parseObject(jsonStr, new TypeReference<T>() {
        });
        return res;
    }

    @Named("startOfDay")
    public LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    @Named("endOfDay")
    public LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(23, 59, 59);
    }

    @Named("jsonStringToStringList")
    public List<String> jsonStringToStringList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        List<String> res = JSON.parseArray(jsonStr, String.class);
        return res;
    }

    @Named("jsonStringToLongList")
    public List<Long> jsonStringToLongList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        List<Long> res = JSON.parseArray(jsonStr, Long.class);
        return res;
    }

}
