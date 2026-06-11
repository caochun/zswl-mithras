package cn.zswltech.mithras.customer.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author zhouning
 */
@Data
@Accessors(chain = true)
public class VisitRecordListParam {

    private String clientName;
    private LocalDateTime visitTimeFrom;
    private LocalDateTime visitTimeTo;
    private List<String> visitWays;
    private List<String> visitTypes;
    private List<String> visitPhases;
    private List<String> statuses;
    private List<Long> clientIds;
    private List<Long> userIds;

    //可查看的
    private Long belongDeptId;
    private Long belongSponsorId;

    // 客户全周期新增
    private Set<Long> targetVisitRecordIds;
}
