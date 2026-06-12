package cn.zswltech.mithras.application.orchestration.payment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.payment.lib.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.payment.application.PaymentUpdateAdvice;
import cn.zswltech.mithras.payment.application.convert.PaymentConvert;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.policy.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.policy.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.enums.PolicyStatusEnum;
import cn.zswltech.mithras.policy.enums.PolicyTypeEnum;
import cn.zswltech.mithras.policy.excel.exporter.PaymentPolicyExcelExporter;
import cn.zswltech.mithras.policy.excel.importer.PaymentPolicyExcelImporter;
import cn.zswltech.mithras.policy.excel.model.PaymentPolicyExcelModel;
import cn.zswltech.mithras.policy.excel.model.PaymentPolicyItemExcelModel;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentPolicyInfo;
import cn.zswltech.mithras.policy.persistence.model.PolicyInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.policy.persistence.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.payment.versioning.service.PaymentPolicyInfoLibService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.application.orchestration.policy.PolicyInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description payment_policy_info
 * @date 2022-09-13
 */
@Service
public class PaymentPolicyInfoService extends ServiceImpl<PaymentPolicyInfoMapper, PaymentPolicyInfo>
        implements PaymentUpdateAdvice {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Resource
    private PaymentPolicyExcelImporter paymentPolicyExcelImporter;

    @Resource
    private PaymentPolicyInfoLibService policyInfoLibService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private PolicyInfoMapper policyInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PaymentConvert paymentConvert;
    @Resource
    private PaymentPolicyExcelExporter paymentPolicyExcelExporter;

    @Transactional(rollbackFor = Throwable.class)
    public void add(MultipartFile[] files,Long paymentId,PaymentPolicyInfoAddREQ req) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(paymentId);
        saveCheck(baseInfo);
//        if (baseMapper.countPolicyCode(req.getPolicyCode()) > 0 ||
//                policyInfoMapper.selectCount(Wrappers.<PolicyInfo>lambdaQuery().eq(PolicyInfo::getPolicyCode, req.getPolicyCode())) > 0) {
//            throw new MithrasException(String.format("保单编号%s已存在", req.getPolicyCode()));
//        }
        if (files == null || files.length == 0) {
            throw new MithrasException("请上传保单附件");
        }
        PaymentPolicyInfo info = BeanUtil.copyProperties(req, PaymentPolicyInfo.class);
        info.setPaymentId(paymentId);
        baseMapper.insert(info);
        for (MultipartFile multipartFile : files) {
            materialsListService.add(multipartFile, info.getId(),"POLICY", "PAYMENTPOLICY");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(MultipartFile[] files, Long id,PaymentPolicyInfoModifyREQ req) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        saveCheck(baseInfo);
        PaymentPolicyInfo paymentPolicyInfo = baseMapper.selectOne(Wrappers.<PaymentPolicyInfo>lambdaQuery()
                .eq(PaymentPolicyInfo::getPaymentId, req.getPaymentId())
                .eq(PaymentPolicyInfo::getPolicyCode, req.getPolicyCode())
                .last(StringUtil.mysqlLimitOne()));
        // 需求变更：保单编号可重复
        //保单编号是保单信息的唯一标识,不可重复
//        if(ObjectUtil.isNotNull(paymentPolicyInfo) && ObjectUtil.notEqual(id, paymentPolicyInfo.getId())){
//            throw new MithrasException(String.format("保单编号%s已存在", req.getPolicyCode()));
//        }
        //保单管理里只保存生效保单
//        if (policyInfoMapper.selectCount(Wrappers.<PolicyInfo>lambdaQuery().eq(PolicyInfo::getPolicyCode, req.getPolicyCode())) > 0) {
//            throw new MithrasException(String.format("保单编号%s已存在", req.getPolicyCode()));
//        }
        PaymentPolicyInfo originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        PaymentPolicyInfo info = BeanUtil.copyProperties(req, PaymentPolicyInfo.class);
        info.setId(id);
        baseMapper.updateAnnotationIncludeNullById(info);
        if (files != null && files.length > 0) {
            for (MultipartFile multipartFile : files) {
                materialsListService.add(multipartFile, id, "POLICY", "PAYMENTPOLICY");
            }
        }
        if(ObjectUtils.isNotEmpty(req.getRemoveFileIds())){
            materialsListService.remove(req.getRemoveFileIds());
        }
    }

    public Page<PaymentPolicyInfo> list(PaymentPolicyInfoListREQ req) {
        LocalDate localDate = LocalDate.now().plusDays(5);
        if (ObjectUtil.isNull(req.getVersion())) {
            return getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize())
                    , Wrappers.<PaymentPolicyInfo>lambdaQuery()
                            .eq(PaymentPolicyInfo::getPaymentId, req.getPaymentId())
                            .le(ObjectUtil.isNotNull(req.getAdventFlag()) && req.getAdventFlag(), PaymentPolicyInfo::getInsuranceEndDate, localDate));
        } else {
            return policyInfoLibService.getByPaymentIdAndVersion(req.getPaymentId(), req.getVersion(), req.getPage(), req.getPageSize(), req.getAdventFlag());
        }
    }

    public void export(PaymentPolicyInfoExportREQ req, ServletOutputStream outputStream){
        List<PaymentPolicyInfo> list = baseMapper.selectList(Wrappers.<PaymentPolicyInfo>lambdaQuery()
                .eq(PaymentPolicyInfo::getPaymentId, req.getPaymentId())
                .in(ObjectUtil.isNotEmpty(req.getPolicyIds()), PaymentPolicyInfo::getId, req.getPolicyIds()));
        if(CollectionUtil.isEmpty(list)){
            return;
        }
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(list.stream().map(PaymentPolicyInfo::getCreateBy).collect(Collectors.toSet()));
        List<PaymentPolicyExcelModel> paymentPolicyExcelModel = new ArrayList<>();
        list.forEach(base -> {
            PaymentPolicyExcelModel model = paymentConvert.base2PaymentPolicyExport(base);

            model.setCreateByName(userId2Name.get(base.getCreateBy()));
            PolicyTypeEnum of = PolicyTypeEnum.of(base.getPolicyType());
            model.setPolicyType(ObjectUtil.isNull(of) ? base.getPolicyType() : of.display);
            model.setPolicyAmount(LongUtil.tenThousand2Dollar(String.valueOf(base.getPolicyAmount())));
            model.setIdentificationInformation(base.getIdentificationInformation());
            PolicyRenewInsuranceEnum of1 = PolicyRenewInsuranceEnum.of(base.getRenewInsuranceFlag());
            model.setRenewInsuranceFlag(ObjectUtil.isNull(of1) ? base.getRenewInsuranceFlag() : of1.display);
            paymentPolicyExcelModel.add(model);
        });
        paymentPolicyExcelExporter.exportExcel(paymentPolicyExcelModel, outputStream);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(PaymentPolicyInfoRemoveREQ req) {
        PaymentPolicyInfo originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(originalInfo.getPaymentId());
        saveCheck(baseInfo);
        //区分
        if(ObjectUtil.isEmpty(originalInfo.getContractId())) {
            baseMapper.deleteById(req.getId());
        } else {
            originalInfo.setPaymentId(null);
            baseMapper.updateAnnotationIncludeNullById(originalInfo);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void removeBatch(PaymentPolicyInfoRemoveBatchREQ req) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        saveCheck(baseInfo);
        List<PaymentPolicyInfo> paymentPolicyInfos = baseMapper.selectBatchIds(req.getIds());
        List<Long> deleteIds = new ArrayList<>();
        //目前不支持批量更新null,数据量不大先循环插入（不推荐）
        paymentPolicyInfos.forEach(paymentPolicyInfo -> {
            if(ObjectUtil.isEmpty(paymentPolicyInfo.getContractId())) {
                deleteIds.add(paymentPolicyInfo.getId());
            } else {
                paymentPolicyInfo.setPaymentId(null);
                baseMapper.updateAnnotationIncludeNullById(paymentPolicyInfo);
            }
        });
        if(CollectionUtil.isNotEmpty(deleteIds)){
            baseMapper.deleteBatchIds(deleteIds);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public String importExcel(InputStream inputStream, Long paymentId) {
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(paymentId);
        saveCheck(baseInfo);
        List<PaymentPolicyItemExcelModel> policyItemExcelModels = paymentPolicyExcelImporter.parse(inputStream);
        if (CollectionUtils.isEmpty(policyItemExcelModels)) {
            throw new MithrasException("导入的文件数据为空");
        }
        checkImport(policyItemExcelModels);
        //导入分为两部分，一部分新增，一部分更新，根据保单编号区分
        List<PaymentPolicyInfo> saveBeans = new ArrayList<>();
        PaymentPolicyInfo tmpPolicy;
//        List<String> policyCodes = policyItemExcelModels.stream().map(PaymentPolicyItemExcelModel::getPolicyCode).collect(Collectors.toList());
//        List<PaymentPolicyInfo> paymentPolicyInfos = baseMapper.listPolicyCode(policyCodes);
//        List<PolicyInfo> policyInfos = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
//                .in(PolicyInfo::getPolicyCode, policyCodes));
//        Set<String> allPolicyCode = new HashSet<>();
//        if(ObjectUtil.isNotEmpty(policyInfos)) {
//            allPolicyCode = policyInfos.stream().map(PolicyInfo::getPolicyCode).collect(Collectors.toSet());
//        }
//        Map<String, Long> collectMap = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
//        if (ObjectUtil.isNotEmpty(paymentPolicyInfos)) {
//            collectMap = paymentPolicyInfos.stream().collect(Collectors.toMap(PaymentPolicyInfo::getPolicyCode, PaymentPolicyInfo::getId, (a, b) -> a));
//        }
        PolicyTypeEnum of = null;
        for (PaymentPolicyItemExcelModel paymentPolicyItemExcelModel : policyItemExcelModels) {
//            if(allPolicyCode.contains(paymentPolicyItemExcelModel.getPolicyCode())) {
//                stringBuilder.append(paymentPolicyItemExcelModel.getPolicyCode());
//                stringBuilder.append(",");
//                continue;
//            }
            tmpPolicy = BeanUtil.copyProperties(paymentPolicyItemExcelModel, PaymentPolicyInfo.class);
//            if (ObjectUtil.isNotEmpty(collectMap.get(tmpPolicy.getPolicyCode()))) {
//                stringBuilder.append(paymentPolicyItemExcelModel.getPolicyCode());
//                stringBuilder.append(",");
//                continue;
//            }
            tmpPolicy.setPaymentId(paymentId);
            tmpPolicy.setPolicyAmount(LongUtil.other2Long(paymentPolicyItemExcelModel.getPolicyAmount().toString()));
            of = PolicyTypeEnum.find(paymentPolicyItemExcelModel.getPolicyType());
            tmpPolicy.setPolicyType(of == null ? null : of.name());
            saveBeans.add(tmpPolicy);
        }
        this.saveBatch(saveBeans);
//        if(stringBuilder.length() > 0){
//            stringBuilder.append("保单编号重复");
//        } else {
            stringBuilder.append("保单导入成功");
//        }
        return stringBuilder.toString();
    }

    //同步至保单模块
    @Transactional(rollbackFor = Throwable.class)
    public void sync2Policy(Long paymentId, String processInstanceId){
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        if(ObjectUtil.isEmpty(paymentBaseInfo)){
            return;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
        if(ObjectUtil.isEmpty(contractBaseInfo)){
            return;
        }
        if(LongUtil.null2zero(paymentBaseInfo.getPolicyFlag()) > 1){
            notice(paymentBaseInfo, contractBaseInfo, processInstanceId);
        }
        List<PaymentPolicyInfo> paymentPolicyInfos = baseMapper.selectList(Wrappers.<PaymentPolicyInfo>lambdaQuery()
                .eq(PaymentPolicyInfo::getPaymentId, paymentId));
        if(ObjectUtil.isEmpty(paymentPolicyInfos)){
            return;
        }
        //同步数据至保单
        List<PolicyInfo> policyInfos = BeanUtil.copyToList(paymentPolicyInfos, PolicyInfo.class);
        policyInfos.forEach(policyInfo -> {
            policyInfo.setProjId(contractBaseInfo.getProjReviewId());
            policyInfo.setContractId(paymentBaseInfo.getContractId());
            policyInfo.setContractCode(paymentBaseInfo.getContractCode());
            policyInfo.setPaymentId(paymentBaseInfo.getId());
            policyInfo.setPaymentPolicyId(policyInfo.getId());
            policyInfo.setId(null);
            policyInfo.setLevel(0);
            policyInfo.setApprovalStatus(PolicyApprovalStatusEnum.NEW_APPROVAL_PASS.name());
            policyInfo.setPolicyStatus(PolicyStatusEnum.EFFECT.name());
            if(PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name().equals(policyInfo.getRenewInsuranceFlag())) {
                policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.NO.getCode());
            } else {
                policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.YES.getCode());
            }
        });
        SpringContextHolder.getBean(PolicyInfoService.class).saveBatch(policyInfos);
        //同步文件
        List<MaterialsList> fileList = materialsListService.list("PAYMENTPOLICY", Collections.singletonList("POLICY"),
                paymentPolicyInfos.stream().map(PaymentPolicyInfo::getId).collect(Collectors.toList()));
        if(ObjectUtil.isNotEmpty(fileList)){
            List<MaterialsList> addFile = new ArrayList<>();
            List<MaterialsList> materialsLists;
            Map<Long, List<MaterialsList>> fileMap = fileList.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
            for (PolicyInfo policyInfo : policyInfos) {
                materialsLists = fileMap.get(policyInfo.getPaymentPolicyId());
                if (ObjectUtil.isNotEmpty(materialsLists)) {
                    addFile.addAll(materialsLists.stream().map(materialsList -> {
                        MaterialsList materialsList1 = BeanUtil.copyProperties(materialsList, MaterialsList.class);
                        materialsList1.setBelongId(policyInfo.getId());
                        materialsList1.setBusinessType(BusinessModuleEnum.POLICY.name());
                        materialsList1.setMaterialsType(BusinessModuleEnum.POLICY.name());
                        materialsList1.setId(null);
                        return materialsList1;
                    }).collect(Collectors.toList()));
                }
            }
            if(ObjectUtil.isNotEmpty(addFile)){
                materialsListService.saveBatch(addFile);
            }
        }
    }

    private void notice(PaymentBaseInfo paymentBaseInfo, ContractBaseInfo contractBaseInfo, String processInstanceId){
        try {
            CompletableFuture.runAsync(() -> {
                if(LongUtil.null2zero(paymentBaseInfo.getPolicyFlag()) >= 2){
                    MessageAddREQ messageAddREQ = new MessageAddREQ();
                    List<Long> toList = new ArrayList<Long>();
                    if(ObjectUtil.isNotEmpty(contractBaseInfo.getProjCosponsorUserIds())){
                        toList.addAll(JSON.parseArray(contractBaseInfo.getProjCosponsorUserIds(), Long.class));
                    }
                    toList.add(contractBaseInfo.getProjSponsorUserId());
                    messageAddREQ.setFrom("系统通知");
                    messageAddREQ.setTo(toList);
                    messageAddREQ.setPcurl("/workbench");
                    messageAddREQ.setContent(processInstanceId);
                    messageAddREQ.setFlowid(processInstanceId);
                    messageAddREQ.setNeedOa(false);
                    messageAddREQ.setRelation("合同" + contractBaseInfo.getContractCode() + "尚未购买保险，请及时处理");
                    messageAddREQ.setMessageType(MessageTypeEnum.REMINDER_NOTICE.name());
                    messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
                }
            });
        }catch (Exception e){
            log.error("保单通知失败", e);
        }
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

    //检查保单附件是否已填
    public void checkFile(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        if( ObjectUtil.isNull(paymentBaseInfo)){
            throw new MithrasException("付款信息不存在");
        }
        //无需维护保单信息
        if (LongUtil.null2zero(paymentBaseInfo.getPolicyFlag()) > 0){
            return;
        }
        List<PaymentPolicyInfo> paymentPolicyInfos = baseMapper.selectList(Wrappers.<PaymentPolicyInfo>lambdaQuery()
                .eq(PaymentPolicyInfo::getPaymentId, paymentId));
        if (CollectionUtil.isEmpty(paymentPolicyInfos)) {
            throw new MithrasException("保单信息不能为空");
        }
        StringBuilder sb = new StringBuilder();
        Map<Long, List<MaterialsList>> fileMap = new HashMap<>();
        List<MaterialsList> fileList = materialsListService.list("PAYMENTPOLICY", Collections.singletonList("POLICY"),
                paymentPolicyInfos.stream().map(PaymentPolicyInfo::getId).collect(Collectors.toList()));
        if (CollectionUtil.isNotEmpty(fileList)) {
            fileMap = fileList.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
        }
        for (PaymentPolicyInfo base : paymentPolicyInfos) {
            if (CollectionUtil.isEmpty(fileMap.get(base.getId()))) {
                if (sb.length() == 0) {
                    sb.append("保险单号:");
                }
                sb.append(base.getPolicyCode()).append("、");
            }
        }
        if( sb.length() > 0){
            sb.deleteCharAt(sb.length()-1);
            sb.append("附件为空，请上传附件后再提交");
            throw new MithrasException(sb.toString());
        }
    }
}
