package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.application.orchestration.maintenance.DataCleanService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/20 10:13
 */
class DataCleanServiceTest extends ApplicationTest{

    @Resource
    private DataCleanService dataCleanService;
    @Test
    void cleanContract() {
        dataCleanService.cleanContract();
    }
}