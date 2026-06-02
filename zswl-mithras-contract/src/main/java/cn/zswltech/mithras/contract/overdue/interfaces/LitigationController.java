package cn.zswltech.mithras.contract.overdue.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.overdue.application.command.*;
import cn.zswltech.mithras.contract.overdue.application.dto.LitigationDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.LitigationListDto;
import cn.zswltech.mithras.contract.overdue.application.query.ContractClientQuery;
import cn.zswltech.mithras.contract.overdue.application.query.LitigationPageQuery;
import cn.zswltech.mithras.contract.overdue.application.service.LitigationApplicationService;
import cn.zswltech.mithras.contract.overdue.application.service.LitigationContractQueryService;
import cn.zswltech.mithras.contract.overdue.domain.acl.ClientOverdueInfoDto;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractClientInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 10:02
 */
@RestController
@Slf4j
@Api(tags = "诉讼登记管理")
public class LitigationController {

    @Resource
    private LitigationApplicationService litigationApplicationService;
    @Resource
    private LitigationContractQueryService litigationContractQueryService;


    @ApiOperation(value = "诉讼登记列表")
    @PostMapping("/litigation/pageList")
    public R<PageR<LitigationListDto>> pageList(@RequestBody LitigationPageQuery query) {
        return R.ok(litigationApplicationService.page(query));
    }


    @ApiOperation(value = "新增诉讼登记")
    @PostMapping("/litigation/add")
    public R<Long> add(@RequestBody LitigationCreateCommand command) {
        return R.ok(litigationApplicationService.create(command));
    }

    @ApiOperation(value = "诉讼登记详情")
    @GetMapping("/litigation/detail")
    public R<LitigationDetailDto> detail(@RequestParam Long id) {
        return R.ok(litigationApplicationService.detail(id));
    }

    @ApiOperation(value = "诉讼登记-客户相关合同下拉")
    @GetMapping("/litigation/contract/pulldown")
    public R<Map<Long, String>> contractPulldown(@RequestParam Long clientId) {
        return R.ok(litigationContractQueryService.contractPulldown(clientId));
    }

    @ApiOperation(value = "诉讼登记-合同相关全量客户")
    @PostMapping("/litigation/contract/client")
    public R<List<ContractClientInfo>> contractClient(@RequestBody ContractClientQuery query) {
        return R.ok(litigationContractQueryService.contractClient(query.getContractIds()));
    }

    @ApiOperation(value = "诉讼登记保存")
    @PostMapping("/litigation/save")
    public R<Void> save(@RequestBody LitigationSaveCommand command) {
        litigationApplicationService.save(command);
        return R.ok();
    }

    @ApiOperation(value = "诉讼登记新增被告")
    @PostMapping("/litigation/defendant/add")
    public R<Void> defendantAdd(@RequestBody DefendantAddCommand command) {
        litigationApplicationService.defendantAdd(command);
        return R.ok();
    }

    @ApiOperation(value = "诉讼登记删除被告")
    @PostMapping("/litigation/defendant/remove")
    public R<Void> defendantRemove(@RequestBody DefendantRemoveCommand command) {
        litigationApplicationService.defendantRemove(command);
        return R.ok();
    }

    @ApiOperation(value = "诉讼登记新增进展")
    @PostMapping("/litigation/progress/add")
    public R<Void> progressAdd(@RequestBody ProgressAddCommand command) {
        litigationApplicationService.progressAdd(command);
        return R.ok();
    }

    @ApiOperation(value = "客户逾期信息展示")
    @GetMapping("/litigation/client/overdueinfo")
    public R<ClientOverdueInfoDto> clientOverdueInfo(@RequestParam("clientId") Long clientId) {
        return R.ok(litigationContractQueryService.clientOverdueInfo(clientId));
    }
}
