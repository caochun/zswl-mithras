package cn.zswltech.mithras.workflow.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.zswltech.mithras.workflow.persistence.mapper.flow.ContractNodeTimeMapper;
import cn.zswltech.mithras.workflow.persistence.model.flow.ContractNodeTime;
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
public class ContractNodeTimeService extends ServiceImpl<ContractNodeTimeMapper, ContractNodeTime> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TIME_JSON_TYPE = new TypeReference<Map<String, Object>>() {
    };

    public void saveKey(Long reviewId, Long contractId, String key, LocalDateTime at, boolean override) {
        ContractNodeTime one = this.getOne(Wrappers.<ContractNodeTime>lambdaQuery()
                .eq(ContractNodeTime::getContractId, contractId));
        if (null == one) {
            one = new ContractNodeTime();
            one.setReviewId(reviewId);
            one.setContractId(contractId);
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
            one.setTimeJson(writeTimeJson(timeMap));
            one.setReviewId(reviewId);
            updateById(one);
        }

    }

    private Map<String, Object> readTimeJson(String timeJson) {
        try {
            return OBJECT_MAPPER.readValue(timeJson, TIME_JSON_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid contract node time json", e);
        }
    }

    private String writeTimeJson(Map<String, Object> timeMap) {
        try {
            return OBJECT_MAPPER.writeValueAsString(timeMap);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid contract node time json", e);
        }
    }
}
