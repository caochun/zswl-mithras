package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.guarantor.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.contract.ContractGuarantorConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.contract.enums.contract.*;
import cn.zswltech.mithras.contract.mapper.contract.ContractGuarantorMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.customer.event.ClientViewAuthorityEvent;
import cn.zswltech.mithras.projectprocess.service.bo.ContractConstitutionFileBO;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.customer.application.lib.client.CorpContactInfoLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.ContractUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import cn.zswltech.mithras.contract.core.application.ContractCodeAbstract;
import cn.zswltech.mithras.contract.core.application.ContractConstitutionFileService;


/**
 * @author vico
 * @description 合同-担保措施
 * @date 2022-08-12
 */
@Slf4j
@Service
public class ContractGuarantorServiceImpl extends ContractCodeAbstract<ContractGuarantorMapper, ContractGuarantor> implements ContractGuarantorService {

    @Resource
    private ContractGuarantorMapper contractGuarantorMapper;

    @Resource
    private ContractGuarantorConverter guarantorConverter;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private CorpContactInfoLibService corpContactInfoLibService;

    @Resource
    private ContractConstitutionFileService contractConstitutionFileService;
    @Resource
    private MaterialsListService materialsListService;

    @Autowired
    private ContractLeasePriceService contractLeasePriceService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean add(ContractGuarantorAddREQ req) {
        ContractGuarantor info = guarantorConverter.reqToEntity(req);
        // 处理联保标志
        this.doGuaranteeBasic(req, info);

        //如果担保人是法人，添加章程文件
        if (Objects.nonNull(info.getGuarantorType()) && ClientType.CORPORATION.name().equals(info.getGuarantorType())) {
            this.saveConstitutionFiles(req.getMultipartFileList(), req.getConstitutionFileList(), info);
        }

        if (ResolutionTypeEnum.OTHER.name().equals(req.getResolutionType()) && CollectionUtils.isEmpty(req.getFileList())) {
            throw new MithrasException("决议类型为其他时-决议文件不得为空");
        }
        // 上传文件
        List<Long> fileIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(req.getFileList())) {
            for (MultipartFile file : req.getFileList()) {
                Long fileId = materialsListService.add(file, req.getContractId(), ContractTypeEnum.RESOLUTION_FILE.name(), BusinessModuleEnum.CONTRACT.name());
                fileIdList.add(fileId);
            }
        }
        String files = JSON.toJSONString(fileIdList);
        info.setResolutionFileId(files);
        boolean result = contractGuarantorMapper.insert(info) > 0 ? Boolean.TRUE : Boolean.FALSE;
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(req.getContractId(), TradeStructureRoleEnum.GUARANTOR);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        info.getContractId().toString(),
                        "合同管理-新增担保措施")
                )
        );
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(Long contractId, List<ClientInfo> clientInfoList) {
        if (ObjectUtil.isEmpty(clientInfoList)) {
            return;
        }
        ContractGuarantor contractGuarantor;
        List<Long> fguarantorIds = new ArrayList<>();
        List<Long> zguarantorIds = new ArrayList<>();
        clientInfoList.forEach(client -> {
            if (ClientType.CORPORATION.name().equals(client.getClientType())) {
                fguarantorIds.add(client.getClientId());
            } else {
                zguarantorIds.add(client.getClientId());
            }
        });
        if (fguarantorIds.size() > 0) {
            contractGuarantor = new ContractGuarantor();
            contractGuarantor.setContractId(contractId);
            contractGuarantor.setGuaranteeMethod(GuaranteeMethodEnum.JOINT_RESPONSIBILITY.name());
            //若果是多人，则联保标志为：联保
            if(fguarantorIds.size() > 1){
                contractGuarantor.setJointGuaranteeMark(JointGuaranteeMarkEnum.JOINT.name());
            }else {
                contractGuarantor.setJointGuaranteeMark(JointGuaranteeMarkEnum.SINGLE.name());
            }
            //合同金额
            ContractLeasePrice leasePrice = contractLeasePriceService.getOne(Wrappers.<ContractLeasePrice>lambdaQuery().eq(ContractLeasePrice::getContractId, contractId));
            if(Objects.nonNull(leasePrice)){
                contractGuarantor.setGuaranteeAmountSingle(leasePrice.getApplyCreditAmount());
            }
            contractGuarantor.setIsReport(1);
            contractGuarantor.setGuarantorType(ClientType.CORPORATION.name());
            contractGuarantor.setGuarantorIds(JSONUtil.toJsonStr(fguarantorIds));
            baseMapper.insert(contractGuarantor);
        }
        if (zguarantorIds.size() > 0) {
            contractGuarantor = new ContractGuarantor();
            contractGuarantor.setContractId(contractId);
            contractGuarantor.setGuaranteeMethod(GuaranteeMethodEnum.JOINT_RESPONSIBILITY.name());
            //若果是多人，则联保标志为：联保
            if(zguarantorIds.size() > 1){
                contractGuarantor.setJointGuaranteeMark(JointGuaranteeMarkEnum.JOINT.name());
            }else {
                contractGuarantor.setJointGuaranteeMark(JointGuaranteeMarkEnum.SINGLE.name());
            }
            //合同金额
            ContractLeasePrice leasePrice = contractLeasePriceService.getOne(Wrappers.<ContractLeasePrice>lambdaQuery().eq(ContractLeasePrice::getContractId, contractId));
            if(Objects.nonNull(leasePrice)){
                contractGuarantor.setGuaranteeAmountSingle(leasePrice.getApplyCreditAmount());
            }
            contractGuarantor.setIsReport(1);
            contractGuarantor.setGuarantorType(ClientType.NORMAL.name());
            contractGuarantor.setGuarantorIds(JSONUtil.toJsonStr(zguarantorIds));
            baseMapper.insert(contractGuarantor);
        }
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractId, TradeStructureRoleEnum.GUARANTOR);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        contractId.toString(),
                        "合同管理-新增担保措施")
                )
        );
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void modify(ContractGuarantorModifyREQ req) throws MithrasException {
        ContractGuarantor info = guarantorConverter.modifyToEntity(req);
        ContractGuarantor byId = this.getById(info);
        if (byId != null) {
            info.setResolutionFileId(byId.getResolutionFileId());
        }
        this.doGuaranteeBasic(req, info);

        //如果担保人是法人，添加章程文件
        if (Objects.nonNull(info.getGuarantorType()) && ClientType.CORPORATION.name().equals(info.getGuarantorType())) {
            this.saveConstitutionFiles(req.getMultipartFileList(), req.getConstitutionFileList(), info);
        }

        // 格式化合同编号
        info.setGuarantorContractCode(ContractUtil.format(info.getGuarantorContractCode()));
        if (ResolutionTypeEnum.OTHER.name().equals(req.getResolutionType()) && CollectionUtils.isEmpty(req.getFileList()) && CollectionUtils.isEmpty(req.getFileListId())) {
            throw new MithrasException("决议类型为其他时-决议文件不得为空");
        }
        // 上传文件
        List<Long> fileIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(req.getFileList())) {
            for (MultipartFile file : req.getFileList()) {
                Long fileId = materialsListService.add(file, info.getContractId(), ContractTypeEnum.RESOLUTION_FILE.name(), BusinessModuleEnum.CONTRACT.name());
                fileIdList.add(fileId);
            }
        }
        // 上传的文件id+前端传来的文件id
        fileIdList.addAll(Optional.ofNullable(req.getFileListId()).orElse(new ArrayList<>()));
        // 数据库现存的文件id
        List<Long> resolutionFileId = JSON.parseArray(Optional.ofNullable(info.getResolutionFileId()).orElse("[]"), Long.class);

        String files = JSON.toJSONString(fileIdList);
        info.setResolutionFileId(files);

        resolutionFileId.removeAll(fileIdList);
        if (CollectionUtils.isNotEmpty(resolutionFileId)) {
            materialsListService.remove(resolutionFileId);
        }

        contractGuarantorMapper.updateAnnotationIncludeNullById(info);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        info.getContractId().toString(),
                        "合同管理-修改担保措施")
                )
        );
    }

    @Override
    public List<ContractGuarantorListRSP> list(ContractIdListREQ req) {
        List<ContractGuarantorListRSP> rsp = new ArrayList<>();
        List<ContractGuarantor> contractGuarantors = baseMapper.selectList(Wrappers.<ContractGuarantor>lambdaQuery()
                .eq(ContractGuarantor::getContractId, req.getContractId()));
        if (CollectionUtils.isEmpty(contractGuarantors)) {
            return rsp;
        }

        Map<Long, ClientInfo> clientMap = id2NameService.clientId2CLient(contractGuarantors.stream().map(g -> {
            Set<Long> cIdSet = new HashSet<>();
            if (StringUtils.isNotBlank(g.getGuarantorIds())) {
                cIdSet.addAll(JSONArray.parseArray(g.getGuarantorIds(), Long.class));
            }
            if (StringUtils.isNotBlank(g.getGuaranteeAmountMultiple())) {
                cIdSet.addAll(JSONArray.parseArray(g.getGuaranteeAmountMultiple(), ContractGuarantor.GuaranteeMultipleJsonWrapper.class).stream().map(ContractGuarantor.GuaranteeMultipleJsonWrapper::getClientId).collect(Collectors.toSet()));
            }
            return cIdSet;
        }).flatMap(Collection::stream).collect(Collectors.toSet()));
        for (ContractGuarantor contractGuarantor : contractGuarantors) {
            ContractGuarantorListRSP guarantorListRSP = guarantorConverter.entityToRSP(contractGuarantor);

            guarantorListRSP.setResolutionType(contractGuarantor.getResolutionType());
            guarantorListRSP.setResolutionFileId(JSON.parseArray(Optional.ofNullable(contractGuarantor.getResolutionFileId()).orElse("[]"), Long.class));
            if (!ObjectUtil.isEmpty(guarantorListRSP)) {
                if (!ObjectUtil.isEmpty(guarantorListRSP.getGuarantorIds())) {
                    //担保人信息
                    guarantorListRSP.setGuarantorInfo(guarantorListRSP.getGuarantorIds().stream().map(gId -> clientMap.get(gId)).filter(Objects::nonNull).collect(Collectors.toList()));
                }
            }
            // 处理担保金额
            if (Objects.nonNull(contractGuarantor.getGuaranteeAmountSingle())) {
                guarantorListRSP.setAmountSingle(contractGuarantor.getGuaranteeAmountSingle());
            }
            if (StrUtil.isNotBlank(contractGuarantor.getGuaranteeAmountMultiple())) {
                List<ContractGuarantor.GuaranteeMultipleJsonWrapper> list = JSONUtil.toList(contractGuarantor.getGuaranteeAmountMultiple(), ContractGuarantor.GuaranteeMultipleJsonWrapper.class);
                if (CollectionUtil.isNotEmpty(list)) {
                    List<GuaranteeAmountMultipleInfo> infoList = new ArrayList<>(list.size());
                    for (ContractGuarantor.GuaranteeMultipleJsonWrapper jsonWrapper : list) {
                        GuaranteeAmountMultipleInfo info = new GuaranteeAmountMultipleInfo();
                        info.setClientId(jsonWrapper.getClientId());
                        info.setClientName(Optional.ofNullable(clientMap.get(jsonWrapper.getClientId())).map(ClientInfo::getClientName).orElse(""));
                        info.setAmount(jsonWrapper.getAmount());
                        infoList.add(info);
                    }
                    guarantorListRSP.setAmountMultiple(infoList);
                }
            }
            if (Objects.nonNull(contractGuarantor.getContactId())) {
                guarantorListRSP.setContactId(contractGuarantor.getContactId());
                CorpContactInfoLib corpContactInfoLib = corpContactInfoLibService.getById(contractGuarantor.getContactId());
                if (Objects.nonNull(corpContactInfoLib)) {
                    guarantorListRSP.setContactName(corpContactInfoLib.getName());
                }
            }
            //如果担保人类型为法人，查询章程文件id
            if (ClientType.CORPORATION.name().equals(contractGuarantor.getGuarantorType())) {
                ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                        .contractId(contractGuarantor.getContractId())
                        .tenantryId(contractGuarantor.getId())
                        .fileType(ContractConstitutionFileTypeEnum.GUARANTOR.name()).build();
                List<Long> list = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
                guarantorListRSP.setConstitutionFileList(list);
            }
            rsp.add(guarantorListRSP);
        }
        return rsp;

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean remove(ContractGuarantorRemoveREQ req) {
        ContractGuarantor originalInfo = contractGuarantorMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        //删除文章文件
        ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                .contractId(req.getContractId())
                .tenantryId(originalInfo.getId())
                .fileType(ContractConstitutionFileTypeEnum.GUARANTOR.name()).build();
        contractConstitutionFileService.deleteByFileIdAndContractId(constitutionFileBO);

        boolean result = contractGuarantorMapper.deleteById(req.getId()) > 0 ? Boolean.TRUE : Boolean.FALSE;
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(originalInfo.getContractId(), TradeStructureRoleEnum.GUARANTOR);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        originalInfo.getContractId().toString(),
                        "合同管理-删除担保措施")
                )
        );
        return result;
    }

    @Override
    public List<ContractRelationRSP> contractByclient(ContractRelationREQ req) {
        List<String> contractGuarantors;
        List<ContractRelationRSP> contractRelationRSPS = new ArrayList<>();
        contractGuarantors = contractGuarantorMapper.contractByClientList(req.getClientIds(), req.getClientCode(), req.getContractId());
        contractGuarantors.stream().forEach(rsp -> {
            ContractRelationRSP contractRelationRSP = new ContractRelationRSP();
            contractRelationRSP.setContractCode(rsp);
            contractRelationRSPS.add(contractRelationRSP);
        });
        return contractRelationRSPS;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void generateGuarantorContractCode(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同基本信息不存在"));
        LambdaQueryWrapper<ContractGuarantor> query = Wrappers.lambdaQuery();
        query.eq(ContractGuarantor::getContractId, contractId);
        query.orderByAsc(ContractGuarantor::getId);
        List<ContractGuarantor> contractGuarantorList = this.list(query);
        Assert.notEmpty(contractGuarantorList, () -> MithrasException.newException("请先新增担保措施"));
        List<String> codeList = ContractUtil.generateSubContractCode(ContractModelEnum.GUARANTEE, contractBaseInfo.getContractCode(), contractGuarantorList.size());
        List<ContractGuarantor> toUpdateList = new ArrayList<>(contractGuarantorList.size());
        for (int i = 0; i < contractGuarantorList.size(); i++) {
            ContractGuarantor exist = contractGuarantorList.get(i);
            ContractGuarantor toUpdate = new ContractGuarantor();
            toUpdate.setId(exist.getId());
            toUpdate.setGuarantorContractCode(codeList.get(i));
            toUpdate.setGuaranteeAmountSingle(exist.getGuaranteeAmountSingle());
            toUpdate.setGuaranteeAmountMultiple(exist.getGuaranteeAmountMultiple());
            toUpdateList.add(toUpdate);
        }
        this.updateBatchById(toUpdateList);
    }

    @Override
    public List<ContractGuarantor> listByContractId(Long contractId) {
        LambdaQueryWrapper<ContractGuarantor> query = Wrappers.lambdaQuery();
        query.eq(ContractGuarantor::getContractId, contractId);
        return this.list(query);
    }

    @Override
    public List<ContractGuarantor> listByContractIds(Collection<Long> contractIds) {
        LambdaQueryWrapper<ContractGuarantor> query = Wrappers.lambdaQuery();
        query.in(ContractGuarantor::getContractId, contractIds);
        return this.list(query);
    }

    @Override
    public Optional<List<Long>> getGuaranteeIdByContractIds(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Optional.empty();
        }
        List<Long> distinctGuarantorIds = baseMapper.selectList(
                        Wrappers.<ContractGuarantor>lambdaQuery().in(ContractGuarantor::getContractId, contractIds))
                .stream()
                .map(contractGuarantor -> JSON.parseArray(contractGuarantor.getGuarantorIds(), Long.class))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        return Optional.ofNullable(distinctGuarantorIds);
    }

    @Override
    public boolean existSpecificClient(Long clientId, Long contractId) {
        List<ContractGuarantor> contractGuarantorList = this.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractGuarantorList)) {
            return false;
        }
        for (ContractGuarantor contractGuarantor : contractGuarantorList) {
            if (StrUtil.isBlank(contractGuarantor.getGuarantorIds())) {
                continue;
            }
            List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
            for (Long id : ids) {
                if (Objects.equals(id, clientId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void doGuaranteeBasic(ContractGuaranteeBasicREQ contractGuaranteeBasicREQ, ContractGuarantor contractGuarantor) {
        String mark = contractGuaranteeBasicREQ.getJointGuaranteeMark();
        if (Objects.equals(JointGuaranteeMarkEnum.SINGLE.name(), mark) || Objects.equals(JointGuaranteeMarkEnum.JOINT.name(), mark)) {
            Assert.notNull(contractGuaranteeBasicREQ.getAmountSingle(), () -> MithrasException.newException("担保金额不能为空"));
            contractGuarantor.setGuaranteeAmountSingle(contractGuaranteeBasicREQ.getAmountSingle());
            contractGuarantor.setGuaranteeAmountMultiple(null);
        } else if (Objects.equals(JointGuaranteeMarkEnum.MULTIPLE_SEPARATE.name(), mark)) {
            String guaranteeAmountMultipleInfoStr = Assert.notEmpty(contractGuaranteeBasicREQ.getAmountMultiple(), () -> MithrasException.newException("担保金额不能为空"));
            List<GuaranteeAmountMultipleInfo> guaranteeAmountMultipleInfoList = JSON.parseArray(guaranteeAmountMultipleInfoStr, GuaranteeAmountMultipleInfo.class);
            List<ContractGuarantor.GuaranteeMultipleJsonWrapper> jsonWrapperList = new ArrayList<>(guaranteeAmountMultipleInfoList.size());
            for (GuaranteeAmountMultipleInfo guaranteeAmountMultipleInfo : guaranteeAmountMultipleInfoList) {
                ContractGuarantor.GuaranteeMultipleJsonWrapper jsonWrapper = new ContractGuarantor.GuaranteeMultipleJsonWrapper();
                jsonWrapper.setClientId(guaranteeAmountMultipleInfo.getClientId());
                jsonWrapper.setAmount(guaranteeAmountMultipleInfo.getAmount());
                jsonWrapperList.add(jsonWrapper);
            }
            contractGuarantor.setGuaranteeAmountMultiple(JSONUtil.toJsonStr(jsonWrapperList));
            contractGuarantor.setGuaranteeAmountSingle(null);
        } else {
            throw new MithrasException("未定义的联保标志");
        }
    }

    private void saveConstitutionFiles(List<MultipartFile> multipartFileList, List<Long> constitutionFileIds, ContractGuarantor info) {
        //保存章程文件
        try {
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .constitutionFileIds(constitutionFileIds)
                    .tenantryId(info.getId())
                    .contractId(info.getContractId())
                    .multipartFileList(multipartFileList)
                    .fileType(ContractConstitutionFileTypeEnum.GUARANTOR.name()).build();
            contractConstitutionFileService.saveConstitutionFiles(constitutionFileBO);
        } catch (Exception e) {
            log.error("保存章程文件异常", e);
            throw new MithrasException("保存章程文件异常");
        }
    }
}
