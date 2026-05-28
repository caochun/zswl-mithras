package cn.zswltech.mithras.others.service.fund.receipt;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/23 11:48
 */
public class BaseInfoServiceTest extends ApplicationTest {

    @Resource
    private FundReceiptRepayBaseInfoService baseInfoService;

    @Test
    public void 起息() {
        baseInfoService.processFinancingEffectEnd(8L);
    }
}
