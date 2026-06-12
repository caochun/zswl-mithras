package cn.zswltech.mithras.application.orchestration.adapter.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.policy.application.port.PolicyContractInfoPort;
import cn.zswltech.mithras.policy.application.port.model.PolicyContractInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PolicyContractInfoPortAdapter implements PolicyContractInfoPort {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Override
    public PolicyContractInfo getById(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            return null;
        }
        return BeanUtil.copyProperties(contractBaseInfo, PolicyContractInfo.class);
    }
}
