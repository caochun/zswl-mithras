package cn.zswltech.mithras.blackgray.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.excel.model.BlackGrayUploadModel;
import cn.zswltech.mithras.blackgray.excel.model.BlackGrayUploadTitleModel;
import cn.zswltech.mithras.blackgray.excel.model.BlackGrayWarehouseRecordExport;
import cn.zswltech.mithras.blackgray.dto.external.VagueEnterpriseSearchREQ;
import cn.zswltech.mithras.blackgray.dto.external.VagueEnterpriseSearchRSP;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBusinessTypeRsp;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRecordDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRecordListRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRecordUploadRSP;
import cn.zswltech.mithras.blackgray.enums.*;
import cn.zswltech.mithras.blackgray.excel.ExcelUtil;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRuleConfigMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRuleConfig;
import cn.zswltech.mithras.blackgray.service.BlackGrayExternalDataService;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRecordService;
import cn.zswltech.mithras.blackgray.service.GruulAuthService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserOrgResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 * @description 黑灰名单记录表
 * @date 2023-11-28
 */
@RestController
@Slf4j
@Api(tags = "黑灰名单记录表-接口")
public class BlackGrayWarehouseRecordController {

    @Resource
    private BlackGrayWarehouseRecordService blackGrayWarehouseRecordService;
    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;
    @Resource(name = "blackGrayHSExternalDataService")
    private BlackGrayExternalDataService blackGrayExternalDataService;
    @Resource
    private OrgService orgService;
    @Resource
    private BlackGrayWarehouseRuleConfigMapper blackGrayWarehouseRuleConfigMapper;
    @Resource
    GruulAuthService gruulAuthService;
    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;
    @Resource
    private UserNameResolver userNameResolver;

    @ApiOperation("新增黑灰名单记录表")
    @PostMapping("/black/gray/warehouse/record/add")
    public R<Long> add(@RequestBody @Valid BlackGrayWarehouseRecordAddREQ req) {
        return R.ok(blackGrayWarehouseRecordService.add(req));
    }

    @ApiOperation("批量新增黑灰名单记录表")
    @PostMapping("/black/gray/warehouse/record/batch/add")
    public R<Void> batchAdd(@RequestBody @Valid List<BlackGrayWarehouseRecordAddREQ> reqs) {
        blackGrayWarehouseRecordService.batchAdd(reqs);
        return R.ok();
    }

    @ApiOperation("批量补全黑灰名单集团信息")
    @PostMapping("/black/gray/warehouse/record/batch/modify")
    public R<Void> batchModify(@RequestBody @Valid BlackGrayWarehouseRecordSupplyREQ req) {
        blackGrayWarehouseRecordService.batchModify(req);
        return R.ok();
    }

    @ApiOperation("修改黑灰名单记录表")
    @PostMapping("/black/gray/warehouse/record/modify")
    public R<Void> modify(@RequestBody BlackGrayWarehouseRecordModifyREQ req) {
        blackGrayWarehouseRecordService.modify(req);
        return R.ok();
    }

    @ApiOperation("黑灰名单记录表列表")
    @PostMapping("/black/gray/warehouse/record/list")
    public R<PageR<BlackGrayWarehouseRecordListRSP>> list(@RequestBody BlackGrayWarehouseRecordListREQ req) {
        PageR<BlackGrayWarehouseRecordListRSP> list;
        if (ObjectUtil.equals(req.getIsStock(), 0)) {
            list = blackGrayLibraryService.recordStock(req);
        } else {
            list = blackGrayWarehouseRecordService.list(req);
        }
        return R.ok(list);
    }

    @ApiOperation("黑灰名单记录表详情")
    @PostMapping("/black/gray/warehouse/record/detail")
    public R<BlackGrayWarehouseRecordDetailRSP> detail(@RequestBody BlackGrayWarehouseRecordDetailREQ req) {
        return R.ok(blackGrayWarehouseRecordService.detail(req.getBlackGrayRecordId()));
    }

