package cn.zswltech.mithras.service.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswl.notice.mapper.NoticeDataScopeMapper;
import cn.zswl.notice.mapper.NoticeMapper;
import cn.zswl.notice.model.DataScope;
import cn.zswl.notice.model.Notice;
import cn.zswl.notice.model.NoticeDataScope;
import cn.zswl.notice.model.NoticeExample;
import cn.zswl.notice.util.RemoteUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.message.service.dto.CicoOaFlowOperateREQ;
import cn.zswltech.mithras.message.service.dto.CicoOaListRSP;
import cn.zswltech.mithras.message.service.dto.CicoOaListReq;
import cn.zswltech.mithras.message.service.remote.CicoOaFlowOperateApiHandler;
import cn.zswltech.mithras.message.service.remote.CicoOaListApiHandler;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName MessageJob
 * @Description 消息管理任务
 * @Author jackerhe
 * @Date 2024/8/16 17:09
 * @Version 1.0
 **/

@Component
@Slf4j
public class MessageJob {

    @Autowired(required = false)
    private NoticeMapper noticeMapper;
    @Autowired(required = false)
    private RemoteUtil remoteUtil;
    @Autowired(required = false)
    private NoticeDataScopeMapper noticeDataScopeMapper;
    @Resource
    private UserService userService;
    @Resource
    private CicoOaListApiHandler cicoOaListApiHandler;
    @Resource
    private CicoOaFlowOperateApiHandler cicoOaFlowOperateApiHandler;

    @Value("${remote.publishNotice.sysCode}")
    private String SYSTEM_CODE;

    private final static String CREATEDATES = "2023-01-01 00:00:00";


