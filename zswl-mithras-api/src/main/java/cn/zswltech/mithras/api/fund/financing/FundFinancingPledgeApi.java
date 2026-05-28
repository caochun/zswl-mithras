package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;
import cn.zswltech.mithras.dto.fund.financing.pledge.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName FinancingPledgeApi
 * @Description 质押明细相关接口
 * @Author jackerhe
 * @Date 2023/2/20 2:11 下午
 * @Version 1.0
 **/
@Api(tags = "融资管理-质押明细相关接口")
@RequestMapping(path = "/fund/financing/pledge")
public interface FundFinancingPledgeApi {

    @ApiOperation("根据业务部门id获取项目下拉列表")
    @PostMapping(path = "/proj/list")
    R<List<FundFinancingPledgeProjListRSP>> projList(@RequestBody @Valid FundFinancingPledgeProjListREQ req);

    @ApiOperation("根据项目id获取合同下拉列表")
    @PostMapping(path = "/contract/list")
    R<List<FundFinancingPledgeContractListRSP>> contractList(@RequestBody @Valid FundFinancingPledgeContractListREQ req);

    @ApiOperation("创建质押")
    @PostMapping(path = "/create")
    R<Void> create(@RequestBody @Valid FundFinancingPledgeCreateREQ req);

    @ApiOperation("修改质押")
    @PostMapping(path = "/modify")
    R<Void> modify(@RequestBody @Valid FundFinancingPledgeModifyREQ req);

    @ApiOperation("获取质押列表")
    @PostMapping(path = "/list")
    R<List<FundFinancingPledgeListRSP>> list(@RequestBody @Valid FundFinancingPledgeListREQ req);

    @ApiOperation("获取质押详情")
    @PostMapping(path = "/detail")
    R<FundFinancingPledgeDetailRSP> detail(@RequestBody @Valid FundFinancingPledgeDetailREQ req);

    @ApiOperation("删除质押")
    @PostMapping(path = "/delete")
    R<Void> delete(@RequestBody @Valid FundFinancingPledgeDetailREQ req);

    @ApiOperation("根据合同编号模糊查询")
    @PostMapping(path = "/contract/search")
    R<List<FundFinancingContractInfoListRSP>> contractSearch(@RequestBody @Valid FundFinancingContractInfoListREQ req);

}
