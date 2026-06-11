package cn.zswltech.mithras.payment.application.lib.service.impl;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.payment.application.lib.handler.PaymentBaseInfoLibHandler;
import cn.zswltech.mithras.payment.mapper.lib.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.payment.application.lib.service.PaymentBaseInfoLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 17:58
 */
@Service
public class PaymentBaseInfoLibServiceImpl extends ServiceImpl<PaymentBaseInfoLibMapper, PaymentBaseInfoLib>
        implements PaymentBaseInfoLibService {

    @Resource
    private PaymentBaseInfoLibHandler baseInfoLibHandler;

    @Override
    public PaymentDetailRsp detail(Long id, String version) {
        PaymentBaseInfoLib versionLib = baseMapper.selectOne(Wrappers.<PaymentBaseInfoLib>lambdaQuery()
                .eq(PaymentBaseInfoLib::getOriginId, id)
                .eq(PaymentBaseInfoLib::getVersion, version)
                .last("LIMIT 1")
        );
        return Optional.ofNullable(versionLib).map(baseInfoLibHandler::actualLib2Rsp).orElse(new PaymentDetailRsp());
    }
}
