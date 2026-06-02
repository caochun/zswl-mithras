package cn.zswltech.mithras.service.service.liquidityrisk;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.contract.price.ContractAocPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractFactoringPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import cn.zswltech.mithras.service.convert.contract.ContractPriceConverter;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.excel.exporter.CashOutflowListExcelExporter;
import cn.zswltech.mithras.service.excel.model.AssetsCashOutflowListExcelModel;
import cn.zswltech.mithras.service.excel.model.FundsCashOutflowListExcelModel;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.dto.FundFinancingRepayActualDTO;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingRepayActualLibMapper;
import cn.zswltech.mithras.service.mapper.liquidityrisk.BaseAmountSettingMapper;
import cn.zswltech.mithras.service.mapper.liquidityrisk.FinancingDeliverDetailSettingMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.*;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.mapper.model.liquidityrisk.BaseAmountSetting;
import cn.zswltech.mithras.service.mapper.model.liquidityrisk.FinancingDeliverDetailSetting;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.contract.archive.application.ContractAocPriceLibService;
import cn.zswltech.mithras.contract.archive.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.archive.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl.FundFinancingBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2023-05-16
 **/

@Slf4j
@Service
public class CapitalOutflowService {

    @Resource
    private FundFinancingRepayActualLibMapper fundFinancingRepayActualLibMapper;
    @Resource
    private FundFinancingBaseInfoMapper fundFinancingBaseInfoMapper;
    @Resource
    private FundFinancingPlanLibMapper fundFinancingPlanLibMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private CashOutflowListExcelExporter cashOutflowListExcelExporter;
    @Resource
    private BaseAmountSettingMapper baseAmountSettingMapper;
    @Resource
    private FinancingDeliverDetailSettingMapper financingDeliverDetailSettingMapper;
    @Autowired
    private ContractLeasePriceLibService contractLeasePriceLibService;

    @Autowired
    private ContractAocPriceLibService contractAocPriceLibService;

    @Autowired
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private ContractPriceConverter priceConverter;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoLibHandler fundFinancingBaseInfoLibHandler;

