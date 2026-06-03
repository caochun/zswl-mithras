package cn.zswltech.mithras.service.service.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.overdue.application.collection.ContractRemainingPrincipalResolver;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.excel.exporter.CollectionListExcelExporter;
import cn.zswltech.mithras.service.excel.model.CollectionListExcelModel;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.dto.CollectionNextRentParam;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.collection.service.bo.CollectionDetailChainBO;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.third.financial.FinancialManagerService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2022-08-20
 **/
@Slf4j
@Service
public class CollectionBaseInfoService extends ServiceImpl<CollectionBaseInfoMapper, CollectionBaseInfo> implements ContractRemainingPrincipalResolver {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CollectionListExcelExporter collectionListExcelExporter;
    @Resource
    private ContractBaseInfoLibHandler baseInfoLibHandler;
    @Lazy
    @Resource
    private FinancialManagerService financialManagerService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private ContractReceiptService contractReceiptService;

    public LocalDate findEarliestOverdueDate(Long receiptId) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getReceiptId, receiptId);
        query.gt(CollectionBaseInfo::getPhase, 0);
        query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        query.lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now());
        query.orderByAsc(CollectionBaseInfo::getPlanCollectionDate);
        List<CollectionBaseInfo> collectionBaseInfoList = this.list(query);
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return null;
        }
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            long planRent = Optional.ofNullable(collectionBaseInfo.getPlanCollectionAmount()).orElse(0L);
            long actualRent = Optional.ofNullable(collectionBaseInfo.getCollectionAmount()).orElse(0L);
            if (planRent > actualRent) {
                return collectionBaseInfo.getPlanCollectionDate();
            }
        }
        return null;
    }

    public List<CollectionBaseInfo> listRentByContractId(Long contractId) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getContractId, contractId);
        query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        query.gt(CollectionBaseInfo::getPhase, 0);
        return this.list(query);
    }

    //返回合同纬度数据
    public Map<Long, List<CollectionBaseInfo>> listRecordByContractIds(Set<Long> contractIds, LocalDate collectionDateFrom, LocalDate collectionDateTo, String cashFlowItemEnum) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return MapUtil.empty();
        }
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionBaseInfo::getContractId, contractIds);
        query.eq(CollectionBaseInfo::getCashFlowItem, cashFlowItemEnum);
        query.gt((CashFlowItemEnum.RENT.name().equals(cashFlowItemEnum)), CollectionBaseInfo::getPhase, 0);
        query.ge(ObjectUtil.isNotEmpty(collectionDateFrom), CollectionBaseInfo::getPlanCollectionDate, collectionDateFrom);
        query.le(ObjectUtil.isNotEmpty(collectionDateTo), CollectionBaseInfo::getPlanCollectionDate, collectionDateTo);
        List<CollectionBaseInfo> collectionBaseInfos = this.list(query);
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        //有实际取实际，没有取计划
        Map<Long, List<CollectionBaseInfo>> rspMap = new HashMap<>();
        Map<Long, List<CollectionBaseInfo>> contractId2CollectionBaseInfo = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        contractId2CollectionBaseInfo.forEach((contractId, baseInfos) -> {
            if (ObjectUtil.isNotEmpty(baseInfos)) {
                rspMap.put(contractId, baseInfos);
            }
        });
        return rspMap;
    }

    public List<CollectionBaseInfo> overdueListWithoutGracePeriod() {
        return this.getBaseMapper().overdueListWithoutGracePeriod();
    }

    public boolean isOverdueContract(Long contractId) {
        List<CollectionBaseInfo> collectionBaseInfoList = this.listBy(contractId, CashFlowItemEnum.RENT);
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return false;
        }
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (LocalDate.now().isAfter(collectionBaseInfo.getPlanCollectionDate().plusDays(3)) && !CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())) {
                return true;
            }
        }
        return false;
    }


    //租金表保存时需传入变更类型
    @Transactional(rollbackFor = Throwable.class)
    public void add(List<CollectionBaseInfoAddREQ> infos) {
        List<CollectionBaseInfo> reqs = new LinkedList<>();
        for (CollectionBaseInfoAddREQ o : infos) {
            CollectionBaseInfo info = new CollectionBaseInfo();
            BeanUtil.copyProperties(o, info);
            info.setWriteOffStatus(CollectionWriteOffStatusEnum.UNCOLLECTION.name());
            info.setAllRecordSort(0);
            info.setPenaltyInterestUpdate(0);
            //租金使用借据编号，其他任使用付款
            info.setCode(getCode(info.getCashFlowItem(), ObjectUtils.equals(info.getCashFlowItem(), CashFlowItemEnum.RENT.name()) ?
                    info.getReceiptCode() : info.getPaymentCode(), info.getPhase(), info.getContractCode()));
            o.setCollectionCode(info.getCode());
            reqs.add(info);
        }
        collectionBaseInfoMapper.insertList(reqs);
       /* //同步苍穹应收单
        financialManagerService.cqReceiveExec(reqs, processModelTypeEnum);*/
    }

    public Map<Long, LocalDate> getEndTimeByReceipt(Collection<Long> receiptIds) {
        Map<Long, LocalDate> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(receiptIds)) {
            map = collectionBaseInfoMapper.getEndTimeByReceipt(receiptIds).stream().collect(Collectors.toMap(CollectionBaseInfo::getReceiptId,
                    CollectionBaseInfo::getPlanCollectionDate, (a, b) -> a));
        }
        return map;

    }

    /**
     * 规则生成收款编号
     */
    public String getCode(String cashFlowItem, String paymentCode, Integer phase, String contractCode) {
        //合同编号+付款申请序号+xxx
        if (CashFlowItemEnum.RENT.name().equals(cashFlowItem)) {
            DecimalFormat format = new DecimalFormat("000");
            return paymentCode + "-" + format.format(phase);
        } else if (CashFlowItemEnum.OTHERAMOUNT.name().equals(cashFlowItem)) {
            return paymentCode + "-zf";
        } else if (CashFlowItemEnum.NOMINAL_PRICE.name().equals(cashFlowItem) || CashFlowItemEnum.EARLY_STOP_COMPENSATION.name().equals(cashFlowItem)) {
            String year = contractCode.substring(contractCode.indexOf("【") + 1, contractCode.indexOf("】"));
            String code = contractCode.substring(contractCode.indexOf("(") + 1, contractCode.indexOf(")"));
            String partone = year + code.substring(0, code.indexOf("-")) + code.substring(code.indexOf("-") + 1);
            Integer integer = collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractCode, contractCode)
                    .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem));
            DecimalFormat format = new DecimalFormat("00");
            return CashFlowItemEnum.NOMINAL_PRICE.name().equals(cashFlowItem) ? partone + "-jk" + "-" + format.format(integer + 1) : partone + "-bcj" + "-" + format.format(integer + 1);
        } else if (CashFlowItemEnum.FIRST_RENT.name().equals(cashFlowItem)) {
            return paymentCode + "-sf";
        } else if (CashFlowItemEnum.EARNEST_MONEY.name().equals(cashFlowItem)) {
            Integer integer = collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractCode, contractCode)
                    .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem));
            if (ObjectUtil.isNotEmpty(integer) && integer > 0) {
                DecimalFormat format = new DecimalFormat("00");
                return paymentCode + "-bzj" + "-" + format.format(integer);
            }
            return paymentCode + "-bzj";
        } else if (CashFlowItemEnum.RETENTION_MONEY.name().equals(cashFlowItem)) {
            return paymentCode + "-zbj";
        } else if (CashFlowItemEnum.COMMISSION.name().equals(cashFlowItem)) {
            return paymentCode + "-sxf";
        }  else if (CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name().equals(cashFlowItem)){
            return paymentCode + "-sqlx";
        }else {
            throw new MithrasException("无此现金流项目收款");
        }
    }

    public PageR<CollectionBaseInfoListRSP> list(CollectionBaseInfoREQ req) {
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        req.setIsBizUser(isBizUser);
        req.setDeptIdList(canViewDeptIds);
        req.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        Page<CollectionBaseInfo> pageList = collectionBaseInfoMapper.pageList(new Page<>(req.getPage(), req.getPageSize()), req);
        List<Long> ids = pageList.getRecords().stream().map(CollectionBaseInfo::getClientId).distinct().collect(Collectors.toList());
        Map<Long, String> clientMap = id2NameService.clientId2Name(ids);
        List<CollectionBaseInfoListRSP> rsps = new LinkedList<>();
        for (CollectionBaseInfo o : pageList.getRecords()) {
            CollectionBaseInfoListRSP tmp = new CollectionBaseInfoListRSP();
            tmp.setClientName(clientMap.get(o.getClientId()));
            tmp.setCode(o.getCode());
            tmp.setContractId(o.getContractId());
            tmp.setContractCode(o.getContractCode());
            tmp.setCollectionDate(o.getCollectionDate());
            tmp.setCollectionAmount(o.getCollectionAmount());
            tmp.setCashFlowItem(Optional.ofNullable(o.getCashFlowItem()).map(CashFlowItemEnum::of).map(CashFlowItemEnum::display).orElse(null));
            tmp.setClientName(clientMap.get(o.getClientId()));
            tmp.setPhase(o.getPhase());
            tmp.setPlanCollectionAmount(o.getPlanCollectionAmount());
            tmp.setPlanCollectionDate(o.getPlanCollectionDate());
            tmp.setWriteOffStatus(Optional.ofNullable(o.getWriteOffStatus()).map(CollectionWriteOffStatusEnum::of).map(CollectionWriteOffStatusEnum::display).orElse(null));
            tmp.setId(o.getId());
            rsps.add(tmp);
        }
        return PageR.of(rsps, pageList.getTotal(),
                pageList.getPages(),
                pageList.getCurrent(),
                pageList.getSize());
    }

    public CollectionBaseInfoRSP detail(CollectionBaseInfoDetailREQ req) {
        CollectionBaseInfo info = collectionBaseInfoMapper.selectById(req.getId());
        ContractBaseInfoLib detail = baseInfoLibHandler.queryLatestDataByOriginId(info.getContractId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(Collections.singleton(detail.getClientId()));
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(Collections.singleton(detail.getProjSponsorUserId()));
        Map<Long, String> deptMap = id2NameService.deptId2Name(Collections.singleton(detail.getBizDeptId()));
        CollectionBaseInfoRSP rsp = new CollectionBaseInfoRSP();
        CollectionBaseInfoRSP.ContractInfo contractInfo = new CollectionBaseInfoRSP.ContractInfo();
        contractInfo.setContractCode(detail.getContractCode());
        contractInfo.setContractId(info.getContractId());
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
        rsp.setPlanCollectionDate(info.getPlanCollectionDate());
        rsp.setPhase(info.getPhase());
        rsp.setCashFlowItem(CashFlowItemEnum.valueOf(info.getCashFlowItem()).getDisplay());
        rsp.setCollectionCode(info.getCode());
        rsp.setCashFlowAmount(info.getCashFlowAmount());
        rsp.setPrincipal(info.getPrincipal());
        rsp.setInterest(info.getInterest());
        rsp.setPenaltyInterest(info.getPenaltyInterest());
        rsp.setPenaltyInterestUpdate(info.getPenaltyInterestUpdate());
        rsp.setPenaltyInterestChange(0);
//        List<Long> writeOffUserIds = JSON.parseObject(info.getWriteOffUserIds(), new TypeReference<List<Long>>() {});
        // CollectionUtil.isNotEmpty(writeOffUserIds) ||
        if (CashFlowItemEnum.FIRST_RENT.name().equals(info.getCashFlowItem()) || CashFlowItemEnum.OTHERAMOUNT.name().equals(info.getCashFlowItem())
                || CashFlowItemEnum.NOMINAL_PRICE.name().equals(info.getCashFlowItem()) || CashFlowItemEnum.EARNEST_MONEY.name().equals(info.getCashFlowItem()) || CashFlowItemEnum.EARLY_STOP_COMPENSATION.name().equals(info.getCashFlowItem())) {
            rsp.setPenaltyInterestChange(1);
        }
        rsp.setWriteOffStatus(info.getWriteOffStatus());
        return rsp;
    }

    @SneakyThrows
    public void exportList(CollectionBaseInfoREQ req, ServletOutputStream outputStream) {
//        List<CollectionBaseInfo> baseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
//                .in(CollectionBaseInfo::getId, req.getExportIdList())
//                .orderByDesc(CollectionBaseInfo::getPlanCollectionDate)
//        );
//        List<Long> ids = baseInfoList.stream().map(CollectionBaseInfo::getClientId).distinct().collect(Collectors.toList());
//        Map<Long, String> clientMap = id2NameService.clientId2Name(ids);
//        List<CollectionListExcelModel> excelModelList = baseInfoList.stream().map(o -> {
//            CollectionListExcelModel excelModel = new CollectionListExcelModel();
//            excelModel.setClientName(clientMap.get(o.getClientId()));
//            excelModel.setCode(o.getCode());
//            excelModel.setContractCode(o.getContractCode());
//            excelModel.setCollectionDate(Optional.ofNullable(o.getCollectionDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
//            excelModel.setCollectionAmount(Optional.ofNullable(o.getCollectionAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
//            excelModel.setCashFlowItem(CashFlowItemEnum.valueOf(o.getCashFlowItem()).getDisplay());
//            excelModel.setClientName(clientMap.get(o.getClientId()));
//            excelModel.setPhase(o.getPhase());
//            excelModel.setPlanCollectionAmount(Optional.ofNullable(o.getPlanCollectionAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
//            excelModel.setPlanCollectionDate(Optional.ofNullable(o.getPlanCollectionDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
//            excelModel.setWriteOffStatus(CollectionWriteOffStatusEnum.valueOf(o.getWriteOffStatus()).display);
//            return excelModel;
//        }).collect(Collectors.toList());
        PageR<CollectionBaseInfoListRSP> result = this.list(req);
        if (CollectionUtil.isEmpty(result.getList())) {
            throw new MithrasException("暂无导出数据");
        }
        List<CollectionListExcelModel> excelModelList = result.getList().stream().map(e -> {
            CollectionListExcelModel excelModel = new CollectionListExcelModel();
            excelModel.setClientName(e.getClientName());
            excelModel.setCode(e.getCode());
            excelModel.setContractCode(e.getContractCode());
            excelModel.setCollectionDate(Optional.ofNullable(e.getCollectionDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            excelModel.setCollectionAmount(Optional.ofNullable(e.getCollectionAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setCashFlowItem(e.getCashFlowItem());
            excelModel.setPhase(e.getPhase());
            excelModel.setPlanCollectionAmount(Optional.ofNullable(e.getPlanCollectionAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setPlanCollectionDate(Optional.ofNullable(e.getPlanCollectionDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            excelModel.setWriteOffStatus(e.getWriteOffStatus());
            return excelModel;
        }).collect(Collectors.toList());
        collectionListExcelExporter.exportExcel(excelModelList, outputStream);
    }

    public boolean allCashFlowIsVerified(Long contractId) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getContractId, contractId);
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(query);
        if (CollectionUtils.isEmpty(collectionBaseInfoList)) {
            return true;
        }
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (!Objects.equals(collectionBaseInfo.getWriteOffStatus(), CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())) {
                return false;
            }
        }
        return true;
    }

    public List<CollectionBaseInfo> listByPaymentIds(Collection<Long> paymentIds) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionBaseInfo::getPaymentId, paymentIds);
        return this.list(query);
    }

    public List<CollectionBaseInfo> listRentByReceiptId(Long receiptId) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getReceiptId, receiptId);
        query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        return this.list(query);
    }

    public List<CollectionBaseInfo> listBy(Long contractId, CashFlowItemEnum cashFlowItemEnum) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getContractId, contractId);
        if (Objects.nonNull(cashFlowItemEnum)) {
            query.eq(CollectionBaseInfo::getCashFlowItem, cashFlowItemEnum.name());
        }
        return collectionBaseInfoMapper.selectList(query);
    }

    public int countUnwrittenRentPhase(Collection<Long> contractIds) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionBaseInfo::getContractId, contractIds);
        query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        query.ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        return this.count(query);
    }

    public List<CollectionBaseInfo> sumAmount(LocalDateTime startDate, LocalDateTime endDate) {
        return collectionBaseInfoMapper.sumAmount(startDate, endDate);
    }

    public List<CollectionBaseInfo> sumContractAmount(LocalDateTime startDate, LocalDateTime endDate) {
        return collectionBaseInfoMapper.sumContractAmount(startDate, endDate);
    }

    public List<CollectionBaseInfo> listByContractIds(List<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return new ArrayList<>();
        }
        return this.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIds)
        );
    }

    /**
     * 查询借据已收本金/其他收款
     *
     * @param receiptIds     借据id
     * @param collectionDate 收款日期
     * @return <借据id, 已收本金/已收金额>
     **/
    public Map<Long, Long> sumRemainingPrincipalByReceipt(Collection<Long> receiptIds, LocalDate collectionDate, String cashFlowItem) {
        if (CollectionUtil.isEmpty(receiptIds) || ObjectUtil.isEmpty(cashFlowItem)) {
            return MapUtil.empty();
        }
        if (ObjectUtil.isEmpty(collectionDate)) {
            collectionDate = LocalDate.now();
        }
        //查询付款信息
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getReceiptIdFinal, receiptIds));
        Map<Long, Long> paymentId2ReceiptId = paymentBaseInfos.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getReceiptIdFinal, (a, b) -> a));

        //查询借据下收款信息
        List<CollectionBaseInfo> collectionBaseInfos = baseMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem)
                .in(CashFlowItemEnum.RENT.name().equals(cashFlowItem), CollectionBaseInfo::getReceiptId, receiptIds)
                .in(!CashFlowItemEnum.RENT.name().equals(cashFlowItem), CollectionBaseInfo::getPaymentId, paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList())));
        if (CollectionUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> receiptIdPrincipalMap = new HashMap<>();
        //收款下规定时间内已收本金
        Map<Long, Long> collectionIdPrincipalMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .in(CollectionRecordInfo::getCollectionId, collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet()))
                .le(CollectionRecordInfo::getCollectionDate, collectionDate)).stream().collect(Collectors.toMap(CollectionRecordInfo::getCollectionId,
                e -> CashFlowItemEnum.RENT.name().equals(cashFlowItem) ? LongUtil.null2zero(e.getPrincipal()) : LongUtil.null2zero(e.getCollectionAmount()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        collectionBaseInfos.forEach(base -> {
            Long receiptId;
            Long planAmount;
            if (CashFlowItemEnum.RENT.name().equals(cashFlowItem)) {
                receiptId = base.getReceiptId();
                planAmount = LongUtil.null2zero(base.getPrincipal());
            } else {
                receiptId = paymentId2ReceiptId.get(base.getPaymentId());
                planAmount = LongUtil.null2zero(base.getCashFlowAmount());
            }
            //实收本金最大为计划收款金额
            receiptIdPrincipalMap.put(receiptId, receiptIdPrincipalMap.getOrDefault(receiptId, 0L) + Math.min(LongUtil.null2zero(collectionIdPrincipalMap.get(base.getId())), planAmount));
        });
        return receiptIdPrincipalMap;
    }


    /**
     * 查询借据已收本金/其他收款
     *
     * @param contractIds     合同id
     * @param collectionDate 收款截止日期
     * @return <合同id, 已收金额>  注意 这里租金只统计本金
     **/
    public Map<Long, Long> sumRemainingByContract(Collection<Long> contractIds, LocalDate collectionDate, String cashFlowItem) {
        if (CollectionUtil.isEmpty(contractIds) || ObjectUtil.isEmpty(cashFlowItem)) {
            return MapUtil.empty();
        }
        if (ObjectUtil.isEmpty(collectionDate)) {
            collectionDate = LocalDate.now();
        }

        //查询借据下收款信息
        List<CollectionBaseInfo> collectionBaseInfos = baseMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem)
                .in(CollectionUtil.isNotEmpty(contractIds), CollectionBaseInfo::getContractId, contractIds));
        if (CollectionUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> contractIdPrincipalMap = new HashMap<>();
        //收款下规定时间内已收本金
        Map<Long, Long> collectionIdPrincipalMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .in(CollectionRecordInfo::getCollectionId, collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet()))
                .le(ObjectUtil.isNotEmpty(collectionDate), CollectionRecordInfo::getCollectionDate, collectionDate)).stream().collect(Collectors.toMap(CollectionRecordInfo::getCollectionId,
                e -> CashFlowItemEnum.RENT.name().equals(cashFlowItem) ? LongUtil.null2zero(e.getPrincipal()) : LongUtil.null2zero(e.getCollectionAmount()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        collectionBaseInfos.forEach(base -> {
            Long planAmount;
            if (CashFlowItemEnum.RENT.name().equals(cashFlowItem)) {
                planAmount = LongUtil.null2zero(base.getPrincipal());
            } else {
                planAmount = LongUtil.null2zero(base.getCashFlowAmount());
            }
            //实收本金最大为计划收款金额
            contractIdPrincipalMap.put(base.getContractId(), contractIdPrincipalMap.getOrDefault(base.getContractId(), 0L) + Math.min(LongUtil.null2zero(collectionIdPrincipalMap.get(base.getId())), planAmount));
        });
        return contractIdPrincipalMap;
    }


    /**
     * 查询收款下剩余未收金额
     *
     * @param collectionIds  收款ID
     * @param collectionDate 截止日期
     * @return <收款ID, 剩余未收金额>
     **/
    public Map<Long, Long> sumRemainingPrincipalByCollection(Collection<Long> collectionIds, LocalDate collectionDate) {
        if (CollectionUtil.isEmpty(collectionIds)) {
            return MapUtil.empty();
        }
        if (ObjectUtil.isEmpty(collectionDate)) {
            collectionDate = LocalDate.now();
        }

        //查询收款信息
        List<CollectionBaseInfo> collectionBaseInfos = baseMapper.selectBatchIds(collectionIds);
        if (CollectionUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> collectionIdAmountMap = new HashMap<>();
        //收款下规定时间内已收款金额
        Map<Long, Long> collectionIdollectionMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .in(CollectionRecordInfo::getCollectionId, collectionIds)
                .le(CollectionRecordInfo::getCollectionDate, collectionDate)).stream().collect(Collectors.toMap(CollectionRecordInfo::getCollectionId,
                e -> LongUtil.null2zero(e.getCollectionAmount()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        collectionBaseInfos.forEach(base -> {
            //实收本金最大为计划收款金额
            collectionIdAmountMap.put(base.getId(), LongUtil.null2zero(base.getPlanCollectionAmount()) - Math.min(LongUtil.null2zero(collectionIdollectionMap.get(base.getId())), LongUtil.null2zero(base.getPlanCollectionAmount())));
        });
        return collectionIdAmountMap;
    }

    public void addOtherAmountByCommission(CollectionBaseInfoAddREQ collectionBaseInfoAddREQ) {
        CollectionBaseInfo info = new CollectionBaseInfo();
        BeanUtil.copyProperties(collectionBaseInfoAddREQ, info);
        info.setWriteOffStatus(CollectionWriteOffStatusEnum.UNCOLLECTION.name());
        info.setAllRecordSort(0);
        info.setPenaltyInterestUpdate(0);
        info.setCashFlowItem(CashFlowItemEnum.COMMISSION.name());
        // 租金使用借据编号，其他任使用付款
        info.setCode(getCode(info.getCashFlowItem(), info.getPaymentCode(), info.getPhase(), info.getContractCode()));
        collectionBaseInfoMapper.insert(info);
    }

    public void addOtherAmountByFirstInstallmentInterest(CollectionBaseInfoAddREQ collectionBaseInfoAddREQ) {
        CollectionBaseInfo info = new CollectionBaseInfo();
        BeanUtil.copyProperties(collectionBaseInfoAddREQ, info);
        info.setWriteOffStatus(CollectionWriteOffStatusEnum.UNCOLLECTION.name());
        info.setAllRecordSort(0);
        info.setPenaltyInterestUpdate(0);
        info.setCashFlowItem(CashFlowItemEnum.RENT.name());
        List<ContractReceipt> contractReceipts = contractReceiptService.lambdaQuery()
                .eq(ContractReceipt::getContractId, collectionBaseInfoAddREQ.getContractId())
                .orderByAsc(ContractReceipt::getSequence).list();
        ContractReceipt contractReceipt = org.springframework.util.ObjectUtils.isEmpty(contractReceipts) ? null : contractReceipts.get(0);
        String receiptCode = Objects.isNull(contractReceipt) ? null : Objects.isNull(contractReceipt.getReceiptCode()) ? null : contractReceipt.getReceiptCode();
        info.setReceiptCode(receiptCode);
        // 租金使用借据编号，其他任使用付款
        info.setCode(getCode(CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name(),  info.getPaymentCode(), info.getPhase(), info.getContractCode()));
        collectionBaseInfoMapper.insert(info);
    }

    /**
     * returns <contractId, remainingUnpaidPrincipal>
     * 获取合同下剩余本金
     **/
    public Map<Long, Long> sumRemainingUnpaidPrincipalByContract(List<Long> contractIds) {
        List<CollectionBaseInfo> collectionBaseInfos = this.listByContractIds(contractIds);
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, List<CollectionBaseInfo>> contractId2Collection = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        Map<Long, Long> rsps = new HashMap<>();
        contractId2Collection.forEach((contractId, collections) -> {
            rsps.put(contractId, collections.stream().map(e -> LongUtil.null2zero(e.getPrincipal()) - LongUtil.null2zero(e.getCollectionPrincipal())).reduce(0L, Math::addExact));
        });
        return rsps;
    }

    @Override
    public Map<Long, Long> remainingUnpaidPrincipalByContract(List<Long> contractIds) {
        return sumRemainingUnpaidPrincipalByContract(contractIds);
    }

    //获取下一期租金信息
    public CollectionBaseInfo getNextCollectionMessage(Long collectionId) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(collectionId);
        if (ObjectUtil.isEmpty(collectionBaseInfo)) {
            return null;
        }
        return collectionBaseInfoMapper.selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .gt(CollectionBaseInfo::getPhase, collectionBaseInfo.getPhase())
                .eq(CollectionBaseInfo::getContractId, collectionBaseInfo.getContractId())
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                .last(StringUtil.mysqlLimitOne()));
    }

    //获取每个合同最近一期租金收款记录
    public List<CollectionBaseInfo> contractNextRentList(CollectionNextRentParam req) {
       return collectionBaseInfoMapper.contractNextRentList(req);
    }


    /**
     * 获取借据的应计利息
     * @param receiptIdList
     * @return
     */
    public Map<Long, Long> getReceiptNextRent(Collection<Long> receiptIdList, LocalDate lastDate) {
        List<CollectionBaseInfo> receiptNextRent = collectionBaseInfoMapper.getReceiptNextRent(receiptIdList, lastDate);
        Map<Long, Long> receipt2NextRent = new HashMap<>();
        if (CollectionUtil.isEmpty(receiptNextRent)) {
            return receipt2NextRent;
        }
        receiptNextRent.forEach(e -> {
            receipt2NextRent.put(e.getReceiptId(), e.getPlanCollectionAmount());
        });
        return receipt2NextRent;
    }

    /*
     *  1.从逾期后还款的累计金额大于等于应还租金+罚息（逾期未还金额+罚息）的累计金额，其中如果罚息存在减免流程，视做0；
     *  2.逾期后条件一达成后的还款期间，起始日取逾期当期的逾期日，首先判断时间，其次判断正常还本付息。两个还款期间合计时长与6个月，取孰长，这期间正常还本付息；
     *  true 满足上迁条件，false/null不可上迁
     **/
    public Map<Long, Boolean> getContractPromotion(List<Long> clientIds, Integer interval) {
        Map<Long, Boolean> resultMap = new HashMap<>();
        //1.查询历史逾期客户
        List<CollectionBaseInfo> overdueCollectionBaseInfos = this.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .gt(CollectionBaseInfo::getPenaltyInterest, 0)
                .in(ObjectUtil.isNotEmpty(clientIds), CollectionBaseInfo::getClientId, clientIds)
                .or((sw -> sw.gt(CollectionBaseInfo::getPenaltyInterestDeductionAmount, 0))));
        if (ObjectUtil.isEmpty(overdueCollectionBaseInfos)) {
            return resultMap;
        }
        //查询逾期客户的所有现金流
        Set<Long> overdueContractIds = overdueCollectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        Map<Long, List<CollectionBaseInfo>> contractId2BaseInfo = overdueCollectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        List<CollectionBaseInfo> allCollection = this.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .in(CollectionBaseInfo::getContractId, overdueContractIds)
                .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now()));
        //构建逾期情况链
        Map<Long, CollectionDetailChainBO> collectionDetailChainBOMap = buildCollectionChain(allCollection);
        //拼接结果
        contractId2BaseInfo.forEach((contractId, overdueBaseInfo) -> {
            CollectionDetailChainBO bo = collectionDetailChainBOMap.get(contractId);
            overdueBaseInfo = overdueBaseInfo.stream().sorted(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate)).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(overdueBaseInfo)) {
                boolean result = true;
                //只关注最后一期
                CollectionBaseInfo c;
                for(int i = 0; i < overdueBaseInfo.size(); i++) {
                    c = overdueBaseInfo.get(i);
                    if (i == overdueBaseInfo.size() - 1) {
                        if (!isComplyWithNormal(bo, c, interval)) {
                            result = false;
                            break;
                        }
                    } else {
                        if (!isComplyWithNormal(bo, c, 0)) {
                            result = false;
                            break;
                        }
                    }
                    //最后一期要大于6个月
                }
                resultMap.put(contractId, result);
            }
        });
        return resultMap;
    }

    //判断某期逾期租金下是否满足逾期后X个月正常还款
    private boolean isComplyWithNormal(CollectionDetailChainBO chainBO, CollectionBaseInfo collectionBaseInfo, Integer interval) {
        if(ObjectUtil.isEmpty(collectionBaseInfo) || ObjectUtil.isEmpty(chainBO) || ObjectUtil.isEmpty(collectionBaseInfo.getPlanCollectionDate())) {
            //要素不全，不可上迁
            return false;
        }
        //获取改日期下的链
        CollectionDetailChainBO chainBOByPlanDate = getChainBOByPlanDate(chainBO, collectionBaseInfo.getPlanCollectionDate());
        if (ObjectUtil.isEmpty(chainBOByPlanDate) || ObjectUtil.isEmpty(chainBOByPlanDate.getNext())) {
            return false;
        }
        if (ObjectUtil.isEmpty(interval) && interval<= 0 && chainBOByPlanDate.isNormal()) {
            return true;
        }
        return decisionResult(chainBOByPlanDate.getNext(), collectionBaseInfo.getPlanCollectionDate().plusMonths(interval));
    }

    //判断是否可上迁
    private boolean decisionResult(CollectionDetailChainBO chainBO, LocalDate targetMinDate) {
        if (ObjectUtil.isEmpty(chainBO) || !chainBO.isNormal()) {
            return false;
        }
        //判断是否超过6个月
        if(ObjectUtil.isNotEmpty(targetMinDate) && chainBO.getPlanCollectionDate().isAfter(targetMinDate)) {
            return true;
        }
        return decisionResult(chainBO.getNext(), targetMinDate);
    }

    private CollectionDetailChainBO getChainBOByPlanDate(CollectionDetailChainBO chainBO, LocalDate targetDate) {
        if (ObjectUtil.isEmpty(chainBO) || targetDate.isBefore(chainBO.getPlanCollectionDate())) {
            //未找到
            return null;
        }
        if (targetDate.equals(chainBO.getPlanCollectionDate())) {
            return chainBO;
        } else {
            return getChainBOByPlanDate(chainBO.getNext(), targetDate);
        }
    }

    //构建循环链<ContractId, >
    private Map<Long, CollectionDetailChainBO> buildCollectionChain(List<CollectionBaseInfo> allCollection) {
        Map<Long, List<CollectionBaseInfo>> clientId2Collection = allCollection.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        Map<Long, CollectionDetailChainBO> collectionDetailChainBOMap = new HashMap<>();
        clientId2Collection.forEach((k, v) -> {
            if (ObjectUtil.isNotEmpty(v)) {
                //按照日期排序
                v = v.stream().sorted(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate)).collect(Collectors.toList());
                CollectionDetailChainBO collectionDetailChainByCollectionBaseInfo = getCollectionDetailChainByCollectionBaseInfo(v.get(0));
                getDetailChain(v, 1, collectionDetailChainByCollectionBaseInfo);
                collectionDetailChainBOMap.put(k, collectionDetailChainByCollectionBaseInfo);
            }
        });
        return collectionDetailChainBOMap;
    }

    //单个客户链
    private void getDetailChain(List<CollectionBaseInfo> collectionBaseInfos, int num, CollectionDetailChainBO detailChainBO) {
        if (num >= collectionBaseInfos.size() || ObjectUtil.isEmpty(detailChainBO)) {
            return;
        }
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfos.get(num);
        CollectionDetailChainBO collectionDetailChainByCollectionBaseInfo = getCollectionDetailChainByCollectionBaseInfo(collectionBaseInfo);
        detailChainBO.setNext(collectionDetailChainByCollectionBaseInfo);
        getDetailChain(collectionBaseInfos, num + 1, collectionDetailChainByCollectionBaseInfo);
    }

    private CollectionDetailChainBO getCollectionDetailChainByCollectionBaseInfo(CollectionBaseInfo collectionBaseInfo) {
        CollectionDetailChainBO bo = new CollectionDetailChainBO();
        bo.setPlanCollectionDate(collectionBaseInfo.getPlanCollectionDate());
        bo.setCollectionDate(collectionBaseInfo.getCollectionDate());
        bo.setOverdue(LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest()) > 0 || LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()) > 0);
        bo.setNormal((LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()) + LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest())) >= (LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) + LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest())));
        return bo;
    }


}
