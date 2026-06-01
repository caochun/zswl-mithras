package cn.zswltech.mithras.blackgray.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.excel.BlackGrayBaseInfoRecordExport;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.*;
import cn.zswltech.mithras.blackgray.enums.BlackGrayBusinessTypeEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.excel.ExcelUtil;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import cn.zswltech.mithras.blackgray.service.GruulAuthService;
import cn.zswltech.mithras.blackgray.utils.BlackDesensitizeUtil;
import cn.zswltech.mithras.blackgray.vo.BlackGrayLibraryDistinctExportVO;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.CurrentUserOrgResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description 黑灰名单库 这里不能直接使用
* @author
* @date 2023-11-28
*/
@Api(tags = "黑灰名单库-查询接口")
@RestController
@Slf4j
public class BlackGrayQueryController {

    @Resource
    private BlackGrayLibraryService blackGrayLibraryService;

    @Resource
    private OrgService orgService;
    @Resource
    GruulAuthService gruulAuthService;
    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;

    /*@ApiOperation("新增黑灰名单库")
    @PostMapping("/black/gray/base/info/add")
    public Response<Void> add(@RequestBody BlackgrayBaseInfoAddREQ req) {
        blackgrayBaseInfoService.add(req);
        return Response.success();
    }

    @ApiOperation("修改黑灰名单库")
    @PostMapping("/black/gray/base/info/modify")
    public Response<Void> modify(@RequestBody BlackgrayBaseInfoModifyREQ req){
        blackgrayBaseInfoService.modify(req);
        return Response.success();
    }*/

    @ApiOperation("风控系统查询黑灰名单库列表")
    @PostMapping("/black/gray/base/info/list")
    public R<PageR<BlackGrayLibraryListRSP>> list(@RequestBody @Validated BlackGrayLibraryListREQ req){
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if(ObjectUtil.isNull(rootOrg)){
            throw new MithrasException("无机构信息");
        }

        PageR<BlackGrayLibraryListRSP> list = blackGrayLibraryService.list(req);
        //脱敏 降级
        /*if(ObjectUtil.isNotEmpty(list) && !ThreadContext.getUser().getIfJinkongAccount()){
            list.getList().forEach(black -> {
                if(!rootOrg.getCode().equals(black.getApplyOrganization())){
                    BlackDesensitizeUtil.desensitize(black);
                    if(ObjectUtil.equals(0, black.getShareType())){
                        //降级
                        black.setBlackGrayType(BlackGrayTypeEnum.GRAY_LIST.name());
                    }
                }
            });
        }*/
        return R.ok(list);
    }

    @ApiOperation("黑灰名单综合查询导出")
    @GetMapping("/black/gray/base/info/export")
    public void export(HttpServletResponse response, BlackGrayLibraryListREQ req) {
        if(req.getIds() == null){
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
        }
        List<BlackGrayLibraryListRSP> list = this.list(req).getData().getList();
        if(CollectionUtil.isNotEmpty(list)){
            try {
                List<BlackGrayBaseInfoRecordExport> blackGrayWarehouseRecordExports = BeanUtil.copyToList(list, BlackGrayBaseInfoRecordExport.class);
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentType("application/octet-stream");
                //[黑灰名单查询]_[所属部门]_[用户名]
                String fileName = "黑灰名单综合查询记录.xlsx";
                response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                ExcelUtil<BlackGrayBaseInfoRecordExport> excelUtil = new ExcelUtil<>(BlackGrayBaseInfoRecordExport.class);
                List<String> orgCodes = blackGrayWarehouseRecordExports.stream().map(BlackGrayBaseInfoRecordExport::getApplyOrganization).distinct().collect(Collectors.toList());
                Map<String, String> orgNameMap = gruulAuthService.batchGetOrgNameMap(orgCodes);

                //转换标识
                blackGrayWarehouseRecordExports.forEach(export -> {
                    export.setBlackGrayType(Optional.ofNullable(BlackGrayTypeEnum.of(export.getBlackGrayType())).map(BlackGrayTypeEnum::getDesc).orElse(export.getBlackGrayType()));
                    export.setBusinessType(Optional.ofNullable(BlackGrayBusinessTypeEnum.of(export.getBusinessType())).map(BlackGrayBusinessTypeEnum::getDesc).orElse(export.getBusinessType()));
                    export.setGroupBlackGrayType(ObjectUtil.isNotEmpty(export.getGroupBlackGrayType()) ? Optional.ofNullable(BlackGrayTypeEnum.of(export.getGroupBlackGrayType())).map(BlackGrayTypeEnum::getDesc).orElse(export.getBlackGrayType()) : null);
                    export.setSource(Optional.ofNullable(BlackGraySourceEnum.of(export.getSource())).map(BlackGraySourceEnum::getDesc).orElse(export.getSource()));
                    if (StringUtils.isNotBlank(export.getApplyOrganization()) || orgNameMap.containsKey(export.getApplyOrganization())) {
                        export.setApplyOrganization(orgNameMap.get(export.getApplyOrganization()));
                    }

                });
                excelUtil.exportExcel(blackGrayWarehouseRecordExports,"黑灰名单综合查询记录",response);
            } catch (IOException e) {
                log.error("黑灰名单名单错误");
            }
        }
    }

