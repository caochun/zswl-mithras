package cn.zswltech.mithras.service.service.notice.job;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.metric.factor.RiskMetricFactorFileListReq;
import cn.zswltech.mithras.message.application.job.MessageNoticeJobService;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorFile;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.projectprocess.application.job.ProjReviewNoticeJobService;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import cn.zswltech.mithras.message.mapper.message.NoticeMessageBody;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.message.service.ZhfkNoticeService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 通知相关定时任务
 */
@Slf4j
@Component
public class NoticeJobServiceImpl implements ProjReviewNoticeJobService, MessageNoticeJobService {

    @Resource
    private ProjReviewService projReviewService;

    @Resource
    private RiskMetricFactorService metricFactorService;
    @Resource
    private UserService userService;
    @Resource
    private MessageService messageService;
    @Resource
    private ZhfkNoticeService zhfkNoticeService;

    //项目立项提醒评审
    @Override
    public void contractStartRentRemindJobHandler() {
        try {
            projReviewService.noticeClose();
        }catch (Exception e){
            log.error("projReviewNotice has error", e);
        }
    }

    //每月五号提醒财务主管 “请前往导入上月财报”
    // 对接主数据接口后无需再进行导入提醒
    @Deprecated
//    @XxlJob("noticeTreasurerMaintainReports")
    public void noticeTreasurerMaintainReports(String param) {
        try {
            log.info("noticeTreasurerMaintainReports get param {}", param);
            //判断是否五号及以后
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate nowDate = ObjectUtil.isEmpty(param) ? LocalDate.now() : LocalDate.parse(param, df);
            if(nowDate.getDayOfMonth() >= 5){
                //查询上月财报
                RiskMetricFactorFileListReq req = new RiskMetricFactorFileListReq();
                req.setSheetDate(nowDate);
                Page<RiskMetricFactorFile> riskMetricFactorFilePage = metricFactorService.fileList(req);
                if(ObjectUtil.isNotEmpty(riskMetricFactorFilePage) && ObjectUtil.isNotEmpty(riskMetricFactorFilePage.getRecords()) && riskMetricFactorFilePage.getRecords().size() > 0){
                    return;
                }
                //通知财务主管
                List<UserDO> usersByjobcod = userService.getUsersByjobcod(JobEnum.financialofficer.name());
                if(ObjectUtil.isNotEmpty(usersByjobcod)){
                    MessageModel messageModel = new MessageModel();
                    NoticeMessageBody bodie = new NoticeMessageBody();
                    messageModel.setFrom("系统提醒");
                    messageModel.setTo(usersByjobcod.stream().map(UserDO::getId).collect(Collectors.toList()));
                    messageModel.setTimestamp(new Date());
                    messageModel.setNeedOa(false);
                    bodie.setTitle("请前往导入上月财报");
                    bodie.setFlowid(nowDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    bodie.setContent(nowDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));//名称
                    bodie.setWorkflowname("维护报表");
                    bodie.setNodename("维护报表");
                    bodie.setAppurl(MessageUrlEnum.NOTICE_TREASURER_REPORT.appUrl);
                    bodie.setPcurl(MessageUrlEnum.NOTICE_TREASURER_REPORT.pcUrl);
                    messageModel.setBodie(bodie);
                    messageService.sendMessage(messageModel);
                }
            }
        }catch (Exception e){
            log.error("noticeTreasurerMaintainReports has error", e);
        }
    }

    //同步redis已读消息至数据库
    @Override
    public void syncMessage2DB(){
        zhfkNoticeService.syncRedisFlag();
    }

}
