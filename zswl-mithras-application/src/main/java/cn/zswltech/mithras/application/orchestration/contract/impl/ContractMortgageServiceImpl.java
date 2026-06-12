package cn.zswltech.mithras.application.orchestration.contract.impl;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractMortgageService;
import cn.zswltech.mithras.contract.core.ContractTradeStructureService;

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
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageAddREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageModifyREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageRemoveREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.contract.convert.contract.ContractEntityMortgageItemConvert;
import cn.zswltech.mithras.contract.convert.contract.ContractMortgageConvert;
import cn.zswltech.mithras.contract.convert.contract.ContractMortgageConverter;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.contract.enums.contract.ContractConstitutionFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractModelEnum;
import cn.zswltech.mithras.contract.enums.contract.MortgageTypeEnum;
import cn.zswltech.mithras.contract.excel.importer.ContractEntityItemMortgageExcelImporter;
import cn.zswltech.mithras.contract.excel.model.ContractEntityMortgageItemExcelModel;
import cn.zswltech.mithras.contract.mapper.contract.ContractMortgageMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.model.contract.ContractMortgageItem;
import cn.zswltech.mithras.contract.model.contract.ContractPledge;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.customer.event.ClientViewAuthorityEvent;
import cn.zswltech.mithras.projectprocess.application.model.ContractConstitutionFileBO;
import cn.zswltech.mithras.application.orchestration.contract.*;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.contract.util.ContractUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import cn.zswltech.mithras.contract.core.ContractCodeAbstract;
import cn.zswltech.mithras.contract.core.ContractConstitutionFileService;
import cn.zswltech.mithras.contract.core.ContractMortgageItemService;


/**
 * @author vico
 * @description 合同-抵押措施
 * @date 2022-08-12
 */
@Service
@Slf4j
public class ContractMortgageServiceImpl extends ContractCodeAbstract<ContractMortgageMapper, ContractMortgage> implements ContractMortgageService {

    @Resource
    private ContractMortgageMapper contractMortgageMapper;

    @Resource
    private ContractMortgageConverter baseConverter;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private ContractEntityItemMortgageExcelImporter contractEntityItemMortgageExcelImporter;

    @Resource
    private ContractMortgageItemService contractMortgageItemService;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private ContractConstitutionFileService contractConstitutionFileService;

