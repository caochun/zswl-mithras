package cn.zswltech.mithras.payment.application.lib.libservice.impl;

import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.lib.PaymentPolicyInfoLibMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPolicyInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPolicyInfoLib;
import cn.zswltech.mithras.payment.application.lib.libservice.PaymentPolicyInfoLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 17:59
 */
@Service
public class PaymentPolicyInfoLibServiceImpl extends ServiceImpl<PaymentPolicyInfoLibMapper, PaymentPolicyInfoLib>
        implements PaymentPolicyInfoLibService {

    @Override
    public Page<PaymentPolicyInfo> getByPaymentIdAndVersion(Long paymentId, String version, Integer page, Integer pageSize, Boolean adventFlag) {
        Page<PaymentPolicyInfoLib> versionLib = baseMapper.selectPage(new Page<>(page, pageSize), Wrappers.<PaymentPolicyInfoLib>lambdaQuery()
                .eq(PaymentPolicyInfoLib::getPaymentId, paymentId)
                .le(Boolean.TRUE.equals(adventFlag), PaymentPolicyInfoLib::getInsuranceEndDate, LocalDate.now().plusDays(5))
                .eq(PaymentPolicyInfoLib::getVersion, version)
        );
        List<PaymentPolicyInfo> collect = versionLib.getRecords().stream().map(this::lib2Entity).collect(Collectors.toList());
        Page<PaymentPolicyInfo> rsp = new Page<>();
        rsp.setTotal(versionLib.getTotal());
        rsp.setCurrent(versionLib.getCurrent());
        rsp.setSize(versionLib.getSize());
        rsp.setRecords(collect);
        return rsp;
    }

    private PaymentPolicyInfo lib2Entity(PaymentPolicyInfoLib lib) {
        PaymentPolicyInfo entity = new PaymentPolicyInfo();
        BeanUtils.copyProperties(lib, entity);
        return entity;
    }
}
