package cn.zswltech.mithras.api.report;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.report.BatchExportExcelREQ;
import cn.zswltech.mithras.dto.report.batch.BatchReportREQ;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 征信报送接口
 *
 * @author wangchuanhao
 * @date 2022/10/19 2:24 PM
 */
@RequestMapping("/cr")
public interface CrReportApi {

    /**
     * 同步业务库
     * @return
     */
    @PostMapping("/sync")
    R<Void> execute();

    /**
     * 提交审批
     * @return
     */
    @PostMapping("/submit")
    R<Void> submit(@RequestBody BatchReportREQ req);

    /**
     * 获取批次号
     */
    @ApiOperation(value = "获取批次号")
    @PostMapping(path = "/get/batch/number")
    R<String> getBatchNumber();


    /**
     * 导出Excel
     */
    @ApiOperation(value = "导出Excel")
    @PostMapping(path = "/export/excel")
    R<Void> exportExcel(@RequestBody @Valid BatchExportExcelREQ req);
}
