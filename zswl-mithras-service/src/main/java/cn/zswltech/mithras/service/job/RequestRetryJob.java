package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.system.service.ExceptionRequestInfoService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQBillPaymentREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQPaymentREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQReceiveREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQReceiveRentREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.FinancialCommonRSP;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 统一接口请求失败重试任务
 * @author: jackerhe
 * @date: 2023/3/31 6:12 下午
 **/
@Component
@Slf4j
public class RequestRetryJob {

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;

    @Resource
    private ExceptionRequestInfoService exceptionRequestInfoService;

    /**
     * 苍穹相关请求重试任务
     */
    @XxlJob("requestRetryCQHandler")
    public void doJobHandler() {
        try {
            log.info(">>>>>>>>>>>>>>requestRetryCQHandler began");
            //todo
            List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.list(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                    .in(ExceptionRequestInfo::getPlatform, ListUtil.toList(PlatformApiEnum.CQ_RENT_RECEIVE.name(), PlatformApiEnum.CQ_RECEIVE.name(),
                            PlatformApiEnum.CQ_PAYMENT.name(), PlatformApiEnum.CQ_BILL_PAYMENT.name()))
                    .eq(ExceptionRequestInfo::getRetryFlag, YesOrNoNumberEnum.NO.getCode()));
            if(ObjectUtil.isEmpty(exceptionRequestInfos)){
                return;
            }

            exceptionRequestInfos.forEach(base -> {
                if(base.getMaxRetryAmount() > base.getRetryAmount()){
                    PlatformApiEnum of = PlatformApiEnum.of(base.getPlatform());
                    if(ObjectUtil.isNotEmpty(of)){
                        switch (of){
                            case CQ_RENT_RECEIVE:
                                PlatformApiHandler<List<CQReceiveRentREQ>, FinancialCommonRSP> receiveRentHandler =
                                        platformApiHandleFactory.getPlatformApiHandler(of);
                                receiveRentHandler.execute(JSON.parseArray(base.getReqData(), CQReceiveRentREQ.class));
                                break;
                            case CQ_RECEIVE:
                                PlatformApiHandler<List<CQReceiveREQ>, FinancialCommonRSP> receiveHandler =
                                        platformApiHandleFactory.getPlatformApiHandler(of);
                                receiveHandler.execute(JSON.parseArray(base.getReqData(), CQReceiveREQ.class));
                                break;
                            case CQ_PAYMENT:
                                PlatformApiHandler<List<CQPaymentREQ>, FinancialCommonRSP> paymentHandler =
                                        platformApiHandleFactory.getPlatformApiHandler(of);
                                paymentHandler.execute(JSON.parseArray(base.getReqData(), CQPaymentREQ.class));
                                break;
                            case CQ_BILL_PAYMENT:
                                PlatformApiHandler<List<CQBillPaymentREQ>, FinancialCommonRSP> billPaymentApiHandler =
                                        platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_BILL_PAYMENT);
                                billPaymentApiHandler.execute(JSON.parseArray(base.getReqData(), CQBillPaymentREQ.class));

                        }
                    }
                }
            });
            log.info(">>>>>>>>>>>>>>requestRetryCQHandler over");
        } catch (Exception e) {
            log.error(">>>>>>>>>>>>>>requestRetryCQHandler error", e);
        }

    }

}