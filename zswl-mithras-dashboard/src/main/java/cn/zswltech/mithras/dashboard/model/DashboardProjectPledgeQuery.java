package cn.zswltech.mithras.dashboard.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPledgeQuery extends CommonAuthQuery {
    private String projName;
    private String contractCode;
    private LocalDate planCollectionDateFrom;
    private LocalDate planCollectionDateTo;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private String pledgeStatus;
    private String financingStatus;
    private String financingCode;
    private List<Long> ids;
}
