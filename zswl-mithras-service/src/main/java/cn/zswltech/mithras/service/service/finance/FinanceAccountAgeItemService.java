package cn.zswltech.mithras.service.service.finance;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.finance.accountage.*;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.finance.enums.third.FinancialAccountAgeRecordStatus;
import cn.zswltech.mithras.finance.enums.third.FinancialAccountAgeSendStatusStatus;
import cn.zswltech.mithras.finance.enums.third.FinancialAccountNumberENUM;
import cn.zswltech.mithras.finance.enums.third.FinancialPaymentContentENUM;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.finance.mapper.finance.FinanceAccountAgeBaseInfoMapper;
import cn.zswltech.mithras.finance.mapper.finance.FinanceAccountAgeItemMapper;
import cn.zswltech.mithras.finance.mapper.finance.query.FinanceAccountAgeitemCountQuery;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceAccountAgeBaseInfo;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceAccountAgeItem;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQ2AccountAgeAddREQ;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 帐龄-详情表
 * @date 2024-09-10
 */
@Service
@Slf4j
public class FinanceAccountAgeItemService extends ServiceImpl<FinanceAccountAgeItemMapper, FinanceAccountAgeItem> {

    @Resource
    private FinanceAccountAgeItemMapper financeAccountAgeItemMapper;

