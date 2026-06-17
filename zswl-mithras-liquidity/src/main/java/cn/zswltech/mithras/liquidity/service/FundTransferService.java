package cn.zswltech.mithras.liquidity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.*;
import cn.zswltech.mithras.liquidity.enums.LiquidityBankAccountType;
import cn.zswltech.mithras.liquidity.enums.SettingTimeEnum;
import cn.zswltech.mithras.liquidity.mapper.AccountBalanceBaseInfoMapper;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/12/17 19:15
 * @description
 */
@Slf4j
@Service
public class FundTransferService implements FundTransferApplicationService {

    @Resource
    private AccountBalanceBaseInfoMapper accountBalanceBaseInfoMapper;
    @Resource
    private FinancingRepayInfoPort financingRepayInfoPort;
    @Resource
    private FundTransferBaseDataPort fundTransferBaseDataPort;

    @Override
    public FundTransferListRSP list(FundTransferListREQ req) {
        if (req.getQueryDateStart() == null) {
            LocalDate queryDateEnd = LocalDate.now();
            LocalDate queryDateStart = queryDateEnd.minusDays(5);
            req.setQueryDateStart(queryDateStart);
            req.setQueryDateEnd(queryDateEnd);
        }
        FundTransferListRSP res = new FundTransferListRSP();
        List<AccountBalanceBaseInfo> accountBalanceBaseInfoList = accountBalanceBaseInfoMapper.selectList(Wrappers.<AccountBalanceBaseInfo>lambdaQuery()
                .like(Objects.nonNull(req.getAccountBank()) ,AccountBalanceBaseInfo::getAccountBank, req.getAccountBank())
                .eq(AccountBalanceBaseInfo::getAccountType, LiquidityBankAccountType.SUPERVISION.name())
                .ge(Objects.nonNull(req.getQueryDateStart()) ,AccountBalanceBaseInfo::getDate, req.getQueryDateStart())
                .le(Objects.nonNull(req.getQueryDateEnd()) ,AccountBalanceBaseInfo::getDate, req.getQueryDateEnd()));
        if(CollectionUtil.isNotEmpty(accountBalanceBaseInfoList)){
            List<FundTransferListRSP.AccountBalanceDetail> list = accountBalanceBaseInfoList.stream().map(item -> {
                FundTransferListRSP.AccountBalanceDetail detail = BeanUtil.copyProperties(item, FundTransferListRSP.AccountBalanceDetail.class);
                Long balanceAmount = 0L;
                Long balanceLimitAmount = 0L;
                if (Objects.nonNull(item.getActualBalanceAmount())) {
                    balanceAmount = item.getActualBalanceAmount();
                } else if (Objects.nonNull(item.getEstimateBalanceAmount())) {
                    balanceAmount = item.getEstimateBalanceAmount();
                }
                if (Objects.nonNull(item.getEstimateBalanceLimitEditAmount())) {
                    balanceLimitAmount = item.getEstimateBalanceLimitEditAmount();
                } else if (Objects.nonNull(item.getEstimateBalanceLimitAmount())) {
                    balanceLimitAmount = item.getEstimateBalanceLimitAmount();
                }
                Long pendingBalanceAmount = (balanceAmount - balanceLimitAmount) < 0 ? 0 : (balanceAmount - balanceLimitAmount);
                detail.setPendingBalanceAmount(pendingBalanceAmount);
                return detail;
            }).collect(Collectors.toList());
            list.sort(Comparator.comparing(FundTransferListRSP.AccountBalanceDetail::getDate));
            res.setList(list);
            // 合计
            res.setSum(accountBalanceSumHandle(accountBalanceBaseInfoList));
        }
        return res;
    }

