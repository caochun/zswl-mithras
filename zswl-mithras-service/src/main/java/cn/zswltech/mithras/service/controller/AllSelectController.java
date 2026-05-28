package cn.zswltech.mithras.service.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.notice.constans.enums.NoticeTypeEnum;
import cn.zswltech.flow.core.enums.*;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.AllSelectApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.associationreport.service.AssociationReportDataAccessService;
import cn.zswltech.mithras.blackgray.enums.BlackGrayOrgEnum;
import cn.zswltech.mithras.dto.LeafSelectRSP;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.TreeSelectRSP;
import cn.zswltech.mithras.factory.enums.RatingBizTypeEnum;
import cn.zswltech.mithras.service.config.enumscan.PullDownEnumProcessor;
import cn.zswltech.mithras.service.convert.CommonConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckStatusEnum;
import cn.zswltech.mithras.service.enums.app.*;
import cn.zswltech.mithras.service.enums.associationreport.*;
import cn.zswltech.mithras.service.enums.client.*;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.*;
import cn.zswltech.mithras.service.enums.creditreport.*;
import cn.zswltech.mithras.service.enums.dashboard.DashboardOperationTermEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsPolicyEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.service.enums.fund.DirectFinancingType;
import cn.zswltech.mithras.service.enums.fund.financing.*;
import cn.zswltech.mithras.service.enums.fund.liquidity.SettingTimeEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.ExpenseType;
import cn.zswltech.mithras.service.enums.lease.LeaseFileOCRStatusEnum;
import cn.zswltech.mithras.service.enums.lease.LeaseVatInvoiceStatusEnum;
import cn.zswltech.mithras.service.enums.lease.LeaseVatInvoiceTypeEnum;
import cn.zswltech.mithras.service.enums.margin.RecordTypeEnum;
import cn.zswltech.mithras.service.enums.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.service.enums.payment.PaymentCurrencyStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.enums.projreview.ProjRegionalDivisionEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjectType;
import cn.zswltech.mithras.service.enums.riskcontrol.jzd.report.JzdReportCreateType;
import cn.zswltech.mithras.service.enums.riskcontrol.jzd.report.JzdReportStatus;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.corp.GeneralDictionaryMapper;
import cn.zswltech.mithras.service.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.dto.FundPlanFlowResultDTO;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.service.mapper.model.client.IndustryType;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.filingmaterials.AfterFilingMaterialsService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.util.ObjectUtil.equal;
import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author luyi
 */
@Slf4j
@RestController
public class AllSelectController implements AllSelectApi {
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;
    @Resource
    private UserService userService;
    @Resource
    private BusinessDataRepository businessDataRepository;
    @Resource
    protected FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;

//    private Map<String, List<SelectRSP>> pullDown;

