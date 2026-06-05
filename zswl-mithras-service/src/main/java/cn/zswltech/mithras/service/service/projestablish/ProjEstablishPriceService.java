package cn.zswltech.mithras.service.service.projestablish;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceModifyREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.convert.projestablish.ProjEstablishPriceConverter;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishLeasePriceLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishAocPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePriceLib;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.ProjEstablishAocPriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.ProjEstablishFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.ProjEstablishLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.impl.ProjEstablishAocPriceLibHandler;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.impl.ProjEstablishFactoringPriceLibHandler;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.impl.ProjEstablishLeasePriceLibHandler;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/3 16:03
 */
@Service
public class ProjEstablishPriceService {
    @Resource
    private ProjEstablishBaseInfoService baseInfoService;
    @Resource
    private ProjEstablishAocPriceService aocPriceService;
    @Resource
    private ProjEstablishFactoringPriceService factoringPriceService;
    @Resource
    private ProjEstablishLeasePriceService leasePriceService;
    @Resource
    private ProjEstablishAocPriceLibService aocPriceLibService;
    @Resource
    private ProjEstablishFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjEstablishLeasePriceLibService leasePriceLibService;
    @Resource
    private ProjEstablishPriceConverter establishPriceConverter;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjEstablishLeasePriceLibMapper leasePriceLibMapper;
    @Resource
    private ProjEstablishAocPriceLibMapper aocPriceLibMapper;
    @Resource
    private ProjEstablishFactoringPriceLibMapper factoringPriceLibMapper;
    @Resource
    private ProjEstablishLeasePriceLibHandler leasePriceLibHandler;
    @Resource
    private ProjEstablishAocPriceLibHandler aocPriceLibHandler;
    @Resource
    private ProjEstablishFactoringPriceLibHandler factoringPriceLibHandler;

    public ProjEstablishPriceDetailRSP detail(Long projEstablishId) {
        return detail(projEstablishId, null);
    }

    public ProjEstablishPriceDetailRSP detail(Long projEstablishId, String version) {
        ProjEstablishPriceDetailRSP res = new ProjEstablishPriceDetailRSP();
        if (StringUtils.isBlank(version)) {
            // 前端不查版本
            // 处理租赁
            res.setLeasePriceRSP(BeanUtil.toBean(leasePriceService.getByProjEstablishId(projEstablishId), ProjEstablishLeasePriceRSP.class));
            // 处理保理
            ProjEstablishFactoringPriceRSP projEstablishFactoringPriceRSP = BeanUtil.toBean(factoringPriceService.getByProjEstablishId(projEstablishId), ProjEstablishFactoringPriceRSP.class);
            if (projEstablishFactoringPriceRSP != null) {
                //查询存续合同
                projEstablishFactoringPriceRSP.setContracts(getSurvivingContract(projEstablishId));
            }
            res.setFactoringPriceRSP(projEstablishFactoringPriceRSP);
            // 处理债权转让
            res.setAocPriceRSP(BeanUtil.toBean(aocPriceService.getByProjEstablishId(projEstablishId), ProjEstablishAocPriceRSP.class));
        } else {
            ProjEstablishLeasePriceLib leasePriceLib = leasePriceLibMapper.selectOne(Wrappers.<ProjEstablishLeasePriceLib>lambdaQuery().eq(ProjEstablishLeasePriceLib::getProjEstablishId, projEstablishId).eq(ProjEstablishLeasePriceLib::getVersion, version).last("LIMIT 1"));
            res.setLeasePriceRSP(Optional.ofNullable(leasePriceLib).map(l -> leasePriceLibHandler.actualLib2Rsp(leasePriceLib)).orElse(null));
            ProjEstablishFactoringPriceLib factoringPriceLib = factoringPriceLibMapper.selectOne(Wrappers.<ProjEstablishFactoringPriceLib>lambdaQuery().eq(ProjEstablishFactoringPriceLib::getProjEstablishId, projEstablishId).eq(ProjEstablishFactoringPriceLib::getVersion, version).last("LIMIT 1"));
            res.setFactoringPriceRSP(Optional.ofNullable(factoringPriceLib).map(l -> factoringPriceLibHandler.actualLib2Rsp(factoringPriceLib)).orElse(null));
            if (res.getFactoringPriceRSP() != null) {
                // TODO 查不到那个版本的存续合同情况
                res.getFactoringPriceRSP().setContracts(getSurvivingContract(projEstablishId));
            }
            ProjEstablishAocPriceLib aocPriceLib = aocPriceLibMapper.selectOne(Wrappers.<ProjEstablishAocPriceLib>lambdaQuery().eq(ProjEstablishAocPriceLib::getProjEstablishId, projEstablishId).eq(ProjEstablishAocPriceLib::getVersion, version).last("LIMIT 1"));
            res.setAocPriceRSP(Optional.ofNullable(aocPriceLib).map(l -> aocPriceLibHandler.actualLib2Rsp(aocPriceLib)).orElse(null));
        }
        return res;
    }

