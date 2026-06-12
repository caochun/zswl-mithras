package cn.zswltech.mithras.application.orchestration.liquiditymanage;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailModifyRSP;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.excel.importer.AccountBalanceBaseInfoImporter;
import cn.zswltech.mithras.liquidity.excel.model.AccountBalanceBaseInfoExcelModel;
import cn.zswltech.mithras.liquidity.mapper.AccountBalanceBaseInfoMapper;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorBoardHolder;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorHolder;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorMismatchHolder;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountCalculatorBo;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountManualCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.Collator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.constant.ResultMsg.LIQUIDITY_MANAGE_LOCK;

/**
 * <p>
 * 账户余额表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-12-12
 */
@Service
@Slf4j
public class AccountBalanceBaseInfoService extends ServiceImpl<AccountBalanceBaseInfoMapper, AccountBalanceBaseInfo> {

    @Autowired
    private List<AbstractLiquidityCalculator<LiquidityAccountCalculatorBo>> calculatorAccountList;
    @Autowired
    private List<AbstractLiquidityCalculator<LiquidityAccountManualCalculatorBo>> calculatorAccountManualList;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private LiquidityDataService liquidityDataService;
    @Resource
    private AccountBalanceBaseInfoImporter accountBalanceBaseInfoImporter;


    @XxlJob("accountBalanceCalculate")
    @Transactional(rollbackFor = Throwable.class)
    public void accountBalanceCalculate(){
        init(false, null);
    }

