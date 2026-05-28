package cn.zswltech.mithras.api.collection;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
*  票据管理表
* @author vico
* @date 2023-06-05
*/
@Api(tags = "票据管理表-接口")
public interface BillManagementApi {

    /**
     *新增票据管理表
     **/
    @ApiOperation("新增票据管理表")
    @PostMapping("/bill/management/add")
    R<Void> add(@RequestBody @Valid BillManagementAddREQ req);

    /**
     *修改票据管理表
     **/
    @ApiOperation("修改票据管理表")
    @PostMapping("/bill/management/modify")
    R<Void> modify(@RequestBody @Valid BillManagementModifyREQ req);

    /**
     *票据管理表列表
     **/
    @ApiOperation("票据管理表列表")
    @PostMapping("/bill/management/list")
    R<PageR<BillManagementListRSP>> list(@RequestBody @Valid BillManagementListREQ req);

    /**
     *删除票据管理表
     **/
    @ApiOperation("删除票据管理表")
    @PostMapping("/bill/management/remove")
    R<Void> remove(@RequestBody @Valid BillManagementRemoveREQ req);

}