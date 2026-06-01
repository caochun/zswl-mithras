package cn.zswltech.mithras.blackgray.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.excel.BlackGrayOutboundRecordExport;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundListRSP;
import cn.zswltech.mithras.blackgray.enums.BlackGrayBusinessTypeEnum;
import cn.zswltech.mithras.blackgray.excel.ExcelUtil;
import cn.zswltech.mithras.blackgray.service.BlackGrayManualOutboundService;
import cn.zswltech.mithras.blackgray.service.GruulAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description 黑灰名单人工出库表
* @author
* @date 2023-11-29
*/
@RestController
@Api(tags = "黑灰名单人工出库表-接口")
@Slf4j
public class BlackGrayManualOutboundController {

    @Resource
    private BlackGrayManualOutboundService blackGrayManualOutboundService;
    @Resource
    GruulAuthService gruulAuthService;

    @ApiOperation("新增黑灰名单人工出库表")
    @PostMapping("/black/gray/manual/outbound/add")
    public R<Long> add(@RequestBody BlackGrayManualOutboundAddREQ req) {
        return R.ok(blackGrayManualOutboundService.add(req));
    }

    @ApiOperation("修改黑灰名单人工出库表")
    @PostMapping("/black/gray/manual/outbound/modify")
    public R<Void> modify(@RequestBody BlackGrayManualOutboundModifyREQ req){
        blackGrayManualOutboundService.modify(req);
        return R.ok();
    }

    @ApiOperation("黑灰名单人工出库表列表")
    @PostMapping("/black/gray/manual/outbound/list")
    public R<PageR<BlackGrayManualOutboundListRSP>> list(@RequestBody BlackGrayManualOutboundListREQ req){
       return R.ok(blackGrayManualOutboundService.list(req));
    }

    @ApiOperation("黑灰名单人工出库表详情")
    @PostMapping("/black/gray/manual/outbound/detail")
    public R<BlackGrayManualOutboundDetailRSP> detail(@RequestBody BlackGrayManualOutboundDetailREQ req){
        BlackGrayManualOutboundDetailRSP detail = blackGrayManualOutboundService.detail(req.getId());
        if(ObjectUtil.isEmpty(req.getAuditTaskId())){
            detail.setWarehouseOrganization(null);
        }
        return R.ok(detail);
    }

    @ApiOperation("黑灰名单人工出库表删除")
    @PostMapping("/black/gray/manual/outbound/remove")
    public R<Void> remove(@RequestBody BlackGrayManualOutboundRemoveREQ req){
        blackGrayManualOutboundService.remove(req);
        return R.ok();
    }

    @ApiOperation("黑灰名单出库导出")
    @GetMapping("/black/gray/manual/outbound/record/export")
    public void export(HttpServletResponse response, BlackGrayManualOutboundListREQ req) {
        if(req.getIds() == null){
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
        }
        List<BlackGrayManualOutboundListRSP> list = this.list(req).getData().getList();
        if(CollectionUtil.isNotEmpty(list)){
            try {
                List<BlackGrayOutboundRecordExport> blackGrayWarehouseRecordExports = BeanUtil.copyToList(list, BlackGrayOutboundRecordExport.class);
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentType("application/x-download");
                //[黑灰名单查询]_[所属部门]_[用户名]
                String fileName = "黑灰名单出库记录.xlsx";
                response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                ExcelUtil<BlackGrayOutboundRecordExport> excelUtil = new ExcelUtil<>(BlackGrayOutboundRecordExport.class);
                List<String> orgCodes = blackGrayWarehouseRecordExports.stream().map(BlackGrayOutboundRecordExport::getApplyOrganization).distinct().collect(Collectors.toList());
                Map<String, String> orgNameMap = gruulAuthService.batchGetOrgNameMap(orgCodes);
                //转换标识
                blackGrayWarehouseRecordExports.forEach(export -> {
                    //export.setBlackGrayType(Optional.ofNullable(BlackGrayTypeEnum.of(export.getBlackGrayType())).map(BlackGrayTypeEnum::getDesc).orElse(export.getBlackGrayType()));
                    export.setBusinessType(Optional.ofNullable(BlackGrayBusinessTypeEnum.of(export.getBusinessType())).map(BlackGrayBusinessTypeEnum::getDesc).orElse(export.getBusinessType()));
                    if (StringUtils.isNotBlank(export.getApplyOrganization()) || orgNameMap.containsKey(export.getApplyOrganization())) {
                        export.setApplyOrganization(orgNameMap.get(export.getApplyOrganization()));
                    }
                });
                excelUtil.exportExcel(blackGrayWarehouseRecordExports,"黑灰名单出库记录",response);
            } catch (IOException e) {
                log.error("黑灰名单名单错误");
            }
        }
    }



}