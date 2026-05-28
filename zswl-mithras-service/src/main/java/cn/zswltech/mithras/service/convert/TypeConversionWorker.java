package cn.zswltech.mithras.service.convert;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.fund.AccountInfo;
import cn.zswltech.mithras.dto.fund.AddressInfo;
import cn.zswltech.mithras.dto.fund.ContactInfo;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordAreaType;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordOptionGrade;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * json字符串转对象
     *
     * @param jsonStr
     * @return
     */
    @Named("jsonStringToClientInfoList")
    public List<ClientInfo> jsonStringToClientInfoList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        List<ClientInfo> res = JSON.parseArray(jsonStr, ClientInfo.class);
        if (CollectionUtil.isNotEmpty(res)) {
            // 查询最新信息
            Map<Long, String> map = SpringUtil.getBean(Id2NameService.class).clientId2Name(res.stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
            for (ClientInfo clientInfo : res) {
                String s = map.get(clientInfo.getClientId());
                if (StrUtil.isNotBlank(s)) {
                    clientInfo.setClientName(s);
                }
            }
        }
        return res;
    }

    @Named("jsonStringToPersonInfoList")
    public List<ProjEstablishPersonInfo> jsonStringToPersonInfoList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        List<ProjEstablishPersonInfo> res = JSON.parseArray(jsonStr, ProjEstablishPersonInfo.class);
        if (CollectionUtil.isNotEmpty(res)) {
            // 查询最新信息
            Map<Long, String> map = SpringUtil.getBean(Id2NameService.class).clientId2Name(res.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList()));
            for (ProjEstablishPersonInfo projEstablishPersonInfo : res) {
                String s = map.get(projEstablishPersonInfo.getClientId());
                if (StrUtil.isNotBlank(s)) {
                    projEstablishPersonInfo.setClientName(s);
                }
            }
        }
        return res;
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

    @Named("jsonStringToContactInfo")
    public ContactInfo jsonStringToContactInfo(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        ContactInfo res = JSON.parseObject(jsonStr, ContactInfo.class);
        return res;
    }

    @Named("jsonStringToAddressInfo")
    public AddressInfo jsonStringToAddressInfo(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        AddressInfo res = JSON.parseObject(jsonStr, AddressInfo.class);
        return res;
    }

    @Named("jsonStringToAccountInfoList")
    public List<AccountInfo> jsonStringToAccountInfoList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        List<AccountInfo> res = JSON.parseArray(jsonStr, AccountInfo.class);
        return res;
    }
    @Named("jsonStringToRiskControlScoreCordAreaList")
    public List<RiskControlScoreCordAreaType> jsonStringToRiskControlScoreCordAreaList(String jsonStr){
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        List<RiskControlScoreCordAreaType> res = JSON.parseArray(jsonStr, RiskControlScoreCordAreaType.class);
        return res;
    }

    @Named("jsonStringToRiskControlScoreCordOptionGradeType")
    public RiskControlScoreCordOptionGrade jsonStringToRiskControlScoreCordOptionGradeType(String jsonStr){
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        RiskControlScoreCordOptionGrade res = JSON.parseObject(jsonStr, RiskControlScoreCordOptionGrade.class);
        return res;
    }

}
