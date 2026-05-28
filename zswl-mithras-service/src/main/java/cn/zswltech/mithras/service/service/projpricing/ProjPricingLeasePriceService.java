package cn.zswltech.mithras.service.service.projpricing;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingLeasePriceModifyREQ;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.service.mapper.projpricing.ProjPricingLeasePriceMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 项目定价-租赁报价方案表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Service
@Slf4j
public class ProjPricingLeasePriceService extends ServiceImpl<ProjPricingLeasePriceMapper, ProjPricingLeasePrice> implements ProjPricingUpdateAdvice{

    @Resource
    private ProjPricingPriceConverter priceConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void saveIrr(Long ProjPricingId, Integer irr) {
        this.saveCheck(ProjPricingId);
        ProjPricingLeasePrice projPricingLeasePrice = this.getByProjectId(ProjPricingId);
        Assert.notNull(projPricingLeasePrice, () -> MithrasException.newException("报价方案不存在"));
        projPricingLeasePrice.setIrrPercent(irr);
        this.updateById(projPricingLeasePrice);
        recordStatus(ProjPricingId);
    }

    public void modify(@Valid ProjPricingLeasePriceModifyREQ req) {
        //  校验手续费和首期利息
        Long commission = req.getCommission();
        Long firstInstallmentInterest = req.getFirstInstallmentInterest();
        if (ObjectUtils.isEmpty(commission) || ObjectUtils.isEmpty(firstInstallmentInterest) ) {
            throw new MithrasException("手续费和首期利息不能为空");
        }
        if (commission < 0) {
            throw new MithrasException("手续费必须填写大于等于零的数");
        }
        if (firstInstallmentInterest < 0) {
            throw new MithrasException("首期利息必须填写大于等于零的数");
        }
        saveCheck(req.getProjectId());
        saveOrUpdate(priceConverter.leaseModifyReqToEntity(req));
        recordStatus(req.getProjectId());
    }

    public ProjPricingLeasePrice getByProjectId(Long projectId) {
        return baseMapper.selectOne(
                Wrappers.<ProjPricingLeasePrice>lambdaQuery().eq(ProjPricingLeasePrice::getProjectId, projectId));
    }

//    public Long sumApplyByContractId(List<Long> ids) {
//        AtomicReference<Long> sum = new AtomicReference<>(0L);
//        if (ObjectUtil.isEmpty(ids)){
//            return sum.get();
//        }
//        List<ProjPricingLeasePrice> ProjPricingLeasePrices = baseMapper.selectList(Wrappers.<ProjPricingLeasePrice>lambdaQuery()
//                .in(ProjPricingLeasePrice::getProjectId, ids));
//        if(ObjectUtil.isEmpty(ProjPricingLeasePrices)){
//            return sum.get();
//        }
//        ProjPricingLeasePrices.forEach(base -> {
//            sum.updateAndGet(v -> v + LongUtil.null2zero(base.getApplyCreditAmount()));
//        });
//        log.info("getStockRiskExposure sumApplyByContractId ProjPricingLeasePrice objects {}", sum.get());
//        return sum.get();
//    }


    public void add(ProjPricingLeasePrice price) {
        baseMapper.insert(price);
    }
}
