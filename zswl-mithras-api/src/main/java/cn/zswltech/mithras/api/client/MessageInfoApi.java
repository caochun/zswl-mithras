/*
package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.message.MessageInfoREQ;
import cn.zswltech.mithras.dto.client.message.MessageInfoRSP;
import cn.zswltech.mithras.dto.client.message.MessageReadREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;


*/
/**
 * 消息通知-接口
 * @author: jackerhe
 * @date: 2022/7/27 4:13 下午
 **//*

@Api(value = "测试", tags = "测试")
public interface MessageInfoApi {

    */
/**
     * 消息查询接口
     **//*

    @GetMapping ("/message/list")
    @ApiOperation("查询列表")
    R<PageR<MessageInfoRSP>> list(@Valid MessageInfoREQ req);

    @PostMapping("/message/read")
    @ApiOperation("已读取消息")
    R<Boolean> read(@RequestBody MessageReadREQ messageReadREQ);

}
*/
