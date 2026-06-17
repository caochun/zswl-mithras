package cn.zswltech.mithras.payment.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentWorkflowProcessSnapshot {

    private String processInstanceId;

    private String modelKey;

    private String curTaskActivityIds;

    private String curAssigneeIds;
}
