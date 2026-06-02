package cn.zswltech.mithras.service.service.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpAddREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpListREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.policy.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.service.enums.policy.PolicyTypeEnum;
import cn.zswltech.mithras.service.excel.importer.PaymentPolicyExcelImporter;
import cn.zswltech.mithras.service.excel.model.PaymentPolicyItemExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.policy.PolicyInfoTmp;
import cn.zswltech.mithras.service.mapper.policy.PolicyInfoTmpMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
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
import java.util.stream.Collectors;

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
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentPolicyExcelImporter paymentPolicyExcelImporter;

    @Transactional(rollbackFor = Throwable.class)
    public Long add(PolicyInfoTmpAddREQ req) {
        PolicyInfoTmp info = BeanUtil.copyProperties(req, PolicyInfoTmp.class);
//        if (policyInfoTmpMapper.selectCount(Wrappers.<PolicyInfoTmp>lambdaQuery()
//                .eq(PolicyInfoTmp::getPolicyCode, req.getPolicyCode())) > 0) {
//            throw new MithrasException("保单编号重复");
//        }
        policyInfoTmpMapper.insert(info);
        return info.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(PolicyInfoTmpModifyREQ req) {
        PolicyInfoTmp originalInfo = policyInfoTmpMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
//        if (policyInfoTmpMapper.selectCount(Wrappers.<PolicyInfoTmp>lambdaQuery()
//                .eq(PolicyInfoTmp::getPolicyCode, req.getPolicyCode())
//                .ne(PolicyInfoTmp::getId, req.getId())) > 0) {
//            throw new MithrasException("保单编号重复");
//        }
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
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if(ObjectUtil.isEmpty(contractBaseInfo)){
            throw new MithrasException("合同信息为空");
        }
        List<PaymentPolicyItemExcelModel> policyItemExcelModels = paymentPolicyExcelImporter.parse(inputStream);
        if (CollectionUtils.isEmpty(policyItemExcelModels)) {
            throw new MithrasException("导入的文件数据为空");
        }
        checkImport(policyItemExcelModels);
        //判断本身有没有重复
        Set<String> policyCodeSet = new HashSet<>();
        policyItemExcelModels.forEach(model -> {
//            if(policyCodeSet.contains(model.getPolicyCode())) {
//                throw new MithrasException(model.getPolicyCode() + "保单号在文件中重复，请修改后重新导入");
//            }
            policyCodeSet.add(model.getPolicyCode());
        });
        //导入
        List<PolicyInfoTmp> saveBeans = new ArrayList<>();
        PolicyInfoTmp tmpPolicy;
//        List<String> policyCodes = policyItemExcelModels.stream().map(PaymentPolicyItemExcelModel::getPolicyCode).collect(Collectors.toList());
//        List<PolicyInfoTmp> policyInfoTmpList = baseMapper.selectList(Wrappers.<PolicyInfoTmp>lambdaQuery()
//        .in(PolicyInfoTmp::getPolicyCode, policyCodes));
//        Set<String> allPolicyCode = new HashSet<>();
//        if(ObjectUtil.isNotEmpty(policyInfoTmpList)) {
//            allPolicyCode = policyInfoTmpList.stream().map(PolicyInfoTmp::getPolicyCode).collect(Collectors.toSet());
//        }
        //判断是否重复
        StringBuilder stringBuilder = new StringBuilder();
        PolicyTypeEnum of = null;
        for (PaymentPolicyItemExcelModel paymentPolicyItemExcelModel : policyItemExcelModels) {
//            if(allPolicyCode.contains(paymentPolicyItemExcelModel.getPolicyCode())) {
//                stringBuilder.append(paymentPolicyItemExcelModel.getPolicyCode());
//                stringBuilder.append(",");
//                continue;
//            }
            tmpPolicy = BeanUtil.copyProperties(paymentPolicyItemExcelModel, PolicyInfoTmp.class);
            tmpPolicy.setPolicyAmount(LongUtil.other2Long(paymentPolicyItemExcelModel.getPolicyAmount().toString()));
            of = PolicyTypeEnum.find(paymentPolicyItemExcelModel.getPolicyType());
            tmpPolicy.setPolicyType(of == null ? null : of.name());
            tmpPolicy.setContractId(contractBaseInfo.getId());
            tmpPolicy.setContractCode(contractBaseInfo.getContractCode());
            saveBeans.add(tmpPolicy);
        }
        SpringContextHolder.getBean(PolicyInfoTmpService.class).saveBatch(saveBeans);
//        if(stringBuilder.length() > 0){
//            stringBuilder.append("保单编号重复");
//        } else {
            stringBuilder.append("保单导入成功");
//        }
        return stringBuilder.toString();
    }
    private void checkImport(List<PaymentPolicyItemExcelModel> policyItemExcelModels) {
        PaymentPolicyItemExcelModel paymentPolicyItemExcelModel;
        //保单号不能重复
        Set<String> codeSet = new HashSet<>();
        for (int i = 0; i < policyItemExcelModels.size(); i++) {
            paymentPolicyItemExcelModel = policyItemExcelModels.get(i);
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
            if(paymentPolicyItemExcelModel.getInsuranceStartDate().isAfter(paymentPolicyItemExcelModel.getInsuranceEndDate())){
                throw new MithrasException(String.format("第%s行保险到期小于保险起始日", i + 1));
            }
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getInsuranceCompany())) {
                throw new MithrasException(String.format("第%s行保险公司名称为空", i + 1));
            }
//            if (codeSet.contains(paymentPolicyItemExcelModel.getPolicyCode())) {
//                throw new MithrasException(String.format("保单编号%s重复", paymentPolicyItemExcelModel.getPolicyCode()));
//            } else {
                codeSet.add(paymentPolicyItemExcelModel.getPolicyCode());
//            }
            if (ObjectUtil.isNull(paymentPolicyItemExcelModel.getRenewInsuranceFlag())) {
                throw new MithrasException(String.format("第%s行保单是否续保为空", i + 1));
            } else {
                PolicyRenewInsuranceEnum of = PolicyRenewInsuranceEnum.find(paymentPolicyItemExcelModel.getRenewInsuranceFlag());
                if (of != null) {
                    paymentPolicyItemExcelModel.setRenewInsuranceFlag(of.name());
                } else {
                    throw new MithrasException(String.format("第%s行保单未知的续保方式", i + 1));
                }
            }
        }
    }

}