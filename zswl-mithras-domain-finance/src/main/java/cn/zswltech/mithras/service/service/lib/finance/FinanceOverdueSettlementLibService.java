package cn.zswltech.mithras.service.service.lib.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueSettlementListREQ;
import cn.zswltech.mithras.service.mapper.lib.finance.FinanceOverdueSettlementLibMapper;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueSettlement;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueSettlementLib;
import cn.zswltech.mithras.service.service.lib.finance.handle.FinanceOverdueSettlementLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName FinanceOverdueSettlementLibService
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/9/19 09:07
 * @Version 1.0
 **/
@Service
public class FinanceOverdueSettlementLibService extends ServiceImpl<FinanceOverdueSettlementLibMapper, FinanceOverdueSettlementLib> {

    @Resource
    private FinanceOverdueSettlementLibHandler financeOverdueSettlementLibHandler;

    public Page<FinanceOverdueSettlement> pageByVersion(FinanceOverdueSettlementListREQ req,List<Long> ids) {
        Page<FinanceOverdueSettlementLib> pageRsp = this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FinanceOverdueSettlementLib>lambdaQuery()
                .eq(FinanceOverdueSettlementLib::getOverdueReportId, req.getOverdueReportId())
                .like(ObjectUtil.isNotEmpty(req.getCollectionCode()), FinanceOverdueSettlementLib::getCollectionCode, req.getCollectionCode())
                .eq(ObjectUtil.isNotEmpty(req.getRecordStatus()), FinanceOverdueSettlementLib::getRecordStatus, req.getRecordStatus())
                .in(CollectionUtil.isNotEmpty(ids), FinanceOverdueSettlementLib::getOriginId, ids)
                .between(ObjectUtil.isNotEmpty(req.getRecordBillDateFrom()), FinanceOverdueSettlementLib::getRecordBillDate, req.getRecordBillDateFrom(), req.getRecordBillDateTo())
                .eq(FinanceOverdueSettlementLib::getVersion, req.getVersion())
                        .orderByDesc(FinanceOverdueSettlementLib::getContractCode)
                        .orderByDesc(FinanceOverdueSettlementLib::getSettlementDate)
                );
        Page<FinanceOverdueSettlement> rsps = BeanUtil.copyProperties(pageRsp, Page.class, "records");
        if (ObjectUtil.isNotEmpty(pageRsp.getRecords())) {
            List<FinanceOverdueSettlement> collect = pageRsp.getRecords().stream().map(financeOverdueSettlementLibHandler::actualLib2Entity).collect(Collectors.toList());
            rsps.setRecords(collect);
        }
        return rsps;
    }

}
