package cn.zswltech.mithras.dashboard.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayNoSettleQuery extends CommonAuthQuery {
    private String projName;
    private Long clientId;
    private String contractCode;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private String regionalProjectClassifyCode;
    private List<Long> ids;
}
