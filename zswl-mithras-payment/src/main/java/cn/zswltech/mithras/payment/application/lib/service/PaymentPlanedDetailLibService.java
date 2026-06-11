package cn.zswltech.mithras.payment.application.lib.service;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.payment.mapper.model.PaymentPlanedDetailLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 11:21
 */
public interface PaymentPlanedDetailLibService extends IService<PaymentPlanedDetailLib> {
    List<PlanedDetailDto> list(Long id, String version);
}
