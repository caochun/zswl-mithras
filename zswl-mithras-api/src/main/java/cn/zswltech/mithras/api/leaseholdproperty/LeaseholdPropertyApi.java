package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SingleFileREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @since 2023-08-14
 * @author yangxiong
 * @description 租赁物类型API接口
 */
@Api(tags = "租赁物类型API接口")
@RequestMapping(path = "/leaseholdproperty")
public interface LeaseholdPropertyApi {
    @PostMapping("/list")
    @ApiOperation("租赁物类型-信息列表")
    R<PageR<LeaseholdPropertyRSP>> list(@RequestBody LeaseholdPropertyREQ param);

    @PostMapping("/import")
    @ApiOperation("租赁物类型-信息导入")
    R<Void> excelImport(@Valid SingleFileREQ file);
}
