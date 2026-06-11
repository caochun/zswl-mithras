package cn.zswltech.mithras.application.orchestration.contract.text;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.contract.text.ContractTextManageBatchSignREQ;
import cn.zswltech.mithras.dto.contract.text.ContractTextManageSingleSignREQ;
import cn.zswltech.mithras.dto.file.FileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.third.qiyuesuo.client.config.QiyuesuoConfig;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.enums.FileTypeEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.text.*;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ContractSettleOwnerChangeRender;
import cn.zswltech.mithras.contract.mapper.contract.ContractTextSignInfoMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextSignInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.ContractSignInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.FileService;
import cn.zswltech.mithras.document.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.third.qiyuesuo.client.QiyuesuoService;
import cn.zswltech.mithras.third.qiyuesuo.client.dto.*;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.third.util.WatermarkUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import net.qiyuesuo.v3sdk.model.common.*;
import net.qiyuesuo.v3sdk.model.contract.request.ContractCreatebycategoryRequest;
import net.qiyuesuo.v3sdk.model.contract.request.ContractDetailRequest;
import net.qiyuesuo.v3sdk.model.contract.request.ContractRecallRequest;
import net.qiyuesuo.v3sdk.model.document.request.DocumentDownloadRequest;
import net.qiyuesuo.v3sdk.model.v2contract.request.V2ContractSignbylegalpersonRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @description 针对表【contract_text_sign_info(合同文件签约信息表)】的数据库操作Service实现
 * @createDate 2024-11-18 16:52:38
 */
@Slf4j
@Service
public class ContractTextSignInfoService extends ServiceImpl<ContractTextSignInfoMapper, ContractTextSignInfo> {

    @Resource
    private QiyuesuoService qiyuesuoService;
    @Resource
    private OssClient ossClient;
    @Resource
    private QiyuesuoConfig qiyuesuoConfig;
    @Resource
    private FileService fileService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractTextSignInfoService thisService;
    @Resource
    private ContractSignInfoService contractSignInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractSettleOwnerChangeRender contractSettleOwnerChangeRender;
    @Resource
    private MaterialsListLibService materialsListLibService;

    /**
     * 批量签约
     *
     * @param req 请求参数
     */
    public String batchSign(ContractTextManageBatchSignREQ req) {
        StringBuilder result = new StringBuilder();
        // 获取合同文本管理信息
        List<ContractTextSignInfo> contractTextSignInfos = thisService.listByIds(req.getIdList());
        Assert.isTrue(CollUtil.isNotEmpty(contractTextSignInfos), () -> MithrasException.newException("不存在合同待签约文件信息"));

        // 判断是否都初始化了签约方式
        contractTextSignInfos.forEach(e -> Assert.notNull(e.getTextSignWay(), () -> MithrasException.newException("文本签约方式为必填项")));
        try {
            // 获取签约方信息
            List<ContractSignInfo> contractSignInfos = contractSignInfoService.list(Wrappers.<ContractSignInfo>lambdaQuery()
                    .in(ContractSignInfo::getFileId, contractTextSignInfos.stream().map(ContractTextSignInfo::getSourceFileId).collect(Collectors.toList()))
                    .ne(ContractSignInfo::getSignStatus, ContractTextStatusEnum.SIGNED.name())
                    .eq(ContractSignInfo::getSignWay, SigningWayEnum.LEASE_ONLINE_SIGN.name()));
            if (CollUtil.isEmpty(contractSignInfos)) {
                throw MithrasException.newException("均已完成线上用印或不存在签约方式为租赁线上先签的合同文本");
            }

            Map<Long, List<ContractSignInfo>> contractSignInfoMap = contractSignInfos.stream().collect(Collectors.groupingBy(ContractSignInfo::getFileId));

            // 创建临时变量存储转换后的文档流
            Map<Long, byte[]> fileId2InputStreamMap = new HashMap<>();
            List<Long> clientIds = contractSignInfos.stream().map(ContractSignInfo::getSignatory).filter(e -> Objects.equals(0L, e)).collect(Collectors.toList());
            Map<Long, String> id2NameMap = id2NameService.clientId2Name(clientIds);
            id2NameMap.put(0L, qiyuesuoConfig.getTenantName());
            // 找到这些已经转化过或者部分签约的文件
            Map<Long, MaterialsList> materialsListMap = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                            .in(MaterialsList::getId, contractTextSignInfos.stream().map(ContractTextSignInfo::getFileId).collect(Collectors.toList())))
                    .stream().collect(Collectors.toMap(MaterialsList::getId, Function.identity(), (v1, v2) -> v1));

            // 遍历检查文件是否合法
            Map<Long, Pair<byte[], String>> fileByteArrayMap = new HashMap<>();
            StringBuilder errorMsg = new StringBuilder();
            for (ContractTextSignInfo contractTextSignInfo : contractTextSignInfos) {
                List<ContractSignInfo> obj = contractSignInfoMap.get(contractTextSignInfo.getSourceFileId());
                if (Objects.nonNull(obj)) {
                    boolean contains = obj.stream().map(ContractSignInfo::getSignatory).collect(Collectors.toList()).contains(0L);
                    if (!contains) {
                        continue;
                    }
                    // 判断当前文件是否合法
                    MaterialsList materialsList = materialsListMap.get(contractTextSignInfo.getFileId());
                    if (materialsList == null) {
                        log.warn("不存在文件信息，文件ID为：{}", contractTextSignInfo.getFileId());
                        continue;
                    }
                    InputStream inputStream = ossClient.downLoad(materialsList.getOssFilename());
                    byte[] byteArray = ByteStreams.toByteArray(inputStream);
                    if (!containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getContractSeal())) {
                        errorMsg.append("[").append(materialsList.getFilename()).append("]\n");
                        continue;
                    }
                    fileByteArrayMap.put(contractTextSignInfo.getId(), Pair.of(byteArray, materialsList.getFilename()));
                    fileId2InputStreamMap.put(contractTextSignInfo.getFileId(), byteArray);
                }
            }
            if (errorMsg.length() > 0) {
                errorMsg.append("<br/><br/><span style=\"color: #000;\">");
                errorMsg.append("未包含【").append(qiyuesuoConfig.getSealKeyword().getContractSeal()).append("】关键字");
                errorMsg.append("</span>");
                throw MithrasException.customException(10086, errorMsg.toString());
            }
            // 将这些签约方按照文件ID分组，然后再分成租赁公司和其他签约人
            Map<Long, ContractSignInfo> companyContractSignInfoMap = contractSignInfos.stream().filter(contractSignInfo -> contractSignInfo.getSignatory().equals(0L)).collect(Collectors.toList())
                    .stream().collect(Collectors.toMap(ContractSignInfo::getFileId, Function.identity(), (v1, v2) -> v1));
            // List<ContractSignInfo> otherSignList = contractSignInfos.stream().filter(contractSignInfo -> !contractSignInfo.getSignatory().equals(0L)).collect(Collectors.toList());
            // 合同签署完毕，将合同文档下载回来
            List<Long> fileIdList = new LinkedList<>();
            // List<CompletableFuture<String>> futures = new LinkedList<>();

