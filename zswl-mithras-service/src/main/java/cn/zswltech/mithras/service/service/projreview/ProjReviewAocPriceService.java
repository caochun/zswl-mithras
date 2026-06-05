package cn.zswltech.mithras.service.service.projreview;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceModifyREQ;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewAocPriceMapper;
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
 * @description 债权转让报价方案表
 * @date 2022-08-01
 */
@Service
@Validated
@Slf4j
public class ProjReviewAocPriceService extends ServiceImpl<ProjReviewAocPriceMapper, ProjReviewAocPrice>
        implements ProjReviewUpdateAdvice {

    @Resource
    private ProjReviewPriceConverter priceConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void saveIrr(Long projReviewId, Integer irr) {
        this.saveCheck(projReviewId);
        ProjReviewAocPrice projReviewAocPrice = this.getByProjectId(projReviewId);
        Assert.notNull(projReviewAocPrice, () -> MithrasException.newException("报价方案不存在"));
        projReviewAocPrice.setIrrPercent(irr);
        this.updateById(projReviewAocPrice);
        recordStatus(projReviewId);
    }

    public void modify(@Valid ProjReviewAocPriceModifyREQ req) {
        saveCheck(req.getProjectId());
        saveOrUpdate(priceConverter.aocModifyReqToEntity(req));
        recordStatus(req.getProjectId());
    }

    public ProjReviewAocPrice getByProjectId(Long projectId) {
        return baseMapper.selectOne(
                Wrappers.<ProjReviewAocPrice>lambdaQuery().eq(ProjReviewAocPrice::getProjectId, projectId));
    }

    public List<ProjReviewAocPrice> listByProjectIds(List<Long> projectIds) {
        return baseMapper.selectList(
                Wrappers.<ProjReviewAocPrice>lambdaQuery().in(ProjReviewAocPrice::getProjectId, projectIds));
    }

    public Long sumApplyByContractId(List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        if (ObjectUtil.isEmpty(ids)){
            return sum.get();
        }
        List<ProjReviewAocPrice> infos = baseMapper.selectList(Wrappers.<ProjReviewAocPrice>lambdaQuery()
                .in(ProjReviewAocPrice::getProjectId, ids));
        if(ObjectUtil.isEmpty(infos)){
            return sum.get();
        }
        infos.forEach(base -> {
            sum.updateAndGet(v -> v + LongUtil.null2zero(base.getApplyCreditAmount()));
        });
        log.info("getStockRiskExposure sumApplyByContractId ProjReviewAocPrice objects {}", sum.get());
        return sum.get();
    }

    public void add(ProjReviewAocPrice price) {
        baseMapper.insert(price);
    }
}