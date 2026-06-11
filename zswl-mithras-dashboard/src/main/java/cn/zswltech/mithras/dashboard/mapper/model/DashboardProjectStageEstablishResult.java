package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageEstablishResult extends DashboardProjectBasicResult {
    private String projectEstablishType;
    private String projectEstablishStatus;
    private String projectEstablishProcessStatus;
}
