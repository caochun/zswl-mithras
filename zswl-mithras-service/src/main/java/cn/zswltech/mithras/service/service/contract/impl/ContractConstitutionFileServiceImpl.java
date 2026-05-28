package cn.zswltech.mithras.service.service.contract.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.contract.ContractConstitutionFileTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractConstitutionFileMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractConstitutionFile;
import cn.zswltech.mithras.service.service.bo.ContractConstitutionFileBO;
import cn.zswltech.mithras.service.service.contract.ContractConstitutionFileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
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

    @Resource
    private MaterialsListService materialsListService;

    @Override
    public void saveConstitutionFiles(ContractConstitutionFileBO constitutionFileBO) throws IOException {

        //删除章程文件旧数据
        this.deleteByFileIdAndContractId(constitutionFileBO);

        List<ContractConstitutionFile> constitutionFileList = new ArrayList<>();
        if (Objects.isNull(constitutionFileBO.getMultipartFileList())) {
            return;
        }

        //保存文件
        for (MultipartFile multipartFile : constitutionFileBO.getMultipartFileList()) {
            String materialsType = null;
            if (constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.GUARANTOR.name())
                    || constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.TENANT.name())) {
                materialsType = ContractTypeEnum.RESOLUTION_FILE.name();
            } else if (constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.PLEDGE.name())
                    || constitutionFileBO.getFileType().equalsIgnoreCase(ContractConstitutionFileTypeEnum.MORTGAGE.name())) {
                materialsType = ContractTypeEnum.MORTGAGE_PLEDGE_FILE.name();
            }
            Long fileId = materialsListService.add(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), constitutionFileBO.getContractId()
                    , materialsType, null, BusinessModuleEnum.CONTRACT.name());

            ContractConstitutionFile constitutionFile = ContractConstitutionFile.builder()
                    .contractId(constitutionFileBO.getContractId())
                    .tenantryId(constitutionFileBO.getTenantryId())
                    .fileType(constitutionFileBO.getFileType())
                    .materialsListId(fileId).build();
            constitutionFile.setCreateTime(LocalDateTime.now());
            constitutionFile.setUpdateTime(LocalDateTime.now());
            constitutionFileList.add(constitutionFile);
        }
        //保存章程文件
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
            //获取章程文件
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
        //先查询出来章程文件的id,删除 materials_list 表中记录的文件
        List<ContractConstitutionFile> constitutionFiles = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(constitutionFiles)) {
            return;
        }
        List<Long> materialsListIds = constitutionFiles.stream().map(ContractConstitutionFile::getMaterialsListId).collect(Collectors.toList());
        List<Long> deleteContractConstitutionFileIds;
        if (CollectionUtils.isNotEmpty(constitutionFileBO.getConstitutionFileIds())) {
            //比较数据库的章程文件id,删除章程文件
            deleteContractConstitutionFileIds = materialsListIds.stream().filter(item -> !constitutionFileBO.getConstitutionFileIds().contains(item)).collect(Collectors.toList());
        } else {
            //为空，删除全部
            deleteContractConstitutionFileIds = materialsListIds;
        }
        //为空直接打回，表示数据未修改
        if (CollectionUtils.isEmpty(deleteContractConstitutionFileIds)) {
            return;
        }
        materialsListService.remove(deleteContractConstitutionFileIds);

        //再删除章程表中的数据
        queryWrapper = Wrappers.<ContractConstitutionFile>lambdaQuery()
                .eq(ContractConstitutionFile::getContractId, constitutionFileBO.getContractId())
                .eq(ContractConstitutionFile::getTenantryId, constitutionFileBO.getTenantryId())
                .eq(ContractConstitutionFile::getFileType, constitutionFileBO.getFileType())
                .in(ContractConstitutionFile::getMaterialsListId, deleteContractConstitutionFileIds);
        this.remove(queryWrapper);
    }
}
