package cn.zswltech.mithras.service.controller.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractEvaluationAgencyApi;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListREQ;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListRSP;
import cn.zswltech.mithras.service.service.contract.ContractEvaluationAgencyDraftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bigbear
 * @date 2025/3/21 15:01
 * @description
 */
@Slf4j
@RestController
public class ContractEvaluationAgencyController implements ContractEvaluationAgencyApi {

    @Resource
    private ContractEvaluationAgencyDraftService contractEvaluationAgencyService;

    @Override
    public R<List<ContractEvaluationAgencyListRSP>> list(ContractEvaluationAgencyListREQ req) {
        return R.ok(contractEvaluationAgencyService.queryList(req));
    }
}
