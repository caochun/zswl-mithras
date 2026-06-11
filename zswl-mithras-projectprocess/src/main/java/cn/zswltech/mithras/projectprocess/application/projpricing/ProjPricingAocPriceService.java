package cn.zswltech.mithras.projectprocess.application.projpricing;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingAocPriceModifyREQ;
import cn.zswltech.mithras.projectprocess.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingAocPriceMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 项目定价-债权转让报价方案表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Service
@Slf4j
public class ProjPricingAocPriceService extends ServiceImpl<ProjPricingAocPriceMapper, ProjPricingAocPrice> implements ProjPricingUpdateAdvice{

    @Resource
    private ProjPricingPriceConverter priceConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void saveIrr(Long ProjPricingId, Integer irr) {
        this.saveCheck(ProjPricingId);
        ProjPricingAocPrice projPricingAocPrice = this.getByProjectId(ProjPricingId);
        Assert.notNull(projPricingAocPrice, () -> MithrasException.newException("报价方案不存在"));
        projPricingAocPrice.setIrrPercent(irr);
        this.updateById(projPricingAocPrice);
        recordStatus(ProjPricingId);
    }

    public void modify(@Valid ProjPricingAocPriceModifyREQ req) {
        saveCheck(req.getProjectId());
        saveOrUpdate(priceConverter.aocModifyReqToEntity(req));
        recordStatus(req.getProjectId());
    }

    public ProjPricingAocPrice getByProjectId(Long projectId) {
        return baseMapper.selectOne(
                Wrappers.<ProjPricingAocPrice>lambdaQuery().eq(ProjPricingAocPrice::getProjectId, projectId));
    }

//    public Long sumApplyByContractId(List<Long> ids) {
//        AtomicReference<Long> sum = new AtomicReference<>(0L);
//        if (ObjectUtil.isEmpty(ids)){
//            return sum.get();
//        }
//        List<ProjPricingAocPrice> infos = baseMapper.selectList(Wrappers.<ProjPricingAocPrice>lambdaQuery()
//                .in(ProjPricingAocPrice::getProjectId, ids));
//        if(ObjectUtil.isEmpty(infos)){
//            return sum.get();
//        }
//        infos.forEach(base -> {
//            sum.updateAndGet(v -> v + LongUtil.null2zero(base.getApplyCreditAmount()));
//        });
//        log.info("getStockRiskExposure sumApplyByContractId ProjPricingAocPrice objects {}", sum.get());
//        return sum.get();
//    }

    public void add(ProjPricingAocPrice price) {
        baseMapper.insert(price);
    }
}