    private static final String MORTGAGE = "MORTGAGE";

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(ContractMortgageAddREQ req) {
        if (Objects.equals(req.getAssess(), YesOrNoNumberEnum.YES.getCode())) {
            Assert.notBlank(req.getAppraisalCompany(), () -> MithrasException.newException("评估公司不能为空"));
            Assert.notBlank(req.getAssessDate(), () -> MithrasException.newException("评估日期不能为空"));
        }
        ContractMortgage info = baseConverter.reqToEntity(req);
        contractMortgageMapper.insert(info);
        if (ObjectUtil.isNotEmpty(req.getFile())) {
            InputStream inputStream;
            InputStream inputStream1;
            try {
                //上传文件
                inputStream = new BufferedInputStream(req.getFile().getInputStream());
                inputStream1 = new BufferedInputStream(req.getFile().getInputStream());
                info.setFileId(materialsListService.add(inputStream1, req.getFile().getOriginalFilename(), info.getId(), MORTGAGE, BusinessModuleEnum.CONTRACT.name()));
                contractMortgageMapper.updateById(info);
                // 扣取表格存入数据库
                List<ContractEntityMortgageItemExcelModel> parse = contractEntityItemMortgageExcelImporter.parse(inputStream);
                // excel数据模型转化为DB数据模型
                List<ContractMortgageItem> dataList = parse.stream().map(item -> {
                    item.checkMortgageItem();
                    ContractMortgageItem contractMortgageItem = ContractEntityMortgageItemConvert.toContractMortgageItem(item);
                    contractMortgageItem.setMortgageId(info.getId());
                    contractMortgageItem.setContractId(info.getContractId());
                    return contractMortgageItem;
                }).collect(Collectors.toList());
                // 批量保存
                contractMortgageItemService.saveBatch(dataList);
            } catch (MithrasException e) {
                throw e;
            } catch (Exception e) {
                log.info("ContractMortgageServiceImpl add upLoadFile error ", e);
                throw new MithrasException("上传抵押物清单失败");
            }
        }
        //上传抵质押文件
        if (MortgageTypeEnum.REAL_ESTATE_MORTGAGE.name().equalsIgnoreCase(req.getContractMortgageType())) {
            try {
                ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                        .tenantryId(info.getId())
                        .contractId(req.getContractId())
                        .fileType(ContractConstitutionFileTypeEnum.MORTGAGE.name())
                        .constitutionFileIds(null)
                        .multipartFileList(req.getMortgagePledgeFileList()).build();
                contractConstitutionFileService.saveConstitutionFiles(constitutionFileBO);
            } catch (Exception e) {
                log.error("保存抵质押文件异常", e);
                throw new MithrasException("保存抵质押文件异常");
            }
        }
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(req.getContractId(), TradeStructureRoleEnum.MORTGAGE);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        info.getContractId().toString(),
                        "合同管理-新增抵押措施")
                )
        );
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(Long contractId, List<ClientInfo> clientInfoList) {
        if (ObjectUtil.isEmpty(clientInfoList)) {
            return;
        }
        ContractMortgage contractMortgage;
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
            contractMortgage = new ContractMortgage();
            contractMortgage.setContractId(contractId);
            contractMortgage.setMortgageType(ClientType.CORPORATION.name());
            contractMortgage.setMortgageIds(JSONUtil.toJsonStr(fguarantorIds));
            baseMapper.insert(contractMortgage);
        }
        if (zguarantorIds.size() > 0) {
            contractMortgage = new ContractMortgage();
            contractMortgage.setContractId(contractId);
            contractMortgage.setMortgageType(ClientType.NORMAL.name());
            contractMortgage.setMortgageIds(JSONUtil.toJsonStr(zguarantorIds));
            baseMapper.insert(contractMortgage);
        }
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractId, TradeStructureRoleEnum.MORTGAGE);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        contractId.toString(),
                        "合同管理-新增抵押措施")
                )
        );
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void modify(ContractMortgageModifyREQ req) {
        if (Objects.equals(req.getAssess(), YesOrNoNumberEnum.YES.getCode())) {
            Assert.notBlank(req.getAppraisalCompany(), () -> MithrasException.newException("评估公司不能为空"));
            Assert.notBlank(req.getAssessDate(), () -> MithrasException.newException("评估日期不能为空"));
        }
        ContractMortgage oldDbData = Assert.notNull(baseMapper.selectById(req.getId()), () -> MithrasException.newException("未找到对应的抵押措施信息"));
//        ContractMortgage info = baseConverter.modifyToEntity(req);
        ContractMortgage info = ContractMortgageConvert.merge(oldDbData, req);
        if (Objects.isNull(req.getFileId()) && Objects.nonNull(req.getFile()) && ObjectUtil.isNotEmpty(req.getFile())) {
            InputStream inputStream;
            InputStream inputStream1;
            try {
                inputStream = new BufferedInputStream(req.getFile().getInputStream());
                inputStream1 = new BufferedInputStream(req.getFile().getInputStream());
                // 扣取表格存入数据库
                List<ContractEntityMortgageItemExcelModel> parse = contractEntityItemMortgageExcelImporter.parse(inputStream);
                Assert.notEmpty(parse, () -> MithrasException.newException("抵押物清单为空"));
                // excel数据模型转化为DB数据模型
                List<ContractMortgageItem> dataList = parse.stream().map(item -> {
                    item.checkMortgageItem();
                    ContractMortgageItem contractMortgageItem = ContractEntityMortgageItemConvert.toContractMortgageItem(item);
                    contractMortgageItem.setMortgageId(info.getId());
                    contractMortgageItem.setContractId(info.getContractId());
                    return contractMortgageItem;
                }).collect(Collectors.toList());
                //上传文件
                info.setFileId(materialsListService.add(inputStream1, req.getFile().getOriginalFilename(), info.getId(), MORTGAGE, BusinessModuleEnum.CONTRACT.name()));
                //清空老数据
                contractMortgageItemService.remove(Wrappers.<ContractMortgageItem>lambdaQuery()
                        .eq(ContractMortgageItem::getMortgageId, req.getId()));
                // 批量保存
                contractMortgageItemService.saveBatch(dataList);
            } catch (IOException e) {
                log.info("ContractMortgageServiceImpl modify upLoadFile error ", e);
                throw new MithrasException("上传抵押物清单失败");
            }
        } else if (Objects.isNull(req.getFileId()) && Objects.isNull(req.getFile())) {
            InputStream inputStream;
            // 删除老的
            List<MaterialsList> exist = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(MORTGAGE), Collections.singletonList(req.getId()));
            if (CollectionUtil.isNotEmpty(exist)) {
                materialsListService.remove(exist.stream().map(MaterialsList::getId).collect(Collectors.toList()));
            }
            //清空老数据
            contractMortgageItemService.remove(Wrappers.<ContractMortgageItem>lambdaQuery()
                    .eq(ContractMortgageItem::getMortgageId, req.getId()));
        }
        //更新抵质押文件
        if (MortgageTypeEnum.REAL_ESTATE_MORTGAGE.name().equalsIgnoreCase(req.getContractMortgageType())) {
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .contractId(info.getContractId())
                    .tenantryId(info.getId())
                    .fileType(ContractConstitutionFileTypeEnum.MORTGAGE.name()).build();
            List<Long> fileList = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
            Set<Long> finalList = null;
            if (req.getMortgagePledgeFileIds() == null || req.getMortgagePledgeFileIds().isEmpty()) {
                finalList = new HashSet<>();
            } else {
                finalList = new HashSet<>();
                if (fileList != null && !fileList.isEmpty()) {
                    for (Long fileId : req.getMortgagePledgeFileIds()) {
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
                        .fileType(ContractConstitutionFileTypeEnum.MORTGAGE.name())
                        .constitutionFileIds(new ArrayList<>(finalList))
                        .multipartFileList(req.getMortgagePledgeFileList()).build();
                contractConstitutionFileService.saveConstitutionFiles(finalConstitutionFileBO);
            } catch (Exception e) {
                log.error("处理抵质押文件发生异常", e);
                throw new MithrasException("处理抵质押文件发生异常");
            }
        }
        // 格式化合同编号
        info.setMortgageContractCode(ContractUtil.format(info.getMortgageContractCode()));
        contractMortgageMapper.updateAnnotationIncludeNullById(info);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        info.getContractId().toString(),
                        "合同管理-修改抵押措施")
                )
        );
    }

    @Override
    public List<ContractRelationRSP> contractByclient(ContractRelationREQ req) {
        List<String> contractGuarantors;
        List<ContractRelationRSP> contractRelationRSPS = new ArrayList<>();
        contractGuarantors = contractMortgageMapper.contractByClientList(req.getClientIds(), req.getClientCode(), req.getContractId());
        contractGuarantors.stream().forEach(rsp -> {
            ContractRelationRSP contractRelationRSP = new ContractRelationRSP();
            contractRelationRSP.setContractCode(rsp);
            contractRelationRSPS.add(contractRelationRSP);
        });
        return contractRelationRSPS;
    }

    @Override
    public List<ContractMortgageListRSP> list(ContractIdListREQ req) {
        List<ContractMortgageListRSP> rsp = new ArrayList<>();
        List<ContractMortgage> contractMortgages = baseMapper.selectList(Wrappers.<ContractMortgage>lambdaQuery()
                .eq(ContractMortgage::getContractId, req.getContractId()));
        if (CollectionUtils.isEmpty(contractMortgages)) {
            return rsp;
        }
        Map<Long, ClientInfo> clientInfoMap = id2NameService.clientId2CLient(contractMortgages.stream().map(ContractMortgage::getMortgageIds).filter(StringUtils::isNotBlank).map(s -> JSONArray.parseArray(s, Long.class)).flatMap(Collection::stream).collect(Collectors.toSet()));
        List<Long> materialsIdList = contractMortgages.stream().map(ContractMortgage::getFileId).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, MaterialsList> materialsListMap = CollectionUtils.isEmpty(materialsIdList) ? new HashMap<>() : materialsListService.getByIds(materialsIdList).stream().collect(Collectors.toMap(MaterialsList::getId, m -> m));
        for (ContractMortgage contractMortgage : contractMortgages) {
            ContractMortgageListRSP guarantorListRSP = baseConverter.entityToRSP(contractMortgage);
            if (!ObjectUtil.isEmpty(guarantorListRSP)) {
                if (!ObjectUtil.isEmpty(guarantorListRSP.getMortgageIds())) {
                    //抵押人
                    guarantorListRSP.setMortgageInfo(guarantorListRSP.getMortgageIds().stream().map(mId -> clientInfoMap.get(mId)).filter(Objects::nonNull).collect(Collectors.toList()));
                }
            }
            if (ObjectUtil.isNotEmpty(guarantorListRSP.getFileId())) {
                MaterialsList byId = materialsListMap.get(guarantorListRSP.getFileId());
                if (ObjectUtil.isNotEmpty(byId)) {
                    guarantorListRSP.setFileId(byId.getId());
                    guarantorListRSP.setFileName(byId.getFilename());
                    guarantorListRSP.setFilePath(byId.getFilePath());
                }
            }
            //查询抵质押文件id
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .contractId(contractMortgage.getContractId())
                    .tenantryId(contractMortgage.getId())
                    .fileType(ContractConstitutionFileTypeEnum.MORTGAGE.name()).build();
            List<Long> list = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
            List<ContractMortgageListRSP.MortgagePledgeObj> mortgagePledgeObjList = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                for (Long id : list) {
                    MaterialsList materialsList = materialsListService.getById(id);
                    ContractMortgageListRSP.MortgagePledgeObj mortgagePledgeObj = new ContractMortgageListRSP.MortgagePledgeObj();
                    mortgagePledgeObj.setFileId(id);
                    mortgagePledgeObj.setFileName(materialsList.getFilename());
                    mortgagePledgeObj.setFilePath(materialsList.getFilePath());
                    mortgagePledgeObjList.add(mortgagePledgeObj);
                }
            }
            guarantorListRSP.setMortgagePledgeObjList(mortgagePledgeObjList);
            rsp.add(guarantorListRSP);
        }
        return rsp;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean remove(ContractMortgageRemoveREQ req) {
        ContractMortgage originalInfo = contractMortgageMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //删除抵质押文件
        ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                .contractId(req.getContractId())
                .tenantryId(originalInfo.getId())
                .fileType(ContractConstitutionFileTypeEnum.MORTGAGE.name()).build();
        contractConstitutionFileService.deleteByFileIdAndContractId(constitutionFileBO);
        // 删除抵押措施
        contractMortgageMapper.deleteById(req.getId());
        // 删除抵押物数据
        contractMortgageItemService.removeByMortgageId(req.getId());
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(originalInfo.getContractId(), TradeStructureRoleEnum.MORTGAGE);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        originalInfo.getContractId().toString(),
                        "合同管理-删除抵押措施")
                )
        );
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void generateMortgageContractCode(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同基本信息不存在"));
        LambdaQueryWrapper<ContractMortgage> query = Wrappers.lambdaQuery();
        query.eq(ContractMortgage::getContractId, contractId);
        query.orderByAsc(ContractMortgage::getId);
        List<ContractMortgage> contractMortgageList = this.list(query);
        Assert.notEmpty(contractMortgageList, () -> MithrasException.newException("请先新增抵押措施"));
        List<String> codeList = ContractUtil.generateSubContractCode(ContractModelEnum.MORTGAGE, contractBaseInfo.getContractCode(), contractMortgageList.size());
        List<ContractMortgage> toUpdateList = new ArrayList<>(contractMortgageList.size());
        for (int i = 0; i < contractMortgageList.size(); i++) {
            ContractMortgage exist = contractMortgageList.get(i);
            ContractMortgage toUpdate = new ContractMortgage();
            toUpdate.setId(exist.getId());
            toUpdate.setMortgageContractCode(codeList.get(i));
            toUpdateList.add(toUpdate);
        }
        this.updateBatchById(toUpdateList);
    }

    @Override
    public List<ContractMortgage> listByContractId(Long contractId) {
        LambdaQueryWrapper<ContractMortgage> query = Wrappers.lambdaQuery();
        query.eq(ContractMortgage::getContractId, contractId);
        return this.list(query);
    }

    @Override
    public Optional<List<Long>> getMortgageIdByContractIds(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Optional.empty();
        }
        List<Long> distinctMortgageIds = baseMapper.selectList(
                        Wrappers.<ContractMortgage>lambdaQuery().in(ContractMortgage::getContractId, contractIds))
                .stream()
                .map(contractMortgage -> JSON.parseArray(contractMortgage.getMortgageIds(), Long.class))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        return Optional.ofNullable(distinctMortgageIds);
    }

    @Override
    public List<ContractMortgage> listByContractIds(Collection<Long> contractIds) {
        LambdaQueryWrapper<ContractMortgage> query = Wrappers.lambdaQuery();
        query.in(ContractMortgage::getContractId, contractIds);
        return this.list(query);
    }

    @Override
    public boolean existSpecificClient(Long clientId, Long contractId) {
        List<ContractMortgage> contractMortgageList = this.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractMortgageList)) {
            return false;
        }
        for (ContractMortgage contractMortgage : contractMortgageList) {
            if (StrUtil.isBlank(contractMortgage.getMortgageIds())) {
                continue;
            }
            List<Long> ids = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
            for (Long id : ids) {
                if (Objects.equals(id, clientId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveConstitutionFiles(List<MultipartFile> multipartFileList, List<Long> constitutionFileIds, ContractMortgage info) {
        //保存抵质押文件
        try {
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .constitutionFileIds(constitutionFileIds)
                    .tenantryId(info.getId())
                    .contractId(info.getContractId())
                    .multipartFileList(multipartFileList)
                    .fileType(ContractConstitutionFileTypeEnum.MORTGAGE.name()).build();
            contractConstitutionFileService.saveConstitutionFiles(constitutionFileBO);
        } catch (Exception e) {
            log.error("保存章程文件异常", e);
            throw new MithrasException("保存章程文件异常");
        }
    }
}
