package cn.zswltech.mithras.associationreport.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.zswltech.mithras.associationreport.mapper.AssociationDictionaryMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationDictionary;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/24
 * @description
 */
@Slf4j
@Service
public class AssociationDictionaryService extends ServiceImpl<AssociationDictionaryMapper, AssociationDictionary> {
    // 本地缓存15min，枚举值几乎不变动，暂时没有更新机制
    private static final Cache<String, Map<String, Map<String, String>>> localCache = new TimedCache<>(15 * 60 * 1000);
    private static final String DISPLAY_2_CODE_MAP_LOCAL_CACHE_KEY = "DISPLAY_2_CODE_MAP";
    private static final String CODE_2_DISPLAY_MAP_LOCAL_CACHE_KEY = "CODE_2_DISPLAY_MAP";

    public Map<String, Map<String, String>> getDisplay2CodeMap() {
        Map<String, Map<String, String>> result = localCache.get(DISPLAY_2_CODE_MAP_LOCAL_CACHE_KEY);
        if (Objects.nonNull(result)) {
            return result;
        }
        List<AssociationDictionary> all = this.list();
        Map<String, List<AssociationDictionary>> map = all.stream().collect(Collectors.groupingBy(AssociationDictionary::getCategoryCode));
        result = new HashMap<>();
        for (Map.Entry<String, List<AssociationDictionary>> entry : map.entrySet()) {
            Map<String, String> m = entry.getValue().stream().collect(Collectors.toMap(AssociationDictionary::getItemName, AssociationDictionary::getItemCode));
            result.put(entry.getKey(), m);
        }
        // 放入缓存
        localCache.put(DISPLAY_2_CODE_MAP_LOCAL_CACHE_KEY, result);
        return result;
    }

    public Map<String, Map<String, String>> getCode2DisplayMap() {
        Map<String, Map<String, String>> result = localCache.get(CODE_2_DISPLAY_MAP_LOCAL_CACHE_KEY);
        if (Objects.nonNull(result)) {
            return result;
        }
        List<AssociationDictionary> all = this.list();
        Map<String, List<AssociationDictionary>> map = all.stream().collect(Collectors.groupingBy(AssociationDictionary::getCategoryCode, LinkedHashMap::new, Collectors.toList()));
        result = new LinkedHashMap<>();
        for (Map.Entry<String, List<AssociationDictionary>> entry : map.entrySet()) {
            Map<String, String> m = entry.getValue().stream().collect(Collectors.toMap(AssociationDictionary::getItemCode, AssociationDictionary::getItemName,
                    (k1, k2) -> k1, LinkedHashMap::new));
            result.put(entry.getKey(), m);
        }
        // 放入缓存
        localCache.put(CODE_2_DISPLAY_MAP_LOCAL_CACHE_KEY, result);
        return result;
    }
}
