package cn.zswltech.mithras.service.application.projestablish;

import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishBaseInfoApplicationService;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListRSP;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonAddMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonDisableMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.rule.special.ProjEstablishAuthViewRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpAddressInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.collection.CollUtil.isEmpty;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.json.JSONUtil.toBean;
import static cn.zswltech.mithras.service.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.service.others.Util.missRequiredParam;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@Service
public class ProjEstablishBaseInfoFacade implements ProjEstablishBaseInfoApplicationService {

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjEstablishAuthViewRule projEstablishAuthViewRule;
    @Resource
    private CorpAddressInfoService corpAddressInfoService;

    @Override
    @DataAuthCheck(
            checkerClass = CommonAddMainAuthCheckerNew.class,
            paramType = DataAuthCheck.ParamType.NO,
            businessModule = BusinessModuleEnum.PROJ_ESTABLISH
    )
    public R<ProjEstablishBaseInfoAddRSP> add(ProjEstablishBaseInfoAddREQ req) {
        SpringContextHolder.getBean(ClientService.class).checkClientOccupy(req.getClientId());
        ProjEstablishBaseInfo info = projEstablishBaseInfoService.add(req);
        return R.ok(copyProperties(info, ProjEstablishBaseInfoAddRSP.class));
    }

    @Override
    public R<ProjEstablishBaseInfoUpdateRatingRSP> updateRating(ProjEstablishBaseInfoUpdateRatingREQ req) {
         return R.ok(projEstablishBaseInfoService.updateRating(req));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_ESTABLISH
    )
    public R<Void> modify(@Valid ProjEstablishBaseInfoModifyREQ req) {
        ProjEstablishBaseInfo info = projEstablishBaseInfoService.getById(req.getId());
        // 更新时，前端没有传clientInfo的名称
        fillClientNames(req);

        missRequiredParam((ZL.name().equals(info.getBizType()) || ZZ.name().equals(info.getBizType())) && isEmpty(req.getLesseeInfo()), "承租人");
        missRequiredParam((BL.name().equals(info.getBizType()) || ZR.name().equals(info.getBizType())) && isEmpty(req.getCreditorInfo()), "债权人");
        req.setProjCosponsorUserIds(duplicateRemoval(req.getProjCosponsorUserIds()));
        // 不能有重复的人
        checkDuplicatePerson(req.getLesseeInfo(), "承租人", true);
        checkDuplicatePerson(req.getCreditorInfo(), "债权人", true);
        checkDuplicatePerson(req.getDebtorInfo(), "债务人", true);
        checkDuplicatePerson(req.getGuaranteeInfo(), "担保人", false);
        checkDuplicatePerson(req.getMortgagorInfo(), "抵押人", false);
        checkDuplicatePerson(req.getPledgorInfo(), "质押人", false);

        projEstablishBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<ProjEstablishBaseInfoListRSP>> list(ProjEstablishBaseInfoListREQ req) {
        Page<ProjEstablishBaseInfoListRSP> data = projEstablishBaseInfoService.list(req);
        return R.ok(PageR.of(data.getRecords(), data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
//    @DataAuthCheck(
//            keyFieldName = "id",
//            checkerClass = CommonViewMainAuthCheckerNew.class,
//            businessModule = BusinessModuleEnum.PROJ_ESTABLISH
//    )
    public R<ProjEstablishBaseInfoListRSP> detail(ProjEstablishBaseInfoDetailREQ req) {
        // 特殊权限校验
        projEstablishAuthViewRule.checkEstablish(req.getId());
        return projEstablishBaseInfoService.detail(req);
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "ids",
            checkerClass = CommonDisableMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_ESTABLISH
    )
    public R<Void> disable(ProjEstablishBaseInfoRemoveREQ req) {
        projEstablishBaseInfoService.disable(req);
        return R.ok();
    }

    @Override
    public R<ClientStockRiskExposureRSP> getClientStockRiskExposure(ClientIdREQ req) {
        Client client = clientService.getById(req.getClientId());
        if (isNull(client) || client.getClientType().equals(ClientType.NORMAL.name())) {
            return R.ok(new ClientStockRiskExposureRSP(req.getClientId(), null));
        }
        return R.ok(new ClientStockRiskExposureRSP(req.getClientId(), contractBaseInfoService.getStockRiskExposure(req.getClientId(), null, null)));
    }

    @Override
    public R<ClientAddressRSP> getClientAddress(@Valid ClientIdREQ req) {
        //根据评估主体，默认填充评估区域
        ClientAddressRSP rsp = new ClientAddressRSP();
        CorpAddressInfoListREQ corpAddressInfoListREQ = new CorpAddressInfoListREQ();
        corpAddressInfoListREQ.setClientId(req.getClientId());
        Page<CorpAddressInfo> list = corpAddressInfoService.list(corpAddressInfoListREQ);
        if (ObjectUtil.isNotEmpty(list) && ObjectUtil.isNotEmpty(list.getRecords())) {
            List<CorpAddressInfo> addressInfos = list.getRecords().stream().filter(e -> CorpAddressType.REGISTRY_ADDRESS.name().equals(e.getAddressType())).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(addressInfos)) {
                CorpAddressInfo first = addressInfos.get(0);
                rsp.setCountry(first.getCountry());
                rsp.setProvince(first.getProvince());
                rsp.setCity(first.getCity());
                rsp.setDistrict(first.getDistrict());
            }
        }
        return R.ok(rsp);
    }

    private void checkDuplicatePerson(List<ProjEstablishPersonInfo> personList, String name, Boolean needOccupy) {
        if (CollUtil.isEmpty(personList)) {
            return;
        }
        Set<Long> set = new HashSet<>(personList.size());
        for (ProjEstablishPersonInfo info : personList) {
            if (StrUtil.isEmpty(info.getClientType())) {
                throw new MithrasException(name + ":客户类型不能为空");
            }
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

    private void fillClientNames(ProjEstablishBaseInfoModifyREQ req) {
        List<ProjEstablishPersonInfo> clientInfos = new ArrayList<>();
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
        Map<Long, ProjEstablishPersonInfo> clients = new HashMap<>();
        for (ProjEstablishPersonInfo clientInfo : clientInfos) {
            if (clientInfo.getClientId() != null) {
                clients.put(clientInfo.getClientId(), clientInfo);
            }
        }
        Map<Long, String> longStringMap = id2NameService.clientId2Name(clients.keySet());
        clients.forEach((aLong, clientInfo) -> clientInfo.setClientName(longStringMap.getOrDefault(aLong, null)));
    }


}
