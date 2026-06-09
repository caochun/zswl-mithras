package cn.zswltech.mithras.contract.core.application.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.annotation.ContractChangeOther;
import cn.zswltech.mithras.contract.convert.contract.ContractPriceConverter;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.application.ContractLeasePriceService;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.dto.contract.price.ContractLeasePriceModifyREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author vico
 * @description 合同-租赁报价方案表
 * @date 2022-08-12
 */
@Service
@Slf4j
public class ContractLeasePriceServiceImpl extends ServiceImpl<ContractLeasePriceMapper, ContractLeasePrice> implements ContractLeasePriceService {

    @Resource
    private ContractPriceConverter priceConverter;
    @Resource
    private ContractBaseInfoMapper baseInfoMapper;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private ContractGuarantorService contractGuarantorService;

    @Override
    @ContractChangeOther
    public Boolean modify(ContractLeasePriceModifyREQ req) {
        checkModify(req);
        saveOrUpdate(priceConverter.leaseModifyReqToEntity(req));
        if (ObjectUtil.isNotEmpty(req.getApplyCreditAmount())) {
            baseInfoMapper.updateApplyCreditAmount(req.getContractId(), req.getApplyCreditAmount());
            contractGuarantorService.update(Wrappers.<ContractGuarantor>lambdaUpdate()
                    .eq(ContractGuarantor::getContractId, req.getContractId())
                    .set(ContractGuarantor::getGuaranteeAmountSingle, req.getApplyCreditAmount()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean modifyChange(ContractLeasePriceModifyREQ req) {
        ContractLeasePrice leasePrice = priceConverter.leaseModifyReqToEntity(req);
        ContractLeasePrice contractLeasePrice = baseMapper.selectById(req.getId());
        leasePrice.setRepayTimesTotal(ObjectUtil.isEmpty(req.getRepayTimesTotal()) ? contractLeasePrice.getRepayTimesTotal() : req.getRepayTimesTotal());
        saveOrUpdate(leasePrice);
        if (ObjectUtil.isNotEmpty(req.getApplyCreditAmount())) {
            baseInfoMapper.updateApplyCreditAmount(req.getContractId(), req.getApplyCreditAmount());
        }
        return Boolean.TRUE;
    }

    @Override
    public ContractLeasePrice detail(ContractPriceDetailREQ req) {
        if (ObjectUtil.isNull(req.getVersion())) {
            return baseMapper.selectOne(
                    Wrappers.<ContractLeasePrice>lambdaQuery().eq(ContractLeasePrice::getContractId, req.getContractId()));
        } else {
            return contractLeasePriceLibService.getByVersion(req.getContractId(), req.getVersion());
        }
    }

    @Override
    public Long sumApplyByContractId(List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        if (ObjectUtil.isEmpty(ids)) {
            return sum.get();
        }
        List<ContractLeasePrice> infos = baseMapper.selectList(Wrappers.<ContractLeasePrice>lambdaQuery()
                .in(ContractLeasePrice::getContractId, ids));
        if (ObjectUtil.isEmpty(infos)) {
            return sum.get();
        }
        infos.forEach(base -> sum.updateAndGet(v -> v + LongUtil.null2zero(base.getApplyCreditAmount())));
        log.info("getStockRiskExposure sumApplyByContractId ContractLeasePriceService objects {}", sum.get());
        return sum.get();
    }

    @Override
    public ContractLeasePrice getByContractId(Long contractId) {
        LambdaQueryWrapper<ContractLeasePrice> query = Wrappers.lambdaQuery();
        query.eq(ContractLeasePrice::getContractId, contractId);
        query.orderByDesc(ContractLeasePrice::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public List<ContractLeasePrice> listByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return new ArrayList<>();
        }
        return this.list(Wrappers.<ContractLeasePrice>lambdaQuery()
                .in(ContractLeasePrice::getContractId, contractIds));
    }

    private void checkModify(ContractLeasePriceModifyREQ req) {
        Long commission = req.getCommission();
        Long firstInstallmentInterest = req.getFirstInstallmentInterest();
        if (ObjectUtils.isEmpty(commission) || ObjectUtils.isEmpty(firstInstallmentInterest)) {
            throw new MithrasException("手续费和首期利息不能为空");
        }
        if (commission < 0) {
            throw new MithrasException("手续费必须填写大于等于零的数");
        }
        if (firstInstallmentInterest < 0) {
            throw new MithrasException("首期利息必须填写大于等于零的数");
        }
        if (ObjectUtil.isNotEmpty(req.getRepayTimesTotal())) {
            if (req.getRepayTimesTotal() <= 0) {
                throw new MithrasException("还款期数必须大于0");
            }
        }
        if (ObjectUtil.isNotEmpty(req.getDownPayment())) {
            if (req.getDownPayment() < 0 || req.getDownPayment() >= req.getApplyCreditAmount()) {
                throw new MithrasException("首期租金必须大于等于0，且小于合同金额");
            }
        }
        if (ObjectUtil.isNotEmpty(req.getEarnestMoney())) {
            if (req.getEarnestMoney() < 0 || req.getEarnestMoney() > req.getApplyCreditAmount()) {
                throw new MithrasException("保证金必须大于等于0，且小于等于合同金额");
            }
        }
        if (ObjectUtil.isNotEmpty(req.getConsultingFee())) {
            if (req.getConsultingFee() < 0 || req.getConsultingFee() >= req.getApplyCreditAmount()) {
                throw new MithrasException("服务费/咨询费必须大于等于0，且小于合同金额");
            }
        }
        if (ObjectUtil.isNotEmpty(req.getNominalPrice())) {
            if (req.getNominalPrice() < 0 || req.getNominalPrice() >= req.getApplyCreditAmount()) {
                throw new MithrasException("名义价款必须大于等于0，且小于合同金额");
            }
        }
        if (ObjectUtil.isNotEmpty(req.getDefaultInterestRate())) {
            if (req.getDefaultInterestRate() <= 0) {
                throw new MithrasException("罚息日利率必须大于0");
            }
        }
    }
}