    @SneakyThrows
    @Override
    public R<Map<String, List<SelectRSP>>> allSelect() {
        // fix since 2022-12-06 内部已使用缓存 去掉外层的缓存pullDown
//        if(ObjectUtil.isEmpty(pullDown)) {
        Map<String, List<SelectRSP>> r = PullDownEnumProcessor.getPullDown();
        r.put("continuousStatus", enumType(businessDataRepository.getContinuousTypeFromLocalCache()));
        r.put("economyType", enumType(businessDataRepository.getEconomyTypeFromLocalCache()));
        r.put("currencyType", enumType(businessDataRepository.getCurrencyTypeFromLocalCache()));
        r.put("certType", enumType(businessDataRepository.getCertTypeFromLocalCache()));
        r.put("orgType", enumType(businessDataRepository.getOrgTypeFromLocalCache()));
        r.put("projEstablishBizType", projEstablishBizType());
        r.put("projEstablishStatus", projEstablishStatus());
        r.put("projectBizType", projectBizType());
        r.put("projReviewStatus", projReviewStatus());
        r.put("processStatus", processStatus());
        r.put("taskStatus", taskStatus());
        r.put("processModelType", processModelType());
        r.put("moduleType", moduleType());
        r.put("operationType", operationType());
        r.put("addressType", addressType());
        r.put("MarginWriteOffStatusEnum", marginWriteOffStatusEnum());
        r.put("lesseeypeEnum", lesseeypeEnum());
        r.put("RecordTypeEnum", recordTypeEnum());
        r.put("LPRTypeEnum", LPRTypeEnum());
        r.put("subjectQuarterType", subjectQuarterType());
        r.put("commonProcessStatus", commonProcessStatus());
        r.put("projProcessStatus", projProcessStatus());
        r.put("approverType", approverType());
        r.put("parallelApprovalMethed", parallelApprovalMethed());
        r.put("buttonList", buttonList());
        r.put("dynamicFormList", dynamicFormList());
        r.put("blProjectType", blProjectType());
        r.put("noticeTypeEnum", noticeTypeEnum());
        r.put("riskControlOpinionHandleStatus", riskControlOpinionHandleStatus());
        r.put("jzdReportCreateType", jzdReportCreateType());
        r.put("jzdReportStatus", jzdReportStatus());
        r.put("financingBizType", financingBizType());
        r.put("leaseVatInvoiceType", leaseVatInvoiceType());
        r.put("leaseFileOCRStatus", leaseFileOCRStatus());
        r.put("leaseVatInvoiceStatus", leaseVatInvoiceStatus());
        // 自定义逻辑进行覆盖
        r.put("cashFlowItemEnum", cashFlowItemEnum());
        r.put("contractTypeEnum", contractTypeEnum());
        r.put("paymentTypeEnum", paymentTypeEnum());
        r.put("resolutionTypeEnum", resolutionTypeEnum());
        r.put("paymentCurrencyStatusEnum", paymentCurrencyStatusEnum());
        // 流水中心-资金端-现金流类型
        r.put("businessFlowFinanceCashFlowItemType", this.businessFlowFinanceCashFlowItemType());
        //融资借款性质
        r.put("fundFinancingTimeLimitTypeEnum", this.fundFinancingTimeLimitTypeEnum());
        r.put("fundFinancingNewStatusEnum", this.fundFinancingNewStatusEnum());
        // 苍穹单据信息
        r.put("cqBillTypeEnum", this.cqBillTypeEnum());
        r.put("confirmIncomeEnum", confirmIncomeEnum());
        r.put("dashboardOperationTermEnum", dashboardOperationTermEnum());
        // 合同文本类型
        r.put("contractTextStandard", this.contractTextStandard());
        r.put("contractTextNonstandard", this.contractTextNonstandard());
        r.put("clientLevel", this.clientLevelEnum());
        r.put("clientProjArchive", this.clientProjArchiveEnum());
        // 客户资料清单子类型
        r.put(CorporationClientMaterialTypeEnum.BASIC_INFORMATION.childSelectName(), this.corporationClientMaterialSubType(CorporationClientMaterialTypeEnum.BASIC_INFORMATION));
        r.put(CorporationClientMaterialTypeEnum.LEASE_APPLICATION.childSelectName(), this.corporationClientMaterialSubType(CorporationClientMaterialTypeEnum.LEASE_APPLICATION));
        r.put(CorporationClientMaterialTypeEnum.CREDIT_LETTER.childSelectName(), this.corporationClientMaterialSubType(CorporationClientMaterialTypeEnum.CREDIT_LETTER));
        r.put(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION.childSelectName(), this.corporationClientMaterialSubType(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION));
        r.put(CorporationClientMaterialTypeEnum.BUSINESS_INFORMATION.childSelectName(), this.corporationClientMaterialSubType(CorporationClientMaterialTypeEnum.BUSINESS_INFORMATION));
        r.put(CorporationClientMaterialTypeEnum.OTHERS.childSelectName(), this.corporationClientMaterialSubType(CorporationClientMaterialTypeEnum.OTHERS));
        r.put(NormalClientMaterialTypeEnum.BASIC_INFORMATION.childSelectName(), this.normalClientMaterialSubType(NormalClientMaterialTypeEnum.BASIC_INFORMATION));
        r.put(NormalClientMaterialTypeEnum.OTHERS.childSelectName(), this.normalClientMaterialSubType(NormalClientMaterialTypeEnum.OTHERS));
        //app信息
        r.put("visitPhaseStatus", this.appVisitPhaseStatus());
        r.put("visitTypeStatus", this.appVisitTypeStatus());
        r.put("visitWayStatus", this.appVisitWayStatus());
        r.put("visitRecordStatus", this.appVisitRecordStatus());
        r.put("appAppPaymentStatus", this.appAppPaymentStatus());
        r.put("appAppContractSubTypeStatus", this.appAppContractSubTypeStatus());
        // 费用项
        r.put("financingFeeType", this.financingFeeType());
        r.put("directFinancingFeeType", this.directFinancingFeeType());
        // 间融资料清单
        r.put("fundFinancingMaterialsEnumBank", this.financingFileTypeBank());
        r.put("fundFinancingMaterialsEnumLetter", this.financingFileTypeLetter());
        r.put("fundFinancingMaterialsEnumFactoring", this.financingFileTypeFactoring());
        r.put("fundFinancingMaterialsEnumWorking", this.financingFileTypeWorking());
        r.put("fundFinancingMaterialsEnumCommerce", this.financingFileTypeCommerce());
        r.put("fundFinancingMaterialsEnumOther", this.financingFileTypeOther());
        // 间融还款方式
        r.put("fundFinancingRepayTypeEnum", this.fundFinancingRepayType());
        r.put("blackGrayOrgEnum", this.blackGrayOrgEnume());
        // 运营管报-业务运行分析表-流程查询下拉框枚举
        r.put("yeWuYunXingFenXiProcessTypeList", this.yeWuYunXingFenXiProcessTypeList());
        r.put("settingTimeTypeEnum", this.settingTimeTypeEnum());
        //账户类别
        r.put("fundFinancingAccountTypeEnum", this.fundFinancingAccountTypeEnum());
        // FTP区域划分（区分FTP行业分类为国有产业类和公用事业类（民生消费类））
        r.put("projRegionalDivisionEnum", this.projRegionalDivisionEnumList());
        r.put("projRegionalDivisionPublicAndCivilEnum", this.projRegionalDivisionPublicAndCivilEnumList());
        r.put("associationReportCategoryRefPeriod", this.associationReportCategoryRefPeriod());
        //金融局报送
        r.put("addAssociationReportCategoryEnum", this.addAssociationReportCategoryEnum());//获取金融局报送可以新增的报表类型
        r.put("selectAssociationReportCategoryEnum", this.selectAssociationReportCategoryEnum());//获取金融局报送可以查看的报表类型
        r.put("associationReportStatusEnum", this.associationReportStatusEnum());//报送状态
        r.put("associationProcessStatus", this.associationProcessStatusEnum());//报送流程状态
        r.put("yesOrNoNumberEnum", this.yesOrNoNumberEnum());// 是否数字标识
        r.put("associationMnfrFlagEnum", this.associationMnfrFlagEnum());//厂商系标志
        r.put("associationListFlagEnum", this.associationListFlagEnum());//上市/非上市标志
        r.put("associationShahGtoModeEnum", this.associationShahGtoModeEnum());//股东进入方式
        r.put("yesOrNoChineseEnum", this.yesOrNoChineseEnum());//是否中文标识
        r.put("searchGoalEnum", this.searchGoalBizType());
        r.put("searchStatusEnum",this.searchStatusBizType());
        r.put("applyStatusEnum",this.applyStatusBizType());
        //征信查询
        r.put(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT.childSelectName(), this.creditSearchMaterialSubType(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT));
        r.put(CreditReportMaterialTypeEnum.HANDLER_CREDIT_REPORT.childSelectName(), this.creditSearchMaterialSubType(CreditReportMaterialTypeEnum.HANDLER_CREDIT_REPORT));
        // 租后检查台账-当前状态
        r.put("checkStatus", this.selectAfterLeaseCheckStatus());
        //付款核销-新增付款明细-选择融资编号
        r.put("financingCode", this.getFinancingProduct());
        r.put("filingPolicy",this.filingMaterialsPolicyEnum());
        r.put("afterFilingMaterialsSiteEnum",getFilingMaterialsEnum(FilingMaterialsFilingTypeEnum.SITE.name()));
        r.put("afterFilingMaterialsOffSiteEnum",getFilingMaterialsEnum(FilingMaterialsFilingTypeEnum.OFFSITE.name()));
        r.put("otherFilingStartUseEnum",getOtherFilingStartUseEnum());
        r.put("otherFilingApproveStatusEnum",getOtherFilingApproveStatusEnum());
        r.put("filingMaterialsFilingTypeEnum",filingMaterialsFilingTypeEnum());
        r.put("lprArrangeModeEnum", this.lprArrangeModeEnum());
        return R.ok(r);
    }
    private List<SelectRSP> filingMaterialsFilingTypeEnum(){
        List<FilingMaterialsFilingTypeEnum> collect = Arrays.stream(FilingMaterialsFilingTypeEnum.values()).collect(Collectors.toList());
        collect.removeIf(e -> !StrUtil.equalsAny(e.name(), FilingMaterialsFilingTypeEnum.FUND_DIRECT_FINANCING.name(),
                FilingMaterialsFilingTypeEnum.FUND_FINANCING.name()));
        return collect.stream().map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }


