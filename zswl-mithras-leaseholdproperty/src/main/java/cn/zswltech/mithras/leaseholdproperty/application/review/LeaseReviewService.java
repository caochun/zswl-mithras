package cn.zswltech.mithras.leaseholdproperty.application.review;

import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;

public interface LeaseReviewService {

    void effect(Long leaseItemInfoId, ProcessModelTypeEnum processModelTypeEnum);

    void processEnd(Long leaseItemInfoId, Integer endType);

    void effectAndCreate(Long projReviewId, String flowId);

}
