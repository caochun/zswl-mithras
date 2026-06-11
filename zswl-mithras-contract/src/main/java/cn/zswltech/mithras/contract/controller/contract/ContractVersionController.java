package cn.zswltech.mithras.contract.controller.contract;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.*;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractVersionApi;
import cn.zswltech.mithras.contract.application.contract.ContractVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractVersionController implements ContractVersionApi {
    @Resource
    private ContractVersionApplicationService contractVersionApplicationService;

    @Override
    public R<Void> effect(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        return contractVersionApplicationService.effect(contractFlowBasicREQ);
    }

    @Override
    public R<Void> addNewReceipt(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        return contractVersionApplicationService.addNewReceipt(contractFlowBasicREQ);
    }

    @Override
    public R<Void> addNewReceiptCancel(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        return contractVersionApplicationService.addNewReceiptCancel(contractFlowBasicREQ);
    }

    @Override
    public R<Void> startRent(@RequestBody @Valid ContractFlowStartRentREQ contractFlowStartRentREQ) {
        return contractVersionApplicationService.startRent(contractFlowStartRentREQ);
    }

    @Override
    public R<Void> startRentCancel(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        return contractVersionApplicationService.startRentCancel(contractFlowBasicREQ);
    }

    @Override
    public R<String> change(@RequestBody @Valid @Param("req") ContractFlowChangeREQ req) {
        return contractVersionApplicationService.change(req);
    }

    @Override
    public R<Void> changeCancel(@RequestBody @Valid @Param("req") ContractFlowChangeREQ req) {
        return contractVersionApplicationService.changeCancel(req);
    }

    @Override
    public R<Void> upload(Long contractId, MultipartFile[] fileArray, String changeType) {
        return contractVersionApplicationService.upload(contractId, fileArray, changeType);
    }

    @Override
    public R<List<MaterialsListRsp>> downFile(@RequestBody @Valid ContractFlowChangeREQ req) {
        return contractVersionApplicationService.downFile(req);
    }

    @Override
    public R<Void> changeDelete(@RequestBody @Valid ContractFileDeleteREQ req) {
        return contractVersionApplicationService.changeDelete(req);
    }

    @Override
    public R<Void> changeConserve(@RequestBody @Valid ContractFlowChangeConserveREQ req) {
        return contractVersionApplicationService.changeConserve(req);
    }

    @Override
    public R<Void> settle(@RequestBody @Valid ContractFlowSettleREQ req) {
        return contractVersionApplicationService.settle(req);
    }

    @Override
    public R<Void> settleCancel(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        return contractVersionApplicationService.settleCancel(contractFlowBasicREQ);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req) {
        return contractVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid @Param("req") ProjReviewVersionDiffREQ req) {
        return contractVersionApplicationService.comparePreVersion(req);
    }

    @Override
    public R<ContractCanChangeRSP> canChange(@RequestBody @Valid ContractCanChangeREQ req) {
        return contractVersionApplicationService.canChange(req);
    }

    @Override
    public R constraint(@RequestBody @Valid ContractConstraintREQ req) {
        return contractVersionApplicationService.constraint(req);
    }

    @Override
    public R<Void> confirmEffect(@RequestBody @Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        return contractVersionApplicationService.confirmEffect(contractFlowBasicREQ);
    }

    @Override
    public R<Void> uploadStartRentFile(Long contractId, MultipartFile[] fileArray) {
        return contractVersionApplicationService.uploadStartRentFile(contractId, fileArray);
    }

    @Override
    public R<List<MaterialsListRsp>> downloadStartRentFile(@RequestBody @Valid ContractFlowBasicREQ req) {
        return contractVersionApplicationService.downloadStartRentFile(req);
    }

    @Override
    public R<Void> startRentFileDelete(@RequestBody @Valid ContractStartRentFileDeleteREQ req) {
        return contractVersionApplicationService.startRentFileDelete(req);
    }
}
