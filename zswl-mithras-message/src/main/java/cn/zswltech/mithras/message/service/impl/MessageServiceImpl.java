package cn.zswltech.mithras.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.notice.model.BizChangeStatusMsg;
import cn.zswl.notice.model.CommonQry;
import cn.zswl.notice.model.Notice;
import cn.zswl.notice.model.NoticeVo;
import cn.zswl.notice.service.ExportService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.message.*;
import cn.zswltech.mithras.message.enums.MessageChannelEnum;
import cn.zswltech.mithras.message.enums.MessageType;
import cn.zswltech.mithras.message.model.MessageModel;
import cn.zswltech.mithras.message.model.TodoMessageBody;
import cn.zswltech.mithras.message.model.ZhfkNoticeRelation;
import cn.zswltech.mithras.message.service.AbstractMessageService;
import cn.zswltech.mithras.message.service.factory.MessageFactory;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.message.service.ZhfkNoticeRelationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @ClassName MessageServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/7/27 10:46 上午
 * @Version 1.0
 **/
@Service
@Primary
@Slf4j
public class MessageServiceImpl extends AbstractMessageService {

    @Autowired
    private MessageFactory messageFactory;

    @Resource
    private UserService userService;

    @Resource
    private ZhfkNoticeRelationService noticeRelationService;

    @Autowired(required = false)
    private ExportService exportService;

    /*@Resource
    private ZhfkNoticeService zhfkNoticeService;*/

