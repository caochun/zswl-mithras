package cn.zswltech.mithras.service.service.creditreport.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportMaterialSubTypeEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportMaterialTypeEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditSearchStatusEnum;
import cn.zswltech.mithras.service.enums.creditreport.SearchGoalEnum;
import cn.zswltech.mithras.service.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportBaseInfo;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportClientItem;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.creditreport.CreditReportApiService;
import cn.zswltech.mithras.service.service.creditreport.CreditReportBaseInfoService;
import cn.zswltech.mithras.service.service.creditreport.CreditReportClientItemService;
import cn.zswltech.mithras.service.service.creditreport.CreditReportConfigService;
import cn.zswltech.mithras.service.service.creditreport.handle.CreditReportObtainResultJSONHandle;
import cn.zswltech.mithras.service.service.creditreport.handle.CreditReportObtainResultPDFHandle;
import cn.zswltech.mithras.service.service.creditreport.handle.CreditReportQueryEntFourEleAuthHandle;
import cn.zswltech.mithras.service.service.creditreport.handle.CreditReportQueryReportHandle;
import cn.zswltech.mithras.service.service.creditreport.req.CreditReportObtainResultJSONReq;
import cn.zswltech.mithras.service.service.creditreport.req.CreditReportObtainResultPDFReq;
import cn.zswltech.mithras.service.service.creditreport.req.CreditReportQueryEntFourEleAuthReq;
import cn.zswltech.mithras.service.service.creditreport.req.CreditReportQueryReportReq;
import cn.zswltech.mithras.service.service.creditreport.resp.CreditReportObtainResultJSONResp;
import cn.zswltech.mithras.service.service.creditreport.resp.CreditReportObtainResultPDFResp;
import cn.zswltech.mithras.service.service.creditreport.resp.CreditReportQueryEntFourEleAuthResp;
import cn.zswltech.mithras.service.service.creditreport.resp.CreditReportQueryReportResp;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CreditReportApiXJServiceImpl implements CreditReportApiService {

    @Resource
    private CreditReportClientItemService creditReportClientItemService;
    @Resource
    private CreditReportBaseInfoService creditReportBaseInfoService;
    @Resource
    private CreditReportQueryEntFourEleAuthHandle creditReportQueryEntFourEleAuthHandle;
    @Resource
    private CreditReportQueryReportHandle creditReportQueryReportHandle;
    @Resource
    private CreditReportObtainResultJSONHandle creditReportObtainResultJSONHandle;
    @Resource
    private CreditReportObtainResultPDFHandle creditReportObtainResultPDFHandle;
    @Resource
    private CreditReportConfigService creditReportConfigService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;

    //生产20
    private final static String CERT_TYPE = "20";

    private final static String PDF = "PDF";
    private final static String FILE_NAME = "征信报告";



    //新建档案
    @Transactional(rollbackFor = Throwable.class)
    public String addArchive(Long creditReportId) {
        CreditReportClientItem creditReportDO = creditReportClientItemService.getById(creditReportId);
        if (ObjectUtil.isEmpty(creditReportDO)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportBaseInfo baseInfo = creditReportBaseInfoService.getById(creditReportDO.getCreditReportBaseInfoId());
        //查询文件
        List<MaterialsList> materialsLists = materialsListService.list(BusinessModuleEnum.CREDIT_REPORT_SELECT.name(), ListUtil.toList(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT.name()), ListUtil.toList(creditReportDO.getId()));
        if (ObjectUtil.isEmpty(materialsLists)) {
            log.warn("CreditReportApiXJSeriveImpl addArchive file is null {}", creditReportId);
            return null;
        }
        Map<String, MaterialsList> subType2file = materialsLists.stream().collect(Collectors.toMap(MaterialsList::getMaterialSubType, e -> e, (a, b) -> b));
        //CreditReportMaterialSubTypeEnum 个类型资料
        CreditReportQueryEntFourEleAuthResp execute = null;
        CreditReportQueryEntFourEleAuthReq req = new CreditReportQueryEntFourEleAuthReq();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
            req.setEntName(creditReportDO.getClientName());
            req.setEntCertType(CERT_TYPE);
            req.setEntCertNum(creditReportDO.getCscCode());
            req.setAuthStartDate(baseInfo.getAuthorizationBeganDate() == null ? null : baseInfo.getAuthorizationBeganDate().format(formatter));
            req.setAuthExpiryDate(baseInfo.getAuthorizationEndDate() == null ? null : baseInfo.getAuthorizationEndDate().format(formatter));
            req.setSignature(creditReportConfigService.getSignature());
            req.setBusinessLicenseCopy(getOssFile(subType2file.get(CreditReportMaterialSubTypeEnum.BUSINESS_LICENSE.name())));
            req.setLegalIdCardFront(getOssFile(subType2file.get(CreditReportMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_ENTERPRISE.name())));
            req.setLegalIdCardBack(getOssFile(subType2file.get(CreditReportMaterialSubTypeEnum.LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_ENTERPRISE.name())));
            req.setLegalAuthorize(getOssFile(subType2file.get(CreditReportMaterialSubTypeEnum.CREDIT_LETTER.name())));
            execute = creditReportQueryEntFourEleAuthHandle.execute(req);
        } catch (Exception e) {
            log.warn("CreditReportApiXJSeriveImpl addArchive error {}", creditReportId, e);
            creditReportDO.setSelectStatus(CreditSearchStatusEnum.FAIL.name());
        } finally {
            creditReportDO.setSelectStatus(creditReportQueryEntFourEleAuthHandle.isExecuteSuccess(execute) ? CreditSearchStatusEnum.SUCCESS.name() : CreditSearchStatusEnum.FAIL.name());
            if (ObjectUtil.isNotEmpty(execute)) {
                creditReportDO.setSelectErrorCode(execute.getStatuscode());
                creditReportDO.setSelectErrorCode(execute.getError());
                creditReportDO.setArchiveId(execute.getArchiveId());
            }
            creditReportClientItemService.updateById(creditReportDO);
            creditReportBaseInfoService.modifySelectStatus(creditReportDO.getCreditReportBaseInfoId());
            //删除文件
            deleteOnExit(req.getBusinessLicenseCopy());
            deleteOnExit(req.getLegalIdCardFront());
            deleteOnExit(req.getLegalIdCardBack());
            deleteOnExit(req.getLegalAuthorize());
        }
        return execute == null ? null : execute.getArchiveId();
    }

    private void deleteOnExit(File file) {
        if (ObjectUtil.isEmpty(file)) {
            return;
        }
        file.deleteOnExit();
    }

    private File getOssFile(MaterialsList materialsList) {
        if (ObjectUtil.isEmpty(materialsList)) {
            return null;
        }
        InputStream inputStream = ossClient.downLoad(materialsList.getOssFilename());
        try {
            String[] split = materialsList.getFilename().split("\\.");

            Path tempFile =  Files.createTempFile(split[0], "." + split[1]);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile.toFile();
        } catch (Exception e) {
            log.warn("征信注册生成文件异常{}", materialsList, e);
        }
        return null;
    }


    //单笔查询
    @Transactional(rollbackFor = Throwable.class)
    public String queryReport(Long creditReportId) {
        CreditReportClientItem creditReportDO = creditReportClientItemService.getById(creditReportId);
        if (ObjectUtil.isEmpty(creditReportDO)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportQueryReportResp execute = null;
        try {
            CreditReportQueryReportReq req = new CreditReportQueryReportReq();
            req.setEntName(creditReportDO.getClientName());
            req.setEntCertType(CERT_TYPE);
            req.setEntCertNum(creditReportDO.getCscCode());
            req.setQueryReason(Optional.ofNullable(SearchGoalEnum.finaByName(creditReportDO.getSelectGoal())).map(SearchGoalEnum::getXjCode).orElse(SearchGoalEnum.PRE_INSURANCE_REVIEW.getXjCode()));
            req.setSignature(creditReportConfigService.getSignature());
            req.setQryStrategy("2");
            req.setArchiveId(creditReportDO.getArchiveId());
            execute = creditReportQueryReportHandle.execute(req);
        } catch (Exception e) {
            log.warn("CreditReportApiXJSeriveImpl queryReport error {}", creditReportId, e);
            creditReportDO.setSelectStatus(CreditSearchStatusEnum.FAIL.name());
        } finally {
            creditReportDO.setSelectStatus(creditReportQueryReportHandle.isExecuteSuccess(execute) ? CreditSearchStatusEnum.SEARCHING.name() : CreditSearchStatusEnum.FAIL.name());
            if (ObjectUtil.isNotEmpty(execute)) {
                creditReportDO.setSelectErrorCode(execute.getStatuscode());
                creditReportDO.setSelectErrorCode(execute.getError());
                creditReportDO.setSerialNumber(execute.getSerialnumber());
            }
            creditReportClientItemService.updateById(creditReportDO);
            creditReportBaseInfoService.modifySelectStatus(creditReportDO.getCreditReportBaseInfoId());
        }
        return execute == null ? null : execute.getSerialnumber();
    }

    //查询json
    @Transactional(rollbackFor = Throwable.class)
    public XJCreditReportJsonDTO resultJSON(Long creditReportId) {
        CreditReportClientItem creditReportDO = creditReportClientItemService.getById(creditReportId);
        if (ObjectUtil.isEmpty(creditReportDO)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportObtainResultJSONResp execute = null;
        try {
            if (ObjectUtil.isEmpty(creditReportDO.getSerialNumber())) {
                log.warn("CreditReportApiXJSeriveImpl resultJSON 无应用交易流水号，不进行查询 {}", creditReportDO);
            }
            CreditReportObtainResultJSONReq req = new CreditReportObtainResultJSONReq();
            req.setSerialnumber(creditReportDO.getSerialNumber());
            req.setSignature(creditReportConfigService.getSignature());
            execute = creditReportObtainResultJSONHandle.execute(req);
        } catch (Exception e) {
            log.warn("CreditReportApiXJSeriveImpl addArchive resultJSON {}", creditReportId, e);
            //creditReportDO.setSelectStatus(CreditSearchStatusEnum.FAIL.name());
        } finally {
           /* if (creditReportObtainResultJSONHandle.isExecuteSuccess(execute)) {
                creditReportDO.setSelectStatus(CreditSearchStatusEnum.SUCCESS.name());
            }*/
            if (ObjectUtil.isNotEmpty(execute)) {
                creditReportDO.setSelectErrorCode(execute.getStatuscode());
                creditReportDO.setSelectErrorCode(execute.getError());
            }
            creditReportClientItemService.updateById(creditReportDO);
            creditReportBaseInfoService.modifySelectStatus(creditReportDO.getCreditReportBaseInfoId());
        }
        if (ObjectUtil.isEmpty(execute) || ObjectUtil.isEmpty(execute.getJson())) {
            return null;
        }
        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        XJCreditReportJsonDTO xjCreditReportJsonDTO = JSONUtil.toBean(new String(decoder.decode(decoder.decode(execute.getJson().getBytes(StandardCharsets.UTF_8)))), XJCreditReportJsonDTO.class);
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument())){
            xjCreditReportJsonDTO.setDocument(xjCreditReportJsonDTO.getDOCUMENT());
        }
        return xjCreditReportJsonDTO;
    }


    //查询json
    @Transactional(rollbackFor = Throwable.class)
    public CreditReportObtainResultPDFResp resultPDF(Long creditReportId) {
        CreditReportClientItem creditReportDO = creditReportClientItemService.getById(creditReportId);
        if (ObjectUtil.isEmpty(creditReportDO)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportObtainResultPDFResp execute = null;
        try {
            if (ObjectUtil.isEmpty(creditReportDO.getSerialNumber())) {
                log.warn("CreditReportApiXJSeriveImpl resultJSON 无应用交易流水号，不进行查询 {}", creditReportDO);
            }
            CreditReportObtainResultPDFReq req = new CreditReportObtainResultPDFReq();
            req.setSerialnumber(creditReportDO.getSerialNumber());
            req.setSignature(creditReportConfigService.getSignature());
            execute = creditReportObtainResultPDFHandle.execute(req);
        } catch (Exception e) {
            log.warn("CreditReportApiXJSeriveImpl addArchive resultPDF {}", creditReportId, e);
            //creditReportDO.setSelectStatus(CreditSearchStatusEnum.FAIL.name());
        } finally {
            /*if (creditReportObtainResultPDFHandle.isExecuteSuccess(execute)) {
                creditReportDO.setSelectStatus(CreditSearchStatusEnum.SUCCESS.name());
            }*/
            if (ObjectUtil.isNotEmpty(execute)) {
                creditReportDO.setSelectErrorCode(execute.getStatuscode());
                creditReportDO.setSelectErrorCode(execute.getError());
                //转pdf
                execute.setMultipartFile(convertBase64ToMultipartFile(execute.getPdf()));
            }
            creditReportClientItemService.updateById(creditReportDO);
            creditReportBaseInfoService.modifySelectStatus(creditReportDO.getCreditReportBaseInfoId());
        }

        return execute;
    }

    public MultipartFile convertBase64ToMultipartFile(String base64String)  {
        if (ObjectUtil.isEmpty(base64String)) {
            return null;
        }
        try {
            // 处理可能存在的 Base64 前缀（如 data:application/pdf;base64,）
            Base64.Decoder decoder = Base64.getDecoder();
            // Base64 解码
            byte[] fileBytes = decoder.decode(decoder.decode(base64String));

            // 创建 MultipartFile（使用 Spring 的 MockMultipartFile）
            return new MockMultipartFile(
                    FILE_NAME,                     // 参数名（可自定义）
                    FILE_NAME + "." + PDF,                   // 文件名
                    "application/octet-stream", // 默认 ContentType（可根据需要修改）
                    new ByteArrayInputStream(fileBytes) // 文件字节流
            );
        } catch (Exception e) {
            log.warn("转化pdf失败", e);
        }
        return null;
    }


}
