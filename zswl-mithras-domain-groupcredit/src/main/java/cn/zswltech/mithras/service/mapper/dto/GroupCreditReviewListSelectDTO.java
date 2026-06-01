package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
