package cn.zswltech.mithras.application.orchestration.projectprocess.projpricing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.*;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoAddByGroupCreditREQ;
import cn.zswltech.mithras.dto.projreview.meet.ProjReviewMeetMinuteBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projreview.meet.ProjReviewMeetMinuteBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.ftp.convert.CommonConvert;
import cn.zswltech.mithras.projectprocess.convert.projpricing.ProjPricingBaseInfoConverter;
import cn.zswltech.mithras.projectprocess.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.credit.groupcredit.review.enums.GroupCreditReviewProcessStatus;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.basedata.persistence.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.dto.persistence.ProjPricingListSelectDTO;
import cn.zswltech.mithras.basedata.persistence.model.AddressDictionary;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.*;
import cn.zswltech.mithras.projectprocess.model.projreview.*;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.customer.event.ClientViewAuthorityEvent;
import cn.zswltech.mithras.system.application.projectcode.ProjCodeStoreService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientAuthorityService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.application.orchestration.client.ProjClientRoleService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.ExecutionService;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.FlowQueryExtraService;
import cn.zswltech.mithras.application.orchestration.groupcredit.review.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.groupcredit.review.GroupCreditReviewService;
import cn.zswltech.mithras.customer.versioning.CorpCommerceInfoLibService;
import cn.zswltech.mithras.customer.versioning.handler.impl.CorpCommerceInfoLibHandlerImpl;
import cn.zswltech.mithras.projectprocess.versioning.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingUpdateAdvice;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.foundation.state.ProjContext;
import cn.zswltech.mithras.foundation.state.ProjEvent;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.projectprocess.application.statemachine.ProjPricingStateMachine;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ONLY_BIZ_DEPT_DO;
import static cn.zswltech.mithras.foundation.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.*;
import static cn.zswltech.mithras.foundation.util.Util.errMithras;

