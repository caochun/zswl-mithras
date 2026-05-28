package cn.zswltech.mithras.others.service;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.service.kpi.KpiProvisionDetailService;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
public class KpiProvisionDetailTest extends ApplicationTest {
    @Resource
    private KpiProvisionDetailService kpiProvisionDetailService;

    @Test
    public void buildProfitCurrent2() {
        kpiProvisionDetailService.buildProfitCurrent2(ListUtil.toList(2936L), LocalDate.now());
    }

    @Test
    public void add() {
        kpiProvisionDetailService.add(LocalDate.of(2025, 7, 31), 7L);
    }
}
