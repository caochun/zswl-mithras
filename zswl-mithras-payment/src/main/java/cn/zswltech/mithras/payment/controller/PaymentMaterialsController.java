package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentMaterialsApi;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsOperateReq;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import cn.zswltech.mithras.payment.application.PaymentMaterialsApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class PaymentMaterialsController implements PaymentMaterialsApi {
    @Resource
    private PaymentMaterialsApplicationService paymentMaterialsApplicationService;

    @Override
    public R<Map<String, List<PaymentMaterialsListRsp>>> list(PaymentMaterialsListReq req) {
        return paymentMaterialsApplicationService.list(req);
    }

    @Override
    public R<Void> upload(MultipartFile file, Long paymentId, String materialsType) {
        return paymentMaterialsApplicationService.upload(file, paymentId, materialsType);
    }

    @Override
    public R<Void> remove(PaymentMaterialsOperateReq req) {
        return paymentMaterialsApplicationService.remove(req);
    }

}