    /**
     * 账户余额表重算
     * @param isTry 是否仅尝试拿锁
     * @param startDate 计算开始日
     */
    public void init(boolean isTry, LocalDate startDate){
        // 计算时不允许变更LiquidityIndicatorHolder中的值，此处需要上锁
        String lockKey = CacheEnum.LIQUIDITY_MANAGE_INDICATOR_CALCULATE_LOCK.buildKey("ALL");
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(LIQUIDITY_MANAGE_LOCK);
        }
        try {
            if(!isTry) {
                liquidityDataService.dataQueryAccount();
                accountBalanceBaseInfoService.cal(startDate);
            }
        } finally {
            redisDistLock.unlock(lockKey);
        }
    }


    @Transactional(rollbackFor = Throwable.class)
    public void cal(LocalDate startDate){
        LocalDate localDate = LocalDate.now();
        // 优先取入参数，无入参取配置中的，无配置取当前时间
        if(startDate == null){
            if(LiquidityIndicatorHolder.ACCOUNT_PARAMETER_CONFIG_START_TIME.getConfigValue() != null) {
                try {
                    localDate = LocalDate.parse(LiquidityIndicatorHolder.ACCOUNT_PARAMETER_CONFIG_START_TIME.getConfigValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }catch (Exception e){
                    log.warn("账户余额表计算开始日参数配置异常, 默认取当前时间");
                }
            }
        }else {
            localDate = startDate;
        }

        List<AccountBalanceBaseInfo> accountBalanceBaseInfoList = new ArrayList<>();
        LocalDate endDate = null;
        try {
            String configValue = LiquidityIndicatorHolder.ACCOUNT_PARAMETER_CONFIG_CALCULATE_MONTH.getConfigValue();
            endDate = LocalDate.now().plusMonths(Long.parseLong(configValue));
        }catch (Exception e){
            log.warn("账户余额表计算终止日参数配置异常，默认计算近两年数据");
            endDate = LocalDate.now().plusYears(2);
        }
        // 先根据时间维度循环
        while (localDate.isBefore(endDate)) {
            Map<Long, AccountBalanceBaseInfo> accountOriginMap = LiquidityIndicatorHolder.ACCOUNT_BALANCE_BASE_INFO.get(localDate);
            // 根据当前时间遍历各个账户
            for (BaseDataBankAccount bankAccount : LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT.values()) {
                // 先取数据库中存在的数据，不存在则新建
                AccountBalanceBaseInfo accountBalanceBaseInfo = CollectionUtil.isNotEmpty(accountOriginMap) ?
                        Optional.ofNullable(accountOriginMap.get(bankAccount.getId())).orElse(new AccountBalanceBaseInfo()) : new AccountBalanceBaseInfo();
                for (AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> cal : calculatorAccountList.stream().sorted(Comparator.comparing(AbstractLiquidityCalculator::sort)).collect(Collectors.toList())) {
                    cal.calculate(accountBalanceBaseInfo, new LiquidityAccountCalculatorBo(localDate, bankAccount.getId()));
                }
                accountBalanceBaseInfoList.add(accountBalanceBaseInfo);
            }
            localDate = localDate.plusDays(1);
        }
        // 结余和结余受限依赖于各指标的计算结果，需要最后重新处理一次
        calculateBalanceAmount(accountBalanceBaseInfoList);

        List<AccountBalanceBaseInfo> addList = accountBalanceBaseInfoList.stream().filter(f -> Objects.isNull(f.getId())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(addList)) {
            this.saveBatch(addList);
        }
        List<AccountBalanceBaseInfo> updateList = accountBalanceBaseInfoList.stream().filter(f -> Objects.nonNull(f.getId())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
        }
        // 账户余额表数据更新
        Map<LocalDate, Map<Long, AccountBalanceBaseInfo>> accountBalanceMap = Optional.ofNullable(accountBalanceBaseInfoService.list()).map(item -> item.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate,
                Collectors.toMap(AccountBalanceBaseInfo::getAccountId, Function.identity(), (m1, m2) -> m2)))).orElse(new HashMap<>());
        LiquidityIndicatorHolder.ACCOUNT_BALANCE_BASE_INFO = accountBalanceMap;
        LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO = accountBalanceMap;
        LiquidityIndicatorBoardHolder.ACCOUNT_BALANCE_BASE_INFO = accountBalanceMap;
        LiquidityIndicatorMismatchHolder.ACCOUNT_BALANCE_BASE_INFO = accountBalanceMap;


    }

    /**
     * 处理手动触发的计算逻辑
     * @param accountBalanceBaseInfoList
     */
    private void calculateBalanceAmount(List<AccountBalanceBaseInfo> accountBalanceBaseInfoList) {
        if(CollectionUtil.isEmpty(accountBalanceBaseInfoList)){
            return;
        }
        Map<LocalDate, Map<Long, AccountBalanceBaseInfo>> accountBalanceMap = accountBalanceBaseInfoList.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate,
                Collectors.toMap(AccountBalanceBaseInfo::getAccountId, Function.identity())));
        LocalDate calFirstDay = accountBalanceMap.keySet().stream().min(Comparator.comparing(Function.identity())).get();
        // 补充前一期
        List<AccountBalanceBaseInfo> accountBalanceListBeforeDay = this.query(null, calFirstDay.minusDays(1));
        if(CollectionUtil.isNotEmpty(accountBalanceListBeforeDay)){
            Map<Long, AccountBalanceBaseInfo> accountBalanceNewMap = new HashMap<>();
            for (AccountBalanceBaseInfo accountBalanceBaseInfo : accountBalanceListBeforeDay) {
                accountBalanceNewMap.put(accountBalanceBaseInfo.getAccountId(), accountBalanceBaseInfo);
            }
            accountBalanceMap.put(calFirstDay.minusDays(1) ,accountBalanceNewMap);
        }
        for (AccountBalanceBaseInfo accountBalanceBaseInfo : accountBalanceBaseInfoList) {
            AccountBalanceBaseInfo accountBalanceBaseInfoBefore = accountBalanceMap.getOrDefault(accountBalanceBaseInfo.getDate().minusDays(1), new HashMap<>()).get(accountBalanceBaseInfo.getAccountId());
            for (AbstractLiquidityCalculator<LiquidityAccountManualCalculatorBo> cal : calculatorAccountManualList) {
                cal.calculate(accountBalanceBaseInfo, new LiquidityAccountManualCalculatorBo(accountBalanceBaseInfo.getDate(), accountBalanceBaseInfo.getAccountId(), accountBalanceBaseInfoBefore));
            }
        }

    }





    public List<AccountBalanceBaseInfo> query(Long accountId, LocalDate localDate){
        LambdaQueryWrapper<AccountBalanceBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Objects.nonNull(accountId) ,AccountBalanceBaseInfo::getAccountId, accountId);
        wrapper.eq(Objects.nonNull(localDate) ,AccountBalanceBaseInfo::getDate, localDate);
        return this.list(wrapper);
    }


    public AccountBalanceDetailListRSP accountBalanceList(AccountBalanceDetailListREQ req) {
        AccountBalanceDetailListRSP rsp = new AccountBalanceDetailListRSP();
        LambdaQueryWrapper<AccountBalanceBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.like(Objects.nonNull(req.getAccountBank()) ,AccountBalanceBaseInfo::getAccountBank, req.getAccountBank());
        wrapper.like(Objects.nonNull(req.getAccountNumber()) ,AccountBalanceBaseInfo::getAccountNumber, req.getAccountNumber());
        wrapper.eq(Objects.nonNull(req.getAccountType()) ,AccountBalanceBaseInfo::getAccountType, req.getAccountType());
        wrapper.ge(Objects.nonNull(req.getQueryDateStart()) ,AccountBalanceBaseInfo::getDate, req.getQueryDateStart());
        wrapper.le(Objects.nonNull(req.getQueryDateEnd()) ,AccountBalanceBaseInfo::getDate, req.getQueryDateEnd().plusDays(9));
        List<AccountBalanceBaseInfo> listContainsAfterTenDay = this.list(wrapper);
        if(CollectionUtil.isNotEmpty(listContainsAfterTenDay)){
            // 先根据时间排序，再根据银行名称排序，再根据账户类型排序
            List<AccountBalanceBaseInfo> accountBalanceBaseInfoList = listContainsAfterTenDay.stream().filter(f -> !f.getDate().isAfter(req.getQueryDateEnd()) && !f.getDate().isBefore(req.getQueryDateStart()))
                    .sorted(Comparator.comparing(AccountBalanceBaseInfo::getDate)
                            .thenComparing(AccountBalanceBaseInfo::getAccountBank, Comparator.comparing(String::toString, Collator.getInstance(Locale.CHINA)))
                            .thenComparing(item -> Optional.ofNullable(BaseDataBankAccountTypeEnum.find(item.getAccountType())).map(BaseDataBankAccountTypeEnum::getSort).orElse(100)))
                    .collect(Collectors.toList());
            if(CollectionUtil.isEmpty(accountBalanceBaseInfoList)){
                return rsp;
            }
            // 列表
            Map<Long, Map<LocalDate, Long>> estimateBalanceAmountMap = listContainsAfterTenDay.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getAccountId,
                    Collectors.toMap(AccountBalanceBaseInfo::getDate, AccountBalanceBaseInfo::getEstimateBalanceAmount)));
            List<AccountBalanceDetailListRSP.AccountBalanceDetail> list = accountBalanceBaseInfoList.stream().map(item -> {
                AccountBalanceDetailListRSP.AccountBalanceDetail detail = BeanUtil.copyProperties(item, AccountBalanceDetailListRSP.AccountBalanceDetail.class);
                if(item.getEstimateBalanceLimitEditAmount() != null) {
                    detail.setEstimateBalanceLimitAmount(item.getEstimateBalanceLimitEditAmount());
                }
                String color = LiquidityColorEnum.BLACK.name();
                // 系统包含当天10天内“结余（预估）”为负值时，“开户银行”、“银行账号”字段标红
                for (int i = 0; i < 10; i++) {
                    Long estimateBalanceAmount = estimateBalanceAmountMap.get(item.getAccountId()).get(item.getDate().plusDays(i));
                    if(LongUtil.null2zero(estimateBalanceAmount) < 0){
                        color = LiquidityColorEnum.RED.name();
                    }
                }
                detail.setColor(color);
                return detail;
            }).collect(Collectors.toList());
            rsp.setList(list);

            // 合计
            rsp.setSum(accountBalanceSumHandle(accountBalanceBaseInfoList));

        }
        return rsp;
    }

    private List<AccountBalanceDetailListRSP.AccountBalanceSum> accountBalanceSumHandle(List<AccountBalanceBaseInfo> accountBalanceBaseInfoList) {
        Map<LocalDate, List<AccountBalanceBaseInfo>> baseInfoMap = accountBalanceBaseInfoList.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate));
        List<AccountBalanceDetailListRSP.AccountBalanceSum> result = baseInfoMap.entrySet().stream().map(entry -> {
            AccountBalanceDetailListRSP.AccountBalanceDetail all = new AccountBalanceDetailListRSP.AccountBalanceDetail();
            AccountBalanceDetailListRSP.AccountBalanceDetail supervision = new AccountBalanceDetailListRSP.AccountBalanceDetail();
            AccountBalanceDetailListRSP.AccountBalanceDetail noSupervision = new AccountBalanceDetailListRSP.AccountBalanceDetail();
            for (AccountBalanceBaseInfo accountBalance : entry.getValue()) {
                if (Objects.equals(accountBalance.getAccountType(), BaseDataBankAccountTypeEnum.SUPERVISION.name())) {
                    supervision.setDiffAmount(Optional.ofNullable(supervision.getDiffAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getDiffAmount()).orElse(0L));
                    supervision.setActualBalanceAmount(Optional.ofNullable(supervision.getActualBalanceAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getActualBalanceAmount()).orElse(0L));
                    supervision.setDrawingsAmount(Optional.ofNullable(supervision.getDrawingsAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getDrawingsAmount()).orElse(0L));
                    supervision.setRepayAmount(Optional.ofNullable(supervision.getRepayAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayAmount()).orElse(0L));
                    supervision.setEstimateBalanceAmount(Optional.ofNullable(supervision.getEstimateBalanceAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getEstimateBalanceAmount()).orElse(0L));
                    supervision.setMustExpenseAmount(Optional.ofNullable(supervision.getMustExpenseAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getMustExpenseAmount()).orElse(0L));
                    supervision.setOtherExpenseAmount(Optional.ofNullable(supervision.getOtherExpenseAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getOtherExpenseAmount()).orElse(0L));
                    supervision.setOtherFlowAmount(Optional.ofNullable(supervision.getOtherFlowAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getOtherFlowAmount()).orElse(0L));
                    supervision.setPaymentAmount(Optional.ofNullable(supervision.getPaymentAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getPaymentAmount()).orElse(0L));
                    supervision.setRentReflowAmount(Optional.ofNullable(supervision.getRentReflowAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRentReflowAmount()).orElse(0L));
                    supervision.setRepayAbsAmount(Optional.ofNullable(supervision.getRepayAbsAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayAbsAmount()).orElse(0L));
                    supervision.setRepayEditAmount(Optional.ofNullable(supervision.getRepayEditAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayEditAmount()).orElse(0L));
                    supervision.setEstimateBalanceLimitAmount(Optional.ofNullable(supervision.getEstimateBalanceLimitAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getEstimateBalanceLimitEditAmount()).orElse(LongUtil.null2zero(accountBalance.getEstimateBalanceLimitAmount())));
                    supervision.setRepayNoAbsAmount(Optional.ofNullable(supervision.getRepayNoAbsAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayNoAbsAmount()).orElse(0L));
                } else {
                    noSupervision.setDiffAmount(Optional.ofNullable(noSupervision.getDiffAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getDiffAmount()).orElse(0L));
                    noSupervision.setActualBalanceAmount(Optional.ofNullable(noSupervision.getActualBalanceAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getActualBalanceAmount()).orElse(0L));
                    noSupervision.setDrawingsAmount(Optional.ofNullable(noSupervision.getDrawingsAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getDrawingsAmount()).orElse(0L));
                    noSupervision.setRepayAmount(Optional.ofNullable(noSupervision.getRepayAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayAmount()).orElse(0L));
                    noSupervision.setEstimateBalanceAmount(Optional.ofNullable(noSupervision.getEstimateBalanceAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getEstimateBalanceAmount()).orElse(0L));
                    noSupervision.setMustExpenseAmount(Optional.ofNullable(noSupervision.getMustExpenseAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getMustExpenseAmount()).orElse(0L));
                    noSupervision.setOtherExpenseAmount(Optional.ofNullable(noSupervision.getOtherExpenseAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getOtherExpenseAmount()).orElse(0L));
                    noSupervision.setOtherFlowAmount(Optional.ofNullable(noSupervision.getOtherFlowAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getOtherFlowAmount()).orElse(0L));
                    noSupervision.setPaymentAmount(Optional.ofNullable(noSupervision.getPaymentAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getPaymentAmount()).orElse(0L));
                    noSupervision.setRentReflowAmount(Optional.ofNullable(noSupervision.getRentReflowAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRentReflowAmount()).orElse(0L));
                    noSupervision.setRepayAbsAmount(Optional.ofNullable(noSupervision.getRepayAbsAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayAbsAmount()).orElse(0L));
                    noSupervision.setRepayEditAmount(Optional.ofNullable(noSupervision.getRepayEditAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayEditAmount()).orElse(0L));
                    noSupervision.setEstimateBalanceLimitAmount(Optional.ofNullable(noSupervision.getEstimateBalanceLimitAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getEstimateBalanceLimitEditAmount()).orElse(LongUtil.null2zero(accountBalance.getEstimateBalanceLimitAmount())));
                    noSupervision.setRepayNoAbsAmount(Optional.ofNullable(noSupervision.getRepayNoAbsAmount()).orElse(0L) + Optional.ofNullable(accountBalance.getRepayNoAbsAmount()).orElse(0L));
                }
            }
            all.setDiffAmount(Optional.ofNullable(supervision.getDiffAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getDiffAmount()).orElse(0L));
            all.setActualBalanceAmount(Optional.ofNullable(supervision.getActualBalanceAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getActualBalanceAmount()).orElse(0L));
            all.setDrawingsAmount(Optional.ofNullable(supervision.getDrawingsAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getDrawingsAmount()).orElse(0L));
            all.setRepayAmount(Optional.ofNullable(supervision.getRepayAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getRepayAmount()).orElse(0L));
            all.setEstimateBalanceAmount(Optional.ofNullable(supervision.getEstimateBalanceAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getEstimateBalanceAmount()).orElse(0L));
            all.setMustExpenseAmount(Optional.ofNullable(supervision.getMustExpenseAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getMustExpenseAmount()).orElse(0L));
            all.setOtherExpenseAmount(Optional.ofNullable(supervision.getOtherExpenseAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getOtherExpenseAmount()).orElse(0L));
            all.setOtherFlowAmount(Optional.ofNullable(supervision.getOtherFlowAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getOtherFlowAmount()).orElse(0L));
            all.setPaymentAmount(Optional.ofNullable(supervision.getPaymentAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getPaymentAmount()).orElse(0L));
            all.setRentReflowAmount(Optional.ofNullable(supervision.getRentReflowAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getRentReflowAmount()).orElse(0L));
            all.setRepayAbsAmount(Optional.ofNullable(supervision.getRepayAbsAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getRepayAbsAmount()).orElse(0L));
            all.setRepayEditAmount(Optional.ofNullable(supervision.getRepayEditAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getRepayEditAmount()).orElse(0L));
            all.setEstimateBalanceLimitAmount(Optional.ofNullable(supervision.getEstimateBalanceLimitAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getEstimateBalanceLimitAmount()).orElse(0L));
            all.setRepayNoAbsAmount(Optional.ofNullable(supervision.getRepayNoAbsAmount()).orElse(0L) + Optional.ofNullable(noSupervision.getRepayNoAbsAmount()).orElse(0L));

            AccountBalanceDetailListRSP.AccountBalanceSum sum = new AccountBalanceDetailListRSP.AccountBalanceSum();
            sum.setDate(entry.getKey());
            sum.setSupervisionSum(supervision);
            sum.setNoSupervisionSum(noSupervision);
            sum.setAllSum(all);
            return sum;
        }).collect(Collectors.toList());
        return result;
    }

    @Transactional(rollbackFor = Throwable.class)
    public AccountBalanceDetailModifyRSP accountBalanceModify(AccountBalanceDetailModifyREQ req) {
        if(req != null && CollectionUtil.isNotEmpty(req.getList())){
            List<AccountBalanceBaseInfo> accountBalanceBaseInfoList = this.listByIds(req.getList().stream().map(AccountBalanceDetailModifyREQ.AccountBalanceDetailModify::getId).collect(Collectors.toList()));
            Map<Long, AccountBalanceBaseInfo> accountOriginMap = accountBalanceBaseInfoList.stream().collect(Collectors.toMap(AccountBalanceBaseInfo::getId, Function.identity()));
            List<AccountBalanceBaseInfo> baseInfoList = req.getList().stream().map(item -> {
                AccountBalanceBaseInfo accountBalanceBaseInfo = BeanUtil.copyProperties(item, AccountBalanceBaseInfo.class);
                AccountBalanceBaseInfo originBaseInfo = accountOriginMap.get(item.getId());
                if(!Objects.equals(originBaseInfo.getEstimateBalanceLimitAmount(), accountBalanceBaseInfo.getEstimateBalanceLimitAmount()) ||
                        !Objects.equals(originBaseInfo.getEstimateBalanceLimitEditAmount(), accountBalanceBaseInfo.getEstimateBalanceLimitAmount())) {
                    // 判断结余受限是否发生的变更，若变更则更新编辑字段
                    accountBalanceBaseInfo.setEstimateBalanceLimitAmount(null);
                    accountBalanceBaseInfo.setEstimateBalanceLimitEditAmount(item.getEstimateBalanceLimitAmount());
                }
                return accountBalanceBaseInfo;
            }).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(baseInfoList)) {
                this.updateBatchById(baseInfoList);
                LocalDate startDate = accountBalanceBaseInfoList.stream().map(AccountBalanceBaseInfo::getDate).min(Comparator.comparing(Function.identity())).orElse(null);
                // 异步重算账户余额表指标
                this.init(true, startDate);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        ThreadPoolUtil.getCommonPool().execute(() -> init(false, startDate));
                    }
                });
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importExcel(InputStream inputStream) {
        List<AccountBalanceBaseInfoExcelModel> excelModelList = accountBalanceBaseInfoImporter.parse(inputStream);
        for (AccountBalanceBaseInfoExcelModel excelModel : excelModelList) {
            Assert.notNull(excelModel.getAccountNumber(), () -> MithrasException.newException("请检查文件数据,银行账户不得为空"));
            Assert.notNull(excelModel.getDate(), () -> MithrasException.newException("请检查文件数据,日期不得为空"));
        }
        LocalDate maxDate = excelModelList.stream().max(Comparator.comparing(AccountBalanceBaseInfoExcelModel::getDate)).map(AccountBalanceBaseInfoExcelModel::getDate).orElse(null);
        if(maxDate == null){
            throw new MithrasException("文件为空");
        }
        List<AccountBalanceBaseInfo> list = accountBalanceBaseInfoService.list(Wrappers.<AccountBalanceBaseInfo>lambdaQuery().le(AccountBalanceBaseInfo::getDate, maxDate));
        Map<LocalDate, Map<String, AccountBalanceBaseInfo>> baseInfoMap = list.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate,
                Collectors.toMap(AccountBalanceBaseInfo::getAccountNumber, Function.identity())));

        List<AccountBalanceBaseInfo> needUpdateBaseInfoList = excelModelList.stream().map(excelModel -> {
            AccountBalanceBaseInfo accountBalanceBaseInfo = baseInfoMap.getOrDefault(excelModel.getDate(), new HashMap<>()).get(excelModel.getAccountNumber());
            if (Objects.isNull(accountBalanceBaseInfo)) {
                throw new MithrasException(String.format("%s银行账号为%s的数据不存在", excelModel.getDate().toString(), excelModel.getAccountNumber()));
            }
            accountBalanceBaseInfo.setDrawingsAmount(Util.toMithrasUnit(excelModel.getDrawingsAmount()));
            accountBalanceBaseInfo.setOtherFlowAmount(Util.toMithrasUnit(excelModel.getOtherFlowAmount()));
            accountBalanceBaseInfo.setPaymentAmount(Util.toMithrasUnit(excelModel.getPaymentAmount()));
            accountBalanceBaseInfo.setRepayEditAmount(Util.toMithrasUnit(excelModel.getRepayEditAmount()));
            accountBalanceBaseInfo.setMustExpenseAmount(Util.toMithrasUnit(excelModel.getMustExpenseAmount()));
            accountBalanceBaseInfo.setOtherExpenseAmount(Util.toMithrasUnit(excelModel.getOtherExpenseAmount()));
            if (!Objects.equals(accountBalanceBaseInfo.getEstimateBalanceLimitAmount(), Util.toMithrasUnit(excelModel.getEstimateBalanceLimitAmount())) ||
                    !Objects.equals(accountBalanceBaseInfo.getEstimateBalanceLimitEditAmount(), Util.toMithrasUnit(excelModel.getEstimateBalanceLimitAmount()))) {
                // 判断结余受限是否发生的变更，若变更则更新编辑字段
                accountBalanceBaseInfo.setEstimateBalanceLimitEditAmount(Util.toMithrasUnit(excelModel.getEstimateBalanceLimitAmount()));
            }
            accountBalanceBaseInfo.setActualBalanceAmount(Util.toMithrasUnit(excelModel.getActualBalanceAmount()));
            return accountBalanceBaseInfo;
        }).collect(Collectors.toList());

        if(CollectionUtil.isNotEmpty(needUpdateBaseInfoList)){
            updateBatchById(needUpdateBaseInfoList);
            LocalDate startDate = needUpdateBaseInfoList.stream().map(AccountBalanceBaseInfo::getDate).min(Comparator.comparing(Function.identity())).orElse(null);
            log.info("导入账户余额表成功，截止时间:{}", maxDate);
            // 异步重算账户余额表指标
            this.init(true, startDate);
            ThreadPoolUtil.getCommonPool().execute(() -> {
                init(false, startDate);
            });
        }

    }
}
