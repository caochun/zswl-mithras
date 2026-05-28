package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.*;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@Api(tags = "合同管理-合同审批（版本）相关操作接口")
public interface ContractVersionApi {
    @ApiOperation("合同生效（提交审批）")
    @PostMapping("/contract/flow/effect/submit")
    R<Void> effect(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ);

    @ApiOperation("新增借据（提交审批）")
    @PostMapping("/contract/flow/receipt/submit")
    R<Void> addNewReceipt(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ);

    @ApiOperation("取消新增借据")
    @PostMapping("/contract/flow/receipt/cancel")
    R<Void> addNewReceiptCancel(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ);

    @ApiOperation("合同起租（提交审批）")
    @PostMapping("/contract/flow/start/submit")
    R<Void> startRent(@RequestBody @Valid ContractFlowStartRentREQ contractFlowStartRentREQ);

    @ApiOperation("取消合同起租")
    @PostMapping("/contract/flow/start/cancel")
    R<Void> startRentCancel(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ);

    @ApiOperation("合同变更（提交审批）")
    @PostMapping("/contract/flow/change/submit")
    R<String> change(@RequestBody @Valid @Param("req") ContractFlowChangeREQ req);

    @ApiOperation("取消合同变更")
    @PostMapping("/contract/flow/change/cancel")
    R<Void> changeCancel(@RequestBody @Valid @Param("req") ContractFlowChangeREQ req);

    @ApiOperation("合同变更（补充协议）")
    @PostMapping("/contract/flow/change/upload")
    R<Void> upload(Long contractId, MultipartFile[] fileArray, String changeType);

    @ApiOperation("合同变更（补充协议-列表）")
    @PostMapping("/contract/flow/change/down")
    R<List<MaterialsListRsp>> downFile(@RequestBody @Valid ContractFlowChangeREQ req);

    @ApiOperation("合同变更（补充协议-删除）")
    @PostMapping("/contract/flow/change/delete")
    R<Void> changeDelete(@RequestBody @Valid ContractFileDeleteREQ req);

    @ApiOperation("合同变更（保存）")
    @PostMapping("/contract/flow/change/conserve")
    R<Void> changeConserve(@RequestBody @Valid ContractFlowChangeConserveREQ req);

    @ApiOperation("合同结清（提交审批）")
    @PostMapping("/contract/flow/settle/submit")
    R<Void> settle(@RequestBody @Valid ContractFlowSettleREQ req);

    @ApiOperation("取消合同结清")
    @PostMapping("/contract/flow/settle/cancel")
    R<Void> settleCancel(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ);

    /**
     * 合同信息版本表列
     *
     * @param req
     * @return
     */
    @ApiOperation("合同信息版本列表")
    @PostMapping("/contract/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    /**
     * 合同信息版本比较详情（与上一版本比较）
     *
     * @param req
     * @return
     */
    @ApiOperation("合同信息版本比较详情（与上一版本比较）")
    @PostMapping("/contract/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid @Param("req") ProjReviewVersionDiffREQ req);

    @ApiOperation("合同变更-检查当前流程是否可继续")
    @PostMapping("/contract/flow/change/check")
    R<ContractCanChangeRSP> canChange(@RequestBody @Valid ContractCanChangeREQ req);

    @ApiOperation("合同变更-约束检查")
    @PostMapping("/contract/flow/change/constraint")
    R constraint(@RequestBody @Valid ContractConstraintREQ req);

    @ApiOperation("结清确认（提交审批）")
    @PostMapping("/contract/flow/confirm/submit")
    R<Void> confirmEffect(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ);

    @ApiOperation("合同起租附件-上传")
    @PostMapping("/contract/flow/startRent/upload")
    R<Void> uploadStartRentFile(Long contractId, MultipartFile[] fileArray);

    @ApiOperation("合同起租附件-下载")
    @PostMapping("/contract/flow/startRent/download")
    R<List<MaterialsListRsp>> downloadStartRentFile(@RequestBody @Valid ContractFlowBasicREQ req);

    @ApiOperation("合同起租附件-删除")
    @PostMapping("/contract/flow/startRent/delete")
    R<Void> startRentFileDelete(@RequestBody @Valid ContractStartRentFileDeleteREQ req);

}
