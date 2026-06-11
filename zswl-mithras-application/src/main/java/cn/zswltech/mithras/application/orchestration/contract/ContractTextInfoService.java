package cn.zswltech.mithras.application.orchestration.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.contract.file.ContractTextInfoREQ;
import cn.zswltech.mithras.dto.contract.file.ContractTextInfoRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTextTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractTextInfoMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author dingqi
 * @date 2024/7/29
 * @description
 */
@Service
public class ContractTextInfoService extends ServiceImpl<ContractTextInfoMapper, ContractTextInfo> {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private SysUserService sysUserService;

    public void saveTextInfo(ContractTextInfoREQ req) {
        List<String> jobs = sysUserService.queryUserJobList(AccountUtil.getLoginInfo().getId());
        if (!jobs.contains(JobEnum.projmanager.name()) && !jobs.contains(JobEnum.yunYingGuanLi.name())) {
            throw new MithrasException("仅项目经理和运营管理（经办）可操作");
        }
        // 保存前先查询一下相关合同文本
        List<String> types = ListUtil.of(ContractTypeEnum.MAIN_CONTRACT.name(), ContractTypeEnum.CONSULTING_CONTRACT.name(), ContractTypeEnum.GUARANTEE_CONTRACT.name(), ContractTypeEnum.MORTGAGE_CONTRACT.name());
        List<MaterialsList> materialsList = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), types, Collections.singletonList(req.getContractId()));
//        long count = materialsList.stream().filter(e -> Objects.equals(e.getSystemGenerate(), YesOrNoNumberEnum.NO.getCode())).count();
//        if (count > 0) {
//            // 说明合同文本已经有非自动生成的文件，不允许把合同文本类型选为标准合同文本
//            if (req.getTextTypeList().contains(ContractTextTypeEnum.STANDARD_TEXT.name())) {
//                throw new MithrasException("主合同、咨询合同、保证合同、抵押合同存在修改，请选择对应的合同“非标准文本”类型后再保存");
//            }
//        }
        if (CollectionUtil.isNotEmpty(materialsList)) {
            Set<String> containsTypes = new HashSet<>();
            for (MaterialsList m : materialsList) {
                if (Objects.equals(m.getSystemGenerate(), YesOrNoNumberEnum.NO.getCode())) {
                    ContractTypeEnum contractTypeEnum = ContractTypeEnum.getByName(m.getMaterialsType());
                    containsTypes.add(Optional.ofNullable(contractTypeEnum).map(ContractTypeEnum::display).orElse("未知类型"));
                }
            }
            if (CollectionUtil.isNotEmpty(containsTypes) && req.getTextTypeList().contains(ContractTextTypeEnum.STANDARD_TEXT.name())) {
                throw new MithrasException(String.format("%s存在非自动生成的合同，请选择对应的合同“非标准文本”类型后再保存", CharSequenceUtil.join("、", containsTypes)));
            }
        }
        ContractTextInfo exist = this.getOneByContractId(req.getContractId());
        ContractTextInfo target = new ContractTextInfo();
        target.setTextType(CharSequenceUtil.join(",", req.getTextTypeList()));
        if (Objects.nonNull(exist)) {
            if (jobs.contains(JobEnum.projmanager.name()) && Objects.equals(YesOrNoNumberEnum.YES.getCode(), exist.getIsConfirmed())) {
                // 如果运营经理已经确认过，则项目经理不允许修改
                throw new MithrasException("合同文本类型已确认，不允许修改保存");
            }
            target.setId(exist.getId());
            this.updateById(target);
        } else {
            target.setContractId(req.getContractId());
            this.save(target);
        }
    }

    public ContractTextInfoRSP getTextInfo(Long contractId) {
        ContractTextInfoRSP rsp = new ContractTextInfoRSP();
        rsp.setContractId(contractId);
        ContractTextInfo exist = this.getOneByContractId(contractId);
        if (Objects.isNull(exist)) {
            return rsp;
        }
        rsp.setId(exist.getId());
        rsp.setTextTypeList(CharSequenceUtil.split(exist.getTextType(), ","));
        return rsp;
    }

    public ContractTextInfo getOneByContractId(Long contractId) {
        LambdaQueryWrapper<ContractTextInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractTextInfo::getContractId, contractId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public void confirm(Long contractId) {
        ContractTextInfo contractTextInfo = this.getOneByContractId(contractId);
        if (Objects.nonNull(contractTextInfo)) {
            contractTextInfo.setContractId(contractId);
            contractTextInfo.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
            this.updateById(contractTextInfo);
        }
    }

    public void cancelConfirm(Long contractId) {
        ContractTextInfo contractTextInfo = this.getOneByContractId(contractId);
        if (Objects.nonNull(contractTextInfo)) {
            contractTextInfo.setContractId(contractId);
            contractTextInfo.setIsConfirmed(YesOrNoNumberEnum.NO.getCode());
            this.updateById(contractTextInfo);
        }
    }
}
