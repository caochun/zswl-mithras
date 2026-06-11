package cn.zswltech.mithras.credit.groupcredit.review.mapper.dto;

import cn.zswltech.mithras.foundation.persistence.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author junke
 */
@Data
public class GroupCreditReviewListSelectDTO extends BaseAuthDTO {

    private Long clientId;

    private String projName;

    private String projCode;

    private Long bizDeptId;

    private Long projSponsorUserId;

    private String groupCreditReviewStatus;

    private String groupCreditReviewProcessStatus;

    private LocalDateTime createFrom;

    private LocalDateTime createTo;

    private LocalDateTime updateFrom;

    private LocalDateTime updateTo;

    private Long creditAmountFrom;

    private Long creditAmountTo;

}
