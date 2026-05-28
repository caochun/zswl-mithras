package cn.zswltech.mithras.dto.afterlease;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/21 11:31
 */
@Data
public class AfterLeaseCheckExternalQueryDto {

    private LocalDateTime inspectionMonth;

    private String clientName;

    private Long sponsorUserId;

    private String approvalStatus;

    private List<Long> deptIdList;

    private Boolean isBizUser;

    private Long currentUserId;
}
