package cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjLifecycleListDTO {

    private Long id;

    private String projName;

    private Long projReviewId;

    private String key;

    private Long clientId;

    private String leaseTypes;

    private String bizType;

    private Long bizDeptId;

    private String dataType;

    private String projStatus;

    private String contractStatuses;

    private Long projSponsorUserId;

    private LocalDateTime createTime;

    private String projStage;

}
