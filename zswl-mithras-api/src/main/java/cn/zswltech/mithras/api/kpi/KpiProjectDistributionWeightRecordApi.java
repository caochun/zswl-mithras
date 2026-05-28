package cn.zswltech.mithras.api.kpi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordRemoveREQ;

/**
* @description 绩效考核-项目分配表-分配比重信息记录表
* @author vico
* @date 2024-09-27
*/
@Api(tags = "绩效考核-项目分配表-分配比重信息记录表-接口")
public interface KpiProjectDistributionWeightRecordApi {

    @ApiOperation("新增绩效考核-项目分配表-分配比重信息记录表")
    @PostMapping("/kpi/project/distribution/weight/record/add")
    R<Void> add(@RequestBody @Valid KpiProjectDistributionWeightRecordAddREQ req);

    @ApiOperation("修改绩效考核-项目分配表-分配比重信息记录表")
    @PostMapping("/kpi/project/distribution/weight/record/modify")
    R<Void> modify(@RequestBody @Valid KpiProjectDistributionWeightRecordModifyREQ req);

    @ApiOperation("绩效考核-项目分配表-分配比重信息记录表列表")
    @PostMapping("/kpi/project/distribution/weight/record/list")
    R<PageR<KpiProjectDistributionWeightRecordListRSP>> list(@RequestBody @Valid KpiProjectDistributionWeightRecordListREQ req);

    @ApiOperation("删除绩效考核-项目分配表-分配比重信息记录表")
    @PostMapping("/kpi/project/distribution/weight/record/remove")
    R<Void> remove(@RequestBody @Valid KpiProjectDistributionWeightRecordRemoveREQ req);

}