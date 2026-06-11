package cn.zswltech.mithras.application.orchestration.contract.impl;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import cn.zswltech.mithras.contract.core.ContractTradeStructureService;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
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
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeAddREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeModifyREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeRemoveREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.contract.convert.contract.ContractEntityPledgeItemConvert;
import cn.zswltech.mithras.contract.convert.contract.ContractPledgeConverter;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.contract.enums.contract.ContractConstitutionFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractModelEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.PledgeTypeEnum;
import cn.zswltech.mithras.contract.excel.importer.ContractEntityItemPladgeExcelImporter;
import cn.zswltech.mithras.contract.excel.model.ContractEntityPledgeItemExcelModel;
import cn.zswltech.mithras.contract.mapper.contract.ContractPledgeMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledgeItem;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.customer.event.ClientViewAuthorityEvent;
import cn.zswltech.mithras.projectprocess.application.bo.ContractConstitutionFileBO;
import cn.zswltech.mithras.application.orchestration.contract.*;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.contract.util.ContractUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import cn.zswltech.mithras.contract.core.ContractConstitutionFileService;
import cn.zswltech.mithras.contract.core.ContractPledgeItemService;


/**
 * @author vico
 * @description 合同-质押措施
 * @date 2022-08-12
 */
@Slf4j
@Service
public class ContractPledgeServiceImpl extends ServiceImpl<ContractPledgeMapper, ContractPledge> implements ContractPledgeService {
    private static final String PLEDGE_ITEM_FILE_TYPE = "PLEDGE";

    @Resource
    private ContractPledgeMapper contractPledgeMapper;

    @Resource
    private ContractPledgeConverter baseConverter;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private ContractEntityItemPladgeExcelImporter contractEntityItemPladgeExcelImporter;

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private ContractPledgeItemService contractPledgeItemService;

