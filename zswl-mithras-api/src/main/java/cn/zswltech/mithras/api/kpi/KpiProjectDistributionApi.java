package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Api(tags = "绩效考核-项目分配")
@RequestMapping(path = "/kpi/projectdistribution")
public interface KpiProjectDistributionApi {
    @ApiOperation("绩效考核-项目分配-分页列表")
    @PostMapping(path = "/pagelist")
    R<PageR<KpiProjectDistributionListRSP>> pageList(@RequestBody @Valid KpiProjectDistributionListREQ req);

    @ApiOperation("绩效考核-项目分配-提交审批")
    @PostMapping(path = "/submit")
    R<Void> submit(@RequestBody @Valid KpiProjectDistributionSubmitREQ req);

    @ApiOperation("绩效考核-项目分配-查看上个版本")
    @PostMapping(path = "/prev")
    R<List<KpiProjectDistributionPrevRSP>> prev(@RequestBody @Valid KpiProjectDistributionPrevREQ req);

    @ApiOperation("绩效考核-项目分配-查看历史")
    @PostMapping(path = "/history")
    R<List<KpiProjectDistributionHistoryRSP>> history(@RequestBody @Valid KpiProjectDistributionHistoryREQ req);

    @ApiOperation("绩效考核-项目分配-数据导入")
    @PostMapping(path = "/import")
    R<Void> importHistoryData(KpiProjectDistributionImportREQ req);

    @ApiOperation("绩效考核-项目分配-数据初始化")
    @PostMapping(path = "/init")
    R<Void> init();

    @PostMapping("/export")
    @ApiOperation("绩效考核-项目分配表导出")
    void export(@RequestBody @Valid KpiProjectDistributionListREQ req);
}
