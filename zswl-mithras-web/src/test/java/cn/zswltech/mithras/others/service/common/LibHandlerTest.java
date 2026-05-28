package cn.zswltech.mithras.others.service.common;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentPlanedDetailLibHandler;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 版本处理器测试
 *
 * @author wangchuanhao
 * @date 2022/9/16 10:04 AM
 */
public class LibHandlerTest extends ApplicationTest {

    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private PaymentPlanedDetailLibHandler paymentPlanedDetailLibHandler;

    @Test
    public void test() {
        log.info("合同主表最新版本数据:{}", JSON.toJSONString(contractBaseInfoLibHandler.queryLatestDataByOriginId(-1L)));
        log.info("合同主表最新版本数据:{}", JSON.toJSONString(contractBaseInfoLibHandler.queryLatestDataByOriginId(98L)));
        log.info("合同主表最新版本数据:{}", JSON.toJSONString(contractBaseInfoLibHandler.queryLatestDataByLibId(63L)));
        log.info("付款子表最新版本数据:{}", JSON.toJSONString(paymentPlanedDetailLibHandler.queryLatestDataByOriginId(-1L)));
        log.info("付款子表最新版本数据:{}", JSON.toJSONString(paymentPlanedDetailLibHandler.queryLatestDataByOriginId(22L)));
        log.info("付款子表最新版本数据:{}", JSON.toJSONString(paymentPlanedDetailLibHandler.queryLatestDataByLibId(18L)));
    }

}
