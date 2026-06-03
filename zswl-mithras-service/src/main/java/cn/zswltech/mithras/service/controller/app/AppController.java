package cn.zswltech.mithras.service.controller.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.app.AppApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.dto.app.*;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualListRSP;
import cn.zswltech.mithras.dto.contractcp.*;
import cn.zswltech.mithras.dto.file.AppFileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessListREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.utils.CashFlowGenerationIrrREQ;
import cn.zswltech.mithras.service.controller.contract.ContractRentController;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.afterlease.domain.enums.RentCollectionIndexFilterConditionType;
import cn.zswltech.mithras.payment.domain.enums.app.AppPaymentStatus;
import cn.zswltech.mithras.service.enums.app.AppProjStageStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitDownloadTaskStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.PayType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitDownloadTaskRecordMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitDownloadTaskRecord;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.afterlese.impl.RentCollectionIndexServiceImpl;
import cn.zswltech.mithras.service.service.app.AppService;
import cn.zswltech.mithras.service.service.app.VisitDownloadTask;
import cn.zswltech.mithras.service.service.bo.CashFlowBO;
import cn.zswltech.mithras.service.service.bo.CashFlowCalculateBO;
import cn.zswltech.mithras.service.service.bo.CashFlowIRRBO;
import cn.zswltech.mithras.service.service.client.*;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.lib.contractcp.ContractCollectionPaymentService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishLeasePriceService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.util.DashboardHelpUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.bean.BeanUtil.copyToList;
import static cn.zswltech.mithras.contract.enums.contract.RepayRateEnum.of;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.util.FinancialUtil.calcCashFlow;
import static cn.zswltech.mithras.service.util.FinancialUtil.calculateIRR;

/**
 * @author luyi
 */
@Slf4j
@RestController
public class AppController implements AppApi {
    @Resource
    private AppService appService;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private MyTaskService myTaskService;
    @Resource
    private ContractCollectionPaymentService contractCollectionPaymentService;
    @Resource
    private ContractLeasePriceService leasePriceService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractRentController contractRentController;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishLeasePriceService establishLeasePriceService;
    @Resource
    private ProjEstablishPriceService establishPriceService;
    @Resource
    private ProjReviewPriceService reviewPriceService;
    @Resource
    private RentCollectionIndexServiceImpl rentCollectionIndexService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private FileService fileService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private Id2NameService id2NameService;

    private static final String RECORDS = "records";
    private static final String SUM_DATA = "sumData";



    @Override
    public R<Void> checkIn(AppCheckInREQ req) {
        appService.checkInUpload(req);
        return R.ok();
    }

    @Override
    public R<Void> reCheckIn(AppCheckInREQ req) {
        appService.checkInUpload(req);
        return R.ok();
    }

    @Override
    public R<PageR<AppVisitRecordRSP>> list(AppVisitRecordREQ req) {
        PageR<AppVisitRecordRSP> data = appService.list(req);
        return R.ok(data);
    }

    @Override
    public R<PageR<AppPCVisitRecordRSP>> pcList(AppPCVisitRecordREQ req) {
        PageR<AppPCVisitRecordRSP> data = appService.pcList(req);
        return R.ok(data);
    }

    @Override
    public R<Map<String,Object>> pcSummary(AppPCVisitSummaryREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<AppPCVisitSummaryRSP> data = appService.pcSummary(req);
        map.put(RECORDS, data);
        try {
            AppPCVisitSummaryRSP sumData = DashboardHelpUtil.countValueUnitDTO(data, new AppPCVisitSummaryRSP());
            sumData.setDeptName("合计值");
            map.put(SUM_DATA,sumData);
        } catch (Exception e) {
            log.warn("访客管理拜访汇总error ", e);
        }
        return R.ok(map);
    }


    @Override
    public R<List<ClientAppProjQueryRSP>> queryProjEstablish(ClientAppProjQueryREQ req) {
        return R.ok(clientService.queryProjEstablishDetail(req.getClientId()));
    }

    @Override
    public R<List<AppContractQueryRSP>> queryContract(AppContractQueryREQ req) {
        return R.ok(appService.listContract(req));
    }

    @Override
    public R<List<ClientAppPlanQueryRSP>> queryCheckPlan(ClientAppPlanQueryREQ req) {
        return R.ok(clientService.queryCheckPlanDetail(req.getClientId()));
    }

    @Override
    public R<Void> invalid(AppInvalidREQ req) {
        appService.invalidRecord(req);
        return R.ok();
    }

    @Override
    public R<AppRecordDetailRSP> detail(AppRecordDetailREQ req) {
        return R.ok(appService.detail(req));
    }

