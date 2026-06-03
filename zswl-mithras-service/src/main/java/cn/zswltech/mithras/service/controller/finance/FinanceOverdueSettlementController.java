package cn.zswltech.mithras.service.controller.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceOverdueSettlementApi;
import cn.zswltech.mithras.dto.finance.overdue.*;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueSettlement;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueReportBaseService;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueSettlementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 应收逾期结算表
* @author vico
* @date 2025-09-15
*/
@RestController
public class FinanceOverdueSettlementController implements FinanceOverdueSettlementApi {

    @Resource
    private FinanceOverdueSettlementService financeOverdueSettlementService;
    @Resource
    private FinanceOverdueReportBaseService financeOverdueReportBaseService;

    @Override
    public R<Void> add(FinanceOverdueSettlementAddREQ req) {
        financeOverdueSettlementService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(FinanceOverdueSettlementModifyREQ req){
        financeOverdueSettlementService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FinanceOverdueSettlementListRSP>> list(FinanceOverdueSettlementListREQ req){
        Page<FinanceOverdueSettlement> data = financeOverdueSettlementService.list(req);
        List<FinanceOverdueSettlementListRSP> list = BeanUtil.copyToList(data.getRecords(), FinanceOverdueSettlementListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(FinanceOverdueSettlementRemoveREQ req){
        financeOverdueSettlementService.remove(req);
        return R.ok();
    }

    @Override
    public R<List<FinanceOverdueSettlementContractRelationRsp>> contractRelation(FinanceOverdueSettlementContractRelationREQ req) {
        return R.ok(financeOverdueSettlementService.contractRelation(req));
    }

}