package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlRelatedTransactionFact;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlRelatedTransactionPort;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlRelatedTransactionQuery;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RiskControlRelatedTransactionPortAdapter implements RiskControlRelatedTransactionPort {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    @Override
    public PageR<RiskControlRelatedTransactionFact> paymentTransactions(RiskControlRelatedTransactionQuery query) {
        Page<PaymentBaseInfo> page = paymentBaseInfoMapper.selectPage(new Page<>(query.getPage(), query.getPageSize()),
                Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .in(PaymentBaseInfo::getClientId, query.getClientIds())
                        .in(PaymentBaseInfo::getWriteOffStatus,
                                Arrays.asList(PaymentWriteOffStatus.WRITTEN_OFF.name(),
                                        PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))
                        .ge(ObjectUtil.isNotEmpty(query.getTransactionDateFrom()), PaymentBaseInfo::getPaidInDate,
                                query.getTransactionDateFrom())
                        .le(ObjectUtil.isNotEmpty(query.getTransactionDateTo()), PaymentBaseInfo::getPaidInDate,
                                query.getTransactionDateTo())
                        .like(ObjectUtil.isNotEmpty(query.getContractCode()), PaymentBaseInfo::getContractCode,
                                query.getContractCode())
                        .ge(ObjectUtil.isNotEmpty(query.getTransactionAmountFrom()),
                                PaymentBaseInfo::getApplyPaymentAmount, query.getTransactionAmountFrom())
                        .le(ObjectUtil.isNotEmpty(query.getTransactionAmountTo()), PaymentBaseInfo::getApplyPaymentAmount,
                                query.getTransactionAmountTo())
                        .orderByDesc(PaymentBaseInfo::getPaidInDate)
                        .orderByDesc(PaymentBaseInfo::getApplyPaymentDate));
        return PageR.of(page, paymentFacts(page.getRecords()));
    }

    @Override
    public PageR<RiskControlRelatedTransactionFact> collectionTransactions(RiskControlRelatedTransactionQuery query) {
        Page<CollectionBaseInfo> page = collectionBaseInfoMapper.selectPage(new Page<>(query.getPage(), query.getPageSize()),
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getClientId, query.getClientIds())
                        .in(CollectionBaseInfo::getWriteOffStatus,
                                Arrays.asList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(),
                                        CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()))
                        .ge(ObjectUtil.isNotEmpty(query.getTransactionDateFrom()), CollectionBaseInfo::getCollectionDate,
                                query.getTransactionDateFrom())
                        .le(ObjectUtil.isNotEmpty(query.getTransactionDateTo()), CollectionBaseInfo::getCollectionDate,
                                query.getTransactionDateTo())
                        .like(ObjectUtil.isNotEmpty(query.getContractCode()), CollectionBaseInfo::getContractCode,
                                query.getContractCode())
                        .ge(ObjectUtil.isNotEmpty(query.getTransactionAmountFrom()),
                                CollectionBaseInfo::getCollectionAmount, query.getTransactionAmountFrom())
                        .le(ObjectUtil.isNotEmpty(query.getTransactionAmountTo()),
                                CollectionBaseInfo::getCollectionAmount, query.getTransactionAmountTo())
                        .orderByDesc(CollectionBaseInfo::getCollectionDate));
        return PageR.of(page, collectionFacts(page.getRecords()));
    }

    private List<RiskControlRelatedTransactionFact> paymentFacts(List<PaymentBaseInfo> records) {
        return records.stream().map(record -> {
            RiskControlRelatedTransactionFact fact = new RiskControlRelatedTransactionFact();
            fact.setClientId(record.getClientId());
            fact.setContractCode(record.getContractCode());
            fact.setCashFlowCode(record.getPaymentCode());
            fact.setTransactionAmount(record.getApplyPaymentAmount());
            fact.setTransactionDate(record.getPaidInDate());
            return fact;
        }).collect(Collectors.toList());
    }

    private List<RiskControlRelatedTransactionFact> collectionFacts(List<CollectionBaseInfo> records) {
        return records.stream().map(record -> {
            RiskControlRelatedTransactionFact fact = new RiskControlRelatedTransactionFact();
            fact.setClientId(record.getClientId());
            fact.setContractCode(record.getContractCode());
            fact.setCashFlowCode(record.getCode());
            fact.setTransactionAmount(record.getCollectionAmount());
            fact.setTransactionDate(record.getCollectionDate());
            return fact;
        }).collect(Collectors.toList());
    }
}
