package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.mapper.PolicyInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.policy.application.lib.handler.impl.PolicyInfoLibHandler;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.impl.ProjEstablishBaseInfoLibHandler;
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
        return new DefaultDataCompare<PolicyInfo, PolicyInfoLib, PolicyInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.POLICY.name(), version);
    }
}
