package cn.zswltech.mithras.application.orchestration.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.policy.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.policy.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.policy.enums.PolicyDataStatusEnum;
import cn.zswltech.mithras.policy.enums.PolicyStatusEnum;
import cn.zswltech.mithras.policy.excel.importer.PaymentPolicyExcelImporter;
import cn.zswltech.mithras.policy.excel.model.PaymentPolicyItemExcelModel;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.policy.dto.persistence.PolicyCodeDTO;
import cn.zswltech.mithras.policy.dto.persistence.PolicyListDTO;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentPolicyInfo;
import cn.zswltech.mithras.policy.model.PolicyInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.policy.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentPolicyInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projlifecycle.ProjectLifecycleService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.json.JSONUtil.toBean;

/**
 * @create: 2023-06-15
 **/

@Slf4j
@Service
public class PolicyInfoService extends ServiceImpl<PolicyInfoMapper, PolicyInfo> {

    @Resource
    private PolicyInfoMapper policyInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ProjectLifecycleService projectLifecycleService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentPolicyInfoService paymentPolicyInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private PaymentPolicyExcelImporter paymentPolicyExcelImporter;

    public List<SelectRSP> projList() {
        Set<Long> nozore = noSettleProj();
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, nozore)
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()));
               /* .and(e -> e.eq(ProjReviewBaseInfo::getProjSponsorUserId, AccountUtil.getLoginInfo().getId())
                        .or().apply(" json_contains(proj_cosponsor_user_ids, CONVERT ({0}, CHAR ))", AccountUtil.getLoginInfo().getId())
                ));*/
        List<SelectRSP> rsps = new ArrayList<>();
        if (CollUtil.isNotEmpty(projReviewBaseInfos)) {
            for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfos) {
                SelectRSP rsp = new SelectRSP();
                rsp.setLabel(projReviewBaseInfo.getProjName());
                rsp.setValue(projReviewBaseInfo.getId().toString());
                rsps.add(rsp);
            }
        }
        return rsps;
    }

    public List<SelectRSP> contractList(PolicyAddContractREQ req) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getProjReviewId, req.getProjId())
                .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.NEW.name(), ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name())));
        List<SelectRSP> rsps = new ArrayList<>();
        if (CollUtil.isNotEmpty(contractBaseInfos)) {
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                SelectRSP rsp = new SelectRSP();
                rsp.setLabel(contractBaseInfo.getContractCode());
                rsp.setValue(contractBaseInfo.getId().toString());
                rsps.add(rsp);
            }
        }
        return rsps;
    }

    public Set<Long> noSettleProj() {
        List<String> list = Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name());
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractStatus, list));
        List<Long> cids = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> rids = contractBaseInfos.stream().map(ContractBaseInfo::getProjReviewId).distinct().collect(Collectors.toList());
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, cids));
        Map<Long, List<CollectionBaseInfo>> cmap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        Map<Long, ProjReviewPriceDetailRSP> reviewPriceMap = projectLifecycleService.getReviewPriceMap(rids, true);
        Set<Long> nozore = new HashSet<>();
        Map<Long, List<ContractBaseInfo>> rcmap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        for (Long rid : rids) {
            List<ContractBaseInfo> infos = rcmap.get(rid);
            ProjReviewPriceDetailRSP projReviewPriceDetail = reviewPriceMap.get(rid);
            if (projReviewPriceDetail != null && CollUtil.isNotEmpty(infos)) {
                Long applyCreditAmount = LongUtil.null2zero(projReviewPriceDetail.getApplyCreditAmount());
                long receivedPrincipal = 0;
                for (ContractBaseInfo o : infos) {
                    List<CollectionBaseInfo> baseInfos = cmap.get(o.getId());
                    if (CollUtil.isNotEmpty(baseInfos)) {
                        for (CollectionBaseInfo info : baseInfos) {
                            receivedPrincipal = receivedPrincipal + LongUtil.null2zero(info.getCollectionPrincipal());
                        }
                    }
                }
                //TODO 杨雄：判断applyCreditAmount!=receivedPrincipal更加语义化；（文靖的代码）
                if ((applyCreditAmount - receivedPrincipal) != 0) {
                    nozore.add(rid);
                }
            }
        }
        return nozore;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long add(PolicyInfoAddREQ req) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(ObjectUtil.isNull(paymentBaseInfo) ? req.getContractId() : paymentBaseInfo.getContractId());
        if (ObjectUtil.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息为空");
        }
        //检查保单号唯一性
