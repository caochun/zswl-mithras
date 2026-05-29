package cn.zswltech.mithras.service.service.contract.text;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.contract.text.*;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.config.QiyuesuoConfig;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.contract.text.*;
import cn.zswltech.mithras.service.enums.payment.LendingMaterialType;
import cn.zswltech.mithras.service.mapper.contract.ContractTextManageMapper;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTextManage;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTextSignInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.bo.ContractTextManageBO;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractSignInfoService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.WatermarkUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author bigbear
 * @description 针对表【contract_text_manage(合同文本管理表)】的数据库操作Service实现
 * @createDate 2024-11-18 16:52:38
 */
@Slf4j
@Service
public class ContractTextManageService extends ServiceImpl<ContractTextManageMapper, ContractTextManage> {

    @Resource
    private FileService fileService;
    @Resource
    private OssClient ossClient;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private QiyuesuoConfig qiyuesuoConfig;
    @Resource
    private ContractTextManageMapper contractTextManageMapper;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractSignInfoService contractSignInfoService;
    @Resource
    private ContractTextSignInfoService contractTextSignInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;


    public PageR<ContractTextManageListRSP> list(ContractTextManageListREQ req) {
        Page<ContractTextManageBO> page = new Page<>(req.getPage(), req.getPageSize());
        Page<ContractTextManageBO> pageRecord = contractTextManageMapper.list(page, req);
        if (CollUtil.isEmpty(pageRecord.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        List<ContractTextManageListRSP> resultList = pageRecord.getRecords().stream().map(e -> {
            ContractTextManageListRSP rsp = new ContractTextManageListRSP();
            BeanUtil.copyProperties(e, rsp);
            rsp.setPushTime(e.getPushTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            return rsp;
        }).collect(Collectors.toList());

        return PageR.of(resultList, pageRecord.getTotal(), pageRecord.getPages(), req.getPage(), req.getPageSize());
    }

    public List<ContractTextManageUnSignDetailRSP> unSignedDetail(ContractTextManageUnSignDetailREQ req) {
        ContractTextManage contractTextManage = this.getById(req.getMainId());
        Assert.notNull(contractTextManage, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        List<ContractTextSignInfo> textSignInfos = contractTextSignInfoService.list(Wrappers.<ContractTextSignInfo>lambdaQuery().eq(ContractTextSignInfo::getMainId, contractTextManage.getId()));
        if (CollUtil.isEmpty(textSignInfos)) {
            return Collections.emptyList();
        }

        List<Long> fileIds = textSignInfos.stream().map(ContractTextSignInfo::getFileId).collect(Collectors.toList());
        List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT_TEXT_MANAGE.name())
                .in(MaterialsList::getId, fileIds));
        Map<Long, MaterialsList> materialsListMap = new HashMap<>();
        if (CollUtil.isNotEmpty(materialsLists)) {
            materialsLists.forEach(e -> materialsListMap.put(e.getId(), e));
        }

        List<ContractSignInfo> signInfoList = contractSignInfoService.list(Wrappers.<ContractSignInfo>lambdaQuery()
                .in(ContractSignInfo::getFileId, textSignInfos.stream().map(ContractTextSignInfo::getSourceFileId).collect(Collectors.toList())));
        Map<Long, List<ContractSignInfo>> signInfoMap = new HashMap<>();
        List<Long> clientIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(signInfoList)) {
            clientIds = signInfoList.stream().map(ContractSignInfo::getSignatory).collect(Collectors.toList());
            signInfoMap = signInfoList.stream().collect(Collectors.groupingBy(ContractSignInfo::getFileId));
        }
        Map<Long, String> clientId2NameMap = id2NameService.clientId2Name(clientIds);

        // 找到源文件的类型，用来构建展示顺序
        Map<Long, MaterialsList> map = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                        .in(MaterialsList::getId, textSignInfos.stream().map(ContractTextSignInfo::getSourceFileId).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(MaterialsList::getId, Function.identity(), (a, b) -> a));

        // 处理每个文件的签约方，构建响应对象
        List<ContractTextManageUnSignDetailRSP> resultList = new ArrayList<>();
        for (ContractTextSignInfo e : textSignInfos) {
            MaterialsList materialsList = materialsListMap.get(e.getFileId());
            if (materialsList == null) {
                log.error("不存在文件信息，文件ID为：{}", e.getFileId());
                continue;
            }
            ContractTextManageUnSignDetailRSP rsp = new ContractTextManageUnSignDetailRSP();
            rsp.setId(e.getId());
            rsp.setMaterialName(materialsList.getFilename());
            rsp.setTextSigningWay(e.getTextSignWay());
            rsp.setTextSigningStatus(e.getTextSignStatus());
            rsp.setFileId(e.getFileId());
            rsp.setModelType(BusinessModuleEnum.CONTRACT_TEXT_MANAGE.name());
            rsp.setPushTime(e.getPushTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            // 设置签约方信息
            List<ContractTextManageUnSignDetailRSP.Signer> signers = new ArrayList<>();
            List<ContractSignInfo> contractSignInfos = signInfoMap.get(e.getSourceFileId());
            if (CollUtil.isNotEmpty(contractSignInfos)) {
                contractSignInfos.forEach(signInfo -> {
                    ContractTextManageUnSignDetailRSP.Signer signer = new ContractTextManageUnSignDetailRSP.Signer();
                    signer.setSignerId(signInfo.getSignatory());
                    signer.setSignerName(signer.getSignerId() != 0 ? clientId2NameMap.get(signInfo.getSignatory()) : qiyuesuoConfig.getTenantName());
                    signer.setSigningWay(signInfo.getSignWay());
                    signer.setSigningStatus(signInfo.getSignStatus());
                    signer.setSigningCompleteTime(signInfo.getSignFinishTime() != null ? signInfo.getSignFinishTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)) : null);
                    signer.setRealNameAuthStatus(signInfo.getRealNameAuthStatus());
                    signer.setId(signInfo.getId());
                    signers.add(signer);
                });
            }
            rsp.setSignerList(signers);
            // 设置文件排序
            MaterialsList one = map.get(e.getSourceFileId());
            // 如果这里是空的，说明文件被删除了，网签也将数据删除
            if (one == null) {
                // 暂时不删除，但是也不展示数据
                continue;
            }
            ContractTypeEnum contractTypeEnum = ContractTypeEnum.getByName(one.getMaterialsType());
            rsp.setSort(Objects.isNull(contractTypeEnum) ? 99 : contractTypeEnum.getSort());
            resultList.add(rsp);
        }
        resultList.sort(Comparator.comparingInt(ContractTextManageUnSignDetailRSP::getSort));
        return resultList;
    }

