package cn.zswltech.mithras.api.workbench;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 首页工作台-公告
 * @date 2023-03-15
 */
@Api(tags = "首页工作台-公告-接口")
public interface WorkbenchAnnouncementApi {

    @ApiOperation("新增首页工作台-公告")
    @PostMapping("/workbench/announcement/add")
    R<Long> add(@RequestBody @Valid WorkbenchAnnouncementAddReq req);

    @ApiOperation("修改首页工作台-公告")
    @PostMapping("/workbench/announcement/modify")
    R<Void> modify(@RequestBody @Valid WorkbenchAnnouncementModifyReq req);

    @ApiOperation("首页工作台-公告列表")
    @PostMapping("/workbench/announcement/list")
    R<PageR<WorkbenchAnnouncementListRsp>> list(@RequestBody @Valid WorkbenchAnnouncementListReq req);

    @ApiOperation("删除首页工作台-公告")
    @PostMapping("/workbench/announcement/remove")
    R<Void> remove(@RequestBody @Valid WorkbenchAnnouncementSingleReq req);

    @ApiOperation("首页工作台-公告详情")
    @PostMapping("/workbench/announcement/detail")
    R<WorkbenchAnnouncementDetailRsp> detail(@RequestBody @Valid WorkbenchAnnouncementSingleReq req);

    @ApiOperation("首页工作台-公告置顶")
    @PostMapping("/workbench/announcement/top")
    R<Void> top(@RequestBody @Valid WorkbenchAnnouncementSingleReq req);

}