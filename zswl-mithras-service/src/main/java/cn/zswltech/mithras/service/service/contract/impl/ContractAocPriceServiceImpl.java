package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.contract.price.ContractAocPriceModifyREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.convert.contract.ContractPriceConverter;
import cn.zswltech.mithras.contract.mapper.contract.ContractAocPriceMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractAocPriceService;
import cn.zswltech.mithras.service.service.contract.ContractGuarantorService;
import cn.zswltech.mithras.contract.archive.application.ContractAocPriceLibService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-08-01
 */
@Service
@Validated
@Slf4j
public class ContractAocPriceServiceImpl extends ServiceImpl<ContractAocPriceMapper, ContractAocPrice> implements ContractAocPriceService {

    @Resource
    private ContractPriceConverter priceConverter;

    @Resource
    private ContractBaseInfoMapper baseInfoMapper;

    @Resource
    private ContractAocPriceLibService libService;

    @Resource
    private ProjReviewAocPriceService projReviewAocPriceService;

    @Resource
    private ContractGuarantorService contractGuarantorService;

    @ContractChangeOther
    @Override
    public void modify(@Valid ContractAocPriceModifyREQ req) {
        checkModify(req);
        saveOrUpdate(priceConverter.aocModifyReqToEntity(req));
        if (ObjectUtil.isNotEmpty(req.getContractAmount())) {
            baseInfoMapper.updateApplyCreditAmount(req.getContractId(), req.getContractAmount());

            //同步担保措施的担保金额
            contractGuarantorService.update(Wrappers.<ContractGuarantor>lambdaUpdate()
                    .eq(ContractGuarantor::getContractId, req.getContractId())
                    .set(ContractGuarantor::getGuaranteeAmountSingle, req.getContractAmount()));
        }
    }

    public ContractAocPrice getByContractId(ContractPriceDetailREQ req) {
        if (ObjectUtil.isNull(req.getVersion())) {
            return baseMapper.selectOne(Wrappers.<ContractAocPrice>lambdaQuery()
                    .eq(ContractAocPrice::getContractId, req.getContractId()));
        } else {
            return libService.getByVersion(req.getContractId(), req.getVersion());
        }
    }

    public Long sumApplyByContractId(List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        if (ObjectUtil.isEmpty(ids)) {
            return sum.get();
        }
        List<ContractAocPrice> infos = baseMapper.selectList(Wrappers.<ContractAocPrice>lambdaQuery()
                .in(ContractAocPrice::getContractId, ids));
        if (ObjectUtil.isEmpty(infos)) {
            return sum.get();
        }
        infos.forEach(base -> {
            sum.updateAndGet(v -> v + LongUtil.null2zero(base.getContractAmount()));
        });
        log.info("getStockRiskExposure sumApplyByContractId ContractAocPriceService objects  {}", sum.get());
        return sum.get();
    }

    @Override
    public ContractAocPrice getByContractId(Long contractId) {
        LambdaQueryWrapper<ContractAocPrice> query = Wrappers.lambdaQuery();
        query.eq(ContractAocPrice::getContractId, contractId);
        query.orderByDesc(ContractAocPrice::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public List<ContractAocPrice> listByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return new ArrayList<>();
        }
        return this.list(Wrappers.<ContractAocPrice>lambdaQuery()
                .in(ContractAocPrice::getContractId, contractIds)
        );
    }

    private void checkModify(ContractAocPriceModifyREQ req) {
        ContractAocPrice aocPrice = baseMapper.selectById(req.getId());
        ContractBaseInfo baseInfo = baseInfoMapper.selectById(aocPrice.getContractId());

        ProjReviewAocPrice projReviewAocPrice = projReviewAocPriceService.getByProjectId(baseInfo.getProjReviewId());
        /*// 合同金额
        if (ObjectUtil.isNotEmpty(req.getContractAmount())) {
            if (req.getContractAmount() <= 0 || req.getContractAmount() > baseInfo.getRemainAvailableQuota()) {
                throw new MithrasException("合同金额需大于0且小于等于可用金额");
            }
        }*/
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
//        if (ObjectUtil.isNotEmpty(req.getIrrPercent()) && Objects.nonNull(projReviewAocPrice.getIrrPercent())) {
//            if (req.getIrrPercent() < projReviewAocPrice.getIrrPercent()) {
//                throw new MithrasException("IRR必须大于等于项目中的IRR");
//            }
//        }
    }
}