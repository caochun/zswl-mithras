package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListREQ;
import cn.zswltech.mithras.dto.contract.ContractEvaluationAgencyListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2025/3/21 14:45
 * @description
 */
@Api(tags = "合同租赁物评估机构-Api")
@RequestMapping("/contract/evaluation/agency")
public interface ContractEvaluationAgencyApi {

    @ApiOperation(value = "查询评估机构列表")
    @PostMapping(path = "/list")
    R<List<ContractEvaluationAgencyListRSP>> list(@RequestBody @Valid ContractEvaluationAgencyListREQ req);
}