//        if (policyInfoMapper.countPolicyCode(req.getPolicyCode()) > 0) {
//            throw new MithrasException("保单号重复");
//        }
        //一个保单只能添加一个
        PolicyInfo info = BeanUtil.copyProperties(req, PolicyInfo.class);
        if (ObjectUtil.isNotEmpty(req.getPolicyId())) {
            //保单延期，一个只能添加一个
//            if (baseMapper.selectCount(Wrappers.<PolicyInfo>lambdaQuery()
//                    .eq(PolicyInfo::getParentId, req.getPolicyId())) > 0) {
//                throw new MithrasException("该保单已续保");
//            }
            PolicyInfo policyInfo = baseMapper.selectById(req.getPolicyId());
            if (ObjectUtil.isNotEmpty(policyInfo)) {
                info.setLevel(policyInfo.getLevel() == null ? 0 : policyInfo.getLevel() + 1);
//                policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.YES.getCode());
//                baseMapper.updateById(policyInfo);
            } else {
                info.setLevel(1);
            }
        } else {
            info.setLevel(0);
        }
        info.setProjId(contractBaseInfo.getProjReviewId());
        info.setParentId(req.getPolicyId());
        info.setContractId(contractBaseInfo.getId());
        info.setContractCode(contractBaseInfo.getContractCode());
        info.setPaymentId(Objects.isNull(paymentBaseInfo) ? null : paymentBaseInfo.getId());
        info.setPolicyStatus(PolicyStatusEnum.EFFECT.name());
        info.setAutomatic(0);
        info.setDataStatus(PolicyDataStatusEnum.TEMP.name());
        policyInfoMapper.insert(info);
        if (req.getFiles() != null && req.getFiles().length > 0) {
            for (MultipartFile multipartFile : req.getFiles()) {
                materialsListService.add(multipartFile, info.getId(), BusinessModuleEnum.POLICY.name(), BusinessModuleEnum.POLICY.name());
            }
        }
        return info.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(PolicyInfoModifyREQ req) {
        PolicyInfo info = policyInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(info)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        modifyCheck(info);
//        if (req.getPolicyCode() != null && !req.getPolicyCode().equals(info.getPolicyCode())) {
//            if (policyInfoMapper.countPolicyCode(req.getPolicyCode()) > 0) {
//                throw new MithrasException("保单号重复");
//            }
//        }
        PolicyInfo update = new PolicyInfo();
        update.setId(req.getId());
        update.setPolicyCode(req.getPolicyCode());
        update.setPolicyAmount(req.getPolicyAmount());
        update.setPolicyType(req.getPolicyType());
        update.setInsuranceStartDate(req.getInsuranceStartDate());
        update.setInsuranceEndDate(req.getInsuranceEndDate());
        update.setInsuranceCompany(req.getInsuranceCompany());
        update.setRenewInsuranceFlag(req.getRenewInsuranceFlag());
        update.setRemark(req.getRemark());
        update.setIdentificationInformation(req.getIdentificationInformation());
        /*if (info.getApprovalStatus().equals(PolicyApprovalStatusEnum.NEW_APPROVAL_PASS.name()) || info.getApprovalStatus().equals(PolicyApprovalStatusEnum.CHANGING_APPROVAL_PASS.name())){
            update.setApprovalStatus(PolicyApprovalStatusEnum.CHANGING_UN_SUBMIT.name());
        }*/
        if (info.getCreateBy() == null) {
            info.setCreateBy(AccountUtil.getLoginInfo().getId());
        }
        policyInfoMapper.updateAnnotationIncludeNullById(update);
        if (req.getFiles() != null && req.getFiles().length > 0) {
            for (MultipartFile multipartFile : req.getFiles()) {
                materialsListService.add(multipartFile, req.getId(), BusinessModuleEnum.POLICY.name(), BusinessModuleEnum.POLICY.name());
            }
        }
        if (ObjectUtils.isNotEmpty(req.getRemoveFileIds())) {
            materialsListService.remove(req.getRemoveFileIds());
        }
    }

    private void modifyCheck(PolicyInfo info) {
        if(!info.getCreateBy().equals(AccountUtil.getLoginInfo().getId())) {
            throw new MithrasException("非保单创建人，不可维护");
        }
    }

    public PolicyInfoDetailRSP detail(PolicyInfoDetailREQ req) {
        ProjReviewBaseInfo projInfo;
        PolicyInfoDetailRSP rsp = new PolicyInfoDetailRSP();
        if (req.getId() == null && req.getProjId() == null) {
            return rsp;
        }
        if (req.getId() != null) {
            PolicyInfo info = policyInfoMapper.selectById(req.getId());
            projInfo = projReviewBaseInfoMapper.selectById(info.getProjId());
            rsp.setId(info.getId());
            rsp.setPolicyCode(info.getPolicyCode());
            rsp.setProjId(info.getProjId());
            rsp.setInsuranceStartDate(info.getInsuranceStartDate());
            rsp.setInsuranceEndDate(info.getInsuranceEndDate());
            rsp.setInsuranceCompany(info.getInsuranceCompany());
            rsp.setPolicyAmount(info.getPolicyAmount());
            Long user = AccountUtil.getLoginInfo().getId();
            List<Long> projCosponsorUserIds = isBlank(projInfo.getProjCosponsorUserIds()) ? null : toBean(projInfo.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
            }, true);
            if (info.getAutomatic() == 1 && info.getCreateBy() == null) {
                rsp.setCreater(user.equals(projInfo.getProjSponsorUserId()) || (projCosponsorUserIds != null && projCosponsorUserIds.contains(user)));
            } else {
                rsp.setCreater(user.equals(info.getCreateBy()) && (info.getApprovalStatus().equals(PolicyApprovalStatusEnum.NEW_UN_SUBMIT.name())
                        || info.getApprovalStatus().equals(PolicyApprovalStatusEnum.NEW_APPROVAL_PASS.name()) || info.getApprovalStatus().equals(PolicyApprovalStatusEnum.CHANGING_UN_SUBMIT.name())
                        || info.getApprovalStatus().equals(PolicyApprovalStatusEnum.CHANGING_APPROVAL_PASS.name())));
            }
            rsp.setApprovalStatus(info.getApprovalStatus());
            rsp.setIdentificationInformation(info.getIdentificationInformation());
        } else {
            projInfo = projReviewBaseInfoMapper.selectById(req.getProjId());
        }

        rsp.setProjName(projInfo.getProjName());
        rsp.setProjCode(projInfo.getProjCode());
        rsp.setClientId(projInfo.getClientId());

        rsp.setProjSponsorUserId(projInfo.getProjSponsorUserId());
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        rsp.setProjCosponsorUserIds(isBlank(projInfo.getProjCosponsorUserIds()) ? null : toBean(projInfo.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
        }, true));
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        clientIds.add(rsp.getClientId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setClientName(clientMap.get(rsp.getClientId()));

        return rsp;
    }

    public PageR<PolicyInfoListRSP> list(PolicyInfoListREQ req) {
        if (ObjectUtil.isEmpty(req.getPolicyId()) && ObjectUtil.isEmpty(req.getPaymentId())) {
            return new PageR<>();
        }
        Page<PolicyInfo> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<PolicyInfo>lambdaQuery()
                .eq(ObjectUtil.isNotNull(req.getPolicyId()), PolicyInfo::getParentId, req.getPolicyId())
                .eq(ObjectUtil.isNotNull(req.getPaymentId()), PolicyInfo::getPaymentId, req.getPaymentId()));
        List<PolicyInfoListRSP> rsps = null;
        if (CollUtil.isNotEmpty(page.getRecords())) {
            rsps = BeanUtil.copyToList(page.getRecords(), PolicyInfoListRSP.class);
            List<Long> belongIds = page.getRecords().stream().map(PolicyInfo::getId).collect(Collectors.toList());
            List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBusinessType, BusinessModuleEnum.POLICY.name())
                    .eq(MaterialsList::getMaterialsType, BusinessModuleEnum.POLICY.name())
                    .in(ObjectUtil.isNotEmpty(belongIds), MaterialsList::getBelongId, belongIds));
            Map<Long, List<MaterialsList>> listMap = new HashMap<>();
            if (CollUtil.isNotEmpty(materialsLists)) {
                listMap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
            }
            Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(rsps.stream().map(PolicyInfoListRSP::getCreateBy).collect(Collectors.toList()));
            List<MaterialsList> materials = null;
            List<PolicyInfoMaterialsListRSP> policyMaterials = null;
            for (PolicyInfoListRSP base : rsps) {
                base.setCreateName(sysUserMap.get(base.getCreateBy()));
                materials = listMap.get(base.getId());
                if (CollUtil.isNotEmpty(materials)) {
                    policyMaterials = new ArrayList<>();
                    for (MaterialsList material : materials) {
                        PolicyInfoMaterialsListRSP tmp = new PolicyInfoMaterialsListRSP();
                        tmp.setId(material.getId());
                        tmp.setName(material.getFilename());
                        policyMaterials.add(tmp);
                    }
                }
                base.setFiles(policyMaterials);
            }
        }
        return PageR.of(rsps, page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(PolicyInfoRemoveREQ req) {
        PolicyInfo info = policyInfoMapper.selectById(req.getId());
        check(info);
        policyInfoMapper.deleteById(req.getId());
    }

    /**
     * 同步保单信息到付款模块 其实维护一套数据即可，保单台账只是展示，没必要再存一份不可修改且和付款保单保存一致的
     **/
    @Transactional(rollbackFor = Throwable.class)
    @Deprecated
    public void syncPolicy2Payment(Long paymentId, Long contractId) {
        List<PolicyInfo> policyInfos = baseMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getContractId, contractId)
                .isNull(PolicyInfo::getPaymentId)
                .isNull(PolicyInfo::getPaymentPolicyId));
        if (CollectionUtil.isNotEmpty(policyInfos)) {
            List<PaymentPolicyInfo> paymentPolicyInfos = BeanUtil.copyToList(policyInfos, PaymentPolicyInfo.class);
            paymentPolicyInfos.forEach(paymentPolicyInfo -> {
                paymentPolicyInfo.setId(null);
            });
            //保存付款保单
            paymentPolicyInfoService.saveBatch(paymentPolicyInfos);
            //回填信息
            Map<String, Long> policyCode2PaymentPolicyIdMap = paymentPolicyInfos.stream().collect(Collectors.toMap(PaymentPolicyInfo::getPolicyCode, PaymentPolicyInfo::getId, (a, b) -> a));
            policyInfos.forEach(policyInfo -> {
                policyInfo.setPaymentId(paymentId);
                policyInfo.setPaymentPolicyId(policyCode2PaymentPolicyIdMap.get(policyInfo.getPolicyCode()));
            });
            SpringContextHolder.getBean(PolicyInfoService.class).updateBatchById(policyInfos);
        }
    }

    public Map<Long, LocalDate> getOverdueDaysByPolicyIds(List<PolicyListDTO> policyListDTOS) {
        if (CollUtil.isEmpty(policyListDTOS)) {
            return MapUtil.empty();
        }
        List<Long> policyIds = policyListDTOS.stream().filter(policyListDTO -> "policy".equals(policyListDTO.getDataSource())).map(PolicyListDTO::getId).collect(Collectors.toList());
        Map<Long, LocalDate> nextPolicy = new HashMap<>();
        Map<Long, LocalDate> rspMap = new HashMap<>();
        if (CollUtil.isNotEmpty(policyIds)) {
            nextPolicy.putAll(policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery().in(PolicyInfo::getParentId, policyIds)).stream().collect(Collectors.toMap(PolicyInfo::getParentId, e -> e.getCreateTime().toLocalDate(), (a, b) -> a)));
        }
        policyListDTOS.forEach(policyListDTO -> {
            if ("policy".equals(policyListDTO.getDataSource())) {
                if (ObjectUtil.isNotEmpty(nextPolicy.get(policyListDTO.getId()))) {
                    rspMap.put(policyListDTO.getId(), nextPolicy.get(policyListDTO.getId()));
                }
            }
        });
        return rspMap;
    }

    public String importExcel(PaymentPoliceImportREQ paymentPoliceImportREQ) {
        try {
            List<PaymentPolicyItemExcelModel> policyItemExcelModels = paymentPolicyExcelImporter.parse(paymentPoliceImportREQ.getFile().getInputStream());
            if (CollectionUtils.isEmpty(policyItemExcelModels)) {
                throw new MithrasException("导入的文件数据为空");
            }
            if (ObjectUtil.isNotEmpty(paymentPoliceImportREQ.getPolicyId())) {

            }
        } catch (Exception e) {
            throw new MithrasException("导入保单文件发生异常");
        }
        return null;
    }

    public Map<String, Integer> countPolicyCodeNum(List<String> list) {
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        return policyInfoMapper.countPolicyCodeNum(list).stream().collect(Collectors.toMap(PolicyCodeDTO::getPolicyCode, PolicyCodeDTO::getCodeNum,
                (a, b) -> a));
    }

    @Transactional(rollbackFor = Throwable.class)
    public Integer updateNotice(List<Long> ids, Integer code) {
        if (ObjectUtil.isEmpty(ids)) {
            return 0;
        }
        return policyInfoMapper.updateNotice(ids, code);
    }

    private void check(PolicyInfo info) {
        if (info.getAutomatic() == 1) {
            throw new MithrasException("自动推送任务无法删除");
        }
        Long id = AccountUtil.getLoginInfo().getId();
        if (!id.equals(info.getCreateBy())) {
            throw new MithrasException("仅创建人可删除");
        }
    }

    public void submit(PolicyInfoSubmitREQ req) {
        Long parentId = req.getParentId();
        List<PolicyInfo> list = this.list(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getParentId, parentId));
        if(CollectionUtil.isEmpty(list)){
            return;
        }
        list.forEach(policyInfo -> {
            checkNotEmpty(policyInfo.getPolicyCode(),"保单编号");
            checkNotEmpty(policyInfo.getInsuranceCompany(),"保险机构");
            checkNotEmpty(policyInfo.getPolicyType(),"险种");
            checkNotEmpty(policyInfo.getPolicyAmount(),"保单金额");
            checkNotEmpty(policyInfo.getInsuranceStartDate(),"保险起始日");
            checkNotEmpty(policyInfo.getInsuranceEndDate(),"保险到期日");
            checkNotEmpty(policyInfo.getRenewInsuranceFlag(),"是否续保");
        });
        List<Long> belongIds = list.stream().map(PolicyInfo::getId).collect(Collectors.toList());
        List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBusinessType, BusinessModuleEnum.POLICY.name())
                .eq(MaterialsList::getMaterialsType, BusinessModuleEnum.POLICY.name())
                .in(MaterialsList::getBelongId, belongIds));
        if(CollectionUtil.isNotEmpty(materialsLists)){
            Set<Long> belongIdSet = materialsLists.stream().map(MaterialsList::getBelongId).collect(Collectors.toSet());
            if(belongIdSet.size() < belongIds.size()){
                throw new MithrasException("【保单资料】不得为空");
            }
        }else{
            throw new MithrasException("【保单资料】不得为空");
        }
        PolicyInfo policyInfo = baseMapper.selectById(parentId);
        if(policyInfo != null) {
            // 父保单更新状态
            policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.YES.getCode());
            baseMapper.updateById(policyInfo);
            // 子保单更新状态
            List<PolicyInfo> policyInfos = baseMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getParentId, parentId));
            if(CollectionUtil.isNotEmpty(policyInfos)){
                policyInfos.forEach(p ->{
                    p.setDataStatus(PolicyDataStatusEnum.FORMAL.name());
                });
                this.updateBatchById(policyInfos);
            }

        }else{
            throw new MithrasException("父级保单数据不存在");
        }


    }

    void checkNotEmpty(Object obj,String filed){
        if(ObjectUtils.isEmpty(obj)){
            throw new MithrasException("【"+filed+"】不得为空");
        }

    }

}
