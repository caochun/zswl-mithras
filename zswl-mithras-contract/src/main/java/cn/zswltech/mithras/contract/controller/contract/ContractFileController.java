package cn.zswltech.mithras.contract.controller.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.file.*;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractFileApi;
import cn.zswltech.mithras.contract.application.contract.ContractFileApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractFileController implements ContractFileApi {
    @Resource
    private ContractFileApplicationService contractFileApplicationService;

    @Override
    public R<List<ContractFileGroupRSP>> listExchangeMaterialGroup(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractFileApplicationService.listExchangeMaterialGroup(contractSingleIdREQ);
    }

    @Override
    public R<List<ContractFileGroupRSP>> listContractGroup(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractFileApplicationService.listContractGroup(contractSingleIdREQ);
    }

    @Override
    public R<Long> upload(@Valid ContractFileUploadREQ contractFileUploadREQ) {
        return contractFileApplicationService.upload(contractFileUploadREQ);
    }

    @Override
    public R<Void> generate(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ) {
        return contractFileApplicationService.generate(contractSingleIdREQ);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid ContractFileRemoveREQ contractFileRemoveREQ) {
        return contractFileApplicationService.remove(contractFileRemoveREQ);
    }

    @Override
    public R<Void> saveTextInfo(@RequestBody @Valid ContractTextInfoREQ req) {
        return contractFileApplicationService.saveTextInfo(req);
    }

    @Override
    public R<ContractTextInfoRSP> getTextInfo(@RequestBody @Valid ContractSingleIdREQ req) {
        return contractFileApplicationService.getTextInfo(req);
    }
}
