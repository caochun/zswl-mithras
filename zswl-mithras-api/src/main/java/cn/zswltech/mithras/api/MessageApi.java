package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.message.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @ClassName MessageApi
 * @Description
 * @Author jackerhe
 * @Date 2022/9/13 11:22 上午
 * @Version 1.0
 **/
@Api(tags = "消息通知")
public interface MessageApi {

    @ApiOperation("发送通知消息")
    @PostMapping("/message/notice")
    R send(@RequestBody MessageAddREQ messageAddREQ);


    @ApiOperation("通知经理消息")
    @PostMapping("/message/notice/manager")
    R sendManager(@RequestBody MessageAddREQ messageAddREQ);

    @ApiOperation("发送待办消息")
    @PostMapping("/message/todo")
    R sendTodo(@RequestBody MessageAddREQ messageAddREQ);

    @ApiOperation("查询消息列表")
    @PostMapping("/message/list")
    R<PageR<MessageListRSP>> list(@RequestBody MessageListREQ messageListREQ);

    @ApiOperation("读消息")
    @PostMapping("/message/read")
    R read(@RequestBody MessageReadREQ req);

    @ApiOperation("消息全部读取")
    @PostMapping("/message/read/all")
    R readAll(MessageReadREQ req);

    @ApiOperation("审批办理")
    @PostMapping("/message/handle")
    R handle(@RequestBody MessageHandleREQ req);


    @ApiOperation("各栏目数量")
    @GetMapping("/message/count")
    R<MessageCountRSP> myTabCount();
}
