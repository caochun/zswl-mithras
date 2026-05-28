package cn.zswltech.mithras.service.service.lib.payment.libservice.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.mapper.lib.payment.PaymentPolicyInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfoLib;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentPolicyInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.payment.libservice.PaymentPolicyInfoLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Resource
    private PaymentPolicyInfoLibHandler handler;
    @Override
    public Page<PaymentPolicyInfo> getByPaymentIdAndVersion(Long paymentId, String version, Integer page, Integer pageSize, Boolean adventFlag) {
        Page<PaymentPolicyInfoLib> versionLib = baseMapper.selectPage(new Page<>(page, pageSize),Wrappers.<PaymentPolicyInfoLib>lambdaQuery()
                .eq(PaymentPolicyInfoLib::getPaymentId, paymentId)
                .le(ObjectUtil.isNotNull(adventFlag) && adventFlag, PaymentPolicyInfoLib::getInsuranceEndDate, LocalDate.now().plusDays(5))
                .eq(PaymentPolicyInfoLib::getVersion, version)
        );
        List<PaymentPolicyInfo> collect = versionLib.getRecords().stream().map(handler::actualLib2Entity).collect(Collectors.toList());
        Page<PaymentPolicyInfo> rsp = new Page<>();
        rsp.setTotal(versionLib.getTotal());
        rsp.setCurrent(versionLib.getCurrent());
        rsp.setSize(versionLib.getSize());
        rsp.setRecords(collect);
        return rsp;
    }
}
