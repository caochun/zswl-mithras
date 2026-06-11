package cn.zswltech.mithras.application.orchestration.document.materialsfile;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.file.*;
import cn.zswltech.mithras.dto.file.ext.FileListREQProjReviewExt;
import cn.zswltech.mithras.dto.file.ext.FileListVersionREQ;
import cn.zswltech.mithras.dto.version.DiffFile;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.application.orchestration.metadata.enumscan.MaterialsTypeFactory;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.InfoOperation;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.document.convert.FileConvert;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.contract.enums.contract.ContractFileQueryType;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.document.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.document.enums.materialslist.FileTemplateEnum;
import cn.zswltech.mithras.payment.enums.pubinfo.PublicInfoFileTypeEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.application.orchestration.document.file.FileListProviderFactory;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.model.MaterialsListLib;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractConstitutionFile;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractConstitutionFileService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.document.materialsfile.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.batchdownload.AbstractFileBatchDownload;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.foundation.util.CompareUtil;
import cn.zswltech.mithras.third.util.WatermarkUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.dto.MaterialsListIdType.VERSIONED;

/**
 * @ClassName FileService
 * @Author jackerhe
 * @Date 2022/11/20 9:57 上午
 * @Version 1.0
 **/
@Service
@Slf4j
public class FileService {

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListLibService materialsListLibService;
    @Resource
    private List<FileModuleCheck> fileModuleChecks;
    @Resource
    private List<AbstractFileBatchDownload> fileBatchDownloads;
    @Resource
    private FileListProviderFactory fileListProviderFactory;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FileConvert fileConvert;
    @Resource
    private OssClient ossClient;
    @Resource
    private UserService userService;
    @Resource
    private MaterialsListLibHandlerProxy materialsListLibHandlerProxy;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractConstitutionFileService contractConstitutionFileService;

    @Value("${oss.minio.base-path:mithras}")
    private String basePath;


    @Transactional(rollbackFor = Throwable.class)
    public FileUploadRSP upload(FileUploadREQ fileUploadREQ) {
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(fileUploadREQ.getModuleType());
        //自定义权限校验
        if (fileCheck != null) {
            if (fileUploadREQ.getCreatedBy() != null) {
                fileCheck.appCheckUpload(fileUploadREQ.getModuleType(), fileUploadREQ.getMainId(), fileUploadREQ.getMaterialsType(), fileUploadREQ.getCreatedBy());
            } else {
                fileCheck.checkUpload(fileUploadREQ.getModuleType(), fileUploadREQ.getMainId(), fileUploadREQ.getMaterialsType());
            }
        }
        try {
            InputStream inputStream = fileUploadREQ.getFile().getInputStream();
            if (YesOrNoNumberEnum.YES.getCode().equals(fileUploadREQ.getNeedWatermark())) {
                try {
                    assert fileCheck != null;
                    Pair<String, InputStream> watermark = WatermarkUtil.watermark(inputStream, fileCheck.getWatermarkSting(), fileUploadREQ.getFile().getOriginalFilename());
                    if (watermark != null) {
//                        throw new MithrasException("生成水印失败, 未知文件类型或无文本内容");
                        inputStream = watermark.getValue();
                    }
                } catch (Exception e) {
                    log.error("文件加水印失败", e);
//                   throw new MithrasException("文件加水印失败");
                }
            }
            String originalFilename = fileUploadREQ.getFile().getOriginalFilename();
            if (fileCheck != null) {
                String fileName = fileCheck.beforeUploadHandle(fileUploadREQ.getFile().getOriginalFilename(), fileUploadREQ.getModuleType(), fileUploadREQ.getMaterialsType(),
                        fileUploadREQ.getMaterialsSubType(), fileUploadREQ.getMainId(), fileUploadREQ.getSourceBusinessKey(), fileUploadREQ.getUserId());
                if (StringUtils.isNotBlank(fileName)) {
                    originalFilename = fileName;
                }
            }
//            Long fileId = materialsListService.add(inputStream, fileUploadREQ.getFile().getOriginalFilename(), fileUploadREQ.getMainId()
//                    , fileUploadREQ.getMaterialsType(), fileUploadREQ.getMaterialsSubType(), fileUploadREQ.getModuleType());
            Long fileId = materialsListService.add(
                    inputStream,
                    originalFilename,
                    fileUploadREQ.getMainId(),
                    fileUploadREQ.getMaterialsType(),
                    fileUploadREQ.getMaterialsSubType(),
                    fileUploadREQ.getModuleType(),
                    YesOrNoNumberEnum.NO,
                    fileUploadREQ.getSourceBusinessKey(),
                    fileUploadREQ.getLocation(),
                    fileUploadREQ.getCreatedBy()
            );
            if (fileCheck != null) {
                fileCheck.afterUploadHandle(fileUploadREQ.getModuleType(), fileUploadREQ.getMainId(), fileId, fileUploadREQ.getSourceBusinessKey(), fileUploadREQ.getUserId());
            }
            FileUploadRSP rsp = new FileUploadRSP();
            FileDownLoadRSP download = materialsListService.download(fileId);
            rsp.setFileUrl(download.getFileUrl());
            rsp.setFileId(fileId);
            rsp.setMainId(fileUploadREQ.getMainId());
            rsp.setModuleType(fileUploadREQ.getModuleType());
            rsp.setMaterialsType(fileUploadREQ.getMaterialsType());
            rsp.setMaterialsSubType(fileUploadREQ.getMaterialsSubType());
            return rsp;
        } catch (IOException e) {
            log.warn("FileService upload error ", e);
            throw new MithrasException("上传文件异常");
        }
    }

