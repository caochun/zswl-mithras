package cn.zswltech.mithras.payment.application.lib.service;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.payment.model.PaymentBaseInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 11:22
 */
public interface PaymentBaseInfoLibService extends IService<PaymentBaseInfoLib> {
    PaymentDetailRsp detail(Long id, String version);
}
