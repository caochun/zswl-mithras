package cn.zswltech.mithras.service.service.contract.impl;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryListRSP;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryModifyREQ;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.contract.convert.contract.ContractTenantryConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.contract.enums.contract.*;
import cn.zswltech.mithras.contract.mapper.contract.ContractTenantryMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpContactInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.customer.event.ClientViewAuthorityEvent;
import cn.zswltech.mithras.projectprocess.service.bo.ContractConstitutionFileBO;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum.MAIN_LESSSEE;
import cn.zswltech.mithras.contract.core.application.ContractCodeAbstract;
import cn.zswltech.mithras.contract.core.application.ContractConstitutionFileService;


/**
 * @author vico
 * @description 合同-承租人表
 * @date 2022-08-12
 */
@Slf4j
@Service
public class ContractTenantryServiceImpl extends ContractCodeAbstract<ContractTenantryMapper, ContractTenantry> implements ContractTenantryService {

    @Resource
    private ContractTenantryMapper contractTenantryMapper;

    @Resource
    private Id2NameService id2NameService;

    @Autowired
    private ContractBaseInfoService baseInfoService;

    @Resource
    private ContractService contractService;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private CorpContactInfoLibMapper contactInfoLibMapper;

    @Resource
    private ContractConstitutionFileService contractConstitutionFileService;

    @Resource
    private MaterialsListService materialsListService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void add(Long contractId, List<ClientInfo> clientInfoList) {
        ContractTenantry contractTenantry;
        ClientInfo clientInfo;
        if (ObjectUtil.isEmpty(clientInfoList)) {
            return;
        }
        for (int i = 0; i < clientInfoList.size(); i++) {
            clientInfo = clientInfoList.get(i);
            SpringContextHolder.getBean(ClientService.class).checkClientOccupy(clientInfo.getClientId());
            contractTenantry = new ContractTenantry();
            if (i == 0) {
                contractTenantry.setLesseeType(MAIN_LESSSEE.name());
            } else {
                contractTenantry.setLesseeType(LesseeTypeEnum.JOINT_LESSEE.name());
            }
            contractTenantry.setContractId(contractId);
            contractTenantry.setLesseeId(clientInfo.getClientId());
            contractTenantry.setLesseeName(clientInfo.getClientName());
            contractTenantry.setIsReport(1);
            //风险敞口计算合同-非审批流不加本次金额
            contractTenantry.setStockRiskExposure(baseInfoService.getStockRiskExposure(contractTenantry.getLesseeId(), contractId, null));
            contractTenantryMapper.insert(contractTenantry);
        }
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractId, TradeStructureRoleEnum.LESSEE);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        contractId.toString().toString(),
                        "合同管理-新增承租人")
                )
        );
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void modify(ContractTenantryModifyREQ req) {
        if (ObjectUtil.isEmpty(req)) {
            return;
        }
        ContractTenantry info;
        ContractTenantry baseInfo;
        baseInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException("未查到此数据");
        }
        if(ResolutionTypeEnum.OTHER.name().equals(req.getResolutionType()) && CollectionUtils.isEmpty(req.getFileList()) && CollectionUtils.isEmpty(req.getFileListId())){
            throw new MithrasException("决议类型为其他时-决议文件不得为空");
        }
        // 上传文件
        List<Long> fileIdList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(req.getFileList())) {
            for (MultipartFile file : req.getFileList()) {
                Long fileId = materialsListService.add(file, baseInfo.getContractId(), ContractTypeEnum.RESOLUTION_FILE.name(), BusinessModuleEnum.CONTRACT.name());
                fileIdList.add(fileId);
            }
        }
        // 上传的文件id+前端传来的文件id
        fileIdList.addAll(Optional.ofNullable(req.getFileListId()).orElse(new ArrayList<>()));
        // 数据库现存的文件id
        List<Long> resolutionFileId = JSON.parseArray(Optional.ofNullable(baseInfo.getResolutionFileId()).orElse("[]"),Long.class);

        String files = JSON.toJSONString(fileIdList);
        baseInfo.setResolutionFileId(files);

        resolutionFileId.removeAll(fileIdList);
        if(CollectionUtils.isNotEmpty(resolutionFileId)) {
            materialsListService.remove(resolutionFileId);
        }
        //获取客户名称
        String rentConcatAccount = id2NameService.clientId2NameSingle(Long.valueOf(req.getRentConcatAccountId()));
        req.setRentConcatAccountName(rentConcatAccount);
        info = ContractTenantryConvert.merge(baseInfo, req);
        if (1 == info.getIsReport()) {
            contractService.checkZhongZhengCode(info.getLesseeId());
        }
        contractTenantryMapper.updateAnnotationIncludeNullById(info);
