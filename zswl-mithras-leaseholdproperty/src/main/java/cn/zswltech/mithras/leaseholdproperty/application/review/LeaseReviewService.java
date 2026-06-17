package cn.zswltech.mithras.leaseholdproperty.application.review;

import cn.zswltech.mithras.leaseholdproperty.enums.LeaseholdPropertyProcessModel;

public interface LeaseReviewService {

    void effect(Long leaseItemInfoId, LeaseholdPropertyProcessModel processModel);

    void processEnd(Long leaseItemInfoId, Integer endType);

    void effectAndCreate(Long projReviewId, String flowId);

}