    @Override
    public R<PageR<AppClientListRSP>> list(AppClientListREQ req) {
        ClientListREQ listReq = BeanUtil.copyProperties(req, ClientListREQ.class);
        Page<Client> data = clientService.newPageList(listReq);
        List<Client> records = data.getRecords();
        List<ClientListRSP> list = BeanUtil.copyToList(records, ClientListRSP.class);
        clientService.fillOtherInfo(list, listReq.getShowApprovalFlag());
        List<AppClientListRSP> res = BeanUtil.copyToList(list, AppClientListRSP.class);
        appService.appFillOtherInfo(res);
        return R.ok(PageR.of(res, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<List<AppContractListRSP>> list(AppContractListREQ req) {
        return R.ok(appService.list(req));
    }

    @Override
    public R<AppClientDetailRSP> detail(AppClientDetailREQ req) {
        return R.ok(appService.detail(req));
    }

    @Override
    public R<List<AppClientFrequentRSP>> list(AppClientFrequentREQ req) {
        return R.ok(appService.frequentList(req));
    }

    @Override
    public R<Void> imageToPdf(AppClientPdfREQ req) {
        appService.imageToPdf(req);
        return R.ok();
    }

    @Override
    public R<List<AppProjListRSP>> list(AppProjListREQ req) {
        long startTime = System.currentTimeMillis();
        List<AppProjListRSP> res = new ArrayList<>();
        //授信立项
        GroupCreditEstablishListREQ groupCreditEstablishListREQ = new GroupCreditEstablishListREQ();
        groupCreditEstablishListREQ.setPage(1);
        groupCreditEstablishListREQ.setPageSize(10000);
        PageR<GroupCreditEstablishListRSP> groupCreditEstablishData = groupCreditEstablishBaseInfoService.list(groupCreditEstablishListREQ);
        List<GroupCreditEstablishListRSP> groupCreditEstablishListRSPList = new ArrayList<>();
        if (groupCreditEstablishData != null && !groupCreditEstablishData.getList().isEmpty()) {
            groupCreditEstablishListRSPList = groupCreditEstablishData.getList();
        }
        //授信评审
        GroupCreditReviewListREQ groupCreditReviewListREQ = new GroupCreditReviewListREQ();
        groupCreditReviewListREQ.setPage(1);
        groupCreditReviewListREQ.setPageSize(10000);
        PageR<GroupCreditReviewListRSP> groupCreditReviewData = groupCreditReviewBaseInfoService.list(groupCreditReviewListREQ);
        List<GroupCreditReviewListRSP> groupCreditReviewListRSPList = new ArrayList<>();
        Map<Long, List<GroupCreditReviewListRSP>> groupCreditReviewMap = new HashMap<>();
        if (groupCreditReviewData != null && !groupCreditReviewData.getList().isEmpty()) {
            groupCreditReviewListRSPList = groupCreditReviewData.getList();
            groupCreditReviewMap = groupCreditReviewListRSPList.stream().collect(Collectors.groupingBy(GroupCreditReviewListRSP::getGroupCreditEstablishId));
        }
        //只有集团授信立项
        List<GroupCreditEstablishListRSP> groupCreditEstablishStage = new ArrayList<>();
        for (GroupCreditEstablishListRSP groupCreditEstablishListRSP : groupCreditEstablishListRSPList) {
            if (!groupCreditReviewMap.containsKey(groupCreditEstablishListRSP.getId())) {
                groupCreditEstablishStage.add(groupCreditEstablishListRSP);
            }
        }
        if (!groupCreditEstablishStage.isEmpty()) {
            List<AppProjListRSP> list = appService.copyGroupCreditEstablish(groupCreditEstablishStage);
            res.addAll(list);
        }
        //项目立项
        ProjEstablishBaseInfoListREQ projEstablishBaseInfoListREQ = new ProjEstablishBaseInfoListREQ();
        projEstablishBaseInfoListREQ.setPage(1);
        projEstablishBaseInfoListREQ.setPageSize(10000);
        Page<ProjEstablishBaseInfoListRSP> projEstablishBaseData = projEstablishBaseInfoService.list(projEstablishBaseInfoListREQ);
        List<ProjEstablishBaseInfoListRSP> projEstablishBaseInfoListRSPList = new ArrayList<>();
        if (projEstablishBaseData != null && !projEstablishBaseData.getRecords().isEmpty()) {
            projEstablishBaseInfoListRSPList = projEstablishBaseData.getRecords();
        }
        //项目评审
        ProjReviewBaseInfoListREQ reviewBaseInfoListREQ = new ProjReviewBaseInfoListREQ();
        reviewBaseInfoListREQ.setPage(1);
        reviewBaseInfoListREQ.setPageSize(10000);
        Page<ProjReviewBaseInfoListRSP> projReviewBaseInfoData = reviewBaseInfoService.list(reviewBaseInfoListREQ);
        List<ProjReviewBaseInfoListRSP> projReviewBaseInfoListRSPList = new ArrayList<>();
        Map<Long, List<ProjReviewBaseInfoListRSP>> projReviewGroupBaseMap = new HashMap<>();
        Map<Long, List<ProjReviewBaseInfoListRSP>> projReviewBaseMap = new HashMap<>();
        if (projReviewBaseInfoData != null && !projReviewBaseInfoData.getRecords().isEmpty()) {
            projReviewBaseInfoListRSPList = projReviewBaseInfoData.getRecords();
            for (ProjReviewBaseInfoListRSP projReviewBaseInfoListRSP : projReviewBaseInfoListRSPList) {
                if (projReviewBaseInfoListRSP.getProjEstablishId() != null) {
                    projReviewBaseMap.putIfAbsent(projReviewBaseInfoListRSP.getProjEstablishId(), new ArrayList<>());
                    projReviewBaseMap.get(projReviewBaseInfoListRSP.getProjEstablishId()).add(projReviewBaseInfoListRSP);
                }
                if (projReviewBaseInfoListRSP.getGroupCreditReviewId() != null) {
                    projReviewGroupBaseMap.putIfAbsent(projReviewBaseInfoListRSP.getGroupCreditReviewId(), new ArrayList<>());
                    projReviewGroupBaseMap.get(projReviewBaseInfoListRSP.getGroupCreditReviewId()).add(projReviewBaseInfoListRSP);
                }
            }
        }
        //只有集团授信评审
        List<GroupCreditReviewListRSP> groupCreditReviewListStage = new ArrayList<>();
        for (GroupCreditReviewListRSP groupCreditReviewListRSP : groupCreditReviewListRSPList) {
            if (!projReviewGroupBaseMap.containsKey(groupCreditReviewListRSP.getId())) {
                groupCreditReviewListStage.add(groupCreditReviewListRSP);
            }
        }
        if (!groupCreditReviewListStage.isEmpty()) {
            List<AppProjListRSP> list = appService.copyGroupCreditReview(groupCreditReviewListStage);
            res.addAll(list);
        }
        //只有项目立项
        List<ProjEstablishBaseInfoListRSP> projEstablishBaseListStage = new ArrayList<>();
        for (ProjEstablishBaseInfoListRSP projEstablishBaseInfoListRSP : projEstablishBaseInfoListRSPList) {
            if (!projReviewBaseMap.containsKey(projEstablishBaseInfoListRSP.getId())) {
                projEstablishBaseListStage.add(projEstablishBaseInfoListRSP);
            }
        }
        if (!projEstablishBaseListStage.isEmpty()) {
            Set<Long> establishBaseInfoIds = projEstablishBaseListStage.stream().map(ProjEstablishBaseInfoListRSP::getId).collect(Collectors.toSet());
            List<ProjEstablishLeasePrice> projEstablishLeasePriceList = establishLeasePriceService.list(Wrappers.<ProjEstablishLeasePrice>lambdaQuery().in(ProjEstablishLeasePrice::getProjEstablishId, establishBaseInfoIds));
            List<AppProjListRSP> list = appService.copyProjEstablishBase(projEstablishBaseListStage, projEstablishLeasePriceList);
            res.addAll(list);
        }
        //评审
        if (!projReviewBaseInfoListRSPList.isEmpty()) {
            List<AppProjListRSP> list = appService.copyProjReviewBase(projReviewBaseInfoListRSPList);
            res.addAll(list);
        }

        if (StringUtils.isNotBlank(req.getProjName())) {
            res = res.stream().filter(x -> x.getProjName().contains(req.getProjName()))
                    .collect(Collectors.toList());
        }

        ProcessListREQ processListREQ = new ProcessListREQ();
        processListREQ.setPage(1);
        processListREQ.setPageSize(Integer.MAX_VALUE);
        PageR<ProcessListRSP> processListRSPPageR = myTaskService.searchList(processListREQ);
        if (processListRSPPageR != null && !processListRSPPageR.getList().isEmpty()) {
            List<ProcessListRSP> processListRSPPageRList = processListRSPPageR.getList().stream().filter(x -> StringUtils.isNotBlank(x.getProjCode()))
                    .collect(Collectors.toList());
            Map<String, List<ProcessListRSP>> map = processListRSPPageRList.stream().collect(Collectors.groupingBy(ProcessListRSP::getProjCode));
            map.keySet().forEach(key -> map.computeIfPresent(key, (k, v) -> v.stream().sorted(Comparator.comparing(ProcessListRSP::getStartTime).reversed()).collect(Collectors.toList())));
            for (AppProjListRSP appProjListRSP : res) {
                List<ProcessListRSP> list = map.getOrDefault(appProjListRSP.getProjCode(), new ArrayList<>());
                if (!list.isEmpty()) {
                    ProcessListRSP processListRSP = processListRSPPageR.getList().get(0);
                    appProjListRSP.setProcessStatus(processListRSP.getModelName());
                    if (StringUtils.isNotBlank(processListRSP.getContractCode())) {
                        appProjListRSP.setIsBlueColor(false);
                    } else {
                        appProjListRSP.setIsBlueColor(true);
                    }
                }
            }
        }
        res.sort(Comparator.comparing(AppProjListRSP::getCreateTime).reversed());
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("程序运行时间" + totalTime + "毫秒");
        return R.ok(res);
    }

    @Override
    public R<AppProjDetailRSP> projDetail(AppProjDetailREQ req) {
        AppProjDetailRSP appProjDetailRSP = new AppProjDetailRSP();
        if (AppProjStageStatus.GROUP_CREDIT_ESTABLISH.name().equalsIgnoreCase(req.getStage())) {
            GroupCreditEstablishBaseInfoDetailRSP rsp = groupCreditEstablishBaseInfoService.detail(req.getId(), req.getVersion());
            appProjDetailRSP = BeanUtil.copyProperties(rsp, AppProjDetailRSP.class);
            appProjDetailRSP.setStage(AppProjStageStatus.GROUP_CREDIT_ESTABLISH.name());
        } else if (AppProjStageStatus.GROUP_CREDIT_REVIEW.name().equalsIgnoreCase(req.getStage())) {
            GroupCreditReviewBaseInfoDetailRSP rsp = groupCreditReviewBaseInfoService.detail(req.getId(), req.getVersion());
            appProjDetailRSP = BeanUtil.copyProperties(rsp, AppProjDetailRSP.class);
            appProjDetailRSP.setStage(AppProjStageStatus.GROUP_CREDIT_REVIEW.name());
        } else if (AppProjStageStatus.PROJ_ESTABLISH_BASE.name().equalsIgnoreCase(req.getStage())) {
            ProjEstablishBaseInfoDetailREQ establishBaseInfoDetailREQ = new ProjEstablishBaseInfoDetailREQ();
            establishBaseInfoDetailREQ.setId(req.getId());
            R<ProjEstablishBaseInfoListRSP> rsp = projEstablishBaseInfoService.detail(establishBaseInfoDetailREQ);
            if (rsp != null) {
                ProjEstablishBaseInfoListRSP projEstablishBaseInfoListRSP = rsp.getData();
                appProjDetailRSP = BeanUtil.copyProperties(projEstablishBaseInfoListRSP, AppProjDetailRSP.class);
                List<AppPersonInfo> guaranteeInfoList = new ArrayList<>();
                if (projEstablishBaseInfoListRSP.getGuaranteeInfo() != null) {
                    for (ProjEstablishPersonInfo projEstablishPersonInfo : projEstablishBaseInfoListRSP.getGuaranteeInfo()) {
                        AppPersonInfo appPersonInfo = BeanUtil.copyProperties(projEstablishPersonInfo, AppPersonInfo.class);
                        guaranteeInfoList.add(appPersonInfo);
                    }
                    appProjDetailRSP.setGuaranteeInfo(guaranteeInfoList);
                }
                List<AppPersonInfo> lesseeInfoList = new ArrayList<>();
                if (projEstablishBaseInfoListRSP.getLesseeInfo() != null) {
                    for (ProjEstablishPersonInfo projEstablishPersonInfo : projEstablishBaseInfoListRSP.getLesseeInfo()) {
                        AppPersonInfo appPersonInfo = BeanUtil.copyProperties(projEstablishPersonInfo, AppPersonInfo.class);
                        lesseeInfoList.add(appPersonInfo);
                    }
                    appProjDetailRSP.setLesseeInfo(lesseeInfoList);
                }
            }
            ProjEstablishPriceDetailRSP priceDetailRSP = establishPriceService.detail(req.getId());
            if (priceDetailRSP != null) {
                AocPriceDetailRSP aocPriceRSP = BeanUtil.copyProperties(priceDetailRSP.getAocPriceRSP(), AocPriceDetailRSP.class);
                appProjDetailRSP.setAocPriceRSP(aocPriceRSP);
                FactoringPriceDetailRSP factoringPriceRSP = BeanUtil.copyProperties(priceDetailRSP.getFactoringPriceRSP(), FactoringPriceDetailRSP.class);
                appProjDetailRSP.setFactoringPriceRSP(factoringPriceRSP);
                LeasePriceDetailRSP leasePriceRSP = BeanUtil.copyProperties(priceDetailRSP.getLeasePriceRSP(), LeasePriceDetailRSP.class);
                appProjDetailRSP.setLeasePriceRSP(leasePriceRSP);
                appProjDetailRSP.setStage(AppProjStageStatus.PROJ_ESTABLISH_BASE.name());
            }
        } else if (AppProjStageStatus.PROJ_REVIEW_BASE.name().equalsIgnoreCase(req.getStage())) {
            ProjReviewBaseInfoDetailRSP rsp = reviewBaseInfoService.detail(req.getId(), null);
            if (rsp != null) {
                appProjDetailRSP = BeanUtil.copyProperties(rsp, AppProjDetailRSP.class);
                List<AppPersonInfo> guaranteeInfoList = new ArrayList<>();
                if (rsp.getGuaranteeInfo() != null) {
                    for (ClientInfo clientInfo : rsp.getGuaranteeInfo()) {
                        AppPersonInfo appPersonInfo = BeanUtil.copyProperties(clientInfo, AppPersonInfo.class);
                        guaranteeInfoList.add(appPersonInfo);
                    }
                    appProjDetailRSP.setGuaranteeInfo(guaranteeInfoList);
                }
                List<AppPersonInfo> lesseeInfoList = new ArrayList<>();
                if (rsp.getLesseeInfo() != null) {
                    for (ClientInfo clientInfo : rsp.getLesseeInfo()) {
                        AppPersonInfo appPersonInfo = BeanUtil.copyProperties(clientInfo, AppPersonInfo.class);
                        lesseeInfoList.add(appPersonInfo);
                    }
                    appProjDetailRSP.setLesseeInfo(lesseeInfoList);
                }
            }
            ProjReviewPriceDetailRSP priceDetailRSP = reviewPriceService.detail(req.getId());
            if (priceDetailRSP != null) {
                AocPriceDetailRSP aocPriceRSP = BeanUtil.copyProperties(priceDetailRSP.getAocPriceDetailRSP(), AocPriceDetailRSP.class);
                appProjDetailRSP.setAocPriceRSP(aocPriceRSP);
                FactoringPriceDetailRSP factoringPriceRSP = BeanUtil.copyProperties(priceDetailRSP.getFactoringPriceDetailRSP(), FactoringPriceDetailRSP.class);
                appProjDetailRSP.setFactoringPriceRSP(factoringPriceRSP);
                LeasePriceDetailRSP leasePriceRSP = BeanUtil.copyProperties(priceDetailRSP.getLeasePriceDetailRSP(), LeasePriceDetailRSP.class);
                appProjDetailRSP.setLeasePriceRSP(leasePriceRSP);
                appProjDetailRSP.setStage(AppProjStageStatus.PROJ_REVIEW_BASE.name());
            }
        }
        return R.ok(appProjDetailRSP);
    }

    @Override
    public R<AppProContractDetailRSP> contractDetail(AppProjContractDetailREQ req) {
        AppProContractDetailRSP detailRSP = new AppProContractDetailRSP();
        ContractLeasePrice contractLeasePrice = leasePriceService.detail(new ContractPriceDetailREQ(req.getContractId()));
        //租赁期限月数
        int leaseMonthCount = 0;
        //还款期数
        int repayTimesTotal = 0;
        if (contractLeasePrice != null) {
            leaseMonthCount = contractLeasePrice.getLeaseMonthCount();
            repayTimesTotal = contractLeasePrice.getRepayTimesTotal();
        }
        //实际租金表
        ContractSingleIdREQ contractSingleIdREQ = new ContractSingleIdREQ();
        contractSingleIdREQ.setContractId(req.getContractId());
        R<List<ContractRentActualListRSP>> rentActualListRSPR = contractRentController.listActualRent(contractSingleIdREQ);
        if (rentActualListRSPR != null && !rentActualListRSPR.getData().isEmpty()) {
            List<AppProContractDetailRSP.BasicInfoRSP> basicInfoRSPList = new ArrayList<>();
            detailRSP.setBasicInfoRSPList(basicInfoRSPList);
            List<ContractRentActualListRSP> contractRentActualListRSPList = rentActualListRSPR.getData();
            for (ContractRentActualListRSP rentActualListRSP : contractRentActualListRSPList) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(rentActualListRSP.getContractId());
                ContractcpContractReceiptDetailREQ contractDetailREQ = new ContractcpContractReceiptDetailREQ();
                contractDetailREQ.setContractId(rentActualListRSP.getContractId());
                contractDetailREQ.setReceiptCode(rentActualListRSP.getReceiptCode());
                List<CollectionBaseInfo> collectionBaseInfoList = contractCollectionPaymentService.getCollectionBaseInfoList(contractDetailREQ);
                if (collectionBaseInfoList.isEmpty()) {
                    throw new MithrasException("没有现金流");
                }
                ContractInfoReceiptRSP contractInfoReceiptRSP = contractCollectionPaymentService.contractInfoByReceipt(contractDetailREQ);
                detailRSP.setId(req.getContractId());
                AppProContractDetailRSP.BasicInfoRSP basicInfoRSP = new AppProContractDetailRSP.BasicInfoRSP();
                basicInfoRSP.setRemainingRent(LongUtil.null2zero(contractInfoReceiptRSP.getLastPrincipal()) + LongUtil.null2zero(contractInfoReceiptRSP.getLastInterest()));
                basicInfoRSP.setRemainingPrincipal(LongUtil.null2zero(contractInfoReceiptRSP.getLastPrincipal()));
                basicInfoRSP.setRiskExposure(LongUtil.null2zero(contractInfoReceiptRSP.getLastPrincipal()) - LongUtil.null2zero(contractInfoReceiptRSP.getLastMargin()));
                basicInfoRSP.setReceiptCode(rentActualListRSP.getReceiptCode());
                basicInfoRSPList.add(basicInfoRSP);
                int monthGap = appService.monthGap(rentActualListRSP.getActualStartDate());
                basicInfoRSP.setLeaseMonthRate(new StringBuilder().append(monthGap).append("/").append(leaseMonthCount).toString());
                List<ContractRentActualListRSP.TableData> res = new ArrayList<>();
                for (ContractRentActualListRSP.TableData tableData : rentActualListRSP.getRentActualList()) {
                    if (appService.isSameYearAndMonth(tableData.getDate())) {
                        res.add(tableData);
                    }
                }
                if (res.isEmpty()) {
                    res = appService.getLatestMonthData(rentActualListRSP.getRentActualList());
                }
                if (!res.isEmpty()) {
                    long planCollectionAmount = 0;
                    long collectionAmount = 0;
                    long unCollectionAmount = 0;
                    for (ContractRentActualListRSP.TableData tableData : res) {
                        String cashFlowCode = tableData.getCashFlowCode();
                        List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                .eq(CollectionBaseInfo::getCode, cashFlowCode));
                        if (list == null || list.isEmpty()) {
                            continue;
                        }
                        CollectionBaseInfoDetailREQ collectionBaseInfoDetailREQ = new CollectionBaseInfoDetailREQ();
                        collectionBaseInfoDetailREQ.setId(list.get(0).getId());
                        CollectionBaseInfoRSP collectionBaseInfoRSP = collectionBaseInfoService.detail(collectionBaseInfoDetailREQ);
                        if (collectionBaseInfoRSP != null) {
                            planCollectionAmount += LongUtil.null2zero(collectionBaseInfoRSP.getCashFlowAmount());
                        }
                        CollectionRecordListREQ collectionRecordListREQ = new CollectionRecordListREQ();
                        collectionRecordListREQ.setId(list.get(0).getId());
                        CollectionRecordListRSP collectionRecordListRSP = collectionRecordInfoService.list(collectionRecordListREQ);
                        if (collectionRecordListRSP != null && collectionRecordListRSP.getSum() != null) {
                            collectionAmount += LongUtil.null2zero(collectionRecordListRSP.getSum().getCollectionAmount());
                        }
                    }
                    unCollectionAmount = (planCollectionAmount - collectionAmount) <= 0 ? 0 : (planCollectionAmount - collectionAmount);
                    basicInfoRSP.setCollectionAmount(collectionAmount);
                    basicInfoRSP.setUnCollectionAmount(unCollectionAmount);
                    basicInfoRSP.setPlanCollectionAmount(planCollectionAmount);
                    int phase = res.get(res.size()-1).getPhase();
                    StringBuilder builder = new StringBuilder().append(phase).append("/").append(repayTimesTotal);
                    basicInfoRSP.setRepayTimesRate(builder.toString());
                }
                //期项
                List<AppProContractDetailRSP.CollectionBaseInfoRSP> collectionBaseInfoRSPList = new ArrayList<>();
                CollectionBaseInfoREQ collectionBaseInfoREQ = new CollectionBaseInfoREQ();
                collectionBaseInfoREQ.setPage(1);
                collectionBaseInfoREQ.setPageSize(Integer.MAX_VALUE);
                collectionBaseInfoREQ.setContractCode(contractBaseInfo.getContractCode());
                PageR<CollectionBaseInfoListRSP> collectionBaseInfoListRSPPageR = collectionBaseInfoService.list(collectionBaseInfoREQ);
                if (collectionBaseInfoListRSPPageR != null && !collectionBaseInfoListRSPPageR.getList().isEmpty()) {
                    List<CollectionBaseInfoListRSP> data = new ArrayList<>();
                    for (CollectionBaseInfoListRSP rsp : collectionBaseInfoListRSPPageR.getList()) {
                        if (rsp.getPhase() != null && rsp.getPhase() > 0 && CashFlowItemEnum.RENT.display().equalsIgnoreCase(rsp.getCashFlowItem())) {
                            data.add(rsp);
                        }
                    }
                    for (CollectionBaseInfoListRSP rsp : data) {
                        AppProContractDetailRSP.CollectionBaseInfoRSP baseInfoRSP = BeanUtil.copyProperties(rsp, AppProContractDetailRSP.CollectionBaseInfoRSP.class);
                        collectionBaseInfoRSPList.add(baseInfoRSP);
                    }
                }
                basicInfoRSP.setCollectionBaseInfoRSPList(collectionBaseInfoRSPList);
            }
        }

        return R.ok(detailRSP);
    }

    @Override
    public R<List<AppProContractBaseRSP>> contractBase(AppProjContractBaseREQ req) {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjCode, req.getProjCode()));
        if (contractBaseInfoList.isEmpty()) {
            return R.ok(new ArrayList<AppProContractBaseRSP>());
        }
        List<AppProContractBaseRSP> res = new ArrayList<>();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            AppProContractBaseRSP appProContractBaseRSP = BeanUtil.copyProperties(contractBaseInfo, AppProContractBaseRSP.class);
            res.add(appProContractBaseRSP);
        }
        return R.ok(res);
    }

    @Override
    public R<AppCashFlowGenerationRSP> generate(AppCashFlowGenerationREQ req) {
        if(ObjectUtil.isEmpty(req.getPayType())){
            req.setPayType(PayType.AFTERWARD.name());
        }
        err(!StrUtil.equalsAny(req.getRentalCalcType(), RepayCalcType.DEBX.name(), RepayCalcType.DEBJ.name()),
                "该功能暂时只支持租金计算方式为[等额本息、等额本金]的项目");
        List<CashFlowBO> cashFlowList = calcCashFlow(copyProperties(req, CashFlowCalculateBO.class));
        AppCashFlowGenerationRSP rsp = new AppCashFlowGenerationRSP();
        List<AppCashFlowGenerationRSP.CashFlowBasicInfoRSP> basicInfoRSPList = copyToList(cashFlowList, AppCashFlowGenerationRSP.CashFlowBasicInfoRSP.class);
        rsp.setBasicInfoRSPList(basicInfoRSPList);

        CashFlowGenerationIrrREQ irrREQ = new CashFlowGenerationIrrREQ();
        irrREQ.setMonthCount(req.getLeaseMonthCount());
        irrREQ.setRepayRate(req.getRepayRate());
        List<CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem> irrItemList = copyToList(cashFlowList, CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem.class);
        irrREQ.setItemList(irrItemList);
        RepayRateEnum repayRateEnum = of(req.getRepayRate());
        List<CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem> itemList = irrREQ.getItemList();
        for (CashFlowGenerationIrrREQ.CashFlowGenerationIrrItem item : itemList) {
            if (ObjectUtil.isNull(item.getRent())) {
                item.setRent(0L);
            }
            if (ObjectUtil.isNull(item.getPrincipal())) {
                item.setPrincipal(0L);
            }
            if (ObjectUtil.isNull(item.getInterest())) {
                item.setInterest(0L);
            }
        }
        CashFlowIRRBO bo = calculateIRR(irrREQ.getMonthCount(), repayRateEnum, copyToList(itemList, CashFlowBO.class));
        rsp.setIrr(bo.getIrr().setScale(4, RoundingMode.HALF_UP).toPlainString());
        Long totalRent = 0L;
        Long totalInterest = 0L;
        for (CashFlowBO cashFlowBO : cashFlowList) {
            if (cashFlowBO.getCashFlowPhase() > 0) {
                totalRent += cashFlowBO.getRent();
                totalInterest += cashFlowBO.getInterest();
            }
        }
        rsp.setTotalRent(totalRent);
        rsp.setTotalInterest(totalInterest);
        return R.ok(rsp);
    }

    @Override
    public R<AppCalendarDailyRSP> dailyList(AppCalendarDailyREQ req) {
        if (req.getCollectionDate() == null) {
            throw new MithrasException("选择日期");
        }
        RentCollectionListREQ collectionListREQ = new RentCollectionListREQ();
        collectionListREQ.setPage(1);
        collectionListREQ.setPageSize(Integer.MAX_VALUE);
        List<RentCollectionListRSP> rentCollectionListRSPList = rentCollectionIndexService.getRentCollectionList(collectionListREQ, req.getType());
        if (rentCollectionListRSPList.isEmpty()) {
            return R.ok(new AppCalendarDailyRSP());
        }
        Map<LocalDate, Map<Long, List<RentCollectionListRSP.CollectionCardData>>> map = new HashMap<>();
        for (RentCollectionListRSP rentCollectionListRSP : rentCollectionListRSPList) {
            for (RentCollectionListRSP.CollectionCardData collectionCardData : rentCollectionListRSP.getCollectionCardList()) {
                map.putIfAbsent(collectionCardData.getPlanCollectionDate(), new HashMap<>());
                map.get(collectionCardData.getPlanCollectionDate()).putIfAbsent(rentCollectionListRSP.getClientId(), new ArrayList<>());
                map.get(collectionCardData.getPlanCollectionDate()).get(rentCollectionListRSP.getClientId()).add(collectionCardData);
            }
        }

        Long dailyPlanCollectionAmount = 0L;
        Long dailyCollectionAmount = 0L;
        Long dailyUnCollectionAmount = 0L;
        AppCalendarDailyRSP appCalendarDailyRSP = new AppCalendarDailyRSP();
        List<AppCalendarDailyRSP.CollectionCardData> collectionCardDataList = new ArrayList<>();
        appCalendarDailyRSP.setCollectionCardDataList(collectionCardDataList);
        Map<Long, List<RentCollectionListRSP.CollectionCardData>> cardDataMap = map.get(req.getCollectionDate());
        if (cardDataMap != null) {
            for (Map.Entry<Long, List<RentCollectionListRSP.CollectionCardData>> entry : cardDataMap.entrySet()) {
                Long clientId = entry.getKey();
                List<RentCollectionListRSP.CollectionCardData> collectionCardDataRes = entry.getValue();
                for (RentCollectionListRSP.CollectionCardData collectionCardData : collectionCardDataRes) {
                    for (RentCollectionListRSP rentCollectionListRSP : rentCollectionListRSPList) {
                        for (RentCollectionListRSP.CollectionCardData cardData : rentCollectionListRSP.getCollectionCardList()) {
                            if (collectionCardData.getCollectionId().equals(cardData.getCollectionId())) {
                                AppCalendarDailyRSP.CollectionCardData data = BeanUtil.copyProperties(collectionCardData, AppCalendarDailyRSP.CollectionCardData.class);
                                data.setClientName(rentCollectionListRSP.getClientName());
                                data.setProjSponsorUserName(rentCollectionListRSP.getProjSponsorUserName());
                                dailyPlanCollectionAmount += LongUtil.null2zero(collectionCardData.getPlanCollectionAmount());
                                dailyCollectionAmount += LongUtil.null2zero(collectionCardData.getCollectionAmount());
                                dailyUnCollectionAmount += (dailyPlanCollectionAmount - dailyCollectionAmount) < 0 ? 0 : (dailyPlanCollectionAmount - dailyCollectionAmount);
                                collectionCardDataList.add(data);
                                break;
                            }
                        }
                    }
                }
            }
        }
        appCalendarDailyRSP.setDailyPlanCollectionAmount(dailyPlanCollectionAmount);
        appCalendarDailyRSP.setDailyCollectionAmount(dailyCollectionAmount);
        appCalendarDailyRSP.setDailyUnCollectionAmount(dailyUnCollectionAmount);
        return R.ok(appCalendarDailyRSP);
    }

    @Override
    public R<AppCalendarDetailRSP> dailyDetail(AppCalendarDetailREQ req) {
        RentCollectionListREQ collectionListREQ = new RentCollectionListREQ();
        collectionListREQ.setPage(1);
        collectionListREQ.setPageSize(Integer.MAX_VALUE);
        collectionListREQ.setFilterConditionType(RentCollectionIndexFilterConditionType.ALL.name());
        PageR<RentCollectionListRSP> rentCollectionListRSP = rentCollectionIndexService.indexList(collectionListREQ);
        if (rentCollectionListRSP == null || rentCollectionListRSP.getList().isEmpty()) {
            return R.ok(new AppCalendarDetailRSP());
        }
        List<RentCollectionListRSP> rentCollectionListRSPList = rentCollectionListRSP.getList();
        for (RentCollectionListRSP rsp : rentCollectionListRSPList) {
            for (RentCollectionListRSP.CollectionCardData collectionCardData : rsp.getCollectionCardList()) {
                if (req.getId().equals(collectionCardData.getCollectionId())) {
                    AppCalendarDetailRSP appCalendarDetailRSP = BeanUtil.copyProperties(collectionCardData, AppCalendarDetailRSP.class);
                    appCalendarDetailRSP.setContractCode(rsp.getContractCode());
                    return R.ok(appCalendarDetailRSP);
                }
            }
        }
        return R.ok();
    }

    @Override
    public R<AppCalendarMonthlyRSP> monthlyList(AppCalendarMonthlyREQ req) {
        if (StringUtils.isBlank(req.getCollectionMonth())) {
            throw new MithrasException("选择月份");
        }
        RentCollectionListREQ collectionListREQ = new RentCollectionListREQ();
        collectionListREQ.setPage(1);
        collectionListREQ.setPageSize(Integer.MAX_VALUE);
        List<RentCollectionListRSP> rentCollectionListRSPList = rentCollectionIndexService.getRentCollectionList(collectionListREQ, req.getType());
        if (rentCollectionListRSPList.isEmpty()) {
            return R.ok(new AppCalendarMonthlyRSP());
        }
        Map<LocalDate, Map<Long, List<RentCollectionListRSP.CollectionCardData>>> map = new HashMap<>();
        for (RentCollectionListRSP rentCollectionListRSP : rentCollectionListRSPList) {
            for (RentCollectionListRSP.CollectionCardData collectionCardData : rentCollectionListRSP.getCollectionCardList()) {
                map.putIfAbsent(collectionCardData.getPlanCollectionDate(), new HashMap<>());
                map.get(collectionCardData.getPlanCollectionDate()).putIfAbsent(rentCollectionListRSP.getClientId(), new ArrayList<>());
                map.get(collectionCardData.getPlanCollectionDate()).get(rentCollectionListRSP.getClientId()).add(collectionCardData);
            }
        }
        //日期排序
        TreeMap<LocalDate, Map<Long, List<RentCollectionListRSP.CollectionCardData>>> treeTemp = new TreeMap<>(
                new Comparator<LocalDate>() {
                    @Override
                    public int compare(LocalDate o1, LocalDate o2) {
                        return o1.compareTo(o2);
                    }
                }
        );
        for (Map.Entry<LocalDate, Map<Long, List<RentCollectionListRSP.CollectionCardData>>> entry : map.entrySet()) {
            LocalDate localDate = entry.getKey();
            Map<Long, List<RentCollectionListRSP.CollectionCardData>> tempMap = entry.getValue();
            if (appService.isSameYearMonth(req.getCollectionMonth(), localDate)) {
                treeTemp.put(localDate, tempMap);
            }
        }
        //汇总
        Map<LocalDate, AppCalendarMonthlyRSP.collectionObj> collectionMap = new HashMap<>();
        for (Map.Entry<LocalDate, Map<Long, List<RentCollectionListRSP.CollectionCardData>>> entry : treeTemp.entrySet()) {
            LocalDate localDate = entry.getKey();
            Map<Long, List<RentCollectionListRSP.CollectionCardData>> tempMap = entry.getValue();
            if (tempMap != null && !tempMap.isEmpty()) {
                List<RentCollectionListRSP.CollectionCardData> tempList = new ArrayList<>();
                for (List<RentCollectionListRSP.CollectionCardData> list : tempMap.values()) {
                    tempList.addAll(list);
                }
                int paidCount = 0;
                int noPaidCount = 0;
                for (RentCollectionListRSP.CollectionCardData collectionCardData : tempList) {
                    if (LongUtil.null2zero(collectionCardData.getPlanCollectionAmount()).equals(LongUtil.null2zero(collectionCardData.getCollectionAmount()))) {
                        paidCount ++;
                    } else {
                        noPaidCount++;
                    }
                }
                AppCalendarMonthlyRSP.collectionObj collectionObj = new AppCalendarMonthlyRSP.collectionObj();
                if (noPaidCount > 0) {
                    collectionObj.setCount(noPaidCount);
                    collectionObj.setType(AppPaymentStatus.NO_PAID.name());
                } else {
                    collectionObj.setCount(paidCount);
                    collectionObj.setType(AppPaymentStatus.PAID.name());
                }
                collectionMap.putIfAbsent(localDate, collectionObj);
            }
        }
        AppCalendarMonthlyRSP res = new AppCalendarMonthlyRSP();
        res.setCollectionMap(collectionMap);
        return R.ok(res);
    }

    @Override
    public R<List<AppContractSignListRSP>> signList(AppContractSignListREQ req) {
        ContractBaseInfoListREQ contractBaseInfoListREQ = new ContractBaseInfoListREQ();
        contractBaseInfoListREQ.setPage(1);
        contractBaseInfoListREQ.setPageSize(Integer.MAX_VALUE);
        Page<ContractBaseInfoListRSP> data = contractBaseInfoService.list(contractBaseInfoListREQ);
        if (data == null || data.getRecords().isEmpty()) {
            return R.ok(new ArrayList<>());
        }
        List<ContractBaseInfoListRSP> tempList = data.getRecords().stream().filter(
                f -> Arrays.asList("START_RENT", "TAKE_EFFECT", "SETTLE").contains(f.getContractStatus())
                && f.getIsSigned().equals(req.getIsSigned())).collect(Collectors.toList());
        List<ContractBaseInfoListRSP> contractBaseInfoListRSPList = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getContractCode())) {
            for (ContractBaseInfoListRSP contractBaseInfoListRSP : tempList) {
                if (contractBaseInfoListRSP.getContractCode().contains(req.getContractCode())) {
                    contractBaseInfoListRSPList.add(contractBaseInfoListRSP);
                }
            }
        } else {
            contractBaseInfoListRSPList = tempList;
        }
        List<AppContractSignListRSP> res = BeanUtil.copyToList(contractBaseInfoListRSPList, AppContractSignListRSP.class);
        List<Long> contractIds = res.stream().map(AppContractSignListRSP::getId).collect(Collectors.toList());
        if (!contractIds.isEmpty()) {
            List<ContractTenantry> contractTenantryList = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery().in(ContractTenantry::getContractId, contractIds));
            Map<Long, List<ContractTenantry>> contractTenantryMap = contractTenantryList.stream().collect(Collectors.groupingBy(ContractTenantry::getContractId));
            for (AppContractSignListRSP appContractSignListRSP : res) {
                List<ContractTenantry> contractTenantries = contractTenantryMap.get(appContractSignListRSP.getId());
                for (ContractTenantry contractTenantry : contractTenantries) {
                    if (LesseeTypeEnum.MAIN_LESSSEE.name().equalsIgnoreCase(contractTenantry.getLesseeType())) {
                        appContractSignListRSP.setLesseeType(contractTenantry.getLesseeType());
                        appContractSignListRSP.setLesseeName(contractTenantry.getLesseeName());
                        break;
                    }
                }
            }
        }
        return R.ok(res);
    }

    @Override
    public R<AppContractSignRSP> sign(AppContractSignREQ req) {
        return R.ok(appService.signContract(req));
    }

    @Override
    public R<List<AppContractSignExistedRSP>> contractExisted(AppContractSignExistedREQ req) {
        return R.ok(appService.contractExistedSigned(req));
    }

    @Override
    public R<List<AppContractSignProjExistedRSP>> projExisted(AppContractSignProjExistedREQ req) {
        return R.ok(appService.projExistedSigned(req));
    }

    @Override
    public R<List<AppContractSignCopyRSP>> signedCopy(AppContractSignCopyREQ req) {
        return R.ok(appService.signedCopy(req));
    }

    @Override
    public R<List<AppContractSignDetailRSP>> signDetail(AppContractSignDetailREQ req) {
        return R.ok(appService.signDetail(req));
    }

    @Override
    public R<AppContractPaySignedRSP> isSigned(AppContractPaySignedREQ req) {
        return R.ok(appService.isSigned(req));
    }

    @Override
    public R<Void> updateSign(AppContractSignUpdateREQ req) {
        appService.updateSign(req);
        return R.ok();
    }

    @Override
    public R<List<AppAuthorityDetailRSP>> detail(AppAuthorityDetailREQ req) {
        return R.ok(appService.authorityDetail(req));
    }

    @Override
    public R<FileUploadRSP> upload(Long id, String moduleType, String materialsType, String materialsSubType, Integer needWatermark, String batchNo, String processInstanceId, String sourceBusinessKey, Map<String, Object> ext, Long userId, Long createdBy, String location, AppFileUploadREQ appFileUploadREQ) {
        FileUploadREQ fileUploadREQ = new FileUploadREQ(appFileUploadREQ.getFile(), id, moduleType, materialsType, materialsSubType, needWatermark, batchNo, processInstanceId, sourceBusinessKey, ext, userId, createdBy, location);
        return R.ok(fileService.upload(fileUploadREQ));
    }

    @Override
    public R<String> batchDownload(AppVisitFileBatchDownloadREQ req) {
        String lockKey = "mithras:visit:batchDownload:lock";
        Object lock = null;
        try {
            // 添加分布式锁，控制只能单人下载
            lock = redisTemplate.opsForValue().get(lockKey);
            if (Objects.nonNull(lock)) {
                throw new MithrasException("当前请求人数过多");
            }
            redisTemplate.opsForValue().set(lockKey, "lock", 60, TimeUnit.SECONDS);
            lock = new Object();
            String url = appService.batchDownload(req);
            return R.ok(url);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量下载发生未知异常[{}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("批量下载发生未知异常");
        } finally {
            if (Objects.nonNull(lock)) {
                // 释放
                try {
                    redisTemplate.delete(lockKey);
                } catch (Exception e) {
                    log.error("分布式锁释放失败[{}]", lockKey, e);
                }
            }
        }
    }

    @Override
    public R<List<VisitDownloadTaskRSP>> listDownloadTask() {
        List<VisitDownloadTaskRecord> dbList = SpringUtil.getBean(VisitDownloadTaskRecordMapper.class).selectList(
                Wrappers.<VisitDownloadTaskRecord>lambdaQuery().eq(VisitDownloadTaskRecord::getUserId, AccountUtil.getLoginInfo().getId()));
        List<VisitDownloadTaskRSP> result = dbList.stream().map(e -> {
            VisitDownloadTaskRSP rsp = copyProperties(e, VisitDownloadTaskRSP.class);
            try {
                rsp.setFileUrl(SpringUtil.getBean(MaterialsListService.class).getPreviewUrl(e.getFilePath(), 3600));
            } catch (Exception ex) {
                log.error("获取文件地址异常[{}]", JSONUtil.toJsonStr(e), ex);
            }
            return rsp;
        }).collect(Collectors.toList());
        Set<Long> userIds = result.stream().map(VisitDownloadTaskRSP::getUserId).collect(Collectors.toSet());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        result.forEach(e -> e.setUserName(userNameMap.get(e.getUserId())));
        return R.ok(result);
    }


}
