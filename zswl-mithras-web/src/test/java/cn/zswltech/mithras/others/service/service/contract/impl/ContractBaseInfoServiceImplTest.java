package cn.zswltech.mithras.others.service.service.contract.impl;

import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContractBaseInfoServiceImplTest {

    @Autowired
    private ContractBaseInfoService contractBaseInfoService;

    @Test
    void list() {
        List<ContractBaseInfoLib> list = contractBaseInfoService.list(3L);
        System.out.println(list);
    }
}