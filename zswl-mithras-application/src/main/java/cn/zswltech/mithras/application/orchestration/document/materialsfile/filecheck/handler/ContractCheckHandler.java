package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.payment.enums.app.AppContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTextTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.payment.enums.LendingMaterialType;
import cn.zswltech.mithras.customer.mapper.app.AppContractSignMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.customer.model.app.AppContractSign;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.contract.ContractTextInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.*;


/**
 * @ClassName ContractCheckHandler
 * 合同文件检查
 * @Author jackerhe
 * @Version 1.0
 **/
@Component
public class ContractCheckHandler extends FileModuleCheck {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private AppContractSignMapper contractSignMapper;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.CONTRACT.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            this.checkByProjManager(mainId, materialsType);
        }
        super.checkUpload(moduleKey, mainId, materialsType);
    }

    public void appCheckUpload(String moduleKey, Long mainId, String materialsType, Long createdBy) {
        if (sysUserService.currentUserIsSpecificJob(createdBy, JobEnum.projmanager.name())) {
            this.checkByProjManager(mainId, materialsType);
        }
        super.appCheckUpload(moduleKey, mainId, materialsType, createdBy);
    }

    @Override
    public void afterUploadHandle(String moduleType, Long mainId, Long fileId, String sourceBusinessKey, Long userId) {
        if (BusinessModuleEnum.CONTRACT.name().equalsIgnoreCase(moduleType)
                && sourceBusinessKey.contains("APP_CONTRACT")) {
            MaterialsList materialsList = materialsListService.getById(fileId);
            AppContractSign appContractSign = new AppContractSign();
            appContractSign.setContractId(mainId);
            appContractSign.setFileId(fileId);
            if (userId != null) {
                appContractSign.setUserId(userId);
                contractSignMapper.insert(appContractSign);
            }
        }
    }

    @Override
    public String beforeUploadHandle(String originalFilename, String moduleType, String materialsType, String materialsSubType, Long mainId, String sourceBusinessKey, Long userId) {
        if (BusinessModuleEnum.CONTRACT.name().equalsIgnoreCase(moduleType)
                && sourceBusinessKey.contains("APP_CONTRACT")) {
            if (StringUtils.isBlank(originalFilename)) {
                throw new MithrasException("文件名为空");
            }
            StringBuilder sBuilder = new StringBuilder();
            String filePrefixName = getFilePrefixName(originalFilename);
            String fileSuffixName = getFileSuffixName(originalFilename);
            String clientName = id2NameService.clientId2NameSingle(userId);
            String middle = null;
            if (LendingMaterialType.SIGN_PHOTO_VIDEO.name().equalsIgnoreCase(materialsType)
                    && AppContractSubTypeEnum.SIGN_LOCATION_PHOTO.name().equalsIgnoreCase(materialsSubType)) {
                middle = AppContractSubTypeEnum.SIGN_LOCATION_PHOTO.display();
            } else if (LendingMaterialType.SIGN_PHOTO_VIDEO.name().equalsIgnoreCase(materialsType)
                    && AppContractSubTypeEnum.SIGN_VIDEO.name().equalsIgnoreCase(materialsSubType)) {
                middle = AppContractSubTypeEnum.SIGN_VIDEO.display();
            } else if (LendingMaterialType.LEASE_RELATED.name().equalsIgnoreCase(materialsType)
                    && AppContractSubTypeEnum.MAN_MACHINE_PHOTO.name().equalsIgnoreCase(materialsSubType)) {
                middle = AppContractSubTypeEnum.MAN_MACHINE_PHOTO.display();
            } else if (LendingMaterialType.LEASE_RELATED.name().equalsIgnoreCase(materialsType)
                    && AppContractSubTypeEnum.DEVICE_PHOTO.name().equalsIgnoreCase(materialsSubType)) {
                middle = AppContractSubTypeEnum.DEVICE_PHOTO.display();
            }
            if (LendingMaterialType.SIGN_PHOTO_VIDEO.name().equalsIgnoreCase(materialsType)) {
                sBuilder.append(clientName).append("-").append(middle).append("-").append(filePrefixName).append(fileSuffixName);
            } else if (LendingMaterialType.LEASE_RELATED.name().equalsIgnoreCase(materialsType)) {
                sBuilder.append(middle).append("-").append(filePrefixName).append(fileSuffixName);
            }
            return sBuilder.toString();
        }
        return null;
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            this.checkRemoveByProjManager(Collections.singletonList(materialsList));
        }
        // 判断当前登陆用户是否法务经理
        boolean isLegalManager = sysUserService.currentUserIsSpecificJob(JobEnum.legalmanager.name());
        if (isLegalManager) {
            this.check(materialsList);
        } else {
            super.checkRemove(moduleKey, fileId);
        }
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        List<MaterialsList> materialsListList = materialsListService.listByIds(fileIds);
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            this.checkRemoveByProjManager(materialsListList);
        }
        // 判断当前登陆用户是否法务经理
        boolean isLegalManager = sysUserService.currentUserIsSpecificJob(JobEnum.legalmanager.name());
        if (isLegalManager) {
            // 单独判断是否法务经理节点
            for (MaterialsList materialsList : materialsListList) {
                this.check(materialsList);
            }
        } else {
            super.checkRemove(moduleKey, fileIds);
        }
    }

    @Override
    public void afterRemoveHandle(Long mainId, List<MaterialsList> fileList, Long userId) {
        if (fileList.isEmpty()) {
            return;
        }
//        List<MaterialsList> materialsListList = materialsListService.listByIds(fileIds);
        if (userId != null) {
            for (MaterialsList materialsList : fileList) {
                if (materialsList.getMainId().equals(mainId) && materialsList.getSourceBusinessKey().contains("APP_CONTRACT")) {
                    List<AppContractSign> appContractSignList = contractSignMapper.selectList(Wrappers.<AppContractSign>lambdaQuery()
                            .eq(AppContractSign::getContractId, mainId)
                            .eq(AppContractSign::getFileId, materialsList.getId())
                            .eq(AppContractSign::getUserId, userId)
                            .eq(AppContractSign::getDeleted, 0));
                    if (!appContractSignList.isEmpty()) {
                        for (AppContractSign appContractSign : appContractSignList) {
                            appContractSign.setDeleted(1);
                            contractSignMapper.updateById(appContractSign);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkDownload(String moduleKey, Long mianId, List<Long> fileId) {
        //下载权限同查看暂不限制
        super.checkDownload(moduleKey, mianId, fileId);
    }

    private void check(MaterialsList materialsList) {
        Assert.isTrue(Objects.equals(materialsList.getSystemGenerate(), YesOrNoNumberEnum.YES.getCode()) && Objects.equals(materialsList.getIsEdit(), YesOrNoNumberEnum.NO.getCode()), () -> MithrasException.newException("非系统生成文件，不允许删除"));
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name()));
        processPageReq.setBusinessKey(materialsList.getBelongId().toString());
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        Assert.isTrue(Objects.nonNull(processRespPage) && processRespPage.getTotal() > 0, () -> MithrasException.newException("存在当前状态不允许删除的文件，请检查"));
        ProcessResp processResp = processRespPage.getContents().get(0);
        Assert.isTrue(Objects.equals(processResp.getCurTaskActivityIds(), "userTask_lawManager"), () -> MithrasException.newException("非法务确认节点不允许删除"));
    }

    public void checkByProjManager(Long contractId, String materialType) {
        if (!CharSequenceUtil.equalsAny(materialType, ContractTypeEnum.MAIN_CONTRACT.name(), ContractTypeEnum.CONSULTING_CONTRACT.name(), ContractTypeEnum.GUARANTEE_CONTRACT.name(), ContractTypeEnum.MORTGAGE_CONTRACT.name())) {
            return;
        }
        // 获取合同文本类型
        ContractTextInfo contractTextInfo = SpringUtil.getBean(ContractTextInfoService.class).getOneByContractId(contractId);
        if (Objects.nonNull(contractTextInfo) && StrUtil.isNotBlank(contractTextInfo.getTextType())) {
            List<String> types = ListUtil.of(contractTextInfo.getTextType().split(","));
            if (types.contains(ContractTextTypeEnum.STANDARD_TEXT.name())) {
                throw new MithrasException("合同文本类型选择为“标准合同文本”，主合同、咨询合同、保证合同、抵押合同不再允许删除和上传");
            }
        }
    }

    private void checkRemoveByProjManager(List<MaterialsList> materialsListList) {
        for (MaterialsList materialsList : materialsListList) {
            this.checkByProjManager(materialsList.getBelongId(), materialsList.getMaterialsType());
        }
    }

    private String getFilePrefixName(String fileName) {
        String[] parts = fileName.split("/");
        String name = parts[parts.length-1];
        int dotIndex = name.lastIndexOf(".");
        String prefix = dotIndex == -1 ? name : name.substring(0, dotIndex);
        return prefix;
    }

    private String getFileSuffixName(String fileName) {
        if(fileName.lastIndexOf(".")==-1){
            throw new MithrasException("文件没有后缀名");
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