    //只查询直融
    public FundsCashOutflowListRsp fundsCashOutflowList(CashOutflowListReq req){
        if (!req.getNeedPage()){
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
        }
        FundsCashOutflowListRsp res = new FundsCashOutflowListRsp();
        if (req.getTimeFrom() == null || req.getTimeTo() == null){
            res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return res;
        }
        //间融
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = fundFinancingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .in(FundFinancingBaseInfo::getFinancingStatus, ListUtil.of(FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name())));
        if (CollUtil.isEmpty(fundFinancingBaseInfos)){
            res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return res;
        }
        Map<Long, FundFinancingBaseInfo> baseInfoMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, o -> o));
        FundFinancingRepayActualDTO fundFinancingRepayActualDTO = new FundFinancingRepayActualDTO();
        fundFinancingRepayActualDTO.setFromTime(req.getTimeFrom());
        fundFinancingRepayActualDTO.setToTime(req.getTimeTo());
        Page<FundFinancingRepayActualLib> page = fundFinancingRepayActualLibMapper.stockPageList(new Page<>(req.getPage(), req.getPageSize()), fundFinancingRepayActualDTO);
        List<FundFinancingRepayActualLib> records = page.getRecords();
        Page<FundFinancingRepayActualLib> pageall = fundFinancingRepayActualLibMapper.stockPageList(new Page<>(1, Integer.MAX_VALUE), fundFinancingRepayActualDTO);
        List<FundFinancingRepayActualLib> allrecords = pageall.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            Set<Long> ids = allrecords.stream().map(FundFinancingRepayActual::getMainId).collect(Collectors.toSet());
            Map<Long, FundFinancingPlan> planMap = fundFinancingPlanLibMapper.queryLastestVersionLibs(ids).stream().collect(Collectors.toMap(FundFinancingPlan::getFinancingId, o -> o));
            List<FundsCashOutflowListRsp.Record> rsps = new ArrayList<>();
            FundsCashOutflowListRsp.Sum pagesum = new FundsCashOutflowListRsp.Sum();
            Set<Long> set = new HashSet<>();
            for (FundFinancingRepayActual record : records) {
                FundsCashOutflowListRsp.Record rsp = new FundsCashOutflowListRsp.Record();
                FundFinancingBaseInfo fundFinancingBaseInfo = baseInfoMap.get(record.getFinancingId());
                FundFinancingPlan fundFinancingPlan = planMap.get(record.getFinancingId());
//                rsp.setFinancingOrgIds(fundFinancingBaseInfo.getOrganizationIds());
//                rsp.setFinancingOrgs(JSON.parseArray(fundFinancingBaseInfo.getOrganizationNames(), String.class));
                rsp.setFinancingId(record.getFinancingId());
                rsp.setFinancingCode(fundFinancingBaseInfo.getFinancingCode());
                rsp.setFinancingAmount(fundFinancingPlan.getFinancingAmount());
                if (!set.contains(record.getFinancingId())){
                    pagesum.setFinancingAmount(LongUtil.null2zero(pagesum.getFinancingAmount())+LongUtil.null2zero(fundFinancingPlan.getFinancingAmount()));
                    set.add(record.getFinancingId());
                }
                rsp.setCashOutflowTime(record.getRepayDate());
                rsp.setPrinciple(record.getPrincipleAmount());
                pagesum.setPrinciple(LongUtil.null2zero(pagesum.getPrinciple())+LongUtil.null2zero(record.getPrincipleAmount()));
                rsp.setInterest(record.getInterestAmount());
                pagesum.setInterest(LongUtil.null2zero(pagesum.getInterest())+LongUtil.null2zero(record.getInterestAmount()));
                rsp.setEstimateCashOutflowAmount(LongUtil.null2zero(record.getPrincipleAmount())+LongUtil.null2zero(record.getInterestAmount()));
                pagesum.setEstimateCashOutflowAmount(LongUtil.null2zero(pagesum.getEstimateCashOutflowAmount())+LongUtil.null2zero(record.getPrincipleAmount())+LongUtil.null2zero(record.getInterestAmount()));
                rsps.add(rsp);
            }
            FundsCashOutflowListRsp.Sum sum = new FundsCashOutflowListRsp.Sum();
            Set<Long> allset = new HashSet<>();
            for (FundFinancingRepayActual record : allrecords) {
                FundFinancingPlan fundFinancingPlan = planMap.get(record.getFinancingId());
                if (!allset.contains(record.getFinancingId())){
                    sum.setFinancingAmount(LongUtil.null2zero(sum.getFinancingAmount())+LongUtil.null2zero(fundFinancingPlan.getFinancingAmount()));
                    allset.add(record.getFinancingId());
                }
                sum.setPrinciple(LongUtil.null2zero(sum.getPrinciple())+LongUtil.null2zero(record.getPrincipleAmount()));
                sum.setInterest(LongUtil.null2zero(sum.getInterest())+LongUtil.null2zero(record.getInterestAmount()));
                sum.setEstimateCashOutflowAmount(LongUtil.null2zero(sum.getEstimateCashOutflowAmount())+LongUtil.null2zero(record.getPrincipleAmount())+LongUtil.null2zero(record.getInterestAmount()));
            }
            res.setRecords(PageR.of(rsps, page.getTotal(),
                    page.getPages(),
                    page.getCurrent(),
                    page.getSize()));
            res.setPageSum(pagesum);
            res.setSum(sum);
        }else {
            res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
        }
        return res;
    }

    //直融+间融
    public FundsCashOutflowListRsp fundsCashOutflowList2(CashOutflowListReq req){
        if (!req.getNeedPage()){
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
        }
        FundsCashOutflowListRsp res = new FundsCashOutflowListRsp();
        if (req.getTimeFrom() == null || req.getTimeTo() == null){
            res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return res;
        }
        Page<FundReceiptRepayCashFlow> page = fundReceiptRepayCashFlowService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .between(FundReceiptRepayCashFlow::getRepayDate, req.getTimeFrom(), req.getTimeTo())
                .orderByAsc(FundReceiptRepayCashFlow::getRepayDate));
        if(ObjectUtil.isEmpty(page) || ObjectUtil.isEmpty(page.getRecords())){
            res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return res;
        }
        Page<FundReceiptRepayCashFlow> pageall = fundReceiptRepayCashFlowService.page(new Page<>(1, Integer.MAX_VALUE), Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .between(FundReceiptRepayCashFlow::getRepayDate, req.getTimeFrom(), req.getTimeTo()));

        List<FundReceiptRepayCashFlow> records = page.getRecords();
        List<FundReceiptRepayCashFlow> allrecords = pageall.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            List<FundsCashOutflowListRsp.Record> rsps = new ArrayList<>();
            FundsCashOutflowListRsp.Sum pagesum = new FundsCashOutflowListRsp.Sum();
            List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfos = fundReceiptRepayBaseInfoService.listByIds(allrecords.stream().map(FundReceiptRepayCashFlow::getReceiptRepayId).collect(Collectors.toSet()));
            Map<Long, FundReceiptRepayBaseInfo> baseMap = fundReceiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, e -> e, (a, b) -> a));
            // 查询融资机构名称
