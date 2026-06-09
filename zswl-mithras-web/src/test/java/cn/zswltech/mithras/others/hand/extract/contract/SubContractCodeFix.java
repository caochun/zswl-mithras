package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.enums.contract.ContractModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.application.ContractMortgageService;
import cn.zswltech.mithras.contract.core.application.ContractPledgeService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractMortgageLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractPledgeLibService;
import cn.zswltech.mithras.service.util.ContractUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/10/19
 * @description 子合同编号修复
 */
@Slf4j
public class SubContractCodeFix extends ApplicationTest {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private ContractMortgageLibService contractMortgageLibService;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private ContractPledgeLibService contractPledgeLibService;

    @Test
    public void fixSubContractCode() {
        List<ContractBaseInfo> all = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, ListUtil.toList(1152L)));
        if (CollectionUtil.isEmpty(all)) {
            return;
        }
        for (ContractBaseInfo contractBaseInfo : all) {
            // 获取主合同编号
            String contractCode = contractBaseInfo.getContractCode();
            if (StrUtil.isBlank(contractCode)) {
                log.warn("主合同编号为空，忽略不处理[contractId: {}]", contractBaseInfo.getId());
                continue;
            }
            // 修复咨询合同编号
            this.fixConsultingContractCode(contractBaseInfo);
            // 修复担保合同编号
            this.fixGuarantorContractCode(contractBaseInfo);
            // 修复抵押合同编号
            this.fixMortgageContractCode(contractBaseInfo);
            // 修复质押合同编号
            this.fixPledgeContractCode(contractBaseInfo);
        }
    }

    private void fixConsultingContractCode(ContractBaseInfo contractBaseInfo) {
        List<String> consultingContractCodeList = ContractUtil.generateSubContractCode(ContractModelEnum.CONSULT, contractBaseInfo.getContractCode(), 1);
        String consultingContractCode = consultingContractCodeList.get(0);
        LambdaUpdateWrapper<ContractBaseInfo> updateContractBaseInfo = Wrappers.lambdaUpdate();
        updateContractBaseInfo.set(ContractBaseInfo::getConsultingContractCode, consultingContractCode);
        updateContractBaseInfo.eq(ContractBaseInfo::getId, contractBaseInfo.getId());
        contractBaseInfoService.update(updateContractBaseInfo);
        LambdaUpdateWrapper<ContractBaseInfoLib> updateContractBaseInfoLib = Wrappers.lambdaUpdate();
        updateContractBaseInfoLib.set(ContractBaseInfo::getConsultingContractCode, consultingContractCode);
        updateContractBaseInfoLib.eq(ContractBaseInfoLib::getOriginId, contractBaseInfo.getId());
        contractBaseInfoLibService.update(updateContractBaseInfoLib);
    }

    private void fixGuarantorContractCode(ContractBaseInfo contractBaseInfo) {
        List<ContractGuarantor> contractGuarantorList = contractGuarantorService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractGuarantorList)) {
            return;
        }
        List<String> guarantorContractCodeList = ContractUtil.generateSubContractCode(ContractModelEnum.GUARANTEE, contractBaseInfo.getContractCode(), contractGuarantorList.size());
        for (int i = 0; i < contractGuarantorList.size(); i++) {
            String guarantorContractCode = guarantorContractCodeList.get(i);
            ContractGuarantor contractGuarantor = contractGuarantorList.get(i);
            LambdaUpdateWrapper<ContractGuarantor> updateContractGuarantor = Wrappers.lambdaUpdate();
            updateContractGuarantor.set(ContractGuarantor::getGuarantorContractCode, guarantorContractCode);
            updateContractGuarantor.eq(ContractGuarantor::getId, contractGuarantor.getId());
            contractGuarantorService.update(updateContractGuarantor);
            LambdaUpdateWrapper<ContractGuarantorLib> updateContractGuarantorLib = Wrappers.lambdaUpdate();
            updateContractGuarantorLib.set(ContractGuarantor::getGuarantorContractCode, guarantorContractCode);
            updateContractGuarantorLib.eq(ContractGuarantorLib::getOriginId, contractGuarantor.getId());
            contractGuarantorLibService.update(updateContractGuarantorLib);
        }
    }

    private void fixMortgageContractCode(ContractBaseInfo contractBaseInfo) {
        List<ContractMortgage> contractMortgageList = contractMortgageService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractMortgageList)) {
            return;
        }
        List<String> mortgageContractCodeList = ContractUtil.generateSubContractCode(ContractModelEnum.MORTGAGE, contractBaseInfo.getContractCode(), contractMortgageList.size());
        for (int i = 0; i < contractMortgageList.size(); i++) {
            String mortgageContractCode = mortgageContractCodeList.get(i);
            ContractMortgage contractMortgage = contractMortgageList.get(i);
            LambdaUpdateWrapper<ContractMortgage> updateContractMortgage = Wrappers.lambdaUpdate();
            updateContractMortgage.set(ContractMortgage::getMortgageContractCode, mortgageContractCode);
            updateContractMortgage.eq(ContractMortgage::getId, contractMortgage.getId());
            contractMortgageService.update(updateContractMortgage);
            LambdaUpdateWrapper<ContractMortgageLib> updateContractMortgageLib = Wrappers.lambdaUpdate();
            updateContractMortgageLib.set(ContractMortgage::getMortgageContractCode, mortgageContractCode);
            updateContractMortgageLib.eq(ContractMortgageLib::getOriginId, contractMortgage.getId());
            contractMortgageLibService.update(updateContractMortgageLib);
        }
    }

    private void fixPledgeContractCode(ContractBaseInfo contractBaseInfo) {
        List<ContractPledge> contractPledgeList = contractPledgeService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractPledgeList)) {
            return;
        }
        List<String> pledgeContractCodeList = ContractUtil.generateSubContractCode(ContractModelEnum.PLEDGE, contractBaseInfo.getContractCode(), contractPledgeList.size());
        for (int i = 0; i < contractPledgeList.size(); i++) {
            String pledgeContractCode = pledgeContractCodeList.get(i);
            ContractPledge contractPledge = contractPledgeList.get(i);
            LambdaUpdateWrapper<ContractPledge> updateContractPledge = Wrappers.lambdaUpdate();
            updateContractPledge.set(ContractPledge::getPledgeContractCode, pledgeContractCode);
            updateContractPledge.eq(ContractPledge::getId, contractPledge.getId());
            contractPledgeService.update(updateContractPledge);
            LambdaUpdateWrapper<ContractPledgeLib> updateContractPledgeLib = Wrappers.lambdaUpdate();
            updateContractPledgeLib.set(ContractPledge::getPledgeContractCode, pledgeContractCode);
            updateContractPledgeLib.eq(ContractPledgeLib::getOriginId, contractPledge.getId());
            contractPledgeLibService.update(updateContractPledgeLib);
        }
    }
}
