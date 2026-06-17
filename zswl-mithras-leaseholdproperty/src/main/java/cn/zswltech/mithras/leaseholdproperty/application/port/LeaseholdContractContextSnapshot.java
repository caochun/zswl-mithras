package cn.zswltech.mithras.leaseholdproperty.application.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaseholdContractContextSnapshot {

    private Long id;

    private Long projReviewId;

    private String projName;

    private Long clientId;

    private Long projSponsorUserId;

    private String projCosponsorUserIds;

    private String bizType;

    private String leaseType;
}