/**
 * <p>
 * 项目定价基本信息表 服务实现类
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Slf4j
@Service
public class ProjPricingBaseInfoService extends ServiceImpl<ProjPricingBaseInfoMapper, ProjPricingBaseInfo> implements ProjPricingUpdateAdvice{

    @Resource
    private ProjPricingBaseInfoConverter baseInfoConverter;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjPricingService projPricingService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private ProjEstablishPriceService establishPriceService;
    @Resource
    private CorpCommerceInfoService commerceInfoService;
    @Resource
    private CorpCommerceInfoLibHandlerImpl corpCommerceInfoLibHandler;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjPricingPriceConverter priceConverter;
    @Resource
    private ProjPricingPriceService pricingPriceService;
    @Resource
    private ProjPricingCashFlowPlanService pricingCashFlowPlanService;
    @Resource
    private FlowQueryExtraService flowQueryExtraService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private ProjCodeStoreService projCodeStoreService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjPricingStateMachine stateMachine;
    @Resource
    private ExecutionService executionService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ProjReviewMeetMinuteBaseInfoService projReviewMeetMinuteBaseInfoService;

    public Long createByProjReview(Long projReviewId) {
        if (isNotNull(baseMapper.selectOne(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .ne(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name())
                .eq(ProjPricingBaseInfo::getProjReviewId, projReviewId)))) {
            throw new MithrasException("项目评审存在未关闭的项目定价数据");
        }
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("项目评审记录不存在");
        }
        if (!RecordStatus.TAKE_EFFECT.name().equals(projReviewBaseInfo.getProjReviewStatus())) {
            throw new MithrasException("项目评审未生效");
        }
        if (ProjProcessState.CHANGING_UN_SUBMIT.name().equals(projReviewBaseInfo.getProjReviewProcessStatus()) || ProjProcessState.CHANGING_UNDER_APPROVAL.name().equals(projReviewBaseInfo.getProjReviewProcessStatus())) {
            throw new MithrasException("项目评审处于变更未提交或者变更审批中，无法选取");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(projReviewBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            throw new MithrasException("只有项目经理可以创建项目定价");
        }
        // 获取项目评审会议纪要
        ProjReviewMeetMinuteBaseInfoDetailREQ projReviewMeetMinuteBaseInfoDetailREQ = new ProjReviewMeetMinuteBaseInfoDetailREQ();
        projReviewMeetMinuteBaseInfoDetailREQ.setProjReviewId(projReviewId);
        projReviewMeetMinuteBaseInfoDetailREQ.setIsEffect(YesOrNoNumberEnum.YES.getCode());
        ProjReviewMeetMinuteBaseInfoDetailRSP meetMinuteDetail = projReviewMeetMinuteBaseInfoService.detail(projReviewMeetMinuteBaseInfoDetailREQ);
        // 用项目评审数据生成项目定价数据
        ProjReviewAocPrice reviewAocPrice = SpringContextHolder.getBean(ProjReviewAocPriceService.class).getByProjectId(projReviewId);
        ProjReviewLeasePrice reviewLeasePrice = SpringContextHolder.getBean(ProjReviewLeasePriceService.class).getByProjectId(projReviewId);
        ProjReviewFactoringPrice reviewFactoringPrice = SpringContextHolder.getBean(ProjReviewFactoringPriceService.class).getByProjectId(projReviewId);
        List<ProjReviewCashFlowPlan> projReviewCashFlowPlanList = SpringContextHolder.getBean(ProjReviewCashFlowPlanService.class).listByProjReviewId(projReviewId, null);
        // 拉取评审基本信息转存
        ProjPricingBaseInfo info = baseInfoConverter.reviewLibToBasePricing(BeanUtil.copyProperties(projReviewBaseInfo, ProjReviewBaseInfoLib.class));
        info.reset();
        info.setProjPricingStatus(NEW.name());
        info.setProjPricingProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
        if (Objects.nonNull(projReviewBaseInfo.getGroupCreditReviewId())) {
            info.setGroupCreditReviewId(projReviewBaseInfo.getGroupCreditReviewId());
            info.setRelationDataType(ReviewRelationDataType.GROUP_CREDIT_REVIEW.name());
        } else {
            info.setProjEstablishId(projReviewBaseInfo.getProjEstablishId());
            info.setRelationDataType(ReviewRelationDataType.PROJ_ESTABLISH.name());
        }
        info.setProjReviewId(projReviewId);
        // 填充评审会纪要信息
        if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
            info.setLesseeInfo(meetMinuteDetail.getLesseeInfo());
            info.setFundsPurpose(meetMinuteDetail.getFundsPurpose());
        }
        baseMapper.insert(info);
        // 拉取报价方案转存
        if (ObjectUtil.isNotEmpty(reviewAocPrice)) {
            info.setDeclaredAmount(reviewAocPrice.getApplyCreditAmount());
            ProjPricingAocPrice projPricingAocPrice = priceConverter.reviewAocRspToPricingEntity(reviewAocPrice);
            projPricingAocPrice.setProjectId(info.getId());
            if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                projPricingAocPrice.setAocCreditTerm(meetMinuteDetail.getLeaseTerm());
                projPricingAocPrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
            }
            projPricingAocPrice.setProjectApprovalAmount(reviewAocPrice.getProjectApprovalAmount());
            pricingPriceService.add(projPricingAocPrice);
        }
        if (ObjectUtil.isNotEmpty(reviewLeasePrice)) {
            info.setDeclaredAmount(reviewLeasePrice.getApplyCreditAmount());
            ProjPricingLeasePrice projPricingLeasePrice = priceConverter.reviewLeaseRspToPricingEntity(reviewLeasePrice);
            projPricingLeasePrice.setProjectId(info.getId());
            if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                projPricingLeasePrice.setLeaseMonthCount(meetMinuteDetail.getLeaseTerm());
                projPricingLeasePrice.setDownPayment(Optional.ofNullable(meetMinuteDetail.getDownPayment()).orElse(0L));
                projPricingLeasePrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
                projPricingLeasePrice.setNominalPrice(Optional.ofNullable(meetMinuteDetail.getNominalPrice()).orElse(0L));
            }
            projPricingLeasePrice.setProjectApprovalAmount(reviewLeasePrice.getProjectApprovalAmount());
            pricingPriceService.add(projPricingLeasePrice);
        }
        if (ObjectUtil.isNotEmpty(reviewFactoringPrice)) {
            info.setDeclaredAmount(reviewFactoringPrice.getApplyCreditAmount());
            ProjPricingFactoringPrice projPricingFactoringPrice = priceConverter.reviewFactoringRspToPricingEntity(reviewFactoringPrice);
            projPricingFactoringPrice.setProjectId(info.getId());
            if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                projPricingFactoringPrice.setFactoringCreditTerm(meetMinuteDetail.getLeaseTerm());
                projPricingFactoringPrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
            }
            projPricingFactoringPrice.setProjectApprovalAmount(reviewFactoringPrice.getProjectApprovalAmount());
            pricingPriceService.add(projPricingFactoringPrice);
        }
        if (CollectionUtils.isNotEmpty(projReviewCashFlowPlanList)){
            List<ProjPricingCashFlowPlan> reviewCashFlowPlanList = projReviewCashFlowPlanList.stream().map(item -> {
                ProjPricingCashFlowPlan pricingCashFlowPlan = BeanUtil.copyProperties(item, ProjPricingCashFlowPlan.class);
                pricingCashFlowPlan.setId(null);
                pricingCashFlowPlan.setProjectId(info.getId());
                return pricingCashFlowPlan;
            }).collect(Collectors.toList());
            pricingCashFlowPlanService.saveBatch(reviewCashFlowPlanList);
        }
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_PRICING,
                        info.getId().toString(),
                        "项目定价-创建")
                )
        );
        return info.getId();
    }

    public PageR<ProjPricingBaseInfoListRSP> list(ProjPricingBaseInfoListREQ req) {
        ProjPricingListSelectDTO dto = baseInfoConverter.listREQtoSelectDTO(req);
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        Page<ProjPricingBaseInfo> pageData = baseMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        List<ProjPricingBaseInfoListRSP> resPage = new ArrayList<>(pageData.getRecords().size());
        for (ProjPricingBaseInfo record : pageData.getRecords()) {
            ProjPricingBaseInfoListRSP rsp = baseInfoConverter.entityToListRsp(record);
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                sysUserIds.addAll(rsp.getProjCosponsorUserIds());
            }
            sysUserIds.add(rsp.getProjSponsorUserId());
            clientIds.add(rsp.getClientId());
            deptIds.add(rsp.getBizDeptId());
            resPage.add(rsp);
        }
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        for (ProjPricingBaseInfoListRSP rsp : resPage) {
            rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get)
                        .collect(Collectors.toList()));
            }
            rsp.setClientName(clientMap.get(rsp.getClientId()));
        }
        return PageR.of(pageData, resPage);
    }

    public ProjPricingBaseInfoDetailRSP detail(Long id, String processInstanceId) {
        ProjPricingBaseInfo record = baseMapper.selectById(id);
        // 填充风险敞口
        record.setLesseeInfo(projPricingService.setExposureRisk(record.getLesseeInfo()));
        record.setPledgorInfo(projPricingService.setExposureRisk(record.getPledgorInfo()));
        record.setGuaranteeInfo(projPricingService.setExposureRisk(record.getGuaranteeInfo()));
        record.setMortgagorInfo(projPricingService.setExposureRisk(record.getMortgagorInfo()));
        record.setDebtorInfo(projPricingService.setExposureRisk(record.getDebtorInfo()));
        record.setCreditorInfo(projPricingService.setExposureRisk(record.getCreditorInfo()));

        ProjPricingBaseInfoDetailRSP rsp = baseInfoConverter.entityToDetailRSP(record);
        //fill name
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        sysUserIds.add(rsp.getBizDeptLeaderId());
        sysUserIds.add(rsp.getBizDivisionLeaderId());
        sysUserIds.add(rsp.getRiskControlManagerId());
        sysUserIds.add(rsp.getLegalManagerUserId());
        deptIds.add(rsp.getBizDeptId());
        clientIds.add(rsp.getClientId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        rsp.setClientName(clientMap.get(rsp.getClientId()));
        rsp.setAssignor(record.getAssignor());
        rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setBizDeptLeaderName(sysUserMap.get(rsp.getBizDeptLeaderId()));
        rsp.setBizDivisionLeaderName(sysUserMap.get(rsp.getBizDivisionLeaderId()));
        rsp.setRiskControlManagerName(sysUserMap.get(rsp.getRiskControlManagerId()));
        rsp.setLegalManagerName(sysUserMap.get(rsp.getLegalManagerUserId()));
        if (ObjectUtil.isNotEmpty(processInstanceId)) {
            ProcessResp process = projPricingService.findProcessByProcessInstanceId(processInstanceId);
            rsp.setProcessModel(process.getModelKey());
        }
        rsp.setIsProjSponsor(Objects.equals(AccountUtil.getLoginInfo().getId(), rsp.getProjSponsorUserId()));
        // 填充客户风控行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(record.getClientId());
        rsp.setRiskControlIndustryClassify(Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null));
        if (ObjectUtil.isNotEmpty(rsp)) {
            Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, ListUtil.toList(rsp.getProvince(), rsp.getCity(), rsp.getDistrict())))
                    .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
            StringBuilder st = new StringBuilder();
            if (ObjectUtil.isNotNull(nameMap.get(rsp.getProvince()))) {
                st.append(nameMap.get(rsp.getProvince()));
            }
            if (ObjectUtil.isNotNull(nameMap.get(rsp.getCity()))) {
                st.append(nameMap.get(rsp.getCity()));
            }
            if (ObjectUtil.isNotNull(nameMap.get(rsp.getDistrict()))) {
                st.append(nameMap.get(rsp.getDistrict()));
            }
            rsp.setAreaName(st.toString());
            //填充主体名称
            if(ObjectUtil.isNotEmpty(rsp.getEvaluationSubjectId())){
                rsp.setEvaluationSubjectName(id2NameService.clientId2NameSingle(rsp.getEvaluationSubjectId()));
            }
        }
        ProcessResp processResp = projPricingService.findRelatedProcess(id);
        rsp.setCurAssigneeIds(processResp != null ? processResp.getCurAssigneeIds() : null);
        return rsp;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public ProjPricingBaseInfoAddRSP add(ProjPricingBaseInfoAddREQ req) {
        if (isNotNull(baseMapper.selectOne(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .ne(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name())
                .eq(ProjPricingBaseInfo::getProjName, req.getProjName())))) {
            throw new MithrasException("该项目名称存在未关闭的项目定价数据");
        }
        ProjEstablishBaseInfo esBaseInfo = establishBaseInfoService.getById(req.getProjEstablishId());
        if (!RecordStatus.TAKE_EFFECT.name().equals(esBaseInfo.getProjEstablishStatus())) {
            throw new MithrasException("该项目的立项未生效");
        }
        if (ProjProcessState.CHANGING_UN_SUBMIT.name().equals(esBaseInfo.getProjEstablishProcessStatus()) || ProjProcessState.CHANGING_UNDER_APPROVAL.name().equals(esBaseInfo.getProjEstablishProcessStatus())) {
            throw new MithrasException("当前立项数据处于变更未提交或者变更审批中，无法选取");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(esBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            throw new MithrasException("只有项目经理可以创建项目定价");
        }
        ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getReviewByPricing(req.getProjEstablishId(), null, req.getProjName());
        ProjReviewBaseInfoLib reviewBaseInfoLib = null;
        if(reviewBaseInfo != null) {
            reviewBaseInfoLib = SpringContextHolder.getBean(ProjReviewBaseInfoLibService.class).getEffectLatestOne(reviewBaseInfo.getId());
        }
        //获取项目评审会议纪要
        ProjReviewMeetMinuteBaseInfoDetailREQ projReviewMeetMinuteBaseInfoDetailREQ = new ProjReviewMeetMinuteBaseInfoDetailREQ();
        if (reviewBaseInfo != null) {
            projReviewMeetMinuteBaseInfoDetailREQ.setProjReviewId(reviewBaseInfo.getId());
        }
        projReviewMeetMinuteBaseInfoDetailREQ.setIsEffect(YesOrNoNumberEnum.YES.getCode());
        ProjReviewMeetMinuteBaseInfoDetailRSP meetMinuteDetail = projReviewMeetMinuteBaseInfoService.detail(projReviewMeetMinuteBaseInfoDetailREQ);

        ProjPricingBaseInfo info;
        List<MaterialsList> copyFileList = null;
        if(reviewBaseInfoLib == null) {
            ProjEstablishPriceDetailRSP priceDetail = establishPriceService.detail(req.getProjEstablishId());
            // 拉取立项基本信息转存
            info = establishToPricing(esBaseInfo);
            info.setDeclaredAmount(priceDetail.getDeclaredAmount());
            // 获取客户信息风控行业分类
            Client client = clientService.getById(info.getClientId());
            Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
            CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
            Assert.notNull(corpCommerceInfoLib, () -> MithrasException.newException("无生效的客户工商信息数据"));
            Assert.notBlank(corpCommerceInfoLib.getRiskControlIndustryClassify(), () -> MithrasException.newException(String.format("请先至客户模块维护<%s>的“风控行业分类”，否则无法判断该项目是否需经董事会审议！", client.getClientName())));
            //更新业务部门负责人，业务分管领导
            OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(info.getBizDeptId());
            if (ObjectUtil.isNotEmpty(bizOrgDO)) {
                info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.businesshead.name()));
                info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.leaderincharge.name()));
            }
            info.setProjPricingStatus(NEW.name());
            info.setProjPricingProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
            info.setProjEstablishId(esBaseInfo.getId());
            info.setRelationDataType(ReviewRelationDataType.PROJ_ESTABLISH.name());
            if (reviewBaseInfo != null) {
                info.setProjReviewId(reviewBaseInfo.getId());
            }
            info.setUpdateTime(null);
            info.setCreateTime(null);
            // FTP行业分类尝试初始化
            info.setFtpIndustryCategory(Optional.ofNullable(CommonConvert.toFtpIndustryCategory(corpCommerceInfoLib.getRiskControlIndustryClassify())).map(Enum::name).orElse(null));
            if (Objects.equals(corpCommerceInfoLib.getRiskControlIndustryClassify(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name())) {
                info.setFtpIndustryCategory(FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES.name());
            } else if (CharSequenceUtil.equalsAny(corpCommerceInfoLib.getRiskControlIndustryClassify(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name())) {
                info.setFtpIndustryCategory(FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION.name());
            }
            //填充评审会纪要信息
            if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                info.setLesseeInfo(meetMinuteDetail.getLesseeInfo());
                info.setFundsPurpose(meetMinuteDetail.getFundsPurpose());
            }
            baseMapper.insert(info);

            // 拉取报价方案转存
            if (ObjectUtil.isNotEmpty(priceDetail.getAocPriceRSP())) {
                ProjPricingAocPrice aocPrice = priceConverter.esAocRspToEntity(priceDetail.getAocPriceRSP());
                aocPrice.setProjectId(info.getId());
                if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                    aocPrice.setAocCreditTerm(meetMinuteDetail.getLeaseTerm());
                    aocPrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
                }
                pricingPriceService.add(aocPrice);
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getFactoringPriceRSP())) {
                ProjPricingFactoringPrice factoringPrice = priceConverter.esFactoringRspToEntity(priceDetail.getFactoringPriceRSP());
                factoringPrice.setProjectId(info.getId());
                if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                    factoringPrice.setFactoringCreditTerm(meetMinuteDetail.getLeaseTerm());
                    factoringPrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
                }
                pricingPriceService.add(factoringPrice);
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getLeasePriceRSP())) {
                ProjPricingLeasePrice leasePrice = priceConverter.esLeaseRspToEntity(priceDetail.getLeasePriceRSP());
                leasePrice.setProjectId(info.getId());
                if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                    leasePrice.setLeaseMonthCount(meetMinuteDetail.getLeaseTerm());
                    leasePrice.setDownPayment(Optional.ofNullable(meetMinuteDetail.getDownPayment()).orElse(0L));
                    leasePrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
                    leasePrice.setNominalPrice(Optional.ofNullable(meetMinuteDetail.getNominalPrice()).orElse(0L));
                }
                pricingPriceService.add(leasePrice);
            }
//            // 从项目立项拷贝客户文件
//            try {
//                // 找对应项目立项的客户资料
//                String sourceBusinessKey = String.format("%s@%s", BusinessModuleEnum.PROJ_ESTABLISH.name(), esBaseInfo.getId());
//                copyFileList = SpringUtil.getBean(MaterialsListService.class).listBySourceBusinessKey(BusinessModuleEnum.CLIENT, sourceBusinessKey);
//            } catch (Exception e) {
//                log.error("项目定价拷贝来自项目立项的客户资料发生异常[id:{}]", esBaseInfo.getId());
//            }
        } else {
            Long projId = reviewBaseInfo.getId();
            ProjReviewAocPrice reviewAocPrice = SpringContextHolder.getBean(ProjReviewAocPriceService.class).getByProjectId(projId);
            ProjReviewLeasePrice reviewLeasePrice = SpringContextHolder.getBean(ProjReviewLeasePriceService.class).getByProjectId(projId);
            ProjReviewFactoringPrice reviewFactoringPrice = SpringContextHolder.getBean(ProjReviewFactoringPriceService.class).getByProjectId(projId);
            List<ProjReviewCashFlowPlan> projReviewCashFlowPlanList = SpringContextHolder.getBean(ProjReviewCashFlowPlanService.class).listByProjReviewId(projId, null);
            // 拉取评审基本信息转存
            info = baseInfoConverter.reviewLibToBasePricing(reviewBaseInfoLib);
            info.setProjPricingStatus(NEW.name());
            info.setProjPricingProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
            info.setProjEstablishId(esBaseInfo.getId());
            info.setRelationDataType(ReviewRelationDataType.PROJ_ESTABLISH.name());
            info.setProjReviewId(projId);
            info.setId(null);
            info.setUpdateTime(null);
            info.setCreateTime(null);
            //填充评审会纪要信息
            if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                info.setLesseeInfo(meetMinuteDetail.getLesseeInfo());
                info.setFundsPurpose(meetMinuteDetail.getFundsPurpose());
            }
            baseMapper.insert(info);
            // 拉取报价方案转存
            if (ObjectUtil.isNotEmpty(reviewAocPrice)) {
                info.setDeclaredAmount(reviewAocPrice.getApplyCreditAmount());
                ProjPricingAocPrice projPricingAocPrice = priceConverter.reviewAocRspToPricingEntity(reviewAocPrice);
                projPricingAocPrice.setProjectId(info.getId());
                if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                    projPricingAocPrice.setAocCreditTerm(meetMinuteDetail.getLeaseTerm());
                    projPricingAocPrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
                }
                projPricingAocPrice.setProjectApprovalAmount(reviewAocPrice.getProjectApprovalAmount());
                pricingPriceService.add(projPricingAocPrice);
            }
            if (ObjectUtil.isNotEmpty(reviewLeasePrice)) {
                info.setDeclaredAmount(reviewLeasePrice.getApplyCreditAmount());
                ProjPricingLeasePrice projPricingLeasePrice = priceConverter.reviewLeaseRspToPricingEntity(reviewLeasePrice);
                projPricingLeasePrice.setProjectId(info.getId());
                if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                    projPricingLeasePrice.setLeaseMonthCount(meetMinuteDetail.getLeaseTerm());
                    projPricingLeasePrice.setDownPayment(Optional.ofNullable(meetMinuteDetail.getDownPayment()).orElse(0L));
                    projPricingLeasePrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
                    projPricingLeasePrice.setNominalPrice(Optional.ofNullable(meetMinuteDetail.getNominalPrice()).orElse(0L));
                }
                projPricingLeasePrice.setProjectApprovalAmount(reviewLeasePrice.getProjectApprovalAmount());
                pricingPriceService.add(projPricingLeasePrice);
            }
            if (ObjectUtil.isNotEmpty(reviewFactoringPrice)) {
                info.setDeclaredAmount(reviewFactoringPrice.getApplyCreditAmount());
                ProjPricingFactoringPrice projPricingFactoringPrice = priceConverter.reviewFactoringRspToPricingEntity(reviewFactoringPrice);
                projPricingFactoringPrice.setProjectId(info.getId());
                if (ObjectUtil.isNotEmpty(meetMinuteDetail)) {
                    projPricingFactoringPrice.setFactoringCreditTerm(meetMinuteDetail.getLeaseTerm());
                    projPricingFactoringPrice.setEarnestMoney(Optional.ofNullable(meetMinuteDetail.getEarnestMoneyAmount()).orElse(0L));
                }
                projPricingFactoringPrice.setProjectApprovalAmount(reviewFactoringPrice.getProjectApprovalAmount());
                pricingPriceService.add(projPricingFactoringPrice);
            }
            if (CollectionUtils.isNotEmpty(projReviewCashFlowPlanList)){
                List<ProjPricingCashFlowPlan> reviewCashFlowPlanList = projReviewCashFlowPlanList.stream().map(item -> {
                    ProjPricingCashFlowPlan pricingCashFlowPlan = BeanUtil.copyProperties(item, ProjPricingCashFlowPlan.class);
                    pricingCashFlowPlan.setId(null);
                    pricingCashFlowPlan.setProjectId(info.getId());
                    return pricingCashFlowPlan;
                }).collect(Collectors.toList());
                pricingCashFlowPlanService.saveBatch(reviewCashFlowPlanList);
            }
//            // 从项目评审拷贝客户文件
//            try {
//                // 找对应项目立项的客户资料
//                String sourceBusinessKey = String.format("%s@%s", BusinessModuleEnum.PROJ_REVIEW.name(), reviewBaseInfo.getId());
//                copyFileList = SpringUtil.getBean(MaterialsListService.class).listBySourceBusinessKey(BusinessModuleEnum.CLIENT, sourceBusinessKey);
//            } catch (Exception e) {
//                log.error("项目定价拷贝来自项目评审的客户资料发生异常[id:{}]", reviewBaseInfo.getId());
//            }
        }
//        // 保存拷贝文件
//        if (CollectionUtil.isNotEmpty(copyFileList)) {
//            copyFileList.forEach(e -> {
//                e.setId(null);
//                e.setSourceBusinessKey(String.format("%s@%s", BusinessModuleEnum.PROJ_PRICING.name(), info.getId()));
//                e.setCreateBy(null);
//                e.setCreateTime(null);
//                e.setUpdateBy(null);
//                e.setUpdateTime(null);
//            });
//            SpringUtil.getBean(MaterialsListService.class).saveBatch(copyFileList);
//        }
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_PRICING,
                        info.getId().toString(),
                        "项目定价-创建")
                )
        );
        return new ProjPricingBaseInfoAddRSP().setId(info.getId()).setBizType(info.getBizType());
    }





    private Set<String> getRelationClientNames(ProjPricingBaseInfo info) {
        Set<String> clientNames = new HashSet<>();
        if (StringUtil.isNotEmpty(info.getLesseeInfo())) {
            clientNames.addAll(JSON.parseArray(info.getLesseeInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        if (StringUtil.isNotEmpty(info.getCreditorInfo())) {
            clientNames.addAll(JSON.parseArray(info.getCreditorInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        if (StringUtil.isNotEmpty(info.getDebtorInfo())) {
            clientNames.addAll(JSON.parseArray(info.getDebtorInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        if (StringUtil.isNotEmpty(info.getGuaranteeInfo())) {
            clientNames.addAll(JSON.parseArray(info.getGuaranteeInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        return clientNames;
    }

    /**
     * 此转换使用mapstruct存在隐患，因此只复制所需的字段
     * @param establishBaseInfo
     * @return ProjPricingBaseInfo
     */
    private ProjPricingBaseInfo establishToPricing(ProjEstablishBaseInfo establishBaseInfo) {
        if (establishBaseInfo == null) {
            return null;
        }
        ProjPricingBaseInfo projPricingBaseInfo = new ProjPricingBaseInfo();
        projPricingBaseInfo.setClientId(establishBaseInfo.getClientId());
        projPricingBaseInfo.setBizType(establishBaseInfo.getBizType());
        projPricingBaseInfo.setProjName(establishBaseInfo.getProjName());
        projPricingBaseInfo.setProjCode(establishBaseInfo.getProjCode());
        projPricingBaseInfo.setLeaseTypes(establishBaseInfo.getLeaseTypes());
        projPricingBaseInfo.setProjSource(establishBaseInfo.getProjSource());
        projPricingBaseInfo.setFundsPurpose(establishBaseInfo.getFundsPurpose());
        projPricingBaseInfo.setProjBackground(establishBaseInfo.getProjBackground());
        projPricingBaseInfo.setAssignor(establishBaseInfo.getAssignor());
        projPricingBaseInfo.setLesseeInfo(establishBaseInfo.getLesseeInfo());
        projPricingBaseInfo.setCreditorInfo(establishBaseInfo.getCreditorInfo());
        projPricingBaseInfo.setDebtorInfo(establishBaseInfo.getDebtorInfo());
        projPricingBaseInfo.setGuaranteeInfo(establishBaseInfo.getGuaranteeInfo());
        projPricingBaseInfo.setPledgorInfo(establishBaseInfo.getPledgorInfo());
        projPricingBaseInfo.setMortgagorInfo(establishBaseInfo.getMortgagorInfo());
        projPricingBaseInfo.setProjSponsorUserId(establishBaseInfo.getProjSponsorUserId());
        projPricingBaseInfo.setProjCosponsorUserIds(establishBaseInfo.getProjCosponsorUserIds());
        projPricingBaseInfo.setBizDeptId(establishBaseInfo.getBizDeptId());
        projPricingBaseInfo.setBizDeptLeaderId(establishBaseInfo.getBizDeptLeaderId());
        projPricingBaseInfo.setBizDivisionLeaderId(establishBaseInfo.getBizDivisionLeaderId());
        projPricingBaseInfo.setEvaluationSubjectId(establishBaseInfo.getEvaluationSubjectId());
        projPricingBaseInfo.setRegionalProjectClassify(establishBaseInfo.getRegionalProjectClassify());
        projPricingBaseInfo.setCountry(establishBaseInfo.getCountry());
        projPricingBaseInfo.setProvince(establishBaseInfo.getProvince());
        projPricingBaseInfo.setCity(establishBaseInfo.getCity());
        projPricingBaseInfo.setDistrict(establishBaseInfo.getDistrict());
        if (ObjectUtil.isNotEmpty(establishBaseInfo.getRiskControlManagerId())) {
            List<Long> RiskControlManagerIds = JSON.parseObject(establishBaseInfo.getRiskControlManagerId(), new TypeReference<List<Long>>() {
            });
            projPricingBaseInfo.setRiskControlManagerId(RiskControlManagerIds.get(0));
        }
        //根据第一个承租人/债权人区分
        projPricingBaseInfo.setEnterpriseNature(getEnterpriseNatureById(establishBaseInfo.getClientId()));
        return projPricingBaseInfo;
    }


    private String getEnterpriseNatureById(Long clientId) {
        List<CorpCommerceInfo> corpCommerceInfoList = commerceInfoService.findByClientId(clientId);
        if (ObjectUtil.isNull(corpCommerceInfoList) || corpCommerceInfoList.isEmpty()) {
            return null;
        }
        CorpCommerceInfo detail = corpCommerceInfoList.get(0);
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibHandler.queryLatestDataByOriginId(detail.getId());
        return ObjectUtil.isNotNull(corpCommerceInfoLib) ? corpCommerceInfoLib.getEnterpriseNature() : null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public ProjPricingBaseInfoAddRSP addByGroupCredit(ProjPricingBaseInfoAddByGroupCreditREQ req) {
        if (isNotNull(baseMapper.selectOne(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .ne(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name())
                .eq(ProjPricingBaseInfo::getProjName, req.getProjName())))) {
            throw new MithrasException("该项目名称存在未关闭的项目定价数据");
        }
        GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoService.getById(req.getGroupCreditReviewId());
        if (Objects.isNull(groupCreditReviewBaseInfo)) {
            throw new MithrasException("集团授信评审不存在");
        }
        if (!RecordStatus.TAKE_EFFECT.name().equals(groupCreditReviewBaseInfo.getGroupCreditReviewStatus())) {
            throw new MithrasException("该集团授信评审未生效");
        }
        if (GroupCreditReviewProcessStatus.CHANGING_UN_SUBMIT.name().equals(groupCreditReviewBaseInfo.getGroupCreditReviewProcessStatus()) || GroupCreditReviewProcessStatus.CHANGING_UNDER_APPROVAL.name().equals(groupCreditReviewBaseInfo.getGroupCreditReviewProcessStatus())) {
            throw new MithrasException("当前集团授信评审数据处于变更未提交或者变更审批中，无法选取");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(groupCreditReviewBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            throw new MithrasException("只有项目经理可以创建项目评审");
        }
        Client client = clientService.getById(req.getClientId());
        if (Objects.isNull(client)) {
            throw new MithrasException("用信客户不存在");
        }
        if (!ClientType.CORPORATION.name().equals(client.getClientType())) {
            throw new MithrasException("只有法人客户可发起用信");
        }
        //todo: 定价需不需要校验授信可用额度？
        String gcrRemainAmountLockKey = CacheEnum.GROUP_CREDIT_PRICING_REMAIN_AMOUNT_LOCK.buildKey(req.getGroupCreditReviewId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(gcrRemainAmountLockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            if (groupCreditReviewService.getRemainCreditAmount(groupCreditReviewBaseInfo.getId()) <= 0) {
                throw new MithrasException("该授信可用额度已小于等于0，不可发起新的项目定价");
            }
        } finally {
            redisDistLock.unlock(gcrRemainAmountLockKey);
        }
        ProjPricingBaseInfo info = groupCreditReviewToPricing(groupCreditReviewBaseInfo);
        info.setClientId(req.getClientId());
        info.setBizType(req.getBizType());
        info.setProjName(req.getProjName());
        //自动设置项目主办、业务部门，业务部门负责人，业务部门分管领导
        info.setProjSponsorUserId(AccountUtil.getLoginInfo().getId());
        info.setBizDeptId(bizOrgDO.getId());
        info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.businesshead.name()));
        info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.leaderincharge.name()));
        Long seq = projCodeStoreService.maxSeqId(req.getBizType()) + 1;
        info.setProjCode(projCodeStoreService.generateProjCode(req.getBizType(), seq));
        info.setDeclaredAmount(0L);
        // 不同业务 填充主承租人或债权人
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientId(client.getId());
        clientInfo.setClientType(client.getClientType());
        clientInfo.setClientName(client.getClientName());
        if (CharSequenceUtil.equalsAny(req.getBizType(), ZL.name(), ZZ.name())) {
            info.setLesseeInfo(JSON.toJSONString(ListUtil.toList(clientInfo)));
        } else if (CharSequenceUtil.equalsAny(req.getBizType(), BL.name(), ZR.name())) {
            info.setCreditorInfo(JSON.toJSONString(ListUtil.toList(clientInfo)));
        }
        //填充企业性质
        info.setEnterpriseNature(getEnterpriseNatureById(client.getId()));
        // FTP行业分类尝试初始化
        List<CorpCommerceInfo> corpCommerceInfoList = commerceInfoService.findByClientId(client.getId());
        if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
            info.setFtpIndustryCategory(Optional.ofNullable(CommonConvert.toFtpIndustryCategory(corpCommerceInfoList.get(0).getRiskControlIndustryClassify())).map(Enum::name).orElse(null));
        }
        baseMapper.insert(info);
        projCodeStoreService.add(req.getBizType(), seq, info.getProjCode());
        // 初始化报价方案
        if (CharSequenceUtil.equalsAny(req.getBizType(), ZL.name(), ZZ.name())) {
            ProjPricingLeasePrice leasePrice = new ProjPricingLeasePrice();
            leasePrice.setProjectId(info.getId());
//            leasePrice.setApplyCreditAmount(0L);
            pricingPriceService.add(leasePrice);
        } else if (CharSequenceUtil.equalsAny(req.getBizType(), BL.name())) {
            ProjPricingFactoringPrice factoringPrice = new ProjPricingFactoringPrice();
            factoringPrice.setProjectId(info.getId());
//            factoringPrice.setApplyCreditAmount(0L);
            pricingPriceService.add(factoringPrice);
        } else if (CharSequenceUtil.equalsAny(req.getBizType(), ZR.name())) {
            ProjPricingAocPrice aocPrice = new ProjPricingAocPrice();
            aocPrice.setProjectId(info.getId());
//            aocPrice.setApplyCreditAmount(0L);
            pricingPriceService.add(aocPrice);
        }
        if(req.isCreateReview()) {
            ProjReviewBaseInfoAddByGroupCreditREQ reviewReq = BeanUtil.copyProperties(req, ProjReviewBaseInfoAddByGroupCreditREQ.class);
            reviewReq.setCreatePricing(false);
            SpringContextHolder.getBean(ProjReviewBaseInfoService.class).addByGroupCredit(reviewReq);
        }
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_PRICING,
                        info.getId().toString(),
                        "项目定价-创建")
                )
        );
        return new ProjPricingBaseInfoAddRSP().setId(info.getId()).setBizType(info.getBizType());
    }


    private ProjPricingBaseInfo groupCreditReviewToPricing(GroupCreditReviewBaseInfo groupCreditReviewBaseInfo) {
        ProjPricingBaseInfo projPricingBaseInfo = new ProjPricingBaseInfo();
        projPricingBaseInfo.setProjectType(groupCreditReviewBaseInfo.getProjectType());
        projPricingBaseInfo.setProjBackground(groupCreditReviewBaseInfo.getProjBackground());
        projPricingBaseInfo.setProjSponsorUserId(groupCreditReviewBaseInfo.getProjSponsorUserId());
        projPricingBaseInfo.setProjCosponsorUserIds(groupCreditReviewBaseInfo.getProjCosponsorUserIds());
        projPricingBaseInfo.setBizDeptId(groupCreditReviewBaseInfo.getBizDeptId());
        projPricingBaseInfo.setBizDeptLeaderId(groupCreditReviewBaseInfo.getBizDeptLeaderId());
        projPricingBaseInfo.setBizDivisionLeaderId(groupCreditReviewBaseInfo.getBizDivisionLeaderId());
        projPricingBaseInfo.setRiskControlManagerId(groupCreditReviewBaseInfo.getRiskControlManagerId());
        projPricingBaseInfo.setLegalManagerUserId(groupCreditReviewBaseInfo.getLegalManagerUserId());
        projPricingBaseInfo.setRelationDataType(ReviewRelationDataType.GROUP_CREDIT_REVIEW.name());
        projPricingBaseInfo.setGroupCreditReviewId(groupCreditReviewBaseInfo.getId());
        projPricingBaseInfo.setProjPricingStatus(NEW.name());
        projPricingBaseInfo.setProjPricingProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
        return projPricingBaseInfo;

    }

    @Transactional(rollbackFor = Throwable.class)
    public void disable(ProjPricingBaseInfoRemoveREQ req) {
        if (CollUtil.isEmpty(req.getIds())) {
            return;
        }
        //check if could be close
        List<ProjPricingBaseInfo> infos = baseMapper.selectBatchIds(req.getIds());
        for (ProjPricingBaseInfo info : infos) {
            if (ObjectUtil.equal(info.getProjPricingStatus(), RecordStatus.CLOSED.name())) {
                throw new MithrasException(String.format("项目[%s]，请勿重复关闭", info.getProjName()));
            }
            if (!couldBeClosed(info.getId())) {
                errMithras(true, String.format("该项目定价已创建合同，[%s]不能关闭", info.getProjName()));
            }
            stateMachine.execute(ProjContext.of(info, ProjEvent.DISABLE, info.getProcessStatus()));
        }
        // 关闭评审 需要结束审批流程 放在所有校验之后做 因为会发消息等
        for (Long id : req.getIds()) {
            ProcessResp processResp = projPricingService.findRelatedProcess(id);
            if (Objects.nonNull(processResp)) {
                ExecutionProcessBaseREQ cancelProcessReq = new ExecutionProcessBaseREQ();
                cancelProcessReq.setProcessInstanceId(processResp.getProcessInstanceId());
                cancelProcessReq.setMessage("因关闭项目定价，审批自动取消");
                executionService.cancelProcess(cancelProcessReq);
            }
        }
        ProjPricingBaseInfo toUpdate = new ProjPricingBaseInfo();
        toUpdate.setProjPricingStatus(RecordStatus.CLOSED.name());
        baseMapper.update(toUpdate, Wrappers.<ProjPricingBaseInfo>lambdaUpdate().in(ProjPricingBaseInfo::getId, req.getIds()));
        projClientRoleService.projPricingFinish(req.getIds());
        req.getIds().forEach(e -> {
            // 通知客户权限变更
            ApplicationContextUtil.getApplicationContext().publishEvent(
                    new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                            BusinessModuleEnum.PROJ_PRICING,
                            e.toString(),
                            "项目定价-关闭")
                    )
            );
        });
    }


    private boolean couldBeClosed(Long id) {
        // 被合同模块关联 则不可关闭
        ProjReviewBaseInfo reviewBaseInfo = this.getReviewByPricing(id);
        if(reviewBaseInfo != null) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getProjReviewId, reviewBaseInfo.getId())
                    .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
                    .ne(ContractBaseInfo::getContractStatus, ContractStatus.CLOSED.name())
                    .last("LIMIT 1")
            );
            return Objects.isNull(contractBaseInfo);
        }
        return true;
    }

    //更新部门领导信息
    @Transactional(rollbackFor = Throwable.class)
    public void renewLeader(Long projPricingId) {
        ProjPricingBaseInfo projPricingBaseInfo = baseMapper.selectById(projPricingId);
        if (ObjectUtil.isEmpty(projPricingBaseInfo)) {
            return;
        }
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(projPricingBaseInfo.getBizDeptId());
        if (isNotNull(bizOrgDO)) {
            LambdaUpdateWrapper<ProjPricingBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProjPricingBaseInfo::getId, projPricingBaseInfo.getId());
            updateWrapper.set(ProjPricingBaseInfo::getBizDeptLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.businesshead.name()));
            updateWrapper.set(ProjPricingBaseInfo::getBizDivisionLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.leaderincharge.name()));
            this.baseMapper.update(null, updateWrapper);
        }

    }

    public ProjPricingBaseInfo getPricingByReview(Long establishId, Long groupReviewId, String projName) {
        ProjReviewBaseInfo reviewBaseInfo = new ProjReviewBaseInfo();
        reviewBaseInfo.setProjEstablishId(establishId);
        reviewBaseInfo.setGroupCreditReviewId(groupReviewId);
        reviewBaseInfo.setProjName(projName);
        return getPricingByReview(reviewBaseInfo);
    }

    public ProjPricingBaseInfo getPricingByReviewId(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        if (Objects.isNull(projReviewBaseInfo)) {
            return null;
        }
        return this.getPricingByReview(projReviewBaseInfo);
    }

    public ProjPricingBaseInfo getPricingByReview(ProjReviewBaseInfo reviewBaseInfo) {
        return this.getBaseMapper().selectOne(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .eq(reviewBaseInfo.getProjEstablishId() != null ,ProjPricingBaseInfo::getProjEstablishId, reviewBaseInfo.getProjEstablishId())
                .eq(reviewBaseInfo.getGroupCreditReviewId() != null ,ProjPricingBaseInfo::getGroupCreditReviewId, reviewBaseInfo.getGroupCreditReviewId())
                .eq(ProjPricingBaseInfo::getProjName, reviewBaseInfo.getProjName())
                .ne(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name())
        );
    }

    public ProjPricingBaseInfo getPricingByReviewIdExcludeProjName(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        return this.getPricingByReviewExcludeProjName(projReviewBaseInfo);
    }

    public ProjPricingBaseInfo getPricingByReviewExcludeProjName(ProjReviewBaseInfo reviewBaseInfo) {
        List<ProjPricingBaseInfo> list = this.getBaseMapper().selectList(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .eq(reviewBaseInfo.getProjEstablishId() != null ,ProjPricingBaseInfo::getProjEstablishId, reviewBaseInfo.getProjEstablishId())
                .eq(reviewBaseInfo.getGroupCreditReviewId() != null ,ProjPricingBaseInfo::getGroupCreditReviewId, reviewBaseInfo.getGroupCreditReviewId())
                .ne(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name())
        );
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        return list.stream().filter(e -> Objects.equals(e.getProjReviewId(), reviewBaseInfo.getId())).max(Comparator.comparing(ProjPricingBaseInfo::getId)).orElse(null);
    }

    public ProcessResp findRelatedProcess(Long projPricingId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(projPricingId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_PRICING.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public ProjReviewBaseInfo getReviewByPricing(ProjPricingBaseInfo pricingBaseInfo){
        if(pricingBaseInfo != null) {
            ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                    .eq(pricingBaseInfo.getProjEstablishId() != null,ProjReviewBaseInfo::getProjEstablishId, pricingBaseInfo.getProjEstablishId())
                    .eq(pricingBaseInfo.getGroupCreditReviewId() != null ,ProjReviewBaseInfo::getGroupCreditReviewId, pricingBaseInfo.getGroupCreditReviewId())
                    .eq(ProjReviewBaseInfo::getProjName, pricingBaseInfo.getProjName())
                    .notIn(ProjReviewBaseInfo::getProjReviewStatus, CLOSED.name(), EXPIRE.name()));
            if(reviewBaseInfo != null){
                return reviewBaseInfo;
            }
        }
        return null;
    }

    public ProjReviewBaseInfo getReviewByPricing(Long pricingId){
        ProjPricingBaseInfo pricingBaseInfo = this.getById(pricingId);
        return getReviewByPricing(pricingBaseInfo);
    }

    public void updateDeclaredAmount(Long projectId, Long declaredAmount) {
        baseMapper.updateDeclaredAmount(projectId, declaredAmount);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(ProjPricingBaseInfoModifyREQ req) {
        saveCheck(req.getId());
        ProjPricingBaseInfo originalInfo = baseMapper.selectById(req.getId());
        // 如果有对应合同则不允许变更承租人
        ProjReviewBaseInfo reviewByPricing = getReviewByPricing(originalInfo.getId());
        if(reviewByPricing != null) {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(reviewByPricing.getId());
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
                Set<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
                List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractIds(contractIds);
                if (CollectionUtil.isNotEmpty(contractTenantryList)) {
                    List<Long> contractClientIds = contractTenantryList.stream().map(ContractTenantry::getLesseeId).distinct().collect(Collectors.toList());
                    Set<Long> temp = new HashSet<>();
                    if (CollectionUtil.isNotEmpty(req.getLesseeInfo())) {
                        temp.addAll(req.getLesseeInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toSet()));
                    }
                    if (CollectionUtil.isNotEmpty(req.getCreditorInfo())) {
                        temp.addAll(req.getCreditorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toSet()));
                    }
                    if (CollectionUtil.isNotEmpty(req.getDebtorInfo())) {
                        temp.addAll(req.getDebtorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toSet()));
                    }
                    List<Long> projPricingClientIds = new LinkedList<>(temp);
                    Assert.isTrue(projPricingClientIds.size() == contractClientIds.size(), () -> MithrasException.newException("合同的承租人/债权人/债务人与项目定价中不一致，请检查"));
                    contractClientIds.sort(Comparator.comparing(e -> e));
                    projPricingClientIds.sort(Comparator.comparing(e -> e));
                    for (int i = 0; i < contractClientIds.size(); i++) {
                        Long c1 = contractClientIds.get(i);
                        Long c2 = projPricingClientIds.get(i);
                        Assert.isTrue(Objects.equals(c1, c2), () -> MithrasException.newException("合同的承租人/债权人/债务人与项目定价中不一致，请检查"));
                    }
                }
            }
        }
        //评审对应的客户id，跟承租人和债权人对应的
        if (ZL.name().equals(req.getBizType()) || ZZ.name().equals(originalInfo.getBizType())) {
            Long clientId = req.getLesseeInfo().get(0).getClientId();
            if (ObjectUtil.notEqual(clientId, originalInfo.getClientId())) {
                ProjPricingBaseInfo updateInfo = new ProjPricingBaseInfo();
                updateInfo.setClientId(clientId);
                updateInfo.setId(originalInfo.getId());
                updateInfo.setEnterpriseNature(getEnterpriseNatureById(clientId));
                baseMapper.updateById(updateInfo);
            }
        }
        if (BL.name().equals(originalInfo.getBizType()) || ZR.name().equals(originalInfo.getBizType())) {
            Long clientId = req.getCreditorInfo().get(0).getClientId();
            if (ObjectUtil.notEqual(clientId, originalInfo.getClientId())) {
                ProjPricingBaseInfo updateInfo = new ProjPricingBaseInfo();
                updateInfo.setClientId(clientId);
                updateInfo.setId(originalInfo.getId());
                updateInfo.setEnterpriseNature(getEnterpriseNatureById(clientId));
                baseMapper.updateById(updateInfo);
            }
        }


        /*---------------------参数转换----------------------*/
        ProjPricingBaseInfo info = baseInfoConverter.modifyREQtoEntity(req);
        //防止code被更新
        info.setProjCode(null);
        //更新业务部门负责人，业务分管领导
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(originalInfo.getBizDeptId());
        if (ObjectUtil.isNotEmpty(bizOrgDO)) {
            info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.businesshead.name()));
            info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), JobEnum.leaderincharge.name()));
        }
        baseMapper.updateAnnotationIncludeNullById(info);
        recordStatus(req.getId());
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_PRICING,
                        info.getId().toString(),
                        "项目定价-修改")
                )
        );
        //维护项目与舆情关系
//        flowQueryExtraService.saveClientProjReviewRelation(getRelationClientNames(info), info.getProjName());
    }

    public List<ProjPricingBaseInfo> listByClients(List<Long> clientIdList) {
        return this.getBaseMapper().selectList(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .in(ProjPricingBaseInfo::getClientId, clientIdList)
                .ne(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name())
        );
    }
}
