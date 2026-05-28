package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dto.onlyoffice.DocDetailRSP;
import cn.zswltech.mithras.api.dto.onlyoffice.GetDocDetailREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 对接onlyoffice组件 实现文档在线编辑
 *
 * @author wangchuanhao
 * @date 2022/7/8 10:19 AM
 */
@Api(tags = "在线编辑文档-接口")
@RequestMapping("/onlyoffice")
public interface OnlyOfficeApi {

    @ApiOperation("调用onlyoffice编辑器所需的配置（前端调用）")
    @PostMapping("/docDetail")
    R<DocDetailRSP> docDetail(@RequestBody @Valid GetDocDetailREQ req);

    @ApiOperation("onlyoffice回调（前端无需关注）")
    @PostMapping("/callback")
    String callback(@RequestBody String callbackData);

    @ApiOperation("下载文件（前端无需关注）")
    @GetMapping("/download")
    void download(String key);

}
