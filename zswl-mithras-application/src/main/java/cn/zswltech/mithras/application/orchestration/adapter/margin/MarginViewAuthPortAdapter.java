package cn.zswltech.mithras.application.orchestration.adapter.margin;

import cn.zswltech.mithras.margin.application.port.MarginViewAuthPort;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MarginViewAuthPortAdapter implements MarginViewAuthPort {
    @Resource
    private CommonViewMainAuthCheckerNew commonViewMainAuthChecker;

    @Override
    public void checkView(Long marginId) {
        commonViewMainAuthChecker.check(BusinessModuleEnum.MARGIN, null, marginId, new Object[0]);
    }
}
