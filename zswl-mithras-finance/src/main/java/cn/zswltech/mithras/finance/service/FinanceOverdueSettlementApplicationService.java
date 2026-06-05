package cn.zswltech.mithras.finance.service;

import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementAddREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementContractRelationREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementContractRelationRsp;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementListREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementModifyREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementRemoveREQ;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueSettlement;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface FinanceOverdueSettlementApplicationService {

    void add(FinanceOverdueSettlementAddREQ req);

    void modify(FinanceOverdueSettlementModifyREQ req);

    Page<FinanceOverdueSettlement> list(FinanceOverdueSettlementListREQ req);

    void remove(FinanceOverdueSettlementRemoveREQ req);

    List<FinanceOverdueSettlementContractRelationRsp> contractRelation(FinanceOverdueSettlementContractRelationREQ req);
}
