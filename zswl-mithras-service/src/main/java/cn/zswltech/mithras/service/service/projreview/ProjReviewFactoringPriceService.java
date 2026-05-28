package cn.zswltech.mithras.service.service.projreview;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceModifyREQ;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewFactoringPriceMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-08-01
 */
@Service
@Validated
@Slf4j
public class ProjReviewFactoringPriceService extends ServiceImpl<ProjReviewFactoringPriceMapper, ProjReviewFactoringPrice>
        implements ProjReviewUpdateAdvice {

    @Resource
    private ProjReviewPriceConverter priceConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void saveIrr(Long projReviewId, Integer irr) {
        this.saveCheck(projReviewId);
        ProjReviewFactoringPrice projReviewFactoringPrice = this.getByProjectId(projReviewId);
        Assert.notNull(projReviewFactoringPrice, () -> MithrasException.newException("报价方案不存在"));
        projReviewFactoringPrice.setIrrPercent(irr);
        this.updateById(projReviewFactoringPrice);
        recordStatus(projReviewId);
    }

    public void modify(@Valid ProjReviewFactoringPriceModifyREQ req) {
        saveCheck(req.getProjectId());
        saveOrUpdate(priceConverter.factoringModifyReqToEntity(req));
        recordStatus(req.getProjectId());
    }

    public ProjReviewFactoringPrice getByProjectId(Long projectId) {
        return baseMapper.selectOne(
                Wrappers.<ProjReviewFactoringPrice>lambdaQuery().eq(ProjReviewFactoringPrice::getProjectId, projectId));
    }

    public List<ProjReviewFactoringPrice> listByProjectIds(List<Long> projectIds) {
        return baseMapper.selectList(
                Wrappers.<ProjReviewFactoringPrice>lambdaQuery().in(ProjReviewFactoringPrice::getProjectId, projectIds));
    }

    public Long sumApplyByContractId(List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        if (ObjectUtil.isEmpty(ids)){
            return sum.get();
        }
        List<ProjReviewFactoringPrice> infos = baseMapper.selectList(Wrappers.<ProjReviewFactoringPrice>lambdaQuery()
                .in(ProjReviewFactoringPrice::getProjectId, ids));
        if(ObjectUtil.isEmpty(infos)){
            return sum.get();
        }
        infos.forEach(base -> {
            sum.updateAndGet(v -> v + LongUtil.null2zero(base.getApplyCreditAmount()));
        });
        log.info("getStockRiskExposure sumApplyByContractId ProjReviewFactoring objects {}", sum.get());
        return sum.get();
    }

    /**
     *
     * @param price
     */
    public void add(ProjReviewFactoringPrice price) {
        baseMapper.insert(price);
    }
}