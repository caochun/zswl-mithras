package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveMainAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.*;
import cn.zswltech.mithras.service.gendoc.render.ContractFileSignBillRender;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.file.ContractFileService;
import cn.zswltech.mithras.service.service.contract.file.ContractGeneratorFactory;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.handler.ContractCheckHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/17
 * @description
 */
@Slf4j
@Service
public class ContractFileServiceImpl implements ContractFileService {


    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseRemoveMainAuthChecker contractBaseRemoveMainAuthChecker;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FileService fileService;

    @Override
    public List<MaterialsList> listExchangeMaterial(Long contractId) {
        return materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(ContractChangeMaterialEnum.EXCHANGE_MATERIAL.name()), Collections.singletonList(contractId));
    }

    @Override
    public Map<String, List<MaterialsList>> getContractFileMap(Long contractId) {
        String bizType = BusinessModuleEnum.CONTRACT.name();
        List<String> materialsTypeList = Arrays.asList(
                ContractTypeEnum.MAIN_CONTRACT.name(),
                ContractTypeEnum.CONSULTING_CONTRACT.name(),
                ContractTypeEnum.GUARANTEE_CONTRACT.name(),
                ContractTypeEnum.MORTGAGE_CONTRACT.name(),
                ContractTypeEnum.PLEDGE_CONTRACT.name(),
                ContractTypeEnum.OTHER_CONTRACT.name()
        );
        List<Long> belongIds = Collections.singletonList(contractId);
        List<MaterialsList> list = materialsListService.list(bizType, materialsTypeList, belongIds);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        // 分组返回
        return list.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Long upload(MultipartFile file, String contractType, Long contractId) {
        // 上传校验
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            SpringUtil.getBean(ContractCheckHandler.class).checkByProjManager(contractId, contractType);
        }
        Long add = materialsListService.add(file, contractId, contractType, BusinessModuleEnum.CONTRACT.name());
        // 变更合同状态
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (!Objects.equals(ContractStatus.NEW.name(), contractBaseInfo.getContractStatus())) {
            // 只要合同状态不是新建，就说明是在合同变更场景下保存数据，需要变更合同流程状态
            // 这里需要兼容运营管理经办人，不需要变更合同流程状态
            // 如果当前审批人是运营管理经办是可以修改的
            AccountVO loginInfo = AccountUtil.getLoginInfo();
            Map<Long, List<String>> allOperateUserJob = fileService.getAllOperateUserJob(JobEnum.yunYingGuanLi);
            if (allOperateUserJob.containsKey(loginInfo.getId())) {
                return add;
            }
            contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.OTHER.name(), contractId);
        }
        return add;
    }

    @Override
    public void generate(ContractSingleIdREQ contractSingleIdREQ) throws Exception {
        Long contractId = contractSingleIdREQ.getContractId();
        List<String> generateContractTypeList = Optional.ofNullable(contractSingleIdREQ.getGenerateContractType()).orElse(ContractTypeEnum.allName());
        // 删除历史合同（只删除自动生成的）
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), generateContractTypeList, Collections.singletonList(contractId));
        if (!CollectionUtils.isEmpty(materialsListList)) {
            List<Long> ids = materialsListList.stream()
                    .filter(item -> Objects.equals(item.getSystemGenerate(), YesOrNoNumberEnum.YES.getCode()) &&
                            Objects.equals(item.getIsEdit(), YesOrNoNumberEnum.NO.getCode()))
                    .map(MaterialsList::getId)
                    .collect(Collectors.toList());
            materialsListService.removeByIds(ids);
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        // 变更合同状态
        if (!Objects.equals(ContractStatus.NEW.name(), contractBaseInfo.getContractStatus())) {
            // 只要合同状态不是新建，就说明是在合同变更-其他场景下保存数据，需要变更合同流程状态
            contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.OTHER.name(), contractId);
        }
        contractBaseInfo.setGenerateContractTypeList(generateContractTypeList);
        ContractGeneratorFactory.getInstance(contractBaseInfo).generate(contractBaseInfo);
        // 生成文件签收单（需放到最后，因为要获取各个分类下的文件数量）
        this.generateFileSignBill(contractBaseInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void removeContractFile(Long fileId) {
        MaterialsList materialsList = Assert.notNull(materialsListService.getById(fileId), () -> MithrasException.newException("文件不存在"));
        contractBaseRemoveMainAuthChecker.check(BusinessModuleEnum.CONTRACT, ContractBaseInfoMapper.class, materialsList.getBelongId(), null);
        materialsListService.remove(Collections.singletonList(fileId));
        Long contractId = materialsList.getBelongId();
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        // 变更合同状态
        if (!Objects.equals(ContractStatus.NEW.name(), contractBaseInfo.getContractStatus())) {
            // 只要合同状态不是新建，就说明是在合同变更-其他场景下保存数据，需要变更合同流程状态
            contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), ContractChangeTypeEnum.OTHER.name(), contractId);
        }
    }

    private void generateFileSignBill(ContractBaseInfo contractBaseInfo) {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = SpringUtil.getBean(ContractFileSignBillRender.class).render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.OTHER_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (Exception e) {
            log.error("生成文件签收单发生未知异常[{}]", JSONUtil.toJsonStr(contractBaseInfo), e);
        } finally {
            if (!Objects.isNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("生成文件签收单-关闭输入流异常[{}]", JSONUtil.toJsonStr(contractBaseInfo), e);
                }
            }
            if (!Objects.isNull(os)) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("生成文件签收单-关闭输出流异常[{}]", JSONUtil.toJsonStr(contractBaseInfo), e);
                }
            }
        }
    }
}
