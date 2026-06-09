package cn.zswltech.mithras.service.application.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.policy.application.PolicyInfoApplicationService;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.policy.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.policy.domain.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyStatusEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyTypeEnum;
import cn.zswltech.mithras.policy.excel.importer.PaymentPolicyExcelImporter;
import cn.zswltech.mithras.policy.excel.model.PaymentPolicyItemExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.policy.PolicyInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @create: 2023-06-15
 **/

@Slf4j
@Service
public class PolicyInfoFacade implements PolicyInfoApplicationService {

    @Resource
    private PolicyInfoService policyInfoService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private PaymentPolicyExcelImporter paymentPolicyExcelImporter;

    @Override
    public R<List<SelectRSP>> projList() {
        return R.ok(policyInfoService.projList());
    }

    @Override
    public R<List<SelectRSP>> contractList(PolicyAddContractREQ req) {
        return R.ok(policyInfoService.contractList(req));
    }

    @Override
    public R<Long> add(@Valid PolicyInfoAddREQ req) {
        return R.ok(policyInfoService.add(req));
    }

    @Override
    public R<Void> submit(PolicyInfoSubmitREQ req) {
        policyInfoService.submit(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(@Valid PolicyInfoModifyREQ req) {
        policyInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PolicyInfoDetailRSP> detail(@Valid PolicyInfoDetailREQ req) {
        return R.ok(policyInfoService.detail(req));
    }

    @Override
    public R<PageR<PolicyInfoListRSP>> list(@Valid PolicyInfoListREQ req) {
        return R.ok(policyInfoService.list(req));
    }

    @Override
    public R<Void> remove(@Valid PolicyInfoRemoveREQ req) {
        policyInfoService.remove(req);
        return R.ok();
    }

    @Override
    public R<String> importExcel(@Valid PaymentPoliceImportREQ paymentPoliceImportREQ) {

        try {
            List<PaymentPolicyItemExcelModel> policyItemExcelModels = paymentPolicyExcelImporter.parse(paymentPoliceImportREQ.getFile().getInputStream());
            if (CollectionUtils.isEmpty(policyItemExcelModels)) {
                throw new MithrasException("导入的文件数据为空");
            }
            StringBuilder stringBuilder = new StringBuilder();
            PolicyTypeEnum of = null;
            PolicyRenewInsuranceEnum insuranceEnum = null;
            if(ObjectUtil.isNotEmpty(paymentPoliceImportREQ.getPolicyId())){
                if(policyItemExcelModels.size() == 1){
                    PolicyInfoAddREQ req  = BeanUtil.copyProperties(policyItemExcelModels.get(0), PolicyInfoAddREQ.class);
                    req.setPaymentId(paymentPoliceImportREQ.getPaymentId());
                    req.setPolicyId(paymentPoliceImportREQ.getPolicyId());
                    of = PolicyTypeEnum.find(req.getPolicyType());
                    req.setPolicyType(of == null ? null : of.name());
                    insuranceEnum = PolicyRenewInsuranceEnum.find(req.getRenewInsuranceFlag());
                    req.setRenewInsuranceFlag(insuranceEnum == null ? null : insuranceEnum.name());
                    req.setPolicyAmount(LongUtil.other2Long(LongUtil.null2zero(req.getPolicyAmount()).toString()));
                    policyInfoService.add(req);
                } else {
                    throw new MithrasException("续保保单仅支持导入一条");
                }
            } else {
//                Map<String, Integer> stringIntegerMap =
//                        policyInfoService.countPolicyCodeNum(policyItemExcelModels.stream().map(PaymentPolicyItemExcelModel::getPolicyCode).collect(Collectors.toList()));
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentPoliceImportREQ.getPaymentId());
                if(ObjectUtil.isEmpty(paymentBaseInfo)){
                    throw new MithrasException("付款信息为空");
                }
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
                List<PolicyInfo> add = new ArrayList<PolicyInfo>();
                PolicyInfo policyInfo;
                for(PaymentPolicyItemExcelModel model : policyItemExcelModels){
//                    if(ObjectUtil.isNotEmpty(stringIntegerMap)){
//                        if(LongUtil.null2zero(stringIntegerMap.get(model.getPolicyCode())) > 0){
//                            stringBuilder.append(model.getPolicyCode());
//                            stringBuilder.append(",");
//                            continue;
//                        }
//                    }
                    policyInfo = BeanUtil.copyProperties(model, PolicyInfo.class);
                    policyInfo.setPaymentId(paymentPoliceImportREQ.getPaymentId());
                    policyInfo.setLevel(0);
                    policyInfo.setProjId(contractBaseInfo.getProjReviewId());
                    policyInfo.setContractId(contractBaseInfo.getId());
                    policyInfo.setContractCode(contractBaseInfo.getContractCode());
                    policyInfo.setPolicyStatus(PolicyStatusEnum.EFFECT.name());
                    policyInfo.setAutomatic(0);
                    of = PolicyTypeEnum.find(model.getPolicyType());
                    policyInfo.setPolicyType(of == null ? null : of.name());
                    insuranceEnum = PolicyRenewInsuranceEnum.find(model.getRenewInsuranceFlag());
                    policyInfo.setRenewInsuranceFlag(insuranceEnum == null ? null : insuranceEnum.name());
                    policyInfo.setPolicyAmount(LongUtil.other2Long(model.getPolicyAmount().toString()));
                    add.add(policyInfo);
                }
                policyInfoService.saveBatch(add);
            }
//            if(stringBuilder.length() > 0){
//                stringBuilder.append("保单编号重复");
//            } else {
                stringBuilder.append("保单导入成功");
//            }
            return R.ok(stringBuilder.toString());
        }catch (MithrasException e){
            throw e;
        }catch(Exception e) {
            log.error("", e);
            throw new MithrasException("导入保单文件发生异常");
        }
    }
}
