package cn.zswltech.mithras.contract.interfaces.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListREQ;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListRSP;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.contract.ContractEvaluationAgencyApi;
import cn.zswltech.mithras.contract.application.contract.ContractEvaluationAgencyApplicationService;
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
