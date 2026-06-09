package cn.zswltech.mithras.policy.application.datacompare;

import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.mapper.PolicyInfoLibMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.policy.application.lib.handler.impl.PolicyInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2023-06-26
 **/
@Service("policyInfo")
public class PolicyInfoFactory implements EditdataCompareFactory {

    @Resource
    private PolicyInfoLibMapper libMapper;
    @Resource
    private PolicyInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<PolicyInfo, PolicyInfoLib, PolicyInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper, "POLICY", version);
    }
}
