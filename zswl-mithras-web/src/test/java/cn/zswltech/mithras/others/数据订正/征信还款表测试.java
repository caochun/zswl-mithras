package cn.zswltech.mithras.others.数据订正;

import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.contract.versioning.application.ContractVersionService;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author yibin
 * 改订正用于
 * 部门修改了名称，或者是合并了部门改了名称，之前的部门已不再使用
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 征信还款表测试 {

    @Test
    public void test() {
        getBean(ContractVersionService.class).recordVersion(1046L, VersionTypeEnum.EFFECT, null, null, 1);
        getBean(ContractVersionService.class).recordVersion(1047L, VersionTypeEnum.EFFECT, null, null, 1);
    }
}

