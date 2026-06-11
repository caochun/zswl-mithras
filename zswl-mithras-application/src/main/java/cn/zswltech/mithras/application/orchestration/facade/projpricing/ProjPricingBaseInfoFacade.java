package cn.zswltech.mithras.application.orchestration.facade.projpricing;

import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingBaseInfoApplicationService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projpricing.ProjPricingButtonStatusRsp;
import cn.zswltech.mithras.dto.projpricing.ProjPricingCreateREQ;
import cn.zswltech.mithras.dto.projpricing.ProjPricingCreateRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.*;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.groupcredit.review.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.PROJECT_CLASSIFY_NULL;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.PROJ_CLOSED;
import static cn.zswltech.mithras.application.orchestration.facade.projreview.ProjReviewBaseInfoFacade.RISK_CONTROL_INDUSTRY_CLASSIFY_SET;
import static cn.zswltech.mithras.foundation.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.util.Util.missRequiredParam;


@Service
public class ProjPricingBaseInfoFacade implements ProjPricingBaseInfoApplicationService {

    @Resource
    private ProjPricingBaseInfoService pricingBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientService clientService;

    @Override
    public R<ProjPricingCreateRSP> create(ProjPricingCreateREQ req) {
        Long projPricingId = pricingBaseInfoService.createByProjReview(req.getProjReviewId());
        ProjPricingCreateRSP rsp = new ProjPricingCreateRSP();
        rsp.setProjPricingId(projPricingId);
        return R.ok(rsp);
    }

    @Deprecated
    @Override
    public R<ProjPricingBaseInfoAddRSP> add(ProjPricingBaseInfoAddREQ req) {
        // 接口废弃，统一使用create接口
        return R.ok(pricingBaseInfoService.add(req));
    }

    @Deprecated
    @Override
    public R<ProjPricingBaseInfoAddRSP> addByGroupCredit(ProjPricingBaseInfoAddByGroupCreditREQ req) {
        // 接口废弃，统一使用create接口
        return R.ok(pricingBaseInfoService.addByGroupCredit(req));
    }

    @Override
    public R<Void> modify(ProjPricingBaseInfoModifyREQ req) {
        ProjPricingBaseInfo originalInfo = pricingBaseInfoService.getById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ObjectUtil.equal(originalInfo.getProjPricingStatus(), CLOSED.name())) {
            throw new MithrasException(PROJ_CLOSED);
        }
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(originalInfo.getClientId());
        if (Objects.nonNull(corpCommerceInfoLib) && !RISK_CONTROL_INDUSTRY_CLASSIFY_SET.contains(corpCommerceInfoLib.getRiskControlIndustryClassify()) && req.getProjectClassify() == null){
            throw new MithrasException(PROJECT_CLASSIFY_NULL);
        }
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

        pricingBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<ProjPricingBaseInfoListRSP>> list(ProjPricingBaseInfoListREQ req) {
        return R.ok(pricingBaseInfoService.list(req));
    }

    @Override
    public R<ProjPricingBaseInfoDetailRSP> detail(ProjPricingBaseInfoDetailREQ req) {
        return R.ok(pricingBaseInfoService.detail(req.getId(), req.getProcessInstanceId()));
    }

    @Override
    public R<Void> disable(ProjPricingBaseInfoRemoveREQ req) {
        pricingBaseInfoService.disable(req);
        return R.ok();
    }

    @Override
    public R<ProjPricingButtonStatusRsp> buttonStatus(ProjPricingBaseInfoDetailREQ req) {
        ProjPricingButtonStatusRsp rsp = new ProjPricingButtonStatusRsp();
        ProjReviewBaseInfo reviewByPricing = pricingBaseInfoService.getReviewByPricing(req.getId());
        if(reviewByPricing != null) {
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getProjReviewId, reviewByPricing.getId()));
            rsp.setCanSaveFlag(1);
            rsp.setCanEffectFlag(1);
            if (ObjectUtil.isNotEmpty(contractBaseInfos)) {
                rsp.setCanSaveFlag(0);
                rsp.setCanEffectFlag(0);
            }
        }
        return R.ok(rsp);
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


    private void fillClientNames(ProjPricingBaseInfoModifyREQ req) {
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

    private boolean bizTypeOf(ProjPricingBaseInfo info, String... bizTypeNames) {
        for (String bizTypeName : bizTypeNames) {
            if (bizTypeName.equals(info.getBizType())) {
                return true;
            }
        }
        return false;
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
