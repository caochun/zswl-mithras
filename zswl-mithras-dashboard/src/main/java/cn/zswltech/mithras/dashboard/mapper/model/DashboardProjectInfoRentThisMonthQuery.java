package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectInfoRentThisMonthQuery extends CommonAuthQuery {
    private String projName;
    private String contractCode;
    private LocalDate planCollectionDateFrom;
    private LocalDate planCollectionDateTo;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private List<Long> ids;
    private String permissionType;
}
