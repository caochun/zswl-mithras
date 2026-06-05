package cn.zswltech.mithras.service.application.client;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.customer.application.client.api.ClientUnifiedViewApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.afterlease.ClientUnifiedViewOverdueRentListRSP;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.collection.mapper.dto.CollectionNextRentParam;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.impl.RentCollectionIndexServiceImpl;
import cn.zswltech.mithras.service.service.client.ClientUnifiedViewService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 *
 * @date 2022/6/23 2:39 PM
 */
@Service
@Slf4j
public class ClientUnifiedViewFacade implements ClientUnifiedViewApplicationService {

    @Resource
    private ClientUnifiedViewService clientUnifiedViewService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private RentCollectionIndexServiceImpl rentCollectionIndexService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<List<SelectRSP>> orgList() {
        return R.ok(clientUnifiedViewService.orgList());
    }

    @Override
    public R<PageR<ClientUnifiedViewListRSP>> unifiedViewList(@Valid ClientUnifiedViewListREQ req) {
        return R.ok(clientUnifiedViewService.unifiedViewList(req));
    }

    @Override
    public R<ClientUnifiedViewDetailRSP> unifiedViewDetail(@Valid ClientUnifiedApplyCreditREQ req) {
        return R.ok(clientUnifiedViewService.unifiedViewDetail(req));
    }

    @Override
    public R<ClientUnifiedApplyCreditRSP> clientApplyCredit(@Valid ClientUnifiedApplyCreditREQ req) {
        return R.ok(clientUnifiedViewService.clientApplyCredit(req));
    }

    @Override
    public R<ClientClassificationRSP> classification(@Valid ClientUnifiedApplyCreditREQ req) {
        return R.ok(clientUnifiedViewService.classification(req));
    }

    @Override
    public R<Map<String, Long>> clientApplyCreditHistory(@Valid ClientUnifiedApplyCreditREQ req) {
        return R.ok(clientUnifiedViewService.clientApplyCreditHistory(req));
    }

    @Override
    public R<List<ClientUnifiedProjListRSP>> clientProjList(@Valid ClientUnifiedApplyCreditREQ req) {
        try {
            return R.ok(clientUnifiedViewService.clientProjList(req));
        } catch (Exception e) {
            log.error("统一视图-概览-项目列表发生异常", e);
            throw new MithrasException("系统繁忙，请稍后再试");
        }
    }

    @Override
    public R<List<ClientUnifiedRatingHistoryRSP>> ratingHistory(@Valid ClientUnifiedApplyCreditREQ req) {
        return R.ok(ratingClientService.ratingHistory(req.getClientId()));
    }

    @Override
    public R<List<ClientUnifiedViewOverdueRentListRSP>> overdueRent(@Valid ClientUnifiedApplyCreditREQ req) {
       /* RentCollectionListREQ rentCollectionListREQ = new RentCollectionListREQ();
        rentCollectionListREQ.setClientId(req.getClientId());
        rentCollectionListREQ.setPage(1);
        rentCollectionListREQ.setPageSize(5000);
        rentCollectionListREQ.setFilterConditionType(OverdueTypeEnum.OVERDUE.name());
        //修改为每个合同最近一期待还数据
        PageR<RentCollectionListRSP> rentCollectionListRSPPageR = rentCollectionIndexService.indexList(rentCollectionListREQ);
        if (ObjectUtil.isNotEmpty(rentCollectionListRSPPageR) && ObjectUtil.isNotEmpty(rentCollectionListRSPPageR.getList())) {
            List<ClientUnifiedViewOverdueRentListRSP> rsps = new ArrayList<>();
            //合同id转项目名称
            Map<Long, String> contractId2ProjName = contractBaseInfoService.listByIds(rentCollectionListRSPPageR.getList().stream().map(RentCollectionListRSP::getContractId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjName, (a, b) -> a));
            rentCollectionListRSPPageR.getList().forEach(rentCollectionList -> {
                rentCollectionList.getCollectionCardList().forEach(data -> {
                    ClientUnifiedViewOverdueRentListRSP rsp = new ClientUnifiedViewOverdueRentListRSP();
                    rsp.setClientId(rentCollectionList.getClientId());
                    rsp.setPaymentId(rentCollectionList.getPaymentId());
                    rsp.setContractId(rentCollectionList.getContractId());
                    rsp.setContractCode(rentCollectionList.getContractCode());
                    rsp.setPlanCollectionAmount(data.getPlanCollectionAmount());
                    rsp.setPhase(data.getPhase());
                    rsp.setPlanCollectionDate(data.getPlanCollectionDate());
                    rsp.setProjName(contractId2ProjName.get(rentCollectionList.getContractId()));
                    rsps.add(rsp);
                });
            });*/
        CollectionNextRentParam query = new CollectionNextRentParam();
        query.setClientId(req.getClientId());
        // 可看数据权限
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        query.setIsBizUser(isBizUser);
        query.setDeptIdList(canViewDeptIds);
        query.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.contractNextRentList(query);
        if (ObjectUtil.isNotEmpty(collectionBaseInfos)) {
            List<ClientUnifiedViewOverdueRentListRSP> rsps = new ArrayList<>();
            //合同id转项目名称
            Map<Long, String> contractId2ProjName = contractBaseInfoService.listByIds(collectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjName, (a, b) -> a));
            collectionBaseInfos.forEach(data -> {
                ClientUnifiedViewOverdueRentListRSP rsp = new ClientUnifiedViewOverdueRentListRSP();
                rsp.setClientId(data.getClientId());
                rsp.setPaymentId(data.getPaymentId());
                rsp.setContractId(data.getContractId());
                rsp.setContractCode(data.getContractCode());
                rsp.setPlanCollectionAmount(data.getPlanCollectionAmount());
                rsp.setPhase(data.getPhase());
                rsp.setPlanCollectionDate(data.getPlanCollectionDate());
                rsp.setProjName(contractId2ProjName.get(data.getContractId()));
                if (ObjectUtil.isNotEmpty(data.getPlanCollectionDate()) && data.getPlanCollectionDate().isBefore(LocalDate.now().minusDays(3))) {
                    rsp.setOverdueFlag(Boolean.TRUE);
                } else {
                    rsp.setOverdueFlag(Boolean.FALSE);
                }
                rsps.add(rsp);
            });
            return R.ok(rsps);
        }
        return R.ok();
    }

    @Override
    public R<List<ClientUnifiedContractListRSP>> clientContractList(@Valid ClientUnifiedApplyCreditREQ req) {
        return R.ok(clientUnifiedViewService.clientContractList(req));
    }

    @Override
    public R<List<ClientUnifiedCustomerTrendsRSP>> customerTrends() {
        List<ClientUnifiedCustomerTrendsRSP> rsps = null;
        try {
            rsps = clientUnifiedViewService.customerTrends();
        } catch (Exception e) {
            log.error("客户趋势图异常", e);
            throw new MithrasException("系统异常，请稍后重试");
        }
        return R.ok(rsps);
    }
}
