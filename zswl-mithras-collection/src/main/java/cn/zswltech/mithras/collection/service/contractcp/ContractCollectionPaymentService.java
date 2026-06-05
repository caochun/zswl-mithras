package cn.zswltech.mithras.collection.service.contractcp;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.PageUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.contractcp.*;
import cn.zswltech.mithras.collection.convert.contractcp.ContractcpConvert;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contractcp.CashSelectTypeEnum;
import cn.zswltech.mithras.contract.enums.contractcp.RecordSourceEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.collection.excel.exporter.ContractcpCashDetailExcelExporter;
import cn.zswltech.mithras.collection.excel.exporter.ContractcpListExcelExporter;
import cn.zswltech.mithras.collection.excel.model.ContractcpCashDetailExcelModel;
import cn.zswltech.mithras.collection.excel.model.ContractcpListExcelModel;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2022-08-17
 **/

@Slf4j
@Service
public class ContractCollectionPaymentService {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractcpCashDetailExcelExporter contractcpCashDetailExcelExporter;
    @Resource
    private ContractcpListExcelExporter contractcpListExcelExporter;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoLibHandler baseInfoLibHandler;

    public PageR<ContractCollectionPaymentListRSP> list(ContractCollectionPaymentListREQ req) {
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        List<String> list = Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name());
        Page<ContractBaseInfo> pageData = contractBaseInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(req.getProjName() != null, ContractBaseInfo::getProjName, req.getProjName())
                        .eq(req.getContractStatus() != null, ContractBaseInfo::getContractStatus, req.getContractStatus())
                        .eq(req.getClientId() != null, ContractBaseInfo::getClientId, req.getClientId())
                        .like(req.getContractCode() != null, ContractBaseInfo::getContractCode, req.getContractCode())
                        .in(ContractBaseInfo::getContractStatus, list)
                        .and(isBizUser, e -> e.eq(ContractBaseInfo::getProjSponsorUserId, AccountUtil.getLoginInfo().getId())
                                .or().in(ContractBaseInfo::getBizDeptId, canViewDeptIds)
                                .or().apply(" json_contains(proj_cosponsor_user_ids, CONVERT ({0}, CHAR ))", AccountUtil.getLoginInfo().getId())
                        )
//                        .in(list1.size() > 0, ContractBaseInfo::getProjSponsorUserId, list1)
                        .orderByDesc(BaseModel::getCreateTime));
        List<ContractCollectionPaymentListRSP> rsps = packageContractCollectionPaymentListRSPList(pageData.getRecords());
        return PageR.of(rsps, pageData.getTotal(),
                pageData.getPages(),
                pageData.getCurrent(),
                pageData.getSize());
    }

    public ContractInfoRSP contractInfo(ContractcpContractDetailREQ req) {
        ContractBaseInfoLib o = baseInfoLibHandler.queryLatestDataByOriginId(req.getContractId());
//        ContractBaseInfo o = contractBaseInfoMapper.selectById(req.getContractId());
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(Collections.singleton(o.getProjSponsorUserId()));
        Map<Long, String> deptMap = id2NameService.deptId2Name(Collections.singleton(o.getBizDeptId()));
        ContractInfoRSP tmp = new ContractInfoRSP();
        tmp.setProjCode(o.getProjCode());
        tmp.setProjName(o.getProjName());
        tmp.setBizDept(deptMap.get(o.getBizDeptId()));
        tmp.setProjSponsorUser(sysUserMap.get(o.getProjSponsorUserId()));
        tmp.setContractStatus(Optional.ofNullable(o.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
        List<CollectionBaseInfo> baseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, o.getOriginId()));
        long receivedPrincipal = 0, principal = 0, receivedInterest = 0, interest = 0, overdueAmount = 0, overdueInterest = 0, receivedOverdueInterest = 0, otherAmount = 0, nominalLoanPrice = 0;
        for (CollectionBaseInfo info : baseInfos) {
            receivedPrincipal = receivedPrincipal + LongUtil.null2zero(info.getCollectionPrincipal());
            principal = principal + LongUtil.null2zero(info.getPrincipal());
            receivedInterest = receivedInterest + LongUtil.null2zero(info.getCollectionInterest());
            interest = interest + LongUtil.null2zero(info.getInterest());
            //计算规则还不明确，暂时不展示
//            if (info.getPlanCollectionDate().isBefore(LocalDate.now())){
//                List<CollectionRecordInfo> collectionRecordInfos = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery().eq(CollectionRecordInfo::getCollectionId, info.getId())
//                        .eq(CollectionRecordInfo::getWriteOffStatus, CollectionRecordWriteOffStatus.WRITTEN_OFF));
//                receivedOverdueInterest = receivedOverdueInterest + LongUtil.null2zero(info.getCollectionInterest());
//                overdueInterest = overdueInterest + (LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
//                overdueAmount = overdueAmount + (LongUtil.null2zero(info.getPrincipal()) - LongUtil.null2zero(info.getCollectionPrincipal()) + LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
//            }
            if (CashFlowItemEnum.OTHERAMOUNT.name().equals(info.getCashFlowItem())) {
                otherAmount = otherAmount + LongUtil.null2zero(info.getCollectionAmount());
            }
            if (CashFlowItemEnum.NOMINAL_PRICE.name().equals(info.getCashFlowItem())) {
                nominalLoanPrice = nominalLoanPrice + (LongUtil.null2zero(info.getCashFlowAmount()) - LongUtil.null2zero(info.getCollectionAmount()));
            }
        }
        tmp.setReceivedPrincipal(receivedPrincipal);
        tmp.setReceivedInterest(receivedInterest);
        tmp.setLastPrincipal(principal - receivedPrincipal);
        tmp.setLastInterest(interest - receivedInterest);
        tmp.setNominalLoanPrice(nominalLoanPrice);
        tmp.setOverdueAmount(overdueAmount);
        tmp.setOverdueInterest(overdueInterest);
        tmp.setReceivedOverdueInterest(receivedOverdueInterest);
        tmp.setUncollectedOverdueInterest(overdueInterest - receivedOverdueInterest);
        MarginBaseInfo info = marginBaseInfoMapper.selectOne(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, o.getOriginId()));
        if (info != null) {
            tmp.setLastMargin(info.getCollectionAmount());
        }
        tmp.setOtherSum(otherAmount);
        return tmp;
    }

    public List<CollectionBaseInfo> getCollectionBaseInfoList(ContractcpContractReceiptDetailREQ req) {
        List<CollectionBaseInfo> baseInfos = new ArrayList<>();
        ContractBaseInfoLib o = baseInfoLibHandler.queryLatestDataByOriginId(req.getContractId());
        if (o != null) {
            baseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, o.getOriginId())
                    .eq(StringUtils.isNotBlank(req.getReceiptCode()), CollectionBaseInfo::getReceiptCode, req.getReceiptCode()));
        }
        return baseInfos;
    }


    public ContractInfoReceiptRSP contractInfoByReceipt(ContractcpContractReceiptDetailREQ req) {
        ContractBaseInfoLib o = baseInfoLibHandler.queryLatestDataByOriginId(req.getContractId());
//      ContractBaseInfo o = contractBaseInfoMapper.selectById(req.getContractId());
        ContractInfoReceiptRSP tmp = new ContractInfoReceiptRSP();
        List<CollectionBaseInfo> baseInfos = new ArrayList<>();
        if (o != null) {
            Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(Collections.singleton(o.getProjSponsorUserId()));
            Map<Long, String> deptMap = id2NameService.deptId2Name(Collections.singleton(o.getBizDeptId()));
            tmp.setProjCode(o.getProjCode());
            tmp.setProjName(o.getProjName());
            tmp.setBizDept(deptMap.get(o.getBizDeptId()));
            tmp.setProjSponsorUser(sysUserMap.get(o.getProjSponsorUserId()));
            tmp.setContractStatus(Optional.ofNullable(o.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
            baseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, o.getOriginId())
                    .eq(StringUtils.isNotBlank(req.getReceiptCode()), CollectionBaseInfo::getReceiptCode, req.getReceiptCode()));
        }
        long receivedPrincipal = 0, principal = 0, receivedInterest = 0, interest = 0, overdueAmount = 0, overdueInterest = 0, receivedOverdueInterest = 0, otherAmount = 0, nominalLoanPrice = 0;
        for (CollectionBaseInfo info : baseInfos) {
            receivedPrincipal = receivedPrincipal + LongUtil.null2zero(info.getCollectionPrincipal());
            principal = principal + LongUtil.null2zero(info.getPrincipal());
            receivedInterest = receivedInterest + LongUtil.null2zero(info.getCollectionInterest());
            interest = interest + LongUtil.null2zero(info.getInterest());
            //计算规则还不明确，暂时不展示
//            if (info.getPlanCollectionDate().isBefore(LocalDate.now())){
//                List<CollectionRecordInfo> collectionRecordInfos = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery().eq(CollectionRecordInfo::getCollectionId, info.getId())
//                        .eq(CollectionRecordInfo::getWriteOffStatus, CollectionRecordWriteOffStatus.WRITTEN_OFF));
//                receivedOverdueInterest = receivedOverdueInterest + LongUtil.null2zero(info.getCollectionInterest());
//                overdueInterest = overdueInterest + (LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
//                overdueAmount = overdueAmount + (LongUtil.null2zero(info.getPrincipal()) - LongUtil.null2zero(info.getCollectionPrincipal()) + LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
//            }
            if (CashFlowItemEnum.OTHERAMOUNT.name().equals(info.getCashFlowItem())) {
                otherAmount = otherAmount + LongUtil.null2zero(info.getCollectionAmount());
            }
            if (CashFlowItemEnum.NOMINAL_PRICE.name().equals(info.getCashFlowItem())) {
                nominalLoanPrice = nominalLoanPrice + (LongUtil.null2zero(info.getCashFlowAmount()) - LongUtil.null2zero(info.getCollectionAmount()));
            }
        }
        tmp.setReceivedPrincipal(receivedPrincipal);
        tmp.setReceivedInterest(receivedInterest);
        tmp.setLastPrincipal(principal - receivedPrincipal);
        tmp.setLastInterest(interest - receivedInterest);
        tmp.setNominalLoanPrice(nominalLoanPrice);
        tmp.setOverdueAmount(overdueAmount);
        tmp.setOverdueInterest(overdueInterest);
        tmp.setReceivedOverdueInterest(receivedOverdueInterest);
        tmp.setUncollectedOverdueInterest(overdueInterest - receivedOverdueInterest);
        MarginBaseInfo info = null;
        if (o != null) {
            info = marginBaseInfoMapper.selectOne(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, o.getOriginId()));
        }
        if (info != null) {
            tmp.setLastMargin(info.getCollectionAmount());
        }
        tmp.setOtherSum(otherAmount);
        return tmp;
    }

    public PageR<ContractRentActualInfoRSP> cashDetail(ContractCollectionPaymentDetailREQ req) {
        List<PaymentBaseInfo> paymentBaseInfos = null;
        List<CollectionBaseInfo> collectionBaseInfos = null;
        List<MarginBaseInfo> marginBaseInfos = null;
        if (req.getCashtype() == null || CashSelectTypeEnum.ALL.name().equals(req.getCashtype())) {
            paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getContractId, req.getContractId()).eq(PaymentBaseInfo::getPaymentStatus, RecordStatus.TAKE_EFFECT.name()));
            collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, req.getContractId()));
            marginBaseInfos = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, req.getContractId()));
        } else if (CashSelectTypeEnum.EARNEST_MONEY.name().equals(req.getCashtype())) {
            marginBaseInfos = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, req.getContractId()));
        } else if (CashSelectTypeEnum.FIRST_RENT.name().equals(req.getCashtype()) || CashSelectTypeEnum.OTHERAMOUNT.name().equals(req.getCashtype()) || CashSelectTypeEnum.NOMINAL_PRICE.name().equals(req.getCashtype()) || CashSelectTypeEnum.EARLY_STOP_COMPENSATION.name().equals(req.getCashtype())) {
            collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, req.getContractId())
                    .eq(CollectionBaseInfo::getCashFlowItem, req.getCashtype()));
        } else {
            paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getContractId, req.getContractId())
                    .likeRight(PaymentBaseInfo::getReceiptCode, req.getCashtype()).in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name())));
            collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, req.getContractId())
                    .likeRight(CollectionBaseInfo::getCode, req.getCashtype()));
        }
        List<ContractRentActualInfoRSP> all = new LinkedList<>();
        if (paymentBaseInfos != null) {
            for (PaymentBaseInfo baseInfo : paymentBaseInfos) {
                ContractRentActualInfoRSP rsp = paymentBaseInfo2ContractRentActualInfoRSP(baseInfo);
                rsp.setRecordSource(RecordSourceEnum.PAYMENT.name());
                all.add(rsp);
            }
        }
        if (collectionBaseInfos != null) {
            for (CollectionBaseInfo baseInfo : collectionBaseInfos) {
                ContractRentActualInfoRSP rsp = collectionBaseInfo2ContractRentActualInfoRSP(baseInfo);
                rsp.setRecordSource(RecordSourceEnum.COLLECTION.name());
                all.add(rsp);
            }
        }
        if (marginBaseInfos != null) {
            for (MarginBaseInfo baseInfo : marginBaseInfos) {
                ContractRentActualInfoRSP rsp = marginBaseInfo2ContractRentActualInfoRSP(baseInfo);
                rsp.setRecordSource(RecordSourceEnum.MARGIN.name());
                all.add(rsp);
            }
        }
        List<ContractRentActualInfoRSP> sortAll = all.stream().sorted(Comparator.comparing(o -> Optional.ofNullable(o.getRentDate()).map(LocalDateTimeUtil::toEpochMilli).orElse(Long.MAX_VALUE))).collect(Collectors.toList());
        if (all.size() > req.getPageSize()) {
            int toIndex = req.getPageSize() * req.getPage();
            if (toIndex > all.size()) {
                toIndex = all.size();
            }
            sortAll = sortAll.subList(req.getPageSize() * (req.getPage() - 1), toIndex);
        }

        return PageR.of(sortAll, all.size(),
                PageUtil.totalPage(all.size(), req.getPageSize()),
                req.getPage(),
                req.getPageSize());
    }

    @SneakyThrows
    public void exportList(ContractCollectionPaymentListREQ req, ServletOutputStream outputStream) {
//        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getId, req.getExportIdList()).orderByDesc(ContractBaseInfo::getCreateTime));
//        List<ContractCollectionPaymentListRSP> rsps = packageContractCollectionPaymentListRSPList(contractBaseInfoList);
        PageR<ContractCollectionPaymentListRSP> result = this.list(req);
        if (CollectionUtil.isEmpty(result.getList())) {
            throw new MithrasException("暂无导出数据");
        }
        List<ContractcpListExcelModel> excelModelList = result.getList().stream().map(ContractcpConvert::contractCollectionPaymentListRSP2ExcelModel).collect(Collectors.toList());
        contractcpListExcelExporter.exportExcel(excelModelList, outputStream);
    }

    @SneakyThrows
    public void exportCashDetail(ContractCollectionPaymentDetailExportREQ req, ServletOutputStream outputStream) {
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getPaymentCode, req.getExportRentCodeList()));
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getCode, req.getExportRentCodeList()));
        List<MarginBaseInfo> marginBaseInfos = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getMarginCode, req.getExportRentCodeList()));
        List<ContractRentActualInfoRSP> rsps = new ArrayList<>();
        rsps.addAll(paymentBaseInfos.stream().map(this::paymentBaseInfo2ContractRentActualInfoRSP).collect(Collectors.toList()));
        rsps.addAll(collectionBaseInfos.stream().map(this::collectionBaseInfo2ContractRentActualInfoRSP).collect(Collectors.toList()));
        rsps.addAll(marginBaseInfos.stream().map(this::marginBaseInfo2ContractRentActualInfoRSP).collect(Collectors.toList()));
        rsps = rsps.stream().sorted(Comparator.comparing(o -> Optional.ofNullable(o.getRentDate()).map(LocalDateTimeUtil::toEpochMilli).orElse(Long.MAX_VALUE))).collect(Collectors.toList());
        List<ContractcpCashDetailExcelModel> excelModelList = rsps.stream().map(ContractcpConvert::contractRentActualInfoRSP2ExcelModel).collect(Collectors.toList());
        contractcpCashDetailExcelExporter.exportExcel(excelModelList, outputStream);
    }

    private ContractRentActualInfoRSP paymentBaseInfo2ContractRentActualInfoRSP(PaymentBaseInfo baseInfo) {
        ContractRentActualInfoRSP rsp = new ContractRentActualInfoRSP();
        rsp.setCashItem(baseInfo.getPayables());
        rsp.setCashFlowAmount(baseInfo.getApplyPaymentAmount());
        rsp.setPhase(0);
        rsp.setRentDate(LocalDateTimeUtil.ofDate(baseInfo.getApplyPaymentDate()));
        rsp.setRentCode(baseInfo.getPaymentCode());
        rsp.setRecordId(baseInfo.getId());
        rsp.setWriteOffStatus(PaymentWriteOffStatus.valueOf(baseInfo.getWriteOffStatus()).display);
        return rsp;
    }

    private ContractRentActualInfoRSP collectionBaseInfo2ContractRentActualInfoRSP(CollectionBaseInfo baseInfo) {
        ContractRentActualInfoRSP rsp = new ContractRentActualInfoRSP();
        rsp.setCashItem(Optional.ofNullable(baseInfo.getCashFlowItem()).map(CashFlowItemEnum::of).map(CashFlowItemEnum::display).orElse(null));
        rsp.setCashFlowAmount(baseInfo.getCashFlowAmount());
        rsp.setPhase(baseInfo.getPhase());
        rsp.setRentDate(baseInfo.getPlanCollectionDate());
        rsp.setRentCode(baseInfo.getCode());
        rsp.setRecordId(baseInfo.getId());
        rsp.setWriteOffStatus(Optional.ofNullable(baseInfo.getWriteOffStatus()).map(CollectionWriteOffStatusEnum::of).map(CollectionWriteOffStatusEnum::display).orElse(null));
        rsp.setPrincipal(baseInfo.getPrincipal());
        rsp.setInterest(baseInfo.getInterest());
        rsp.setPenaltyInterest(baseInfo.getPenaltyInterest());
        return rsp;
    }

    private ContractRentActualInfoRSP marginBaseInfo2ContractRentActualInfoRSP(MarginBaseInfo baseInfo) {
        ContractRentActualInfoRSP rsp = new ContractRentActualInfoRSP();
        rsp.setCashItem(CashFlowItemEnum.EARNEST_MONEY.getDisplay());
        rsp.setCashFlowAmount(baseInfo.getPlanMarginAmount());
        rsp.setPhase(0);
        rsp.setRentDate(baseInfo.getPlanMarginDate());
        rsp.setRentCode(baseInfo.getMarginCode());
        rsp.setRecordId(baseInfo.getId());
        ContractBaseInfoLib info = baseInfoLibHandler.queryLatestDataByOriginId(baseInfo.getContractId());
//        ContractBaseInfo info = contractBaseInfoMapper.selectById(baseInfo.getContractId());
        rsp.setWriteOffStatus(Optional.ofNullable(info.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
        return rsp;
    }

    private List<ContractCollectionPaymentListRSP> packageContractCollectionPaymentListRSPList(List<ContractBaseInfo> contractBaseInfoList) {
        List<ContractCollectionPaymentListRSP> rsps = new LinkedList<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        List<ContractBaseInfoLib> infos = new LinkedList<>();
        for (ContractBaseInfo record : contractBaseInfoList) {
            ContractBaseInfoLib detail = baseInfoLibHandler.queryLatestDataByOriginId(record.getId());
            if (Objects.nonNull(detail)) {
                infos.add(detail);
            }
        }
        for (ContractBaseInfoLib record : infos) {
            sysUserIds.add(record.getProjSponsorUserId());
            clientIds.add(record.getClientId());
            deptIds.add(record.getBizDeptId());
        }
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);

        for (ContractBaseInfoLib o : infos) {
            ContractCollectionPaymentListRSP tmp = new ContractCollectionPaymentListRSP();
            tmp.setId(o.getOriginId());
            tmp.setProjCode(o.getProjCode());
            tmp.setProjName(o.getProjName());
            tmp.setContractCode(o.getContractCode());
            if (ProjectBizType.of(o.getBizType()) == null) {
                tmp.setBizType(Optional.ofNullable(o.getLeaseType()).map(LeaseType::of).map(LeaseType::display).orElse(""));
            } else if (LeaseType.of(o.getLeaseType()) == null) {
                tmp.setBizType(Optional.ofNullable(o.getBizType()).map(ProjectBizType::of).map(p -> p.display).orElse(""));
            } else {
                tmp.setBizType(Optional.ofNullable(o.getBizType()).map(ProjectBizType::of).map(p -> p.display).orElse("") + "-" + Optional.ofNullable(o.getLeaseType()).map(LeaseType::of).map(LeaseType::display).orElse(""));
            }
            tmp.setContractStatus(Optional.ofNullable(o.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
            tmp.setClientName(clientMap.get(o.getClientId()));
            tmp.setBizDept(deptMap.get(o.getBizDeptId()));
            tmp.setProjSponsorUser(sysUserMap.get(o.getProjSponsorUserId()));
            List<CollectionBaseInfo> baseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, o.getOriginId()));
            long receivedPrincipal = 0, principal = 0, receivedInterest = 0, interest = 0, overdueAmount = 0, overdueInterest = 0, receivedOverdueInterest = 0, nominalLoanPrice = 0;
            for (CollectionBaseInfo info : baseInfos) {
                receivedPrincipal = receivedPrincipal + LongUtil.null2zero(info.getCollectionPrincipal());
                principal = principal + LongUtil.null2zero(info.getPrincipal());
                receivedInterest = receivedInterest + LongUtil.null2zero(info.getCollectionInterest());
                interest = interest + LongUtil.null2zero(info.getInterest());
                //计算规则还不明确，暂时不展示
//                if (info.getPlanCollectionDate().isBefore(LocalDate.now())){
//                    receivedOverdueInterest = receivedOverdueInterest + LongUtil.null2zero(info.getCollectionInterest());
//                    overdueInterest = overdueInterest + (LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
//                    overdueAmount = overdueAmount + (LongUtil.null2zero(info.getPrincipal()) - LongUtil.null2zero(info.getCollectionPrincipal()) + LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
//                }
                if (CashFlowItemEnum.NOMINAL_PRICE.name().equals(info.getCashFlowItem())) {
                    nominalLoanPrice = nominalLoanPrice + (LongUtil.null2zero(info.getCashFlowAmount()) - LongUtil.null2zero(info.getCollectionAmount()));
                }
            }
            tmp.setReceivedPrincipal(receivedPrincipal);
            tmp.setReceivedInterest(receivedInterest);
            tmp.setReceivedSum(receivedInterest + receivedPrincipal);
            tmp.setLastPrincipal(principal - receivedPrincipal);
            tmp.setLastInterest(interest - receivedInterest);
            tmp.setNominalLoanPrice(nominalLoanPrice);
            tmp.setLastSum(tmp.getLastInterest() + tmp.getLastPrincipal() + tmp.getNominalLoanPrice());
            tmp.setOverdueAmount(overdueAmount);
            tmp.setOverdueInterest(overdueInterest);
            tmp.setReceivedOverdueInterest(receivedOverdueInterest);
            tmp.setUncollectedOverdueInterest(overdueInterest - receivedOverdueInterest);
            rsps.add(tmp);
        }
        return rsps;
    }


    public Long collectionAmount(Long contractId, CashFlowItemEnum cashFlowItemEnum) {
        Long collection = 0L;
        switch (cashFlowItemEnum) {
            case OTHERAMOUNT:
            case FIRST_RENT:
            case NOMINAL_PRICE:
            case RENT: {
                List<CollectionBaseInfo> infos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, contractId)
                        .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItemEnum.name()));
                if (CollectionUtil.isEmpty(infos)) {
                    break;
                }
                for (CollectionBaseInfo info : infos) {
                    collection = collection + LongUtil.null2zero(info.getCollectionAmount());
                }
                break;
            }
            case EARNEST_MONEY: {
                MarginBaseInfo marginBaseInfo = marginBaseInfoMapper.selectOne(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, contractId));
                if (marginBaseInfo == null) {
                    break;
                }
                collection = marginBaseInfo.getCollectionAmount();
                break;
            }
            default:
                break;
        }
        return collection;
    }

    public Long collectionAmountByContractIds(List<Long> contractIds, CashFlowItemEnum cashFlowItemEnum) {
        Long collection = 0L;
        switch (cashFlowItemEnum) {
            case OTHERAMOUNT:
            case FIRST_RENT:
            case NOMINAL_PRICE: {
                List<CollectionBaseInfo> infos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getContractId, contractIds)
                        .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItemEnum.name()));
                if (CollectionUtil.isEmpty(infos)) {
                    break;
                }
                for (CollectionBaseInfo info : infos) {
                    collection = collection + LongUtil.null2zero(info.getCollectionAmount());
                }
                break;
            }
            case RENT: {
                List<CollectionBaseInfo> infos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getContractId, contractIds)
                        .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItemEnum.name()));
                if (CollectionUtil.isEmpty(infos)) {
                    break;
                }
                for (CollectionBaseInfo info : infos) {
                    collection = collection + LongUtil.null2zero(info.getCollectionPrincipal());
                }
                break;
            }
            case EARNEST_MONEY: {
                List<MarginBaseInfo> marginBaseInfos =
                        marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getContractId, contractIds));
                if (marginBaseInfos == null) {
                    break;
                }
                for (MarginBaseInfo base : marginBaseInfos) {
                    collection = collection + base.getCollectionAmount();
                }
                break;
            }
            default:
                break;
        }
        return collection;
    }
}
