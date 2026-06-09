package cn.zswltech.mithras.service.application.message;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.notice.model.CommonQry;
import cn.zswl.notice.model.NoticeVo;
import cn.zswltech.gruul.biz.service.LoginService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.RequestUtil;
import cn.zswltech.gruul.common.util.ShaUtil;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.message.application.api.MessageApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.message.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName MessageController
 * @Description
 * @Author jackerhe
 * @Date 2022/9/13 1:43 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class MessageFacade implements MessageApplicationService {

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    @Autowired
    private MessageConver messageConver;

    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private LoginService loginService;

    @Value("${system.token.expiration:604800000}")
    public long tokenExpiration;

    @Value("${trade.notice.privateKey:}")
    private String privateKey;


    @Override
    public R send(MessageAddREQ messageAddREQ) {
        messageAddREQ.setNeedOa(messageAddREQ.isNeedQa());
        messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "businessId",
            checkerClass = CommonModifyMainAuthCheckerNew.class, businessModule = "PROJ_REVIEW")
    public R sendManager(MessageAddREQ messageAddREQ) {
        messageAddREQ.setNeedOa(messageAddREQ.isNeedQa());
        //一日一次
        CommonQry commonQry = new CommonQry();
        commonQry.setPageNum(1);
        commonQry.setPageSize(20);
        Calendar calendar = Calendar.getInstance();
        MessageTypeEnum remind = MessageTypeEnum.REMIND;
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        commonQry.setCreateStart(zero);
        commonQry.setTitle(remind.display + ": " + messageAddREQ.getRelation() + remind.message);
        PageInfo<NoticeVo> list = messageService.list(commonQry);
        if(ObjectUtil.isNotEmpty(list) && ObjectUtil.isNotEmpty(list.getList()) && list.getList().size() > 0){
            throw new MithrasException(messageAddREQ.getRelation() + "今日已通知法务/风控");
        }
        messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
        return R.ok();
    }

    @Override
    public R sendTodo(MessageAddREQ messageAddREQ) {
        messageAddREQ.setNeedOa(messageAddREQ.isNeedQa());
        messageService.sendMessage(messageConver.reqToTodoMessage(messageAddREQ));
        return R.ok();
    }

    @Override
    public R<PageR<MessageListRSP>> list(MessageListREQ messageListREQ) {
        CommonQry commonQry = BeanUtil.copyProperties(messageListREQ, CommonQry.class);
        List<MessageListRSP> list = null;
        //流程id
        if(ObjectUtil.isNotEmpty(messageListREQ.getMessageType())){
            commonQry.setBizType(messageListREQ.getMessageType());
        }
        if(ObjectUtil.isEmpty(messageListREQ.getUserId())){
            messageListREQ.setUserId(AccountUtil.getLoginInfo().getId());
        }
        commonQry.setUserId(userService.getRealPhone(messageListREQ.getUserId()));
        commonQry.setPageNum(messageListREQ.getPage());
        PageInfo<NoticeVo> noticeVoPageInfo = messageService.list(commonQry);
        if(ObjectUtil.isNull(noticeVoPageInfo)){
            return R.ok();
        }
        if (ObjectUtil.isNotNull(noticeVoPageInfo) && ObjectUtil.isNotEmpty(noticeVoPageInfo.getList())) {
            list = noticeVoPageInfo.getList().stream().map(base -> {
                MessageListRSP messageListRSP = BeanUtil.copyProperties(base, MessageListRSP.class);
                if (!base.isReadFlag() && LocalDateTimeUtil.between(base.getGmtCreate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        LocalDateTime.now(),
                        ChronoUnit.HOURS) > 11) {
                    messageListRSP.setOvertimeFlag(YesOrNoNumberEnum.YES.getCode());
                } else {
                    messageListRSP.setOvertimeFlag(YesOrNoNumberEnum.NO.getCode());
                }
                return messageListRSP;
            }).collect(Collectors.toList());
        }
        return R.ok(PageR.of(list, noticeVoPageInfo.getTotal(), noticeVoPageInfo.getPages(), noticeVoPageInfo.getPageNum(),noticeVoPageInfo.getPageSize()));
    }

    @Override
    public R read(MessageReadREQ req) {
        log.info("MessageController read param {}", req);
        //默认发送oa
        req.setNeedQA(ObjectUtil.isEmpty(req.getNeedQA()) ? Boolean.TRUE : req.getNeedQA());
        if (ObjectUtil.isEmpty(req.getMithrasUserId())) {
            if (ObjectUtil.isNull(AccountUtil.getLoginInfo())) {
                throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
            }
            req.setMithrasUserId(AccountUtil.getLoginInfo().getId());
        }
        messageService.makeReadedAsync(req);
        log.info("MessageController read over {}", req);
        return R.ok();
    }

    @Override
    public R readAll(@RequestBody MessageReadREQ req) {
        req.setNeedQA(ObjectUtil.isEmpty(req.getNeedQA()) ? Boolean.TRUE : req.getNeedQA());
        if (ObjectUtil.isEmpty(req.getMithrasUserId())) {
            if (ObjectUtil.isEmpty(AccountUtil.getLoginInfo())) {
                throw new MithrasException("用户无登陆信息");
            }
            req.setMithrasUserId(AccountUtil.getLoginInfo().getId());
        }
        messageService.readAll(req.getMithrasUserId());
        return R.ok();
    }

    @Override
    public R handle(MessageHandleREQ req) {
        messageService.handle(req);
        return R.ok();
    }

    @Override
    public R<MessageCountRSP> myTabCount() {
        return R.ok(messageService.myTabCount());
    }

    @ApiOperation("oa集团换取tocken")
    public Response authOA(@RequestBody OAAuthREQ req, HttpServletRequest request, HttpServletResponse response) {
        this.logHeaderInfo(request);
        if(!checkTocken(req)){
            throw new MithrasException("用户信息不合法");
        }
        UserDO userDO = userDOMapper.selectByPrimaryKey(req.getMithrasClientId());
        if(ObjectUtil.isEmpty(userDO)){
            throw new MithrasException("无法查询到该用户");
        }
        //String jwtToken = JwtUtils.createJWT(userDO.getId(),userDO.getPwd(), userDO.getAccount(), tokenExpiration);
        Response<Map<String, Object>> mapResponse = loginService.userLogin(response, userDO, RequestUtil.getIpAddr(request));
        return mapResponse;
    }


    private Boolean checkTocken(OAAuthREQ req) {
        if (ObjectUtils.isEmpty(req.getRandom()) || ObjectUtils.isEmpty(req.getSecret()) || ObjectUtils.isEmpty(req.getTimestamp())) {
            return Boolean.FALSE;
        }
        return req.getSecret().equals(ShaUtil.shaEncode(privateKey.concat(req.getTimestamp()).concat(req.getRandom())));
    }

    private void logHeaderInfo(HttpServletRequest request) {
        ThreadPoolUtil.getCommonPool().execute(() -> {
            try {
                List<String> logList = new LinkedList<>();
                Enumeration<String> enumeration = request.getHeaderNames();
                while (enumeration.hasMoreElements()) {
                    String headerName = enumeration.nextElement();
                    logList.add(headerName + "=" + request.getHeader(headerName));
                }
                log.info("集团OA换取token请求头信息:{}", JSONUtil.toJsonStr(logList));
            } catch (Exception e) {
                // do nothing
                log.error("集团OA换取token打印请求头信息异常", e);
            }
        });
    }
}