    public List<FileListRSP> signedDetail(ContractTextManageSignedDetailREQ req) {
        ContractTextManage contractTextManage = this.getById(req.getMainId());
        Assert.notNull(contractTextManage, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        List<ContractTextSignInfo> textSignInfos = contractTextSignInfoService.list(Wrappers.<ContractTextSignInfo>lambdaQuery().eq(ContractTextSignInfo::getMainId, contractTextManage.getId()));
        if (CollUtil.isEmpty(textSignInfos)) {
            return Collections.emptyList();
        }
        // 获取文件列表
        List<Long> signedFileIds = textSignInfos.stream()
                .filter(e -> ContractTextStatusEnum.SIGNED.name().equals(e.getTextSignStatus()))
                .map(ContractTextSignInfo::getSignedFileId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollUtil.isEmpty(signedFileIds)) {
            return Collections.emptyList();
        }
        List<MaterialsList> materialsLists = materialsListService.listByIds(signedFileIds);
        if (CollUtil.isEmpty(materialsLists)) {
            return Collections.emptyList();
        }

        List<Long> userIds = materialsLists.stream().map(BaseModel::getCreateBy).collect(Collectors.toList());
        userIds.addAll(materialsLists.stream().map(BaseModel::getUpdateBy).collect(Collectors.toList()));
        Map<Long, String> userId2NameMap = id2NameService.sysUserId2Name(userIds);
        // 构造文件列表返回
        return materialsLists.stream().map(e -> {
            FileListRSP rsp = new FileListRSP();
            fileService.fillFiledValue(e, rsp);
            rsp.setCreateByName(userId2NameMap.get(rsp.getCreateBy()));
            rsp.setUpdateByName(userId2NameMap.get(rsp.getUpdateBy()));
            return rsp;
        }).collect(Collectors.toList());
    }

    public void downloadAll(HttpServletResponse response, ContractTextManageDownloadAllREQ req) throws IOException {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        ContractTextManage contractTextManage = this.getOne(Wrappers.<ContractTextManage>lambdaQuery()
                .eq(ContractTextManage::getContractId, req.getContractId())
                .last(StringUtil.mysqlLimitOne()));
        Assert.notNull(contractTextManage, () -> MithrasException.newException("文本管理不存在"));
        // 所有的文件打包导出
        ZipOutputStream zipOutputStream = null;
        try {
            response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            String zipName = contractBaseInfo.getContractCode() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)) + ".zip";
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(zipName, StandardCharsets.UTF_8.name()));
            zipOutputStream = new ZipOutputStream(response.getOutputStream());

