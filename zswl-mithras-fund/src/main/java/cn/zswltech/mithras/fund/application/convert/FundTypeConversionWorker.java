package cn.zswltech.mithras.fund.application.convert;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.fund.AccountInfo;
import cn.zswltech.mithras.dto.fund.AddressInfo;
import cn.zswltech.mithras.dto.fund.ContactInfo;
import com.alibaba.fastjson.JSON;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class FundTypeConversionWorker {

    @Named("toLocalDateForYYYYMMDD")
    public LocalDate toLocalDateForYYYYMMDD(String dateStr) {
        if (StrUtil.isBlank(dateStr)) {
            return null;
        }
        return LocalDateTimeUtil.parse(dateStr, DatePattern.NORM_DATE_PATTERN).toLocalDate();
    }

    @Named("toJsonString")
    public String toJsonString(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JSON.toJSONString(obj);
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
        return JSON.parseArray(jsonStr, String.class);
    }

    @Named("jsonStringToContactInfo")
    public ContactInfo jsonStringToContactInfo(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSON.parseObject(jsonStr, ContactInfo.class);
    }

    @Named("jsonStringToAddressInfo")
    public AddressInfo jsonStringToAddressInfo(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSON.parseObject(jsonStr, AddressInfo.class);
    }

    @Named("jsonStringToAccountInfoList")
    public List<AccountInfo> jsonStringToAccountInfoList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSON.parseArray(jsonStr, AccountInfo.class);
    }
}
