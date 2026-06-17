package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.zswltech.mithras.associationreport.application.AssociationReportCollectionFactPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportCollectionSnapshot;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssociationReportCollectionFactPortAdapter implements AssociationReportCollectionFactPort {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Override
    public List<AssociationReportCollectionSnapshot> listByContractIds(List<Long> contractIds) {
        return collectionBaseInfoService.listByContractIds(contractIds).stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssociationReportCollectionSnapshot> listRentByReceiptId(Long receiptId) {
        return collectionBaseInfoService.listRentByReceiptId(receiptId).stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private AssociationReportCollectionSnapshot toSnapshot(CollectionBaseInfo collectionBaseInfo) {
        return AssociationReportCollectionSnapshot.builder()
                .receiptId(collectionBaseInfo.getReceiptId())
                .paymentId(collectionBaseInfo.getPaymentId())
                .cashFlowItem(collectionBaseInfo.getCashFlowItem())
                .planCollectionDate(collectionBaseInfo.getPlanCollectionDate())
                .planCollectionAmount(collectionBaseInfo.getPlanCollectionAmount())
                .collectionAmount(collectionBaseInfo.getCollectionAmount())
                .collectionPrincipal(collectionBaseInfo.getCollectionPrincipal())
                .build();
    }
}
