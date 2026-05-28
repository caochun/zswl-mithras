package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailDetailREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailDetailRSP;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailGenREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailSendREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * 租金催收发送邮件
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:18 PM
 */
@Api(tags = "租金催收发送邮件-接口")
public interface RentCollectionEmailApi {

    @ApiOperation("发送催收邮件")
    @PostMapping("/rent/collection/email/send")
    R<Void> sendEmail(@RequestBody @Valid RentCollectionEmailSendREQ req);

    @ApiOperation("生成催收邮件（返回预览文件id）")
    @PostMapping("/rent/collection/email/gen")
    R<Long> genEmail(@RequestBody @Valid RentCollectionEmailGenREQ req);

    @ApiOperation("生成催收邮件（返回可预览url）")
    @PostMapping("/rent/collection/email/genHtml")
    R<String> genEmailHtml(@RequestBody @Valid RentCollectionEmailGenREQ req);

    @ApiOperation("催收邮件页面详情")
    @PostMapping("/rent/collection/email/detail")
    R<RentCollectionEmailDetailRSP> detail(@RequestBody @Valid RentCollectionEmailDetailREQ req);

    @ApiOperation("获取可预览的html")
    @GetMapping(value = "/rent/collection/email/htmlPreview", produces = MediaType.TEXT_HTML_VALUE)
    byte[] htmlPreview(@RequestParam("htmlKey") String htmlKey);

    @ApiOperation("下载的html")
    @GetMapping(value = "/rent/collection/email/down")
    R<Void> down(Long collectionId, String accountName, String accountBank, String accountNumber);

}
