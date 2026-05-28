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
import cn.zswltech.mithras.service.enums.dashboard.DashboardCardGroupEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.mapper.dashboard.DashboardFundFinanceMapper;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardFundCreditQuery;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardFundCreditResult;
import cn.zswltech.mithras.service.mapper.model.fund.FundCredit;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.service.service.dashboard.DashboardFundFinanceService;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

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
    private DashboardFundFinanceMapper dashboardFundFinanceMapper;

    @Override
    public void generate(Long mainId, LocalDate dataTime) {
        // 获取接口数据
        DashboardFundCreditQuery query = new DashboardFundCreditQuery();
        query.setQueryDate(dataTime);
        List<DashboardFundCreditResult> dbList = dashboardFundFinanceMapper.listCredit(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return;
        }
        // 取出所有授信ID计算
        Set<Long> creditIds = dbList.stream().map(DashboardFundCreditResult::getId).collect(Collectors.toSet());
        List<FundCredit> fundCreditList = null;
        if (CollUtil.isNotEmpty(creditIds)) {
            fundCreditList = SpringUtil.getBean(FundCreditService.class).listByIds(creditIds);
        }
        Map<Long, CreditLimitDetailBO> creditLimitDetailBoMap = getBean(FundCreditService.class).queryLimitDetailBatch(fundCreditList, false);
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
            CreditLimitDetailBO creditLimitDetail = creditLimitDetailBoMap.get(e.getId());
            // 复用授信列表逻辑，已使用额度统一都按照可循环计算
            Long usedLimit = Optional.ofNullable(creditLimitDetail).map(CreditLimitDetailBO::getOccupyTotalLimit).orElse(0L);
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

