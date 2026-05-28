package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentDetailRSP;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPrepayment;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPrepaymentLib;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @ClassName ContractPrepaymentLibHandler
 * @Description
 * @Author jackerhe
 * @Date 2022/10/21 2:36 下午
 * @Version 1.0
 **/
@Service
public class ContractPrepaymentLibHandler extends ContractLibAbstractHandler<ContractPrepaymentLib, ContractPrepayment, ContractPrepaymentDetailRSP> {

    @Override
    protected ContractPrepaymentLib entity2Lib(ContractPrepayment f) {
        return BeanUtil.copyProperties(f, ContractPrepaymentLib.class);
    }

    @Override
    protected ContractPrepayment lib2Entity(ContractPrepaymentLib t) {
        return BeanUtil.copyProperties(t, ContractPrepayment.class);
    }

    @Override
    protected ContractPrepaymentDetailRSP lib2Rsp(ContractPrepaymentLib f) {
        ContractPrepaymentDetailRSP rsp = BeanUtil.copyProperties(f, ContractPrepaymentDetailRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.PREPAYMENT;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