//        // 通知客户权限变更
//        ApplicationContextUtil.getApplicationContext().publishEvent(
//                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
//                        BusinessModuleEnum.CONTRACT,
//                        baseInfo.getContactId().toString(),
//                        "合同管理-修改承租人")
//                )
//        );

        //保存章程文件
        try {
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .tenantryId(req.getId())
                    .contractId(req.getContractId())
                    .fileType(ContractConstitutionFileTypeEnum.TENANT.name())
                    .constitutionFileIds(req.getConstitutionFileIds())
                    .multipartFileList(req.getMultipartFileList()).build();
            contractConstitutionFileService.saveConstitutionFiles(constitutionFileBO);
        } catch (Exception e) {
            log.error("保存章程文件异常", e);
            throw new MithrasException("保存章程文件异常");
        }

        if (!Objects.equals(req.getLesseeType(), baseInfo.getLesseeType())) {
            throw new MithrasException("承租人类型不能修改");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateStockRiskExposure(Long contractId) {
        ContractBaseInfo baseInfo = baseInfoService.getById(contractId);
        //更新主承租人的风险敞口
        ContractTenantry contractTenantry = baseMapper.selectOne(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getContractId, contractId)
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name()));
        if (ObjectUtil.isEmpty(contractTenantry)) {
            return;
        }
        //新建状态下更新风险敞口
        if (ContractStatus.NEW.name().equals(baseInfo.getContractStatus())) {
            //风险敞口计算合同-非审批流不加本次金额
            contractTenantry.setStockRiskExposure(baseInfoService.getStockRiskExposure(contractTenantry.getLesseeId(), contractId, null));
            baseMapper.updateById(contractTenantry);
        }
    }

    @Override
    public List<ContractTenantryListRSP> list(ContractIdListREQ req) {
        List<ContractTenantryListRSP> rsp = new ArrayList<>();
        ContractTenantryListRSP contractTenantryListRSP;
        // ContractTenantry 合同-承租人表  根据合同id
        List<ContractTenantry> contractTenantries = baseMapper.selectList(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getContractId, req.getContractId()));
        Set<Long> clientIds = new HashSet<>();
        Map<Long, ClientInfo> clientInfoMap = new HashMap<>();
        contractTenantries.forEach(rep -> clientIds.add(rep.getLesseeId()));
        // 合同基本信息表信息
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (ObjectUtil.isNotEmpty(clientIds)) {
            clientInfoMap = id2NameService.clientId2CLient(clientIds);
        }
        for (ContractTenantry contractTenantry : contractTenantries) {
            contractTenantryListRSP = BeanUtil.copyProperties(contractTenantry, ContractTenantryListRSP.class);
            if (ObjectUtil.isNotEmpty(contractTenantryListRSP.getContactId())) {
                CorpContactInfoLib corpContactInfoLib = contactInfoLibMapper.selectById(contractTenantryListRSP.getContactId());
                if (ObjectUtil.isNotEmpty(corpContactInfoLib)) {
                    contractTenantryListRSP.setContactName(corpContactInfoLib.getName());
                }
            }
            contractTenantryListRSP.setLesseeClient(clientInfoMap.get(contractTenantry.getLesseeId()));

            //获取章程文件： key - id , value - 章程文件名
            ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                    .tenantryId(req.getContractId()).contractId(req.getContractId())
                    .tenantryId(contractTenantry.getId())
                    .fileType(ContractConstitutionFileTypeEnum.TENANT.name()).build();
            List<Long> list = contractConstitutionFileService.getConstitutionFileList(constitutionFileBO);
            if (CollectionUtils.isNotEmpty(list)) {
                contractTenantryListRSP.setConstitutionFileList(list);
            }

            List<Long> fileList = JSON.parseArray(Optional.ofNullable(contractTenantry.getResolutionFileId()).orElse("[]"), Long.class);
            contractTenantryListRSP.setResolutionFileId(fileList);
            //非新建实时计算
            if (ObjectUtil.isNotNull(contractBaseInfo) && ContractStatus.NEW.name().equals(contractBaseInfo.getContractStatus())) {
                contractTenantryListRSP.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(contractTenantry.getLesseeId(), contractTenantry.getContractId(), null));
            }
            rsp.add(contractTenantryListRSP);
        }
        return rsp;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(ContractTenantryRemoveREQ req) {
        ContractTenantry originalInfo = contractTenantryMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        contractTenantryMapper.deleteById(req.getId());
        //删除章程文件
        ContractConstitutionFileBO constitutionFileBO = ContractConstitutionFileBO.builder()
                .contractId(req.getContractId())
                .tenantryId(originalInfo.getLesseeId())
                .fileType(ContractConstitutionFileTypeEnum.TENANT.name()).build();
        contractConstitutionFileService.deleteByFileIdAndContractId(constitutionFileBO);
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(originalInfo.getContractId(), TradeStructureRoleEnum.LESSEE);
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        originalInfo.getContactId().toString(),
                        "合同管理-删除承租人")
                )
        );
    }

    @Override
    public ContractTenantry getMain(Long contractId) {
        LambdaQueryWrapper<ContractTenantry> query = Wrappers.lambdaQuery();
        query.eq(ContractTenantry::getContractId, contractId);
        query.orderByAsc(ContractTenantry::getId);
        List<ContractTenantry> contractTenantryList = this.list(query);
        if (CollectionUtils.isEmpty(contractTenantryList)) {
            return null;
        }
        for (ContractTenantry contractTenantry : contractTenantryList) {
            if (Objects.equals(MAIN_LESSSEE.name(), contractTenantry.getLesseeType())) {
                return contractTenantry;
            }
            if (Objects.equals(CreditorDebtorTypeEnum.CREDITOR.name(), contractTenantry.getLesseeType())) {
                return contractTenantry;
            }
        }
        return null;
    }

    @Override
    public List<ContractTenantry> listByContractId(Long contractId) {
        LambdaQueryWrapper<ContractTenantry> query = Wrappers.lambdaQuery();
        query.eq(ContractTenantry::getContractId, contractId);
        return this.list(query);
    }

    @Override
    public List<ContractTenantry> listByClientIds(List<Long> clientIds) {
        LambdaQueryWrapper<ContractTenantry> query = Wrappers.lambdaQuery();
        query.in(ContractTenantry::getLesseeId, clientIds);
        query.last(StringUtil.mysqlLimit(0, 1000));
        return this.list(query);
    }

    @Override
    public List<ContractTenantry> listByContractIds(Collection<Long> contractIds) {
        LambdaQueryWrapper<ContractTenantry> query = Wrappers.lambdaQuery();
        query.in(ContractTenantry::getContractId, contractIds);
        return this.list(query);
    }

    @Override
    public Map<Long, String> listRentConcatAccountByContractId(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return MapUtil.empty();
        }
        List<ContractTenantry> contractTenantries = baseMapper.selectList(Wrappers.<ContractTenantry>lambdaQuery()
                .in(ContractTenantry::getContractId, contractIds)
                .eq(ContractTenantry::getLesseeType, MAIN_LESSSEE.name()));
        if (ObjectUtil.isEmpty(contractTenantries)) {
            return MapUtil.empty();
        }
        return contractTenantries.stream().filter(e -> ObjectUtil.isNotEmpty(e.getRentConcatAccountId())).collect(Collectors.toMap(ContractTenantry::getContractId, ContractTenantry::getRentConcatAccountId, (a, b) -> b));
    }

    @Override
    public boolean existSpecificClient(Long clientId, Long contractId) {
        List<ContractTenantry> contractTenantryList = this.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractTenantryList)) {
            return false;
        }
        for (ContractTenantry contractTenantry : contractTenantryList) {
            if (Objects.equals(contractTenantry.getLesseeId(), clientId)) {
                return true;
            }
        }
        return false;
    }
}
