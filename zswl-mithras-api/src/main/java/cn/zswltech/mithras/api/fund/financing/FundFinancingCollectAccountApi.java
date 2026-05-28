package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.financing.collectaccount.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName FinancingPledgeApi
 * @Description 银行账户相关接口
 * @Author jackerhe
 * @Date 2023/2/20 2:11 下午
 * @Version 1.0
 **/
@Api(tags = "融资管理-对方收款账户相关接口")
@RequestMapping(path = "/fund/financing/collection/account")
public interface FundFinancingCollectAccountApi {

    @ApiOperation("创建账户")
    @PostMapping(path = "/create")
    R<Void> create(@RequestBody @Valid FundFinancingCollectAccountCreateREQ req);

    @ApiOperation("修改账户")
    @PostMapping(path = "/modify")
    R<Void> modify(@RequestBody @Valid FundFinancingCollectAccountModifyREQ req);

    @ApiOperation("获取账户列表")
    @PostMapping(path = "/list")
    R<List<FundFinancingCollectAccountListRSP>> list(@RequestBody @Valid FundFinancingCollectAccountListREQ req);

    @ApiOperation("删除账户")
    @PostMapping(path = "/delete")
    R<Void> delete(@RequestBody @Valid FundFinancingCollectAccountDetailREQ req);

}
