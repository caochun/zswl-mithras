package cn.zswltech.mithras.others.service.contract;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.contract.impl.ContractPriceServiceImpl;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/5 19:43
 */
public class ContractPriceTest extends ApplicationTest {

    @Resource
    private ContractPriceServiceImpl contractPriceService;

    @Test
    public void test() {
        Set<Long> targetContractIds = new HashSet<>(Arrays.asList(1273L, 1183L, 1243L, 1159L));
        Map<Long, Integer> map1 = contractPriceService.queryNewestIrr(targetContractIds);
        System.out.println();
    }
}