    public List<ContractBaseInfoRSP> getSurvivingContract(Long projEstablishId) {
        ProjEstablishBaseInfo projEstablishBaseInfo = baseInfoService.getById(projEstablishId);
        if (!Objects.equals(projEstablishBaseInfo.getBizType(), ProjectBizType.BL.name())) {
            return Collections.emptyList();
        }
        Map<Long, ProjEstablishPersonInfo> creditMap;
        Map<Long, ProjEstablishPersonInfo> debtorMap;
        if (StrUtil.isNotBlank(projEstablishBaseInfo.getCreditorInfo())) {
            creditMap = JSONUtil.toList(projEstablishBaseInfo.getCreditorInfo(), ProjEstablishPersonInfo.class).stream().collect(Collectors.toMap(ProjEstablishPersonInfo::getClientId, e -> e));
        } else {
            creditMap = Collections.emptyMap();
        }
        if (StrUtil.isNotBlank(projEstablishBaseInfo.getDebtorInfo())) {
            debtorMap = JSONUtil.toList(projEstablishBaseInfo.getDebtorInfo(), ProjEstablishPersonInfo.class).stream()
                    // 非法人无需关注，直接过滤掉
                    .filter(e -> !Objects.equals(e.getClientType(), GlobalConstants.DEBTOR_NO_CORPORATION))
                    .collect(Collectors.toMap(ProjEstablishPersonInfo::getClientId, e -> e));
        } else {
            debtorMap = Collections.emptyMap();
        }
        // 取出所有客户id
        List<Long> clientIds = new LinkedList<>();
        clientIds.addAll(creditMap.keySet());
        clientIds.addAll(debtorMap.keySet());
        if (CollectionUtil.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        // 查询合同模块债权债务人
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByClientIds(clientIds);
        if (CollectionUtil.isEmpty(contractTenantryList)) {
            return Collections.emptyList();
        }
        // 债权债务人和合同的关系
        Map<Long, List<Long>> clientContractMap = new HashMap<>();
        for (ContractTenantry contractTenantry : contractTenantryList) {
            List<Long> contractIdList = clientContractMap.get(contractTenantry.getLesseeId());
            if (Objects.isNull(contractIdList)) {
                contractIdList = new LinkedList<>();
                clientContractMap.put(contractTenantry.getLesseeId(), contractIdList);
            }
            contractIdList.add(contractTenantry.getContractId());
        }
        // 取出所有合同id查询合同信息
        Set<Long> contractIds = contractTenantryList.stream().map(ContractTenantry::getContractId).collect(Collectors.toSet());
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByIds(contractIds);
        // 过滤出新建、生效和起租的租赁合同
        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoList.stream()
                .filter(e -> {
                    if (!Objects.equals(e.getBizType(), ProjectBizType.ZL.name())) {
                        return false;
                    }
                    return Objects.equals(e.getContractStatus(), ContractStatus.TAKE_EFFECT.name())
                            || Objects.equals(e.getContractStatus(), ContractStatus.START_RENT.name())
                            || Objects.equals(e.getContractStatus(), ContractStatus.NEW.name());
                })
                .collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
        if (CollectionUtil.isEmpty(contractBaseInfoMap)) {
            return Collections.emptyList();
        }
        // 拼装返回参数
        List<ContractBaseInfoRSP> result = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(creditMap)) {
            List<ContractBaseInfoRSP> list = this.assembly(contractBaseInfoMap, creditMap, clientContractMap, CreditorDebtorTypeEnum.CREDITOR);
            if (CollectionUtil.isNotEmpty(list)) {
                result.addAll(list);
            }
        }
        if (CollectionUtil.isNotEmpty(debtorMap)) {
            List<ContractBaseInfoRSP> list = this.assembly(contractBaseInfoMap, debtorMap, clientContractMap, CreditorDebtorTypeEnum.DEBTOR);
            if (CollectionUtil.isNotEmpty(list)) {
                result.addAll(list);
            }
        }
        return result;
    }

