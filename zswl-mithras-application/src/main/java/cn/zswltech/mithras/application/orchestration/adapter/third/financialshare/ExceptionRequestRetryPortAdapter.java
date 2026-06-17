package cn.zswltech.mithras.application.orchestration.adapter.third.financialshare;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.handle.CQ2CollectionHandle;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.handle.CQ2PaymentHandle;
import cn.zswltech.mithras.third.financialshare.application.ExceptionRequestRetryPort;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2CollectionReq;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2PaymentReq;
import org.springframework.stereotype.Component;

@Component
public class ExceptionRequestRetryPortAdapter implements ExceptionRequestRetryPort {
    @Override
    public void retryCollection(String reqData) {
        SpringUtil.getBean(CQ2CollectionHandle.class).execute(JSONUtil.toList(reqData, CQ2CollectionReq.class));
    }

    @Override
    public void retryPayment(String reqData) {
        SpringUtil.getBean(CQ2PaymentHandle.class).execute(JSONUtil.toList(reqData, CQ2PaymentReq.class));
    }
}
