package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.settle.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Api(tags = "合同管理-合同结清相关接口")
public interface ContractSettleApi {
    @ApiOperation("保存正常结清方案")
    @PostMapping("/contract/settle/plan/normal/save")
    R<Long> savePlanNormal(@RequestBody @Valid ContractSettlePlanNormalREQ contractSettlePlanNormalREQ);

    @ApiOperation("保存提前结清方案")
    @PostMapping("/contract/settle/plan/inadvance/save")
    R<Long> savePlanInAdvance(@RequestBody @Valid ContractSettlePlanInAdvanceREQ contractSettlePlanInAdvanceREQ);

    @ApiOperation("获取最近一次结清方案")
    @PostMapping("/contract/settle/plan/latest/get")
    R<ContractSettlePlanDetailRSP> getLatestSettlePlan(@RequestBody @Valid ContractSettlePlanDetailREQ contractSettlePlanDetailREQ);

    @ApiOperation("上传补充协议")
    @PostMapping("/contract/settle/extra/file/upload")
    R<Void> uploadExtraFile(@Valid ContractSettleExtraFileUploadREQ contractSettleExtraFileUploadREQ);

    @ApiOperation("删除补充协议")
    @PostMapping("/contract/settle/extra/file/remove")
    R<Void> removeExtraFile(@RequestBody @Valid ContractSettleExtraFileRemoveREQ contractSettleExtraFileRemoveREQ);

    @ApiOperation("获取合同结清补充协议列表")
    @PostMapping("/contract/settle/extra/file/list")
    R<List<ContractSettleExtraFileRSP>> listSettleExtraFile(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);
}
