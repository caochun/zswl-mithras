package cn.zswltech.mithras.others.kpi;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.kpi.application.config.KpiParameterConfigService;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
public class KpiParameterConfigTest extends ApplicationTest {
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Test
    public void test(){
        BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_HZ);
        System.out.println(taxRate);
    }

    public static void main(String[] args) {

    }
}
