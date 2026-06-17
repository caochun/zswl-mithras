package cn.zswltech.mithras.application.orchestration.facade.projectprocess.projreview;

import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewBaseInfoApplicationService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewVagueListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewVagueListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckReasonREQ;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckResult;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckResultDetail;
import cn.zswltech.mithras.dto.projreview.ProjReviewButtonStatusRsp;
import cn.zswltech.mithras.dto.projreview.baseinfo.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonAddMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonDisableMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialCommentsEnum;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewMaterial;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.credit.groupcredit.review.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.customer.versioning.CorpCommerceInfoLibService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewMaterialService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.*;
import static cn.zswltech.mithras.foundation.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.EXPIRE;
import static cn.zswltech.mithras.foundation.util.Util.missRequiredParam;

/**
 * @author zhaozhengkang
 * @description 立项基本信息表
 * @date 2022-08-01
 */
@Service
public class ProjReviewBaseInfoFacade implements ProjReviewBaseInfoApplicationService {

    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private ProjPricingBaseInfoService pricingBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientService clientService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private ProjReviewMaterialService projReviewMaterialService;

    public final static Set<String> RISK_CONTROL_INDUSTRY_CLASSIFY_SET;

    static {
        RISK_CONTROL_INDUSTRY_CLASSIFY_SET = new HashSet<>();
        RISK_CONTROL_INDUSTRY_CLASSIFY_SET.add(RiskControlIndustryClassify.PUBLIC_UTILITIES.name());
        RISK_CONTROL_INDUSTRY_CLASSIFY_SET.add(RiskControlIndustryClassify.CIVIL_CONSUMPTION.name());
        RISK_CONTROL_INDUSTRY_CLASSIFY_SET.add(RiskControlIndustryClassify.TRAVEL.name());
//        RISK_CONTROL_INDUSTRY_CLASSIFY_SET.add(RiskControlIndustryClassify.ENGINEERING_MACHINERY.name());
        RISK_CONTROL_INDUSTRY_CLASSIFY_SET.add(RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name());
    }

