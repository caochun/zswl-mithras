package cn.zswltech.mithras.service.service.projreview;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceModifyREQ;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhaozhengkang
 * @description 租赁报价方案表
 * @date 2022-08-01
 */
@Service
@Validated
@Slf4j
public class ProjReviewLeasePriceService extends ServiceImpl<ProjReviewLeasePriceMapper, ProjReviewLeasePrice>
        implements ProjReviewUpdateAdvice {

    @Resource
    private ProjReviewPriceConverter priceConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void saveIrr(Long projReviewId, Integer irr) {
        this.saveCheck(projReviewId);
        ProjReviewLeasePrice projReviewLeasePrice = this.getByProjectId(projReviewId);
        Assert.notNull(projReviewLeasePrice, () -> MithrasException.newException("报价方案不存在"));
        projReviewLeasePrice.setIrrPercent(irr);
        this.updateById(projReviewLeasePrice);
        recordStatus(projReviewId);
    }

    public void modify(@Valid ProjReviewLeasePriceModifyREQ req) {
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

    public ProjReviewLeasePrice getByProjectId(Long projectId) {
        return baseMapper.selectOne(
                Wrappers.<ProjReviewLeasePrice>lambdaQuery().eq(ProjReviewLeasePrice::getProjectId, projectId));
    }

    public List<ProjReviewLeasePrice> listByProjectIds(List<Long> projectIds) {
        return baseMapper.selectList(
                Wrappers.<ProjReviewLeasePrice>lambdaQuery().in(ProjReviewLeasePrice::getProjectId, projectIds));
    }

    public Long sumApplyByContractId(List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        if (ObjectUtil.isEmpty(ids)){
            return sum.get();
        }
        List<ProjReviewLeasePrice> projReviewLeasePrices = baseMapper.selectList(Wrappers.<ProjReviewLeasePrice>lambdaQuery()
                .in(ProjReviewLeasePrice::getProjectId, ids));
        if(ObjectUtil.isEmpty(projReviewLeasePrices)){
            return sum.get();
        }
        projReviewLeasePrices.forEach(base -> {
            sum.updateAndGet(v -> v + LongUtil.null2zero(base.getApplyCreditAmount()));
        });
        log.info("getStockRiskExposure sumApplyByContractId ProjReviewLeasePrice objects {}", sum.get());
        return sum.get();
    }


    public void add(ProjReviewLeasePrice price) {
        baseMapper.insert(price);
    }
}