    private List<ContractBaseInfoRSP> assembly(Map<Long, ContractBaseInfo> contractBaseInfoMap, Map<Long, ProjEstablishPersonInfo> personInfoMap, Map<Long, List<Long>> clientContractMap, CreditorDebtorTypeEnum creditorDebtorTypeEnum) {
        List<ContractBaseInfoRSP> result = new LinkedList<>();
        for (Map.Entry<Long, ProjEstablishPersonInfo> entry : personInfoMap.entrySet()) {
            Long clientId = entry.getKey();
            ProjEstablishPersonInfo personInfo = entry.getValue();
            List<Long> contractIdList = clientContractMap.get(clientId);
            if (CollectionUtil.isEmpty(contractIdList)) {
                continue;
            }
            for (Long contractId : contractIdList) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(contractId);
                if (Objects.isNull(contractBaseInfo)) {
                    continue;
                }
                ContractBaseInfoRSP rsp = new ContractBaseInfoRSP();
                rsp.setClientId(clientId);
                rsp.setClientName(personInfo.getClientName());
                rsp.setClientType(creditorDebtorTypeEnum.name());
                rsp.setClientTypeDisplay(creditorDebtorTypeEnum.display());
                rsp.setContractId(contractId);
                rsp.setContractNo(contractBaseInfo.getContractCode());
                rsp.setContractAmount(contractBaseInfo.getApplyCreditAmount());
                rsp.setContractDueDate(contractBaseInfo.getActualFinishDate());
                rsp.setContractStatus(contractBaseInfo.getContractStatus());
                result.add(rsp);
            }
        }
        return result;
    }

    public void modify(ProjEstablishPriceModifyREQ req) {
        ProjEstablishBaseInfo byId = null;
        if (req.getAocPriceModifyREQ() != null) {
            aocPriceService.modify(req.getAocPriceModifyREQ());
        }
        if (req.getFactoringPriceModifyREQ() != null) {
            factoringPriceService.modify(req.getFactoringPriceModifyREQ());
        }
        if (req.getLeasePriceModifyREQ() != null) {
            leasePriceService.modify(req.getLeasePriceModifyREQ());
        }
    }

    public ProjEstablishPriceDetailRSP versionDetail(Long projEstablishId, String version){
        ProjEstablishPriceDetailRSP rsp = new ProjEstablishPriceDetailRSP();
        ProjEstablishAocPriceLib aocPriceLib = aocPriceLibService.getOne(Wrappers.<ProjEstablishAocPriceLib>lambdaQuery()
                .eq(ProjEstablishAocPriceLib::getProjEstablishId, projEstablishId)
                .eq(ProjEstablishAocPriceLib::getVersion,version));
        if(ObjectUtil.isNotEmpty(aocPriceLib)) {
            rsp.setAocPriceRSP(establishPriceConverter.esAocPriceLibToRsp(aocPriceLib));
        }
        ProjEstablishFactoringPriceLib factoringPriceLib = factoringPriceLibService.getOne(Wrappers.<ProjEstablishFactoringPriceLib>lambdaQuery()
                .eq(ProjEstablishFactoringPriceLib::getProjEstablishId, projEstablishId)
                .eq(ProjEstablishFactoringPriceLib::getVersion, version));
        if (ObjectUtil.isNotEmpty(factoringPriceLib)) {
            rsp.setFactoringPriceRSP(establishPriceConverter.esFactoringPriceLibToRsp(factoringPriceLib));
        }
        ProjEstablishLeasePriceLib leasePriceLib = leasePriceLibService.getOne(Wrappers.<ProjEstablishLeasePriceLib>lambdaQuery()
                .eq(ProjEstablishLeasePriceLib::getProjEstablishId, projEstablishId)
                .eq(ProjEstablishLeasePriceLib::getVersion, version));
        if (ObjectUtil.isNotEmpty(factoringPriceLib)) {
            rsp.setLeasePriceRSP(establishPriceConverter.esLeasePriceLibToRsp(leasePriceLib));
        }
        return rsp;
    }


    public Map<Long, Long> newestPrice(Set<Long> establishIds) {
        List<ProjEstablishAocPriceLib> projEstablishAocPriceLibs = aocPriceLibService.listNewestByProjEstablishIds(establishIds);
        List<ProjEstablishFactoringPriceLib> projEstablishFactoringPriceLibs = factoringPriceLibService.listNewestByProjEstablishIds(establishIds);
        List<ProjEstablishLeasePriceLib> projEstablishLeasePriceLibs = leasePriceLibService.listNewestByProjEstablishIds(establishIds);

        Map<Long, Long> result = new HashMap<>();
        if (CollectionUtil.isNotEmpty(projEstablishAocPriceLibs)) {
            for (ProjEstablishAocPriceLib projEstablishAocPriceLib : projEstablishAocPriceLibs) {
                result.put(projEstablishAocPriceLib.getProjEstablishId(), LongUtil.null2zero(projEstablishAocPriceLib.getApplyCreditAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projEstablishFactoringPriceLibs)) {
            for (ProjEstablishFactoringPriceLib projEstablishFactoringPriceLib : projEstablishFactoringPriceLibs) {
                result.put(projEstablishFactoringPriceLib.getProjEstablishId(), LongUtil.null2zero(projEstablishFactoringPriceLib.getApplyCreditAmount()));
            }
        }
        if (CollectionUtil.isNotEmpty(projEstablishLeasePriceLibs)) {
            for (ProjEstablishLeasePriceLib projEstablishLeasePriceLib : projEstablishLeasePriceLibs) {
                result.put(projEstablishLeasePriceLib.getProjEstablishId(), LongUtil.null2zero(projEstablishLeasePriceLib.getApplyCreditAmount()));
            }
        }
        return result;
    }
}
