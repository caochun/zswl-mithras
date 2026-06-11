package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.dto.dashboard.DashboardProjectBasicRSP;
import cn.zswltech.mithras.dashboard.mapper.model.DashboardProjectBasicResult;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
public interface DashboardProjectStageCustomConvert<T extends DashboardProjectBasicRSP, M extends DashboardProjectBasicResult> {
    T convert(M dbResult);
}
