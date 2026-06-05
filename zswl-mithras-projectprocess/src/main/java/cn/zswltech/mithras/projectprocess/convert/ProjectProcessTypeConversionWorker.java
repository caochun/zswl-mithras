package cn.zswltech.mithras.projectprocess.convert;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.projectprocess.service.ProjectProcessNameResolver;
import com.alibaba.fastjson.JSON;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Project-process scoped conversions used by MapStruct converters.
 */
@Component
public class ProjectProcessTypeConversionWorker {

    @Autowired(required = false)
    private ProjectProcessNameResolver nameResolver;

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

    @Named("jsonStringToClientInfoList")
    public List<ClientInfo> jsonStringToClientInfoList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        List<ClientInfo> res = JSON.parseArray(jsonStr, ClientInfo.class);
        if (CollectionUtil.isNotEmpty(res) && nameResolver != null) {
            Map<Long, String> map = nameResolver.clientId2Name(res.stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
            for (ClientInfo clientInfo : res) {
                String clientName = map.get(clientInfo.getClientId());
                if (StrUtil.isNotBlank(clientName)) {
                    clientInfo.setClientName(clientName);
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
        return JSON.parseArray(jsonStr, String.class);
    }

    @Named("jsonStringToLongList")
    public List<Long> jsonStringToLongList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSON.parseArray(jsonStr, Long.class);
    }
}
