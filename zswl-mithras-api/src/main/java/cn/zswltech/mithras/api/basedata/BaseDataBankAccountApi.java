package cn.zswltech.mithras.api.basedata;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Api(tags = "基础数据-我方账户相关接口")
public interface BaseDataBankAccountApi {
    @ApiOperation("保存我方账户")
    @PostMapping("/basedata/bankaccount/save")
    R<Long> save(@RequestBody @Valid BaseDataBankAccountSaveREQ baseDataBankAccountSaveREQ);

    @ApiOperation("我方账户列表")
    @PostMapping("/basedata/bankaccount/list")
    R<List<BaseDataBankAccountListRSP>> list(@RequestBody @Valid BaseDataBankAccountListREQ req);

    @ApiOperation("我方账户详情")
    @PostMapping("/basedata/bankaccount/detail")
    R<BaseDataBankAccountDetailRSP> detail(SinglePkREQ req);

    @ApiOperation("删除我方账户")
    @PostMapping("/basedata/bankaccount/delete")
    R<Void> delete(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("乙方账户列表")
    @PostMapping("/basedata/contractAccount/list")
    R<List<ContractAccountPayListRSP>> list(@RequestBody @Valid ContractAccountPayListREQ req);

    @ApiOperation("我方账户默认值")
    @PostMapping("/basedata/bankaccount/init")
    R<BaseDataBankAccountDetailRSP> init();
}
