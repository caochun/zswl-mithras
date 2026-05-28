package cn.zswltech.mithras.service.service.dashboard;

import cn.zswltech.mithras.dto.dashboard.DashboardProjectBasicRSP;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardProjectBasicResult;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
public interface DashboardProjectStageCustomConvert<T extends DashboardProjectBasicRSP, M extends DashboardProjectBasicResult> {
    T convert(M dbResult);
}
