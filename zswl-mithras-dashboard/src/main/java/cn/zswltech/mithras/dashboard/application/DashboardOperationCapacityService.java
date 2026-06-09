package cn.zswltech.mithras.dashboard.application;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.DashboardAdjustPersonInfoMapper;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.DashboardOperationPayMapper;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardAdjustPersonInfo;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardAdjustPersonLatestQuery;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardOperationCapacityQuery;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardOperationCapacityResult;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.dashboard.application.util.DashboardAmountUtil;
import cn.zswltech.mithras.dashboard.application.util.DashboardOperationUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardOperationCapacityService implements DashboardOperationCapacityApplicationService {

    @Resource
    private DashboardOperationPayMapper dashboardOperationPayMapper;
    @Resource
    private DashboardAdjustPersonInfoMapper adjustPersonInfoMapper;
    @Resource
    private SysUserService sysUserService;

    public List<DashboardOperationCapacityListRSP> capacityList(DashboardOperationCapacityListREQ req) {
        DashboardOperationCapacityQuery query = buildQuery(req);
        if(CollectionUtils.isNotEmpty(req.getBizDeptIdList())) {
            query.setBizDeptIdList(req.getBizDeptIdList());
        }
        List<DashboardOperationCapacityResult> resultList =  dashboardOperationPayMapper.capacityList(query);
        if(CollectionUtils.isEmpty(resultList)){
            resultList = new ArrayList<>();
        }
        List<DashboardOperationCapacityListRSP> rspList = new ArrayList<>();
        Map<Long, List<DashboardOperationCapacityResult>> resultMap = resultList.stream().filter(f -> f.getType().equals("CURRENCY"))
                .collect(Collectors.groupingBy(DashboardOperationCapacityResult::getBizDeptId));
        List<DashboardAdjustPersonInfo> latest = adjustPersonInfoMapper.getLatest(new DashboardAdjustPersonLatestQuery(req.getType(), query.getBizDeptIdList()));
        Map<Long, BigDecimal> adjustPersonMap = DashboardOperationUtil.getAdjustPerson(latest);
        for (OrgDO orgDO : sysUserService.listBizDeptSort(req.getType())) {
            if (Objects.equals(orgDO.getState(), YesOrNoNumberEnum.NO.getCode())) {
                continue;
            }
            if(CollectionUtils.isNotEmpty(req.getBizDeptIdList()) && !req.getBizDeptIdList().contains(orgDO.getId())){
                continue;
            }
            BigDecimal adjustPersonSum = adjustPersonMap.getOrDefault(orgDO.getId(),BigDecimal.ZERO);
            DashboardOperationCapacityListRSP rsp = new DashboardOperationCapacityListRSP();
            List<DashboardOperationCapacityResult> currencyList = resultMap.get(orgDO.getId());
            if(CollectionUtils.isNotEmpty(currencyList)){
                int projSum = currencyList.size();
                long amountSum = currencyList.stream().mapToLong(DashboardOperationCapacityResult::getAmountLong).sum();
                if(!Objects.equals(adjustPersonSum, BigDecimal.ZERO)) {
                    BigDecimal personAverageAmountSum = BigDecimal.valueOf(amountSum).divide(adjustPersonSum, 2, RoundingMode.HALF_UP);
                    BigDecimal personAverageProjSum = BigDecimal.valueOf(projSum).divide(adjustPersonSum, 2, RoundingMode.HALF_UP);
                    rsp.setPersonAverageProjSum(new ValueUnitDTO(personAverageProjSum.toPlainString(),"个"));
                    rsp.setPersonAverageAmountSum(new ValueUnitDTO(DashboardAmountUtil.toWanYuanWithoutSplit(personAverageAmountSum.longValue()),"万元"));
                }
                if(projSum != 0) {
                    BigDecimal pieceAverageAmountSum = BigDecimal.valueOf(amountSum).divide(new BigDecimal(projSum), 2, RoundingMode.HALF_UP);
                    rsp.setPieceAverageAmountSum(new ValueUnitDTO(DashboardAmountUtil.toWanYuanWithoutSplit(pieceAverageAmountSum.longValue()),"万元"));
                }
                rsp.setProjSum(new ValueUnitDTO(String.valueOf(projSum),"个"));
                rsp.setAmountSum(new ValueUnitDTO(DashboardAmountUtil.toWanYuanWithoutSplit(amountSum),"万元"));
            }

            rsp.setAdjustPersonSum(new ValueUnitDTO(String.valueOf(adjustPersonSum),"个"));
            rsp.setBizDeptId(orgDO.getId());
            rsp.setBizDeptName(orgDO.getName());
            rspList.add(rsp);
        }
        return rspList;
    }


    public List<DashboardOperationCapacityStatisticsRSP> statistics(DashboardOperationCapacityStatisticsREQ req) {
        DashboardOperationCapacityQuery query = buildQuery(req);
        List<DashboardOperationCapacityResult> resultList =  Optional.ofNullable(dashboardOperationPayMapper.capacityList(query)).orElse(new ArrayList<>());
        List<DashboardOperationCapacityStatisticsRSP> rspList = new ArrayList<>();
        Map<String, Map<Long, List<DashboardOperationCapacityResult>>> resultMap = resultList.stream().collect(Collectors.groupingBy(DashboardOperationCapacityResult::getType,
                Collectors.groupingBy(DashboardOperationCapacityResult::getBizDeptId)));
        Map<Long, List<DashboardOperationCapacityResult>> currency = resultMap.get("CURRENCY");
        Map<Long, List<DashboardOperationCapacityResult>> last = resultMap.get("LAST");
        List<DashboardAdjustPersonInfo> latest = adjustPersonInfoMapper.getLatest(new DashboardAdjustPersonLatestQuery(req.getType(), query.getBizDeptIdList()));
        Map<Long, BigDecimal> adjustPersonMap = DashboardOperationUtil.getAdjustPerson(latest);
        for (OrgDO orgDO : sysUserService.listBizDeptSort(req.getType())) {
            if (Objects.equals(orgDO.getState(), YesOrNoNumberEnum.NO.getCode())) {
                continue;
            }
            BigDecimal adjustPersonSum = adjustPersonMap.getOrDefault(orgDO.getId(),BigDecimal.ZERO);
            DashboardOperationCapacityStatisticsRSP rsp = new DashboardOperationCapacityStatisticsRSP();
            List<DashboardOperationCapacityResult> resultCurrencyList = Optional.ofNullable(currency).orElse(new HashMap<>()).get(orgDO.getId());
            List<DashboardOperationCapacityResult> resultLastList = Optional.ofNullable(last).orElse(new HashMap<>()).get(orgDO.getId());
            if(CollectionUtils.isNotEmpty(resultCurrencyList)){
                long currencySum = resultCurrencyList.stream().mapToLong(DashboardOperationCapacityResult::getAmountLong).sum();
                int currencySize = resultCurrencyList.size();
                if(!Objects.equals(adjustPersonSum,BigDecimal.ZERO)) {
                    BigDecimal personAverageAmountSum = BigDecimal.valueOf(currencySum).divide(adjustPersonSum, 2, RoundingMode.HALF_UP);
                    BigDecimal personAverageProjSum = BigDecimal.valueOf(currencySize).divide(adjustPersonSum, 2, RoundingMode.HALF_UP);
                    rsp.setPersonAverageAmountSum(new ValueUnitDTO(DashboardAmountUtil.toYiYuanWithoutSplit(personAverageAmountSum.longValue()),"亿元"));
                    rsp.setPersonAverageProjSum(new ValueUnitDTO(personAverageProjSum.toPlainString(),"个"));
                }
                BigDecimal pieceAverageAmountSum = BigDecimal.valueOf(currencySum).divide(new BigDecimal(currencySize), 2, RoundingMode.HALF_UP);
                rsp.setAmountSum(new ValueUnitDTO(DashboardAmountUtil.toYiYuanWithoutSplit(currencySum),"亿元"));


                rsp.setPieceAverageAmountSum(new ValueUnitDTO(DashboardAmountUtil.toYiYuanWithoutSplit(pieceAverageAmountSum.longValue()),"亿元"));
                rsp.setProjSum(new ValueUnitDTO(String.valueOf(currencySize),"个"));
            }
            if(CollectionUtils.isNotEmpty(resultLastList)){
                long lastSum = resultLastList.stream().mapToLong(DashboardOperationCapacityResult::getAmountLong).sum();
                int lastSize = resultLastList.size();
                if(!Objects.equals(adjustPersonSum,BigDecimal.ZERO)) {
                    BigDecimal personAverageAmountSum = BigDecimal.valueOf(lastSum).divide(adjustPersonSum, 2, RoundingMode.HALF_UP);
                    BigDecimal personAverageProjSum = BigDecimal.valueOf(lastSize).divide(adjustPersonSum, 2, RoundingMode.HALF_UP);
                    rsp.setLastPersonAverageAmountSum(new ValueUnitDTO(DashboardAmountUtil.toYiYuanWithoutSplit(personAverageAmountSum.longValue()),"亿元"));
                    rsp.setLastPersonAverageProjSum(new ValueUnitDTO(personAverageProjSum.toPlainString(),"个"));
                }
                BigDecimal pieceAverageAmountSum = BigDecimal.valueOf(lastSum).divide(new BigDecimal(lastSize), 2, RoundingMode.HALF_UP);
                rsp.setLastPieceAverageAmountSum(new ValueUnitDTO(DashboardAmountUtil.toYiYuanWithoutSplit(pieceAverageAmountSum.longValue()),"亿元"));
                rsp.setLastAmountSum(new ValueUnitDTO(DashboardAmountUtil.toYiYuanWithoutSplit(lastSum), "亿元"));
                rsp.setLastProjSum(new ValueUnitDTO(String.valueOf(lastSize),"个"));
            }
            rsp.setBizDeptId(orgDO.getId());
            rsp.setBizDeptName(orgDO.getName());
            rspList.add(rsp);
        }
        return rspList;
    }

    private DashboardOperationCapacityQuery buildQuery(DashboardOperationBaseREQ req) {
        DashboardOperationCapacityQuery query = BeanUtil.copyProperties(req, DashboardOperationCapacityQuery.class);
        query.setQueryDateLastFrom(req.getQueryDateFrom().minusYears(1));
        query.setQueryDateLastTo(req.getQueryDateTo().minusYears(1));
        if(req.getType() != null) {
            List<String> riskControlIndustryClassify = DashboardOperationUtil.getRiskControlIndustryClassify(req);
            query.setRiskControlList(riskControlIndustryClassify);
            if(DashboardOperationBaseREQ.publicType.equals(req.getType())) {
                List<OrgDO> orgDOList = sysUserService.listBizDeptSort(req.getType());
                List<Long> orgIdList = orgDOList.stream().filter(f -> Arrays.asList("浙江业务部", "公用事业业务部").contains(f.getName()))
                        .map(OrgDO::getId).collect(Collectors.toList());
                query.setBizDeptIdList((orgIdList));
            }
        }
        return query;
    }


}
