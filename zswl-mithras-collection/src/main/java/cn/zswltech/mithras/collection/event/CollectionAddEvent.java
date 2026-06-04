package cn.zswltech.mithras.collection.event;

import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

/**
 * @create: 2022-08-29
 **/
@Getter
@Setter
public class CollectionAddEvent  extends ApplicationEvent {

    private CashFlowItemEnum cashFlowItem;

    //名义货价，服务费/咨询费/手续费
    private Long amount;

    private Long contractId;

    private LocalDate planCollectionDate;

    private ProcessModelTypeEnum processModelTypeEnum;

    private String handleType;

    private int phase = 0;

    public CollectionAddEvent(Object source, Long contractId, CashFlowItemEnum cashFlowItem) {
        super(source);
        this.contractId = contractId;
        this.cashFlowItem = cashFlowItem;

    }
    public CollectionAddEvent(Object source, Long contractId, CashFlowItemEnum cashFlowItem,Long amount) {
        super(source);
        this.contractId = contractId;
        this.cashFlowItem = cashFlowItem;
        this.amount = amount;
    }

    public CollectionAddEvent(Object source, Long contractId, CashFlowItemEnum cashFlowItem,Long amount,LocalDate planCollectionDate) {
        super(source);
        this.contractId = contractId;
        this.cashFlowItem = cashFlowItem;
        this.amount = amount;
        this.planCollectionDate = planCollectionDate;
    }

    public CollectionAddEvent(Object source, Long contractId, CashFlowItemEnum cashFlowItem,Long amount,LocalDate planCollectionDate,int phase) {
        super(source);
        this.contractId = contractId;
        this.cashFlowItem = cashFlowItem;
        this.amount = amount;
        this.planCollectionDate = planCollectionDate;
        this.phase = phase;
    }

    public CollectionAddEvent(Object source, Long contractId, CashFlowItemEnum cashFlowItem,Long amount,LocalDate planCollectionDate, String handleType) {
        super(source);
        this.contractId = contractId;
        this.cashFlowItem = cashFlowItem;
        this.amount = amount;
        this.planCollectionDate = planCollectionDate;
        this.handleType = handleType;
    }
}