    /**
     * 用于维护交投集团消息一致性
     * 客户维护，逐一匹配
     * 存在两种原因 1.业务回滚消息未撤回，此类只能修改标题后办结
     * 2.两边业务消息不匹配，同步
     **/
    @XxlJob("syncJTMessage")
    public void syncJTMessage() {
        try {
            String param = XxlJobHelper.getJobParam();
            //查询全量用户
            List<Long> newMessageUser;
            if (StrUtil.isNotBlank(param)) {
                newMessageUser = new ArrayList<>();
                newMessageUser.add(Long.valueOf(param));
            } else {
                newMessageUser = this.getNewMessageUser();
                /*newMessageUser = new ArrayList<>();
                newMessageUser.add(184L);*/
            }
            if (CollectionUtil.isEmpty(newMessageUser)) {
                return;
            }

            newMessageUser.forEach(clientId -> {
                //获取真实电话
                String dealUser = userService.getRealPhone(clientId);
                //查询用户待办信息
                CicoOaListReq cicoOaListReq = new CicoOaListReq();
                cicoOaListReq.setPagenum(1);
                cicoOaListReq.setPagesize(30);
                cicoOaListReq.setReceiver(dealUser);
                cicoOaListReq.setIsremark(0);
                cicoOaListReq.setSyscode(SYSTEM_CODE);
                cicoOaListReq.setCreatedates(CREATEDATES);
                cicoOaListReq.setCreatedatee(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                CicoOaListRSP oaListRSP = cicoOaListApiHandler.execute(cicoOaListReq);
                if (ObjectUtil.isNotEmpty(oaListRSP) && ObjectUtil.isNotEmpty(oaListRSP.getData())) {
                    //解析比对
                    //Map<String, CicoOaListRSP.CicoOaListBody> flowId2Oa = oaListRSP.getData().stream().collect(Collectors.toMap(CicoOaListRSP.CicoOaListBody::getFlowid, e -> e, (a, b) -> a));
                    NoticeExample example = new NoticeExample();
                    NoticeExample.Criteria criteria = example.createCriteria();
                    criteria.andDealUserEqualTo(dealUser);
                    //criteria.andContentIn(new ArrayList<>(flowId2Oa.keySet()));
                    criteria.andTypeEqualTo((short) 3);
                    Map<String, Notice> flow2Notice = new HashMap<>();
                    List<Notice> notices = noticeMapper.selectByExample(example);
                    if (ObjectUtil.isNotEmpty(notices)) {
                        notices.stream().forEach(e -> {
                            String flowId = getFlowId(e.getBizInfo());
                            if (ObjectUtil.isNotEmpty(flowId)) {
                                flow2Notice.put(flowId, e);
                            }
                        });
                    }
                    int sum  = 0;
                    for (CicoOaListRSP.CicoOaListBody oaListBody : oaListRSP.getData()) {
                        Notice notice = flow2Notice.get(oaListBody.getFlowid());
                        if (ObjectUtil.isEmpty(notice)) {
                            //不存在，直接关闭
                            sum = ++sum;
                            this.sendRemoteMessage(oaListBody, null, "审批待办");
                        } else if (ObjectUtil.equals(Integer.valueOf(notice.getStatus()), YesOrNoNumberEnum.YES.getCode())) {
                            //已经关闭
                            sum = ++sum;
                            this.sendRemoteMessage(oaListBody, notice.getTitle(), "审批待办");
                        }
                    }
                    log.info("{}消息同步消息{}条", dealUser, sum);
                }
                //同步待阅
                cicoOaListReq.setIsremark(8);
                CicoOaListRSP oaReadListRSP = cicoOaListApiHandler.execute(cicoOaListReq);
                if (ObjectUtil.isNotEmpty(oaReadListRSP) && ObjectUtil.isNotEmpty(oaReadListRSP.getData())) {
                    //解析比对
                    Map<String, CicoOaListRSP.CicoOaListBody> flowId2Oa = oaReadListRSP.getData().stream().collect(Collectors.toMap(CicoOaListRSP.CicoOaListBody::getFlowid, e -> e, (a, b) -> a));
                    NoticeExample example = new NoticeExample();
                    NoticeExample.Criteria criteria = example.createCriteria();
                    criteria.andDealUserEqualTo(dealUser);
                    criteria.andContentIn(new ArrayList<>(flowId2Oa.keySet()));
                    criteria.andTypeEqualTo((short) 1);
                    Map<String, Notice> flow2Notice = new HashMap<>();
                    List<Notice> notices = noticeMapper.selectByExample(example);
                    if (ObjectUtil.isNotEmpty(notices)) {
                        notices.stream().forEach(e -> {
                            String flowId = getFlowId(e.getBizInfo());
                            if (ObjectUtil.isNotEmpty(flowId)) {
                                flow2Notice.put(flowId, e);
                            }
                        });
                    }
                    for (CicoOaListRSP.CicoOaListBody oaListBody : oaReadListRSP.getData()) {
                        Notice notice = flow2Notice.get(oaListBody.getFlowid());
                        if (ObjectUtil.isEmpty(notice)) {
                            //不存在，直接关闭
                            this.sendRemoteMessage(oaListBody, null, "系统通知");
                        } else if (ObjectUtil.equals(notice.getStatus(), YesOrNoNumberEnum.YES.getCode())) {
                            //已经关闭
                            this.sendRemoteMessage(oaListBody, notice.getTitle(), "系统通知");
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error("消息同步任务异常", e);
        }
    }

    private String getFlowId(String content) {
        if (content == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(content);
        } catch (JsonProcessingException e) {
           log.warn("getFlowId error", e);
        }
        return root.path("other")
                .path("flowid")
                .asText();  // 返回示例："4922044"
    }

    private void sendRemoteMessage(CicoOaListRSP.CicoOaListBody oaListBody, String requestname, String workflowname) {
        if (ObjectUtil.isEmpty(oaListBody)) {
            return;
        }
        if (ObjectUtil.isEmpty(requestname)) {
            requestname = "无效流程，自动关闭";
        }
        CicoOaFlowOperateREQ oaFlowOperateREQ = BeanUtil.copyProperties(oaListBody, CicoOaFlowOperateREQ.class);
        oaFlowOperateREQ.setRequestname(requestname);
        oaFlowOperateREQ.setCreatedatetime(oaListBody.getCreatedate() + " " + oaListBody.getCreatetime());
        oaFlowOperateREQ.setCreator("administrator");
        oaFlowOperateREQ.setIsremark("4");
        oaFlowOperateREQ.setReceivedatetime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        oaFlowOperateREQ.setReceivets(String.valueOf(System.currentTimeMillis()));
        oaFlowOperateREQ.setViewtype("0");
        oaFlowOperateREQ.setWorkflowname(workflowname);
        log.info("jtmessage param {}", oaFlowOperateREQ);
        cicoOaFlowOperateApiHandler.execute(oaFlowOperateREQ);
    }

    //初始化数据
    @XxlJob("initMessage")
    public void initMessage() {
        try {
            StopWatch st = new StopWatch("初始化数据");
            st.start("开始");
            Long offset = 0L;
            NoticeExample example = new NoticeExample();
            example.setLimit(5000);
            example.setOffset(offset);
            List<Notice> notices = noticeMapper.selectByExample(example);
            List<NoticeDataScope> scopes = new ArrayList<>();
            while(notices.size() > 0){
                for (Notice notice : notices) {
                    if(notice.getDataScope() == null){
                        continue;
                    }
                    DataScope dataScope = JSON.parseObject(notice.getDataScope(), DataScope.class);
                    if(dataScope.getDepts() != null && dataScope.getDepts().size() > 0){
                        for (String dept : dataScope.getDepts()) {
                            scopes.add(new NoticeDataScope()
                                    .setNoticeId(notice.getId()).setScopeType("dept").setContent(dept));
                        }
                    }
                    if(dataScope.getUsers() != null && dataScope.getUsers().size() > 0){
                        for (String user : dataScope.getUsers()){
                            scopes.add(new NoticeDataScope()
                                    .setNoticeId(notice.getId()).setScopeType("user").setContent(user));
                        }
                    }
                    if(dataScope.getRoles() != null && dataScope.getRoles().size() > 0){
                        for(String role : dataScope.getRoles()){
                            scopes.add(new NoticeDataScope()
                                    .setNoticeId(notice.getId()).setScopeType("role").setContent(role));
                        }
                    }
                }
                noticeDataScopeMapper.insertBatch(scopes);

                scopes.clear();
                offset += 5000;
                example.setOffset(offset);
               notices = noticeMapper.selectByExample(example);
            }
            st.stop();
            log.info("initMessage over {}", st.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("initMessage error", e);
        }
    }

    //获取需要处理的消息
    private List<Notice> getNeedSyncNotice(List<Long> noticeIds) {
        NoticeExample noticeExample = new NoticeExample();
        //查询已经完结的但是
        noticeExample.createCriteria()
                .andStatusEqualTo((short) 1)
                .andIdIn(noticeIds);
        return noticeMapper.selectByExample(noticeExample);
    }

    private List<Long> getNewMessageUser() {
        //查询所有
        /*NoticeExample noticeExample = new NoticeExample();
        //查询昨日发生变动的
        noticeExample.createCriteria()
                .andGmtUpdateGreaterThanOrEqualTo(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant()));
        List<Notice> notices = noticeMapper.selectByExample(noticeExample);
        return notices == null ? null : notices.stream().map(Notice::getDealUser).collect(Collectors.toList());*/
        UserQuery userQuery = new UserQuery();
        userQuery.setPage(1);
        userQuery.setPageSize(5000);
        userQuery.setActive(Boolean.TRUE);
        return userService.queryUserSys(userQuery).getContents().stream().map(UserVO::getId).collect(Collectors.toList());
    }

    //"http://newportal.cncico.com/mobilemode/apps/zjjt/toWorkflow.jsp?type=1&appid=1&syscode=ztzl&url=http%3A%2F%2Frzy.zsrzzl.com.cn%2Fprocess%2Freceive%2Fdetail%2F3034846%3FtypeId%3Dapproval%26businessKey%3D1844%26diff%3DtaskId%26clientType%3Dnull%26flag%3Dinfo%26mithrasMsgId%3D787682%26mithrasClientId%3D130";
    private Long analysisUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            Map<String, List<String>> params = parseQuery(url.getQuery());
            // 获取mithrasMsgId参数的值
            if (params.containsKey("url")) {
                List<String> urls = params.get("url");
                if (!urls.isEmpty()) {
                    String decodedUrl = java.net.URLDecoder.decode(urls.get(0), "UTF-8");
                    URL nestedUrl = new URL(decodedUrl);
                    Map<String, List<String>> nestedParams = parseQuery(nestedUrl.getQuery());
                    if (nestedParams.containsKey("mithrasMsgId")) {
                        return Long.parseLong(nestedParams.get("mithrasMsgId").get(0));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析消息url异常", e);
        }
        return null;
    }

    private static Map<String, List<String>> parseQuery(String query) {
        Map<String, List<String>> params = new HashMap<>();
        if (query != null) {
            String[] paramPairs = query.split("&");
            for (String paramPair : paramPairs) {
                String[] pair = paramPair.split("=");
                if (pair.length > 1) {
                    String key = pair[0];
                    String value = pair[1];
                    params.putIfAbsent(key, new ArrayList<>());
                    params.get(key).add(value);
                }
            }
        }
        return params;
    }


}
