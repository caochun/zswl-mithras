package cn.zswltech.mithras.service.service.lib.payment.libservice.impl;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.service.mapper.lib.payment.PaymentPlanedDetailLibMapper;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetailLib;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentPlanedDetailLibHandler;
import cn.zswltech.mithras.service.service.lib.payment.libservice.PaymentPlanedDetailLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 17:59
 */
@Service
public class PaymentPlanedDetailLibServiceImpl extends ServiceImpl<PaymentPlanedDetailLibMapper, PaymentPlanedDetailLib>
        implements PaymentPlanedDetailLibService {

    @Resource
    private PaymentPlanedDetailLibHandler planedDetailLibHandler;

    @Override
    public List<PlanedDetailDto> list(Long paymentId, String version) {
        List<PaymentPlanedDetailLib> dataList =
                baseMapper.selectList( Wrappers.<PaymentPlanedDetailLib>lambdaQuery()
                        .eq(PaymentPlanedDetailLib::getPaymentId, paymentId)
                        .eq(PaymentPlanedDetailLib::getVersion, version)
                );
        return dataList.stream().map(planedDetailLibHandler::actualLib2Rsp).collect(Collectors.toList());
    }
}
