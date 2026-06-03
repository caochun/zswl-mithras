package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

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
public class DashboardProvisionQuery extends CommonAuthQuery {
    private Long clientId;
    private String contractCode;
    private Long bizDeptId;
    private List<Long> ids;
}
