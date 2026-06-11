package cn.zswltech.mithras.third.dataminer.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description
 */
@Getter
@AllArgsConstructor
public enum DataMinerApiInfoEnum {
    QUERY_DM_REGION_SCORE("/api/open/service/queryDmRegionScore", false),
    QUERY_DM_INDICATOR("/api/open/service/queryDmIndicator", false);

    private final String uri;
    private final boolean isPageable;
}
