package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectBasicRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardProjectBasicResult;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractGuarantorService;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
public abstract class DashboardProjectService {
    @Resource
    protected Id2NameService id2NameService;

    protected <T extends DashboardProjectBasicRSP, M extends DashboardProjectBasicResult> List<T> buildRspList(List<M> dbResultList, DashboardProjectStageCustomConvert<T,M> convert) {
        List<T> rspList = new LinkedList<>();
        Set<Long> bizDeptIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (M dbResult : dbResultList) {
            if (Objects.nonNull(dbResult.getBizDeptId())) {
                bizDeptIds.add(dbResult.getBizDeptId());
            }
            if (Objects.nonNull(dbResult.getProjSponsorUserId())) {
                userIds.add(dbResult.getProjSponsorUserId());
            }
            if (StrUtil.isNotBlank(dbResult.getProjCosponsorUserIdsJson())) {
                List<Long> list = JSONUtil.toList(dbResult.getProjCosponsorUserIdsJson(), Long.class);
                if (CollectionUtil.isNotEmpty(list)) {
                    userIds.addAll(list);
                }
            }
        }
        Map<Long, String> bizDeptMap = CollectionUtil.isEmpty(bizDeptIds) ? Collections.emptyMap() : id2NameService.deptId2Name(bizDeptIds);
        Map<Long, String> userMap = CollectionUtil.isEmpty(userIds) ? Collections.emptyMap() : id2NameService.sysUserId2Name(userIds);
        for (M dbResult : dbResultList) {
            T rsp = convert.convert(dbResult);
            // 补全基类参数
            rsp.setProjName(dbResult.getProjName());
            rsp.setMainId(dbResult.getMainId());
            rsp.setClientId(dbResult.getClientId());
            rsp.setClientName(dbResult.getClientName());
            rsp.setLengthOfStay(new ValueUnitDTO(Optional.ofNullable(dbResult.getWorkdaysOnStage()).map(Object::toString).orElse(""), "工作日"));
            rsp.setRiskControlIndustryClassifyCode(dbResult.getRiskControlIndustryClassifyCode());
            rsp.setRiskControlIndustryClassifyDisplay(Optional.ofNullable(RiskControlIndustryClassify.findByName(dbResult.getRiskControlIndustryClassifyCode())).map(RiskControlIndustryClassify::display).orElse(""));
            rsp.setIndustryTypeCode(dbResult.getIndustryCode());
            rsp.setIndustryTypeDisplay(dbResult.getIndustryDisplay());
            rsp.setBizTypeCode(dbResult.getBizTypeCode());
            rsp.setBizTypeDisplay(Optional.ofNullable(ProjectBizType.of(dbResult.getBizTypeCode())).map(ProjectBizType::display).orElse(""));
            rsp.setLeaseTypeCode(dbResult.getLeaseTypeCode());
            rsp.setLeaseTypeDisplay(Optional.ofNullable(LeaseType.of(dbResult.getLeaseTypeCode())).map(LeaseType::display).orElse(""));
            if (StrUtil.isNotBlank(dbResult.getGuaranteeInfoJson())) {
                List<ProjEstablishPersonInfo> list = JSONUtil.toList(dbResult.getGuaranteeInfoJson(), ProjEstablishPersonInfo.class);
                if (CollectionUtil.isNotEmpty(list)) {
                    List<String> ids = new LinkedList<>();
                    List<String> names = new LinkedList<>();
                    for (ProjEstablishPersonInfo projEstablishPersonInfo : list) {
                        if (Objects.nonNull(projEstablishPersonInfo.getClientId())) {
                            ids.add(projEstablishPersonInfo.getClientId().toString());
                            names.add(projEstablishPersonInfo.getClientName());
                        }
                    }
                    rsp.setGuarantorIds(CharSequenceUtil.join(",", ids));
                    rsp.setGuarantorNames(CharSequenceUtil.join(",", names));
                }
            }
            rsp.setBizDeptId(dbResult.getBizDeptId());
            rsp.setBizDeptName(bizDeptMap.get(dbResult.getBizDeptId()));
            rsp.setProjSponsorUserId(dbResult.getProjSponsorUserId());
            rsp.setProjSponsorUserName(userMap.get(dbResult.getProjSponsorUserId()));
            if (StrUtil.isNotBlank(dbResult.getProjCosponsorUserIdsJson())) {
                List<Long> list = JSONUtil.toList(dbResult.getProjCosponsorUserIdsJson(), Long.class);
                if (CollectionUtil.isNotEmpty(list)) {
                    List<String> ids = new LinkedList<>();
                    List<String> names = new LinkedList<>();
                    for (Long id : list) {
                        if(id != null){
                            ids.add(id.toString());
                            names.add(userMap.get(id));
                        }
                    }
                    rsp.setProjCosponsorUserIds(CharSequenceUtil.join(",", ids));
                    rsp.setProjCosponsorUserNames(CharSequenceUtil.join(",", names));
                }
            }
            rsp.setApplyTime(dbResult.getCreateTime());
            rsp.setApproveElapsedTime(new ValueUnitDTO(Optional.ofNullable(dbResult.getProcessWorkdays()).map(Object::toString).orElse(""), "工作日"));
            rspList.add(rsp);
        }
        return rspList;
    }

    protected  <T extends DashboardProjectBasicRSP> void fillGuarantorInfo(List<T> list) {
        Set<Long> contractIds = new HashSet<>();
        list.forEach(e -> {
            if (Objects.nonNull(e.getContractId())) {
                contractIds.add(e.getContractId());
            }
        });
        if (CollectionUtil.isEmpty(contractIds)) {
            return;
        }
        List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractIds(contractIds);
        if (CollectionUtil.isEmpty(contractGuarantorList)) {
            return;
        }
        Set<Long> clientIds = new HashSet<>();
        Map<Long, List<Long>> contractGuarantorMap = new HashMap<>(512);
        for (ContractGuarantor contractGuarantor : contractGuarantorList) {
            if (StrUtil.isBlank(contractGuarantor.getGuarantorIds())) {
                continue;
            }
            List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
            if (CollectionUtil.isEmpty(ids)) {
                continue;
            }
            clientIds.addAll(ids);
            List<Long> valueList = contractGuarantorMap.get(contractGuarantor.getContactId());
            if (Objects.isNull(valueList)) {
                valueList = new LinkedList<>();
                contractGuarantorMap.put(contractGuarantor.getContactId(), valueList);
            }
            valueList.addAll(ids);
        }
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        // 组装
        list.forEach(e -> {
            List<Long> ids = contractGuarantorMap.get(e.getContractId());
            if (CollectionUtil.isNotEmpty(ids)) {
                List<String> names = new LinkedList<>();
                for (Long id : ids) {
                    names.add(clientMap.get(id));
                }
                e.setGuarantorIds(CharSequenceUtil.join(",", ids));
                e.setGuarantorNames(CharSequenceUtil.join(",", names));
            }
        });
    }
}