    @Resource
    private FinanceAccountAgeBaseInfoMapper financeAccountAgeBaseInfoMapper;

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private ContractReceiptMapper contractReceiptMapper;

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ContractTenantryService contractTenantryService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FinanceAccountAgeItemAddREQ req) {
        this.check(req.getAccountAgeId());
        FinanceAccountAgeItem info = BeanUtil.copyProperties(req, FinanceAccountAgeItem.class);
        info.setSource(YesOrNoNumberEnum.YES.getCode());
        if(ObjectUtil.isEmpty(info.getAccountancyOrganizationNumber())) {
            info.setAccountancyOrganizationNumber(FinancialConstants.RZZL_CODE);
        }
        if(ObjectUtil.isEmpty(info.getAccountancyOrganizationName())) {
            info.setAccountancyOrganizationName(FinancialConstants.RZZL_NAME);
        }
        financeAccountAgeItemMapper.insert(info);
    }

    /**
     * 创建某个时间段内的帐龄
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void accountAgeInit(Long accountAgeId) {
        FinanceAccountAgeBaseInfo financeAccountAgeBaseInfo = financeAccountAgeBaseInfoMapper.selectById(accountAgeId);
        if (ObjectUtil.isEmpty(financeAccountAgeBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<Long> receiptIds = contractReceiptMapper.selectList(Wrappers.<ContractReceipt>lambdaQuery()
                .le(ContractReceipt::getReceiptStartDate, financeAccountAgeBaseInfo.getDeadline())).stream().map(ContractReceipt::getId).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(receiptIds)) {
            return;
        }
        //查询截止日期前的租金
        List<CollectionBaseInfo> allCollectionBaseList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .in(CollectionBaseInfo::getReceiptId, receiptIds));
        SpringContextHolder.getBean(FinanceAccountAgeItemService.class).calculateFinanceAccountAge(allCollectionBaseList, accountAgeId);
    }

    //计算并更新或保存某期下的收款帐龄
    @Transactional(rollbackFor = Throwable.class)
    public List<FinanceAccountAgeItem> calculateFinanceAccountAge(List<CollectionBaseInfo> allCollectionBaseList, Long accountAgeId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("准备数据");

        FinanceAccountAgeBaseInfo financeAccountAgeBaseInfo = financeAccountAgeBaseInfoMapper.selectById(accountAgeId);
        if (ObjectUtil.isEmpty(financeAccountAgeBaseInfo) || ObjectUtil.isEmpty(allCollectionBaseList)) {
            return Collections.emptyList();
        }
        List<Long> allCollections = allCollectionBaseList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
        //获取上一期
        List<FinanceAccountAgeItem> lastAccountAgeItemByCollectionIds = financeAccountAgeItemMapper.getLastAccountAgeItemByCollectionIds(allCollections);
        Map<Long, FinanceAccountAgeItem> collectionId2LastAccountMap = new HashMap<>();
        Set<Long> receiptIdSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(lastAccountAgeItemByCollectionIds)) {
            collectionId2LastAccountMap = lastAccountAgeItemByCollectionIds.stream().collect(Collectors.toMap(FinanceAccountAgeItem::getCollectionId, e -> e, (a, b) -> a));
            receiptIdSet.addAll(lastAccountAgeItemByCollectionIds.stream().map(FinanceAccountAgeItem::getReceiptId).collect(Collectors.toSet()));
        }
        stopWatch.stop();
        stopWatch.start("准备数据1");
        Set<Long> newAddReceipt = allCollectionBaseList.stream().map(CollectionBaseInfo::getReceiptId).filter(receiptId -> !receiptIdSet.contains(receiptId)).collect(Collectors.toSet());
        //获取借据起租时间
        Map<Long, LocalDate> receiptId2Date = contractReceiptMapper.selectBatchIds(allCollectionBaseList.stream().map(CollectionBaseInfo::getReceiptId).collect(Collectors.toSet())).stream().filter(e -> ObjectUtil.isNotEmpty(e.getReceiptStartDate())).collect(Collectors.toMap(ContractReceipt::getId,
                ContractReceipt::getReceiptStartDate, (a, b) -> b));
        //获取合同相关信息
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectBatchIds(allCollectionBaseList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()));
        Map<Long, String> contractId2ProjName = new HashMap<Long, String>();
        if (ObjectUtil.isNotEmpty(contractBaseInfos)) {
            contractId2ProjName.putAll(contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjName, (a, b) -> b)));
        }
        stopWatch.stop();
        stopWatch.start("准备数据2");
        //获取不同时间的租金的未核销金额
        Map<Long, Long> newAddAmountMap = collectionBaseInfoService.sumRemainingPrincipalByCollection(allCollectionBaseList.stream().filter(baseInfo -> !receiptIdSet.contains(baseInfo.getReceiptId())).map(CollectionBaseInfo::getId).collect(Collectors.toList()), financeAccountAgeBaseInfo.getDeadline());
        Map<Long, Long> oldAmountMap = collectionBaseInfoService.sumRemainingPrincipalByCollection(allCollectionBaseList.stream().filter(baseInfo -> receiptIdSet.contains(baseInfo.getReceiptId())).map(CollectionBaseInfo::getId).collect(Collectors.toList()), financeAccountAgeBaseInfo.getDeadline());
        stopWatch.stop();
        stopWatch.start("构建");
        //开始构建
        List<FinanceAccountAgeItem> financeAccountAgeItems = this.buildFinanceAccountAgeItem(financeAccountAgeBaseInfo, allCollectionBaseList, collectionId2LastAccountMap, newAddReceipt, newAddAmountMap, oldAmountMap, receiptId2Date, contractId2ProjName);
        stopWatch.stop();
        stopWatch.start("结束");
        List<FinanceAccountAgeItem> needAddItems = new ArrayList<>();
        List<FinanceAccountAgeItem> needUpdateItems = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(financeAccountAgeItems)) {
            //合并数据
            List<FinanceAccountAgeItem> sendOriginalInfos = new ArrayList<>();
            Map<Long, List<FinanceAccountAgeItem>> contractId2Item = financeAccountAgeItems.stream().collect(Collectors.groupingBy(FinanceAccountAgeItem::getContractId));
            contractId2Item.forEach((k, v) -> {
                Map<LocalDate, List<FinanceAccountAgeItem>> collect = v.stream().collect(Collectors.groupingBy(FinanceAccountAgeItem::getPlanCollectionDate));
                collect.values().forEach(l -> {
                    if (ObjectUtil.isNotEmpty(l)) {
                        FinanceAccountAgeItem ageItem = l.get(0);
                        for(int i = 1; i < l.size(); i++) {
                            ageItem.setOriginalValueInitial(add(l.get(i).getOriginalValueInitial(), ageItem.getOriginalValueInitial()));
                            ageItem.setOriginalValueIncrease(add(l.get(i).getOriginalValueIncrease(), ageItem.getOriginalValueIncrease()));
                            ageItem.setOriginalValueReduce(add(l.get(i).getOriginalValueReduce(), ageItem.getOriginalValueReduce()));
                            ageItem.setOriginalValueFinal(add(l.get(i).getOriginalValueFinal(), ageItem.getOriginalValueFinal()));
                        }
                        sendOriginalInfos.add(ageItem);
                    }
                });
            });

            //查询已经存在的
            Map<Long, FinanceAccountAgeItem> financeAccountAgeItemId2Bean = financeAccountAgeItemMapper.selectList(Wrappers.<FinanceAccountAgeItem>lambdaQuery()
                    .eq(FinanceAccountAgeItem::getAccountAgeId, accountAgeId)
                    .in(FinanceAccountAgeItem::getCollectionId, allCollections)).stream().collect(Collectors.toMap(FinanceAccountAgeItem::getCollectionId, e -> e, (a, b) -> b));
            sendOriginalInfos.forEach(financeAccountAgeItem -> {
                //这里
                FinanceAccountAgeItem oldItem = financeAccountAgeItemId2Bean.get(financeAccountAgeItem.getCollectionId());
                if(ObjectUtil.isEmpty(oldItem)){
                    needAddItems.add(financeAccountAgeItem);
                } else if (ObjectUtil.equals(oldItem.getSendStatus(), FinancialAccountAgeSendStatusStatus.SUCCESS.name())) {
                    //这里不能处理
                } else {
                    financeAccountAgeItem.setId(oldItem.getId());
                    needUpdateItems.add(financeAccountAgeItem);
                }
            });
            stopWatch.stop();
            stopWatch.start("保存");
            if(!needAddItems.isEmpty()){
                SpringContextHolder.getBean(FinanceAccountAgeItemService.class).saveBatch(needAddItems);
            }
            if(!needUpdateItems.isEmpty()){
                SpringContextHolder.getBean(FinanceAccountAgeItemService.class).updateBatchById(needUpdateItems);
            }
            stopWatch.stop();
        }
        log.info("FinanceAccountAgeItemService calculateFinanceAccountAge end {}", stopWatch.prettyPrint(TimeUnit.SECONDS));
        return needAddItems;
    }

    private List<FinanceAccountAgeItem> buildFinanceAccountAgeItem(FinanceAccountAgeBaseInfo financeAccountAgeBaseInfo, List<CollectionBaseInfo> allCollectionBaseList, Map<Long, FinanceAccountAgeItem> collectionId2LastAccountMap, Set<Long> newAddReceipt,
                                                                   Map<Long, Long> newAddAmountMap, Map<Long, Long> oldAmountMap, Map<Long, LocalDate> receiptId2Date, Map<Long, String> contractId2ProjName) {
        List<FinanceAccountAgeItem> items = new ArrayList<FinanceAccountAgeItem>();
        //获取租金往来方
        Map<Long, String> clientId2RentConcatAccount = contractTenantryService.listRentConcatAccountByContractId(allCollectionBaseList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()));
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientId2RentConcatAccount.values().stream().map(Long::parseLong).collect(Collectors.toList()));
        allCollectionBaseList.forEach(collectionBaseInfo -> {
            FinanceAccountAgeItem item = new FinanceAccountAgeItem();
            item.setAccountAgeId(financeAccountAgeBaseInfo.getId());
            item.setAccountancyOrganizationNumber(FinancialConstants.RZZL_CODE);
            item.setAccountancyOrganizationName(FinancialConstants.RZZL_NAME);
            //判断是否是新增借据的
            if (newAddReceipt.contains(collectionBaseInfo.getReceiptId())) {
                //这里是新增逻辑
                item.setOriginalValueInitial(BigDecimal.ZERO);
                item.setOriginalValueFinal(LongUtil.tenThousand2Dollar(String.valueOf(newAddAmountMap.getOrDefault(collectionBaseInfo.getId(), 0L))));
            } else {
                //更新逻辑
                item.setOriginalValueInitial(Optional.ofNullable(collectionId2LastAccountMap.get(collectionBaseInfo.getId())).map(FinanceAccountAgeItem::getOriginalValueFinal).orElse(BigDecimal.ZERO));
                item.setOriginalValueFinal(LongUtil.tenThousand2Dollar(String.valueOf(oldAmountMap.getOrDefault(collectionBaseInfo.getId(), 0L))));
            }
            item.setCurrency(FinancialConstants.RMB);
            item.setAccountNumber(FinancialAccountNumberENUM.LONG_TERM_ACCOUNT_RECEIVABLE.name());
            item.setPaymentContent(FinancialPaymentContentENUM.KX08.name());
            item.setClientId(clientId2RentConcatAccount.get(collectionBaseInfo.getContractId()) == null ? null : Long.parseLong(clientId2RentConcatAccount.get(collectionBaseInfo.getContractId())));
            item.setCustomerUnitName(clientId2RentConcatAccount.get(collectionBaseInfo.getContractId()) == null ? null : clientId2Name.get(Long.parseLong(clientId2RentConcatAccount.get(collectionBaseInfo.getContractId()))));
            item.setBusinessDate(receiptId2Date.get(collectionBaseInfo.getReceiptId()));
            item.setAgingDeadline(financeAccountAgeBaseInfo.getDeadline());
            if (ObjectUtil.isNotEmpty(item.getAgingDeadline()) && ObjectUtil.isNotEmpty(item.getBusinessDate())) {
                long between = ChronoUnit.DAYS.between(item.getBusinessDate(), item.getAgingDeadline());
                item.setBusinessAge((int) (between / 30));
            }
            item.setContractId(collectionBaseInfo.getContractId());
            item.setReceiptId(collectionBaseInfo.getReceiptId());
            item.setCollectionId(collectionBaseInfo.getId());
            item.setCollectionCode(collectionBaseInfo.getCode());
            item.setContractCode(collectionBaseInfo.getContractCode());
            item.setProjName(contractId2ProjName.get(collectionBaseInfo.getContractId()));
            item.setPlanCollectionDate(collectionBaseInfo.getPlanCollectionDate());
            item.setSendStatus(FinancialAccountAgeSendStatusStatus.NEW.name());
            // >0增加 <0减少 期末 - 期初
            BigDecimal subtract = item.getOriginalValueFinal().subtract(item.getOriginalValueInitial());
            if (subtract.compareTo(BigDecimal.ZERO) >= 0){
                item.setOriginalValueIncrease(subtract);
                item.setOriginalValueReduce(BigDecimal.ZERO);
            } else {
                item.setOriginalValueIncrease(BigDecimal.ZERO);
                item.setOriginalValueReduce(subtract.abs());
            }
            //起初金额，本期增加，本期减少，期末金额四个全是0的就过滤掉
            if (notZero(item.getOriginalValueInitial()) || notZero(item.getOriginalValueIncrease()) ||
                    notZero(item.getOriginalValueReduce()) || notZero(item.getOriginalValueFinal())) {
                items.add(item);
            }
        });
        return items;
    }

    private boolean notZero (BigDecimal value) {
        //null 也认为符合
        if(ObjectUtil.isEmpty(value)) {
            return true;
        }
        return value.compareTo(BigDecimal.ZERO) != 0;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void modify(FinanceAccountAgeItemModifyREQ req) {
        this.check(req.getAccountAgeId());
        FinanceAccountAgeItem originalInfo = financeAccountAgeItemMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FinanceAccountAgeItem info = BeanUtil.copyProperties(req, FinanceAccountAgeItem.class);
        financeAccountAgeItemMapper.updateById(info);
    }

    public Page<FinanceAccountAgeItem> list(FinanceAccountAgeItemListREQ req) {
        return financeAccountAgeItemMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FinanceAccountAgeItem>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getClientId()), FinanceAccountAgeItem::getClientId, req.getClientId())
                .eq(ObjectUtil.isNotEmpty(req.getAccountAgeId()), FinanceAccountAgeItem::getAccountAgeId, req.getAccountAgeId())
                .like(ObjectUtil.isNotEmpty(req.getContractCode()), FinanceAccountAgeItem::getContractCode, req.getContractCode())
                .like(ObjectUtil.isNotEmpty(req.getProjName()), FinanceAccountAgeItem::getProjName, req.getProjName())
                .eq(ObjectUtil.isNotEmpty(req.getSendStatus()), FinanceAccountAgeItem::getSendStatus, req.getSendStatus())
                .like(ObjectUtil.isNotEmpty(req.getCustomerUnitName()), FinanceAccountAgeItem::getCustomerUnitName, req.getCustomerUnitName())
                .ge(ObjectUtil.isNotEmpty(req.getPlanCollectionDateFrom()), FinanceAccountAgeItem::getPlanCollectionDate, req.getPlanCollectionDateFrom())
                .le(ObjectUtil.isNotEmpty(req.getPlanCollectionDateTo()), FinanceAccountAgeItem::getPlanCollectionDate, req.getPlanCollectionDateTo())
                .in(ObjectUtil.isNotEmpty(req.getIds()), FinanceAccountAgeItem::getId, req.getIds())
                .orderByAsc(FinanceAccountAgeItem::getSource)
                .orderByAsc(FinanceAccountAgeItem::getContractId)
                .orderByAsc(FinanceAccountAgeItem::getPlanCollectionDate));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FinanceAccountAgeItemRemoveREQ req) {
        List<FinanceAccountAgeItem> originalInfos = financeAccountAgeItemMapper.selectBatchIds(req.getIds());
        if (ObjectUtil.isEmpty(originalInfos)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        this.check(originalInfos.get(0).getAccountAgeId());
        List<FinanceAccountAgeBaseInfo> financeAccountAgeBaseInfos = financeAccountAgeBaseInfoMapper.selectBatchIds(originalInfos.stream().map(FinanceAccountAgeItem::getAccountAgeId).collect(Collectors.toList()));
        if (ObjectUtil.isEmpty(financeAccountAgeBaseInfos)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        financeAccountAgeBaseInfos.forEach(e -> {
            if (!ObjectUtil.equals(e.getStatus(), FinancialAccountAgeRecordStatus.NEW.name())) {
                throw new MithrasException("非新建账龄，不可删除");
            }
        });
        originalInfos.forEach(e -> {
            if (ObjectUtil.equals(e.getSendStatus(), FinancialAccountAgeSendStatusStatus.SUCCESS.name())) {
                throw new MithrasException("存在推送成功数据，不可删除");
            }
        });
        financeAccountAgeItemMapper.deleteBatchIds(req.getIds());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void regeneration(@Valid FinanceAccountAgeItemRemoveREQ req){
        if(ObjectUtil.isEmpty(req.getIds())) {
            if(ObjectUtil.isEmpty(req.getAccountAgeId())) {
                throw new MithrasException("帐龄id不能为空");
            }
            FinanceAccountAgeBaseInfo financeAccountAgeBaseInfo = financeAccountAgeBaseInfoMapper.selectById(req.getAccountAgeId());
            check(req.getAccountAgeId());
            List<Long> receiptIds = contractReceiptMapper.selectList(Wrappers.<ContractReceipt>lambdaQuery()
                    .le(ContractReceipt::getReceiptStartDate, financeAccountAgeBaseInfo.getDeadline())).stream().map(ContractReceipt::getId).collect(Collectors.toList());
            if(ObjectUtil.isEmpty(receiptIds)) {
                return;
            }
            //查询截止日期前的租金
            List<CollectionBaseInfo> allCollectionBaseList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .gt(CollectionBaseInfo::getPhase, 0)
                    .in(CollectionBaseInfo::getReceiptId, receiptIds));
            SpringContextHolder.getBean(FinanceAccountAgeItemService.class).calculateFinanceAccountAge(allCollectionBaseList, req.getAccountAgeId());
            return;
        }
        //未发送成功的可重新计算
        List<FinanceAccountAgeItem> originalInfos = financeAccountAgeItemMapper.selectList(Wrappers.<FinanceAccountAgeItem>lambdaQuery()
                .in(FinanceAccountAgeItem::getId, req.getIds())
                .eq(FinanceAccountAgeItem::getSource, YesOrNoNumberEnum.NO.getCode())
                .ne(FinanceAccountAgeItem::getSendStatus, FinancialAccountAgeSendStatusStatus.SUCCESS.name()));
        if (ObjectUtil.isEmpty(originalInfos)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        this.check(originalInfos.get(0).getAccountAgeId());
        SpringContextHolder.getBean(FinanceAccountAgeItemService.class).calculateFinanceAccountAge(collectionBaseInfoService.listByIds(originalInfos.stream().map(FinanceAccountAgeItem::getCollectionId).collect(Collectors.toSet())), originalInfos.get(0).getAccountAgeId());
    }

    public FinanceAccountAgeCountRSP count(FinanceAccountAgeBaseInfoDetailREQ req) {
        FinanceAccountAgeitemCountQuery query = new FinanceAccountAgeitemCountQuery();
        query.setAccountAgeId(req.getId());
        FinanceAccountAgeItem financeAccountAgeItem = financeAccountAgeItemMapper.countAmount(query);
        return BeanUtil.copyProperties(financeAccountAgeItem, FinanceAccountAgeCountRSP.class);
    }

   public void sendRemote(@Valid FinanceAccountAgeItemRemoveREQ req) {
       if (ObjectUtil.isEmpty(req.getAccountAgeId())) {
           throw new MithrasException("帐龄id不能为空");
       }
       SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator();
       List<FinanceAccountAgeItem> originalInfos = financeAccountAgeItemMapper.selectList(Wrappers.<FinanceAccountAgeItem>lambdaQuery()
               .in(ObjectUtil.isNotEmpty(req.getIds()), FinanceAccountAgeItem::getId, req.getIds())
               .eq(FinanceAccountAgeItem::getAccountAgeId, req.getAccountAgeId())
               .notIn(FinanceAccountAgeItem::getSendStatus, ListUtil.toList(FinancialAccountAgeSendStatusStatus.SUCCESS.name(), FinancialAccountAgeSendStatusStatus.PUSH_IN_PROGRESS.name())));
       if (ObjectUtil.isEmpty(originalInfos)) {
           throw new MithrasException("无可推送数据");
       }
       //占用
       LambdaUpdateWrapper<FinanceAccountAgeItem> updateWrapper = new LambdaUpdateWrapper<>();
       updateWrapper.set(FinanceAccountAgeItem::getSendStatus, FinancialAccountAgeSendStatusStatus.PUSH_IN_PROGRESS.name());
       updateWrapper.in(FinanceAccountAgeItem::getId, originalInfos.stream().map(FinanceAccountAgeItem::getId).collect(Collectors.toList()));
       SpringContextHolder.getBean(FinanceAccountAgeItemService.class).update(updateWrapper);
       List<FinanceAccountAgeItem> sendInfos = new ArrayList<>();

       originalInfos.forEach(e -> {
           sendInfos.add(e);
           //100发一次
           if (sendInfos.size() == 100) {
               List<CQ2AccountAgeAddREQ> cq2AccountAgeAddREQS = this.buildCQ2AccountAgeAddREQ(sendInfos, snowflakeIdGenerator);
               financialManagerServiceImpl2.sendAccountAge(cq2AccountAgeAddREQS);
               sendInfos.clear();
           }
       });
       if (!sendInfos.isEmpty()) {
           List<CQ2AccountAgeAddREQ> cq2AccountAgeAddREQS = this.buildCQ2AccountAgeAddREQ(sendInfos, snowflakeIdGenerator);
           financialManagerServiceImpl2.sendAccountAge(cq2AccountAgeAddREQS);
       }
   }

    private BigDecimal add(BigDecimal a, BigDecimal b) {
        if (ObjectUtil.isEmpty(a)) {
            a = BigDecimal.ZERO;
        }
        if (ObjectUtil.isEmpty(b)) {
            b = BigDecimal.ZERO;
        }
        return a.add(b);
   }

   //构建苍穹参数
   private List<CQ2AccountAgeAddREQ> buildCQ2AccountAgeAddREQ(List<FinanceAccountAgeItem> originalInfos, SnowflakeIdGenerator snowflakeIdGenerator) {
       List<CQ2AccountAgeAddREQ> reqs = new ArrayList<>();
       List<Long> clientIds = originalInfos.stream().map(FinanceAccountAgeItem::getClientId).collect(Collectors.toList());
       Map<Long, String> clientId2Code = new HashMap<>();
       if (ObjectUtil.isNotEmpty(clientIds)) {
           clientId2Code.putAll(clientMapper.selectBatchIds(clientIds).stream().filter(e -> ObjectUtil.isNotEmpty(e.getClientCode())).collect(Collectors.toMap(Client::getId, Client::getClientCode, (a, b) -> b)));
       }
       originalInfos.forEach(originalInfo -> {
           CQ2AccountAgeAddREQ req = new CQ2AccountAgeAddREQ();
           req.setBusinessId(snowflakeIdGenerator.nextId());
           req.setOrg(originalInfo.getAccountancyOrganizationNumber());
           req.setOriginalValue(originalInfo.getOriginalValueFinal().setScale(2, RoundingMode.HALF_UP));
           req.setCurrency(originalInfo.getCurrency());
           req.setSubject(Optional.ofNullable(FinancialAccountNumberENUM.findByName(originalInfo.getAccountNumber())).map(FinancialAccountNumberENUM::getCode).orElse(FinancialAccountNumberENUM.LONG_TERM_ACCOUNT_RECEIVABLE.getCode()));
           req.setPayContent(originalInfo.getPaymentContent());
           if (ObjectUtil.isNotEmpty(originalInfo.getClientId())) {
               req.setAssact(clientId2Code.get(originalInfo.getClientId()));
           } else {
               req.setAssact(originalInfo.getCustomerUnitName());
           }
           req.setAssactType(FinancialConstants.BD_CUSTOMER);
           req.setAcctAgeBegining(originalInfo.getBusinessDate() == null ? null : originalInfo.getBusinessDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
           req.setBusinessAging(originalInfo.getBusinessAge());
           req.setAgingDeadline(originalInfo.getAgingDeadline() == null ? null : originalInfo.getAgingDeadline().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
           req.setCico_original_qc(originalInfo.getOriginalValueInitial().setScale(2, RoundingMode.HALF_UP));
           req.setCico_add(originalInfo.getOriginalValueIncrease() == null ? BigDecimal.ZERO : originalInfo.getOriginalValueIncrease().setScale(2, RoundingMode.HALF_UP));
           req.setCico_des(originalInfo.getOriginalValueReduce() == null ? BigDecimal.ZERO : originalInfo.getOriginalValueReduce().setScale(2, RoundingMode.HALF_UP));
           req.setCico_contractno(originalInfo.getContractCode());
           req.setCico_contractname(originalInfo.getProjName());
           req.setCico_overduedate(originalInfo.getPlanCollectionDate() == null ? null : originalInfo.getPlanCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
           req.setCico_bussinessno((originalInfo.getCollectionCode() == null ? req.getCico_overduedate() + req.getOriginalValue() : originalInfo.getCollectionCode()) + "-" + originalInfo.getAccountAgeId());
           req.setCico_source(FinancialConstants.ZL);
           req.setCico_access_method(FinancialConstants.A);
           req.setBusinessKey(String.valueOf(originalInfo.getId()));
           req.setBusinessTitle(originalInfo.getCollectionCode());
           reqs.add(req);
       });
       return reqs;
   }

   /*private synchronized Long getBusinessId() {
        return Long.parseLong(System.currentTimeMillis() + String.valueOf(RandomUtil.randomLong(100000,999999)));
   }*/

    public void check(Long id) {
        FinanceAccountAgeBaseInfo originalInfo = financeAccountAgeBaseInfoMapper.selectById(id);
        if (ObjectUtil.isEmpty(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ObjectUtil.equals(originalInfo.getStatus(), FinancialAccountAgeRecordStatus.NEW.name())) {
            throw new MithrasException("帐龄已完成，不可变更");
        }
    }

}