    @Override
    public String getType() {
        return MessageType.MESSAGE.getType();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<Long> sendMessage(MessageModel messageModel) {
        if(ObjectUtil.isNull(exportService)){
            log.info("消息通知功能未开启");
            return null;
        }
        if (Boolean.FALSE.equals(checkMessage(messageModel))) {
            return null;
        }
        try {
        MessageService messageService = messageFactory.chooseBean(messageModel.getBodie().getSendType());
        //消息记录保存接口
        //messageBaseInfoService.saveMessage(messageModel);
        //调用消息自定义保存接口
        if (!messageService.saveMessage(messageModel)) {
            log.info("自定义消息体处理失败");
            return null;
        }
        // 拆分成单个用户推
        List<Long> msgIds = new ArrayList<>(messageModel.getTo().size());
        for (Long id : messageModel.getTo()) {
            // 拷贝一个消息对象
            MessageModel mm = new MessageModel();
            BeanUtil.copyProperties(messageModel, mm);
            // 覆盖用户信息
            mm.setTo(Collections.singletonList(id));
            // 获取手机号填充
            String phone = userService.getRealPhone(id);
            mm.setToTel(Collections.singletonList(phone));
            try {
                //转换消息ID为我方id
                ZhfkNoticeRelation zhfkNoticeRelation = buildZhfkNoticeRelation(mm);
                noticeRelationService.save(zhfkNoticeRelation);
                mm.setMsgId(zhfkNoticeRelation.getId());
                msgIds.add(zhfkNoticeRelation.getId());
                Long aLong = messageService.pushMessage(mm);//messageId
                zhfkNoticeRelation.setMessageId(aLong);
                noticeRelationService.updateById(zhfkNoticeRelation);
            } catch (Exception e) {
                log.error("发送消息异常[{}]", JSONUtil.toJsonStr(mm), e);
            }
        }
        return msgIds;
        }catch (Exception e){
            log.error("MessageServiceImpl sendMessage error", e);
        }
        return null;
    }

    @Override
    //todo 异步发送有点问题，暂不做
    //@Async
    @Transactional(rollbackFor = Throwable.class)
    public void sendMessageAsync(MessageModel messageModel) {
        sendMessage(messageModel);
    }

    @Override
    public void handle(MessageHandleREQ req) {
        if(ObjectUtil.isNull(exportService)){
            log.info("消息通知功能未开启");
            return;
        }
        try {
        if (ObjectUtil.isEmpty(req.getMithrasUserId())) {
            if (ObjectUtil.isEmpty(AccountUtil.getLoginInfo())) {
                log.info("用无登陆信息");
                return;
            }
            req.setMithrasUserId(AccountUtil.getLoginInfo().getId());
        }
        log.info("MessageServiceImpl handle param {}", req);
            //移动端需要转换成
        if (MessageChannelEnum.APP.name().equals(req.getMessageChannel())) {
            Long msgId = noticeRelationService.idToMsgId(req.getNoticeId());
            if (ObjectUtil.isNotNull(msgId)) {
                req.setNoticeId(msgId);
            }
        }
        //消息ID为空，采用taskId
        if(ObjectUtil.isEmpty(req.getNoticeId())){
            if(ObjectUtil.isNotEmpty(req.getTaskId())){
                ZhfkNoticeRelation relation = noticeRelationService.getOne(Wrappers.<ZhfkNoticeRelation>lambdaQuery()
                        .eq(ZhfkNoticeRelation::getMithrasId, req.getTaskId())
                        .orderByDesc(ZhfkNoticeRelation::getUpdateTime)
                        .last(" limit 1"));
                if(ObjectUtil.isEmpty(relation)){
                    log.info("未找到对应待办消息");
                    return;
                }
                req.setNoticeId(relation.getMessageId());
            }
        }
        BizChangeStatusMsg msg = new BizChangeStatusMsg();
        msg.setNoticeId(req.getNoticeId());
        msg.setUserId(userService.getRealPhone(req.getMithrasUserId()));
        msg.setGmtUpdate(new Date());
        msg.setNeedOa(ObjectUtil.isEmpty(req.getNeedQA()) ? Boolean.TRUE : req.getNeedQA());
        exportService.publishEvent(msg);
            List<ZhfkNoticeRelation> zhfkNoticeRelations = noticeRelationService.listByNoticeIds(Collections.singletonList(req.getNoticeId()));
            if(ObjectUtil.isNotEmpty(zhfkNoticeRelations)){
                zhfkNoticeRelations.forEach(zhfkNoticeRelation -> {
                    zhfkNoticeRelation.setOperationRecords(zhfkNoticeRelation.getOperationRecords() + "," + "[4,x]");
                });
                noticeRelationService.updateBatchById(zhfkNoticeRelations);
            }
        }catch (Exception e){
            log.error("MessageServiceImpl handle error", e);
        }
    }

    @Override
    @Async
    public void handleAsync(MessageHandleREQ req) {
        handle(req);
    }

    @Override
    public void makeReaded(MessageReadREQ req) {
        if (ObjectUtil.isNull(exportService)) {
            log.info("消息通知功能未开启");
            return;
        }
        if (ObjectUtil.isEmpty(req.getMithrasUserId())) {
            if (ObjectUtil.isNull(AccountUtil.getLoginInfo())) {
                log.info("用户无登陆信息");
                return;
            }
            req.setMithrasUserId(AccountUtil.getLoginInfo().getId());
        }
        log.info("MessageServiceImpl makeRead param {}", req);
        try {
            //移动端需要转换成消息id
            if (MessageChannelEnum.APP.name().equals(req.getMessageChannel())) {
                req.setNoticeIds(noticeRelationService.idToMsgIdList(req.getNoticeIds()));
            }
            //过滤已读的，不再重复发送
            List<Notice> notices = exportService.detailList(req.getNoticeIds());
            if (ObjectUtil.isNotEmpty(notices)) {
                List<Long> noticeIds = new ArrayList<>();
                for (Notice notice : notices) {
                    if (!notice.isReadFlag()) {
                        noticeIds.add(notice.getId());
                    }
                }
                List<ZhfkNoticeRelation> zhfkNoticeRelations = noticeRelationService.listByNoticeIds(noticeIds);
                if(ObjectUtil.isNotEmpty(zhfkNoticeRelations)){
                    zhfkNoticeRelations.forEach(zhfkNoticeRelation -> {
                        zhfkNoticeRelation.setOperationRecords(zhfkNoticeRelation.getOperationRecords() + "," + "[x,1]");
                    });
                    noticeRelationService.updateBatchById(zhfkNoticeRelations);
                }
                exportService.makeReaded(userService.getRealPhone(req.getMithrasUserId()), noticeIds, req.getNeedQA());
                //回调租赁读状态变更接口
                //zhfkNoticeService.read(noticeIds);
            }
        } catch (Exception e) {
            log.error("MessageServiceImpl makeReaded error", e);
        }
    }

    @Override
    @Async
    public void makeReadedAsync(MessageReadREQ req){
        makeReaded(req);
    }

    @Override
    @Async
    public void readAll(Long userId) {
        if(ObjectUtil.isNull(exportService)){
            log.info("消息通知功能未开启");
            return;
        }
        log.info("MessageServiceImpl readAll param {}", userId);
        CommonQry commonQry = new CommonQry();
        commonQry.setUserId(userService.getRealPhone(userId));
        commonQry.setNeedRead(2);
        commonQry.setPageSize(Integer.MAX_VALUE);
        commonQry.setPageNum(1);
        PageInfo<NoticeVo> noticeVoPageInfo = exportService.pageList(commonQry);
        if(ObjectUtil.isEmpty(noticeVoPageInfo)){
            return;
        }
        List<Long> msgIds = noticeVoPageInfo.getList().stream().map(NoticeVo::getId).collect(Collectors.toList());
        log.info("MessageServiceImpl readAll began msgIds {}", msgIds);
        List<ZhfkNoticeRelation> zhfkNoticeRelations = noticeRelationService.listByNoticeIds(msgIds);
        if(ObjectUtil.isNotEmpty(zhfkNoticeRelations)){
            zhfkNoticeRelations.forEach(zhfkNoticeRelation -> {
                zhfkNoticeRelation.setOperationRecords(zhfkNoticeRelation.getOperationRecords() + "," + "[x,1]");
            });
            noticeRelationService.updateBatchById(zhfkNoticeRelations);
        }
        exportService.makeReaded(commonQry.getUserId(), msgIds, Boolean.TRUE);
        //回调租赁读状态变更接口
        //zhfkNoticeService.read(msgIds);
        log.info("MessageServiceImpl readAll over {}", userId);
    }

    @Override
    public PageInfo<NoticeVo> list(CommonQry commonQry) {
        if(ObjectUtil.isNull(exportService)){
            log.info("消息通知功能未开启");
            return null;
        }
        log.info("MessageController list param is commonQry : {}", commonQry);
        PageInfo<NoticeVo> noticeVoPageInfo = exportService.pageList(commonQry);
        //PageInfo<NoticeVo> noticeVoPageInfo = zhfkNoticeService.list(commonQry);
        log.info("MessageController list result is noticeVoPageInfo: {}", noticeVoPageInfo);
        return noticeVoPageInfo;
    }

    @Override
    public MessageCountRSP myTabCount() {
        long startTime = System.currentTimeMillis();
        MessageCountRSP rsp = new MessageCountRSP();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
        List<Callable<MessageTodoRSP>> tasks = new ArrayList<>();
        MessageTodoREQ todoREQ = new MessageTodoREQ();
        todoREQ.setPage(1);
        todoREQ.setPageSize(20);
        todoREQ.setAccount(String.valueOf(AccountUtil.getLoginInfo().getId()));
        tasks.add(new ApiMessageTotalCallerTask(todoREQ));
        tasks.add(new ApiMessageUnreadCallerTask(todoREQ));

        try {
            List<Future<MessageTodoRSP>> results = fixedThreadPool.invokeAll(tasks);
            for (Future<MessageTodoRSP> result : results) {
                if ("total".equalsIgnoreCase(result.get().getTabName())) {
                    rsp.setTotalCount(result.get().getValue());
                } else if ("unread".equalsIgnoreCase(result.get().getTabName())) {
                    rsp.setUnreadCount(result.get().getValue());
                }
            }
            fixedThreadPool.shutdown();
            while (true){
                if (fixedThreadPool.isTerminated()){
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("代码运行时间：" + totalTime + "毫秒");
        return rsp;
    }

    //检查消息合法性
    private Boolean checkMessage(MessageModel messageModel) {
        if (ObjectUtil.isNull(messageModel) || ObjectUtil.isNull(messageModel.getTo()) ||
                ObjectUtil.isNull(messageModel.getBodie()) || ObjectUtil.isNull(messageModel.getBodie().getSendType())) {
            return false;
        }
        if (ObjectUtil.isEmpty(messageModel.getTimestamp())) {
            messageModel.setTimestamp(new Date());
        }
        return true;
    }

    private ZhfkNoticeRelation buildZhfkNoticeRelation(MessageModel messageModel){
        ZhfkNoticeRelation zhfkNoticeRelation = new ZhfkNoticeRelation();
        zhfkNoticeRelation.setCreateTime(LocalDateTime.now());
        zhfkNoticeRelation.setUpdateTime(LocalDateTime.now());
        //待办配置
        if(messageModel.getBodie() instanceof TodoMessageBody){
            zhfkNoticeRelation.setMithrasId(((TodoMessageBody) messageModel.getBodie()).getMithrasId());
            zhfkNoticeRelation.setOperationRecords("[0,0]");
        } else {
            zhfkNoticeRelation.setOperationRecords("[8,0]");
        }
        return zhfkNoticeRelation;
    }

    public class ApiMessageTotalCallerTask implements Callable<MessageTodoRSP> {
        private MessageTodoREQ messageTodoREQ;
        public ApiMessageTotalCallerTask(MessageTodoREQ messageTodoREQ) {
            this.messageTodoREQ = messageTodoREQ;
        }
        @Override
        public MessageTodoRSP call() throws Exception {
            Long res = myMessageTotalPageCountList(messageTodoREQ);
            MessageTodoRSP messageTodoRSP = new MessageTodoRSP("total", messageTodoREQ.getAccount(), res.intValue());
            return messageTodoRSP;
        }
    }


    public class ApiMessageUnreadCallerTask implements Callable<MessageTodoRSP> {
        private MessageTodoREQ messageTodoREQ;
        public ApiMessageUnreadCallerTask(MessageTodoREQ messageTodoREQ) {
            this.messageTodoREQ = messageTodoREQ;
        }
        @Override
        public MessageTodoRSP call() throws Exception {
            Long res = myMessageUnreadPageCountList(messageTodoREQ);
            MessageTodoRSP messageTodoRSP = new MessageTodoRSP("unread", messageTodoREQ.getAccount(), res.intValue());
            return messageTodoRSP;
        }
    }


    private Long myMessageTotalPageCountList(MessageTodoREQ todoREQ) {
        CommonQry commonQry = new CommonQry();
        Long userId = Long.valueOf(todoREQ.getAccount());
        if(ObjectUtil.isEmpty(todoREQ.getAccount())){
            userId = AccountUtil.getLoginInfo().getId();
        }
        commonQry.setUserId(userService.getRealPhone(userId));
        commonQry.setPageSize(todoREQ.getPageSize());
        commonQry.setPageNum(todoREQ.getPage());
        PageInfo<NoticeVo> noticeVoPageInfo = exportService.pageList(commonQry);
        return noticeVoPageInfo.getTotal();
    }

    private Long myMessageUnreadPageCountList(MessageTodoREQ todoREQ) {
        CommonQry commonQry = new CommonQry();
        Long userId = Long.valueOf(todoREQ.getAccount());
        if(ObjectUtil.isEmpty(todoREQ.getAccount())){
            userId = AccountUtil.getLoginInfo().getId();
        }
        commonQry.setUserId(userService.getRealPhone(userId));
        commonQry.setNeedRead(2);
        commonQry.setPageSize(todoREQ.getPageSize());
        commonQry.setPageNum(todoREQ.getPage());
        PageInfo<NoticeVo> noticeVoPageInfo = exportService.pageList(commonQry);
        return noticeVoPageInfo.getTotal();
    }
}
