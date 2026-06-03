package cn.zswltech.mithras.workflow.application.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.flow.ProjNodeTimeMapper;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.flow.model.ProjNodeTime;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author luyi
 */
@Service
public class ProjNodeTimeService extends ServiceImpl<ProjNodeTimeMapper, ProjNodeTime> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TIME_JSON_TYPE = new TypeReference<Map<String, Object>>() {
    };

    public void saveKey(Long establishId, Integer establishType, Long reviewId, String key, LocalDateTime at, boolean override) {
        ProjNodeTime one = this.getOne(Wrappers.<ProjNodeTime>lambdaQuery()
                .eq(ProjNodeTime::getEstablishId, establishId)
                .eq(ProjNodeTime::getEstablishType, establishType));
        if (null == one) {
            one = new ProjNodeTime();
            one.setReviewId(reviewId);
            one.setEstablishId(establishId);
            one.setEstablishType(establishType);
            Map<String, Object> timeMap = new LinkedHashMap<>();
            timeMap.put(key, at.toString());
            one.setTimeJson(writeTimeJson(timeMap));
            save(one);
        } else {
            Map<String, Object> timeMap = readTimeJson(one.getTimeJson());
            if (null != timeMap.get(key) && !override) {
                return;
            }
            timeMap.put(key, at.toString());
            one.setReviewId(reviewId);
            one.setTimeJson(writeTimeJson(timeMap));
            updateById(one);
        }

    }

    private Map<String, Object> readTimeJson(String timeJson) {
        try {
            return OBJECT_MAPPER.readValue(timeJson, TIME_JSON_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid project node time json", e);
        }
    }

    private String writeTimeJson(Map<String, Object> timeMap) {
        try {
            return OBJECT_MAPPER.writeValueAsString(timeMap);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid project node time json", e);
        }
    }
}
