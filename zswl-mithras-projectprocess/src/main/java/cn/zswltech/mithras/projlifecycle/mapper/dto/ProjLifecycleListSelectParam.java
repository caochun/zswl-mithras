package cn.zswltech.mithras.projlifecycle.mapper.dto;

import cn.zswltech.mithras.service.mapper.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
