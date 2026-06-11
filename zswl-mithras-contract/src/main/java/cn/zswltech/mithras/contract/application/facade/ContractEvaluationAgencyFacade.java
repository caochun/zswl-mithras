package cn.zswltech.mithras.contract.application.facade;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.ContractEvaluationAgencyApplicationService;
import cn.zswltech.mithras.contract.core.evaluation.ContractEvaluationAgencyDraftService;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListREQ;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListRSP;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author bigbear
 * @date 2025/3/21 15:01
 * @description
 */
@Slf4j
@Service
public class ContractEvaluationAgencyFacade implements ContractEvaluationAgencyApplicationService {

    @Resource
    private ContractEvaluationAgencyDraftService contractEvaluationAgencyService;

    @Override
    public R<List<ContractEvaluationAgencyListRSP>> list(ContractEvaluationAgencyListREQ req) {
        return R.ok(contractEvaluationAgencyService.queryList(req));
    }
}