    @Resource
    private ContractConstitutionFileService contractConstitutionFileService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean add(ContractPledgeAddREQ req) {
        ContractPledge info = baseConverter.reqToEntity(req);
        contractPledgeMapper.insert(info);
        // 处理质押物清单
        if (req.getFile().isEmpty()) {
            throw new MithrasException("质押物清单不能为空");
        }
        // 上传文件
        materialsListService.add(req.getFile(), info.getId(), PLEDGE_ITEM_FILE_TYPE, BusinessModuleEnum.CONTRACT.name());
        //上传抵质押文件
        if (PledgeTypeEnum.ACCOUNTS_RECEIVABLE_PLEDGE.name().equalsIgnoreCase(req.getContractPledgeType())) {
            try {
                ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                        .tenantryId(info.getId())
                        .contractId(req.getContractId())
                        .fileType(ContractConstitutionFileTypeEnum.PLEDGE.name())
                        .constitutionFileIds(null)
                        .multipartFileList(req.getMortgagePledgeFileList()).build();
                contractConstitutionFileService.saveConstitutionFiles(constitutionFileBO);
            } catch (Exception e) {
                log.error("保存抵质押文件异常", e);
                throw new MithrasException("保存抵质押文件异常");
            }
        }
        // 解析excel数据并落库，导入逻辑和租赁物、抵押物共用，但是质押物只需要其中三个字段即可
        try {
            contractPledgeItemService.saveBatch(this.parse(req.getFile(), info.getContractId(), info.getId()));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("处理质押物清单发生异常", e);
            throw new MithrasException("处理质押物清单发生异常");
        }
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(req.getContractId(), TradeStructureRoleEnum.PLEDGE);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        info.getContractId().toString(),
                        "合同管理-新增质押措施")
                )
        );
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(Long contractId, List<ClientInfo> clientInfoList) {
        if (ObjectUtil.isEmpty(clientInfoList)) {
            return;
        }
        ContractPledge contractPledge = null;
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
            contractPledge = new ContractPledge();
            contractPledge.setContractId(contractId);
            contractPledge.setPledgeType(ClientType.CORPORATION.name());
            contractPledge.setPledgeIds(JSONUtil.toJsonStr(fguarantorIds));
            baseMapper.insert(contractPledge);
        }
        if (zguarantorIds.size() > 0) {
            contractPledge = new ContractPledge();
            contractPledge.setContractId(contractId);
            contractPledge.setPledgeType(ClientType.NORMAL.name());
            contractPledge.setPledgeIds(JSONUtil.toJsonStr(zguarantorIds));
            baseMapper.insert(contractPledge);
        }
        if (Objects.nonNull(contractPledge)) {
            // 合同交易结构辅助表
            SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractId, TradeStructureRoleEnum.PLEDGE);
            // 通知客户权限变更
            ApplicationContextUtil.getApplicationContext().publishEvent(
                    new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                            BusinessModuleEnum.CONTRACT,
                            contractPledge.getContractId().toString(),
                            "合同管理-新增质押措施")
                    )
            );
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void modify(ContractPledgeModifyREQ modifyREQ) {
        if (Objects.isNull(modifyREQ.getFileId()) && Objects.nonNull(modifyREQ.getFile()) && !modifyREQ.getFile().isEmpty()) {
            // 删除老的
            List<MaterialsList> exist = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(PLEDGE_ITEM_FILE_TYPE), Collections.singletonList(modifyREQ.getId()));
            if (CollectionUtil.isNotEmpty(exist)) {
                materialsListService.remove(exist.stream().map(MaterialsList::getId).collect(Collectors.toList()));
            }
            contractPledgeItemService.removeByPledgeId(modifyREQ.getId());
            // 处理新的质押物清单
            try {
                contractPledgeItemService.saveBatch(this.parse(modifyREQ.getFile(), modifyREQ.getContractId(), modifyREQ.getId()));
                materialsListService.add(modifyREQ.getFile(), modifyREQ.getId(), PLEDGE_ITEM_FILE_TYPE, BusinessModuleEnum.CONTRACT.name());
            } catch (MithrasException e) {
                throw e;
            } catch (Exception e) {
                log.error("处理质押物清单发生异常", e);
                throw new MithrasException("处理质押物清单发生异常");
            }
        } else if (Objects.isNull(modifyREQ.getFileId()) && Objects.isNull(modifyREQ.getFile())) {
            // 删除老的
            List<MaterialsList> exist = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(PLEDGE_ITEM_FILE_TYPE), Collections.singletonList(modifyREQ.getId()));
            if (CollectionUtil.isNotEmpty(exist)) {
                materialsListService.remove(exist.stream().map(MaterialsList::getId).collect(Collectors.toList()));
            }
            contractPledgeItemService.removeByPledgeId(modifyREQ.getId());
        }
        // 更新质押措施信息
        ContractPledge info = baseConverter.modifyToEntity(modifyREQ);
        if (PledgeTypeEnum.ACCOUNTS_RECEIVABLE_PLEDGE.name().equalsIgnoreCase(modifyREQ.getContractPledgeType())) {
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .contractId(info.getContractId())
                    .tenantryId(info.getId())
                    .fileType(ContractConstitutionFileTypeEnum.PLEDGE.name()).build();
            List<Long> fileList = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
            Set<Long> finalList = null;
            if (modifyREQ.getMortgagePledgeFileIds() == null || modifyREQ.getMortgagePledgeFileIds().isEmpty()) {
                finalList = new HashSet<>();
            } else {
                finalList = new HashSet<>();
                if (fileList != null && !fileList.isEmpty()) {
                    for (Long fileId : modifyREQ.getMortgagePledgeFileIds()) {
                        for (Long existId : fileList) {
                            if (fileId.equals(existId)) {
                                finalList.add(fileId);
                            }
                        }
                    }
                }
            }
            try {
                ContractConstitutionFileBO finalConstitutionFileBO = ContractConstitutionFileBO.builder()
                        .tenantryId(info.getId())
                        .contractId(info.getContractId())
                        .fileType(ContractConstitutionFileTypeEnum.PLEDGE.name())
                        .constitutionFileIds(new ArrayList<>(finalList))
                        .multipartFileList(modifyREQ.getMortgagePledgeFileList()).build();
                contractConstitutionFileService.saveConstitutionFiles(finalConstitutionFileBO);
            } catch (Exception e) {
                log.error("处理抵质押文件发生异常", e);
                throw new MithrasException("处理抵质押文件发生异常");
            }
        }
        // 格式化合同编号
        info.setPledgeContractCode(ContractUtil.format(info.getPledgeContractCode()));
        contractPledgeMapper.updateAnnotationIncludeNullById(info);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        info.getContractId().toString(),
                        "合同管理-修改质押措施")
                )
        );
    }

    @Override
    public List<ContractPledgeListRSP> list(ContractIdListREQ req) {
        List<ContractPledgeListRSP> rsp = new ArrayList<>();
        ContractPledgeListRSP contractPledgeListRSP;
        List<ContractPledge> contractPledgeList = baseMapper.selectList(Wrappers.<ContractPledge>lambdaQuery()
                .eq(ContractPledge::getContractId, req.getContractId()));
        if (CollectionUtils.isEmpty(contractPledgeList)) {
            return rsp;
        }
        Map<Long, ClientInfo> clientInfoMap = id2NameService.clientId2CLient(contractPledgeList.stream().map(ContractPledge::getPledgeIds).filter(StringUtils::isNotBlank).map(s -> JSONArray.parseArray(s, Long.class)).flatMap(Collection::stream).collect(Collectors.toSet()));
        Map<Long, List<MaterialsList>> materialsListMap = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(PLEDGE_ITEM_FILE_TYPE), contractPledgeList.stream().map(ContractPledge::getId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(MaterialsList::getMainId));
        for (ContractPledge contractPledge : contractPledgeList) {
            contractPledgeListRSP = baseConverter.entityToRSP(contractPledge);
            if (!ObjectUtil.isEmpty(contractPledgeListRSP)) {
                if (!ObjectUtil.isEmpty(contractPledgeListRSP.getPledgeIds())) {
                    //抵押人
                    contractPledgeListRSP.setPledgeInfo(contractPledgeListRSP.getPledgeIds().stream().map(pId -> clientInfoMap.get(pId)).filter(Objects::nonNull).collect(Collectors.toList()));
                }
            }
            // 查询质押物清单文件
            List<MaterialsList> materialsListList = Optional.ofNullable(materialsListMap.get(contractPledgeListRSP.getId())).orElse(new ArrayList<>());
            if (CollectionUtil.isNotEmpty(materialsListList)) {
                MaterialsList file = materialsListList.get(0);
                contractPledgeListRSP.setFileId(file.getId());
                contractPledgeListRSP.setFileName(file.getFilename());
                contractPledgeListRSP.setFileId(file.getId());
            }
            //查询抵质押文件id
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .contractId(contractPledge.getContractId())
                    .tenantryId(contractPledge.getId())
                    .fileType(ContractConstitutionFileTypeEnum.PLEDGE.name()).build();
            List<Long> list = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
            List<ContractPledgeListRSP.MortgagePledgeObj> mortgagePledgeObjList = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                for (Long id : list) {
                    MaterialsList materialsList = materialsListService.getById(id);
                    ContractPledgeListRSP.MortgagePledgeObj mortgagePledgeObj = new ContractPledgeListRSP.MortgagePledgeObj();
                    mortgagePledgeObj.setFileId(id);
                    mortgagePledgeObj.setFileName(materialsList.getFilename());
                    mortgagePledgeObj.setFilePath(materialsList.getFilePath());
                    mortgagePledgeObjList.add(mortgagePledgeObj);
                }
            }
            contractPledgeListRSP.setMortgagePledgeObjList(mortgagePledgeObjList);
            rsp.add(contractPledgeListRSP);
        }
        return rsp;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean remove(ContractPledgeRemoveREQ req) {
        ContractPledge originalInfo = contractPledgeMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //删除抵质押文件
        ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                .contractId(req.getContractId())
                .tenantryId(originalInfo.getId())
                .fileType(ContractConstitutionFileTypeEnum.PLEDGE.name()).build();
        contractConstitutionFileService.deleteByFileIdAndContractId(constitutionFileBO);
        // 删除质押措施
        contractPledgeMapper.deleteById(req.getId());
        // 删除质押物清单数据
        contractPledgeItemService.removeByPledgeId(req.getId());
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(originalInfo.getContractId(), TradeStructureRoleEnum.PLEDGE);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        originalInfo.getContractId().toString(),
                        "合同管理-删除质押措施")
                )
        );
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void generatePledgeContractCode(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同基本信息不存在"));
        LambdaQueryWrapper<ContractPledge> query = Wrappers.lambdaQuery();
        query.eq(ContractPledge::getContractId, contractId);
        query.orderByAsc(ContractPledge::getId);
        List<ContractPledge> contractPledgeList = this.list(query);
        Assert.notEmpty(contractPledgeList, () -> MithrasException.newException("请先新增质押措施"));
        List<String> codeList = ContractUtil.generateSubContractCode(ContractModelEnum.PLEDGE, contractBaseInfo.getContractCode(), contractPledgeList.size());
        List<ContractPledge> toUpdateList = new ArrayList<>(contractPledgeList.size());
        for (int i = 0; i < contractPledgeList.size(); i++) {
            ContractPledge exist = contractPledgeList.get(i);
            ContractPledge toUpdate = new ContractPledge();
            toUpdate.setId(exist.getId());
            toUpdate.setPledgeContractCode(codeList.get(i));
            toUpdateList.add(toUpdate);
        }
        this.updateBatchById(toUpdateList);
    }

    @Override
    public List<ContractPledge> listByContractId(Long contractId) {
        LambdaQueryWrapper<ContractPledge> query = Wrappers.lambdaQuery();
        query.eq(ContractPledge::getContractId, contractId);
        return this.list(query);
    }

    @Override
    public Optional<List<Long>> getPledgeIdByContractIds(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Optional.empty();
        }
        List<Long> distinctMortgageIds = baseMapper.selectList(
                        Wrappers.<ContractPledge>lambdaQuery().in(ContractPledge::getContractId, contractIds))
                .stream()
                .map(contractPledge -> JSON.parseArray(contractPledge.getPledgeIds(), Long.class))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        return Optional.ofNullable(distinctMortgageIds);
    }

    @Override
    public List<ContractPledge> listByContractIds(Collection<Long> contractIds) {
        LambdaQueryWrapper<ContractPledge> query = Wrappers.lambdaQuery();
        query.in(ContractPledge::getContractId, contractIds);
        return this.list(query);
    }

    @Override
    public boolean existSpecificClient(Long clientId, Long contractId) {
        List<ContractPledge> contractPledgeList = this.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractPledgeList)) {
            return false;
        }
        for (ContractPledge contractPledge : contractPledgeList) {
            if (StrUtil.isBlank(contractPledge.getPledgeIds())) {
                continue;
            }
            List<Long> ids = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
            for (Long id : ids) {
                if (Objects.equals(id, clientId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ContractRelationRSP> contractByclient(ContractRelationREQ req) {
        List<String> contractGuarantors;
        List<ContractRelationRSP> contractRelationRSPS = new ArrayList<>();
        contractGuarantors = contractPledgeMapper.contractByClientList(req.getClientIds(), req.getClientCode(), req.getContractId());
        contractGuarantors.stream().forEach(rsp -> {
            ContractRelationRSP contractRelationRSP = new ContractRelationRSP();
            contractRelationRSP.setContractCode(rsp);
            contractRelationRSPS.add(contractRelationRSP);
        });
        return contractRelationRSPS;
    }

    private List<ContractPledgeItem> parse(MultipartFile file, Long contractId, Long pledgeId) throws Exception {
        InputStream inputStream = file.getInputStream();
        List<ContractEntityPledgeItemExcelModel> excelModelList = contractEntityItemPladgeExcelImporter.parse(inputStream);
        if (CollectionUtil.isEmpty(excelModelList)) {
            throw new MithrasException("质押物清单内容不能为空");
        }
        return excelModelList.stream().map(item -> {
            item.checkPledgeItem();
            ContractPledgeItem contractPledgeItem = ContractEntityPledgeItemConvert.toContractPledgeItem(item);
            contractPledgeItem.setPledgeId(pledgeId);
            contractPledgeItem.setContractId(contractId);
            return contractPledgeItem;
        }).collect(Collectors.toList());
    }

    private void saveConstitutionFiles(List<MultipartFile> multipartFileList, List<Long> constitutionFileIds, ContractPledge info) {
        //保存抵质押文件
        try {
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .constitutionFileIds(constitutionFileIds)
                    .tenantryId(info.getId())
                    .contractId(info.getContractId())
                    .multipartFileList(multipartFileList)
                    .fileType(ContractConstitutionFileTypeEnum.PLEDGE.name()).build();
            contractConstitutionFileService.saveConstitutionFiles(constitutionFileBO);
        } catch (Exception e) {
            log.error("保存章程文件异常", e);
            throw new MithrasException("保存章程文件异常");
        }
    }

}