    //获取预上传地址
    public FileUploadPresignedRSP presignedPut(FileUploadPresignedREQ req) {
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        //自定义权限校验
        if (fileCheck != null) {
            fileCheck.checkUpload(req.getModuleType(), req.getMainId(), req.getMaterialsType());
        }
        //拼接fileName;
        Map<String, String> name2Path = new HashMap<>();
        Map<String, URL> rspMap = new HashMap<>();
        String path;
        for (String name : req.getFileNames()) {
            path = materialsListService.buildFilePath(FileNameUtil.getPrefix(name), FileNameUtil.getSuffix(name), req.getMainId(),
                    req.getMaterialsType(), req.getMaterialsSubType(), req.getModuleType());
            name2Path.put(path, name);
        }
        Map<String, URL> stringURLMap = ObjectUtil.isNull(req.getExpireMs()) ? materialsListService.presignedPut(new ArrayList<>(name2Path.keySet())) : materialsListService.presignedPut(new ArrayList<>(name2Path.keySet()), req.getExpireMs());
        for (String key : stringURLMap.keySet()) {
            rspMap.put(name2Path.get(key), stringURLMap.get(key));
        }
        //保存文件信息
       /* List<MaterialsList> materialsLists = new ArrayList<>();
        rspMap.forEach((key, value) -> {
            MaterialsList o = new MaterialsList();
            o.setFilename(key);
            o.setBelongId(req.getMainId());
            o.setSuffix(FileNameUtil.getSuffix(key));
            o.setOssFilename();
            o.setFilePath(ossInfo.getPath());
            o.setMaterialsType(materialsType);
            if (!org.springframework.util.StringUtils.isEmpty(materialsSubType)) {
                o.setMaterialSubType(materialsSubType);
            }
            o.setBusinessType(businessType);
            o.setSystemGenerate(systemGenerate.getCode());
        });

        materialsListMapper.insert(o);*/
        return new FileUploadPresignedRSP(rspMap);

    }

    //保存上传信息
    public void uploadRecord(FileUploadRecordREQ req) {
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        //自定义权限校验
        if (fileCheck != null) {
            fileCheck.checkUpload(req.getModuleType(), req.getMainId(), req.getMaterialsType());
        }
        //保存文件信息
        List<MaterialsList> materialsLists = new ArrayList<>();
        req.getFileNames().forEach(fileName -> {
            MaterialsList o = new MaterialsList();
            o.setBelongId(req.getMainId());
            o.setBusinessType(req.getModuleType());
            o.setMaterialsType(req.getMaterialsType());
            o.setMaterialSubType(req.getMaterialsSubType());
            o.setOssFilename(fileName);
            o.setFilename(FileNameUtil.getName(fileName));
            o.setSuffix(FileNameUtil.getSuffix(fileName));
            o.setFilePath(String.format("/%s/%s", basePath, fileName.substring(0, fileName.lastIndexOf("/"))));
            o.setSystemGenerate(YesOrNoNumberEnum.NO.getCode());
            materialsLists.add(o);
        });
        materialsListService.saveBatch(materialsLists);
    }