    @Override
    public R<List<ProjEstablishVagueListRSP>> vague(ProjEstablishVagueListREQ req) {
        Map<String, ProjEstablishVagueListRSP> projEstablishVagueMap = establishBaseInfoService.vagueQuery(req);
        List<String> projNames = projEstablishVagueMap.values().stream().map(ProjEstablishVagueListRSP::getProjName)
                .collect(Collectors.toList());
        // 查询出已有审批的项目并过滤掉
        if (ObjectUtil.isNotEmpty(projNames)) {
            List<String> reviewNames = reviewBaseInfoService.getBaseMapper().selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                    .in(ProjReviewBaseInfo::getProjName, projNames)
                    .notIn(ProjReviewBaseInfo::getProjReviewStatus, ListUtil.toList(RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())))
                    .stream().map(ProjReviewBaseInfo::getProjName)
                    .collect(Collectors.toList());
            List<String> pricingNames = pricingBaseInfoService.getBaseMapper().selectList(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                    .in(ProjPricingBaseInfo::getProjName, projNames)
                    .ne(ProjPricingBaseInfo::getProjPricingStatus, CLOSED.name()))
                    .stream().map(ProjPricingBaseInfo::getProjName)
                    .collect(Collectors.toList());

            for (String reviewName : reviewNames) {
                if(CollectionUtil.isNotEmpty(pricingNames) && pricingNames.contains(reviewName)) {
                    projEstablishVagueMap.remove(reviewName);
                }
            }
        }
        if (ObjectUtil.isEmpty(projEstablishVagueMap)) {
            return R.ok();
        }
        return R.ok(new ArrayList<>(projEstablishVagueMap.values()));
    }

    @Override
    public R<List<ProjEstablishVagueListRSP>> vagueEffect(ProjEstablishVagueListREQ req) {
        Map<String, ProjEstablishVagueListRSP> projEstablishVagueMap = establishBaseInfoService.vagueQuery(req);
        List<String> projNames = projEstablishVagueMap.values().stream().map(ProjEstablishVagueListRSP::getProjName)
                .collect(Collectors.toList());
        List<ProjEstablishVagueListRSP> rsps = new ArrayList<>();
        // 查询出已有审批的项目并过滤掉
        if (ObjectUtil.isNotEmpty(projNames)) {
            List<String> reviewNames = reviewBaseInfoService.getBaseMapper().selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                    .in(ProjReviewBaseInfo::getProjName, projNames)
                    .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()))
                    .stream().map(ProjReviewBaseInfo::getProjName)
                    .collect(Collectors.toList());
            List<String> pricingNames = pricingBaseInfoService.getBaseMapper().selectList(Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                    .in(ProjPricingBaseInfo::getProjName, projNames)
                    .ne(ProjPricingBaseInfo::getProjPricingStatus, CLOSED.name()))
                    .stream().map(ProjPricingBaseInfo::getProjName)
                    .collect(Collectors.toList());
            //过滤已经存在的
            for (String reviewName : reviewNames) {
                if(CollectionUtil.isNotEmpty(pricingNames) && pricingNames.contains(reviewName)) {
                    projEstablishVagueMap.remove(reviewName);
                }
            }
            //过滤评审未生效的
            for(String projName : reviewNames) {
                ProjEstablishVagueListRSP projEstablishVagueListRSP = projEstablishVagueMap.get(projName);
                if(ObjectUtil.isNotEmpty(projEstablishVagueListRSP)) {
                    rsps.add(projEstablishVagueListRSP);
                }
            }
        }
        return R.ok(rsps);
    }

    @Override
    public R<List<GroupCreditReviewVagueListRSP>> groupCreditReviewVague(GroupCreditReviewVagueListREQ req) {
        List<GroupCreditReviewVagueListRSP> reviewVagueList = groupCreditReviewBaseInfoService.vagueQuery(req);
        return R.ok(reviewVagueList);
    }

    @Override
    @DataAuthCheck(
            checkerClass = CommonAddMainAuthCheckerNew.class,
            paramType = DataAuthCheck.ParamType.NO,
            businessModule = "PROJ_REVIEW"
    )
    public R<ProjReviewBaseInfoAddRSP> add(ProjReviewBaseInfoAddREQ req) {
        return R.ok(reviewBaseInfoService.add(req));
    }

    @Override
    @DataAuthCheck(checkerClass = CommonAddMainAuthCheckerNew.class, paramType = DataAuthCheck.ParamType.NO, businessModule = "PROJ_REVIEW")
    public R<ProjReviewBaseInfoAddRSP> addByGroupCredit(ProjReviewBaseInfoAddByGroupCreditREQ req) {
        return R.ok(reviewBaseInfoService.addByGroupCredit(req));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = "PROJ_REVIEW"
    )
    public R<Void> modify(ProjReviewBaseInfoModifyREQ req) {
        ProjReviewBaseInfo originalInfo = reviewBaseInfoService.getById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ObjectUtil.equal(originalInfo.getProjReviewStatus(), CLOSED.name()) || ObjectUtil.equals(originalInfo.getProjReviewStatus(), EXPIRE.name())) {
            throw new MithrasException(PROJ_CLOSED);
        }
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(originalInfo.getClientId());
        if (Objects.nonNull(corpCommerceInfoLib) && !RISK_CONTROL_INDUSTRY_CLASSIFY_SET.contains(corpCommerceInfoLib.getRiskControlIndustryClassify()) && req.getProjectClassify() == null){
            throw new MithrasException(PROJECT_CLASSIFY_NULL);
        }
        //是否新建
//        boolean isNew = false;
//        if(req.getProjReviewProcessStatus() == null ||StrUtil.equalsAny(req.getProjReviewProcessStatus(), ProjProcessState.NEW_UN_SUBMIT.name(), ProjProcessState.NEW_UNDER_APPROVAL.name(), ProjProcessState.CANCEL_NEW.name(),  ProjProcessState.NEW_REJECT.name())){
//            if (StrUtil.equalsAny(req.getProjReviewProcessStatus(),ProjProcessState.NEW_APPROVAL_PASS.name())&&!projReviewService.canSave(req.getId())) {
//                //"项目评审处于审批流程中，不可修改数据"(针对新建审批通过流程状态但发起了变更的情况)
//            }else{
//                isNew = true;
//            }
//        }
//        //非新建状态需要校验法务经理
//        if(!isNew&&req.getLegalManagerUserId()==null){
//            throw new MithrasException(LEGAL_MANAGER_NULL);
//        }
        originalInfo.getProjReviewProcessStatus();
        // 更新时，前端没有传clientInfo的名称
        fillClientNames(req);
        // 业务相关字段的校验
        missRequiredParam(bizTypeOf(originalInfo, ZL.name(), ZZ.name()) && ObjectUtil.isEmpty(req.getLesseeInfo()), "承租人");
        missRequiredParam(bizTypeOf(originalInfo, ZL.name(), ZZ.name()) && ObjectUtil.isEmpty(req.getLeaseTypes()), "租赁类型");
        missRequiredParam(bizTypeOf(originalInfo, BL.name()) && ObjectUtil.isEmpty(req.getFactoringTypes()), "保理类型");
        missRequiredParam(bizTypeOf(originalInfo, ZR.name()) && ObjectUtil.isEmpty(req.getZrTypes()), "转让类型");
