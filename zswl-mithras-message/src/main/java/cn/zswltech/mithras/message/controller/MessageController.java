package cn.zswltech.mithras.message.controller;

import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.mithras.api.MessageApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.message.MessageCountRSP;
import cn.zswltech.mithras.dto.message.MessageHandleREQ;
import cn.zswltech.mithras.dto.message.MessageListREQ;
import cn.zswltech.mithras.dto.message.MessageListRSP;
import cn.zswltech.mithras.dto.message.MessageReadREQ;
import cn.zswltech.mithras.dto.message.OAAuthREQ;
import cn.zswltech.mithras.message.application.MessageApplicationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class MessageController implements MessageApi {

    @Resource
    private MessageApplicationService messageApplicationService;

    @Override
    public R send(MessageAddREQ messageAddREQ) {
        return messageApplicationService.send(messageAddREQ);
    }

    @Override
    public R sendManager(MessageAddREQ messageAddREQ) {
        return messageApplicationService.sendManager(messageAddREQ);
    }

    @Override
    public R sendTodo(MessageAddREQ messageAddREQ) {
        return messageApplicationService.sendTodo(messageAddREQ);
    }

    @Override
    public R<PageR<MessageListRSP>> list(MessageListREQ messageListREQ) {
        return messageApplicationService.list(messageListREQ);
    }

    @Override
    public R read(MessageReadREQ req) {
        return messageApplicationService.read(req);
    }

    @Override
    public R readAll(MessageReadREQ req) {
        return messageApplicationService.readAll(req);
    }

    @Override
    public R handle(MessageHandleREQ req) {
        return messageApplicationService.handle(req);
    }

    @Override
    public R<MessageCountRSP> myTabCount() {
        return messageApplicationService.myTabCount();
    }

    @ApiOperation("oa集团换取tocken")
    @PostMapping("/message/oa/auth")
    public Response<Map<String, Object>> authOA(@RequestBody OAAuthREQ req, HttpServletRequest request, HttpServletResponse response) {
        return messageApplicationService.authOA(req, request, response);
    }
}