            // 找到所有的文件，优先导出已签约的，其次导出转换后的文件
            List<ContractTextSignInfo> textSignInfos = contractTextSignInfoService.list(Wrappers.<ContractTextSignInfo>lambdaQuery()
                    .eq(ContractTextSignInfo::getMainId, contractTextManage.getId()));
            if (CollUtil.isEmpty(textSignInfos)) {
                throw new MithrasException("合同文本管理不存在已签约的文件，无法导出");
            }
            List<Long> convertedFileIds = textSignInfos.stream().map(ContractTextSignInfo::getFileId).collect(Collectors.toList());
            List<MaterialsList> materialsLists = materialsListService.listByIds(convertedFileIds);
            if (CollUtil.isEmpty(materialsLists)) {
                throw new MithrasException("合同文本管理不存在已签约的文件，无法导出");
            }
            zipHandler(materialsLists, zipOutputStream);
        } catch (IOException e) {
            log.error("合同文本管理文件打包导出异常", e);
            throw new MithrasException("合同文本管理文件打包导出异常, 请稍后再试或者联系管理员");
        } finally {
            if (zipOutputStream != null) {
                zipOutputStream.close();
            }
        }
    }

    /**
     * 转换所有文件
     *
     * @param contractId 合同ID
     */
    @Transactional(rollbackFor = Throwable.class)
    public void transformAllFile(@NotNull Long contractId, @NotNull Long mainId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));

        // 找到合同下的所有文件记录
        List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, contractId)
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                .in(MaterialsList::getMaterialsType, Arrays.stream(ContractTypeEnum.values()).map(ContractTypeEnum::name).collect(Collectors.toList()))
                .ne(MaterialsList::getMaterialsType, LendingMaterialType.SIGN_PHOTO_VIDEO.name()));
        if (CollUtil.isEmpty(materialsLists)) {
            return;
        }
        // 将所有的文件全部转化重新存储一遍
        List<ContractTextSignInfo> needSaveList = new LinkedList<>();
        if (CollUtil.isNotEmpty(materialsLists)) {
//            List<CompletableFuture<Void>> futures = new LinkedList<>();
            for (MaterialsList material : materialsLists) {
//                CompletableFuture<Void> future = CompletableFuture.supplyAsync(() ->
//                {
                    ContractTextSignInfo leaseNeedSignThreeFile = new ContractTextSignInfo();
                    leaseNeedSignThreeFile.setMainId(mainId);
                    leaseNeedSignThreeFile.setSourceFileId(material.getId());
                    try {
                        InputStream inputStream = ossClient.downLoad(material.getOssFilename());
                        ByteArrayOutputStream outputStream;
                        byte[] byteArray;
                        String fileName;
                        if (CharSequenceUtil.equalsAny(material.getSuffix(), FileTypeEnum.DOC.getExName(), FileTypeEnum.DOCX.getExName())) {
                            outputStream = WatermarkUtil.doc2Pdf(inputStream);
                            fileName = material.getFilename().substring(0, material.getFilename().lastIndexOf(".") + 1) + FileTypeEnum.PDF.getExName();
                            byteArray = outputStream.toByteArray();
                        } else {
                            byteArray = ByteStreams.toByteArray(inputStream);
                            fileName = material.getFilename();
                        }
                        // 将文件重新上传到OSS
                        Long returnFileId = materialsListService.add(
                                new ByteArrayInputStream(byteArray),
                                fileName,
                                mainId,
                                ContractTextSignMaterialTypeEnum.CONVERTED.name(),
                                null,
                                BusinessModuleEnum.CONTRACT_TEXT_MANAGE.name(),
                                YesOrNoNumberEnum.YES
                        );
                        leaseNeedSignThreeFile.setTextSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
                        leaseNeedSignThreeFile.setConvertedFileId(returnFileId);
                    } catch (Exception e) {
                        log.error("合同文本管理文件下载异常, 文件ID：{}", material.getId(), e);
                    }
                    leaseNeedSignThreeFile.setPushTime(LocalDateTime.now());
                    needSaveList.add(leaseNeedSignThreeFile);
//                    return null;
//                });
//                futures.add(future);
            }

//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        if (CollUtil.isNotEmpty(needSaveList)) {
            contractTextSignInfoService.saveBatch(needSaveList);
        }
    }

    /**
     * 转换当前文件
     *
     * @param contractId 合同ID
     */
    @Transactional(rollbackFor = Throwable.class)
    public Long transformFile(@NotNull Long contractId, @NotNull Long mainId, @NotNull Long fileId) {
        log.info("转换当前文件,合同id:"+contractId);
        log.info("转换当前文件,mainId:"+mainId);
        log.info("转换当前文件,fileId:"+fileId);
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));
        Long signId = null;
        // 找到合同下的所有文件记录
        MaterialsList material = materialsListService.getById(fileId);
        if (ObjectUtil.isEmpty(material)) {
            return signId;
        }
        log.info("合同下的文件记录,fileId:"+material.getId());
        // 将文件转化重新存储一遍
        if (ObjectUtil.isNotEmpty(material)) {
                ContractTextSignInfo leaseNeedSignThreeFile = new ContractTextSignInfo();
                leaseNeedSignThreeFile.setMainId(mainId);
                leaseNeedSignThreeFile.setSourceFileId(material.getId());
                try {
                    InputStream inputStream = ossClient.downLoad(material.getOssFilename());
                    ByteArrayOutputStream outputStream;
                    byte[] byteArray;
                    String fileName;
                    if (CharSequenceUtil.equalsAny(material.getSuffix(), FileTypeEnum.DOC.getExName(), FileTypeEnum.DOCX.getExName())) {
                        outputStream = WatermarkUtil.doc2Pdf(inputStream);
                        fileName = material.getFilename().substring(0, material.getFilename().lastIndexOf(".") + 1) + FileTypeEnum.PDF.getExName();
                        byteArray = outputStream.toByteArray();
                    } else {
                        byteArray = ByteStreams.toByteArray(inputStream);
                        fileName = material.getFilename();
                    }
                    // 将文件重新上传到OSS
                    Long returnFileId = materialsListService.add(
                            new ByteArrayInputStream(byteArray),
                            fileName,
                            mainId,
                            ContractTextSignMaterialTypeEnum.CONVERTED.name(),
                            null,
                            BusinessModuleEnum.CONTRACT_TEXT_MANAGE.name(),
                            YesOrNoNumberEnum.YES
                    );
                    leaseNeedSignThreeFile.setTextSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
                    leaseNeedSignThreeFile.setConvertedFileId(returnFileId);
                } catch (Exception e) {
                    log.error("合同文本管理文件下载异常, 文件ID：{}", material.getId(), e);
                }
                leaseNeedSignThreeFile.setTextSignWay(SigningWayEnum.LEASE_ONLINE_SIGN.name());
                leaseNeedSignThreeFile.setPushTime(LocalDateTime.now());
                contractTextSignInfoService.save(leaseNeedSignThreeFile);
//                ContractTextSignInfo textSignInfo = contractTextSignInfoService.getOne(Wrappers.<ContractTextSignInfo>lambdaQuery()
//                        .eq(ContractTextSignInfo::getConvertedFileId, leaseNeedSignThreeFile.getConvertedFileId()));
                log.info("生成的合同文本管理文件Id:"+leaseNeedSignThreeFile.getId());
                signId = leaseNeedSignThreeFile.getId();
        }
        return signId;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateDefaultSigningWay(ContractTextManageUpdateDefaultSigningWayREQ req) {
        ContractTextManage textManage = contractTextManageMapper.selectById(req.getMainId());
        Assert.notNull(textManage, () -> MithrasException.newException("合同文本管理不存在"));
        // 找到关联的文件
        List<ContractTextSignInfo> contractTextSignInfos = contractTextSignInfoService.list(Wrappers.<ContractTextSignInfo>lambdaQuery()
                .eq(ContractTextSignInfo::getMainId, textManage.getId()));
        if (CollUtil.isEmpty(contractTextSignInfos)) {
            throw MithrasException.newException("合同文本管理没有关联的文件");
        }
        List<ContractTextSignInfo> needUpdateList = new LinkedList<>();
        // 找到文本管理下的所有签约方
        List<ContractSignInfo> contractSignInfos = contractSignInfoService.list(Wrappers.<ContractSignInfo>lambdaQuery()
                .eq(ContractSignInfo::getContractId, textManage.getContractId())
                .eq(ContractSignInfo::getSignatory, 0));
        List<ContractSignInfo> needInitList = new LinkedList<>();
        // 找到转化后的文件备用
        Map<Long, MaterialsList> materialsListMap = materialsListService.listByIds(contractTextSignInfos.stream().map(ContractTextSignInfo::getConvertedFileId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(MaterialsList::getId, Function.identity(), (v1, v2) -> v1));
        if (CollUtil.isEmpty(contractSignInfos)) {
            for (ContractTextSignInfo textSignInfo : contractTextSignInfos) {
                // 还需要检查文件是否存在关键字
                MaterialsList materialsList = materialsListMap.get(textSignInfo.getConvertedFileId());
                if (Objects.isNull(materialsList)) {
                    continue;
                }
                try {
                    InputStream inputStream = ossClient.downLoad(materialsList.getOssFilename());
                    byte[] byteArray = ByteStreams.toByteArray(inputStream);
                    boolean containsKeyword = ContractTextSignInfoService.containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getContractSeal());
                    if (!containsKeyword) {
                        continue;
                    }
                } catch (IOException e) {
                    continue;
                }
                // init one
                ContractSignInfo info = new ContractSignInfo();
                info.setContractId(textManage.getContractId());
                info.setFileId(textSignInfo.getSourceFileId());
                // 签约方不是租赁公司的需要默认设置为线下签约
                info.setSignWay(SigningWayEnum.OFFLINE_SIGN.name());
                info.setSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
                info.setSignatory(0L);
                needInitList.add(info);
            }
        } else {
            List<Long> fileIds = contractSignInfos.stream().filter(e -> e.getSignatory().equals(0L))
                    .map(ContractSignInfo::getFileId).collect(Collectors.toList());
            List<ContractTextSignInfo> tmpList = contractTextSignInfos.stream()
                    .filter(e -> !fileIds.contains(e.getSourceFileId()))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(tmpList)) {
                // 初始化不存在的签约人的文件
                for (ContractTextSignInfo textSignInfo : tmpList) {
                    // 还需要检查文件是否存在关键字
                    MaterialsList materialsList = materialsListMap.get(textSignInfo.getConvertedFileId());
                    if (Objects.isNull(materialsList)) {
                        continue;
                    }
                    try {
                        InputStream inputStream = ossClient.downLoad(materialsList.getOssFilename());
                        byte[] byteArray = ByteStreams.toByteArray(inputStream);
                        boolean containsKeyword = ContractTextSignInfoService.containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getContractSeal());
                        if (!containsKeyword) {
                            continue;
                        }
                    } catch (IOException e) {
                        continue;
                    }
                    if (!fileIds.contains(textSignInfo.getSourceFileId())) {
                        // init one
                        ContractSignInfo info = new ContractSignInfo();
                        info.setContractId(textManage.getContractId());
                        info.setFileId(textSignInfo.getSourceFileId());
                        // 签约方不是租赁公司的需要默认设置为线下签约
                        info.setSignWay(SigningWayEnum.OFFLINE_SIGN.name());
                        info.setSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
                        info.setSignatory(0L);
                        needInitList.add(info);
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(needInitList)) {
            contractSignInfoService.saveBatch(needInitList);
        }
        for (ContractTextSignInfo contractTextSignInfo : contractTextSignInfos) {
            if (!CharSequenceUtil.equalsAny(contractTextSignInfo.getTextSignStatus(), ContractTextStatusEnum.SIGNED.name(), ContractTextStatusEnum.PART_SIGNED.name())) {
                contractTextSignInfo.setTextSignWay(req.getTextSigningWay());
                needUpdateList.add(contractTextSignInfo);
            }
            // 同时变更签约方的签约方式
            contractSignInfoService.update(Wrappers.<ContractSignInfo>lambdaUpdate()
                    .eq(ContractSignInfo::getFileId, contractTextSignInfo.getSourceFileId())
                    // 王振说：目前批量修改只改租赁公司的签约方式
                    .eq(ContractSignInfo::getSignatory, 0)
                    .ne(ContractSignInfo::getSignStatus, ContractTextStatusEnum.SIGNED.name())
                    .set(ContractSignInfo::getSignWay, req.getTextSigningWay()));
        }
        contractTextSignInfoService.updateBatchById(needUpdateList);

        // 更新列表的签约方式
        SpringUtil.getBean(ContractTextManageService.class).lambdaUpdate()
                .set(ContractTextManage::getSignWay, req.getTextSigningWay())
                .eq(ContractTextManage::getId, req.getMainId())
                .update();
    }


    @Transactional(rollbackFor = Throwable.class)
    public void updateSingleSigningWay(ContractTextManageUpdateSingleSigningWayREQ req) {
        try {

            // 更改单个文件的签约方式根据ID修改
            ContractTextSignInfo contractTextSignInfo = contractTextSignInfoService.getById(req.getId());
            Assert.notNull(contractTextSignInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
            // 检查是不是已经签约
            Assert.isFalse(CharSequenceUtil.equalsAny(contractTextSignInfo.getTextSignStatus(), ContractTextStatusEnum.SIGNED.name(),
                    ContractTextStatusEnum.PART_SIGNED.name()), () -> MithrasException.newException("文件已经签约，不允许修改"));
            contractTextSignInfo.setTextSignWay(req.getTextSigningWay());
            contractTextSignInfoService.updateById(contractTextSignInfo);

            ContractTextManage contractTextManage = contractTextManageMapper.selectById(contractTextSignInfo.getMainId());
            Assert.notNull(contractTextManage, () -> MithrasException.newException("合同文本管理不存在"));

            // 找到关联的文件，如果没有签约方，则需要初始化一个租赁的签约方
            ContractSignInfo contractSignInfo = contractSignInfoService.getOne(Wrappers.<ContractSignInfo>lambdaQuery()
                    .eq(ContractSignInfo::getSignatory, 0)
                    .eq(ContractSignInfo::getFileId, contractTextSignInfo.getSourceFileId())
                    .last(StringUtil.mysqlLimitOne()));
            // 拿到当前转化后的文件，判断是否需要租赁盖章
            MaterialsList materialsList = materialsListService.getById(contractTextSignInfo.getConvertedFileId());
            if (Objects.nonNull(materialsList)) {
                // 拿到文件流，判断是否需要盖章
                InputStream contentStream = ossClient.downLoad(materialsList.getOssFilename());
                byte[] byteArray = ByteStreams.toByteArray(contentStream);
                boolean containsKeyword = ContractTextSignInfoService.containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getContractSeal());
                if (Objects.isNull(contractSignInfo) && containsKeyword) {
                    ContractSignInfo info = new ContractSignInfo();
                    info.setContractId(contractTextManage.getContractId());
                    info.setFileId(contractTextSignInfo.getSourceFileId());
                    // 签约方不是租赁公司的需要默认设置为线下签约
                    info.setSignWay(SigningWayEnum.OFFLINE_SIGN.name());
                    info.setSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
                    info.setSignatory(0L);
                    info.setSignStatus(ContractTextStatusEnum.NO_SIGNED.name());
                    contractSignInfoService.save(info);
                }
            }
            // 同时变更签约方的签约方式
            contractSignInfoService.update(Wrappers.<ContractSignInfo>lambdaUpdate()
                    .eq(ContractSignInfo::getFileId, contractTextSignInfo.getSourceFileId())
                    // 王振说：目前批量修改只改租赁公司的签约方式
                    .eq(ContractSignInfo::getSignatory, 0)
                    .ne(ContractSignInfo::getSignStatus, ContractTextStatusEnum.SIGNED.name())
                    .set(ContractSignInfo::getSignWay, req.getTextSigningWay()));
        } catch (Exception e) {
            log.error("修改单个签约方式异常", e);
            throw MithrasException.newException(e.getMessage());
        }
    }

    public void downloadWaitSign(HttpServletResponse response, ContractTextManageDownloadWaitSignREQ req) throws IOException {
        ZipOutputStream zipOutputStream = null;
        try {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
            Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同ID为【" + req.getContractId() + "】的合同不存在"));
            response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            String zipName = contractBaseInfo.getContractCode() + "-批量导出" + ".zip";
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(zipName, StandardCharsets.UTF_8.name()));
            zipOutputStream = new ZipOutputStream(response.getOutputStream());

            // 找到文件列表
            List<ContractTextSignInfo> contractTextSignInfos = contractTextSignInfoService.listByIds(req.getFileRecordIds());
            if (CollUtil.isEmpty(contractTextSignInfos)) {
                throw MithrasException.newException("文件不存在");
            }
            List<MaterialsList> materialsLists = materialsListService.listByIds(contractTextSignInfos.stream().map(ContractTextSignInfo::getFileId).collect(Collectors.toList()));
            if (CollUtil.isEmpty(materialsLists)) {
                throw MithrasException.newException("文件不存在");
            }

            // 将文件下载下来打包
            zipHandler(materialsLists, zipOutputStream);
        } catch (MithrasException e) {
            log.error("导出文件异常", e);
            throw e;
        } finally {
            if (zipOutputStream != null) {
                zipOutputStream.close();
            }
        }
    }

    private void zipHandler(List<MaterialsList> materialsLists, ZipOutputStream zipOutputStream) {
        Map<String, List<MaterialsList>> collect = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getFilename));
        for (Map.Entry<String, List<MaterialsList>> entry : collect.entrySet()) {
            List<MaterialsList> materialsList = entry.getValue();
            if (materialsList.size() > 1) {
                for (int i = 0; i < materialsList.size(); i++) {
                    MaterialsList one = materialsList.get(i);
                    String ossFilename = one.getOssFilename();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ossClient.downLoad(outputStream, ossFilename);
                    try {
                        // 优化一下，把字符拼到后面，但是不影响文件后缀
                        String currentPath = one.getFilename().substring(0, one.getFilename().lastIndexOf("."))
                                + "_" + (i + 1) + one.getFilename().substring(one.getFilename().lastIndexOf("."));
                        zipOutputStream.putNextEntry(new ZipEntry(currentPath));
                        zipOutputStream.write(outputStream.toByteArray());
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                        log.error("压缩文件异常", e);
                    }
                }
            } else {
                try {
                    MaterialsList one = materialsList.get(0);
                    String ossFilename = one.getOssFilename();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ossClient.downLoad(outputStream, ossFilename);
                    zipOutputStream.putNextEntry(new ZipEntry(one.getFilename()));
                    zipOutputStream.write(outputStream.toByteArray());
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    log.error("压缩文件异常", e);
                }
            }
        }
    }

    /**
     * 查询App端上传的图片和视频
     *
     * @param req 请求参数
     * @return List<FileListRSP>
     */
    public List<ContractTextSignInfoSignPhotosAndVideosRSP> getSignPhotosAndVideos(ContractTextSignInfoSignPhotosAndVideosREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        // 找到实际的文件资料
        List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, contractBaseInfo.getId())
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                .eq(MaterialsList::getMaterialsType, LendingMaterialType.SIGN_PHOTO_VIDEO.name()));

        if (CollUtil.isEmpty(materialsLists)) {
            return Collections.emptyList();
        }

        // 转换为返回值
        List<Long> userIds = new LinkedList<>();
        if (CollUtil.isNotEmpty(materialsLists)) {
            // 找到上传人更新人信息
            userIds.addAll(materialsLists.stream().map(MaterialsList::getCreateBy).collect(Collectors.toList()));
            userIds.addAll(materialsLists.stream().map(MaterialsList::getUpdateBy).collect(Collectors.toList()));
        }
        Map<Long, String> userId2NameMap = id2NameService.sysUserId2Name(userIds);
        return materialsLists.stream().map(e -> {
            ContractTextSignInfoSignPhotosAndVideosRSP rsp = new ContractTextSignInfoSignPhotosAndVideosRSP();
            fileService.fillFiledValue(e, rsp);
            rsp.setLocation(e.getLocation());
            rsp.setCreateByName(userId2NameMap.get(e.getCreateBy()));
            rsp.setUpdateByName(userId2NameMap.get(e.getUpdateBy()));
            return rsp;
        }).collect(Collectors.toList());
    }
}