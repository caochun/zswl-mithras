package cn.zswltech.mithras.policy.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpAddREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpListREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpModifyREQ;
import cn.zswltech.mithras.policy.domain.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyTypeEnum;
import cn.zswltech.mithras.policy.excel.importer.PaymentPolicyExcelImporter;
import cn.zswltech.mithras.policy.excel.model.PaymentPolicyItemExcelModel;
import cn.zswltech.mithras.policy.infrastructure.persistence.mapper.PolicyInfoTmpMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfoTmp;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description 保单暂存表c
 * @author vico
 * @date 2023-10-23
 */
@Service
public class PolicyInfoTmpService extends ServiceImpl<PolicyInfoTmpMapper, PolicyInfoTmp> {

    @Resource
    private PolicyInfoTmpMapper policyInfoTmpMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentPolicyExcelImporter paymentPolicyExcelImporter;

    @Transactional(rollbackFor = Throwable.class)
    public Long add(PolicyInfoTmpAddREQ req) {
        PolicyInfoTmp info = BeanUtil.copyProperties(req, PolicyInfoTmp.class);
        policyInfoTmpMapper.insert(info);
        return info.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(PolicyInfoTmpModifyREQ req) {
        PolicyInfoTmp originalInfo = policyInfoTmpMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        PolicyInfoTmp info = BeanUtil.copyProperties(req, PolicyInfoTmp.class);
        policyInfoTmpMapper.updateById(info);
    }

    public Page<PolicyInfoTmp> list(PolicyInfoTmpListREQ req) {
        return policyInfoTmpMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<PolicyInfoTmp>lambdaQuery()
                .eq(PolicyInfoTmp::getContractId, req.getContractId())
                .le(ObjectUtil.isNotNull(req.getAdventFlag()) && req.getAdventFlag(), PolicyInfoTmp::getInsuranceEndDate, LocalDate.now().plusDays(5)));
    }

    @Transactional(rollbackFor = Throwable.class)
    public String importExcel(InputStream inputStream, Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            throw new MithrasException("合同信息为空");
        }
        List<PaymentPolicyItemExcelModel> policyItemExcelModels = paymentPolicyExcelImporter.parse(inputStream);
        if (CollectionUtils.isEmpty(policyItemExcelModels)) {
            throw new MithrasException("导入的文件数据为空");
        }
        checkImport(policyItemExcelModels);
        Set<String> policyCodeSet = new HashSet<>();
        policyItemExcelModels.forEach(model -> policyCodeSet.add(model.getPolicyCode()));
        List<PolicyInfoTmp> saveBeans = new ArrayList<>();
        for (PaymentPolicyItemExcelModel paymentPolicyItemExcelModel : policyItemExcelModels) {
            PolicyInfoTmp tmpPolicy = BeanUtil.copyProperties(paymentPolicyItemExcelModel, PolicyInfoTmp.class);
            tmpPolicy.setPolicyAmount(LongUtil.other2Long(paymentPolicyItemExcelModel.getPolicyAmount().toString()));
            PolicyTypeEnum policyType = PolicyTypeEnum.find(paymentPolicyItemExcelModel.getPolicyType());
            tmpPolicy.setPolicyType(policyType == null ? null : policyType.name());
            tmpPolicy.setContractId(contractBaseInfo.getId());
            tmpPolicy.setContractCode(contractBaseInfo.getContractCode());
            saveBeans.add(tmpPolicy);
        }
        saveBatch(saveBeans);
        return "保单导入成功";
    }

    private void checkImport(List<PaymentPolicyItemExcelModel> policyItemExcelModels) {
        Set<String> codeSet = new HashSet<>();
        for (int i = 0; i < policyItemExcelModels.size(); i++) {
            PaymentPolicyItemExcelModel paymentPolicyItemExcelModel = policyItemExcelModels.get(i);
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getPolicyCode())) {
                throw new MithrasException(String.format("第%s行保单编号为空", i + 1));
            }
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getPolicyAmount())) {
                throw new MithrasException(String.format("第%s行保单金额为空", i + 1));
            }
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getInsuranceStartDate())) {
                throw new MithrasException(String.format("第%s行保险起始日为空", i + 1));
            }
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getInsuranceEndDate())) {
                throw new MithrasException(String.format("第%s行保险到期日为空", i + 1));
            }
            if (paymentPolicyItemExcelModel.getInsuranceStartDate().isAfter(paymentPolicyItemExcelModel.getInsuranceEndDate())) {
                throw new MithrasException(String.format("第%s行保险到期小于保险起始日", i + 1));
            }
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getInsuranceCompany())) {
                throw new MithrasException(String.format("第%s行保险公司名称为空", i + 1));
            }
            codeSet.add(paymentPolicyItemExcelModel.getPolicyCode());
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getRenewInsuranceFlag())) {
                throw new MithrasException(String.format("第%s行保单是否续保为空", i + 1));
            }
            PolicyRenewInsuranceEnum renewInsurance = PolicyRenewInsuranceEnum.find(paymentPolicyItemExcelModel.getRenewInsuranceFlag());
            if (renewInsurance == null) {
                throw new MithrasException(String.format("第%s行保单未知的续保方式", i + 1));
            }
            paymentPolicyItemExcelModel.setRenewInsuranceFlag(renewInsurance.name());
        }
    }
}