    private List<SelectRSP> getOtherFilingApproveStatusEnum(){
        List<FilingMaterialsProcessStatusEnum> filingMaterialsProcessStatusEnums = FilingMaterialsProcessStatusEnum.listAll();
        filingMaterialsProcessStatusEnums.removeIf(e -> StrUtil.equalsAny(e.name(), FilingMaterialsProcessStatusEnum.APPROVAL_REJECT.name(),
                FilingMaterialsProcessStatusEnum.ABOLISH.name()));
        return filingMaterialsProcessStatusEnums.stream().map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> getOtherFilingStartUseEnum(){
        //项目经理、资产经理、法务经理、运营经理、评审会秘书
        HashSet<String> set = new HashSet<>();
        set.addAll(Arrays.asList(JobEnum.projmanager.name(),JobEnum.legalmanager.name(),JobEnum.operationManagement.name(),
                JobEnum.operationManagementReview.name(),JobEnum.secretaryjury.name(),JobEnum.assetmanagement.name()));
        List<Long> initUserIds = SpringUtil.getBean(SysUserService.class).jobUsers(set);
        UserQuery userQuery = new UserQuery();
        userQuery.setIdList(new ArrayList<>(initUserIds));
        userQuery.setPageSize(Integer.MAX_VALUE);
        List<UserVO> userVOS = userDOMapper.queryPage(userQuery);
        return userVOS.stream().map(e -> new SelectRSP(e.getUserName(), String.valueOf(e.getId()))).collect(Collectors.toList());
    }


    private List<SelectRSP> getFilingMaterialsEnum(String type){
        List<List<SelectRSP>> rspList = SpringUtil.getBean(AfterFilingMaterialsService.class).getOperationsDirDictByFilingType(type).values().stream().collect(Collectors.toList());
        if(CollUtil.isNotEmpty(rspList)){
            return rspList.get(0);
        }
        return Collections.emptyList();
    }

    private List<SelectRSP> lprArrangeModeEnum() {
        List<LprArrangeModeEnum> sort = Arrays.stream(LprArrangeModeEnum.values())
                .sorted(Comparator.comparingInt(LprArrangeModeEnum::getSortCode))
                .collect(Collectors.toList());
        return sort.stream().map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> filingMaterialsPolicyEnum() {
        return Arrays.stream(FilingMaterialsPolicyEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    //查询状态为起息、生效、新建的直融/间融产品编号
    public List<SelectRSP> getFinancingProduct(){
        //查询间融融资编号
        List<SelectRSP> list = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                        .or()
                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name())
                        .or()
                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.NEW.name()))
                .stream().map(e -> new SelectRSP(e.getFinancingCode(), e.getFinancingCode())).collect(Collectors.toList());
        //查询直融融资编号
        List<SelectRSP> list1 = fundDirectFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                        .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                        .or()
                        .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name())
                        .or()
                        .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.NEW.name()))
                .stream().map(e -> new SelectRSP(e.getFinancingCode(), e.getFinancingCode())).collect(Collectors.toList());
        return Stream.concat(list.stream(),list1.stream()).collect(Collectors.toList());
    }



    private List<SelectRSP> selectAfterLeaseCheckStatus () {
        List<AfterLeaseCheckStatusEnum> itemList = ListUtil.of(AfterLeaseCheckStatusEnum.values());
        return itemList.stream().map(e -> new SelectRSP(e.display(), e.getStatusCode())).collect(Collectors.toList());
    }

    private List<SelectRSP> searchStatusBizType() {
        List<CreditSearchStatusEnum> itemList = ListUtil.of(CreditSearchStatusEnum.values());
        return itemList.stream().map(e -> new SelectRSP(e.getDisplay(), e.getName())).collect(Collectors.toList());
    }

    private List<SelectRSP> applyStatusBizType() {
        List<CreditApplyStatusEnum> itemList = ListUtil.of(CreditApplyStatusEnum.values());
        return itemList.stream().map(e -> new SelectRSP(e.getDisplay(), e.getName())).collect(Collectors.toList());
    }


    private List<SelectRSP> searchGoalBizType() {
        List<SearchGoalEnum> itemList = ListUtil.of(SearchGoalEnum.values());
        return itemList.stream().map(e -> new SelectRSP(e.getDisplay(), e.getName())).collect(Collectors.toList());
    }

    private List<SelectRSP> creditSearchMaterialSubType(CreditReportMaterialTypeEnum mainType) {
        List<CreditReportMaterialSubTypeEnum> subTypeList = CreditReportMaterialSubTypeEnum.listSub(mainType);
        if (CollectionUtil.isEmpty(subTypeList)) {
            return Collections.emptyList();
        }
        return subTypeList.stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.getDisplay());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> associationReportCategoryRefPeriod() {
        List<AssociationReportCategoryEnum> itemList = ListUtil.of(AssociationReportCategoryEnum.values());
        return itemList.stream().map(e -> new SelectRSP(e.name(), e.getPeriod().name())).collect(Collectors.toList());
    }

    private List<SelectRSP> addAssociationReportCategoryEnum () {
        Set<String> addReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessAddReportCategoryCodes();
        if (CollectionUtil.isEmpty(addReportCategoryCodes)) {
            return Collections.emptyList();
        }
        List<AssociationReportCategoryEnum> itemList = ListUtil.of(AssociationReportCategoryEnum.values());
        return itemList.stream().filter(e->addReportCategoryCodes.contains(e.name())).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> selectAssociationReportCategoryEnum () {
        Set<String> queryReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessQueryReportCategoryCodes();
        if (CollectionUtil.isEmpty(queryReportCategoryCodes)) {
            return Collections.emptyList();
        }
        List<AssociationReportCategoryEnum> itemList = ListUtil.of(AssociationReportCategoryEnum.values());
        return itemList.stream().filter(e->queryReportCategoryCodes.contains(e.name())).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }


    private List<SelectRSP> projRegionalDivisionEnumList() {
        List<ProjRegionalDivisionEnum> list = ProjRegionalDivisionEnum.projRegionalDivisionEnumList();
        return list.stream().map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> projRegionalDivisionPublicAndCivilEnumList() {
        List<ProjRegionalDivisionEnum> list = ProjRegionalDivisionEnum.projRegionalDivisionPublicAndCivilEnumList();
        return list.stream().map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> yeWuYunXingFenXiProcessTypeList() {
        List<SelectRSP> result = new LinkedList<>();
        result.add(new SelectRSP("立项创建", ProcessModelTypeEnum.ProjEstablishCreateFlow.name()));
        result.add(new SelectRSP("评审创建", ProcessModelTypeEnum.ProjReviewCreateFlow.name()));
        result.add(new SelectRSP("租赁物创建", ProcessModelTypeEnum.LeaseCreateFlow.name()));
        result.add(new SelectRSP("合同创建", ProcessModelTypeEnum.ContractCreateFlow.name()));
        result.add(new SelectRSP("合同付款", ProcessModelTypeEnum.PaymentCreateFlow.name()));
        result.add(new SelectRSP("合同投放", ProcessModelTypeEnum.PaymentActualDetailFlow.name()));
        return result;
    }

    private List<SelectRSP> corporationClientMaterialSubType(CorporationClientMaterialTypeEnum mainType) {
        List<CorporationClientMaterialSubTypeEnum> subTypeList = CorporationClientMaterialSubTypeEnum.listSub(mainType);
        if (CollectionUtil.isEmpty(subTypeList)) {
            return Collections.emptyList();
        }
        return subTypeList.stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.getDisplay());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> blackGrayOrgEnume() {
        return Arrays.stream(BlackGrayOrgEnum.values()).map(e -> new SelectRSP(e.getDesc(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> fundFinancingRepayType() {
        return Stream.of(RepayCalcType.DQYCHBFX, RepayCalcType.QTDQHB, RepayCalcType.BGZHK)
                .map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> financingFileTypeBank() {
        return FundFinancingMaterialsEnum.getMaterialTypeByFinancingType(FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name()).stream()
                .map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> financingFileTypeLetter() {
        return FundFinancingMaterialsEnum.getMaterialTypeByFinancingType(FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name()).stream()
                .map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> financingFileTypeFactoring() {
        return FundFinancingMaterialsEnum.getMaterialTypeByFinancingType(FundFinancingBizTypeEnum.FACTORING_FINANCING.name()).stream()
                .map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> financingFileTypeWorking() {
        return FundFinancingMaterialsEnum.getMaterialTypeByFinancingType(FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name()).stream()
                .map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> financingFileTypeCommerce() {
        return FundFinancingMaterialsEnum.getMaterialTypeByFinancingType(FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name()).stream()
                .map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> financingFileTypeOther() {
        return FundFinancingMaterialsEnum.getMaterialTypeByFinancingType(FundFinancingBizTypeEnum.SYNDICATIONS.name()).stream()
                .map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }


    private List<SelectRSP> financingFeeType() {
        return ExpenseType.indirectExpenseType().stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.display());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> normalClientMaterialSubType(NormalClientMaterialTypeEnum mainType) {
        List<NormalClientMaterialSubTypeEnum> subTypeList = NormalClientMaterialSubTypeEnum.listSub(mainType);
        if (CollectionUtil.isEmpty(subTypeList)) {
            return Collections.emptyList();
        }
        return subTypeList.stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.getDisplay());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> directFinancingFeeType() {
        return ExpenseType.directExpenseType().stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.display());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> contractTextStandard() {
        return ContractTextTypeEnum.listStandard().stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.getDisplay());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> contractTextNonstandard() {
        return ContractTextTypeEnum.listNonstandard().stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.getDisplay());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> cqBillTypeEnum() {
        List<PlatformApiEnum> list = PlatformApiEnum.getCqNeedManualApiList();
        return list.stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.getApiName());
            rsp.setValue(e.name());
            return rsp;
        }).collect(Collectors.toList());
    }

    private List<SelectRSP> ratingBizTypeEnum() {
        return Arrays.asList(RatingBizTypeEnum.values()).stream().map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> businessFlowFinanceCashFlowItemType() {
        List<SelectRSP> res = new LinkedList<>();
        res.addAll(Arrays.stream(FundPlanFlowResultDTO.CashFlowItem.values()).map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList()));
        res.addAll(Arrays.stream(ExpenseType.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList()));
        return res;
    }

    private List<SelectRSP> financingBizType() {
        List<SelectRSP> res = Arrays.stream(FundFinancingBizTypeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
        res.addAll(Arrays.stream(DirectFinancingType.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList()));
        return res;
    }

    private List<SelectRSP> leaseVatInvoiceType() {
        return Stream.of(LeaseVatInvoiceTypeEnum.values()).map(e -> new SelectRSP(e.display(), e.getFieldName())).collect(Collectors.toList());
    }

    private List<SelectRSP> leaseFileOCRStatus() {
        return Stream.of(LeaseFileOCRStatusEnum.values()).map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> leaseVatInvoiceStatus() {
        return Stream.of(LeaseVatInvoiceStatusEnum.values()).map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> blProjectType() {
        return Arrays.asList(ProjectType.STATE_OWNED_ENTERPRISE, ProjectType.OTHER).stream().map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> dynamicFormList() {
        return Stream.of(FlowDynamicFormEnum.values()).map(e -> new SelectRSP(e.name(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> buttonList() {
        return Stream.of(ApprovalButtonTypeEnum.values()).map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> parallelApprovalMethed() {
        SelectRSP oneSelectRSP = new SelectRSP("或签", String.valueOf(ParallelApprovalMethedEnum.ONE.getType()));
        SelectRSP allSelectRSP = new SelectRSP("会签", String.valueOf(ParallelApprovalMethedEnum.ALL.getType()));
        return Arrays.asList(oneSelectRSP, allSelectRSP);
    }

    private List<SelectRSP> approverType() {
        SelectRSP modelTarget = new SelectRSP("模型中指定审批人", String.valueOf(UserDefineTypeEnum.MODEL_TARGET.getType()));
        SelectRSP varTarget = new SelectRSP("根据变量选择", String.valueOf(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType()));
        SelectRSP jobTarget = new SelectRSP("根据岗位选择", String.valueOf(UserDefineTypeEnum.PROCESS_START_JOB.getType()));
        SelectRSP startUser = new SelectRSP("流程发起人", String.valueOf(UserDefineTypeEnum.START_USER.getType()));
        return Arrays.asList(modelTarget, varTarget, jobTarget, startUser);
    }

    private List<SelectRSP> projProcessStatus() {
        return Arrays.stream(ProjProcessState.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    @Override
    public R<List<SelectRSP>> countryList() {
        return R.ok(addressDictionaryMapper.selectList(
                Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, 0).orderByAsc(AddressDictionary::getSort)
        ).stream().map(e -> new SelectRSP(e.getDisplay(), e.getCode())).collect(Collectors.toList()));
    }

    @Override
    public R<List<LeafSelectRSP>> childRegionList(String code) {
        List<LeafSelectRSP> result = new ArrayList<>();
        AddressDictionary ad = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getCode, code).eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode()).orderByAsc(AddressDictionary::getSort));
        if (isNotNull(ad)) {
            result = addressDictionaryMapper.selectList(
                    Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, ad.getId()).eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode())
            ).stream().map(e -> new LeafSelectRSP(e.getDisplay(), e.getCode(), equal(e.getLevel(), 4))).collect(Collectors.toList());
        }
        return R.ok(result);
    }

    @Override
    public R<List<LeafSelectRSP>> rootIndustry() {
        return R.ok(industryTypeMapper.selectList(
                Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getParentId, 0)
        ).stream().map(e -> new LeafSelectRSP(e.getDisplay(), e.getCode(), equal(e.getLevel(), 4))).collect(Collectors.toList()));
    }

    @Override
    public R<List<TreeSelectRSP>> allIndustry() {
        List<TreeSelectRSP> result = new LinkedList<>();
        List<IndustryType> all = industryTypeMapper.selectList(Wrappers.lambdaQuery());
        if (CollectionUtil.isEmpty(all)) {
            return R.ok(Collections.emptyList());
        }
        Map<Long, List<IndustryType>> integerListMap = all.stream().collect(Collectors.groupingBy(IndustryType::getParentId));
        // parent_id为0的是根节点
        List<IndustryType> rootList = integerListMap.get(0L);
        for (IndustryType industryType : rootList) {
            result.add(CommonConvert.toTreeSelectRSP(industryType));
        }
        // 移除根节点
        integerListMap.remove(0L);
        // 拼接叶子节点
        for (TreeSelectRSP treeSelectRSP : result) {
            this.buildTree(integerListMap, treeSelectRSP);
        }
        return R.ok(result);
    }

    @Override
    public R<List<LeafSelectRSP>> childIndustry(String code) {
        List<LeafSelectRSP> result = new ArrayList<>();
        IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getCode, code));
        if (isNotNull(industryType)) {
            result = industryTypeMapper.selectList(
                    Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getParentId, industryType.getId())
            ).stream().map(e -> new LeafSelectRSP(e.getDisplay(), e.getCode(), equal(e.getLevel(), 4))).collect(Collectors.toList());
        }
        return R.ok(result);
    }

    @Override
    public R<List<SelectRSP>> founderList(String name, String job, Boolean sameDept, int pageSize) {
        Example example = new Example(UserDO.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(name)) {
            criteria.andLike("userName", "%" + name + "%");
        }
        //todo 岗位
//        if (StrUtil.isNotEmpty(job)) {
//            Example.Criteria criteria2 = example.createCriteria();
//            for (String s : job.split(",")) {
//                criteria2.orCondition("FIND_IN_SET(\"" + s + "\"," + "jobs)");
//            }
//            example.and(criteria2);
//        }
//        RowBounds rowBounds = new RowBounds(0,pageSize);
        List<UserDO> userDOList = userDOMapper.selectByExample(example);
        List<UserDO> jobUsers = null;
        List<String> jobs = null;
        if (StrUtil.isNotEmpty(job)) {
            jobs = Arrays.stream(job.split(",")).collect(Collectors.toList());
        }
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        List<Long> orgIds = userOrgJobDOMapper.selectOrgIdByUserId(loginInfo.getId());
        if (sameDept) {
//            List<Long> orgIds = userOrgRoleDOMapper.selectOrgIdsByUserId(loginInfo.getId());
            jobUsers = userService.jobsUsers(orgIds, jobs);
        } else {
            jobUsers = userService.jobsUsers(null, jobs);
        }
        List<UserDO> finalJobUsers = jobUsers;
        if (CollectionUtil.isNotEmpty(userDOList) && CollectionUtil.isNotEmpty(finalJobUsers)) {
            Set<Long> finalJobUserIds = finalJobUsers.stream().map(UserDO::getId).collect(Collectors.toSet());
            userDOList = userDOList.stream().filter(x -> finalJobUserIds.contains(x.getId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtil.isEmpty(userDOList)) {
            return R.ok(new ArrayList<>());
        }
        //排序，同部门的放到前面
        List<UserDO> sortUsers = jobUsers = userService.jobsUsers(orgIds, jobs);
        if (null == sortUsers) {
            sortUsers = new ArrayList<>();
        }
        List<Long> sortIdList = sortUsers.stream().map(UserDO::getId).collect(Collectors.toList());
        userDOList.sort((o1, o2) -> {
            if (sortIdList.contains(o1.getId()) && sortIdList.contains(o2.getId())) {
                return 0;
            }
            if (sortIdList.contains(o1.getId())) {
                return -1;
            }
            if (sortIdList.contains(o2.getId())) {
                return 1;
            }
            return 0;
        });
        List<SelectRSP> result = userDOList.stream().map(e -> new SelectRSP(e.getUserName() + "(" + e.getAccount() + ")", e.getId().toString())).collect(Collectors.toList());
        return R.ok(result);
    }

    @Override
    public R<List<SelectRSP>> orgList(String name, Integer type) {
        //List<OrgDO> orgDOList = orgDOMapper.queryByName(name, type);
        Example example = new Example(OrgDO.class);
        if (ObjectUtil.isNotEmpty(type)) {
            example.createCriteria().andEqualTo("type", type);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        List<OrgDO> orgDOList = orgDOMapper.selectByExample(example);
        List<SelectRSP> result = new ArrayList<>();
        if (orgDOList != null) {
            result = orgDOList.stream().map(e -> new SelectRSP(e.getName(), e.getId().toString(), e.getState())).collect(Collectors.toList());
        }
        return R.ok(result);
    }

    @Override
    public R<List<SelectRSP>> newOrgList(String name, Integer type) {
        List<OrgDO> orgDOList = orgDOMapper.queryByName(name, type);
        List<SelectRSP> result = new ArrayList<>();
        Map<Long, List<OrgDO>> map = new HashMap<>();
        List<OrgDO> businessOrgDOList = new ArrayList<>();
        List<OrgDO> otherOrgDOList = new ArrayList<>();
        for (OrgDO orgDO : orgDOList) {
            if (Objects.equals(orgDO.getType(), OrgConstants.BUSINESS_DEPT)) {
                if (orgDO.getLevel().equals(2)) {
                    businessOrgDOList.add(orgDO);
                }
            } else {
                otherOrgDOList.add(orgDO);
            }
        }
        List<OrgDO> res = new ArrayList<>();
        res.addAll(businessOrgDOList);
        res.addAll(otherOrgDOList);

        if (!res.isEmpty()) {
            result = res.stream().map(e -> new SelectRSP(e.getName(), e.getId().toString())).collect(Collectors.toList());
        }
        return R.ok(result);
    }

    @Override
    public R<List<SelectRSP>> orgListByUserId(Long userId) {
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).getSpecificUserDeptList(userId);
        if (CollectionUtil.isEmpty(orgList)) {
            return R.ok(Collections.emptyList());
        }
        List<SelectRSP> result = orgList.stream().map(e -> new SelectRSP(e.getName(), e.getId().toString())).collect(Collectors.toList());
        return R.ok(result);
    }

    @Override
    public R<List<SelectRSP>> businessheaderListByDeptId(Long deptId) {
        List<UserDO> userList = SpringUtil.getBean(SysUserService.class).listSpecificOrgJobUser(deptId, JobEnum.businesshead.name());
        if (CollectionUtil.isEmpty(userList)) {
            return R.ok(Collections.emptyList());
        }
        List<SelectRSP> result = userList.stream().map(e -> new SelectRSP(e.getUserName(), e.getId().toString())).collect(Collectors.toList());
        return R.ok(result);
    }

    @Override
    public R<List<SelectRSP>> levelOrgList(String name, Integer type, Integer level) {
        Example example = new Example(OrgDO.class);
        if (ObjectUtil.isNotEmpty(type)) {
            example.createCriteria().andEqualTo("type", type);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        if (ObjectUtil.isNotEmpty(level)) {
            example.createCriteria().andEqualTo("level", level);
        }
        List<OrgDO> orgDOList = orgDOMapper.selectByExample(example);
        if (ObjectUtil.isEmpty(level)) {
            orgDOList = orgDOList.stream().filter(e -> Objects.equals(e.getLevel(), 2)).collect(Collectors.toList());
        }
        List<SelectRSP> result = new ArrayList<>();
        if (orgDOList != null) {
            result = orgDOList.stream().map(e -> new SelectRSP(e.getName(), e.getId().toString(), e.getState())).collect(Collectors.toList());
        }
        return R.ok(result);
    }

    private void buildTree(Map<Long, List<IndustryType>> industryMap, TreeSelectRSP parentNode) {
        // 找到parentNode下所有的子节点
        List<IndustryType> industryTypeList = industryMap.get(parentNode.getId());
        if (CollectionUtil.isEmpty(industryTypeList)) {
            return;
        }
        for (IndustryType industryType : industryTypeList) {
            TreeSelectRSP treeSelectRSP = CommonConvert.toTreeSelectRSP(industryType);
            parentNode.getChildren().add(treeSelectRSP);
            this.buildTree(industryMap, treeSelectRSP);
        }
    }

    private List<SelectRSP> moduleType() {
        return Arrays.stream(InfoModule.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> operationType() {
        return Arrays.stream(InfoOperation.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    public List<SelectRSP> enumType(String dictKey) {
        return generalDictionaryMapper.selectList(
                        Wrappers.<GeneralDictionary>lambdaQuery().eq(GeneralDictionary::getDictKey, dictKey).orderByAsc(GeneralDictionary::getSort))
                .stream().map(e -> new SelectRSP(e.getDisplay(), e.getCode())).collect(Collectors.toList());
    }

    public List<SelectRSP> enumType(List<GeneralDictionary> list) {
        return list.stream().map(item -> new SelectRSP(item.getDisplay(), item.getCode())).collect(Collectors.toList());
    }

    private List<SelectRSP> addressType() {
        return Arrays.stream(CorpAddressType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> subjectQuarterType() {
        return Arrays.stream(SubjectQuarterType.values()).map(e -> new SelectRSP(e.display, String.valueOf(e.value))).collect(Collectors.toList());
    }

    private List<SelectRSP> projEstablishBizType() {
        return Arrays.stream(ProjectBizType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> projEstablishStatus() {
        return Arrays.stream(RecordStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> projectBizType() {
        return Arrays.stream(ProjectBizType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> projReviewStatus() {
        return Arrays.stream(RecordStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> processStatus() {
        return Stream.of(ProcessBusinessStatusEnum.RUNNING, ProcessBusinessStatusEnum.PASS, ProcessBusinessStatusEnum.REJECT, ProcessBusinessStatusEnum.CANCEL, ProcessBusinessStatusEnum.PASS_ALL, ProcessBusinessStatusEnum.REJECT_ALL).map(e -> new SelectRSP(e.getDisplay(), e.getType().toString())).collect(Collectors.toList());
    }

    private List<SelectRSP> taskStatus() {
        return Arrays.stream(TaskBusinessStatusEnum.values()).map(e -> new SelectRSP(e.getDisplay(), e.getStatus().toString())).collect(Collectors.toList());
    }

    private List<SelectRSP> processModelType() {
        //        return Arrays.stream(ProcessModelTypeEnum.values()).filter(ProcessModelTypeEnum::getFormal).map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
        List<ProcessModelTypeEnum> list = Arrays.asList(ProcessModelTypeEnum.values());
        List<ProcessModelTypeEnum> typeEnumList = list.stream().filter(x -> x.getStatus() == 1).collect(Collectors.toList());
        List<ProcessModelTypeEnum> typeEnums = typeEnumList.stream().filter(x -> x.getStatus() == 1).filter(x -> x.getParentCode().equals("0")).collect(Collectors.toList());
        List<SelectRSP> res = typeEnums.stream().map(x -> {
            SelectRSP selectRSP = new SelectRSP();
            selectRSP.setLabel(x.getDisplay());
            selectRSP.setValue(x.name());
            List<SelectRSP> rspList = typeEnumList.stream().filter(o -> o.getParentCode().equals(x.getId())).collect(Collectors.toList()).stream().map(m -> {
                SelectRSP selectRSPChildren = new SelectRSP();
                selectRSPChildren.setLabel(m.getDisplay());
                selectRSPChildren.setValue(m.name());
                return selectRSPChildren;
            }).collect(Collectors.toList());
            selectRSP.setChildren(rspList);
            return selectRSP;
        }).collect(Collectors.toList());
        return res;
    }

    private List<SelectRSP> commonProcessStatus() {
        return Arrays.stream(ProcessStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> LPRTypeEnum() {
        return Arrays.stream(LPRTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> recordTypeEnum() {
        return Arrays.stream(RecordTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> marginWriteOffStatusEnum() {
        return Arrays.stream(MarginWriteOffStatusEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> lesseeypeEnum() {
        return Arrays.stream(LesseeTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> noticeTypeEnum() {
        return Arrays.stream(NoticeTypeEnum.values()).map(e -> new SelectRSP(e.getName(), String.valueOf(e.getCode()))).collect(Collectors.toList());
    }

    private List<SelectRSP> riskControlOpinionHandleStatus() {
        return Arrays.stream(RiskControlOpinionHandleStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> jzdReportCreateType() {
        return Arrays.stream(JzdReportCreateType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> jzdReportStatus() {
        return Arrays.stream(JzdReportStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> cashFlowItemEnum() {
        //业务角度首期利息属于租金
        return Arrays.stream(CashFlowItemEnum.values()).filter(e -> e != CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST)
                .map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> confirmIncomeEnum() {
        return Arrays.stream(ConfirmIncomeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> dashboardOperationTermEnum() {
        return Arrays.stream(DashboardOperationTermEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> paymentCurrencyStatusEnum() {
        return Arrays.stream(PaymentCurrencyStatusEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> clientLevelEnum() {
        return Arrays.stream(ClientLevelEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> clientProjArchiveEnum() {
        return Arrays.stream(ClientProjArchiveEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> appVisitPhaseStatus() {
        return Arrays.stream(VisitPhaseStatus.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> appVisitTypeStatus() {
        return Arrays.stream(VisitTypeStatus.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> appVisitWayStatus() {
        return Arrays.stream(VisitWayStatus.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> appVisitRecordStatus() {
        return Arrays.stream(VisitRecordStatus.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> appAppPaymentStatus() {
        return Arrays.stream(AppPaymentStatus.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> appAppContractSubTypeStatus() {
        return Arrays.stream(AppContractSubTypeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> contractTypeEnum() {
        //业务角度首期利息属于租金
        return Arrays.stream(ContractTypeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> fundFinancingTimeLimitTypeEnum() {
        return Arrays.stream(FundFinancingTimeLimitTypeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> settingTimeTypeEnum() {
        return Arrays.stream(SettingTimeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> resolutionTypeEnum() {
        return Arrays.stream(ResolutionTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> paymentTypeEnum() {
        return Arrays.stream(PaymentTypeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> fundFinancingAccountTypeEnum() {
        return Arrays.stream(FundFinancingAccountTypeEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> fundFinancingNewStatusEnum() {
        return Arrays.stream(FundFinancingNewStatusEnum.values()).map(e -> new SelectRSP(e.display(), e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> associationReportStatusEnum() {
        return Arrays.stream(AssociationReportStatusEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> associationProcessStatusEnum() {
        return Arrays.stream(AssociationProcessStatusEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> yesOrNoNumberEnum() {
        return Arrays.stream(YesOrNoNumberEnum.values()).map(e -> new SelectRSP(e.getChinese(), e.getCode().toString())).collect(Collectors.toList());
    }

    private List<SelectRSP> associationMnfrFlagEnum() {
        return Arrays.stream(AssociationMnfrFlagEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> associationListFlagEnum() {
        return Arrays.stream(AssociationListFlagEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> associationShahGtoModeEnum() {
        return Arrays.stream(AssociationShahGtoModeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }

    private List<SelectRSP> yesOrNoChineseEnum() {
        return Arrays.stream(YesOrNoChineseEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
    }


//


//    private List<SelectRSP> genderType() {
//        return Arrays.stream(GenderType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> clientType() {
//        return Arrays.stream(ClientType.values()).map(e -> new SelectRSP(e.getDisplay(), e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> shareholderType() {
//        return Arrays.stream(ShareholderType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> materialsType() {
//        return Arrays.stream(MaterialsType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> marriageType() {
//        return Arrays.stream(MarriageType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> normalMaterialsType() {
//        return Arrays.stream(NormalMaterialsType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> orgScaleType() {
//        return Arrays.stream(OrgScaleType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> subjectReportType() {
//        return Arrays.stream(SubjectReportType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> subjectItemDisplayDimension() {
//        return Arrays.stream(SubjectItemDisplayDimension.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> governmentSubjectItemType() {
//        return Arrays.stream(GovernmentSubjectItemType.values()).map(e -> new SelectRSP(e.sheetName, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> subjectItemType() {
//        return Arrays.stream(SubjectItemType.values()).map(e -> new SelectRSP(e.sheetName, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> relationshipType() {
//        return Arrays.stream(RelationshipType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> clientStatus() {
//        return Arrays.stream(ClientStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> clientProcessStatus() {
//        return Arrays.stream(ClientProcessStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> projEstablishApprovalType() {
//        return Arrays.stream(ProjEstablishApprovalType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> leaseType() {
//        return Arrays.stream(LeaseType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> factoringType() {
//        return Arrays.stream(FactoringType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> projSourceType() {
//        return Arrays.stream(ProjSourceType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> rePayType() {
//        return Arrays.stream(RePayType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> rentalCalcType() {
//        return Arrays.stream(RentalCalcType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> rateType() {
//        return Arrays.stream(RateType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> payType() {
//        return Arrays.stream(PayType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> projectType() {
//        return Arrays.stream(ProjectType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> projReviewInfoModule() {
//        return Arrays.stream(ProjReviewInfoModule.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> projReviewProcessStatus() {
//        return Arrays.stream(ProjReviewProcessStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> bizClientType() {
//        return Arrays.stream(BizClientType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> lendingMaterialType() {
//        return Arrays.stream(LendingMaterialType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> paymentMethod() {
//        return Arrays.stream(PaymentMethod.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> writeOffStatus() {
//        return Arrays.stream(WriteOffStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> questionAnswer() {
//        return Arrays.stream(QuestionAnswer.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> contractStatus() {
//        return Arrays.stream(ContractStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> paymentWriteOffStatus() {
//        return Arrays.stream(PaymentWriteOffStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> repayRateEnum() {
//        return Arrays.stream(RepayRateEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> guaranteeMethodEnum() {
//        return Arrays.stream(GuaranteeMethodEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> cashFlowItemEnum() {
//        return Arrays.stream(CashFlowItemEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> previewTypeEnum() {
//        return Arrays.stream(PreviewTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> collectionRecordWriteOffStatus() {
//        return Arrays.stream(CollectionRecordWriteOffStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> collectionWriteOffStatusEnum() {
//        return Arrays.stream(CollectionWriteOffStatusEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> projItemStatus() {
//        return Arrays.stream(ProjItemStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> contractChangeTypeEnum() {
//        return Arrays.stream(ContractChangeTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> contractSettlePlanTypeEnum() {
//        return Arrays.stream(ContractSettlePlanTypeEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
//    private List<SelectRSP> contractProcessStatusEnum() {
//        return Arrays.stream(ContractProcessStatusEnum.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList());
//    }
}