    @ApiOperation("风控系统查询黑灰名单库按企业汇总列表")
    @PostMapping("/black/gray/base/info/distinct")
    public R<PageR<BlackGrayLibraryDistinctListRSP>> distinct(@RequestBody BlackGrayLibraryDistinctListREQ req) {
        return R.ok(blackGrayLibraryService.distinctList(req, true));
    }

    @ApiOperation("查询黑灰名单库-客户最严重记录")
    @PostMapping("/black/gray/base/info/library")
    public R<BlackGrayLibraryRSP> libraryRecord(@RequestBody BlackGrayLibraryREQ req) {
        return R.ok(blackGrayLibraryService.libraryRecord(req));
    }

    @ApiOperation("风控系统查询黑灰名单库按企业汇总列表导出")
    @GetMapping("/black/gray/base/info/distinct/export")
    public R<Void> distinctExport(BlackGrayLibraryDistinctListREQ req, HttpServletResponse response) {
        PageR<BlackGrayLibraryDistinctListRSP> page = blackGrayLibraryService.distinctList(req, false);
        List<BlackGrayLibraryDistinctListRSP> list = page.getList();
        if (CollectionUtil.isNotEmpty(list)) {
            List<BlackGrayLibraryDistinctExportVO> exportList = list.stream().map(e -> {
                BlackGrayLibraryDistinctExportVO exportVO = new BlackGrayLibraryDistinctExportVO();
                BeanUtils.copyProperties(e, exportVO);
                exportVO.setBlackGrayType(Optional.ofNullable(BlackGrayTypeEnum.of(e.getBlackGrayType())).map(BlackGrayTypeEnum::getDesc).orElse(null));
                return exportVO;
            }).collect(Collectors.toList());
            try {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentType("application/octet-stream");
                response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("全量名单查询-按企业汇总.xlsx", "UTF-8"));
                ExcelUtil<BlackGrayLibraryDistinctExportVO> excelUtil = new ExcelUtil<>(BlackGrayLibraryDistinctExportVO.class);
                excelUtil.exportExcel(exportList, "黑灰名单综合查询记录", response);
                return null;
            } catch (Exception e) {
                log.error("distinctExport error", e);
                return R.fail("导出失败");
            }
        } else {
            return R.fail("未查询到数据");
        }
    }

    @ApiOperation("风控系统查询黑灰名单库机构下列表")
    @PostMapping("/black/gray/org/list")
    public R<PageR<BlackGrayLibraryOrgListRSP>> orgList(@RequestBody BlackGrayLibraryListREQ req){
        PageR<BlackGrayLibraryOrgListRSP> list = blackGrayLibraryService.orgList(req);
        return R.ok(list);
    }

    @ApiOperation("风控系统查询黑灰名单库详情")
    @PostMapping("/black/gray/base/info/detail")
    public R<BlackGrayLibraryDetailRSP> detail(@RequestBody BlackGrayLibraryDetailREQ req){
        BlackGrayLibraryDetailRSP detail = blackGrayLibraryService.detail(req.getId());
        //脱敏
        /*if(!ThreadContext.getUser().getIfJinkongAccount()){
           BlackDesensitizeUtil.desensitize(detail);
        }*/
        return R.ok(detail);
    }


    @ApiOperation("风控系统查询可突破黑灰名单类型")
    @PostMapping("/black/gray/can/break/business")
    public R<List<BlackGrayCanBreakBusinessRSP>> getCanBreakBusiness(@RequestBody BlackGrayCanBreakREQ req){
        return R.ok(blackGrayLibraryService.getCanBreakBusiness(req));
    }

    @ApiOperation("风控系统查询风险规模")
    @PostMapping("/black/gray/enterprise/riskScale")
    public R<BlackGrayEnterpriseRiskScaleRSP> getEnterpriseRiskScale(@RequestBody BlackGrayEnterpriseRiskScaleREQ req){
        return R.ok(blackGrayLibraryService.getEnterpriseRiskScale(req));
    }

    @ApiOperation("批量查询下载模版")
    @GetMapping("/black/gray/batch/query/template/download")
    public void templateDownload(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        try (OutputStream outputStream = response.getOutputStream()) {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename="
                    + URLEncoder.encode("黑灰名单批量查询模版.xlsx", StandardCharsets.UTF_8.name()));
            ClassPathResource templateResource = new ClassPathResource("template/blackgray/黑灰名单批量查询模版.xlsx");
            IOUtils.copy(templateResource.getInputStream(), outputStream);
        } catch (IOException e) {
            log.error("黑灰名单批量查询模板下载出现异常", e);
        }
    }

    @ApiOperation("单一企业tab中展示类别过滤")
    @GetMapping("/black/gray/singleEnt/businessType")
    public R<List<String>> businessType (){
        // 根据用户所有所属机构身份，返回能显示出来的类别 对应BlackGrayBusinessTypeEnum的name()
        return R.ok(blackGrayLibraryService.businessType(currentUserOrgResolver.getUserDeptList().stream().map(OrgDO::getCode).collect(Collectors.toList())));
    }

    @ApiOperation("批量查询")
    @PostMapping("/black/gray/batch/batch/query")
    public R<List<BlackGrayLibraryListRSP>> batchQuery(BlackGrayBatchQueryFileREQ req) {
        return R.ok(blackGrayLibraryService.batchQuery(req));
    }




    /*

    @ApiOperation("单一企业tab中黑灰名单数量统计")
    @GetMapping("/black/gray/singleEnt/cardCount")
    public R<List<BlackGrayLibCountDTO>> cardCount(@RequestParam(required = false) String businessType){
        // 根据用户所有所属机构身份，返回能显示出来的类别 对应BlackGrayBusinessTypeEnum的name()
        return R.ok(blackGrayLibraryService.blackGrayCount(ThreadContext.getOrgCodeList(), businessType));
    }

    @ApiOperation("单一企业tab中黑灰名单数量统计")
    @GetMapping("/black/gray/singleEnt/cardCount")
    public Response<List<BlackGrayLibCountDTO>> cardCount(@RequestParam(required = false) String businessType){
        // 根据用户所有所属机构身份，返回能显示出来的类别 对应BlackGrayBusinessTypeEnum的name()
        return Response.success(blackGrayLibraryService.blackGrayCount(ThreadContext.getOrgCodeList(), businessType));
    }

    @ApiModelProperty("单一集团tab列表")
    @GetMapping("/black/gray/singleGroup/list")
    public Response<BasePage<BlackGrayGroupListRSP>> blackGrayGroupList(BlackGrayGroupListREQ req) {
        return Response.success(blackGrayLibraryService.blackGrayGroupList(req, true));
    }

    @ApiModelProperty("单一集团详情页-集团信息")
    @GetMapping("/black/gray/singleGroup/detail")
    public Response<BlackGrayGroupDetailRSP> blackGrayGroupDetail(@RequestParam String groupName) {
        return Response.success(blackGrayLibraryService.blackGrayGroupDetail(groupName));
    }

    @ApiModelProperty("单一集团详情页-集团主企业在库明细")
    @GetMapping("/black/gray/singleGroup/groupStockList")
    public Response<BasePage<GroupInStockListRSP>> groupStockList(GroupStockListREQ req) {
        return Response.success(blackGrayLibraryService.groupStockList(req, true));
    }

    @ApiModelProperty("单一集团详情页-集团下属企业在库明细")
    @GetMapping("/black/gray/singleGroup/groupCompanyStockList")
    public Response<BasePage<BlackGrayLibraryDistinctListRSP>> groupCompanyStockList(GroupStockListREQ req) {
        return Response.success(blackGrayLibraryService.groupCompanyStockList(req, true));
    }

    @ApiModelProperty("单一集团tab导出")
    @GetMapping("/black/gray/singleGroup/export")
    public Response<BasePage<BlackGrayGroupListRSP>> blackGrayGroupExport(BlackGrayGroupListREQ req, HttpServletResponse response) {
        BasePage<BlackGrayGroupListRSP> page = blackGrayLibraryService.blackGrayGroupList(req, false);
        List<BlackGrayGroupListRSP> list = page.getList();

        List<BlackGrayGroupExportVO> exportList = list.stream().map(e -> {
            BlackGrayGroupExportVO exportVO = new BlackGrayGroupExportVO();
            BeanUtils.copyProperties(e, exportVO);
            exportVO.setBlackGrayType(Optional.ofNullable(BlackGrayTypeEnum.of(e.getBlackGrayType())).map(BlackGrayTypeEnum::getDesc).orElse(null));
            return exportVO;
        }).collect(Collectors.toList());

        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("application/octet-stream");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("全量名单查询-按集团汇总.xlsx", "UTF-8"));
            ExcelUtil<BlackGrayGroupExportVO> excelUtil = new ExcelUtil<>(BlackGrayGroupExportVO.class);
            excelUtil.exportExcel(exportList, "黑灰名单综合查询记录", response);
            return null;
        } catch (Exception e) {
            log.error("distinctExport error", e);
            return Response.error("导出失败");
        }

    }*/

}
