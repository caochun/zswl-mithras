package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.service.providence.service.impl.BillOverdueDraftService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/10/29
 * @description
 */
public class BillOverdueDraftServiceTest extends ApplicationTest {
    @Resource
    private BillOverdueDraftService billOverdueDraftService;

    @Test
    public void effectTest() {
        billOverdueDraftService.effect();
    }
}
