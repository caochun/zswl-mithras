package cn.zswltech.mithras.others;

import cn.zswl.notice.mapper.ExceptionInfoMapper;
import cn.zswl.notice.mapper.NoticeMapper;
import cn.zswl.notice.model.Notice;
import cn.zswl.notice.service.ExportService;
import cn.zswl.notice.util.RemoteUtil;
import cn.zswltech.mithras.dto.message.MessageHandleREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.enums.MessageChannelEnum;
import cn.zswltech.mithras.service.service.message.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/12/9
 * @description
 */
@Slf4j
public class GroupOAExceptionRetryTest extends ApplicationTest {
    @Resource
    private ExceptionInfoMapper exceptionInfoMapper;
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private ExportService exportService;
    @Resource
    private RemoteUtil remoteUtil;
    @Resource
    private MessageService messageService;

    @Test
    public void retryTest() {
        // step 1 补发网络异常的消息
        /*log.info("补发消息开始<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        List<Long> ignoreIds = Arrays.asList(1L,2L,3L,28L,29L,31L,38L,39L,45L,46L,49L,50L,80L,81L,83L,84L,85L,86L,101L,102L,103L,104L,106L,107L,108L,117L,118L,119L,120L,121L,139L,140L,141L,142L,143L,144L,145L,164L,165L,166L,167L,168L,169L,170L,171L,172L,173L,174L,175L,176L,177L,196L,197L,198L,199L,200L,201L,202L,203L,204L,205L,206L,207L,208L,209L,210L,211L,212L,213L,214L,215L,216L,217L,218L,219L,220L,221L,225L,228L,231L,235L,236L,237L,256L,257L,258L,259L,261L,262L,266L,277L,279L,280L,281L,282L,301L,302L,303L,321L,322L,323L,327L,328L,330L,331L,332L,333L,334L,335L,336L,337L,338L,340L,341L,344L,347L,348L,351L,352L,364L,365L,377L,378L,379L,392L,393L,396L,397L,400L,401L,402L,403L,411L,431L,434L,438L,439L,440L,441L,442L,443L,444L,445L,446L,447L,450L,454L,455L,456L,457L,458L,459L,460L,461L,462L,463L,464L,475L,476L,477L,488L,490L,491L,492L,493L,494L,495L,496L,497L,498L,501L,505L,506L,508L,518L,519L,520L,521L,554L,555L,556L,557L,558L,559L,571L,580L,581L,584L,585L,586L,587L,588L,589L,590L,591L,592L,593L,601L,602L,603L,604L,605L,606L,607L,608L,609L,610L,632L,633L,634L,652L,653L,654L,655L,656L,657L,658L,660L,661L,670L,671L,674L,679L,692L,705L,706L,710L,711L,712L,713L,714L,715L,716L,717L,718L,719L,728L,729L,730L,731L,732L,733L,734L,735L,736L,737L,738L,739L,742L,743L,745L,748L,749L,750L,753L,756L,803L,804L,805L,806L,813L,814L,815L,830L,831L,832L,833L,834L,835L,836L,837L,838L,839L,869L,870L,886L,887L,895L,919L,921L,922L,923L,924L,925L,926L,929L,930L,938L,939L,940L,941L,942L,943L,944L,945L,946L,947L,949L,951L,952L,953L,954L,955L,956L,958L,962L,964L,965L,996L,997L,998L,999L,1000L,1001L,1002L,1003L,1004L,1005L,1079L,1080L,1082L,1092L,1093L,1094L);
        String startTime = "2022-12-09 00:00:00";
        String endTime = "2022-12-13 23:59:59";
        ExceptionInfoExample example = new ExceptionInfoExample();
        ExceptionInfoExample.Criteria criteria = example.createCriteria();
        criteria.andGmtCreateBetween(DateUtil.parse(startTime, DatePattern.NORM_DATETIME_PATTERN), DateUtil.parse(endTime, DatePattern.NORM_DATETIME_PATTERN));
        List<ExceptionInfo> dataList = exceptionInfoMapper.selectByExample(example);
        for (ExceptionInfo exceptionInfo : dataList) {
            try {
                if (!Objects.equals("do post 异常", exceptionInfo.getMsg())) {
//                    log.info("非调用失败记录不处理[{}]", exceptionInfo.getBizInfo());
                    continue;
                }
                Notice retryNotice = JSONUtil.toBean(exceptionInfo.getBizInfo(), Notice.class);
                if (ignoreIds.contains(retryNotice.getId())) {
//                    log.info("处于忽略列表中，忽略不处理[{}]", exceptionInfo.getBizInfo());
                    continue;
                }
                // 调用集团OA接口（默认待办，状态的变更由下一步操作实现）
//                log.info("调用集团OA接口重新发送[{}]", JSONUtil.toJsonStr(retryNotice));
                remoteUtil.remotePublishForOA(retryNotice, RemoteIsRemarkEnum.getCodeByNoticeType(retryNotice.getType()));
                ignoreIds.add(retryNotice.getId());
            } catch (Exception e) {
                log.error("补发发生异常[id: {}]", JSONUtil.toJsonStr(exceptionInfo.getId()), e);
            }
        }
        log.info("补发消息结束<<<<<<<<<<<<<<<<<<<<<<<<<<<");*/
        log.info("同步状态开始<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        // step 2 同步状态
        List<Long> toSyncNoticeIds = Arrays.asList(8581L);
        // 设为已读已办
        for (Long noticeId : toSyncNoticeIds) {
            try {
                Notice notice = noticeMapper.selectByPrimaryKey(noticeId);
                if (Objects.isNull(notice)) {
                    continue;
                }
                if (notice.getDealUser().contains("*")) {
                    continue;
                }
                String[] mobiles = notice.getDealUser().split(",");
                for (String s : mobiles) {
                   /* if(ObjectUtil.notEqual(s, "15974139025")){
                        continue;
                    }*/
                    // 已读
                    exportService.makeReaded(s, Collections.singletonList(notice.getId()), true);
                }
                // 已办
                MessageHandleREQ req = new MessageHandleREQ();
                req.setNoticeId(noticeId);
                req.setMessageChannel(MessageChannelEnum.PC.name());
                req.setNeedQA(true);
                req.setMithrasUserId(3L);//admin
                messageService.handle(req);
            } catch (Exception e) {
                log.error("同步状态发生异常[id: {}]", noticeId, e);
            }
        }
        log.info("同步状态结束<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
