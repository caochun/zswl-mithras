package cn.zswltech.mithras.contract.core.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.annotation.ContractChangeOther;
import cn.zswltech.mithras.contract.convert.contract.ContractPriceConverter;
import cn.zswltech.mithras.contract.core.ContractFactoringPriceService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractFactoringPriceMapper;
import cn.zswltech.mithras.contract.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.versioning.service.ContractFactoringPriceLibService;
import cn.zswltech.mithras.dto.contract.price.ContractFactoringPriceModifyREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-08-01
 */
@Service
@Slf4j
public class ContractFactoringPriceServiceImpl extends ServiceImpl<ContractFactoringPriceMapper, ContractFactoringPrice> implements ContractFactoringPriceService {

    @Resource
    private ContractPriceConverter priceConverter;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractFactoringPriceLibService libService;
    @Resource
    private ContractGuarantorService contractGuarantorService;

    @ContractChangeOther
    @Transactional(rollbackFor = Throwable.class)
    public Boolean modify(@Valid ContractFactoringPriceModifyREQ req) {
        req.setFactoringRatePercent(req.getLprPercent() + req.getLprAddPercent());
        checkModify(req);
        saveOrUpdate(priceConverter.factoringModifyReqToEntity(req));
        if (ObjectUtil.isNotEmpty(req.getContractAmount())) {
            contractBaseInfoMapper.updateApplyCreditAmount(req.getContractId(), req.getContractAmount());
            contractGuarantorService.update(Wrappers.<ContractGuarantor>lambdaUpdate()
                    .eq(ContractGuarantor::getContractId, req.getContractId())
                    .set(ContractGuarantor::getGuaranteeAmountSingle, req.getContractAmount()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean modifyChange(ContractFactoringPriceModifyREQ req) {
        return null;
    }

    @Override
    public ContractFactoringPrice detail(ContractPriceDetailREQ req) {
        return null;
    }

    public ContractFactoringPrice getByContractId(ContractPriceDetailREQ req) {
        if (ObjectUtil.isNull(req.getVersion())) {
            return baseMapper.selectOne(
                    Wrappers.<ContractFactoringPrice>lambdaQuery().eq(ContractFactoringPrice::getContractId, req.getContractId()));
        } else {
            return libService.getByVersion(req.getContractId(), req.getVersion());
        }
    }

    public ContractFactoringPrice getByContractId(Long contractId) {
        LambdaQueryWrapper<ContractFactoringPrice> query = Wrappers.lambdaQuery();
        query.eq(ContractFactoringPrice::getContractId, contractId);
        query.orderByDesc(ContractFactoringPrice::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public List<ContractFactoringPrice> listByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return new ArrayList<>();
        }
        return this.list(Wrappers.<ContractFactoringPrice>lambdaQuery()
                .in(ContractFactoringPrice::getContractId, contractIds));
    }

    public void add(ContractFactoringPrice price) {
        baseMapper.insert(price);
    }

    public Long sumApplyByContractId(List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        if (ObjectUtil.isEmpty(ids)) {
            return sum.get();
        }
        List<ContractFactoringPrice> infos = baseMapper.selectList(Wrappers.<ContractFactoringPrice>lambdaQuery()
                .in(ContractFactoringPrice::getContractId, ids));
        if (ObjectUtil.isEmpty(infos)) {
            return sum.get();
        }
        infos.forEach(base -> sum.updateAndGet(v -> v + LongUtil.null2zero(base.getContractAmount())));
        log.info("getStockRiskExposure sumApplyByContractId ContractFactoringPriceService objects {}", sum.get());
        return sum.get();
    }

    private void checkModify(ContractFactoringPriceModifyREQ req) {
        if (ObjectUtil.isNotEmpty(req.getEarnestMoney())) {
            if (req.getEarnestMoney() < 0 || req.getEarnestMoney() > req.getContractAmount()) {
                throw new MithrasException("保证金必须大于等于0，且小于等于合同金额");
            }
        }
        if (ObjectUtil.isNotEmpty(req.getConsultingFee())) {
            if (req.getConsultingFee() < 0 || req.getConsultingFee() >= req.getContractAmount()) {
                throw new MithrasException("手续费必须大于等于0，且小于合同金额");
            }
        }
    }
}
