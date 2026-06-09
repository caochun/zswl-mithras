package cn.zswltech.mithras.application.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseModifyAuthPort;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AfterLeaseModifyAuthPortAdapter implements AfterLeaseModifyAuthPort {
    @Resource
    private CommonModifyMainAuthCheckerNew commonModifyMainAuthChecker;

    @Override
    public void check(String businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        commonModifyMainAuthChecker.check(BusinessModuleEnum.valueOf(businessModule), helperMapperClass, keyId, args);
    }
}
