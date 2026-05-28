package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoAddREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoModifyREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRSP;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 外部信息 中登网
 *
 * @author wangchuanhao
 * @date 2022/6/21 14:14 PM
 */
@Api(tags = "外部信息-中登网-接口")
@RequestMapping("/zhongdengInfo")
public interface ZhongdengInfoApi {

    @ApiOperation("新增中登网信息")
    @PostMapping("/add")
    R<Void> add(@RequestBody @Valid ZhongdengInfoAddREQ req);

    @ApiOperation("修改中登网信息")
    @PostMapping("/modify")
    R<Void> modify(@RequestBody @Valid ZhongdengInfoModifyREQ req);

    @ApiOperation(("中登网信息列表"))
    @PostMapping("/list")
    R<PageR<ZhongdengInfoRSP>> list(@RequestBody @Valid ExternalPageREQ req);

    @ApiOperation("删除中登网信息")
    @PostMapping("/remove")
    R<Void> remove(@RequestBody @Valid ZhongdengInfoRemoveREQ req);
}
