package cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence;

import cn.zswltech.mithras.foundation.persistence.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjLifecycleListSelectParam extends BaseAuthDTO {

    private Long clientId;

    private String projName;

    private String bizType;

    private Long bizDeptId;

    private Long projSponsorUserId;

    private String projStage;

    private LocalDateTime createFrom;

    private LocalDateTime createTo;

}
