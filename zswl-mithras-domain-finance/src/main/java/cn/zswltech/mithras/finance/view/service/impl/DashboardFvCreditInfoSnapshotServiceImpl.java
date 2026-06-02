package cn.zswltech.mithras.finance.view.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceCreditInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceCreditInfoRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.finance.view.entity.DashboardFvCardSnapshot;
import cn.zswltech.mithras.finance.view.entity.DashboardFvCreditInfoSnapshot;
import cn.zswltech.mithras.finance.view.mapper.DashboardFvCreditInfoSnapshotMapper;
import cn.zswltech.mithras.finance.view.service.DashboardFvCardSnapshotService;
import cn.zswltech.mithras.finance.view.service.DashboardFvCreditInfoSnapshotService;
import cn.zswltech.mithras.finance.view.service.DashboardFundFinanceDataProvider;
import cn.zswltech.mithras.finance.view.service.dto.DashboardFundFinanceCreditSnapshotData;
import cn.zswltech.mithras.service.enums.dashboard.DashboardCardGroupEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardFundCreditQuery;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作台授信信息快照表(DashboardFvCreditInfoSnapshot)表服务实现类
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
@Service
public class DashboardFvCreditInfoSnapshotServiceImpl extends ServiceImpl<DashboardFvCreditInfoSnapshotMapper, DashboardFvCreditInfoSnapshot> implements DashboardFvCreditInfoSnapshotService {

    @Resource
    private DashboardFvCreditInfoSnapshotService thisService;
    @Resource
    private DashboardFundFinanceDataProvider dashboardFundFinanceDataProvider;

    @Override
    public void generate(Long mainId, LocalDate dataTime) {
        List<DashboardFundFinanceCreditSnapshotData> dbList = dashboardFundFinanceDataProvider.listCreditSnapshotData(dataTime);
        if (CollectionUtil.isEmpty(dbList)) {
            return;
        }
        List<DashboardFvCreditInfoSnapshot> creditInfoSnapshotList = dbList.stream().map(e -> {
            DashboardFvCreditInfoSnapshot rsp = new DashboardFvCreditInfoSnapshot();
            rsp.setCardId(mainId);
            rsp.setCreditCode(e.getCreditCode());
            rsp.setOrgName(e.getOrganizationName());
            rsp.setFinancingBizTypeCode(e.getBusinessType());
            rsp.setFinancingBizTypeDisplay(Optional.ofNullable(FundFinancingBizTypeEnum.finaByName(e.getBusinessType())).map(FundFinancingBizTypeEnum::display).orElse(""));
            if (Objects.nonNull(e.getTotalCreditLimit())) {
                rsp.setCreditTotalAmount(new BigDecimal(LongUtil.null2zero(e.getTotalCreditLimit())).divide(new BigDecimal(10000), 6, RoundingMode.HALF_UP));
            }
            Long usedLimit = LongUtil.null2zero(e.getUsedCreditLimit());
            rsp.setCreditUsedAmount(new BigDecimal(usedLimit).divide(new BigDecimal(10000), 6, RoundingMode.HALF_UP));
            rsp.setIsCycle(e.getRecyclable());
            rsp.setDeadline(e.getDeadline());
            return rsp;
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(creditInfoSnapshotList)) {
            thisService.saveBatch(creditInfoSnapshotList);
        }
    }

    @Override
    public List<DashboardFundFinanceCreditInfoRSP> listCredit(DashboardFundCreditQuery query) {
        DashboardFvCardSnapshot cardSnapshot = SpringUtil.getBean(DashboardFvCardSnapshotService.class).getOne(Wrappers.<DashboardFvCardSnapshot>lambdaQuery()
                .eq(DashboardFvCardSnapshot::getDataTime, query.getQueryDate())
                .eq(DashboardFvCardSnapshot::getCardCode, DashboardCardGroupEnum.FUND_FINANCE_CREDIT.name()));
        if (cardSnapshot == null) {
            return Collections.emptyList();
        }
        List<DashboardFvCreditInfoSnapshot> creditInfoSnapshotList = baseMapper.selectList(Wrappers.<DashboardFvCreditInfoSnapshot>lambdaQuery()
                .in(CollUtil.isNotEmpty(query.getIds()), DashboardFvCreditInfoSnapshot::getId, query.getIds())
                .like(CharSequenceUtil.isNotBlank(query.getCreditCode()), DashboardFvCreditInfoSnapshot::getCreditCode, query.getCreditCode())
                .like(CharSequenceUtil.isNotBlank(query.getOrganizationName()), DashboardFvCreditInfoSnapshot::getOrgName, query.getOrganizationName()));
        if (CollUtil.isEmpty(creditInfoSnapshotList)) {
            return Collections.emptyList();
        }
        return creditInfoSnapshotList.stream().map(e -> {
            DashboardFundFinanceCreditInfoRSP rsp = new DashboardFundFinanceCreditInfoRSP();
            rsp.setId(e.getId());
            rsp.setCreditCode(e.getCreditCode());
            rsp.setOrgName(e.getOrgName());
            rsp.setFinancingBizTypeCode(e.getFinancingBizTypeCode());
            rsp.setFinancingBizTypeDisplay(e.getFinancingBizTypeDisplay());
            rsp.setCreditUsedAmount(Optional.ofNullable(e.getCreditUsedAmount())
                    .map(a -> new ValueUnitDTO(a.divide(BigDecimal.valueOf(10000), 0, RoundingMode.HALF_UP).toPlainString(), "万元"))
                    .orElse(new ValueUnitDTO("0", "万元")));
            rsp.setCreditTotalAmount(Optional.ofNullable(e.getCreditTotalAmount())
                    .map(a -> new ValueUnitDTO(a.divide(BigDecimal.valueOf(10000), 0, RoundingMode.HALF_UP).toPlainString(), "万元"))
                    .orElse(new ValueUnitDTO("0", "万元")));
            rsp.setIsCycle(e.getIsCycle());
            rsp.setDeadline(e.getDeadline());
            return rsp;
        }).collect(Collectors.toList());
    }
}

