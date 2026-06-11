package cn.zswltech.mithras.contract.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListREQ;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListRSP;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractEvaluationAgencyApi;
import cn.zswltech.mithras.contract.application.ContractEvaluationAgencyApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContractEvaluationAgencyController implements ContractEvaluationAgencyApi {
    @Resource
    private ContractEvaluationAgencyApplicationService contractEvaluationAgencyApplicationService;

    @Override
    public R<List<ContractEvaluationAgencyListRSP>> list(@RequestBody @Valid ContractEvaluationAgencyListREQ req) {
        return contractEvaluationAgencyApplicationService.list(req);
    }
}
