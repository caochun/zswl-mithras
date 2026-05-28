package cn.zswltech.mithras.service.service.lib.policy.impl;

import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.service.mapper.lib.policy.PolicyInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.policy.PolicyInfoLib;
import cn.zswltech.mithras.service.service.lib.policy.PolicyInfoLibService;
import cn.zswltech.mithras.service.service.lib.policy.handler.impl.PolicyInfoLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;


@Service
public class PolicyInfoLibServiceImpl
        extends ServiceImpl<PolicyInfoLibMapper, PolicyInfoLib>
        implements PolicyInfoLibService {

    @Resource
    private PolicyInfoLibHandler baseInfoLibHandler;

    @Override
    public PolicyInfoDetailRSP detail(Long id, String version) {
        PolicyInfoLib versionLib = baseMapper.selectOne(Wrappers.<PolicyInfoLib>lambdaQuery().eq(PolicyInfoLib::getOriginId, id)
                .eq(PolicyInfoLib::getVersion, version)
                .last("LIMIT 1")
        );
        return Optional.ofNullable(versionLib).map(baseInfoLibHandler::actualLib2Rsp).orElse(new PolicyInfoDetailRSP());
    }
}