    private List<FundTransferListRSP.AccountBalanceSum> accountBalanceSumHandle(List<AccountBalanceBaseInfo> accountBalanceBaseInfoList) {
        Map<LocalDate, List<AccountBalanceBaseInfo>> baseInfoMap = accountBalanceBaseInfoList.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate));
        List<FundTransferListRSP.AccountBalanceSum> result = baseInfoMap.entrySet().stream().map(entry -> {
            FundTransferListRSP.AccountBalanceDetail all = new FundTransferListRSP.AccountBalanceDetail();
            for (AccountBalanceBaseInfo item : entry.getValue()) {
                Long balanceAmount = 0L;
                Long balanceLimitAmount = 0L;
                Long pendingBalanceAmount = 0L;
                if (Objects.nonNull(item.getActualBalanceAmount())) {
                    balanceAmount = item.getActualBalanceAmount();
                } else if (Objects.nonNull(item.getEstimateBalanceAmount())) {
                    balanceAmount = item.getEstimateBalanceAmount();
                }
                if (Objects.nonNull(item.getEstimateBalanceLimitEditAmount())) {
                    balanceLimitAmount = item.getEstimateBalanceLimitEditAmount();
                } else if (Objects.nonNull(item.getEstimateBalanceLimitAmount())) {
                    balanceLimitAmount = item.getEstimateBalanceLimitAmount();
                }
                pendingBalanceAmount = (balanceAmount - balanceLimitAmount) < 0 ? 0 : (balanceAmount - balanceLimitAmount);
                all.setPendingBalanceAmount(Optional.ofNullable(all.getPendingBalanceAmount()).orElse(0L) + pendingBalanceAmount);
            }
            FundTransferListRSP.AccountBalanceSum sum = new FundTransferListRSP.AccountBalanceSum();
            all.setDate(entry.getKey());
            sum.setDate(entry.getKey());
            sum.setAllSum(all);
            return sum;
        }).collect(Collectors.toList());
        result.sort(Comparator.comparing(FundTransferListRSP.AccountBalanceSum::getDate));
        return result;
    }


    @Override
    public FundTransferGraphRSP graphList(FundTransferGraphREQ req) {
        FundTransferListREQ transferListREQ = BeanUtil.copyProperties(req, FundTransferListREQ.class);
        FundTransferListRSP fundTransferListRSP = list(transferListREQ);
        if (fundTransferListRSP == null || fundTransferListRSP.getSum() == null || fundTransferListRSP.getSum().isEmpty()) {
            return new FundTransferGraphRSP();
        }
        FundTransferGraphRSP res = new FundTransferGraphRSP();
        if (fundTransferListRSP.getSum() != null && !fundTransferListRSP.getSum().isEmpty()) {
            List<FundTransferGraphRSP.AccountBalanceSum> sumList = new ArrayList<>();
            for (FundTransferListRSP.AccountBalanceSum accountBalanceSum : fundTransferListRSP.getSum()) {
                FundTransferGraphRSP.AccountBalanceSum sum = BeanUtil.copyProperties(accountBalanceSum, FundTransferGraphRSP.AccountBalanceSum.class);
                sumList.add(sum);
            }
            res.setSum(sumList);
        }
        return res;
    }


    @Override
    public FundTransferDetailListRSP detailList(FundTransferDetailListREQ req) {
        FundTransferListREQ transferListREQ = BeanUtil.copyProperties(req, FundTransferListREQ.class);
        FundTransferListRSP fundTransferListRSP = list(transferListREQ);
        if (fundTransferListRSP == null || fundTransferListRSP.getList().isEmpty()) {
            return new FundTransferDetailListRSP();
        }
        FundTransferDetailListRSP res = new FundTransferDetailListRSP();
        List<FundTransferDetailListRSP.AccountBalanceDetail> list = new ArrayList<>();
        for (FundTransferListRSP.AccountBalanceDetail balanceDetail : fundTransferListRSP.getList()) {
            if (req.getAccountId().equals(balanceDetail.getAccountId())) {
                FundTransferDetailListRSP.AccountBalanceDetail detail = BeanUtil.copyProperties(balanceDetail, FundTransferDetailListRSP.AccountBalanceDetail.class);
                list.add(detail);
            }
        }
        res.setList(list);
        return res;
    }


    @Override
    public FundTransferListDailyRSP listDaily(FundTransferListDailyREQ req) {
        FundTransferListREQ transferListREQ = BeanUtil.copyProperties(req, FundTransferListREQ.class);
        FundTransferListRSP fundTransferListRSP = list(transferListREQ);
        if (fundTransferListRSP == null || fundTransferListRSP.getList().isEmpty()) {
            return new FundTransferListDailyRSP();
        }
        FundTransferListDailyRSP res = new FundTransferListDailyRSP();
        Long sum = 0L;
        for (FundTransferListRSP.AccountBalanceDetail balanceDetail : fundTransferListRSP.getList()) {
            if (req.getAccountId().equals(balanceDetail.getAccountId()) && req.getCurrentDate().minusDays(1).isEqual(balanceDetail.getDate())) {
                sum = balanceDetail.getPendingBalanceAmount();
                res.setSum(sum);
                break;
            }
        }
        FundTransferListREQ newReq = new FundTransferListREQ();
        LocalDate endDate = req.getCurrentDate().minusDays(1);
        LocalDate startDate = endDate.minusDays(100);
        newReq.setQueryDateStart(startDate);
        newReq.setAccountBank(req.getAccountBank());
        newReq.setQueryDateEnd(endDate);
        FundTransferListRSP newRsp = list(newReq);
        if (newRsp == null || newRsp.getList().isEmpty()) {
            return new FundTransferListDailyRSP();
        }
        List<FundTransferListRSP.AccountBalanceDetail> detailList = new ArrayList<>();
        for (FundTransferListRSP.AccountBalanceDetail balanceDetail : newRsp.getList()) {
            if (req.getAccountId().equals(balanceDetail.getAccountId())) {
                detailList.add(balanceDetail);
            }
        }
        //时间升序排序
        Collections.sort(detailList, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        Map<Long, List<FundTransferListRSP.AccountBalanceDetail>> accountMap = detailList.stream().collect(Collectors.groupingBy(FundTransferListRSP.AccountBalanceDetail::getAccountId));
        List<List<FundTransferObj>> fundTransferObjList = new ArrayList<>();
        for (Map.Entry<Long, List<FundTransferListRSP.AccountBalanceDetail>> entry : accountMap.entrySet()) {
            Long id = entry.getKey();
            List<FundTransferListRSP.AccountBalanceDetail> accountBalanceDetailList = entry.getValue();
            List<FundTransferObj> objList = new ArrayList<>();
            for (FundTransferListRSP.AccountBalanceDetail detail : accountBalanceDetailList) {
                FundTransferObj obj = new FundTransferObj();
                obj.setId(id);
                obj.setDate(detail.getDate());
                obj.setPendingBalanceAmount(detail.getPendingBalanceAmount());
                objList.add(obj);
            }
            //处理最后一天待分配沉淀资金
            if (objList.size() < 100) {
                FundTransferObj obj = new FundTransferObj();
                obj.setId(id);
                obj.setDate(accountBalanceDetailList.get(accountBalanceDetailList.size()-1).getDate().minusDays(1));
                obj.setPendingBalanceAmount(0L);
                objList.add(obj);
            }
            fundTransferObjList.add(objList);
        }

        List<DepositedAmountDetail> depositedAmountDetailList = getDepositedAmountList(fundTransferObjList, endDate, sum);
        res.setList(depositedAmountDetailList);
        return res;
    }


    @Override
    public FundTransferGraphDailyRSP graphDaily(FundTransferGraphDailyREQ req) {
        FundTransferListREQ transferListREQ = BeanUtil.copyProperties(req, FundTransferListREQ.class);
        FundTransferListRSP fundTransferListRSP = list(transferListREQ);
        if (fundTransferListRSP == null || fundTransferListRSP.getSum().isEmpty()) {
            return new FundTransferGraphDailyRSP();
        }
        FundTransferGraphDailyRSP res = new FundTransferGraphDailyRSP();
        Long allSum = 0L;
        List<FundTransferListRSP.AccountBalanceSum> sumList = fundTransferListRSP.getSum();
        for (FundTransferListRSP.AccountBalanceSum balanceSum : sumList) {
            if (req.getCurrentDate().minusDays(1).isEqual(balanceSum.getDate())) {
                allSum = balanceSum.getAllSum().getPendingBalanceAmount();
                res.setSum(allSum);
                break;
            }
        }
        FundTransferListREQ newReq = new FundTransferListREQ();
        LocalDate endDate = req.getCurrentDate().minusDays(1);
        LocalDate startDate = endDate.minusDays(100);
        newReq.setQueryDateStart(startDate);
        newReq.setAccountBank(req.getAccountBank());
        newReq.setQueryDateEnd(endDate);
        FundTransferListRSP newRsp = list(newReq);
        if (newRsp == null || newRsp.getList().isEmpty()) {
            return new FundTransferGraphDailyRSP();
        }
        List<FundTransferListRSP.AccountBalanceDetail> detailList = newRsp.getList();

        //时间升序排序
        detailList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        Map<Long, List<FundTransferListRSP.AccountBalanceDetail>> accountMap = detailList.stream().collect(Collectors.groupingBy(FundTransferListRSP.AccountBalanceDetail::getAccountId));
        List<List<FundTransferObj>> fundTransferObjList = new ArrayList<>();
        for (Map.Entry<Long, List<FundTransferListRSP.AccountBalanceDetail>> entry : accountMap.entrySet()) {
            Long id = entry.getKey();
            List<FundTransferListRSP.AccountBalanceDetail> accountBalanceDetailList = entry.getValue();
            List<FundTransferObj> objList = new ArrayList<>();
            for (FundTransferListRSP.AccountBalanceDetail detail : accountBalanceDetailList) {
                FundTransferObj obj = new FundTransferObj();
                obj.setId(id);
                obj.setDate(detail.getDate());
                obj.setPendingBalanceAmount(detail.getPendingBalanceAmount());
                objList.add(obj);
            }
            //处理最后一天待分配沉淀资金
            if (objList.size() < 100) {
                FundTransferObj obj = new FundTransferObj();
                obj.setId(id);
                obj.setDate(accountBalanceDetailList.get(accountBalanceDetailList.size()-1).getDate().minusDays(1));
                obj.setPendingBalanceAmount(0L);
                objList.add(obj);
            }
            fundTransferObjList.add(objList);
        }
        List<DepositedAmountDetail> depositedAmountDetailList = getDepositedAmountList(fundTransferObjList, endDate, allSum);
        res.setList(depositedAmountDetailList);
        return res;
    }


    @Override
    public FundTransferCurrentDailyRSP currentDaily(FundTransferCurrentDailyREQ req) {
        FundTransferListREQ transferListREQ = BeanUtil.copyProperties(req, FundTransferListREQ.class);
        FundTransferListRSP fundTransferListRSP = list(transferListREQ);
        if (fundTransferListRSP == null || fundTransferListRSP.getSum().isEmpty()) {
            return new FundTransferCurrentDailyRSP();
        }
        Long allSum = 0L;
        FundTransferCurrentDailyRSP res = new FundTransferCurrentDailyRSP();
        List<FundTransferListRSP.AccountBalanceSum> sumList = fundTransferListRSP.getSum();
        for (FundTransferListRSP.AccountBalanceSum balanceSum : sumList) {
            if (req.getCurrentDate().minusDays(1).isEqual(balanceSum.getDate())) {
                allSum = balanceSum.getAllSum().getPendingBalanceAmount();
                res.setSum(allSum);
            }
        }
        FundTransferListREQ newReq = new FundTransferListREQ();
        LocalDate endDate = req.getCurrentDate().minusDays(1);
        LocalDate startDate = endDate.minusDays(100);
        newReq.setQueryDateStart(startDate);
        newReq.setQueryDateEnd(endDate);
        FundTransferListRSP newRsp = list(newReq);
        if (newRsp == null || newRsp.getList().isEmpty()) {
            return new FundTransferCurrentDailyRSP();
        }
        List<FundTransferListRSP.AccountBalanceDetail> detailList = newRsp.getList();

        //时间升序排序
        detailList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        Map<Long, List<FundTransferListRSP.AccountBalanceDetail>> accountMap = detailList.stream().collect(Collectors.groupingBy(FundTransferListRSP.AccountBalanceDetail::getAccountId));
        List<List<FundTransferObj>> fundTransferObjList = new ArrayList<>();
        for (Map.Entry<Long, List<FundTransferListRSP.AccountBalanceDetail>> entry : accountMap.entrySet()) {
            Long id = entry.getKey();
            List<FundTransferListRSP.AccountBalanceDetail> accountBalanceDetailList = entry.getValue();
            List<FundTransferObj> objList = new ArrayList<>();
            for (FundTransferListRSP.AccountBalanceDetail detail : accountBalanceDetailList) {
                FundTransferObj obj = new FundTransferObj();
                obj.setId(id);
                obj.setDate(detail.getDate());
                obj.setPendingBalanceAmount(detail.getPendingBalanceAmount());
                objList.add(obj);
            }
            //处理最后一天待分配沉淀资金
            if (objList.size() < 100) {
                FundTransferObj obj = new FundTransferObj();
                obj.setId(id);
                obj.setDate(accountBalanceDetailList.get(accountBalanceDetailList.size()-1).getDate().minusDays(1));
                obj.setPendingBalanceAmount(0L);
                objList.add(obj);
            }
            fundTransferObjList.add(objList);
        }
        List<DepositedAmountDetail> depositedAmountDetailList = getDepositedAmountList(fundTransferObjList, endDate, allSum);
        res.setList(depositedAmountDetailList);
        return res;
    }


    private List<DepositedAmountDetail> getDepositedAmountList(List<List<FundTransferObj>> objList, LocalDate firstDate, Long sum) {
        if (objList.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<FundTransferObj>> rsp = new ArrayList<>();
        for (List<FundTransferObj> fundTransferObjList : objList) {
            List<FundTransferObj> finalList = new ArrayList<>();
            rsp.add(finalList);
            for (int i = 0; i < fundTransferObjList.size()-1; i++) {
                FundTransferObj first = fundTransferObjList.get(i);
                FundTransferObj second = fundTransferObjList.get(i+1);
                finalList.add(first);
                //沉淀资金
                Long firstDepositedAmount = Optional.ofNullable(first.getPendingBalanceAmount()).orElse(0L) - Optional.ofNullable(second.getPendingBalanceAmount()).orElse(0L);
                if (firstDepositedAmount <= 0L) {
                    first.setDepositedAmount(0L);
                } else {
                    if (i > 0) {
                        firstDepositedAmount = Math.min(firstDepositedAmount, fundTransferObjList.get(i-1).getPendingDepositedAmount());
                    }
                    first.setDepositedAmount(firstDepositedAmount);
                }
                //沉淀时间
                if (firstDepositedAmount > 0L) {
                    LocalDate currentDate = first.getDate();
                    long days = fundTransferBaseDataPort.calculateWorkDays(currentDate, firstDate);
                    first.setSettingTime(Long.valueOf(days).intValue());
                }
                //待分配沉淀资金
                if (i == 0) {
                    Long pendingDepositedAmount = Optional.ofNullable(first.getPendingBalanceAmount()).orElse(0L) - Optional.ofNullable(first.getDepositedAmount()).orElse(0L);
                    first.setPendingDepositedAmount(pendingDepositedAmount);
                    if (pendingDepositedAmount.equals(0L)) {
                        break;
                    }
                } else {
                    Long prevPendingDepositedAmount = Optional.ofNullable(fundTransferObjList.get(i-1).getPendingDepositedAmount()).orElse(0L);
                    Long pendingDepositedAmount = prevPendingDepositedAmount - Optional.ofNullable(first.getDepositedAmount()).orElse(0L);
                    first.setPendingDepositedAmount(pendingDepositedAmount);
                    if (pendingDepositedAmount.equals(0L)) {
                        break;
                    }
                }
            }
        }

        List<DepositedAmountDetail> res = getDepositedAmountPercentList(rsp, sum);
        return res;
    }


    private List<DepositedAmountDetail> getDepositedAmountPercentList(List<List<FundTransferObj>> rsp, Long sum) {
        if (rsp.isEmpty() || sum == 0) {
            List<DepositedAmountDetail> res = new ArrayList<>();
            for (SettingTimeEnum settingTimeEnum : SettingTimeEnum.values()) {
                DepositedAmountDetail detail = new DepositedAmountDetail();
                detail.setSettingTime(settingTimeEnum.name());
                detail.setDepositedAmount(0L);
                detail.setPayAmount(new ValueUnitDTO(String.valueOf(0),"%"));
                res.add(detail);
            }
            return res;
        }
        Map<Integer, Long> fundsMap = new HashMap<>();
        for (List<FundTransferObj> finalList : rsp) {
            for (FundTransferObj obj : finalList) {
                if (obj.getDepositedAmount() > 0 && obj.getSettingTime() >= 0) {
                    if (fundsMap.containsKey(obj.getSettingTime())) {
                        fundsMap.put(obj.getSettingTime(), fundsMap.get(obj.getSettingTime()) + obj.getDepositedAmount());
                    } else {
                        fundsMap.put(obj.getSettingTime(), obj.getDepositedAmount());
                    }
                }
            }
        }
        LinkedList<DepositedAmountDetail> res = new LinkedList<>();
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        map.putIfAbsent(SettingTimeEnum.WITHIN_THREE_DAYS.name(), 0L);
        map.putIfAbsent(SettingTimeEnum.THREE_TO_TEN_DAYS.name(), 0L);
        map.putIfAbsent(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name(), 0L);
        map.putIfAbsent(SettingTimeEnum.ONE_TO_THREE_MONTHS.name(), 0L);
        map.putIfAbsent(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name(), 0L);
        for (Map.Entry<Integer, Long> entry : fundsMap.entrySet()) {
            Integer settingTime = entry.getKey();
            Long depositedAmount = entry.getValue();
            if (depositedAmount > 0) {
                if (settingTime >= 0 && settingTime <= 3) {
                    map.put(SettingTimeEnum.WITHIN_THREE_DAYS.name(), map.get(SettingTimeEnum.WITHIN_THREE_DAYS.name()) + depositedAmount);
                } else if (settingTime > 3 && settingTime <= 10) {
                    map.put(SettingTimeEnum.THREE_TO_TEN_DAYS.name(), map.get(SettingTimeEnum.THREE_TO_TEN_DAYS.name()) + depositedAmount);
                } else if (settingTime > 10 && settingTime <= 30) {
                    map.put(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name(), map.get(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name()) + depositedAmount);
                } else if (settingTime > 30 && settingTime <= 90) {
                    map.put(SettingTimeEnum.ONE_TO_THREE_MONTHS.name(), map.get(SettingTimeEnum.ONE_TO_THREE_MONTHS.name()) + depositedAmount);
                } else if (settingTime > 90) {
                    map.put(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name(), map.get(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name()) + depositedAmount);
                }
            }
        }
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            DepositedAmountDetail detail = new DepositedAmountDetail();
            BigDecimal minValue = new BigDecimal("0.01");
            if (SettingTimeEnum.WITHIN_THREE_DAYS.name().equalsIgnoreCase(key)) {
                detail.setSettingTime(SettingTimeEnum.WITHIN_THREE_DAYS.name());
                detail.setDepositedAmount(value);
                BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                    detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                } else {
                    detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                }
            } else if (SettingTimeEnum.THREE_TO_TEN_DAYS.name().equalsIgnoreCase(key)) {
                detail.setSettingTime(SettingTimeEnum.THREE_TO_TEN_DAYS.name());
                detail.setDepositedAmount(value);
                BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                    detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                } else {
                    detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                }
            } else if (SettingTimeEnum.TEN_TO_THIRTY_DAYS.name().equalsIgnoreCase(key)) {
                detail.setSettingTime(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name());
                detail.setDepositedAmount(value);
                BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                    detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                } else {
                    detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                }
            } else if (SettingTimeEnum.ONE_TO_THREE_MONTHS.name().equalsIgnoreCase(key)) {
                detail.setSettingTime(SettingTimeEnum.ONE_TO_THREE_MONTHS.name());
                detail.setDepositedAmount(value);
                BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                    detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                } else {
                    detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                }
            } else if (SettingTimeEnum.MORE_THAN_THREE_MONTHS.name().equalsIgnoreCase(key)) {
                detail.setSettingTime(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name());
                detail.setDepositedAmount(value);
                BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                    detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                } else {
                    detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                }
            }
            res.add(detail);
        }
        return res;
    }


    @Override
    public List<FundTransferBankAccountListRSP> list(FundTransferBankAccountListREQ req) {
        return fundTransferBaseDataPort.listSupervisionAccounts(req);
    }

    @Override
    public void financingRepayInfoInAdvance() {
        financingRepayInfoPort.financingRepayInfoInAdvance();
    }

    @Override
    public PageR<AccountDepositedAmountDetail> accountCurrentDaily(FundTransferAccountCurrentDailyREQ req) {
        FundTransferListREQ transferListREQ = BeanUtil.copyProperties(req, FundTransferListREQ.class);
        FundTransferListRSP fundTransferListRSP = list(transferListREQ);
        if (fundTransferListRSP == null || fundTransferListRSP.getSum().isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Long allSum = 0L;
        List<FundTransferListRSP.AccountBalanceSum> sumList = fundTransferListRSP.getSum();
        for (FundTransferListRSP.AccountBalanceSum balanceSum : sumList) {
            if (req.getCurrentDate().minusDays(1).isEqual(balanceSum.getDate())) {
                allSum = balanceSum.getAllSum().getPendingBalanceAmount();
            }
        }
        FundTransferListREQ newReq = new FundTransferListREQ();
        LocalDate endDate = req.getCurrentDate().minusDays(1);
        LocalDate startDate = endDate.minusDays(100);
        newReq.setQueryDateStart(startDate);
        newReq.setQueryDateEnd(endDate);
        newReq.setAccountBank(req.getAccountBank());
        FundTransferListRSP newRsp = list(newReq);
        if (newRsp == null || newRsp.getList().isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<FundTransferListRSP.AccountBalanceDetail> detailList = newRsp.getList();

        //时间升序排序
        detailList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        Map<Long, List<FundTransferListRSP.AccountBalanceDetail>> accountMap = detailList.stream().collect(Collectors.groupingBy(FundTransferListRSP.AccountBalanceDetail::getAccountId));
        List<List<FundTransferAccountObj>> fundTransferObjList = new ArrayList<>();
        for (Map.Entry<Long, List<FundTransferListRSP.AccountBalanceDetail>> entry : accountMap.entrySet()) {
            Long id = entry.getKey();
            List<FundTransferListRSP.AccountBalanceDetail> accountBalanceDetailList = entry.getValue();
            List<FundTransferAccountObj> objList = new ArrayList<>();
            for (FundTransferListRSP.AccountBalanceDetail detail : accountBalanceDetailList) {
                FundTransferAccountObj obj = new FundTransferAccountObj();
                obj.setId(id);
                obj.setAccountBank(detail.getAccountBank());
                obj.setAccountNumber(detail.getAccountNumber());
                obj.setDate(detail.getDate());
                obj.setPendingBalanceAmount(detail.getPendingBalanceAmount());
                objList.add(obj);
            }
            //处理最后一天待分配沉淀资金
            if (objList.size() < 100) {
                FundTransferAccountObj obj = new FundTransferAccountObj();
                obj.setId(id);
                obj.setDate(accountBalanceDetailList.get(accountBalanceDetailList.size()-1).getDate().minusDays(1));
                obj.setPendingBalanceAmount(0L);
                obj.setAccountBank(accountBalanceDetailList.get(accountBalanceDetailList.size()-1).getAccountBank());
                obj.setAccountNumber(accountBalanceDetailList.get(accountBalanceDetailList.size()-1).getAccountNumber());
                objList.add(obj);
            }
            fundTransferObjList.add(objList);
        }
        List<AccountDepositedAmountDetail> depositedAmountDetailList = getAccountDepositedAmountList(fundTransferObjList, endDate, allSum);
        if(CharSequenceUtil.isNotEmpty(req.getSettingTime())){
            depositedAmountDetailList = depositedAmountDetailList.stream().filter(e -> Objects.equals(e.getSettingTime(), req.getSettingTime())).collect(Collectors.toList());
        }
        if(CollUtil.isEmpty(depositedAmountDetailList)){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<AccountDepositedAmountDetail> result = depositedAmountDetailList.stream().skip((long) (req.getPage() - 1) * req.getPageSize()).limit(req.getPageSize()).collect(Collectors.toList());
        //总页数
        int pages = this.ceil(this.divide(depositedAmountDetailList.size(), req.getPageSize()), 0).intValue();
        return PageR.of(result, depositedAmountDetailList.size(), pages,req.getPage(), req.getPageSize());
    }


    private List<AccountDepositedAmountDetail> getAccountDepositedAmountList(List<List<FundTransferAccountObj>> objList, LocalDate firstDate, Long sum) {
        if (objList.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<FundTransferAccountObj>> rsp = new ArrayList<>();
        for (List<FundTransferAccountObj> fundTransferObjList : objList) {
            List<FundTransferAccountObj> finalList = new ArrayList<>();
            rsp.add(finalList);
            for (int i = 0; i < fundTransferObjList.size()-1; i++) {
                FundTransferAccountObj first = fundTransferObjList.get(i);
                FundTransferAccountObj second = fundTransferObjList.get(i+1);
                finalList.add(first);
                //沉淀资金
                Long firstDepositedAmount = Optional.ofNullable(first.getPendingBalanceAmount()).orElse(0L) - Optional.ofNullable(second.getPendingBalanceAmount()).orElse(0L);
                if (firstDepositedAmount <= 0L) {
                    first.setDepositedAmount(0L);
                } else {
                    if (i > 0) {
                        firstDepositedAmount = Math.min(firstDepositedAmount, fundTransferObjList.get(i-1).getPendingDepositedAmount());
                    }
                    first.setDepositedAmount(firstDepositedAmount);
                }
                //沉淀时间
                if (firstDepositedAmount > 0L) {
                    LocalDate currentDate = first.getDate();
                    long days = fundTransferBaseDataPort.calculateWorkDays(currentDate, firstDate);
                    first.setSettingTime(Long.valueOf(days).intValue());
                }
                //待分配沉淀资金
                if (i == 0) {
                    Long pendingDepositedAmount = Optional.ofNullable(first.getPendingBalanceAmount()).orElse(0L) - Optional.ofNullable(first.getDepositedAmount()).orElse(0L);
                    first.setPendingDepositedAmount(pendingDepositedAmount);
                    if (pendingDepositedAmount.equals(0L)) {
                        break;
                    }
                } else {
                    Long prevPendingDepositedAmount = Optional.ofNullable(fundTransferObjList.get(i-1).getPendingDepositedAmount()).orElse(0L);
                    Long pendingDepositedAmount = prevPendingDepositedAmount - Optional.ofNullable(first.getDepositedAmount()).orElse(0L);
                    first.setPendingDepositedAmount(pendingDepositedAmount);
                    if (pendingDepositedAmount.equals(0L)) {
                        break;
                    }
                }
            }
        }

        return getAccountDepositedAmountPercentList(rsp, sum);
    }

    private List<AccountDepositedAmountDetail> getAccountDepositedAmountPercentList(List<List<FundTransferAccountObj>> rsp, Long sum) {
        if (rsp.isEmpty() || sum == 0) {
            return new ArrayList<>();
        }
        Map<Integer, Long> fundsMap = new HashMap<>();
        LinkedList<AccountDepositedAmountDetail> res = new LinkedList<>();
        for (List<FundTransferAccountObj> finalList : rsp) {
            fundsMap.clear();
            /*以账户维度生成资金明细*/
            for (FundTransferAccountObj obj : finalList) {
                if (obj.getDepositedAmount() > 0 && obj.getSettingTime() >= 0) {
                    if (fundsMap.containsKey(obj.getSettingTime())) {
                        fundsMap.put(obj.getSettingTime(), fundsMap.get(obj.getSettingTime()) + obj.getDepositedAmount());
                    } else {
                        fundsMap.put(obj.getSettingTime(), obj.getDepositedAmount());
                    }
                }
            }
            if (fundsMap.isEmpty()) {
                continue;
            }
            LinkedHashMap<String, Long> map = new LinkedHashMap<>();
            map.putIfAbsent(SettingTimeEnum.WITHIN_THREE_DAYS.name(), 0L);
            map.putIfAbsent(SettingTimeEnum.THREE_TO_TEN_DAYS.name(), 0L);
            map.putIfAbsent(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name(), 0L);
            map.putIfAbsent(SettingTimeEnum.ONE_TO_THREE_MONTHS.name(), 0L);
            map.putIfAbsent(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name(), 0L);
            for (Map.Entry<Integer, Long> entry : fundsMap.entrySet()) {
                Integer settingTime = entry.getKey();
                Long depositedAmount = entry.getValue();
                if (depositedAmount > 0) {
                    if (settingTime >= 0 && settingTime <= 3) {
                        map.put(SettingTimeEnum.WITHIN_THREE_DAYS.name(), map.get(SettingTimeEnum.WITHIN_THREE_DAYS.name()) + depositedAmount);
                    } else if (settingTime > 3 && settingTime <= 10) {
                        map.put(SettingTimeEnum.THREE_TO_TEN_DAYS.name(), map.get(SettingTimeEnum.THREE_TO_TEN_DAYS.name()) + depositedAmount);
                    } else if (settingTime > 10 && settingTime <= 30) {
                        map.put(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name(), map.get(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name()) + depositedAmount);
                    } else if (settingTime > 30 && settingTime <= 90) {
                        map.put(SettingTimeEnum.ONE_TO_THREE_MONTHS.name(), map.get(SettingTimeEnum.ONE_TO_THREE_MONTHS.name()) + depositedAmount);
                    } else if (settingTime > 90) {
                        map.put(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name(), map.get(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name()) + depositedAmount);
                    }
                }
            }
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                String key = entry.getKey();
                Long value = entry.getValue();
                if(value == 0){
                    continue;
                }
                AccountDepositedAmountDetail detail = new AccountDepositedAmountDetail();
                detail.setAccountBank(finalList.get(0).getAccountBank());
                detail.setAccountNumber(finalList.get(0).getAccountNumber());
                BigDecimal minValue = new BigDecimal("0.01");
                if (SettingTimeEnum.WITHIN_THREE_DAYS.name().equalsIgnoreCase(key)) {
                    detail.setSettingTime(SettingTimeEnum.WITHIN_THREE_DAYS.name());
                    detail.setDepositedAmount(value);
                    BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                    if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                        detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                    } else {
                        detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                    }
                } else if (SettingTimeEnum.THREE_TO_TEN_DAYS.name().equalsIgnoreCase(key)) {
                    detail.setSettingTime(SettingTimeEnum.THREE_TO_TEN_DAYS.name());
                    detail.setDepositedAmount(value);
                    BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                    if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                        detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                    } else {
                        detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                    }
                } else if (SettingTimeEnum.TEN_TO_THIRTY_DAYS.name().equalsIgnoreCase(key)) {
                    detail.setSettingTime(SettingTimeEnum.TEN_TO_THIRTY_DAYS.name());
                    detail.setDepositedAmount(value);
                    BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                    if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                        detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                    } else {
                        detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                    }
                } else if (SettingTimeEnum.ONE_TO_THREE_MONTHS.name().equalsIgnoreCase(key)) {
                    detail.setSettingTime(SettingTimeEnum.ONE_TO_THREE_MONTHS.name());
                    detail.setDepositedAmount(value);
                    BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                    if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                        detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                    } else {
                        detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                    }
                } else if (SettingTimeEnum.MORE_THAN_THREE_MONTHS.name().equalsIgnoreCase(key)) {
                    detail.setSettingTime(SettingTimeEnum.MORE_THAN_THREE_MONTHS.name());
                    detail.setDepositedAmount(value);
                    BigDecimal valueDecimal = new BigDecimal(value).multiply(BigDecimal.valueOf(100)).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                    if (valueDecimal.compareTo(minValue) < 0 && value > 0) {
                        detail.setPayAmount(new ValueUnitDTO("<0.01","%"));
                    } else {
                        detail.setPayAmount(new ValueUnitDTO(String.valueOf(valueDecimal),"%"));
                    }
                }
                res.add(detail);
            }
        }

        return res.stream().sorted(Comparator.comparing(AccountDepositedAmountDetail::getDepositedAmount, Comparator.reverseOrder())
                .thenComparing(this::getEnumSort, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    private Integer getEnumSort(AccountDepositedAmountDetail detail) {
        return Optional.ofNullable(SettingTimeEnum.findByName(detail.getSettingTime())).map(SettingTimeEnum::getSort).orElse(Integer.MAX_VALUE);
    }

    public Number divide(Number v1, Number v2) {
        return BigDecimal.valueOf(v1.doubleValue()).divide(BigDecimal.valueOf(v2.doubleValue()), 128, BigDecimal.ROUND_HALF_UP);
    }

    public Number ceil(Number value, int scale) {
        return BigDecimal.valueOf(value.doubleValue()).setScale(scale, RoundingMode.UP);
    }



}
