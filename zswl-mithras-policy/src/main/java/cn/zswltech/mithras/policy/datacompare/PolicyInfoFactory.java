package cn.zswltech.mithras.policy.datacompare;

import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.policy.mapper.PolicyInfoLibMapper;
import cn.zswltech.mithras.policy.model.PolicyInfo;
import cn.zswltech.mithras.policy.model.PolicyInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.policy.versioning.handler.impl.PolicyInfoLibHandler;
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
