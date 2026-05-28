package cn.zswltech.mithras.api.kpi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordRemoveREQ;

/**
* @description 绩效考核-项目分配记录表
* @author vico
* @date 2024-09-27
*/
@Api(tags = "绩效考核-项目分配记录表-接口")
public interface KpiProjectDistributionRecordApi {

    @ApiOperation("新增绩效考核-项目分配记录表")
    @PostMapping("/kpi/project/distribution/record/add")
    R<Void> add(@RequestBody @Valid KpiProjectDistributionRecordAddREQ req);

    @ApiOperation("修改绩效考核-项目分配记录表")
    @PostMapping("/kpi/project/distribution/record/modify")
    R<Void> modify(@RequestBody @Valid KpiProjectDistributionRecordModifyREQ req);

    @ApiOperation("绩效考核-项目分配记录表列表")
    @PostMapping("/kpi/project/distribution/record/list")
    R<PageR<KpiProjectDistributionRecordListRSP>> list(@RequestBody @Valid KpiProjectDistributionRecordListREQ req);

    @ApiOperation("删除绩效考核-项目分配记录表")
    @PostMapping("/kpi/project/distribution/record/remove")
    R<Void> remove(@RequestBody @Valid KpiProjectDistributionRecordRemoveREQ req);

}