//            Map<Long, String> orgNames = fundOrganizationService.getNamesByIds(fundReceiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getFinancingOrgId).collect(Collectors.toSet()));
            //查询融资信息
            Set<Long> financingIds = fundReceiptRepayBaseInfos.stream().filter(base -> !ObjectUtil.equals(base.getFinancingType(), "DIRECT")).map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toSet());
            Set<Long> directIds = fundReceiptRepayBaseInfos.stream().filter(base -> ObjectUtil.equals(base.getFinancingType(), "DIRECT")).map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toSet());
            Map<Long, FundFinancingBaseInfo> financingId2bean = new HashMap<>() ;
            Map<Long, Long> financingAmounts = new HashMap<>();
            if(ObjectUtil.isNotEmpty(financingIds)){
                financingId2bean = fundFinancingBaseInfoMapper.selectBatchIds(financingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
                // 查询最新版本的融资信息
                financingAmounts = fundFinancingBaseInfoLibHandler.listLatestByOriginIds(financingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfoLib::getOriginId, FundFinancingBaseInfo::getFinancingAmount, (k1, k2) -> k1));
                List<FundFinancingBaseInfoLib> fundFinancingBaseInfoLibs = fundFinancingBaseInfoLibHandler.listLatestByOriginIds(financingIds);
                if(CollectionUtil.isNotEmpty(fundFinancingBaseInfoLibs)){
                    financingAmounts = fundFinancingBaseInfoLibs.stream().collect(Collectors.toMap(FundFinancingBaseInfoLib::getOriginId, FundFinancingBaseInfo::getFinancingAmount, (k1, k2) -> k1));
                }
            }
             //直融信息
            Map<Long, FundDirectFinancingBaseInfo> directId2bean = new HashMap<>();
            if(ObjectUtil.isNotEmpty(directIds)){
                directId2bean = fundDirectFinancingBaseInfoService.listByIds(directIds).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
            }
            Set<Long> set = new HashSet<>();
            FundReceiptRepayBaseInfo receiptRepayBaseInfo;
            FundFinancingBaseInfo financingBaseInfo;
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo;
            Map<Long, List<FundOrganization>> orgMap = fundOrganizationService.getBatchByFinancingId(records.stream().map(FundReceiptRepayCashFlow::getFinancingId).collect(Collectors.toList()));
            for (FundReceiptRepayCashFlow record : records) {
                List<FundOrganization> organizationList = orgMap.getOrDefault(record.getFinancingId(), Collections.emptyList());
                FundsCashOutflowListRsp.Record rsp = new FundsCashOutflowListRsp.Record();
                receiptRepayBaseInfo = baseMap.getOrDefault(record.getReceiptRepayId(), new FundReceiptRepayBaseInfo());
                rsp.setFinancingOrgIds(organizationList.stream().map(FundOrganization::getId).collect(Collectors.toList()));
                rsp.setFinancingType(receiptRepayBaseInfo.getFinancingType());
                if (CollectionUtil.isNotEmpty(organizationList)) {
                    rsp.setFinancingOrgs(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
                } else {
                    rsp.setFinancingOrgs(Collections.singletonList(receiptRepayBaseInfo.getFinancingChannel()));
                }
                rsp.setFinancingId(record.getFinancingId());
                if ("DIRECT".equals(receiptRepayBaseInfo.getFinancingType())) {
                    fundDirectFinancingBaseInfo = directId2bean.getOrDefault(record.getFinancingId(), new FundDirectFinancingBaseInfo());
                    rsp.setFinancingOrgs(Collections.singletonList(fundDirectFinancingBaseInfo.getProductName()));
                    rsp.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
                    rsp.setFinancingAmount(receiptRepayBaseInfo.getFinancingAmount());
                } else {
                    financingBaseInfo = financingId2bean.getOrDefault(record.getFinancingId(), new FundFinancingBaseInfo());
                    rsp.setFinancingCode(financingBaseInfo.getFinancingCode());
                    rsp.setFinancingAmount(financingAmounts.get(record.getFinancingId()));
                }
                if (!set.contains(record.getFinancingId())){
                    pagesum.setFinancingAmount(LongUtil.null2zero(pagesum.getFinancingAmount())+LongUtil.null2zero(rsp.getFinancingAmount()));
                    set.add(record.getFinancingId());
                }
                rsp.setCashOutflowTime(record.getRepayDate());
                rsp.setPrinciple(record.getPrincipleAmount());
                pagesum.setPrinciple(LongUtil.null2zero(pagesum.getPrinciple())+LongUtil.null2zero(record.getPrincipleAmount()));
                rsp.setInterest(record.getInterestAmount());
                pagesum.setInterest(LongUtil.null2zero(pagesum.getInterest())+LongUtil.null2zero(record.getInterestAmount()));
                rsp.setEstimateCashOutflowAmount(LongUtil.null2zero(record.getPrincipleAmount())+LongUtil.null2zero(record.getInterestAmount()));
                pagesum.setEstimateCashOutflowAmount(LongUtil.null2zero(pagesum.getEstimateCashOutflowAmount())+LongUtil.null2zero(record.getPrincipleAmount())+LongUtil.null2zero(record.getInterestAmount()));
                rsps.add(rsp);
            }
            FundsCashOutflowListRsp.Sum sum = new FundsCashOutflowListRsp.Sum();
            Set<Long> allset = new HashSet<>();
            for (FundReceiptRepayCashFlow record : allrecords) {
                receiptRepayBaseInfo = baseMap.getOrDefault(record.getReceiptRepayId(), new FundReceiptRepayBaseInfo());
                Long financingAmount;
                if ("DIRECT".equals(receiptRepayBaseInfo.getFinancingType())) {
                    receiptRepayBaseInfo = baseMap.getOrDefault(record.getReceiptRepayId(), new FundReceiptRepayBaseInfo());
                    financingAmount = receiptRepayBaseInfo.getFinancingAmount();
                } else {
                    financingAmount = financingAmounts.get(record.getFinancingId());
                }
                if (!allset.contains(record.getFinancingId())){
                    sum.setFinancingAmount(LongUtil.null2zero(sum.getFinancingAmount())+LongUtil.null2zero(financingAmount));
                    allset.add(record.getFinancingId());
                }
                sum.setPrinciple(LongUtil.null2zero(sum.getPrinciple())+LongUtil.null2zero(record.getPrincipleAmount()));
                sum.setInterest(LongUtil.null2zero(sum.getInterest())+LongUtil.null2zero(record.getInterestAmount()));
                sum.setEstimateCashOutflowAmount(LongUtil.null2zero(sum.getEstimateCashOutflowAmount())+LongUtil.null2zero(record.getPrincipleAmount())+LongUtil.null2zero(record.getInterestAmount()));
            }
            res.setRecords(PageR.of(rsps, page.getTotal(),
                    page.getPages(),
                    page.getCurrent(),
                    page.getSize()));
            res.setPageSum(pagesum);
            res.setSum(sum);
        }else {
            res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
        }
        return res;
    }

    public AssetsCashOutflowListRsp assetsCashOutflowList(CashOutflowListReq req){
        if (!req.getNeedPage()){
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
        }
        AssetsCashOutflowListRsp res = new AssetsCashOutflowListRsp();
        if (req.getTimeFrom() == null || req.getTimeTo() == null){
            res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return res;
        }
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getWriteOffStatus, ListUtil.of(PaymentWriteOffStatus.WRITTEN_OFF.name(),PaymentWriteOffStatus.PART_WRITTEN_OFF.name())));
        if (CollUtil.isNotEmpty(paymentBaseInfos)) {
            List<Long> contractIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).distinct().collect(Collectors.toList());
            List<Long> receiptIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getReceiptId).distinct().collect(Collectors.toList());
            Map<Long, List<PaymentBaseInfo>> cpMap = paymentBaseInfos.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getContractId));
            ContractLastDateDTO dto = new ContractLastDateDTO();
            dto.setContractIds(contractIds);
            dto.setReceiptIds(receiptIds);
            dto.setTimeFrom(req.getTimeFrom());
            dto.setTimeTo(req.getTimeTo());
            Page<ContractLastDate> page = contractBaseInfoMapper.ContractRentLastDate(new Page<>(req.getPage(), req.getPageSize()), dto);
            List<ContractLastDate> records = page.getRecords();
            Page<ContractLastDate> pageall = contractBaseInfoMapper.ContractRentLastDate(new Page<>(1, Integer.MAX_VALUE), dto);
            List<ContractLastDate> allrecords = pageall.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                List<Long> ids = records.stream().map(ContractLastDate::getContractId).collect(Collectors.toList());
                Set<Long> allids = allrecords.stream().map(ContractLastDate::getContractId).collect(Collectors.toSet());
                Map<Long, LocalDate> dateMap = records.stream().collect(Collectors.toMap(ContractLastDate::getContractId, ContractLastDate::getCashFlowDate));
                List<ContractBaseInfo> infos = contractBaseInfoMapper.selectBatchIds(ids);
                if (CollUtil.isNotEmpty(infos)) {
                    List<Long> reviewIds = infos.stream().map(ContractBaseInfo::getProjReviewId).distinct().collect(Collectors.toList());
                    Map<Long, ProjReviewBaseInfo> idMap = projReviewBaseInfoMapper.selectBatchIds(reviewIds).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, o -> o));
                    List<AssetsCashOutflowListRsp.Record> rsps = new ArrayList<>();
                    AssetsCashOutflowListRsp.Sum pagesum = new AssetsCashOutflowListRsp.Sum();
                    Map<Long, ContractPriceDetailRSP> priceMap = getPriceMap(allids);
                    for (ContractBaseInfo info : infos) {
                        AssetsCashOutflowListRsp.Record rsp = new AssetsCashOutflowListRsp.Record();
                        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(idMap.get(info.getProjReviewId()).getRelationDataType())) {
                            rsp.setProjId(info.getProjReviewId());
                            rsp.setDataType(ReviewRelationDataType.GROUP_CREDIT_REVIEW.name());
                        } else {
                            rsp.setProjId(idMap.get(info.getProjReviewId()).getProjEstablishId());
                            rsp.setDataType(ReviewRelationDataType.PROJ_ESTABLISH.name());
                        }
                        rsp.setProjName(info.getProjName());
                        rsp.setContractId(info.getId());
                        rsp.setContractCode(info.getContractCode());
                        ContractPriceDetailRSP price = priceMap.get(info.getId());
                        rsp.setContractAmount(price.getApplyCreditAmount());
                        pagesum.setContractAmount(LongUtil.null2zero(pagesum.getContractAmount()) + LongUtil.null2zero(rsp.getContractAmount()));
                        rsp.setCashOutflowTime(dateMap.get(info.getId()));
                        List<PaymentBaseInfo> paymentBaseInfos1 = cpMap.get(info.getId());
                        rsp.setEarnestMoney(0L);
                        if (CollUtil.isNotEmpty(paymentBaseInfos1)) {
                            rsp.setEarnestMoney(paymentBaseInfos1.stream().mapToLong(o -> LongUtil.null2zero(o.getEarnestMoney())).sum());
                        }
                        pagesum.setEarnestMoney(LongUtil.null2zero(pagesum.getEarnestMoney()) + LongUtil.null2zero(rsp.getEarnestMoney()));
                        rsp.setEstimateCashOutflowAmount(rsp.getEarnestMoney());
                        pagesum.setEstimateCashOutflowAmount(LongUtil.null2zero(pagesum.getEstimateCashOutflowAmount()) + LongUtil.null2zero(rsp.getEarnestMoney()));
                        rsps.add(rsp);
                    }
                    AssetsCashOutflowListRsp.Sum sum = new AssetsCashOutflowListRsp.Sum();
                    for (Long id : priceMap.keySet()) {
                        ContractPriceDetailRSP price = priceMap.get(id);
                        sum.setContractAmount(LongUtil.null2zero(sum.getContractAmount()) + LongUtil.null2zero(price.getApplyCreditAmount()));
                        List<PaymentBaseInfo> paymentBaseInfos1 = cpMap.get(id);
                        long sum1 = 0;
                        if (CollUtil.isNotEmpty(paymentBaseInfos1)) {
                            sum1 = paymentBaseInfos1.stream().mapToLong(o -> LongUtil.null2zero(o.getEarnestMoney())).sum();
                        }
                        sum.setEarnestMoney(LongUtil.null2zero(sum.getEarnestMoney()) + LongUtil.null2zero(sum1));
                        sum.setEstimateCashOutflowAmount(LongUtil.null2zero(sum.getEstimateCashOutflowAmount()) + LongUtil.null2zero(sum1));
                    }
                    // 返回
                    res.setRecords(PageR.of(rsps, page.getTotal(),
                            page.getPages(),
                            page.getCurrent(),
                            page.getSize()));
                    res.setPageSum(pagesum);
                    res.setSum(sum);
                }
            }else {
                res.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            }
        }
        return res;
    }

    private Map<Long,ContractPriceDetailRSP> getPriceMap(Set<Long> ids){
        Map<Long,ContractPriceDetailRSP> priceMap= new HashMap<>();
        Map<Long,ContractLeasePrice> leasePricesMap = new HashMap<>();
        Map<Long,ContractFactoringPrice> factoringPricesMap = new HashMap<>();
        Map<Long,ContractAocPrice> aocPricesMap = new HashMap<>();
        List<ContractLeasePriceLib> leasePrices = contractLeasePriceLibService.queryNewestLib(ids);
        if (CollUtil.isNotEmpty(leasePrices)){
            leasePricesMap = leasePrices.stream().collect(Collectors.toMap(ContractLeasePrice::getContractId,o -> o));
        }
        List<ContractFactoringPriceLib> factoringPrices = contractFactoringPriceLibService.queryNewestLib(ids);
        if (CollUtil.isNotEmpty(leasePrices)){
            factoringPricesMap = factoringPrices.stream().collect(Collectors.toMap(ContractFactoringPrice::getContractId,o -> o));
        }
        List<ContractAocPriceLib> aocPrices = contractAocPriceLibService.queryNewestLib(ids);
        if (CollUtil.isNotEmpty(leasePrices)){
            aocPricesMap = aocPrices.stream().collect(Collectors.toMap(ContractAocPrice::getContractId,o -> o));
        }
        for (Long id : ids) {
            ContractPriceDetailRSP res = new ContractPriceDetailRSP();
            // 处理租赁
            res.setLeasePriceModifyRSP(priceConverter.entityToLeaseRsp(leasePricesMap.get(id)));
            // 处理保理
            ContractFactoringPriceDetailRSP contractFactoringPriceDetailRSP = priceConverter.entityToFactoringRsp(factoringPricesMap.get(id));
            res.setFactoringPriceRSP(contractFactoringPriceDetailRSP);
            // 处理债权转让
            ContractAocPriceDetailRSP contractAocPriceDetailRSP = priceConverter.entityToAocRsp(aocPricesMap.get(id));
            res.setAocPriceRSP(contractAocPriceDetailRSP);
            priceMap.put(id,res);
        }
        return priceMap;
    }


    public ChartQueryRSP estimateCashOutflowList(CashOutflowListReq req){
        Map<LocalDate,Long> dateMap = new HashMap<>();
        BaseAmountSetting baseAmountSetting = baseAmountSettingMapper.selectOne(Wrappers.<BaseAmountSetting>query().last("limit 1"));
        if (baseAmountSetting == null){
            baseAmountSetting = new BaseAmountSetting();
        }

        Page<FundReceiptRepayCashFlow> pageall = fundReceiptRepayCashFlowService.page(new Page<>(1, Integer.MAX_VALUE), Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .between(FundReceiptRepayCashFlow::getRepayDate, req.getTimeFrom(), req.getTimeTo()));
        List<FundReceiptRepayCashFlow> fundList = pageall.getRecords();
        if (CollUtil.isNotEmpty(fundList)) {
            for (FundReceiptRepayCashFlow repayActual : fundList) {
                if (dateMap.containsKey(repayActual.getRepayDate())) {
                    dateMap.put(repayActual.getRepayDate(), LongUtil.null2zero(dateMap.get(repayActual.getRepayDate())) + LongUtil.null2zero(repayActual.getPrincipleAmount()) + LongUtil.null2zero(repayActual.getInterestAmount()));
                } else {
                    dateMap.put(repayActual.getRepayDate(), LongUtil.null2zero(repayActual.getPrincipleAmount()) + LongUtil.null2zero(repayActual.getInterestAmount()));
                }
            }
        }
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getWriteOffStatus, ListUtil.of(PaymentWriteOffStatus.WRITTEN_OFF.name(),PaymentWriteOffStatus.PART_WRITTEN_OFF.name())));
        List<Long> contractIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toList());
        List<Long> receiptIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getReceiptId).distinct().collect(Collectors.toList());
        Map<Long, List<PaymentBaseInfo>> cpMap = paymentBaseInfos.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getContractId));
        ContractLastDateDTO dto = new ContractLastDateDTO();
        dto.setContractIds(contractIds);
        dto.setReceiptIds(receiptIds);
        dto.setTimeFrom(req.getTimeFrom());
        dto.setTimeTo(req.getTimeTo());
        Page<ContractLastDate> page = contractBaseInfoMapper.ContractRentLastDate(new Page<>(1, Integer.MAX_VALUE), dto);
        List<ContractLastDate> records = page.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (ContractLastDate record : records) {
                List<PaymentBaseInfo> paymentBaseInfos1 = cpMap.get(record.getContractId());
                long sum1 = 0;
                if (CollUtil.isNotEmpty(paymentBaseInfos1)) {
                    sum1 = paymentBaseInfos1.stream().mapToLong(o -> LongUtil.null2zero(o.getEarnestMoney())).sum();
                }
                if (dateMap.containsKey(record.getCashFlowDate())) {
                    dateMap.put(record.getCashFlowDate(), LongUtil.null2zero(dateMap.get(record.getCashFlowDate())) + LongUtil.null2zero(sum1));
                } else {
                    dateMap.put(record.getCashFlowDate(), LongUtil.null2zero(sum1));
                }
            }
        }
        ChartQueryRSP rsp = new ChartQueryRSP();
        rsp.setChartType("line");
        rsp.setDataType("预估现金流流出");
        List<ChartQueryRSP.ChartQueryDetail> details = new ArrayList<>();
        LocalDate date = req.getTimeFrom().toLocalDate();
        Long preDayValue = LongUtil.null2zero(baseAmountSetting.getOtherExpenses());
        while ((date.equals(req.getTimeFrom().toLocalDate()) || date.isAfter(req.getTimeFrom().toLocalDate())) && (date.isBefore(req.getTimeTo().toLocalDate()) || date.equals(req.getTimeTo().toLocalDate()))){
            ChartQueryRSP.ChartQueryDetail detail = new ChartQueryRSP.ChartQueryDetail();
            detail.setName(date.toString());
            if (dateMap.get(date) != null) {
                detail.setValue(preDayValue+LongUtil.null2zero(dateMap.get(date)));
            }else {
                detail.setValue(preDayValue);
            }
            preDayValue = detail.getValue();
            details.add(detail);
            date = date.plusDays(1);
        }
        rsp.setDetails(details);
        return rsp;
    }

    public ChartQueryRSP stressTestingOutflowList(CashOutflowListReq req){
        ChartQueryRSP rsp = estimateCashOutflowList(req);
        List<FinancingDeliverDetailSetting> detailSettings = financingDeliverDetailSettingMapper.selectList(Wrappers.<FinancingDeliverDetailSetting>lambdaQuery()
                .ge(FinancingDeliverDetailSetting::getDate, req.getTimeFrom()).le(FinancingDeliverDetailSetting::getDate, req.getTimeTo())
                .eq(FinancingDeliverDetailSetting::getType, 1));
        if (CollUtil.isNotEmpty(detailSettings)){
            List<ChartQueryRSP.ChartQueryDetail> details = rsp.getDetails();
            Map<String, Long> map = details.stream().collect(Collectors.toMap(ChartQueryRSP.ChartQueryDetail::getName, ChartQueryRSP.ChartQueryDetail::getValue));
            for (FinancingDeliverDetailSetting detailSetting : detailSettings) {
                LocalDate date = detailSetting.getDate();
                while ((date.equals(req.getTimeFrom().toLocalDate()) || date.isAfter(req.getTimeFrom().toLocalDate())) && (date.isBefore(req.getTimeTo().toLocalDate()) || date.equals(req.getTimeTo().toLocalDate()))){
                    map.put(date.toString(), LongUtil.null2zero(map.get(date.toString())) +detailSetting.getAmount());
                    date = date.plusDays(1);
                }
            }
            details.forEach(o -> o.setValue(map.get(o.getName())));
            rsp.setDetails(details);
        }
        rsp.setDataType("压力测试-流出");
        return rsp;
    }

    public void cashOutflowExport(CashOutflowListReq req, ServletOutputStream outputStream){
        FundsCashOutflowListRsp fundsCashOutflowList = fundsCashOutflowList2(req);
        AssetsCashOutflowListRsp assetsCashOutflowList = assetsCashOutflowList(req);
        List<FundsCashOutflowListExcelModel> fundsExcelModelList = null;
        if (CollUtil.isNotEmpty(fundsCashOutflowList.getRecords().getList())) {
            fundsExcelModelList = fundsCashOutflowList.getRecords().getList().stream().map(o -> {
                FundsCashOutflowListExcelModel excelModel = new FundsCashOutflowListExcelModel();
                excelModel.setFinancingOrgs(o.getFinancingOrgs());
                excelModel.setFinancingCode(o.getFinancingCode());
                excelModel.setCashOutflowTime(Optional.ofNullable(o.getCashOutflowTime()).map(LocalDateTimeUtil::formatNormal).orElse(""));
                excelModel.setFinancingAmount(Optional.ofNullable(o.getFinancingAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
                excelModel.setPrinciple(Optional.ofNullable(o.getPrinciple()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
                excelModel.setInterest(Optional.ofNullable(o.getInterest()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
                excelModel.setEstimateCashOutflowAmount(Optional.ofNullable(o.getEstimateCashOutflowAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
                return excelModel;
            }).collect(Collectors.toList());
            FundsCashOutflowListExcelModel excelModel = new FundsCashOutflowListExcelModel();
            excelModel.setFinancingOrgs(Collections.singletonList("合计"));
            FundsCashOutflowListRsp.Sum pageSum = fundsCashOutflowList.getPageSum();
            excelModel.setFinancingCode("-");
            excelModel.setFinancingAmount(Optional.ofNullable(pageSum.getFinancingAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setCashOutflowTime("-");
            excelModel.setPrinciple(Optional.ofNullable(pageSum.getPrinciple()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setInterest(Optional.ofNullable(pageSum.getInterest()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setEstimateCashOutflowAmount(Optional.ofNullable(pageSum.getEstimateCashOutflowAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
            fundsExcelModelList.add(excelModel);
        }
        List<AssetsCashOutflowListExcelModel> assetsExcelModelList = null;
        if (CollUtil.isNotEmpty(assetsCashOutflowList.getRecords().getList())) {
            assetsExcelModelList = assetsCashOutflowList.getRecords().getList().stream().map(o -> {
                AssetsCashOutflowListExcelModel excelModel = new AssetsCashOutflowListExcelModel();
                excelModel.setProjName(o.getProjName());
                excelModel.setContractCode(o.getContractCode());
                excelModel.setCashOutflowTime(Optional.ofNullable(o.getCashOutflowTime()).map(LocalDateTimeUtil::formatNormal).orElse(""));
                excelModel.setEarnestMoney(Optional.ofNullable(o.getEarnestMoney()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
                excelModel.setContractAmount(Optional.ofNullable(o.getContractAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
                excelModel.setEstimateCashOutflowAmount(Optional.ofNullable(o.getEstimateCashOutflowAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
                return excelModel;
            }).collect(Collectors.toList());
            AssetsCashOutflowListExcelModel excelModel = new AssetsCashOutflowListExcelModel();
            AssetsCashOutflowListRsp.Sum pageSum = assetsCashOutflowList.getPageSum();
            excelModel.setProjName("合计");
            excelModel.setContractCode("-");
            excelModel.setCashOutflowTime("-");
            excelModel.setEarnestMoney(Optional.ofNullable(pageSum.getEarnestMoney()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setContractAmount(Optional.ofNullable(pageSum.getContractAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setEstimateCashOutflowAmount(Optional.ofNullable(pageSum.getEstimateCashOutflowAmount()).map(Util::mithrasLong2BigDecimalWY).map(BigDecimal::toPlainString).orElse("0"));
            assetsExcelModelList.add(excelModel);
        }
        cashOutflowListExcelExporter.exportExcel(fundsExcelModelList,assetsExcelModelList, outputStream);
    }
}
