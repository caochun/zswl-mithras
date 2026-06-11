package cn.zswltech.mithras.dashboard.model;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Data
public class DashboardDeptUserBasicResult {
    private Long bizDeptId;
    private Long projSponsorUserId;
    private String projCosponsorUserIdsJson;
}