    public PageR<FileListRSP> list(FileListREQ req) {//各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        if (fileCheck != null) {
            fileCheck.checkList(req.getModuleType(), req.getMainId());
        }
        AbstractFileListProvider fileListProvider = fileListProviderFactory.getProvider(req.getModuleType());
        if (Objects.isNull(fileListProvider)) {
            throw new MithrasException(ResultMsg.UNSUPPORT_TYPE);
        }
        return fileListProvider.list(req);
    }

    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        if (fileCheck != null) {
            fileCheck.checkList(req.getModuleType(), req.getMainId());
        }
        AbstractFileListProvider fileListProvider = fileListProviderFactory.getProvider(req.getModuleType());
        if (Objects.isNull(fileListProvider)) {
            throw new MithrasException(ResultMsg.UNSUPPORT_TYPE);
        }
        LinkedHashMap<String, List<FileListRSP>> map = new LinkedHashMap<>();
        List<Pair<String, List<FileListRSP>>> res = fileListProvider.listGroup(req);
        if (res != null && !res.isEmpty()) {
            for (Pair<String, List<FileListRSP>> temp : res) {
                if (map.containsKey(temp.getKey())) {
                    map.get(temp.getKey()).addAll(temp.getValue());
                } else {
                    map.put(temp.getKey(), temp.getValue());
                }
            }
        }
        if (fileCheck != null) {
            List<Pair<String, List<FileListRSP>>> pairList = fileCheck.afterList(req.getMainId(), req.getModuleType(), req);
            if (pairList != null && !pairList.isEmpty()) {
                for (Pair<String, List<FileListRSP>> pair : pairList) {
                    if (map.containsKey(pair.getKey())) {
                        map.get(pair.getKey()).addAll(pair.getValue());
                    } else {
                        map.put(pair.getKey(), pair.getValue());
                    }
                }
            }
        }
        List<Pair<String, List<FileListRSP>>> rsp = new ArrayList<>();
        for (Map.Entry<String, List<FileListRSP>> entry : map.entrySet()) {
            Pair<String, List<FileListRSP>> pair = new Pair<>(entry.getKey(), entry.getValue());
            rsp.add(pair);
        }
        return rsp;
    }

    public void remove(FileRemoveREQ req) {
        // 先查询文档信息
        materialsListService.getEntityWithCheck(req.getFileId(), null);
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        if (fileCheck != null) {
            fileCheck.checkRemove(req.getModuleType(), req.getFileId());
        }
        MaterialsList materialsList = materialsListService.getById(req.getFileId());
        materialsListService.remove(req.getFileId());
        //删除后处理
        if (fileCheck != null) {
            fileCheck.afterRemoveHandle(req.getMainId(), ListUtil.toList(materialsList), req.getUserId());
        }
    }

    public void batchRemove(FileBatchRemoveREQ req) {
        // 先查询文档信息
        if (ObjectUtil.isEmpty(req.getFileIds())) {
            return;
        }
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        if (fileCheck != null) {
            fileCheck.checkRemove(req.getModuleType(), req.getFileIds());
        }
        // 决议文件同步删除
        resolutionHandler(req);
        //章程文件同步删除
        if (BusinessModuleEnum.CONTRACT.name().equals(req.getModuleType())) {
            contractConstitutionFile(req);
        }
        if (BusinessModuleEnum.CLIENT.name().equals(req.getModuleType())) {
            Client client = new Client();
            client.setId(req.getMainId());
            client.setLatestUserId(AccountUtil.getLoginInfo().getId());
            SpringUtil.getBean(ClientService.class).updateById(client);
        }
        List<MaterialsList> fileList = materialsListService.listByIds(req.getFileIds());
        materialsListService.remove(req.getFileIds(), req.getMainId());
        //删除后处理
        if (fileCheck != null) {
            fileCheck.afterRemoveHandle(req.getMainId(), fileList, req.getUserId());
        }
    }

    /**
     * 决议文件同步删除
     *
     * @param req
     */
    public void resolutionHandler(FileBatchRemoveREQ req) {
        if (BusinessModuleEnum.CONTRACT.name().equals(req.getModuleType())) {
            // 找到属于决议文件类型的文件
            List<MaterialsList> resolutionFileList = materialsListService.getByIds(req.getFileIds()).stream()
                    .filter(m -> ContractTypeEnum.RESOLUTION_FILE.name().equals(m.getMaterialsType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(resolutionFileList)) {
                Long contractId = req.getMainId();
                List<ContractTenantry> contractTenantryList = new ArrayList<>();
                List<ContractGuarantor> contractGuarantorList = new ArrayList<>();

                for (MaterialsList materialsList : resolutionFileList) {
                    // 查询承租人表中该文件的数据
                    ContractTenantry contractTenantry = contractTenantryService.getOne(Wrappers.<ContractTenantry>lambdaQuery()
                            .eq(ContractTenantry::getContractId, contractId)
                            .like(ContractTenantry::getResolutionFileId, materialsList.getId()));
                    if (contractTenantry != null) {
                        List<String> tenantryFileIds = JSON.parseArray(contractTenantry.getResolutionFileId(), String.class);
                        tenantryFileIds.remove(materialsList.getId().toString());
                        contractTenantry.setResolutionFileId(JSON.toJSONString(tenantryFileIds));
                        contractTenantryList.add(contractTenantry);
                    } else {
                        // 若承租人表中不存在 则查询合同担保表中该文件的数据
                        ContractGuarantor contractGuarantor = contractGuarantorService.getOne(Wrappers.<ContractGuarantor>lambdaQuery()
                                .eq(ContractGuarantor::getContractId, contractId)
                                .like(ContractGuarantor::getResolutionFileId, materialsList.getId()));
                        if (contractGuarantor != null) {
                            List<String> guarantorFileIds = JSON.parseArray(contractGuarantor.getResolutionFileId(), String.class);
                            guarantorFileIds.remove(materialsList.getId().toString());
                            contractGuarantor.setResolutionFileId(JSON.toJSONString(guarantorFileIds));
                            contractGuarantorList.add(contractGuarantor);
                        }
                    }
                }
                // 更新表数据
                if (CollectionUtils.isNotEmpty(contractTenantryList)) {
                    contractTenantryService.updateBatchById(contractTenantryList);
                }
                if (CollectionUtils.isNotEmpty(contractGuarantorList)) {
                    contractGuarantorService.updateBatchById(contractGuarantorList);
                }

            }

        }

    }

    /**
     * 章程文件同步删除
     *
     * @param req
     */
    public void contractConstitutionFile(FileBatchRemoveREQ req) {
        // 找到属于决议文件类型的文件
        List<MaterialsList> resolutionFileList = materialsListService.getByIds(req.getFileIds()).stream()
                .filter(m -> ContractTypeEnum.RESOLUTION_FILE.name().equals(m.getMaterialsType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(resolutionFileList)) {
            Wrapper<ContractConstitutionFile> queryWrapper = Wrappers.<ContractConstitutionFile>lambdaQuery().eq(ContractConstitutionFile::getContractId, req.getMainId())
                    .in(ContractConstitutionFile::getMaterialsListId, req.getFileIds());
            contractConstitutionFileService.remove(queryWrapper);
        }
        //找到抵质押文件
        List<MaterialsList> mortgagePledgeFileList = materialsListService.getByIds(req.getFileIds()).stream()
                .filter(m -> ContractTypeEnum.MORTGAGE_PLEDGE_FILE.name().equals(m.getMaterialsType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(mortgagePledgeFileList)) {
            Wrapper<ContractConstitutionFile> queryWrapper = Wrappers.<ContractConstitutionFile>lambdaQuery().eq(ContractConstitutionFile::getContractId, req.getMainId())
                    .in(ContractConstitutionFile::getMaterialsListId, req.getFileIds());
            contractConstitutionFileService.remove(queryWrapper);
        }
    }

    public FileDownLoadRSP download(FileDownLoadREQ req) {
        if (ObjectUtil.equal(VERSIONED, req.getIdType())) {
            return materialsListService.downloadLib(req.getFileId());
        }
        // 先查询文档信息
        materialsListService.getEntityWithCheck(req.getFileId(), req.getVersion());
        //各模块检查
        FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
        if (fileCheck != null) {
            fileCheck.checkDownload(req.getModuleType(), req.getMainId(), Collections.singletonList(req.getFileId()));
        }
        if (BusinessModuleEnum.ARCHIVES.name().equals(req.getModuleType())) {
            String watermark = null;
            AccountVO loginInfo = AccountUtil.getLoginInfo();
            Response<UserVO> userInfoById = userService.getUserInfoById(loginInfo.getId());
            if (userInfoById.isSuccess()) {
                UserVO data = userInfoById.getData();
                String realPhone = userService.getRealPhone(loginInfo.getId());
                String s = StrUtil.isNotEmpty(realPhone) ? realPhone.substring(realPhone.length() - 4) : "";
                watermark = "浙商租赁" + data.getUserName() + s;
            }
            return materialsListService.download(req.getFileId(), req.getVersion(), watermark);
        }
        return materialsListService.download(req.getFileId(), req.getVersion());
    }

    public String downloadTemplate(FileDownLoadTemplateREQ req) {
        if (ObjectUtil.isNotNull(req.getModuleType())) {
            FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
            if (fileCheck != null) {
                fileCheck.checkTemplateDownload(req.getModuleType(), req.getTemplateName());
            }
        }
        try {
            FileTemplateEnum moduleEnum = Optional.ofNullable(FileTemplateEnum.of(req.getTemplateName())).orElseThrow(() -> new MithrasException("未找到相应模版"));
            return ossClient.getPreviewUrl(moduleEnum.display, GlobalConstants.FILE_TEMPLATE_EXPIRY);
        } catch (Exception e) {
            log.error("FileService downloadTemplate param : {} error", req, e);
            throw new MithrasException("模版下载失败");
        }

    }

    public void batchDownload(FileBatchDownLoadREQ req) {
        if (!ObjectUtil.equal(VERSIONED, req.getIdType())) {
            //各模块检查
            FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
            if (fileCheck != null) {
                fileCheck.checkDownload(req.getModuleType(), req.getMainId(), req.getFileId());
            }
        }
        AbstractFileBatchDownload fileBatchDownload = getFileBatchDownload(req.getModuleType());
        if (fileBatchDownload != null) {
            fileBatchDownload.batchDownload(req);
        }
        //下载
    }

    //有自定义验证返回自定义，没有返回默认
    private FileModuleCheck getFileCheck(String moduleKey) {
        FileModuleCheck fileModuleCheckRSP = null;
        for (FileModuleCheck fileModuleCheck : fileModuleChecks) {
            if (fileModuleCheck.isCheck(moduleKey)) {
                return fileModuleCheck;
            }
            if (ObjectUtil.isNull(fileModuleCheckRSP) && ObjectUtil.equals(fileModuleCheck.getModuleKey(), BusinessModuleEnum.DEFAULT.name())) {
                fileModuleCheckRSP = fileModuleCheck;
            }
        }
        return fileModuleCheckRSP;
    }

    public AbstractFileBatchDownload getFileBatchDownload(String moduleKey) {
        AbstractFileBatchDownload fileBatchDownload = null;
        for (AbstractFileBatchDownload download : fileBatchDownloads) {
            if (download.isCheck(moduleKey)) {
                return download;
            }
            if (ObjectUtil.isNull(fileBatchDownload) && ObjectUtil.equals(download.getModuleKey(), BusinessModuleEnum.DEFAULT.name())) {
                fileBatchDownload = download;
            }
        }
        return fileBatchDownload;
    }

    public PageR<Map<String, DiffValue>> fileListCompare(FileListREQ req) {
        PageR<FileListRSP> pageData = list(req);
        if (CollectionUtils.isEmpty(pageData.getList())) {
            return PageR.of(new ArrayList<>(), pageData.getTotal(), pageData.getPages(), pageData.getCurrentPage(), pageData.getPageSize());
        }
        int versionType = calCompareVersionType(req);
        CommonVersion beforeVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getModule, req.getModuleType())
                .eq(CommonVersion::getMainId, req.getMainId())
                .eq(CommonVersion::getVersionType, versionType)
                .lt(StringUtils.isNotBlank(req.getVersion()), CommonVersion::getVersion, req.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1")
        );
        Map<Long, FileListRSP> beforeFileMap = Objects.isNull(beforeVersion) ? new HashMap<>() :
                materialsListLibService.getBaseMapper().selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                                .eq(MaterialsListLib::getVersion, beforeVersion.getVersion())
                                .in(MaterialsListLib::getOriginId, pageData.getList().stream().map(FileListRSP::getId).collect(Collectors.toList()))
                        ).stream().map(fileConvert::actualLib2Entity)
                        .map(fileConvert::entity2RSP)
                        .collect(Collectors.toMap(FileListRSP::getId, rsp -> rsp));
        fileConvert.fillName(beforeFileMap.values());
        List<Map<String, DiffValue>> diffList = new ArrayList<>();
        for (FileListRSP rsp : pageData.getList()) {
            Map<String, DiffValue> diffMap = CompareUtil.compare(rsp, beforeFileMap.get(rsp.getId()));
            if (Objects.isNull(beforeVersion)) {
                // 之前没版本，不做对比
                diffMap.values().forEach(d -> d.setIsChange(false));
            }
            diffList.add(diffMap);
        }
        return PageR.of(diffList, pageData.getTotal(), pageData.getPages(), pageData.getCurrentPage(), pageData.getPageSize());
    }

    public List<Pair<String, List<Map<String, DiffValue>>>> fileListGroupCompare(FileListREQ req) {
        List<Pair<String, List<FileListRSP>>> dataList = listGroup(req);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        int versionType = calCompareVersionType(req);
        CommonVersion beforeVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getModule, req.getModuleType())
                .eq(CommonVersion::getMainId, req.getMainId())
                .eq(CommonVersion::getVersionType, versionType)
                .lt(StringUtils.isNotBlank(req.getVersion()), CommonVersion::getVersion, req.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1")
        );
        Map<Long, FileListRSP> beforeFileMap = Objects.isNull(beforeVersion) ? new HashMap<>() :
                materialsListLibService.getBaseMapper().selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                                .eq(MaterialsListLib::getVersion, beforeVersion.getVersion())
                                .in(MaterialsListLib::getOriginId, dataList.stream().map(Pair::getValue).flatMap(Collection::stream).map(FileListRSP::getId).collect(Collectors.toList()))
                        ).stream().map(fileConvert::actualLib2Entity)
                        .map(fileConvert::entity2RSP)
                        .collect(Collectors.toMap(FileListRSP::getId, rsp -> rsp));
        fileConvert.fillName(beforeFileMap.values());
        List<Pair<String, List<Map<String, DiffValue>>>> diffList = new ArrayList<>();
        for (Pair<String, List<FileListRSP>> pair : dataList) {
            List<Map<String, DiffValue>> diffActList = new ArrayList<>();
            diffList.add(new Pair<>(pair.getKey(), diffActList));
            for (FileListRSP fileListRSP : pair.getValue()) {
                Map<String, DiffValue> diffMap = CompareUtil.compare(fileListRSP, beforeFileMap.get(fileListRSP.getId()));
                if (Objects.isNull(beforeVersion)) {
                    // 之前没版本，不做对比
                    diffMap.values().forEach(d -> d.setIsChange(false));
                }
                diffActList.add(diffMap);
            }
        }
        return diffList;
    }

    public List<Pair<String, List<Map<String, DiffValue>>>> fileListGroupCompareV2(FileListREQ req) {
        List<Pair<String, List<FileListRSP>>> dataList = listGroup(req);
        int versionType = calCompareVersionType(req);
        CommonVersion beforeVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getModule, req.getModuleType())
                .eq(CommonVersion::getMainId, req.getMainId())
                .eq(CommonVersion::getVersionType, versionType)
                .lt(StringUtils.isNotBlank(req.getVersion()), CommonVersion::getVersion, req.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1")
        );
        Map<Long, FileListRSP> beforeFileMap = new HashMap<>();
        List<Pair<String, List<FileListRSP>>> oldList = null;
        if (Objects.nonNull(beforeVersion)) {
            req.setVersion(beforeVersion.getVersion());
            oldList = listGroup(req);
            beforeFileMap = oldList.stream().map(Pair::getValue).flatMap(Collection::stream).collect(Collectors.toMap(FileListRSP::getId, Function.identity()));
        }

        Map<Long, List<String>> userJobMap = getAllOperateUserJob(JobEnum.projmanager);
        List<Pair<String, List<Map<String, DiffValue>>>> diffList = new ArrayList<>();
        Map<String, List<Map<String, DiffValue>>> diffListMap = new HashMap<>();
        // 这个地方直接N^3时间复杂度，如果需要，可以稍微优化一下
        for (Pair<String, List<FileListRSP>> pair : dataList) {
            List<Map<String, DiffValue>> diffActList = new ArrayList<>();
            diffList.add(new Pair<>(pair.getKey(), diffActList));
            diffListMap.put(pair.getKey(), diffActList);
            for (FileListRSP fileListRSP : pair.getValue()) {
                Map<String, DiffValue> diffMap = CompareUtil.compare(fileListRSP, beforeFileMap.get(fileListRSP.getId()));
                if (Objects.isNull(beforeVersion)) {
                    // 之前没版本，不做对比
                    diffMap.values().forEach(d -> d.setIsChange(false));
                } else if (diffMap.containsKey(CompareUtil.MODULE_CHANGED_FLAG_KEY)) {
                    diffMap.values().forEach(d -> d.setChangeType(InfoOperation.ADD.name()));
                }
                // 增加上传人的岗位列表，出于扩展性和安全性考虑，虽然是list，但是当前只返回运营经办岗, 这里是可以删除的标识
                AccountVO loginInfo = AccountUtil.getLoginInfo();
                if (CollUtil.isNotEmpty(userJobMap.get(fileListRSP.getUpdateBy()))
                        || Objects.equals(ReflectUtil.getFieldValue(fileListRSP, "systemGenerate"), 1)
                        || Optional.ofNullable(loginInfo).map(AccountVO::getId).orElse(0L).equals(fileListRSP.getUpdateBy())
                ) {
                    DiffValue diffValue = diffMap.get("uploadByPostList");
                    diffValue.setValue(Collections.singletonList(JobEnum.projmanager.name()));
                }
                diffActList.add(diffMap);
            }
        }

        if (Objects.isNull(beforeVersion)) {
            return diffList;
        }

        Map<Long, FileListRSP> newFileMap = dataList.stream().map(Pair::getValue).flatMap(Collection::stream).collect(Collectors.toMap(FileListRSP::getId, Function.identity()));
        for (Pair<String, List<FileListRSP>> pair : oldList) {
            List<Map<String, DiffValue>> diffActList = diffListMap.get(pair.getKey());
            if (diffActList == null) {
                diffActList = new ArrayList<>();
                diffList.add(new Pair<>(pair.getKey(), diffActList));
            }
            for (FileListRSP fileListRSP : pair.getValue()) {
                Map<String, DiffValue> diffMap = CompareUtil.compare(fileListRSP, newFileMap.get(fileListRSP.getId()));
                if (diffMap.containsKey(CompareUtil.MODULE_CHANGED_FLAG_KEY)) {
                    diffMap.values().forEach(d -> d.setChangeType(InfoOperation.REMOVE.name()));
                    diffActList.add(diffMap);
                }
            }
        }
        return diffList;
    }

    public Map<Long, List<String>> getAllOperateUserJob(JobEnum jobEnum) {
        List<UserDO> usersByJobCod = userService.getUsersByjobcod(jobEnum.name());
        HashMap<Long, List<String>> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(usersByJobCod)) {
            usersByJobCod.forEach(userDO -> map.put(userDO.getId(), Collections.singletonList(jobEnum.name())));
            return map;
        }
        return map;
    }

    /**
     * 计算对比所使用的版本类型
     *
     * @return
     */
    private int calCompareVersionType(FileListREQ req) {
        int versionType = VersionTypeConstants.NORMAL;
        if (BusinessModuleEnum.PROJ_REVIEW.name().equals(req.getModuleType()) && Objects.nonNull(req.getExt())) {
            FileListREQProjReviewExt extREQ = JSONObject.parseObject(JSON.toJSONString(req.getExt()), FileListREQProjReviewExt.class);
            if (Objects.nonNull(extREQ.getProcessInstanceId())) {
                // 审批流中查询
                ProcessResp processResp = taskApiService.queryProcessById(extREQ.getProcessInstanceId());
                if (Objects.isNull(processResp)) {
                    throw new MithrasException("流程不存在");
                }
//                if (CharSequenceUtil.equalsAny(processResp.getModelKey(), ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.name())) {
//                    versionType = ProjReviewVersionTypeConstants.PRICING_NORMAL;
//                }
            }
        }
        return versionType;
    }

    public List<Pair<String, List<DiffFile>>> fileListVersionCompare(FileListVersionREQ req) {
        CommonVersion newVersion = commonVersionMapper.selectById(req.getVersionId());
        if (Objects.isNull(newVersion)) {
            throw new MithrasException("版本不存在");
        }
        CommonVersion oldVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, newVersion.getMainId())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, newVersion.getModule())
                .lt(CommonVersion::getVersion, newVersion.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if (Objects.isNull(oldVersion)) {
            throw new MithrasException("上一版本不存在");
        }

        BusinessModuleEnum businessModuleEnum = BusinessModuleEnum.of(req.getModuleType());
        if (businessModuleEnum == null) {
            throw new MithrasException("moduleType不合法");
        }
        List<MaterialsListLib> oldMaterialsLists = materialsListLibHandlerProxy
                .listNeedHandleLib(oldVersion.getMainId(), oldVersion.getVersion(), businessModuleEnum);
        List<MaterialsListLib> newMaterialsLists = materialsListLibHandlerProxy
                .listNeedHandleLib(newVersion.getMainId(), newVersion.getVersion(), businessModuleEnum);

        Map<String, List<MaterialsListLib>> oldGroupMap = oldMaterialsLists.stream()
                .filter(f -> filterCompareFile(businessModuleEnum, f))
                .collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
        Map<String, List<MaterialsListLib>> newGroupMap = newMaterialsLists.stream()
                .filter(f -> filterCompareFile(businessModuleEnum, f))
                .collect(Collectors.groupingBy(MaterialsList::getMaterialsType));

        Set<String> materialsTypeSet = new HashSet<>();
        materialsTypeSet.addAll(oldGroupMap.keySet());
        materialsTypeSet.addAll(newGroupMap.keySet());

        List<Pair<String, List<DiffFile>>> fileList = new ArrayList<>();
        for (String materialsType : materialsTypeSet) {
            List<MaterialsListLib> newMaterialsList = Optional.ofNullable(newGroupMap.get(materialsType)).orElse(Collections.emptyList());
            List<MaterialsListLib> oldMaterialsList = Optional.ofNullable(oldGroupMap.get(materialsType)).orElse(Collections.emptyList());
            List<DiffFile> diffFiles = fileCompare(newMaterialsList, oldMaterialsList);
            fileList.add(new Pair<>(materialsType, diffFiles));
        }
        return fileList;
    }

    private boolean filterCompareFile(BusinessModuleEnum businessModuleEnum, MaterialsListLib materialsListLib) {
        if (businessModuleEnum != BusinessModuleEnum.CONTRACT) {
            return true;
        }
        return ContractFileQueryType.COMMON.getMaterialsTypeList().contains(materialsListLib.getMaterialsType());
    }

    private List<DiffFile> fileCompare(List<MaterialsListLib> newMaterialsList, List<MaterialsListLib> oldMaterialsList) {
        List<DiffFile> fileList = new ArrayList<>();
        Map<Long, MaterialsListLib> newFileMap = newMaterialsList.stream().collect(Collectors.toMap(MaterialsListLib::getOriginId, Function.identity()));
        Map<Long, MaterialsListLib> oldFileMap = oldMaterialsList.stream().collect(Collectors.toMap(MaterialsListLib::getOriginId, Function.identity()));
        for (Map.Entry<Long, MaterialsListLib> newFileEntry : newFileMap.entrySet()) {
            Long id = newFileEntry.getKey();
            DiffFile diffFile = new DiffFile();
            BeanUtils.copyProperties(newFileEntry.getValue(), diffFile);
            if (oldFileMap.get(id) == null) {
                diffFile.setChangeType(InfoOperation.ADD.name());
                diffFile.setIsChange(true);
            }
            diffFile.setId(id);
            fileList.add(diffFile);
        }
        for (Map.Entry<Long, MaterialsListLib> oldFileEntry : oldFileMap.entrySet()) {
            Long id = oldFileEntry.getKey();
            if (newFileMap.get(id) == null) {
                DiffFile diffFile = new DiffFile();
                BeanUtils.copyProperties(oldFileEntry.getValue(), diffFile);
                diffFile.setChangeType(InfoOperation.REMOVE.name());
                diffFile.setIsChange(true);
                diffFile.setId(id);
                fileList.add(diffFile);
            }
        }
        for (DiffFile rsp : fileList) {
            rsp.setMaterialsTypeName(MaterialsTypeFactory.convert(rsp.getBusinessType(), rsp.getMaterialsType()));
            rsp.setMaterialSubTypeName(MaterialsTypeFactory.convert(rsp.getBusinessType(), rsp.getMaterialSubType()));
        }
        fileConvert.fillName(fileList);
        return fileList;
    }

    public void rename(FileRenameREQ req) {
        // 先查询文档信息
        materialsListService.getEntityWithCheck(req.getFileId(), null);
        if (Objects.equals(BusinessModuleEnum.CONTRACT.name(), req.getModuleType())) {
            //各模块检查
            FileModuleCheck fileCheck = getFileCheck(req.getModuleType());
            if (fileCheck != null) {
                fileCheck.checkRemove(req.getModuleType(), req.getFileId());
            }
        }
        materialsListService.rename(req);
    }

    public void fillFiledValue(MaterialsList obj, FileListRSP fileListRsp) {
        fileListRsp.setId(obj.getId());
        fileListRsp.setBelongId(obj.getBelongId());
        fileListRsp.setMaterialsType(obj.getMaterialsType());
        fileListRsp.setMaterialsTypeName(MaterialsTypeFactory.convert(obj.getBusinessType(), obj.getMaterialsType()));
        fileListRsp.setMaterialSubType(obj.getMaterialSubType());
        fileListRsp.setMaterialSubTypeName(MaterialsTypeFactory.convert(obj.getBusinessType(), obj.getMaterialSubType()));
        fileListRsp.setOssFilename(obj.getOssFilename());
        fileListRsp.setFilename(obj.getFilename());
        fileListRsp.setBusinessType(obj.getBusinessType());
        fileListRsp.setSuffix(obj.getSuffix());
        fileListRsp.setFilePath(obj.getFilePath());
        fileListRsp.setSystemGenerate(obj.getSystemGenerate());
        fileListRsp.setCreateTime(obj.getCreateTime());
        fileListRsp.setUpdateTime(obj.getUpdateTime());
        fileListRsp.setCreateBy(obj.getCreateBy());
        fileListRsp.setUpdateBy(obj.getUpdateBy());
    }

    public void checkBatchDownload(String moduleType,Long mainId,List<Long> fileIds){
        FileModuleCheck fileCheck = getFileCheck(moduleType);
        if (fileCheck != null) {
            fileCheck.checkDownload(moduleType, mainId, fileIds);
        }
    }
}
