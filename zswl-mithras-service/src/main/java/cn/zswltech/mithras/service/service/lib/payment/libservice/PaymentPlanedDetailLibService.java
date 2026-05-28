package cn.zswltech.mithras.service.service.lib.payment.libservice;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetailLib;
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
