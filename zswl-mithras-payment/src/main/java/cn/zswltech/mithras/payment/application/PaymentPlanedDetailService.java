package cn.zswltech.mithras.payment.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.payment.application.convert.PaymentPlanedDetailConverter;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentPlanedDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPlanedDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 计划付款明细表（付款申请 1:n付款明细）
 * @date 2022-08-12
 */
@Service
public class PaymentPlanedDetailService extends ServiceImpl<PaymentPlanedDetailMapper, PaymentPlanedDetail> {
    @Resource
    private PaymentPlanedDetailConverter planedDetailConverter;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Transactional(rollbackFor = Throwable.class)
    public Long add(PlanedDetailDto req) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        List<PaymentPlanedDetail> details = baseMapper.selectList(Wrappers.<PaymentPlanedDetail>lambdaQuery()
                .eq(PaymentPlanedDetail::getPaymentId, req.getPaymentId()));
        long amount = 0L;
        for (PaymentPlanedDetail detail : details) {
            amount += detail.getPaymentAmount();
        }
        if (req.getPaymentAmount() + amount > baseInfo.getApplyPaymentAmount()) {
            throw new MithrasException("付款明细累计已超出付款申请金额");
        }
        PaymentPlanedDetail detail = planedDetailConverter.dtoToEntity(req);
        save(detail);
        return detail.getId();
    }

    public List<PlanedDetailDto> list(Long paymentId) {
        List<PaymentPlanedDetail> pageData =
                baseMapper.selectList(Wrappers.<PaymentPlanedDetail>lambdaQuery()
                        .eq(PaymentPlanedDetail::getPaymentId, paymentId));
        return planedDetailConverter.entitiesToDtos(pageData);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateBatch(List<PlanedDetailDto> planedDetailDtos) {
        if (ObjectUtil.isNotEmpty(planedDetailDtos)) {
            List<PaymentPlanedDetail> details = planedDetailConverter.dtosToEntities(planedDetailDtos);
            saveOrUpdateBatch(details);
        }
    }

    public void updateBatchById(List<PlanedDetailDto> planedDetailDtos) {
        List<PaymentPlanedDetail> details = planedDetailConverter.dtosToEntities(planedDetailDtos);
        updateBatchById(details);
    }

    public void saveBatch(List<PlanedDetailDto> planedDetailDtos) {
        List<PaymentPlanedDetail> details = planedDetailConverter.dtosToEntities(planedDetailDtos);
        saveBatch(details);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(PlanedDetailDto req) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        List<PaymentPlanedDetail> details = baseMapper.selectList(Wrappers.<PaymentPlanedDetail>lambdaQuery()
                .eq(PaymentPlanedDetail::getPaymentId, req.getPaymentId()));
        long amount = 0L;
        for (PaymentPlanedDetail detail : details) {
            amount += detail.getPaymentAmount();
        }
        if (req.getPaymentAmount() + amount > baseInfo.getApplyPaymentAmount()) {
            throw new MithrasException("付款明细累计已超出付款申请金额");
        }
        PaymentPlanedDetail detail = planedDetailConverter.dtoToEntity(req);
        baseMapper.updateAnnotationIncludeNullById(detail);
        return;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void removeByPaymentId(Long paymentId) {
        remove(Wrappers.<PaymentPlanedDetail>lambdaQuery().eq(PaymentPlanedDetail::getPaymentId, paymentId));
    }
}