    @ApiOperation("黑灰名单记录表删除")
    @PostMapping("/black/gray/warehouse/record/delete")
    public R<Void> delete(@RequestBody BlackGrayWarehouseRecordRemoveREQ req) {
        blackGrayWarehouseRecordService.remove(req);
        return R.ok();
    }


    @ApiOperation("业务类型树")
    @PostMapping("/black/gray/business/type/list")
    public R<List<BlackGrayBusinessTypeRsp>> listBusinessType(@RequestBody BlackGrayBusinessTypeReq req) {
        return R.ok(blackGrayWarehouseRecordService.listBusinessType(req));
    }

    @ApiOperation("黑灰名单上传模版下载")
    @GetMapping("/black/gray/warehouse/record/upload/template/download")
    public void templateDownload(HttpServletResponse R) {
        R.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        try (OutputStream outputStream = R.getOutputStream()) {
            R.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            R.addHeader("Content-Type", "application/octet-stream");
            R.setHeader("Content-disposition", "attachment;filename="
                    + URLEncoder.encode("黑灰名单上传模版.xlsx", StandardCharsets.UTF_8.name()));

            List<BlackGrayWarehouseRuleConfig> blackGrayWarehouseRuleConfigs = blackGrayWarehouseRuleConfigMapper.selectList(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                    .eq(BlackGrayWarehouseRuleConfig::getStatus, 1)
                    .eq(BlackGrayWarehouseRuleConfig::getSource, BlackGraySourceEnum.INTERNAL_APPROVAL.name()));
            Map<BlackGrayBusinessTypeEnum, List<String>> blackGrayBusinessTypeEnumListMap = new HashMap<>();
            if (ObjectUtil.isNotEmpty(blackGrayWarehouseRuleConfigs) ) {
                //分类
                blackGrayWarehouseRuleConfigs.forEach(blackGrayWarehouseRuleConfig -> {
                    List<String> suitOrgList = JSONUtil.toList(blackGrayWarehouseRuleConfig.getSuitOrg(), String.class);
                    List<String> suitBusinessList = JSONUtil.toList(blackGrayWarehouseRuleConfig.getSuitBusiness(), String.class);
                    BlackGrayTypeEnum blackGrayTypeEnum = BlackGrayTypeEnum.of(blackGrayWarehouseRuleConfig.getBlackGrayType());
                    if (ObjectUtil.isNotEmpty(suitOrgList) && suitOrgList.contains(BlackGrayOrgEnum.ZSZL.getCode()) && blackGrayTypeEnum != null) {
                        if (ObjectUtil.isNotEmpty(suitBusinessList)) {
                            suitBusinessList.forEach(business -> {
                                BlackGrayBusinessTypeEnum blackGrayBusinessTypeEnum = BlackGrayBusinessTypeEnum.of(business);
                                if (blackGrayBusinessTypeEnum != null) {
                                    List<String> strings = blackGrayBusinessTypeEnumListMap.computeIfAbsent(blackGrayBusinessTypeEnum, k -> new ArrayList<>());
                                    strings.add(String.join("/", blackGrayBusinessTypeEnum.getDesc(), blackGrayTypeEnum.getDesc(), blackGrayWarehouseRuleConfig.getRuleName()));
                                }
                            });
                        }
                    }
                });
            }
            //
            ExcelUtil<BlackGrayUploadTitleModel> titleModelExcelUtil = new ExcelUtil<>(BlackGrayUploadTitleModel.class);
            for (BlackGrayBusinessTypeEnum value : BlackGrayBusinessTypeEnum.values()) {
                try {
                    List<String> strings = blackGrayBusinessTypeEnumListMap.get(value);
                    if (CollectionUtil.isEmpty(strings)) {
                        continue;
                    }
                    titleModelExcelUtil.exportExcelMultiSheets(null, value.getDesc(), R, titleModelExcelUtil.getWb(), false);
                    addColumnValidation(titleModelExcelUtil.getWb(), strings, value, value.getDesc());
                } catch (IOException e) {
                    log.error("黑灰名单上传模版下载出现异常", e);
                }
            }
            titleModelExcelUtil.getWb().write(outputStream);
        } catch (IOException e) {
            log.error("黑灰名单上传模版下载出现异常", e);
        }
    }

