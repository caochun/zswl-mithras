package cn.zswltech.mithras.others.service.util;

import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.util.ContractUtil;
import org.junit.jupiter.api.Test;

class ContractUtilTest {

    @Test
    public void main() {
        System.out.println(ContractUtil.generateMainCode(1, ProjectBizType.ZL, "hui_zu"));
    }

}