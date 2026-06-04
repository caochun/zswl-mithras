package cn.zswltech.mithras.projectprocess.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author dingqi
 * @date 2023/6/9
 * @description
 */
@Getter
public class ProjReviewApprovalPassEvent extends ApplicationEvent {
    private final Long projReviewId;

    public ProjReviewApprovalPassEvent(Long projReviewId) {
        super("ProjReviewApprovalPass");
        this.projReviewId = projReviewId;
    }
}
