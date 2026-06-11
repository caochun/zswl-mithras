package cn.zswltech.mithras.projectprocess.dto.persistence;

import cn.zswltech.mithras.foundation.persistence.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 */
@Data
public class ProjPricingListSelectDTO extends BaseAuthDTO {

    private Long clientId;

    private String projName;

    private String projCode;

    private String bizType;

    private Long bizDeptId;

    private Long projSponsorUserId;

    private Long projCosponsorUserId;

    private String projPricingStatus;

    private String projPricingProcessStatus;

    private LocalDateTime createFrom;

    private LocalDateTime createTo;

    private LocalDateTime updateFrom;

    private LocalDateTime updateTo;

}
