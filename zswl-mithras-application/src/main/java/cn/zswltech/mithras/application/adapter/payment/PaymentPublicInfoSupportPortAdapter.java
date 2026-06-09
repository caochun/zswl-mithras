package cn.zswltech.mithras.application.adapter.payment;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.payment.application.pubinfo.PaymentPublicInfoSupportPort;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.PublicInfoFileTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentPublicInfoSupportPortAdapter implements PaymentPublicInfoSupportPort {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FileService fileService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Override
    public Map<Long, String> clientId2Name(Collection<Long> clientIds) {
        return id2NameService.clientId2Name(clientIds);
    }

    @Override
    public Map<Long, String> sysUserId2Name(Collection<Long> userIds) {
        return id2NameService.sysUserId2Name(userIds);
    }

    @Override
    public List<UserDO> getUserByDeptCode(String orgCode) {
        return sysUserService.getUserByDeptCode(orgCode);
    }

    @Override
    public List<MaterialsList> listMaterials(Collection<Long> belongIds) {
        if (CollUtil.isEmpty(belongIds)) {
            return Collections.emptyList();
        }
        return materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BUSINESS_TYPE_PUBLIC_INFO)
                .in(MaterialsList::getBelongId, belongIds));
    }

    @Override
    public List<MaterialsList> listMaterials(Collection<Long> belongIds, Collection<PublicInfoFileTypeEnum> fileTypes) {
        if (CollUtil.isEmpty(belongIds)) {
            return Collections.emptyList();
        }
        return materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BUSINESS_TYPE_PUBLIC_INFO)
                .in(MaterialsList::getBelongId, belongIds)
                .in(CollUtil.isNotEmpty(fileTypes), MaterialsList::getMaterialSubType,
                        fileTypes.stream().map(PublicInfoFileTypeEnum::name).collect(Collectors.toList())));
    }

    @Override
    public FileListRSP toFileListRSP(MaterialsList materialsList) {
        FileListRSP fileListRSP = new FileListRSP();
        fileService.fillFiledValue(materialsList, fileListRSP);
        return fileListRSP;
    }

    @Override
    public Long addMaterial(InputStream inputStream, String fileName, Long belongId, String materialsType, String materialsSubType, YesOrNoNumberEnum systemGenerate) throws IOException {
        return materialsListService.add(inputStream, fileName, belongId, materialsType, materialsSubType, BUSINESS_TYPE_PUBLIC_INFO, systemGenerate);
    }

    @Override
    public List<ContractBaseInfo> listContractsByProjReviewId(Long projReviewId) {
        return contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getProjReviewId, projReviewId));
    }
}