            List<StringBuilder> sbList = new LinkedList<>();
            AtomicReference<Integer> successCount = new AtomicReference<>(0);
            for (ContractTextSignInfo contractTextSignInfo : contractTextSignInfos) {
                // 如果已经签约过，则跳过
                if (CharSequenceUtil.equalsAny(contractTextSignInfo.getTextSignStatus(), ContractTextStatusEnum.SIGNED.name(),
                        ContractTextStatusEnum.PART_SIGNED.name())) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                try {
                    transactionTemplate.executeWithoutResult(transactionStatus -> {
                        // CompletableFuture<String> completableFuture;
                        String account = AccountUtil.getLoginInfo().getAccount();
                        // completableFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            UploadLocalFileRequest uploadLocalFileRequest = new UploadLocalFileRequest();
                            Pair<byte[], String> stringPair = fileByteArrayMap.get(contractTextSignInfo.getId());
                            if (stringPair == null) {
                                return;
                            }
                            uploadLocalFileRequest.setFile(new MockMultipartFile(stringPair.getValue(), stringPair.getKey()));
                            uploadLocalFileRequest.setTitle(stringPair.getValue());
                            uploadLocalFileRequest.setFileType(UploadLocalFileRequest.FileTypeEnum.PDF);
                            UploadLocalFileResponse uploadLocalFileResponse = qiyuesuoService.uploadLocalFile(uploadLocalFileRequest);
                            ContractTextSignInfo updateEntry = new ContractTextSignInfo();
                            updateEntry.setId(contractTextSignInfo.getId());
                            updateEntry.setQysDocumentId(Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId()));
                            thisService.updateById(updateEntry);
                            // 将数据更新到已经查出来的数据里
                            contractTextSignInfo.setQysDocumentId(Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId()));
                            fileId2InputStreamMap.put(contractTextSignInfo.getConvertedFileId(), stringPair.getKey());

                            ContractSignInfo contractSignInfo = companyContractSignInfoMap.get(contractTextSignInfo.getSourceFileId());
                            if (contractSignInfo == null) {
                                log.warn("文件[{}]签约失败，原因：文件不存在", stringPair.getValue());
                                sb.append(String.format("文件[%s]签约失败\n原因：文件不存在", stringPair.getValue()));
                                sbList.add(sb);
                                return;
                            }
                            ContractCreatebycategoryRequest request = new ContractCreatebycategoryRequest();
                            String fileName = Optional.of(materialsListMap.get(contractTextSignInfo.getConvertedFileId())).map(MaterialsList::getFilename).orElse(IdUtil.getSnowflakeNextIdStr());
                            request.setSubject(fileName);
                            request.setCategoryId(String.valueOf(qiyuesuoConfig.getCategoryId()));
                            request.setSend(true);
                            request.setTenantName(qiyuesuoConfig.getTenantName());
                            request.setDocuments(CollUtil.newArrayList(contractTextSignInfo.getQysDocumentId()));
                            request.setCreatorName(account);
                            List<Signatory> signatories = CollUtil.newArrayList();
                            Pair<Boolean, List<Action>> booleanListPair;
                            try {
                                for (ContractSignInfo signInfo : contractSignInfoMap.get(contractTextSignInfo.getSourceFileId())) {
                                    // 构建多个签约方
                                    Signatory signatory = new Signatory();
                                    // 签约主体 COMPANY（企业），PERSONAL（个人）
                                    signatory.setTenantType(contractSignInfo.getSignatory().equals(0L) ? "COMPANY" : "PERSONAL");
                                    signatory.setTenantName(id2NameMap.get(signInfo.getSignatory()));
                                    signatory.setSerialNo("1");
                                    signatory.setLanguage("zh_CN");
                                    booleanListPair = getActionList(signInfo, fileId2InputStreamMap.get(contractTextSignInfo.getConvertedFileId()), contractTextSignInfo, fileName);
                                    signatory.setActions(booleanListPair.getValue());
                                    if (CollUtil.isEmpty(booleanListPair.getValue())) {
                                        throw new RuntimeException("文件不需要签署");
                                    }
                                    signatories.add(signatory);
                                }
                            } catch (RuntimeException e) {
                                log.warn("文件[{}]不需要签署，原因：{}", stringPair.getValue(), e.getMessage(), e);
                                sb.append(String.format("文件[%s]签约失败\n原因：%s", stringPair.getValue(), e.getMessage()));
                                sbList.add(sb);
                                return;
                            }
                            request.setSignatories(signatories);
                            CreateContractResponse contract = qiyuesuoService.createContract(request);
                            contractTextSignInfo.setQysContractId(Long.valueOf(contract.getContractId()));
                            checkSignStatus(contractTextSignInfo.getQysContractId());

                            if (Objects.nonNull(contractTextSignInfo.getQysDocumentId())) {
                                DocumentDownloadRequest downloadRequest = new DocumentDownloadRequest();
                                downloadRequest.setDocumentId(contractTextSignInfo.getQysDocumentId());
                                try {
                                    DownloadContractResponse downloadContractResponse = qiyuesuoService.downloadContract(downloadRequest);
                                    MultipartFile file = downloadContractResponse.getFile();
                                    ReflectUtil.setFieldValue(file, "originalFilename", materialsListMap.get(contractTextSignInfo.getFileId()).getFilename());
                                    // 将这个文件上传到minio
                                    FileUploadREQ uploadReq = new FileUploadREQ();
                                    uploadReq.setFile(file);
                                    uploadReq.setMainId(contractTextSignInfo.getMainId());
                                    uploadReq.setModuleType(BusinessModuleEnum.CONTRACT_TEXT_MANAGE.name());
                                    uploadReq.setMaterialsType(ContractTextSignMaterialTypeEnum.SIGNED.name());
                                    FileUploadRSP rsp = fileService.upload(uploadReq);
                                    fileIdList.add(rsp.getFileId());
                                    contractTextSignInfo.setSignedFileId(rsp.getFileId());
                                    thisService.updateById(contractTextSignInfo);
                                } catch (IOException e) {
                                    log.error("合同文本管理文件下载异常, 文件：{}", stringPair.getValue());
                                    sb.append(String.format("文件[%s]签约失败\n原因：下载失败", stringPair.getValue()));
                                    sbList.add(sb);
                                    return;
                                }
                            }

                            boolean ok = thisService.queryContractDetail(contractTextSignInfo);
                            if (!ok) {
                                throw MithrasException.newException("法人章授权过期，需要重新授权");
                            }
                            successCount.getAndSet(successCount.get() + 1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //     return "";
                        // }).exceptionally(ex -> {
                        //     log.error("合同网签失败，文件为：{}", Optional.of(materialsListMap.get(contractTextSignInfo.getFileId()))
                        //             .map(MaterialsList::getFilename).orElse("id = " + contractTextSignInfo.getSourceFileId()), ex);
                        //     return String.format("合同网签失败，文件为：%s", Optional.of(materialsListMap.get(contractTextSignInfo.getFileId()))
                        //             .map(MaterialsList::getFilename).orElse("id = " + contractTextSignInfo.getSourceFileId()));
                        // });
                        // futures.add(completableFuture);
                    });
                } catch (Exception e) {
                    sb.append(String.format("文件[%s]签约失败\n原因：%s", Optional.of(materialsListMap.get(contractTextSignInfo.getFileId()))
                            .map(MaterialsList::getFilename).orElse("id = " + contractTextSignInfo.getSourceFileId()), e.getMessage()));
                    log.error("文件[{}]签约失败原因：{}", Optional.of(materialsListMap.get(contractTextSignInfo.getFileId()))
                            .map(MaterialsList::getFilename).orElse("id = " + contractTextSignInfo.getSourceFileId()), e.getMessage(), e);
                    sbList.add(sb);
                }
            }

            // 等待所有的异步线程全部执行完
            // CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            // 组装返回结果
            for (StringBuilder sb : sbList) {
                String msg = sb.toString();
                if (CharSequenceUtil.isNotBlank(msg)) {
                    result.append(msg).append("\n");
                }
            }
            if (CollUtil.isNotEmpty(fileIdList)) {
                materialsListService.lambdaUpdate()
                        .set(BaseModel::getUpdateBy, null)
                        .set(BaseModel::getCreateBy, null)
                        .in(MaterialsList::getId, fileIdList)
                        .update();
            }
            if (CharSequenceUtil.isNotBlank(result)) {
                return "网签执行成功！一共" +
                        contractTextSignInfos.size() + "个文件，成功用印" +
                        successCount + "个文件!，失败用印" +
                        sbList.size() + "个文件，忽略了" +
                        (contractTextSignInfos.size() - sbList.size() - successCount.get()) + "个文件" +
                        "\n存在下列问题：\n" + result;
            } else {
                // 返回空，前端不展示
                return null;
            }
        } catch (TransactionException | UtilException | MithrasException | IOException e) {
            if (e instanceof MithrasException) {
                if (((MithrasException) e).getCode().equals(10086)) {
                    throw MithrasException.customException(((MithrasException) e).getCode(), e.getMessage());
                }
            }
            throw MithrasException.newException(e.getMessage());
        }
    }

    /**
     * 单个文件签约
     *
     * @param signId 请求参数
     */
    public String fileSign(Long signId,String account,Long contractId,String modelKey) {
        StringBuilder result = new StringBuilder();
        // 获取合同文本管理信息
        ContractTextSignInfo contractTextSignInfo = thisService.getById(signId);
        Assert.isTrue(ObjectUtil.isNotEmpty(contractTextSignInfo), () -> MithrasException.newException("不存在待签约文件信息"));

        // 判断是否都初始化了签约方式
        Assert.notNull(contractTextSignInfo.getTextSignWay(), () -> MithrasException.newException("文本签约方式为必填项"));
        try {
            // 获取签约方信息
            ContractSignInfo contractSignInfo = contractSignInfoService.getOne(Wrappers.<ContractSignInfo>lambdaQuery()
                    .eq(ContractSignInfo::getFileId, contractTextSignInfo.getSourceFileId())
                    .ne(ContractSignInfo::getSignStatus, ContractTextStatusEnum.SIGNED.name())
                    .eq(ContractSignInfo::getSignWay, SigningWayEnum.LEASE_ONLINE_SIGN.name()));
            if (Objects.isNull(contractSignInfo)) {
                throw MithrasException.newException("均已完成线上用印或不存在签约方式为租赁线上先签的合同文本");
            }

            // 创建临时变量存储转换后的文档流
            Map<Long, byte[]> fileId2InputStreamMap = new HashMap<>();
            //设置签约人id（我方）
            Map<Long, String> id2NameMap = new HashMap<>();
            id2NameMap.put(0L, qiyuesuoConfig.getTenantName());

            // 检查文件是否合法
            // 找到已经转化过或者部分签约的文件
            MaterialsList materialsList = materialsListService.getById(contractTextSignInfo.getFileId());
            if (materialsList == null) {
                log.warn("不存在文件信息，文件ID为：{}", contractTextSignInfo.getFileId());
                return "";
            }
            Map<Long, Pair<byte[], String>> fileByteArrayMap = new HashMap<>();
            StringBuilder errorMsg = new StringBuilder();
            if (Objects.nonNull(contractSignInfo)) {
                //下载文件流
                InputStream inputStream = ossClient.downLoad(materialsList.getOssFilename());
                byte[] byteArray = ByteStreams.toByteArray(inputStream);
                if (!containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getContractSettleSeal())) {
                    errorMsg.append("[").append(materialsList.getFilename()).append("]\n");
                    return "";
                }
                fileByteArrayMap.put(contractTextSignInfo.getId(), Pair.of(byteArray, materialsList.getFilename()));
                fileId2InputStreamMap.put(contractTextSignInfo.getFileId(), byteArray);
            }
            if (errorMsg.length() > 0) {
                errorMsg.append("<br/><br/><span style=\"color: #000;\">");
                errorMsg.append("未包含【").append(qiyuesuoConfig.getSealKeyword().getContractSettleSeal()).append("】关键字");
                errorMsg.append("</span>");
                throw MithrasException.customException(10086, errorMsg.toString());
            }
            // 合同签署完毕，将合同文档下载回来
            List<Long> fileIdList = new LinkedList<>();

            List<StringBuilder> sbList = new LinkedList<>();
            AtomicReference<Integer> successCount = new AtomicReference<>(0);
            // 如果已经签约过，则跳过
            if (CharSequenceUtil.equalsAny(contractTextSignInfo.getTextSignStatus(), ContractTextStatusEnum.SIGNED.name(),
                    ContractTextStatusEnum.PART_SIGNED.name())) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            try {
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    try {
                        UploadLocalFileRequest uploadLocalFileRequest = new UploadLocalFileRequest();
                        Pair<byte[], String> stringPair = fileByteArrayMap.get(contractTextSignInfo.getId());
                        if (stringPair == null) {
                            return;
                        }
                        uploadLocalFileRequest.setFile(new MockMultipartFile(stringPair.getValue(), stringPair.getKey()));
                        uploadLocalFileRequest.setTitle(stringPair.getValue());
                        uploadLocalFileRequest.setFileType(UploadLocalFileRequest.FileTypeEnum.PDF);
                        //上传本地签署文件创建待签署的文档
                        UploadLocalFileResponse uploadLocalFileResponse = qiyuesuoService.uploadLocalFile(uploadLocalFileRequest);
                        ContractTextSignInfo updateEntry = new ContractTextSignInfo();
                        updateEntry.setId(contractTextSignInfo.getId());
                        updateEntry.setQysDocumentId(Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId()));
                        thisService.updateById(updateEntry);
                        // 将数据更新到已经查出来的数据里
                        contractTextSignInfo.setQysDocumentId(Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId()));
                        fileId2InputStreamMap.put(contractTextSignInfo.getConvertedFileId(), stringPair.getKey());

                        if (contractSignInfo == null) {
                            log.warn("文件[{}]签约失败，原因：文件不存在", stringPair.getValue());
                            sb.append(String.format("文件[%s]签约失败\n原因：文件不存在", stringPair.getValue()));
                            sbList.add(sb);
                            return;
                        }
                        ContractCreatebycategoryRequest request = new ContractCreatebycategoryRequest();
                        String fileName = materialsList.getFilename();
                        request.setSubject(fileName);
                        request.setCategoryId(String.valueOf(qiyuesuoConfig.getCategoryId()));
                        request.setSend(true);
                        request.setTenantName(qiyuesuoConfig.getTenantName());
                        request.setDocuments(CollUtil.newArrayList(contractTextSignInfo.getQysDocumentId()));
                        request.setCreatorName(account);
                        List<Signatory> signatories = CollUtil.newArrayList();
                        Pair<Boolean, List<Action>> booleanListPair;
                        try {
                            // 构建签约方
                            Signatory signatory = new Signatory();
                            // 签约主体 COMPANY（企业），PERSONAL（个人）
                            signatory.setTenantType(contractSignInfo.getSignatory().equals(0L) ? "COMPANY" : "PERSONAL");
                            signatory.setTenantName(id2NameMap.get(contractSignInfo.getSignatory()));
                            signatory.setSerialNo("1");
                            signatory.setLanguage("zh_CN");
                            booleanListPair = getSettleActionList(contractSignInfo, fileId2InputStreamMap.get(contractTextSignInfo.getConvertedFileId()), contractTextSignInfo, fileName);
                            signatory.setActions(booleanListPair.getValue());
                            if (CollUtil.isEmpty(booleanListPair.getValue())) {
                                throw new RuntimeException("文件不需要签署");
                            }
                            signatories.add(signatory);
                        } catch (RuntimeException e) {
                            log.warn("文件[{}]不需要签署，原因：{}", stringPair.getValue(), e.getMessage(), e);
                            sb.append(String.format("文件[%s]签约失败\n原因：%s", stringPair.getValue(), e.getMessage()));
                            sbList.add(sb);
                            return;
                        }
                        request.setSignatories(signatories);
                        //调用签约接口
                        CreateContractResponse contract = qiyuesuoService.createContract(request);
                        contractTextSignInfo.setQysContractId(Long.valueOf(contract.getContractId()));
                        //校验是否成功、失败重新推送
                        checkSignStatus(contractTextSignInfo.getQysContractId());

                        if (Objects.nonNull(contractTextSignInfo.getQysDocumentId())) {
                            DocumentDownloadRequest downloadRequest = new DocumentDownloadRequest();
                            downloadRequest.setDocumentId(contractTextSignInfo.getQysDocumentId());
                            try {
                                DownloadContractResponse downloadContractResponse = qiyuesuoService.downloadContract(downloadRequest);
                                MultipartFile file = downloadContractResponse.getFile();
                                ReflectUtil.setFieldValue(file, "originalFilename", materialsList.getFilename());
                                // 将这个文件上传到minio
                                FileUploadREQ uploadReq = new FileUploadREQ();
                                uploadReq.setFile(file);
                                uploadReq.setMainId(contractTextSignInfo.getMainId());
                                uploadReq.setModuleType(BusinessModuleEnum.CONTRACT_TEXT_MANAGE.name());
                                uploadReq.setMaterialsType(ContractTextSignMaterialTypeEnum.SIGNED.name());
                                FileUploadRSP rsp = fileService.upload(uploadReq);
                                fileIdList.add(rsp.getFileId());
                                contractTextSignInfo.setSignedFileId(rsp.getFileId());
                                thisService.updateById(contractTextSignInfo);
                            } catch (IOException e) {
                                log.error("合同文本管理文件下载异常, 文件：{}", stringPair.getValue());
                                sb.append(String.format("文件[%s]签约失败\n原因：下载失败", stringPair.getValue()));
                                sbList.add(sb);
                                return;
                            }
                        }
                        //更新签约状态
                        boolean ok = thisService.queryContractDetail(contractTextSignInfo);
                        if (!ok) {
                            throw MithrasException.newException("法人章授权过期，需要重新授权");
                        }
                        successCount.getAndSet(successCount.get() + 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                sb.append(String.format("文件[%s]签约失败\n原因：%s", Optional.of(materialsList)
                        .map(MaterialsList::getFilename).orElse("id = " + contractTextSignInfo.getSourceFileId()), e.getMessage()));
                log.error("文件[{}]签约失败原因：{}", Optional.of(materialsList)
                        .map(MaterialsList::getFilename).orElse("id = " + contractTextSignInfo.getSourceFileId()), e.getMessage(), e);
                sbList.add(sb);
            }
            // 组装返回结果
            String msg = sb.toString();
            if (CharSequenceUtil.isNotBlank(msg)) {
                result.append(msg).append("\n");
            }
            if (CollUtil.isNotEmpty(fileIdList)) {
                materialsListService.lambdaUpdate()
                        .set(BaseModel::getUpdateBy, null)
                        .set(BaseModel::getCreateBy, null)
                        .in(MaterialsList::getId, fileIdList)
                        .update();

                MaterialsList material = materialsListService.getById(fileIdList.get(0));
                if (ObjectUtil.isNotEmpty(material)) {
                    ByteArrayOutputStream outputStream = null;
                    ByteArrayInputStream pdfis = null;
                    InputStream inputStream = null;
                    try {
                        inputStream = ossClient.downLoad(material.getOssFilename());
                        if (inputStream != null) {
                            byte[] byteArray = ByteStreams.toByteArray(inputStream);
                            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
                            if(Objects.equals(modelKey, ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
                                //将所有权转移证书（已用印）文件返回到合同结清流程页面
                                Long fileId = materialsListService.add(new ByteArrayInputStream(byteArray), ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN_SIGN.display() + GlobalConstants.OFFICE_PDF_SUFFIX, contractBaseInfo.getId(), ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name(), "", BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                            }else{
                                //将实际租金表（已用印）文件返回到合同起租流程页面
                                Long fileId = materialsListService.add(new ByteArrayInputStream(byteArray), "实际租金表（已用印）" + GlobalConstants.OFFICE_PDF_SUFFIX, contractBaseInfo.getId(), ContractStatus.START_RENT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                            }
                        }
                    } catch (MithrasException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new MithrasException("所有权转移证书（已用印）发生未知异常！");
                    } finally {
                        if (Objects.nonNull(outputStream)) {
                            outputStream.close();
                        }
                        if (Objects.nonNull(pdfis)) {
                            pdfis.close();
                        }
                        if (Objects.nonNull(inputStream)) {
                            inputStream.close();
                        }
                    }


                }
            }
            if (CharSequenceUtil.isNotBlank(result)) {
                return "网签执行成功！一共1个文件，成功用印" +
                        successCount + "个文件!，失败用印" +
                        sbList.size() + "个文件，忽略了" +
                        (1 - sbList.size() - successCount.get()) + "个文件" +
                        "\n存在下列问题：\n" + result;
            } else {
                // 返回空，前端不展示
                return null;
            }
        } catch (TransactionException | UtilException | MithrasException | IOException e) {
            if (e instanceof MithrasException) {
                if (((MithrasException) e).getCode().equals(10086)) {
                    throw MithrasException.customException(((MithrasException) e).getCode(), e.getMessage());
                }
            }
            throw MithrasException.newException(e.getMessage());
        }
    }

    /**
     * 单个文件签约--针对存量没有合同文本管理数据的文件
     * @param fileId 文件编号 account签约人 contractId 合同编号
     */
    public String existFileSign(Long fileId,String account,Long contractId,String modelKey) {
        StringBuilder result = new StringBuilder();
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            //设置签约人id（我方）
            Map<Long, String> id2NameMap = new HashMap<>();
            // 设置签约方信息-浙江浙商融资租赁有限公司
            id2NameMap.put(0L, qiyuesuoConfig.getTenantName());
            // 检查文件是否合法
            // 找到已经转化过或者部分签约的文件
            MaterialsList materialsList = materialsListService.getById(fileId);
            if (materialsList == null) {
                log.warn("不存在文件信息，文件ID为：{}", fileId);
                return "";
            }
            String fileName;
            StringBuilder errorMsg = new StringBuilder();
            //下载文件流
            inputStream = ossClient.downLoad(materialsList.getOssFilename());
            byte[] byteArray;
            if (CharSequenceUtil.equalsAny(materialsList.getSuffix(), FileTypeEnum.DOC.getExName(), FileTypeEnum.DOCX.getExName())) {
                outputStream = WatermarkUtil.doc2Pdf(inputStream);
                fileName = materialsList.getFilename().substring(0, materialsList.getFilename().lastIndexOf(".") + 1) + FileTypeEnum.PDF.getExName();
                byteArray = outputStream.toByteArray();
            } else {
                byteArray = ByteStreams.toByteArray(inputStream);
                fileName = materialsList.getFilename();
            }
            if (!containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getContractSettleSeal())) {
                errorMsg.append("[").append(materialsList.getFilename()).append("]\n");
                return "";
            }
            if (errorMsg.length() > 0) {
                errorMsg.append("<br/><br/><span style=\"color: #000;\">");
                errorMsg.append("未包含【").append(qiyuesuoConfig.getSealKeyword().getContractSettleSeal()).append("】关键字");
                errorMsg.append("</span>");
                throw MithrasException.customException(10086, errorMsg.toString());
            }
            // 合同签署完毕，将合同文档下载回来
            List<StringBuilder> sbList = new LinkedList<>();
            AtomicReference<Integer> successCount = new AtomicReference<>(0);
            StringBuilder sb = new StringBuilder();
            try {
                String finalFileName = fileName;
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    try {
                        UploadLocalFileRequest uploadLocalFileRequest = new UploadLocalFileRequest();
                        uploadLocalFileRequest.setFile(new MockMultipartFile(finalFileName, byteArray));
                        uploadLocalFileRequest.setTitle(finalFileName);
                        uploadLocalFileRequest.setFileType(UploadLocalFileRequest.FileTypeEnum.PDF);
                        //上传本地签署文件创建待签署的文档
                        UploadLocalFileResponse uploadLocalFileResponse = qiyuesuoService.uploadLocalFile(uploadLocalFileRequest);
                        Long qysDocumentId = Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId());
                        ContractCreatebycategoryRequest request = new ContractCreatebycategoryRequest();
                        request.setSubject(finalFileName);
                        request.setCategoryId(String.valueOf(qiyuesuoConfig.getCategoryId()));
                        request.setSend(true);
                        request.setTenantName(qiyuesuoConfig.getTenantName());
                        request.setDocuments(CollUtil.newArrayList(qysDocumentId));
                        request.setCreatorName(account);
                        List<Signatory> signatories = CollUtil.newArrayList();
                        Pair<Boolean, List<Action>> booleanListPair;
                        try {
                            // 构建签约方
                            Signatory signatory = new Signatory();
                            // 签约主体 COMPANY（企业），PERSONAL（个人）
                            signatory.setTenantType("COMPANY" );
                            signatory.setTenantName(id2NameMap.get(0L));
                            signatory.setSerialNo("1");
                            signatory.setLanguage("zh_CN");
                            booleanListPair = getOldSettleActionList(0L, byteArray , qysDocumentId, finalFileName);
                            signatory.setActions(booleanListPair.getValue());
                            if (CollUtil.isEmpty(booleanListPair.getValue())) {
                                throw new RuntimeException("文件不需要签署");
                            }
                            signatories.add(signatory);
                        } catch (RuntimeException e) {
                            log.warn("文件[{}]不需要签署，原因：{}", finalFileName, e.getMessage(), e);
                            sb.append(String.format("文件[%s]签约失败\n原因：%s", finalFileName, e.getMessage()));
                            sbList.add(sb);
                            return;
                        }
                        request.setSignatories(signatories);
                        //调用签约接口
                        CreateContractResponse contract = qiyuesuoService.createContract(request);
                        //校验是否成功、失败重新推送
                        checkSignStatus(Long.valueOf(contract.getContractId()));

                        if (Objects.nonNull(Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId()))) {
                            DocumentDownloadRequest downloadRequest = new DocumentDownloadRequest();
                            downloadRequest.setDocumentId(Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId()));
                            try {
                                DownloadContractResponse downloadContractResponse = qiyuesuoService.downloadContract(downloadRequest);
                                MultipartFile file = downloadContractResponse.getFile();
                                if(Objects.equals(modelKey, ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
                                    //将所有权转移证书（已用印）文件返回到合同结清流程页面
                                    materialsListService.add(file.getInputStream(), ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN_SIGN.display() + GlobalConstants.OFFICE_PDF_SUFFIX, contractId, ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name(), "", BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                                }else{
                                    //将实际租金表（已用印）文件返回到合同起租流程页面
                                    materialsListService.add(new ByteArrayInputStream(byteArray), "实际租金表（已用印）" + GlobalConstants.OFFICE_PDF_SUFFIX, contractId, ContractStatus.START_RENT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                                }
                            } catch (IOException e) {
                                log.error("合同文本管理文件下载异常, 文件：{}", finalFileName);
                                sb.append(String.format("文件[%s]签约失败\n原因：下载失败", finalFileName));
                                sbList.add(sb);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                sb.append(String.format("文件[%s]签约失败\n原因：%s", Optional.of(materialsList)
                        .map(MaterialsList::getFilename).orElse("id = " + fileId), e.getMessage()));
                log.error("文件[{}]签约失败原因：{}", Optional.of(materialsList)
                        .map(MaterialsList::getFilename).orElse("id = " + fileId), e.getMessage(), e);
                sbList.add(sb);
            }
            // 组装返回结果
            String msg = sb.toString();
            if (CharSequenceUtil.isNotBlank(msg)) {
                result.append(msg).append("\n");
            }
            if (CharSequenceUtil.isNotBlank(result)) {
                return "网签执行成功！一共1个文件，成功用印" +
                        successCount + "个文件!，失败用印" +
                        sbList.size() + "个文件，忽略了" +
                        (1 - sbList.size() - successCount.get()) + "个文件" +
                        "\n存在下列问题：\n" + result;
            } else {
                // 返回空，前端不展示
                return null;
            }
        } catch (TransactionException | UtilException | MithrasException | IOException e) {
            if (e instanceof MithrasException) {
                if (((MithrasException) e).getCode().equals(10086)) {
                    throw MithrasException.customException(((MithrasException) e).getCode(), e.getMessage());
                }
            }
            throw MithrasException.newException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void checkSignStatus(Long signContractId) throws IOException {
        // 开始计时
        StopWatch stopWatch = StopWatch.create("合同签约");
        stopWatch.start();

        ContractDetailRequest detailRequest = new ContractDetailRequest();
        detailRequest.setContractId(signContractId);
        ContractDetailResponse contractDetail = qiyuesuoService.contractDetail(detailRequest);
        if (ContractSignStatusEnum.COMPLETE.name().equals(contractDetail.getContract().getStatus())) {
            log.info("合同签约成功");
            return;
        }
        // 阶梯重试时间间隔（毫秒）：1秒, 2秒, 3秒, 5秒, 8秒
        int[] retryIntervals = {1000, 2000, 3000, 5000, 9000};
        int maxRetries = retryIntervals.length;
        int retryCount = 0;
        boolean signedSuccess = false;

        while (retryCount < maxRetries) {
            try {
                ContractDetailResponse contractDetailRetry = qiyuesuoService.contractDetail(detailRequest);
                if (ContractSignStatusEnum.COMPLETE.name().equals(contractDetailRetry.getContract().getStatus())) {
                    log.info("合同签约成功");
                    signedSuccess = true;
                    break;
                }
            } catch (Exception e) {
                log.error("查询合同签约详情失败", e);
                // 如果是业务异常，直接抛出
                if (e instanceof MithrasException) {
                    throw e;
                }
            }
            // 如果不是最后一次重试，则等待指定时间后继续
            if (retryCount < maxRetries - 1) {
                try {
                    long sleepTime = retryIntervals[retryCount];
                    log.info("合同签约未完成，第{}次重试，{}ms后重试", retryCount + 1, sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw MithrasException.newException("线程中断，合同签约被取消");
                }
            }
            retryCount++;
        }
        stopWatch.stop();
        if (!signedSuccess) {
            // 超过最大重试次数仍未完成签约
            throw MithrasException.newException("合同签约超时，请稍后查看签约状态");
        }

        log.info("合同签约完成，耗时: {}ms", stopWatch.getLastTaskTimeMillis());
    }

    /**
     * 单个客户用印
     */
    @Transactional(rollbackFor = Throwable.class)
    public void singleSign(ContractTextManageSingleSignREQ req) {
        ContractTextSignInfo textSignInfo = thisService.getById(req.getId());
        if (Objects.isNull(textSignInfo)) {
            throw MithrasException.newException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (SigningWayEnum.OFFLINE_SIGN.name().equals(textSignInfo.getTextSignWay())) {
            throw MithrasException.newException("记录不允许线上签约");
        }
        if (ContractTextStatusEnum.SIGNED.name().equals(textSignInfo.getTextSignStatus())) {
            throw MithrasException.newException("文件已经签约，暂时不支持重新签约");
        }

        ContractSignInfo contractSignInfo = contractSignInfoService.getOne(Wrappers.<ContractSignInfo>lambdaQuery()
                .eq(ContractSignInfo::getFileId, textSignInfo.getSourceFileId())
                .eq(ContractSignInfo::getSignatory, req.getSignerId())
                .last(StringUtil.mysqlLimitOne()));
        Assert.notNull(contractSignInfo, "不存在合同签约方信息");

        // 拿到转换后的文件
        MaterialsList materialsList = materialsListService.getById(textSignInfo.getConvertedFileId());
        if (Objects.isNull(materialsList)) {
            throw MithrasException.newException("转换后的文件不存在，不允许修改");
        }
        // 生成签署文档
        byte[] byteArray = null;
        try {
            UploadLocalFileRequest uploadLocalFileRequest = new UploadLocalFileRequest();
            InputStream contentStream = ossClient.downLoad(materialsList.getOssFilename());
            byteArray = ByteStreams.toByteArray(contentStream);
            if (!containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getContractSeal())) {
                String errorMsg = "<br/><br/><span style=\"color: #000;\">" +
                        "未包含【" + qiyuesuoConfig.getSealKeyword().getContractSeal() + "】关键字</span>";
                throw MithrasException.newException(errorMsg);
            }
            uploadLocalFileRequest.setFile(new MockMultipartFile(materialsList.getFilename(), new ByteArrayInputStream(byteArray)));
            uploadLocalFileRequest.setTitle(materialsList.getFilename());
            uploadLocalFileRequest.setFileType(UploadLocalFileRequest.FileTypeEnum.PDF);
            UploadLocalFileResponse uploadLocalFileResponse = qiyuesuoService.uploadLocalFile(uploadLocalFileRequest);
            textSignInfo.setQysDocumentId(Long.valueOf(uploadLocalFileResponse.getResult().getDocumentId()));
            thisService.updateById(textSignInfo);
        } catch (IOException e) {
            log.error("创建合同签署文档失败！");
        }
        try {
            // 创建合同
            ContractCreatebycategoryRequest request = new ContractCreatebycategoryRequest();
            request.setSubject(materialsList.getFilename());
            request.setCategoryId(String.valueOf(qiyuesuoConfig.getCategoryId()));
            request.setSend(true);
            request.setTenantName(qiyuesuoConfig.getTenantName());
            request.setDocuments(CollUtil.newArrayList(textSignInfo.getQysDocumentId()));
            request.setCreatorName(AccountUtil.getLoginInfo().getAccount());
            Signatory st = new Signatory();
            // 签约主体 COMPANY（企业），PERSONAL（个人）
            st.setTenantType(req.getSignerId() == 0 ? "CORPORATE" : "PERSONAL");
            st.setTenantName(qiyuesuoConfig.getTenantName());
            st.setSerialNo(String.valueOf(1L));
            Pair<Boolean, List<Action>> booleanListPair = getActionList(contractSignInfo, byteArray, textSignInfo, materialsList.getFilename());
            st.setActions(booleanListPair.getValue());
            if (CollUtil.isEmpty(booleanListPair.getValue())) {
                throw MithrasException.newException("该文件不需要盖章");
            }
            request.setSignatories(CollUtil.newArrayList(st));

            qiyuesuoService.createContract(request);

            // 如果需要法人签章的话
//            if (containsKeyword(byteArray, qiyuesuoConfig.getSealKeyword().getCorporateSeal())) {
//                // 开始静默签署
//                try {
//                    V2ContractSignbylegalpersonRequest legalPersonRequest = getV2ContractSignbylegalpersonRequest(textSignInfo);
//                    qiyuesuoService.legalPersonSealSignV2(legalPersonRequest);
//                } catch (IOException e) {
//                    log.error("网签静默签署失败", e);
//                    throw new RuntimeException(e);
//                }
//            }
        } catch (IOException e) {
            log.error("创建网签合同失败", e);
            throw MithrasException.newException("创建网签合同失败");
        }
        // 查询网签合同的签约状态，若还在签约中的话需要将记录设置为待签约
        boolean ok = thisService.queryContractDetail(textSignInfo);
        if (!ok) {
            // transactionManager.rollback(transaction);
            throw MithrasException.newException("法人章授权过期，需要重新授权");
        }
    }

    public V2ContractSignbylegalpersonRequest getV2ContractSignbylegalpersonRequest(ContractTextSignInfo textSignInfo) {
        V2ContractSignbylegalpersonRequest legalPersonRequest = new V2ContractSignbylegalpersonRequest();
        SignSilentContract silentContract = new SignSilentContract();
        silentContract.setId(textSignInfo.getQysContractId());
        legalPersonRequest.setContract(silentContract);
        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setName(qiyuesuoConfig.getTenantName());
        legalPersonRequest.setCompany(companyRequest);
        return legalPersonRequest;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean queryContractDetail(ContractTextSignInfo textSignInfo) {
        try {
            ContractDetailRequest detailRequest = new ContractDetailRequest();
            detailRequest.setContractId(textSignInfo.getQysContractId());
            ContractDetailResponse contractDetail = qiyuesuoService.contractDetail(detailRequest);
            if (ContractSignStatusEnum.SIGNING.name().equals(contractDetail.getContract().getStatus())) {
                thisService.lambdaUpdate()
                        .eq(ContractTextSignInfo::getId, textSignInfo.getId())
                        .set(ContractTextSignInfo::getTextSignStatus, ContractTextStatusEnum.NO_SIGNED.name())
                        .set(ContractTextSignInfo::getSignedFileId, null)
                        .set(ContractTextSignInfo::getQysContractId, null)
                        .update();
                return false;
            } else if (ContractSignStatusEnum.COMPLETE.name().equals(contractDetail.getContract().getStatus())) {
                // 说明本次塞进去的签约方已经全部签署完成
                List<ContractSignInfo> contractSignInfos = contractSignInfoService.list(Wrappers.<ContractSignInfo>lambdaQuery()
                        .eq(ContractSignInfo::getFileId, textSignInfo.getSourceFileId())
                        .ne(ContractSignInfo::getSignatory, 0));
                if (CollUtil.isEmpty(contractSignInfos)) {
                    thisService.lambdaUpdate()
                            .set(ContractTextSignInfo::getTextSignStatus, ContractTextStatusEnum.SIGNED.name())
                            .eq(ContractTextSignInfo::getId, textSignInfo.getId())
                            .update();
                } else {
                    // 部分签署完成
                    thisService.lambdaUpdate()
                            .set(ContractTextSignInfo::getTextSignStatus, ContractTextStatusEnum.PART_SIGNED.name())
                            .eq(ContractTextSignInfo::getId, textSignInfo.getId())
                            .update();
                }
                // 同时更新签约方状态
                contractSignInfoService.lambdaUpdate()
                        .set(ContractSignInfo::getSignStatus, ContractTextStatusEnum.SIGNED.name())
                        .set(ContractSignInfo::getSignFinishTime, LocalDateTime.now())
                        .eq(ContractSignInfo::getSignatory, 0)
                        .eq(ContractSignInfo::getFileId, textSignInfo.getSourceFileId())
                        .update();
                return true;
            } else {
                // 将合同作废
                thisService.cancelContract(textSignInfo);
                throw MithrasException.newException("存在签约方自动签署失败");
            }
        } catch (IOException e) {
            log.error("查询网签合同详情失败", e);
            // transactionManager.rollback(transaction);
            throw MithrasException.newException("查询网签合同详情失败");
        }
    }

    public void cancelContract(ContractTextSignInfo textSignInfo) throws IOException {
        ContractRecallRequest contractRecallRequest = new ContractRecallRequest();
        contractRecallRequest.setContractId(textSignInfo.getQysContractId());
        contractRecallRequest.setReason("存在签约方未自动签署完成");
        qiyuesuoService.withdrawContract(contractRecallRequest);
        log.error("网签合同签署失败，存在签约方自动签署失败");
    }


    private Pair<Boolean, List<Action>> getActionList(@NotNull ContractSignInfo signInfo, byte[] fileBytes, ContractTextSignInfo textSignInfo, String fileName) throws IOException {
        Assert.notNull(signInfo, () -> MithrasException.newException("签约方信息不能为空"));
        boolean needCorporateSign = false;
        boolean keywordSign = false;
        List<Action> actionList = CollUtil.newArrayList();
        if (signInfo.getSignatory() == 0) {
            Action systemAction = new Action();
            systemAction.setType("CORPORATE");
            systemAction.setName("合同签署");
            systemAction.setSerialNo(1L);
            systemAction.setSealId(qiyuesuoConfig.getCompanyContractSealId());
            systemAction.setAutoSign(true);
            List<SignatoryRect> locations = CollUtil.newArrayList();
            // 签署方为系统租户
            if (containsKeyword(fileBytes, qiyuesuoConfig.getSealKeyword().getContractSeal())) {
                keywordSign = true;
                SignatoryRect signatoryRect = new SignatoryRect();
                signatoryRect.setDocumentId(textSignInfo.getQysDocumentId());
                signatoryRect.setKeyword(qiyuesuoConfig.getSealKeyword().getContractSeal());
                signatoryRect.setKeywordIndex(0L);
                signatoryRect.setRelativePosition("CENTER");
                signatoryRect.setRectType(RectTypeEnum.SEAL_CORPORATE.name());
                locations.add(signatoryRect);
            } else {
                throw MithrasException.newException("合同文件[" + fileName + "]未包含合同印章关键字");
            }

            // 文件超过一页，盖骑缝章
            try {
                if (keywordSign && PDDocument.load(fileBytes).getNumberOfPages() > 1) {
                    SignatoryRect rect = new SignatoryRect();
                    rect.setRectType(RectTypeEnum.ACROSS_PAGE.name());
                    rect.setDocumentId(textSignInfo.getQysDocumentId());
                    rect.setOffsetY(0.5);
                    locations.add(rect);
                }
            } catch (IOException e) {
                log.error("判断PDF文件页数异常，骑缝章预设异常");
            }

            // 判断是否需要盖法人章 这个章不需要了
            /*if (containsKeyword(fileBytes, qiyuesuoConfig.getSealKeyword().getCorporateSeal())) {
                Action action = BeanUtil.copyProperties(systemAction, Action.class);
                action.setType("LP");
                action.setSealId(qiyuesuoConfig.getLegalPersonSealId());
                SignatoryRect rect = new SignatoryRect();
                rect.setDocumentId(textSignInfo.getQysDocumentId());
                rect.setKeyword(qiyuesuoConfig.getSealKeyword().getCorporateSeal());
                rect.setRectType(RectTypeEnum.SEAL_CORPORATE.name());
                rect.setKeywordIndex(0L);
                rect.setOffsetY(0.03);
                rect.setRelativePosition("CENTER");
                action.setLocations(CollUtil.newArrayList(rect));
                action.setAutoSign(true);
                actionList.add(action);
                needCorporateSign = true;
            }*/
            systemAction.setLocations(locations);
            actionList.add(systemAction);
        } else {
            // TODO 签署方为个人
            throw MithrasException.newException("个人签署暂不支持");
        }

        return Pair.of(needCorporateSign, actionList);
    }

    private Pair<Boolean, List<Action>> getSettleActionList(@NotNull ContractSignInfo signInfo, byte[] fileBytes, ContractTextSignInfo textSignInfo, String fileName) throws IOException {
        Assert.notNull(signInfo, () -> MithrasException.newException("签约方信息不能为空"));
        boolean needCorporateSign = false;
        boolean keywordSign = false;
        List<Action> actionList = CollUtil.newArrayList();
        if (signInfo.getSignatory() == 0) {
            Action systemAction = new Action();
            systemAction.setType("CORPORATE");
            systemAction.setName("合同签署");
            systemAction.setSerialNo(1L);
            systemAction.setSealId(qiyuesuoConfig.getCompanyContractSealId());
            systemAction.setAutoSign(true);
            List<SignatoryRect> locations = CollUtil.newArrayList();
            // 签署方为系统租户
            if (containsKeyword(fileBytes, qiyuesuoConfig.getSealKeyword().getContractSettleSeal())) {
                keywordSign = true;
                SignatoryRect signatoryRect = new SignatoryRect();
                signatoryRect.setDocumentId(textSignInfo.getQysDocumentId());
                signatoryRect.setKeyword(qiyuesuoConfig.getSealKeyword().getContractSettleSeal());
                signatoryRect.setKeywordIndex(0L);
                signatoryRect.setRelativePosition("CENTER");
                signatoryRect.setRectType(RectTypeEnum.SEAL_CORPORATE.name());
                locations.add(signatoryRect);
            } else {
                throw MithrasException.newException("合同文件[" + fileName + "]未包含合同印章关键字");
            }

            // 文件超过一页，盖骑缝章
            try {
                if (keywordSign && PDDocument.load(fileBytes).getNumberOfPages() > 1) {
                    SignatoryRect rect = new SignatoryRect();
                    rect.setRectType(RectTypeEnum.ACROSS_PAGE.name());
                    rect.setDocumentId(textSignInfo.getQysDocumentId());
                    rect.setOffsetY(0.5);
                    locations.add(rect);
                }
            } catch (IOException e) {
                log.error("判断PDF文件页数异常，骑缝章预设异常");
            }
            systemAction.setLocations(locations);
            actionList.add(systemAction);
        } else {
            // TODO 签署方为个人
            throw MithrasException.newException("个人签署暂不支持");
        }

        return Pair.of(needCorporateSign, actionList);
    }

    private Pair<Boolean, List<Action>> getOldSettleActionList(@NotNull Long signatory, byte[] fileBytes,Long qysDocumentId, String fileName) throws IOException {
        Assert.notNull(signatory, () -> MithrasException.newException("签约方信息不能为空"));
        boolean needCorporateSign = false;
        boolean keywordSign = false;
        List<Action> actionList = CollUtil.newArrayList();
        if (signatory == 0) {
            Action systemAction = new Action();
            systemAction.setType("CORPORATE");
            systemAction.setName("合同签署");
            systemAction.setSerialNo(1L);
            systemAction.setSealId(qiyuesuoConfig.getCompanyContractSealId());
            systemAction.setAutoSign(true);
            List<SignatoryRect> locations = CollUtil.newArrayList();
            // 签署方为系统租户
            if (containsKeyword(fileBytes, qiyuesuoConfig.getSealKeyword().getContractSettleSeal())) {
                keywordSign = true;
                SignatoryRect signatoryRect = new SignatoryRect();
                signatoryRect.setDocumentId(qysDocumentId);
                signatoryRect.setKeyword(qiyuesuoConfig.getSealKeyword().getContractSettleSeal());
                signatoryRect.setKeywordIndex(0L);
                signatoryRect.setRelativePosition("CENTER");
                signatoryRect.setRectType(RectTypeEnum.SEAL_CORPORATE.name());
                locations.add(signatoryRect);
            } else {
                throw MithrasException.newException("合同文件[" + fileName + "]未包含合同印章关键字");
            }

            // 文件超过一页，盖骑缝章
            try {
                if (keywordSign && PDDocument.load(fileBytes).getNumberOfPages() > 1) {
                    SignatoryRect rect = new SignatoryRect();
                    rect.setRectType(RectTypeEnum.ACROSS_PAGE.name());
                    rect.setDocumentId(qysDocumentId);
                    rect.setOffsetY(0.5);
                    locations.add(rect);
                }
            } catch (IOException e) {
                log.error("判断PDF文件页数异常，骑缝章预设异常");
            }
            systemAction.setLocations(locations);
            actionList.add(systemAction);
        } else {
            // TODO 签署方为个人
            throw MithrasException.newException("个人签署暂不支持");
        }

        return Pair.of(needCorporateSign, actionList);
    }

    public static boolean containsKeyword(byte[] inputStream, String keyword) throws IOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).contains(keyword);
        } catch (Exception e) {
            log.error("判断文件是否包含关键字异常，关键字为：{}", keyword);
            return false;
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
}




