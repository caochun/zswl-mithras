package cn.zswltech.mithras.margin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.margin.*;
import cn.zswltech.mithras.margin.convert.MarginConvert;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.overdue.application.collection.ContractDepositBalanceResolver;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.excel.exporter.MarginListExcelExporter;
import cn.zswltech.mithras.service.excel.model.MarginBaseInfoListExcelModel;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.margin.mapper.model.DepositCollectRefund;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2022-08-17
 **/
@Slf4j
@Service
public class MarginBaseInfoService extends ServiceImpl<MarginBaseInfoMapper, MarginBaseInfo> implements ContractDepositBalanceResolver {
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MarginListExcelExporter marginListExcelExporter;
    @Resource
    private ContractBaseInfoLibHandler baseInfoLibHandler;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MarginRecordService marginRecordService;

    public MarginBaseInfo getMarginBaseInfoByContractId(Long contractId) {
        LambdaQueryWrapper<MarginBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(MarginBaseInfo::getContractId, contractId);
        query.orderByDesc(MarginBaseInfo::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public long getMarginBalance(Long contractId) {
        LambdaQueryWrapper<MarginBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(MarginBaseInfo::getContractId, contractId);
        List<MarginBaseInfo> marginBaseInfoList = marginBaseInfoMapper.selectList(query);
        long total = 0;
        if (CollectionUtil.isNotEmpty(marginBaseInfoList)) {
            for (MarginBaseInfo marginBaseInfo : marginBaseInfoList) {
                if (Objects.nonNull(marginBaseInfo.getCollectionAmount())) {
                    total = total + marginBaseInfo.getCollectionAmount();
                }
            }
        }
        return total;
    }

    public Map<Long,Long> getMarginBalances(List<Long> contractIds) {
        LambdaQueryWrapper<MarginBaseInfo> query = Wrappers.lambdaQuery();
        query.in(MarginBaseInfo::getContractId, contractIds);
        List<MarginBaseInfo> marginBaseInfoList = marginBaseInfoMapper.selectList(query);
        Map<Long,Long> balancesMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(marginBaseInfoList)) {
            Map<Long, List<MarginBaseInfo>> cmMap = marginBaseInfoList.stream().collect(Collectors.groupingBy(MarginBaseInfo::getContractId));
            for (Long contractId : contractIds) {
                long total = 0;
                List<MarginBaseInfo> marginBaseInfos = cmMap.get(contractId);
                if (CollUtil.isNotEmpty(marginBaseInfos)) {
                    for (MarginBaseInfo marginBaseInfo : marginBaseInfos) {
                        if (Objects.nonNull(marginBaseInfo.getCollectionAmount())) {
                            total = total + marginBaseInfo.getCollectionAmount();
                        }
                    }
                }
                balancesMap.put(contractId,total);
            }
        }
        return balancesMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public String add(MarginBaseInfoAddREQ req) {
        MarginBaseInfo info = new MarginBaseInfo();
        BeanUtil.copyProperties(req, info);
        info.setMarginCode(getCode(req.getContractCode()));
        info.setCollectionAmount(0L);
        marginBaseInfoMapper.insert(info);
        return null;
    }

    private String getCode(String contractCode) {
        String year = contractCode.substring(contractCode.indexOf("【") + 1, contractCode.indexOf("】"));
        String code = contractCode.substring(contractCode.indexOf("(") + 1, contractCode.indexOf(")"));
        return year + code.substring(0, code.indexOf("-")) + code.substring(code.indexOf("-") + 1) + "-bzj";
    }


    public PageR<MarginBaseInfoListRSP> list(MarginBaseInfoListREQ req) {
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        req.setIsBizUser(isBizUser);
        req.setDeptIdList(canViewDeptIds);
        req.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        Page<MarginBaseInfo> pageList = marginBaseInfoMapper.pageList(new Page<>(req.getPage(), req.getPageSize()), req);
        List<MarginBaseInfoListRSP> rsps = getMarginBaseInfoListRSPS(pageList.getRecords());
        return PageR.of(rsps, pageList.getTotal(),
                pageList.getPages(),
                pageList.getCurrent(),
                pageList.getSize());
    }

    @NonNull
    private List<MarginBaseInfoListRSP> getMarginBaseInfoListRSPS(List<MarginBaseInfo> list) {
        List<Long> ids = list.stream().map(MarginBaseInfo::getClientId).distinct().collect(Collectors.toList());
        Map<Long, String> clientMap = id2NameService.clientId2Name(ids);
        List<MarginBaseInfoListRSP> rsps = new LinkedList<>();
        for (MarginBaseInfo o : list) {
            MarginBaseInfoListRSP tmp = new MarginBaseInfoListRSP();
            tmp.setClientName(clientMap.get(o.getClientId()));
            tmp.setCode(o.getMarginCode());
            tmp.setContractId(o.getContractId());
            tmp.setContractCode(o.getContractCode());
            tmp.setCollectionDate(o.getCollectionDate());
            tmp.setMarginAmount(o.getCollectionAmount());
            tmp.setBackAmount(o.getBackAmount());
            tmp.setDeductAmount(o.getDeductAmount());
            tmp.setCanBackAmount(o.getCollectionAmount());
            if (o.getContractIsSettle() == 1) {
                tmp.setCanBackAmount(o.getCollectionAmount());
            }
            tmp.setId(o.getId());
            rsps.add(tmp);
        }
        return rsps;
    }

    @SneakyThrows
    public void exportList(MarginBaseInfoListREQ req, ServletOutputStream outputStream) {
//        List<MarginBaseInfo> baseInfoList = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getId, req.getIds()));
//        List<MarginBaseInfoListRSP> rsps = getMarginBaseInfoListRSPS(baseInfoList);
        PageR<MarginBaseInfoListRSP> result = this.list(req);
        if (CollectionUtil.isEmpty(result.getList())) {
            throw new MithrasException("暂无导出数据");
        }
        List<MarginBaseInfoListExcelModel> excelModelList = result.getList().stream().map(MarginConvert::MarginBaseInfoListRSP2ExcelModel).collect(Collectors.toList());
        marginListExcelExporter.exportExcel(excelModelList, outputStream);
    }

    public MarginBaseInfoRSP detail(MarginBaseInfoDetailREQ req) {
        MarginBaseInfo info = marginBaseInfoMapper.selectById(req.getId());
        ContractBaseInfoLib detail = baseInfoLibHandler.queryLatestDataByOriginId(info.getContractId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(Collections.singleton(detail.getClientId()));
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(Collections.singleton(detail.getProjSponsorUserId()));
        Map<Long, String> deptMap = id2NameService.deptId2Name(Collections.singleton(detail.getBizDeptId()));
        MarginBaseInfoRSP rsp = new MarginBaseInfoRSP();
        MarginBaseInfoRSP.ContractInfo contractInfo = new MarginBaseInfoRSP.ContractInfo();
        contractInfo.setContractId(info.getContractId());
        contractInfo.setContractCode(detail.getContractCode());
        if (ProjectBizType.of(detail.getBizType()) == null) {
            contractInfo.setContractType(Optional.ofNullable(detail.getLeaseType()).map(LeaseType::of).map(LeaseType::display).orElse(""));
        } else if (LeaseType.of(detail.getLeaseType()) == null) {
            contractInfo.setContractType(Optional.ofNullable(detail.getBizType()).map(ProjectBizType::of).map(p -> p.display).orElse(""));
        } else {
            contractInfo.setContractType(Optional.ofNullable(detail.getBizType()).map(ProjectBizType::of).map(p -> p.display).orElse("") + "-" + Optional.ofNullable(detail.getLeaseType()).map(LeaseType::of).map(LeaseType::display).orElse(""));
        }
        contractInfo.setBizDept(deptMap.get(detail.getBizDeptId()));
        contractInfo.setClientName(clientMap.get(detail.getClientId()));
        contractInfo.setProjName(detail.getProjName());
        contractInfo.setProjSponsorUserName(sysUserMap.get(detail.getProjSponsorUserId()));
        contractInfo.setContractStatus(Objects.requireNonNull(ContractStatus.of(detail.getContractStatus())).display);
        rsp.setContractInfo(contractInfo);
        rsp.setMarginAmount(info.getCollectionAmount());
        rsp.setPlanMarginAmount(info.getPlanMarginAmount());
        rsp.setTotalReceivableAmount(info.getTotalReceivableAmount());
        rsp.setDeductAmount(info.getDeductAmount());
        rsp.setCollectionDate(info.getCollectionDate());
        rsp.setPlanMarginDate(info.getPlanMarginDate());
        rsp.setCanBackAmount(0L);
        //if (info.getContractIsSettle() == 1) {
            rsp.setCanBackAmount(info.getCollectionAmount());
        //}
        rsp.setBackAmount(info.getBackAmount());
        rsp.setMarginCode(info.getMarginCode());
        rsp.setNotReceivableAmount(LongUtil.null2zero(info.getTotalReceivableAmount()) - LongUtil.null2zero(rsp.getMarginAmount()) - LongUtil.null2zero(rsp.getBackAmount()) - LongUtil.null2zero(rsp.getDeductAmount()));
        return rsp;
    }

    public Map<Long, Long> getClientMarginBalances(Collection<Long> clientIdSet) {
        LambdaQueryWrapper<MarginBaseInfo> query = Wrappers.lambdaQuery();
        query.in(MarginBaseInfo::getClientId, clientIdSet);
        List<MarginBaseInfo> marginBaseInfoList = marginBaseInfoMapper.selectList(query);
        Map<Long, Long> balancesMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(marginBaseInfoList)) {
            balancesMap = marginBaseInfoList.stream().collect(Collectors.groupingBy(MarginBaseInfo::getClientId, Collectors.summingLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L))));
        }
        return balancesMap;

    }
    /**
     * 查询借据下保证金余额，部分退款按比例分配至借据
     **/
    public Map<Long, Long> getAmountByReceiptIds(Collection<Long> receiptIds, LocalDate actualDate) {
        if (CollectionUtil.isEmpty(receiptIds)) {
            return MapUtil.empty();
        }
        List<PaymentBaseInfo> paymentBaseInfosAll = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getReceiptIdFinal, receiptIds));
        //查询合同对应付款 <付款id, 合同id>
        Map<Long, Long> paymentId2ContractId = paymentBaseInfosAll.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getContractId, (a, b) -> a));
        //<付款id, 借据ID>
        Map<Long, Long> paymentId2ReceiptIdMap = paymentBaseInfosAll.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getReceiptIdFinal, (a, b) -> a));
        Map<Long, Set<Long>> contractId2ReceiptIds = new HashMap<>();
        Set<Long> longs;
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfosAll) {
            longs = contractId2ReceiptIds.get(paymentBaseInfo.getContractId());
            if (CollectionUtil.isEmpty(longs)) {
                longs = new HashSet<>();
                contractId2ReceiptIds.put(paymentBaseInfo.getContractId(), longs);
            }
            longs.add(paymentBaseInfo.getReceiptIdFinal());
        }
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, paymentId2ContractId.values())
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.EARNEST_MONEY.name())
                .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(), CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name())));
        if(CollectionUtil.isEmpty(collectionBaseInfos)){
            return MapUtil.empty();
        }
        //查询<收款id, 付款id>
        Map<Long, Long> collectionId2PaymentIdMap = collectionBaseInfos.stream().filter(e -> ObjectUtils.isNotEmpty(e.getPaymentId())).collect(Collectors.toMap(CollectionBaseInfo::getId, CollectionBaseInfo::getPaymentId, (a, b) -> a));
        Set<Long> collectionIds = collectionId2PaymentIdMap.keySet();
        if(CollectionUtil.isEmpty(collectionIds)){
            return MapUtil.empty();
        }
        //<借据ID， 最终保证金>
        Map<Long, Long> receiptId2AmountFinal = new HashMap<>();
        //<合同ID， 保证金余额>
        Map<Long, Long> contractIdMarginMap = new HashMap<>();
        //<借据ID， 收保证金>
        Map<Long, Long> receiptIdMarginMap = new HashMap<>();
        //<收款ID， 收款金额>
        Map<Long, Long> collectionIdMarginMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .in(CollectionRecordInfo::getCollectionId, collectionIds)
                .le(CollectionRecordInfo::getCollectionDate, actualDate)).stream().collect(Collectors.toMap(CollectionRecordInfo::getCollectionId,
                e -> LongUtil.null2zero(e.getCollectionAmount()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //计算保证金收款
        collectionIdMarginMap.forEach((collectionId, margin) -> {
            Long paymentId = collectionId2PaymentIdMap.get(collectionId);
            Long contractId = paymentId2ContractId.get(paymentId);
            Long receiptId = paymentId2ReceiptIdMap.get(paymentId);
            contractIdMarginMap.put(contractId, contractIdMarginMap.getOrDefault(contractId, 0L) + LongUtil.null2zero(margin));
            receiptIdMarginMap.put(receiptId, receiptIdMarginMap.getOrDefault(receiptId, 0L) + LongUtil.null2zero(margin));
        });
        Map<Long, Long> marginId2ContractIdMap = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery()
                .in(MarginBaseInfo::getContractId, paymentId2ContractId.values())).stream().collect(Collectors.toMap(MarginBaseInfo::getId, MarginBaseInfo::getContractId, (a, b) -> a));
        //<保证金ID, 保证金退款>
        Map<Long, Long> marginId2MarginMap = marginRecordService.list(Wrappers.<MarginRecordInfo>lambdaQuery()
                .in(MarginRecordInfo::getMarginId, marginId2ContractIdMap.keySet())
                .le(MarginRecordInfo::getCollectionDate, actualDate)
                .in(MarginRecordInfo::getRecordType, ListUtil.toList(RecordTypeEnum.REFUND.name(), RecordTypeEnum.REFUND_MARGIN.name(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.name()))).stream().collect(Collectors.toMap(MarginRecordInfo::getMarginId, MarginRecordInfo::getCollectionAmount,
                (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        marginId2MarginMap.forEach((marginId, amount) -> {
            Long contractId = marginId2ContractIdMap.get(marginId);
            contractIdMarginMap.put(contractId, contractIdMarginMap.getOrDefault(contractId, 0L) - LongUtil.null2zero(amount));
        });
        //按合同分配
        contractIdMarginMap.forEach((contractId, amount) -> {
            //拿到合同下借据金额
            Set<Long> receiptIdSet = contractId2ReceiptIds.get(contractId);
            Long marginAmount = contractIdMarginMap.get(contractId);
            long sum = 0L;
            long tmp = 0L;
            if (CollectionUtil.isNotEmpty(receiptIdSet)) {
                for (Long receiptId : receiptIdSet) {
                    sum += LongUtil.null2zero(receiptIdMarginMap.get(receiptId));
                }
                if (sum > 0) {
                    Iterator<Long> iterator = receiptIdSet.iterator();
                    while (iterator.hasNext()) {
                        Long receiptId = iterator.next();
                        if (iterator.hasNext()) {
                            long receiptMargin = new BigDecimal(marginAmount).multiply(new BigDecimal(LongUtil.null2zero(receiptIdMarginMap.get(receiptId)))).divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP).longValue();
                            receiptId2AmountFinal.put(receiptId, receiptMargin);
                            tmp += receiptMargin;
                        } else {
                            receiptId2AmountFinal.put(receiptId, marginAmount - tmp);
                        }
                    }
                }
            }
        });
        return receiptId2AmountFinal;
    }

    /**
     * 获取合同保证金余额
     * @param contractIds
     * @return
     */
    public Map<Long, Long> getDepositBalances(Set<Long> contractIds) {
        if(CollectionUtil.isEmpty(contractIds)){
            return MapUtil.empty();
        }
        List<DepositCollectRefund> margins = marginBaseInfoMapper.getMargins(contractIds);
        Map<String, Long> tmp = margins.stream().collect(Collectors.toMap(depositCollectRefund -> String.join("-", depositCollectRefund.getContractId().toString(), depositCollectRefund.getRecordType()), DepositCollectRefund::getAmount));
        Map<Long,Long> res = new HashMap<>();
        for(Long contractId : contractIds){
            Long collection = tmp.getOrDefault(String.join("-", contractId.toString(), "COLLECTION"),0L);
            Long refund = tmp.getOrDefault(String.join("-", contractId.toString(), "REFUND"),0L);
            res.put(contractId, collection - refund);
        }
        return res;
    }

    @Override
    public Map<Long, Long> depositBalances(Set<Long> contractIds) {
        return getDepositBalances(contractIds);
    }

    public Map<Long, List<MarginRecordInfo>> getContractMarginRecord(Set<Long> contractIds, List<String> recordTypes, LocalDate endTime){
        List<MarginBaseInfo> margins = this.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(contractIds), MarginBaseInfo::getContractId, contractIds));
        Map<Long, List<MarginRecordInfo>> marginRecordInfoMap = new HashMap<>();
        if (ObjectUtil.isEmpty(margins)) {
            return marginRecordInfoMap;
        }
        List<MarginRecordInfo> marginRecordInfos = marginRecordService.list(Wrappers.<MarginRecordInfo>lambdaQuery()
                .in(MarginRecordInfo::getMarginId, margins.stream().map(MarginBaseInfo::getId).collect(Collectors.toList()))
                .in(ObjectUtil.isNotEmpty(recordTypes), MarginRecordInfo::getRecordType, recordTypes)
                .le(ObjectUtil.isNotEmpty(endTime), MarginRecordInfo::getCollectionDate, endTime));
        if (ObjectUtil.isEmpty(marginRecordInfos)) {
            return marginRecordInfoMap;
        }
        Map<Long, List<MarginBaseInfo>> contractId2MarginBase = margins.stream().collect(Collectors.groupingBy(MarginBaseInfo::getContractId));
        Map<Long, List<MarginRecordInfo>>  marginBaseId2Records = marginRecordInfos.stream().collect(Collectors.groupingBy(MarginRecordInfo::getMarginId));
        contractId2MarginBase.forEach((contractId, mbs) -> {
            if (CollectionUtil.isNotEmpty(mbs)) {
                List<MarginRecordInfo> orDefault = marginRecordInfoMap.getOrDefault(contractId, new ArrayList<>());
                mbs.forEach( e -> {
                    List<MarginRecordInfo> marginRecordTemp = marginBaseId2Records.get(e.getId());
                    if (CollectionUtil.isNotEmpty(marginRecordTemp)) {
                        orDefault.addAll(marginRecordTemp);
                    }
                });
                marginRecordInfoMap.put(contractId, orDefault);
            }
        });
        return marginRecordInfoMap;
    }
}
