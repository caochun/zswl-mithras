package cn.zswltech.mithras.service.service.leaseholdproperty;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;

public interface LeaseReviewService {

    void effect(Long leaseItemInfoId, ProcessModelTypeEnum processModelTypeEnum);

    void processEnd(Long leaseItemInfoId, Integer endType);

    void effectAndCreate(Long projReviewId, String flowId);

}