//        missRequiredParam(bizTypeOf(originalInfo, BL.name()) && ObjectUtil.isEmpty(req.getProjectType()), "项目类型");
        missRequiredParam(bizTypeOf(originalInfo, BL.name(), ZR.name()) && ObjectUtil.isEmpty(req.getCreditorInfo()), "债权人");
        missRequiredParam(bizTypeOf(originalInfo, BL.name()) && ObjectUtil.isEmpty(req.getDebtorInfo()), "债务人");
        //不能有重复的人 且有占用权限
        checkDuplicatePerson(req.getLesseeInfo(), "承租人", true);
        checkDuplicatePerson(req.getDebtorInfo(), "债务人", true);
        checkDuplicatePerson(req.getGuaranteeInfo(), "担保人", false);
        checkDuplicatePerson(req.getMortgagorInfo(), "抵押人", false);
        checkDuplicatePerson(req.getPledgorInfo(), "质押人", false);
        checkDuplicatePerson(req.getCreditorInfo(), "债权人", true);
        req.setProjCosponsorUserIds(duplicateRemoval(req.getProjCosponsorUserIds()));

        reviewBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<ProjReviewBaseInfoUpdateRatingRSP> updateRating(ProjReviewBaseInfoUpdateRatingREQ req) {
        return R.ok(reviewBaseInfoService.updateRating(req));
    }

    private void fillClientNames(ProjReviewBaseInfoModifyREQ req) {
        List<ClientInfo> clientInfos = new ArrayList<>();
        if (req.getDebtorInfo() != null) {
            clientInfos.addAll(req.getDebtorInfo());
        }
        if (req.getLesseeInfo() != null) {
            clientInfos.addAll(req.getLesseeInfo());
        }
        if (req.getGuaranteeInfo() != null) {
            clientInfos.addAll(req.getGuaranteeInfo());
        }
        if (req.getMortgagorInfo() != null) {
            clientInfos.addAll(req.getMortgagorInfo());
        }
        if (req.getPledgorInfo() != null) {
            clientInfos.addAll(req.getPledgorInfo());
        }
        if (req.getCreditorInfo() != null) {
            clientInfos.addAll(req.getCreditorInfo());
        }
        Map<Long, ClientInfo> clients = new HashMap<>();
        for (ClientInfo clientInfo : clientInfos) {
            if (clientInfo.getClientId() != null) {
                clients.put(clientInfo.getClientId(), clientInfo);
            }
        }
        Map<Long, String> longStringMap = id2NameService.clientId2Name(clients.keySet());
        clients.forEach((aLong, clientInfo) -> clientInfo.setClientName(longStringMap.getOrDefault(aLong, null)));
    }


    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonViewMainAuthCheckerNew.class,
            businessModule = "PROJ_REVIEW"
    )
    public R<ProjReviewBaseInfoDetailRSP> detail(ProjReviewBaseInfoDetailREQ req) {
        return R.ok(reviewBaseInfoService.detail(req.getId(), req.getProcessInstanceId()));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "ids",
            checkerClass = CommonDisableMainAuthCheckerNew.class,
            businessModule = "PROJ_REVIEW"
    )
    public R<Void> disable(ProjReviewBaseInfoRemoveREQ req) {
        reviewBaseInfoService.disable(req);
        return R.ok();
    }

    @Override
    public R<ProjReviewButtonStatusRsp> buttonStatus(ProjReviewBaseInfoDetailREQ req) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getProjReviewId, req.getId()));
        ProjReviewButtonStatusRsp rsp = new ProjReviewButtonStatusRsp();
        rsp.setCanSaveFlag(1);
        rsp.setCanEffectFlag(1);
        if (ObjectUtil.isNotEmpty(contractBaseInfos)) {
            rsp.setCanSaveFlag(0);
            rsp.setCanEffectFlag(0);
        }
        return R.ok(rsp);
    }

    @Override
    public R<CorpSubjectItemCheckResult> preCheckSubjectCorpItem(@Valid SinglePkREQ req) {
        List<CorpSubjectItemCheckResultDetail> detailList = projReviewService.listClientSubjectItemCheckResult(req.getId());
        CorpSubjectItemCheckResult corpSubjectItemCheckResult = new CorpSubjectItemCheckResult();
        if (CollectionUtil.isEmpty(detailList)) {
            corpSubjectItemCheckResult.setCheckResult(YesOrNoNumberEnum.YES.getCode());
        } else {
            int checkResult = YesOrNoNumberEnum.YES.getCode();
            for (CorpSubjectItemCheckResultDetail detail : detailList) {
                if (Objects.equals(YesOrNoNumberEnum.NO.getCode(), detail.getCheckResult())) {
                    checkResult = YesOrNoNumberEnum.NO.getCode();
                    break;
                }
            }
            corpSubjectItemCheckResult.setCheckResult(checkResult);
        }
        return R.ok(corpSubjectItemCheckResult);
    }

    @Override
    public R<CorpSubjectItemCheckResult> preCheckReviewMaterialComments(SinglePkREQ req) {
        CorpSubjectItemCheckResult corpSubjectItemCheckResult = new CorpSubjectItemCheckResult();
        List<ProjReviewMaterial> reviewMaterialsByReviewId = projReviewMaterialService.getReviewMaterialsByReviewId(req.getId());
        String msg = reviewMaterialsByReviewId.stream().filter(item -> !ProjReviewMaterialCommentsEnum.AUDITED.name().equals(item.getReviewComments()))
                .map(ProjReviewMaterial::getName).collect(Collectors.joining("、"));
        corpSubjectItemCheckResult.setCheckResult(StrUtil.isNotEmpty(msg) ? YesOrNoNumberEnum.NO.getCode() : YesOrNoNumberEnum.YES.getCode());
        corpSubjectItemCheckResult.setCheckResultMsg(StrUtil.isNotEmpty(msg) ? "资料清单页面【"+msg+"】的基础资料审核意见不为已审核，请完成审核后提交！" : "");
        return R.ok(corpSubjectItemCheckResult);
    }

    @Override
    public R<List<CorpSubjectItemCheckResultDetail>> listSubjectItemCheckResult(@Valid SinglePkREQ req) {
        return R.ok(projReviewService.listClientSubjectItemCheckResult(req.getId()));
    }

    @Override
    public R<Void> saveSubjectItemReason(@Valid CorpSubjectItemCheckReasonREQ req) {
        if (CollectionUtil.isNotEmpty(req.getReasonList())) {
            List<ProjReviewBaseInfo.SubjectReasonData> list = req.getReasonList().stream().map(e -> new ProjReviewBaseInfo.SubjectReasonData(e.getClientId(), e.getReason())).collect(Collectors.toList());
            ProjReviewBaseInfo projReviewBaseInfo = reviewBaseInfoService.getById(req.getProjReviewId());
            if (Objects.isNull(projReviewBaseInfo)) {
                throw new MithrasException("项目评审数据不存在");
            }
            projReviewBaseInfo.setSubjectItemCheckReason(JSONUtil.toJsonStr(list));
            reviewBaseInfoService.updateById(projReviewBaseInfo);
        }
        return R.ok();
    }

    @Override
    public R<PageR<ProjReviewBaseInfoListRSP>> list(ProjReviewBaseInfoListREQ req) {
        Page<ProjReviewBaseInfoListRSP> data = reviewBaseInfoService.list(req);
        return R.ok(PageR.of(data.getRecords(), data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    private boolean bizTypeOf(ProjReviewBaseInfo info, String... bizTypeNames) {
        for (String bizTypeName : bizTypeNames) {
            if (bizTypeName.equals(info.getBizType())) {
                return true;
            }
        }
        return false;
    }

    private void checkDuplicatePerson(List<ClientInfo> personList, String name, Boolean needOccupy) {
        if (CollUtil.isEmpty(personList)) {
            return;
        }
        Set<Long> set = new HashSet<>(personList.size());
        for (ClientInfo info : personList) {
            if (set.contains(info.getClientId())) {
                throw new MithrasException(name + ":存在重复的客户");
            }
            if (isNotNull(info.getClientId())) {
                set.add(info.getClientId());
            }
            if(needOccupy){
                clientService.checkClientOccupy(info.getClientId());
            }
        }
    }

    /**
     * 去重
     *
     * @param ids
     * @return
     */
    private List<Long> duplicateRemoval(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ids;
        }
        Set<Long> set = new HashSet<>(ids.size());
        for (Long id : ids) {
            set.add(id);
        }
        return new ArrayList<>(set);
    }

}
