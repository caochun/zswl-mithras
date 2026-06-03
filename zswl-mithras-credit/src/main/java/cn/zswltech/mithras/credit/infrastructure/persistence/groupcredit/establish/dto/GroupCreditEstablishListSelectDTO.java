package cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.dto;

import cn.zswltech.mithras.service.mapper.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author junke
 */
@Data
public class GroupCreditEstablishListSelectDTO extends BaseAuthDTO {

    private Long clientId;

    private String projName;

    private String projCode;

    private Long bizDeptId;

    private Long projSponsorUserId;

    private String groupCreditEstablishStatus;

    private String groupCreditEstablishProcessStatus;

    private LocalDateTime createFrom;

    private LocalDateTime createTo;

    private LocalDateTime updateFrom;

    private LocalDateTime updateTo;

    private Long creditAmountFrom;

    private Long creditAmountTo;

}
