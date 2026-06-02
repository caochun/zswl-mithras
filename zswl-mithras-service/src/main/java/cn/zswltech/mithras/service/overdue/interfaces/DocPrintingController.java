package cn.zswltech.mithras.service.overdue.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.contract.overdue.application.command.PrintingAddCommand;
import cn.zswltech.mithras.contract.overdue.application.command.PrintingSubmitCommand;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.application.query.PrintingPageQuery;
import cn.zswltech.mithras.service.overdue.application.service.PrintingApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @description: 文书用印接口
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:30
 */
@RestController
@Slf4j
@Api(tags = "文书用印")
public class DocPrintingController {

    @Resource
    private PrintingApplicationService printingApplicationService;

    @ApiOperation(value = "文书用印删除")
    @PostMapping(path = "/printing/remove")
    public R<Void> remove(@RequestBody @Valid SinglePkREQ req) {
        printingApplicationService.remove(req.getId());
        return R.ok();
    }

    @ApiOperation(value = "文书用印列表")
    @PostMapping("/printing/pageList")
    public R<PageR<PrintingListDto>> pageList(@RequestBody PrintingPageQuery query) {
        return R.ok(printingApplicationService.pageList(query));
    }

    @ApiOperation(value = "文书用印新增")
    @PostMapping("/printing/add")
    public R<Long> add(@RequestBody PrintingAddCommand command) {
        return R.ok(printingApplicationService.add(command));
    }

    @ApiOperation(value = "文书用印详情")
    @PostMapping("/printing/detail")
    public R<PrintingDetailDto> detail(@RequestBody SinglePkREQ req) {
        return R.ok(printingApplicationService.detail(req));
    }

    @ApiOperation(value = "文书用印编辑")
    @PostMapping("/printing/save")
    public R<Void> save(@RequestBody PrintingDetailDto dto) {
        printingApplicationService.save(dto);
        return R.ok();
    }

    @ApiOperation(value = "文书用印提交审批")
    @PostMapping("/printing/submit")
    public R<Void> submit(@RequestBody PrintingSubmitCommand id) {
        printingApplicationService.submit(id.getId());
        return R.ok();
    }

}
