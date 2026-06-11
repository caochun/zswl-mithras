package cn.zswltech.mithras.blackgray.controller;

import cn.hutool.core.bean.BeanException;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseTaskDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseTaskListRSP;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayWarehouseTask;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 黑灰名单任务表
* @author 
* @date 2024-01-16
*/
@RestController
@Api(tags = "黑灰名单任务表-接口")
public class BlackGrayWarehouseTaskController {

    @Resource
    private BlackGrayWarehouseTaskService blackGrayWarehouseTaskService;

    @ApiOperation("新增黑灰名单任务表")
    @PostMapping("/black/gray/warehouse/task/add")
    public R<Long> add(@RequestBody @Valid BlackGrayWarehouseTaskAddREQ req) {
        return R.ok(blackGrayWarehouseTaskService.add(req));
    }

    @ApiOperation("修改黑灰名单任务表")
    @PostMapping("/black/gray/warehouse/task/modify")
    public R<Void> modify(@RequestBody @Valid BlackGrayWarehouseTaskModifyREQ req){
        blackGrayWarehouseTaskService.modify(req);
        return R.ok();
    }
    @ApiOperation("黑灰名单任务表列表")
    @PostMapping("/black/gray/warehouse/task/list")
    public R<PageR<BlackGrayWarehouseTaskListRSP>> list(@RequestBody @Valid BlackGrayWarehouseTaskListREQ req){
        PageR<BlackGrayWarehouseTask> data = blackGrayWarehouseTaskService.list(req);
        List<BlackGrayWarehouseTaskListRSP> list = BeanUtil.copyToList(data.getList(), BlackGrayWarehouseTaskListRSP.class);
        return R.ok(PageR.of(list, data.getTotal()));
    }

    @ApiOperation("黑灰名单任务表详情")
    @PostMapping("/black/gray/warehouse/task/detail")
    public R<BlackGrayWarehouseTaskDetailRSP> detail(@RequestBody @Valid BlackGrayWarehouseTaskDetailREQ req){
        BlackGrayWarehouseTask detail = blackGrayWarehouseTaskService.detail(req.getId());
        if(ObjectUtil.isEmpty(detail)){
            throw new BeanException("记录不存在");
        }
        BlackGrayWarehouseTaskDetailRSP rsp = BeanUtil.copyProperties(detail, BlackGrayWarehouseTaskDetailRSP.class);
        if(ObjectUtil.isNotEmpty(detail.getUploadFile())) {
            rsp.setUploadFileList(JSONUtil.toList(detail.getUploadFile(), String.class));
        }
        return R.ok(rsp);
    }

    @ApiOperation("删除黑灰名单任务表")
    @PostMapping("/black/gray/warehouse/task/remove")
    public R<Void> remove(@RequestBody @Valid BlackGrayWarehouseTaskRemoveREQ req){
        blackGrayWarehouseTaskService.remove(req);
        return R.ok();
    }

}