package cn.zswltech.mithras.datashare.service.impl;

import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.basic.Constant;
import cn.zswltech.mithras.dto.file.FileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.service.config.redis.RedisHelper;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.job.req.ArchivesReq;
import cn.zswltech.mithras.service.job.req.DetailReq;
import cn.zswltech.mithras.datashare.mapper.DataShareFkMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.datashare.mapper.model.DataShareFk;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.datashare.service.DataShareFkService;
import cn.zswltech.mithras.datashare.service.entity.AiResult;
import cn.zswltech.mithras.datashare.service.req.CQ2AttachmentSaveReq;
import cn.zswltech.mithras.datashare.service.rsp.CQ2AcchmentSaveRsp;
import cn.zswltech.mithras.datashare.service.util.CustomMultipartFile;
import cn.zswltech.mithras.datashare.service.util.HLYEnum;
import cn.zswltech.mithras.datashare.service.util.HLYHttpUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class DataShareFKServiceImpl extends ServiceImpl<DataShareFkMapper, DataShareFk> implements DataShareFkService {
    @Value("${hly.host}")
    private String host;
    @Value("${hly.clientId}")
    private String clientId;
    @Value("${hly.secret}")
    private String secret;
    @Value("${hly.fileBasePath}")
    private String fileBasePath;
    @Value("${hly.timeout}")
    private Integer timeout;
    @Value("${hly.address}")
    private String address;
    @Value("${server.port}")
    private String port;

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private RedisHelper redisHelper;

    private static String fileType = "xlsx";  // 文件类型
    private static Map<String, String> errorCodeMap = new HashMap<>();  // 错误码
    private static Map<String, String> reportAuditResultMap = new HashMap<>();  // 审核结果
    private static Map<String, String> aiAgentAuditResult = new HashMap<>();  // 	AI坐席审核结果

    static {
        errorCodeMap.put("0000", "成功");
        errorCodeMap.put("120003", "缺少必填项:{0}");
        errorCodeMap.put("129702", "单据不存在");
        errorCodeMap.put("130004", "该单据没有“AI坐席审核”审批历史");

        reportAuditResultMap.put("PASS", "通过");
        reportAuditResultMap.put("FAILED", "不通过");
        reportAuditResultMap.put("PENDING", "待人工复核");

        aiAgentAuditResult.put("PASS", "通过");
        aiAgentAuditResult.put("PARTIAL_PASS", "部分通过");
        aiAgentAuditResult.put("FAILED", "不通过");
        aiAgentAuditResult.put("HIDE", "无结果");
    }


    @Override
    /*获取已完成的报销单 列表*/
    public Set<String> sendToArchives(String lastModifyStartDate, String lastModifyEndDate) {
        ArchivesReq req = new ArchivesReq();
        req.setStatusList(Arrays.asList(1001, 1002, 1003, 1004, 1005, 1007, 1008, 1015));
        req.setLastModifyStartDate(lastModifyStartDate);
        req.setLastModifyEndDate(lastModifyEndDate);
        req.setCompanyOIDList(new HashSet<>());
        req.setDocCompanyOIDList(new HashSet<>());
//        req.setPrintFree(true);
        req.setApplicantEmployeeIdList(new HashSet<>());
        req.setReceiveStatusList(Arrays.asList(0, 1, 2, 3));
        req.setSendBillStatusList(Arrays.asList(0, 1, 2, 3));
        req.setSortDTOList(Arrays.asList(new ArchivesReq.sortDTO("lastModifyDate", "DESC")));
        req.setDateSearchField("lastSubmittedDate");
        req.setFormCodeList(Arrays.asList("CLBXD", "GRBXD")); // 指定查询差旅报销单 个人报销单
//        req.setCorporateFlag(true);
        req.setQueryIncludeArchived(true);

        String res = HLYHttpUtil.post(clientId, secret, host, HLYEnum.ARCHIVES.display(), req);
        Set<String> businessNos = JSON.parseObject(res, new TypeReference<Set<String>>() {
        });
        return businessNos;
    }

    @Override
    public void batchGetDetails(List<String> businessNoList) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (String businessNo : businessNoList) {
            // 业务编号下获取第二个接口报文 并将报文返回内容存入文件中 将文件地址记录到数据库中
            FileUploadRSP rsp = getDetailAndFile(businessNo, today);
            if (rsp == null) {
                continue;
            }
            DataShareFk dataShareFk = new DataShareFk();
            dataShareFk.setBusinessNo(businessNo);
            dataShareFk.setFileName(String.format("%s.%s", businessNo, fileType));
            dataShareFk.setFileUrl(rsp.getFileUrl());
            dataShareFk.setCreateBy(3L);
            dataShareFk.setUpdateBy(3L);
            save(dataShareFk);
        }
    }

    @Override
    public void sendCQ2AttachmentSave(DataShareFk dataShareFk) {
        /*CQ2AttachmentSaveHandle */
        String url = dataShareFk.getFileUrl();
        // 当前环境中苍穹有链路问题，无法直接访问miio，通过本系统中转  暴露下载接口给苍穹
        try {
            url = "http://" + address + ":" + port + "/miio/download?filename=" + URLEncoder.encode(dataShareFk.getFileName(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        CQ2AttachmentSaveReq req = new CQ2AttachmentSaveReq();
        if (dataShareFk.getBusinessNo().startsWith(Constant.YGBX)) {
            req.setCico_entity_number(Constant.ER_DAILYREIMBURSEBILL);
        } else {
            req.setCico_entity_number(Constant.ER_TRIPREIMBURSEBILL);
        }
        req.setCico_billno(dataShareFk.getBusinessNo());
        req.addEntry(dataShareFk.getFileName(), "." + fileType, url);
        PlatformApiHandler<CQ2AttachmentSaveReq, CQ2AcchmentSaveRsp> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_ATTACHMENT_SAVE);
        CQ2AcchmentSaveRsp rsp = platformApiHandler.execute(req);

        if (rsp == null || !rsp.getStatus()) {
            // 模拟测试
//            redisHelper.setStringObjectSecond(dataShareFk.getFileName(), dataShareFk.getFileName(), timeout * 60);
            dataShareFk.setStatus(Constant.CQSENDSTATUS_ERROR);
            dataShareFk.setRetryNum(dataShareFk.getRetryNum() + 1);
        } else {
            dataShareFk.setStatus(Constant.CQSENDSTATUS_SENDED);
            redisHelper.setStringObjectSecond(dataShareFk.getFileName(), dataShareFk.getFileName(), timeout * 60);  // 苍穹发送成功 redis中记录附件名称设定超时时间
        }
        this.updateById(dataShareFk);
    }

    @Override
    public void download(String filename, HttpServletResponse response) {
        // 白名单进来下载附件，通过文件名称去materials_list中查询miio中地址，然后下载给第三方
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getMaterialsType, Constant.ARCHIVES);
        query.in(MaterialsList::getBusinessType, Constant.ARCHIVE_STAFF, Constant.ARCHIVE_TRAVEL);
        query.eq(MaterialsList::getFilename, filename);
        query.orderByDesc(MaterialsList::getUpdateTime);
        query.last(StringUtil.mysqlLimitOne());
        MaterialsList material = materialsListService.getOne(query);

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = ossClient.downLoad(material.getOssFilename());
            // 设置响应头
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));

            outputStream = response.getOutputStream();

            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    /*解析JSON 并生成对应的excel结果文件*/
    private String getAiResultExcel(String businessNo, String content) {
        // 参数校验
        if (StringUtils.isBlank(content)) {
            return null;
        }

        JSONObject json = JSON.parseObject(content);
        String errorCode = json.getString("errorCode");
        if (!"0000".equals(errorCode)) {
            log.info("获取AI结果失败：{},{}", errorCode, json.getString("message"));
            return null;
        }

        JSONObject data = json.getJSONObject("data");
        JSONArray expenseReportLabels = data.getJSONArray("expenseReportLabels");

        StringBuffer summary = new StringBuffer();// 概要   单据规则校验通过/不通过，费用规则校验通过/部分通过/不通过（通过n，不通过m）
        summary.append("单据规则校验").append(aiAgentAuditResult.get(data.getString("aiAgentAuditReportResult")));
        summary.append("，费用规则校验").append(aiAgentAuditResult.get(data.getString("aiAgentAuditResult")));
        summary.append("(通过").append(data.getString("auditSuccessInvoiceCount"));
        summary.append("，不通过").append(data.getString("auditFailedInvoiceCount")).append(")");

        StringBuffer tip = new StringBuffer();
        for(int i = 0; i < expenseReportLabels.size(); i++) {
            JSONObject expenseReportLabel = expenseReportLabels.getJSONObject(i);
            tip.append(expenseReportLabel.getString("name")).append("，");
        }
        if (tip.length() > 0) { // 摘除最后的逗号
            tip.setLength(tip.length() - 1);
        }

        AiResult aiResult = new AiResult();
        aiResult.setBusinessNo(businessNo);
        aiResult.setReportAuditResult(reportAuditResultMap.get(data.getString("reportAuditResult")));
        aiResult.setSummary(summary.toString().replace("\n",""));
        aiResult.setApproveTime(getApproveTime(businessNo));
        aiResult.setDetail(data.getString("auditOpinion"));
        aiResult.setTip(tip.toString());

        return createExcel(aiResult);
    }

    private String getApproveTime(String businessNo) {
        String approveTime = "";
        String url = HLYEnum.AUDIT_RESULT_LIST.display()
                .concat("?businessCode=").concat(businessNo);
        String res = HLYHttpUtil.get(clientId, secret, host, url);

        JSONObject json = JSON.parseObject(res);
        if (json.getString("errorCode").equals("0000")) {
            JSONObject data = json.getJSONObject("data");
            JSONArray autoAuditResultDetailList = data.getJSONArray("autoAuditResultDetailList");
            if (autoAuditResultDetailList.size() > 0) {
                approveTime = autoAuditResultDetailList.getJSONObject(0).getString("aiReviewDate");
                approveTime = approveTime.replace("T"," ").replace("Z",""); // 去掉 T和时区
            }
        } else {
            log.info("获取审批时间失败：{}, {}", json.getString("errorCode"), json.getString("message"));
        }
        return approveTime;
    }

    private String createExcel(AiResult aiResult) {
        // 1. 创建工作簿和工作表
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("AI审核结果");

        // 2. 定义样式（边框,居左）
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setAlignment(HorizontalAlignment.LEFT);
        borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        borderStyle.setWrapText(true); // 关键：开启自动换行

        // 第1行
        Row row1 = sheet.createRow(0);
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("单据号");
        cell11.setCellStyle(borderStyle);

        Cell cell12 = row1.createCell(1);
        cell12.setCellValue(aiResult.getBusinessNo());
        cell12.setCellStyle(borderStyle);

        Cell cell13 = row1.createCell(2);
        cell13.setCellValue("AI审核结论");
        cell13.setCellStyle(borderStyle);

        Cell cell14 = row1.createCell(3);
        cell14.setCellValue(aiResult.getReportAuditResult());
        cell14.setCellStyle(borderStyle);

        // 第2行
        Row row2 = sheet.createRow(1);
        row2.setHeight((short) 1000);
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue("AI审核概要");
        cell21.setCellStyle(borderStyle);

        Cell cell22 = row2.createCell(1);
        cell22.setCellValue(aiResult.getSummary());
        cell22.setCellStyle(borderStyle);

        Cell cell23 = row2.createCell(2);
        cell23.setCellValue("AI审核时间");
        cell23.setCellStyle(borderStyle);

        Cell cell24 = row2.createCell(3);
        cell24.setCellValue(aiResult.getApproveTime());
        cell24.setCellStyle(borderStyle);

        // 第3行（合并B-D列）
        Row row3 = sheet.createRow(2);
        row3.setHeight((short) 3000);
        Cell cell31 = row3.createCell(0);
        cell31.setCellValue("AI详细信息");
        cell31.setCellStyle(borderStyle);

        Cell cell32 = row3.createCell(1);
        cell32.setCellValue(aiResult.getDetail());
        cell32.setCellStyle(borderStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 3)); // 行3，列B-D

        // 第4行（提示情况，合并B-D列）
        Row row4 = sheet.createRow(3);
        row4.setHeight((short) 1000);
        Cell cell41 = row4.createCell(0);
        cell41.setCellValue("提示情况");
        cell41.setCellStyle(borderStyle);

        Cell cell42 = row4.createCell(1);
        cell42.setCellValue(aiResult.getTip());
        cell42.setCellStyle(borderStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 3)); // 行4，列B-D

        // 4. 调整列宽
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 12000);
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 6000);

        for (int rowNum = 0; rowNum <= 3; rowNum++) { // 补充边框
            Row row = sheet.getRow(rowNum);
            if (row == null) row = sheet.createRow(rowNum);
            for (int col = 0; col < 4; col++) {
                Cell cell = row.getCell(col);
                if (cell == null) {
                    cell = row.createCell(col);
                    cell.setCellStyle(borderStyle);
                }
            }
        }
