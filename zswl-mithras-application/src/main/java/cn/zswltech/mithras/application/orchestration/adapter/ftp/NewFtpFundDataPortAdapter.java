package cn.zswltech.mithras.application.orchestration.adapter.ftp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListREQ;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListRSP;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingApplicationService;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingPledgeInfoMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.financing.FundFinancingBaseInfoLibMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.ftp.newftp.service.port.DirectFinancingCostSample;
import cn.zswltech.mithras.ftp.newftp.service.port.GuaranteeCostSample;
import cn.zswltech.mithras.ftp.newftp.service.port.IndirectFinancingCostSample;
import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpFundDataPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NewFtpFundDataPortAdapter implements NewFtpFundDataPort {

    @Resource
    private FundFinancingBaseInfoLibMapper fundFinancingBaseInfoLibMapper;
    @Resource
    private FundFinancingPlanLibMapper fundFinancingPlanLibMapper;
    @Resource
    private FundDirectFinancingBaseInfoMapper directFinancingBaseInfoMapper;
    @Resource
    private FundFinancingApplicationService fundFinancingApplicationService;
    @Resource
    private FundFinancingPledgeInfoMapper fundFinancingPledgeInfoMapper;
    @Resource
    private FundDirectFinancingPledgeInfoMapper fundDirectFinancingPledgeInfoMapper;

    @Override
    public List<IndirectFinancingCostSample> listIndirectFinancingCostSamples(LocalDate beginDate, LocalDate endDate) {
        List<FundFinancingBaseInfoLib> baseInfoLibs = fundFinancingBaseInfoLibMapper.selectList(Wrappers.<FundFinancingBaseInfoLib>lambdaQuery()
                .between(FundFinancingBaseInfo::getActualLoanDate, beginDate, endDate)
                .notIn(FundFinancingBaseInfo::getBusinessType, Arrays.asList(
                        FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(),
                        FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name(),
                        FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name()))
                .eq(FundFinancingBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL));
        if (CollectionUtil.isEmpty(baseInfoLibs)) {
            return Collections.emptyList();
        }

        Map<Long, FundFinancingBaseInfoLib> latestBaseInfoLibMap = new HashMap<>(8);
        baseInfoLibs.stream().collect(Collectors.groupingBy(FundFinancingBaseInfoLib::getOriginId))
                .forEach((id, libs) -> {
                    libs.sort(Comparator.comparing(FundFinancingBaseInfoLib::getVersion));
                    latestBaseInfoLibMap.put(id, libs.get(libs.size() - 1));
                });

        return latestBaseInfoLibMap.entrySet().stream()
                .map(entry -> fundFinancingPlanLibMapper.selectOne(Wrappers.<FundFinancingPlanLib>lambdaQuery()
                        .eq(FundFinancingPlanLib::getVersion, entry.getValue().getVersion())
                        .eq(FundFinancingPlan::getFinancingId, entry.getKey())
                        .eq(FundFinancingPlanLib::getVersionType, VersionTypeConstants.NORMAL)
                        .last(StringUtil.mysqlLimitOne())))
                .filter(Objects::nonNull)
                .map(plan -> BeanUtil.copyProperties(plan, IndirectFinancingCostSample.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DirectFinancingCostSample> listDirectFinancingCostSamples(LocalDate beginDate, LocalDate endDate) {
        return directFinancingBaseInfoMapper.selectList(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                        .between(FundDirectFinancingBaseInfo::getDurationFrom, beginDate, endDate))
                .stream()
                .map(baseInfo -> BeanUtil.copyProperties(baseInfo, DirectFinancingCostSample.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GuaranteeCostSample> listGuaranteeCostSamples(LocalDate month) {
        LocalDateTime endDate = LocalDateTime.of(month.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
        FundFinancingListREQ req = new FundFinancingListREQ();
        req.setActualLoanDateTo(endDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        R<FundFinancingListRSP> response = fundFinancingApplicationService.pageList(req);
        if (Objects.isNull(response) || Objects.isNull(response.getData()) || Objects.isNull(response.getData().getRecords())) {
            return Collections.emptyList();
        }
        PageR<FundFinancingListRSP.FundFinancingList> records = response.getData().getRecords();
        if (CollectionUtil.isEmpty(records.getList())) {
            return Collections.emptyList();
        }
        return records.getList().stream()
                .filter(base -> FundFinancingStatusEnum.EFFECT.name().equals(base.getFinancingStatus())
                        || FundFinancingStatusEnum.CARRY_INTEREST.name().equals(base.getFinancingStatus()))
                .map(base -> BeanUtil.copyProperties(base, GuaranteeCostSample.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsPledge(Set<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return false;
        }
        List<FundFinancingPledgeInfo> pledgeInfoList = fundFinancingPledgeInfoMapper.selectList(
                Wrappers.<FundFinancingPledgeInfo>lambdaQuery().in(FundFinancingPledgeInfo::getContractId, contractIds));
        return CollectionUtil.isNotEmpty(pledgeInfoList) && pledgeInfoList.stream().anyMatch(pledgeInfo -> Boolean.TRUE.equals(pledgeInfo.getIsPledge()));
    }

    @Override
    public boolean existsDirectPledge(Set<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return false;
        }
        List<FundDirectFinancingPledgeInfo> pledgeInfoList = fundDirectFinancingPledgeInfoMapper.selectList(
                Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().in(FundDirectFinancingPledgeInfo::getContractId, contractIds));
        return CollectionUtil.isNotEmpty(pledgeInfoList) && pledgeInfoList.stream().anyMatch(pledgeInfo -> Boolean.TRUE.equals(pledgeInfo.getIsPledge()));
    }
}
