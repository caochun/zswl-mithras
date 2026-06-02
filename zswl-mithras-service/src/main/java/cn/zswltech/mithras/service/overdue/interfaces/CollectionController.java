package cn.zswltech.mithras.service.overdue.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.contract.mapper.dto.OcContractListDto;
import cn.zswltech.mithras.contract.overdue.application.command.CollectionActionSubmitCommand;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionActionDto;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.contract.overdue.application.query.CollectionPageQuery;
import cn.zswltech.mithras.service.overdue.application.service.CollectionApplicationService;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionActionId;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionId;
import cn.zswltech.mithras.service.overdue.infrastructure.service.SchedulingJobServiceImpl;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
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
 * @date: 2024/10/22 19:52
 */
@RestController
@Slf4j
@Api(tags = "催收管理")
public class CollectionController {
    @Resource
    private SchedulingJobServiceImpl schedulingJobService;
    @Resource
    private CollectionApplicationService collectionApplicationService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @GetMapping("/test/overdueClientInfoUpdateTask")
    public void overdueClientInfoUpdateTask() {
        schedulingJobService.overdueClientInfoUpdateTask();
    }

    @ApiOperation(value = "催收列表")
    @PostMapping("/overduecollection/list")
    public R<List<CollectionListDto>> overdueCollectionList(@RequestBody CollectionPageQuery query){
        return R.ok(collectionApplicationService.page(query));
    }

    @ApiOperation(value = "催收详情")
    @PostMapping("/overduecollection/detail")
    public R<CollectionDetailDto> overdueCollectionDetail(@RequestBody CollectionId dto) {
        return R.ok(collectionApplicationService.detail(dto));
    }

    @ApiOperation(value = "合同列表")
    @GetMapping("/overduecollection/contract/list")
    public R<List<OcContractListDto>> contractList(@RequestParam("clientId") Long clientId) {
        return R.ok(contractBaseInfoService.ocContractList(clientId));
    }

    @ApiOperation(value = "合同列表导出")
    @PostMapping("/overduecollection/contract/export")
    public void contractListExport(@RequestBody CollectionPageQuery query) {
        contractBaseInfoService.contractListExport(query.getClientId());
    }

    @ApiOperation(value = "合同下拉列表")
    @GetMapping("/overduecollection/contract/pulldown")
    public R<Map<Long, String>> contractPulldown(@RequestParam("clientId") Long clientId) {
        return R.ok(contractBaseInfoService.contractPulldown(clientId));
    }

    @ApiOperation(value = "催收动作列表导出")
    @PostMapping("/overduecollection/action/download")
    public void download(@RequestBody CollectionId collectionId) {
        collectionApplicationService.downloadAction(collectionId);
    }

    @ApiOperation(value = "新增催收动作")
    @PostMapping("/overduecollection/action/add")
    public R<Long> addAction(@RequestBody CollectionActionDto dto) {
        return R.ok(collectionApplicationService.addAction(dto));
    }

    @ApiOperation(value = "修改催收信息")
    @PostMapping("/overduecollection/action/update")
    public R<Void> updateAction(@RequestBody CollectionActionDto dto) {
        collectionApplicationService.updateAction(dto);
        return R.ok();
    }

    @ApiOperation(value = "删除催收信息")
    @PostMapping("/overduecollection/action/delete")
    public R<Void> updateAction(@RequestBody CollectionActionId id) {
        collectionApplicationService.delete(id);
        return R.ok();
    }

    @ApiOperation(value = "生成函件")
    @PostMapping("/overduecollection/letter/generate")
    public R<Void> generate(@RequestBody CollectionActionSubmitCommand dto) {
        collectionApplicationService.genLetter(new CollectionActionId(dto.getId()));
        return R.ok();
    }

    @ApiOperation(value = "催收提交审批")
    @PostMapping("/overduecollection/action/submit")
    public R<Void> submit(@RequestBody CollectionActionSubmitCommand id) {
        collectionApplicationService.actionSubmit(id);
        return R.ok();
    }

    @ApiOperation(value = "催收动作详情")
    @PostMapping("/overduecollection/action/detail")
    public R<CollectionActionDto> actionDetail(@RequestBody SinglePkREQ req) {
        return R.ok(collectionApplicationService.actionDetail(req));
    }
}