//        row3.setHeight((short) -1); // -1 代表自动计算行高
//        sheet.getRow(2).setHeight((short) -1);  //强制让Excel重新计算行高

        // 5. 输出到文件
        FileOutputStream fos = null;
        try {
            Path filePath = Paths.get(fileBasePath, String.format("%s.%s", aiResult.getBusinessNo(), fileType));
            fos = new FileOutputStream(filePath.toFile());
            workbook.write(fos);
            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private FileUploadRSP getDetailAndFile(String businessNo, String today) {
        // /api/open/expenseReport/auto/audit/result/detail?businessCode={businessCode}&corporateFlag={corporateFlag}&entityType={entityType}
        DetailReq req = new DetailReq(businessNo, null, null, null);
        String url = HLYEnum.AUDIT_RESULT_DETAIL.display()
                .concat("?businessCode=").concat(req.getBusinessCode());
        String res = HLYHttpUtil.get(clientId, secret, host, url);
        String filePath = getAiResultExcel(businessNo, res);
        if (filePath == null) {
            return null;
        }

        /*文件传入文件系统中， 后续通过url方式给到苍穹系统*/
        CustomMultipartFile file = null;
        try {
            File jsonFile = new File(filePath);
            file = new CustomMultipartFile(jsonFile);

            // 将这个文件上传到minio
            FileUploadREQ uploadReq = new FileUploadREQ();
            uploadReq.setFile(file);
            uploadReq.setMainId(Long.valueOf(today));
            uploadReq.setMaterialsType(Constant.ARCHIVES); // 报销单
            if (businessNo.startsWith(Constant.YGBX)) {
                uploadReq.setModuleType(Constant.ARCHIVE_STAFF); // 员工报销单
            } else {
                uploadReq.setModuleType(Constant.ARCHIVE_TRAVEL); // 差旅报销单
            }
            FileUploadRSP rsp = upload(uploadReq);

            jsonFile.delete(); // 本地不保留文件
            return rsp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public FileUploadRSP upload(FileUploadREQ fileUploadREQ) {
        try {
            InputStream inputStream = fileUploadREQ.getFile().getInputStream();

            String originalFilename = fileUploadREQ.getFile().getOriginalFilename();
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

            FileUploadRSP rsp = new FileUploadRSP();
            String url = "http://" + address + ":" + port + "/miio/download?filename=" + URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.name());
            rsp.setFileUrl(url);
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


    /**
     * 将字符串内容写入文件
     *
     * @param businessNo   业务编号（用于生成文件名）
     * @param content      要写入的内容
     * @param fileBasePath 文件基础路径（建议改为配置项）
     */
    @Deprecated
    private String stringToFile(String businessNo, String content, String fileBasePath, String today) {
        // 参数校验
        if (StringUtils.isBlank(content)) {
            return null;
        }
        Path filePath = Paths.get(fileBasePath, businessNo + ".json");
        try {
            // 自动创建父目录
            Files.createDirectories(filePath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                writer.write(content);
            }
            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to write file, businessNo: {}, path: {}", businessNo, filePath, e);
            throw new RuntimeException("Failed to write file", e);
        }
    }
}