    private void addColumnValidation(Workbook workbook, List<String> columnValidationList, BlackGrayBusinessTypeEnum blackGrayBusinessTypeEnum, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        /////
        if (ObjectUtil.hasEmpty(workbook, columnValidationList, blackGrayBusinessTypeEnum, sheet)) {
            return;
        }
        //定义sheet的名称
        String hiddenName = "hidden" + "黑灰名单上传模版" + sheetName;
        // 创建一个隐藏的sheet 名称为 hidden
        Sheet hidden = workbook.getSheet(hiddenName);
        if (hidden == null) {
            hidden = workbook.createSheet(hiddenName);
        }
        DataValidationHelper helper = sheet.getDataValidationHelper();
        //循环赋值（为了防止下拉框的行数与隐藏域的行数相对应，将隐藏域加到结束行之后)
        for (int i = 0, length = columnValidationList.size(); i < length; i++) {
            //表示你开始的行数 3表示 你开始的列数
            hidden.createRow(2 + i).createCell(0).setCellValue(columnValidationList.get(i));
        }
        Name category1Name = workbook.createName();
        category1Name.setNameName(hiddenName);
        //4 A1:A代表隐藏域创建第N列createcell(N) 时。LXAI列开始A行数据获我下拉数组
        category1Name.setRefersToFormula(hiddenName + "!A1:A" + (columnValidationList.size() + 2));
        // 将刚才设置的sheet引用到下拉列表中
        DataValidationConstraint constraint8 = helper.createFormulaListConstraint(hiddenName + "!$A$2:$A$" + (columnValidationList.size() + 2));
        DataValidation dataValidation3 = helper.createValidation(constraint8, new CellRangeAddressList(1, 1000, 1, 1));
        sheet.addValidationData(dataValidation3);
        //设置hiddenSheet隐藏
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenName), true);
    }

    @ApiOperation("上传黑灰名单记录表")
    @PostMapping(value = "/black/gray/warehouse/record/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> upload(BlackGrayWarehouseRecordUploadREQ req) {
        Long userId = AccountUtil.getLoginInfo().getId();
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        List<OrgDO> deptDos = currentUserOrgResolver.getUserDeptList();
        if (ObjectUtil.isNull(rootOrg)) {
            throw new MithrasException("无机构信息");
        }

        String deptName = "";
        if (ObjectUtil.isNotEmpty(deptDos)) {
            deptName = deptDos.get(0).getCode();
        }
        try {
            Date date = new Date();
            List<BlackGrayUploadModel> blackGrayBatchQueryModels = EasyExcel.read(req.getFile().getInputStream())
                    .headRowNumber(1)
                    .head(BlackGrayUploadModel.class).sheet().doReadSync();
            BlackGrayTypeEnum blackGrayTypeEnum = Optional.ofNullable(BlackGrayTypeEnum.of(req.getBlackGrayType())).orElse(BlackGrayTypeEnum.BLACK_LIST);
            if (blackGrayBatchQueryModels != null) {
                List<BlackGrayUploadModel> addList = new ArrayList<>();
                List<BlackGrayWarehouseRecordUploadRSP.Body> errorList = new ArrayList<>();
                blackGrayBatchQueryModels.forEach(e -> checkUpdate(e, addList, errorList));
                if (!errorList.isEmpty()) {
                    List<String> collect = errorList.stream().map(e -> String.join("-", e.getEnterpriseName(), e.getUnifiedSocialCreditCode(), e.getErrorFields() == null ? "" : e.getErrorFields().toString())).collect(Collectors.toList());
                    throw new MithrasException(collect.toString());
                }
                List<BlackGrayLibrary> blackGrayLibrarys = new ArrayList<>();
                String finalDeptName = deptName;
                addList.forEach(blackGrayUploadModel -> {
                    BlackGrayLibrary black = BeanUtil.copyProperties(blackGrayUploadModel, BlackGrayLibrary.class, "reportFlag");
                    black.setBlackGrayType(blackGrayTypeEnum.name());
                    black.setBlackGrayTypeNum(BlackGrayTypeEnum.name2Num(black.getBlackGrayType()));
                    black.setBusinessType(Optional.ofNullable(BlackGrayBusinessTypeEnum.ofByDesc(black.getBusinessType())).map(BlackGrayBusinessTypeEnum::name).orElse(black.getBusinessType()));
                    black.setApplyOrganization(rootOrg.getName());
                    black.setApplyDept(finalDeptName);
                    black.setCreateTime(date);
                    black.setApplyTime(date);
                    black.setUpdateTime(date);
                    black.setCreateBy(userId);
                    black.setUpdateBy(userId);
                    black.setStockStatus(0);
                    black.setSource(req.getSource());
                    black.setReportFlag(ObjectUtil.equals("上报", blackGrayUploadModel.getReportFlag()) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
                    blackGrayLibrarys.add(black);
                });
                //插入记录表
                List<BlackGrayWarehouseRecord> blackGrayWarehouseRecords = BeanUtil.copyToList(blackGrayLibrarys, BlackGrayWarehouseRecord.class);
                blackGrayLibraryService.attemptBatchWarehouse(blackGrayLibrarys);
                blackGrayWarehouseRecords.forEach(blackGrayWarehouseRecord -> {
                    blackGrayWarehouseRecord.setAuditStatus((int) AuditStatusEnum.FINISH.getCode());
                });
                blackGrayWarehouseRecordService.saveBatch(blackGrayWarehouseRecords);
            }
        } catch (Exception e) {
            log.error("批量上传失败", e);
            throw new MithrasException("批量上传失败");
        }
        return R.ok();
    }

    @ApiOperation("解析黑灰名单上传记录表")
    @PostMapping(value = "/black/gray/warehouse/analysis/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<BlackGrayWarehouseRecordUploadRSP> analysisBatchRecord(BlackGrayWarehouseRecordUploadREQ req) {
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if (ObjectUtil.isNull(rootOrg)) {
            throw new MithrasException("无机构信息");
        }
        BlackGrayWarehouseRecordUploadRSP rsp = new BlackGrayWarehouseRecordUploadRSP();
        try {
            ExcelReaderBuilder read = EasyExcel.read(req.getFile().getInputStream()).headRowNumber(1)
                    .head(BlackGrayUploadModel.class);
            List<BlackGrayUploadModel> blackGrayBatchQueryModels = read.doReadAllSync();
            if (ObjectUtil.isEmpty(blackGrayBatchQueryModels)) {
                return R.ok(rsp);
            }
            List<BlackGrayUploadModel> addList = new ArrayList<>();
            List<BlackGrayWarehouseRecordUploadRSP.Body> errorList = new ArrayList<>();
            blackGrayBatchQueryModels.forEach(e -> checkUpdate(e, addList, errorList));
            rsp.setAddList(addList);
            rsp.setErrorList(errorList);
        } catch (ExcelDataConvertException dataConvertException) {
            log.error("批量上传失败 日期格式不对", dataConvertException);
            throw new MithrasException("日期格式");
        } catch (Exception e) {
            log.error("批量上传失败", e);
            throw new MithrasException("不支持该文件格式，请按照模板填写上传");
        }
        return R.ok(rsp);
    }

    @ApiOperation("黑灰名单导出")
    @GetMapping("/black/gray/warehouse/record/export")
    public void export(HttpServletResponse response, BlackGrayWarehouseRecordListREQ req) {
        if (req.getIds() == null) {
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
        }
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if (ObjectUtil.isNull(rootOrg)) {
            throw new MithrasException("无机构信息");
        }
        String rootOrgName = rootOrg.getName();
        List<BlackGrayWarehouseRecordListRSP> rsps = this.list(req).getData().getList();
        if (CollectionUtil.isNotEmpty(rsps)) {
            try {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentType("application/x-download");
                //[黑灰名单查询]_[所属部门]_[用户名]
                StringBuilder fileName = new StringBuilder();
                fileName.append("黑灰名单申请记录");
                fileName.append("_");
                fileName.append(rootOrgName);
                fileName.append("_");
                fileName.append(userNameResolver.sysUserId2NameSingle(AccountUtil.getLoginInfo().getId()));
                fileName.append(".xlsx");
                response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName.toString(), "UTF-8"));
                ExcelUtil<BlackGrayWarehouseRecordExport> excelUtil = new ExcelUtil<>(BlackGrayWarehouseRecordExport.class);
                List<BlackGrayWarehouseRecordExport> blackGrayWarehouseRecordExports = new ArrayList<>();
                rsps.forEach(rsp -> {
                    BlackGrayWarehouseRecordExport export = BeanUtil.copyProperties(rsp, BlackGrayWarehouseRecordExport.class);
                    export.setBlackGrayType(Optional.ofNullable(BlackGrayTypeEnum.of(rsp.getBlackGrayType())).map(BlackGrayTypeEnum::getDesc).orElse(rsp.getBlackGrayType()));
                    export.setGroupBlackGrayType(ObjectUtil.isNotEmpty(rsp.getGroupBlackGrayType()) ? Optional.ofNullable(BlackGrayTypeEnum.of(rsp.getGroupBlackGrayType())).map(BlackGrayTypeEnum::getDesc).orElse(rsp.getBlackGrayType()) : null);
                    export.setPeriodUnderObservation(Optional.ofNullable(BlackGrayTypeEnum.of(rsp.getBlackGrayType())).map(BlackGrayTypeEnum::getMonthName).orElse(null));
                    if (ObjectUtil.isNotEmpty(rsp.getApplyReasonName())) {
                        StringBuilder sb = new StringBuilder();
                        rsp.getApplyReasonName().forEach(name -> {
                            sb.append(name);
                            sb.append(",");
                        });
                        sb.deleteCharAt(sb.length() - 1);
                        export.setApplyReason(sb.toString());
                    }
                    export.setBusinessType(Optional.ofNullable(BlackGrayBusinessTypeEnum.of(rsp.getBusinessType())).map(BlackGrayBusinessTypeEnum::getDesc).orElse(rsp.getBusinessType()));
                    //export.setApplyOrganization(systemSupportService.getOrgName(rsp.getApplyOrganization()));
                    blackGrayWarehouseRecordExports.add(export);
                });
                excelUtil.exportExcel(blackGrayWarehouseRecordExports, "黑灰名单申请记录", response);
            } catch (IOException e) {
                log.error("黑灰名单名单错误");
            }
        }
    }

    private void checkUpdate(BlackGrayUploadModel blackGrayUploadModel, List<BlackGrayUploadModel> addList, List<BlackGrayWarehouseRecordUploadRSP.Body> errorList) {
        if(ObjectUtil.isEmpty(blackGrayUploadModel.getApplyReasonType()) || (ObjectUtil.isAllEmpty(blackGrayUploadModel.getApplyReasonType(), blackGrayUploadModel.getWarehouseTime()) && ObjectUtil.equals(blackGrayUploadModel.getEnterpriseName().split("/"), 3))){
            return;
        }
        //拆分建议禁止/受限制准入业务及原因
        String[] split = blackGrayUploadModel.getApplyReasonType().split("/");
        //有隐藏表单，过滤掉
        if(!ObjectUtil.equals(split.length, 3)){
            return;
        }
        blackGrayUploadModel.setBusinessType(split[0]);
        blackGrayUploadModel.setBlackGrayType(split[1]);
        blackGrayUploadModel.setApplyReasonType(split[2]);
        List<String> errorFields = new ArrayList<>();
        blackGrayUploadModel.setBlackGrayType(Optional.ofNullable(BlackGrayTypeEnum.ofName(blackGrayUploadModel.getBlackGrayType())).map(BlackGrayTypeEnum::name).orElse(null));
        blackGrayUploadModel.setBusinessType(Optional.ofNullable(BlackGrayBusinessTypeEnum.ofByDesc(blackGrayUploadModel.getBusinessType())).map(BlackGrayBusinessTypeEnum::name).orElse(null));
        //校验数据准确性
        VagueEnterpriseSearchREQ searchREQ = new VagueEnterpriseSearchREQ();
        searchREQ.setEnterpriseName(blackGrayUploadModel.getEnterpriseName());
        List<VagueEnterpriseSearchRSP> vagueEnterpriseSearchRSPS = blackGrayExternalDataService.vagueEnterpriseSearch(searchREQ);
        VagueEnterpriseSearchRSP vagueEnterprise = null;
        for (VagueEnterpriseSearchRSP vagueEnterpriseSearchRSP : vagueEnterpriseSearchRSPS) {
            if (ObjectUtil.equals(vagueEnterpriseSearchRSP.getEnterpriseName(), blackGrayUploadModel.getEnterpriseName())) {
                vagueEnterprise = vagueEnterpriseSearchRSP;
                blackGrayUploadModel.setUnifiedSocialCreditCode(vagueEnterprise.getUnifiedSocialCreditCode());
            }
        }
        //校验数据
        if (ObjectUtil.isEmpty(blackGrayUploadModel.getEnterpriseName()) || ObjectUtil.isEmpty(vagueEnterprise)) {
            errorFields.add("企业名称");
        }
        if (ObjectUtil.isEmpty(blackGrayUploadModel.getBlackGrayType())) {
            errorFields.add("黑灰标识");
        }
        if (ObjectUtil.isEmpty(blackGrayUploadModel.getUnifiedSocialCreditCode()) || vagueEnterprise == null || ObjectUtil.notEqual(vagueEnterprise.getUnifiedSocialCreditCode(), blackGrayUploadModel.getUnifiedSocialCreditCode())) {
            errorFields.add("统一社会信用代码");
        }
        if (ObjectUtil.isEmpty(blackGrayUploadModel.getBusinessType())) {
            errorFields.add("业务类型");
        }
        BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig = blackGrayWarehouseRuleConfigMapper.selectOne(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .like(BlackGrayWarehouseRuleConfig::getSuitBusiness, blackGrayUploadModel.getBusinessType())
                .eq(BlackGrayWarehouseRuleConfig::getBlackGrayType, blackGrayUploadModel.getBlackGrayType())
                .eq(BlackGrayWarehouseRuleConfig::getRuleName, blackGrayUploadModel.getApplyReasonType())
                .eq(BlackGrayWarehouseRuleConfig::getStatus, 1));
        if (blackGrayWarehouseRuleConfig == null) {
            errorFields.add("申请入库原因编码");
        } else {
            blackGrayUploadModel.setApplyReasonType(blackGrayWarehouseRuleConfig.getRuleNumber());
            blackGrayUploadModel.setApplyReasonName(blackGrayWarehouseRuleConfig.getRuleName());
            List<String> strings = JSONUtil.toList(blackGrayWarehouseRuleConfig.getSuitBusiness(), String.class);
            if (ObjectUtil.notEqual(blackGrayUploadModel.getBlackGrayType(), blackGrayWarehouseRuleConfig.getBlackGrayType()) || (strings != null && !strings.contains(blackGrayUploadModel.getBusinessType()))) {
                errorFields.add("申请入库原因编码");
            }
        }

        if (errorFields.isEmpty()) {
            //解析申请原因，构建层级
            addList.add(blackGrayUploadModel);
        } else {
            BlackGrayWarehouseRecordUploadRSP.Body body = new BlackGrayWarehouseRecordUploadRSP.Body();
            body.setEnterpriseName(blackGrayUploadModel.getEnterpriseName());
            body.setUnifiedSocialCreditCode(blackGrayUploadModel.getUnifiedSocialCreditCode());
            body.setErrorFields(errorFields);
            errorList.add(body);
        }
    }
}
