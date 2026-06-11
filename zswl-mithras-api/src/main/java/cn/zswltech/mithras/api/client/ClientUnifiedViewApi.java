package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.afterlease.ClientUnifiedViewOverdueRentListRSP;
import cn.zswltech.mithras.dto.client.client.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Api(value = "客户统一视图接口", tags = "客户统一视图接口")
public interface ClientUnifiedViewApi {

    @ApiOperation("获取用户有权限部门列表")
    @GetMapping("/client/select/orgs")
    R<List<SelectRSP>> orgList();

    @ApiOperation("获取的客户列表")
    @PostMapping("/client/unified/view/list")
    R<PageR<ClientUnifiedViewListRSP>> unifiedViewList(@RequestBody @Valid ClientUnifiedViewListREQ req);

    @ApiOperation("获取的客户详情")
    @PostMapping("/client/unified/view/detail")
    R<ClientUnifiedViewDetailRSP> unifiedViewDetail(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

    @ApiOperation("获取客户授信信息")
    @PostMapping("/client/unified/view/apply/credit")
    R<ClientUnifiedApplyCreditRSP> clientApplyCredit(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

    @ApiOperation("获取客户授信五级分类信息")
    @PostMapping("/client/unified/view/apply/classification")
    R<ClientClassificationRSP> classification(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

    @ApiOperation("获取客户授信历史")
    @PostMapping("/client/unified/view/apply/credit/history")
    R<Map<String, Long>> clientApplyCreditHistory(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

    @ApiOperation("获取客户项目列表")
    @PostMapping("/client/unified/view/proj/list")
    R<List<ClientUnifiedProjListRSP>> clientProjList(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

    @ApiOperation("获取客户合同列表")
    @PostMapping("/client/unified/view/contract/list")
    R<List<ClientUnifiedContractListRSP>> clientContractList(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

    @ApiOperation("获取客户统一折线图")
    @PostMapping("/client/unified/view/customer/trends")
    R<List<ClientUnifiedCustomerTrendsRSP>> customerTrends();

    @ApiOperation("获取客户历史评级")
    @PostMapping("/client/unified/view/rating/history")
    R<List<ClientUnifiedRatingHistoryRSP>> ratingHistory(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

    @ApiOperation("获取客户逾期租金情况")
    @PostMapping("/client/unified/view/overdue/rent")
    R<List<ClientUnifiedViewOverdueRentListRSP>> overdueRent(@RequestBody @Valid ClientUnifiedApplyCreditREQ req);

}
