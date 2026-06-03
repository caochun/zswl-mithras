package cn.zswltech.mithras.finance.service.lib.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationListREQ;
import cn.zswltech.mithras.finance.mapper.lib.finance.FinanceOverdueIntegrationLibMapper;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueIntegration;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueIntegrationLib;
import cn.zswltech.mithras.finance.service.lib.finance.handle.FinanceOverdueIntegrationLibHandler;
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
public class FinanceOverdueIntegrationLibService extends ServiceImpl<FinanceOverdueIntegrationLibMapper, FinanceOverdueIntegrationLib> {

    @Resource
    private FinanceOverdueIntegrationLibHandler financeOverdueIntegrationLibHandler;

    public Page<FinanceOverdueIntegration> pageByVersion(FinanceOverdueIntegrationListREQ req, List<Long> ids) {
        Page<FinanceOverdueIntegrationLib> pageRsp = this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FinanceOverdueIntegrationLib>lambdaQuery()
                .eq(FinanceOverdueIntegrationLib::getOverdueReportId, req.getOverdueReportId())
                .eq(ObjectUtil.isNotEmpty(req.getCollectionCode()), FinanceOverdueIntegrationLib::getCollectionCode, req.getCollectionCode())
                .eq(ObjectUtil.isNotEmpty(req.getRecordStatus()), FinanceOverdueIntegrationLib::getRecordStatus, req.getRecordStatus())
                .in(CollectionUtil.isNotEmpty(ids), FinanceOverdueIntegrationLib::getId, ids)
                .between(ObjectUtil.isNotEmpty(req.getRecordStartDateFrom()), FinanceOverdueIntegrationLib::getRecordStartDate, req.getRecordStartDateFrom(), req.getRecordStartDateTo())
                .eq(FinanceOverdueIntegrationLib::getVersion, req.getVersion())
                .orderByDesc(FinanceOverdueIntegrationLib::getContractId)
                .orderByDesc(FinanceOverdueIntegrationLib::getRecordStartDate)
                .in(CollectionUtil.isNotEmpty(ids), FinanceOverdueIntegrationLib::getOriginId, ids));
        Page<FinanceOverdueIntegration> rsps = BeanUtil.copyProperties(pageRsp, Page.class, "records");
       if (ObjectUtil.isNotEmpty(pageRsp.getRecords())) {
           List<FinanceOverdueIntegration> collect = pageRsp.getRecords().stream().map(financeOverdueIntegrationLibHandler::actualLib2Entity).collect(Collectors.toList());
           rsps.setRecords(collect);
       }
       return rsps;
    }
}
