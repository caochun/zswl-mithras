package cn.zswltech.mithras.payment.versioning.service.impl;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.payment.mapper.lib.PaymentPlanedDetailLibMapper;
import cn.zswltech.mithras.payment.model.PaymentPlanedDetailLib;
import cn.zswltech.mithras.payment.versioning.service.PaymentPlanedDetailLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public List<PlanedDetailDto> list(Long paymentId, String version) {
        List<PaymentPlanedDetailLib> dataList =
                baseMapper.selectList(Wrappers.<PaymentPlanedDetailLib>lambdaQuery()
                        .eq(PaymentPlanedDetailLib::getPaymentId, paymentId)
                        .eq(PaymentPlanedDetailLib::getVersion, version)
                );
        return dataList.stream().map(this::lib2Dto).collect(Collectors.toList());
    }

    private PlanedDetailDto lib2Dto(PaymentPlanedDetailLib lib) {
        PlanedDetailDto dto = new PlanedDetailDto();
        BeanUtils.copyProperties(lib, dto);
        dto.setId(lib.getOriginId());
        return dto;
    }
}
