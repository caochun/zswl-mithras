package cn.zswltech.mithras.service.service.projpricing;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingFactoringPriceModifyREQ;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingFactoringPrice;
import cn.zswltech.mithras.service.mapper.projpricing.ProjPricingFactoringPriceMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 项目定价-保理报价方案表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Service
@Slf4j
public class ProjPricingFactoringPriceService extends ServiceImpl<ProjPricingFactoringPriceMapper, ProjPricingFactoringPrice> implements ProjPricingUpdateAdvice {
    @Resource
    private ProjPricingPriceConverter priceConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void saveIrr(Long ProjPricingId, Integer irr) {
        this.saveCheck(ProjPricingId);
        ProjPricingFactoringPrice projPricingFactoringPrice = this.getByProjectId(ProjPricingId);
        Assert.notNull(projPricingFactoringPrice, () -> MithrasException.newException("报价方案不存在"));
        projPricingFactoringPrice.setIrrPercent(irr);
        this.updateById(projPricingFactoringPrice);
        recordStatus(ProjPricingId);
    }

    public void modify(@Valid ProjPricingFactoringPriceModifyREQ req) {
        saveCheck(req.getProjectId());
        saveOrUpdate(priceConverter.factoringModifyReqToEntity(req));
        recordStatus(req.getProjectId());
    }

    public ProjPricingFactoringPrice getByProjectId(Long projectId) {
        return baseMapper.selectOne(
                Wrappers.<ProjPricingFactoringPrice>lambdaQuery().eq(ProjPricingFactoringPrice::getProjectId, projectId));
    }

//    public Long sumApplyByContractId(List<Long> ids) {
//        AtomicReference<Long> sum = new AtomicReference<>(0L);
//        if (ObjectUtil.isEmpty(ids)){
//            return sum.get();
//        }
//        List<ProjPricingFactoringPrice> infos = baseMapper.selectList(Wrappers.<ProjPricingFactoringPrice>lambdaQuery()
//                .in(ProjPricingFactoringPrice::getProjectId, ids));
//        if(ObjectUtil.isEmpty(infos)){
//            return sum.get();
//        }
//        infos.forEach(base -> {
//            sum.updateAndGet(v -> v + LongUtil.null2zero(base.getApplyCreditAmount()));
//        });
//        log.info("getStockRiskExposure sumApplyByContractId ProjPricingFactoring objects {}", sum.get());
//        return sum.get();
//    }

    /**
     *
     * @param price
     */
    public void add(ProjPricingFactoringPrice price) {
        baseMapper.insert(price);
    }
}
