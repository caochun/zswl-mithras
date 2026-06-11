package cn.zswltech.mithras.others.service.util;

import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.application.orchestration.contract.util.ContractUtil;
import org.junit.jupiter.api.Test;

class ContractUtilTest {

    @Test
    public void main() {
        System.out.println(ContractUtil.generateMainCode(1, ProjectBizType.ZL, "hui_zu"));
    }

}