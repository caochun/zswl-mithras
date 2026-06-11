package cn.zswltech.mithras.contract.core.impl;

import cn.zswltech.mithras.contract.core.ContractConstitutionFileService;
import cn.zswltech.mithras.contract.core.ContractMaterialsPort;
import cn.zswltech.mithras.contract.enums.contract.ContractConstitutionFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractConstitutionFileMapper;
import cn.zswltech.mithras.contract.model.contract.ContractConstitutionFile;
import cn.zswltech.mithras.projectprocess.application.bo.ContractConstitutionFileBO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yupengfei
 * @date 2024/4/18 16:08
 */
@Service
public class ContractConstitutionFileServiceImpl extends ServiceImpl<ContractConstitutionFileMapper, ContractConstitutionFile> implements ContractConstitutionFileService {

    private static final String BUSINESS_MODULE_CONTRACT = "CONTRACT";

    @Resource
    private ContractMaterialsPort contractMaterialsPort;

    @Override
    public void saveConstitutionFiles(ContractConstitutionFileBO constitutionFileBO) throws IOException {
        this.deleteByFileIdAndContractId(constitutionFileBO);

        List<ContractConstitutionFile> constitutionFileList = new ArrayList<>();
        if (Objects.isNull(constitutionFileBO.getMultipartFileList())) {
            return;
        }

        for (MultipartFile multipartFile : constitutionFileBO.getMultipartFileList()) {
            String materialsType = null;
            if (constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.GUARANTOR.name())
                    || constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.TENANT.name())) {
                materialsType = ContractTypeEnum.RESOLUTION_FILE.name();
            } else if (constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.PLEDGE.name())
                    || constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.MORTGAGE.name())) {
                materialsType = ContractTypeEnum.MORTGAGE_PLEDGE_FILE.name();
            }
            Long fileId = contractMaterialsPort.add(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), constitutionFileBO.getContractId(),
                    materialsType, null, BUSINESS_MODULE_CONTRACT);

            ContractConstitutionFile constitutionFile = ContractConstitutionFile.builder()
                    .contractId(constitutionFileBO.getContractId())
                    .tenantryId(constitutionFileBO.getTenantryId())
                    .fileType(constitutionFileBO.getFileType())
                    .materialsListId(fileId)
                    .build();
            constitutionFile.setCreateTime(LocalDateTime.now());
            constitutionFile.setUpdateTime(LocalDateTime.now());
            constitutionFileList.add(constitutionFile);
        }
        this.saveBatch(constitutionFileList);
    }

    @Override
    public List<Long> getConstitutionFileList(ContractConstitutionFileBO constitutionFileBO) {
        Wrapper<ContractConstitutionFile> queryWrapper = Wrappers.<ContractConstitutionFile>lambdaQuery()
                .eq(ContractConstitutionFile::getContractId, constitutionFileBO.getContractId())
                .eq(ContractConstitutionFile::getTenantryId, constitutionFileBO.getTenantryId())
                .eq(ContractConstitutionFile::getFileType, constitutionFileBO.getFileType());
        List<ContractConstitutionFile> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(ContractConstitutionFile::getMaterialsListId).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void deleteByFileIdAndContractId(ContractConstitutionFileBO constitutionFileBO) {
        Wrapper<ContractConstitutionFile> queryWrapper = Wrappers.<ContractConstitutionFile>lambdaQuery()
                .eq(ContractConstitutionFile::getContractId, constitutionFileBO.getContractId())
                .eq(ContractConstitutionFile::getTenantryId, constitutionFileBO.getTenantryId())
                .eq(ContractConstitutionFile::getFileType, constitutionFileBO.getFileType());
        List<ContractConstitutionFile> constitutionFiles = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(constitutionFiles)) {
            return;
        }
        List<Long> materialsListIds = constitutionFiles.stream().map(ContractConstitutionFile::getMaterialsListId).collect(Collectors.toList());
        List<Long> deleteContractConstitutionFileIds;
        if (CollectionUtils.isNotEmpty(constitutionFileBO.getConstitutionFileIds())) {
            deleteContractConstitutionFileIds = materialsListIds.stream()
                    .filter(item -> !constitutionFileBO.getConstitutionFileIds().contains(item))
                    .collect(Collectors.toList());
        } else {
            deleteContractConstitutionFileIds = materialsListIds;
        }
        if (CollectionUtils.isEmpty(deleteContractConstitutionFileIds)) {
            return;
        }
        contractMaterialsPort.remove(deleteContractConstitutionFileIds);

        queryWrapper = Wrappers.<ContractConstitutionFile>lambdaQuery()
                .eq(ContractConstitutionFile::getContractId, constitutionFileBO.getContractId())
                .eq(ContractConstitutionFile::getTenantryId, constitutionFileBO.getTenantryId())
                .eq(ContractConstitutionFile::getFileType, constitutionFileBO.getFileType())
                .in(ContractConstitutionFile::getMaterialsListId, deleteContractConstitutionFileIds);
        this.remove(queryWrapper);
    }
}
