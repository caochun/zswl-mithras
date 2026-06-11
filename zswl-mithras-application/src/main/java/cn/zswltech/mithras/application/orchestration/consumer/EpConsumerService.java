package cn.zswltech.mithras.application.orchestration.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.riskcontrol.opinion.RiskControlOpinionMonitorAddREQ;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionEnum;
import cn.zswltech.mithras.system.mapper.SystemConfigMapper;
import cn.zswltech.mithras.system.mapper.model.SystemConfig;
import cn.zswltech.mithras.third.ep.model.EpCaseInfo;
import cn.zswltech.mithras.third.ep.model.EpChangeInfo;
import cn.zswltech.mithras.third.ep.model.EpCourtAnnounce;
import cn.zswltech.mithras.third.ep.model.EpCourtSession;
import cn.zswltech.mithras.third.ep.service.EpCaseInfoService;
import cn.zswltech.mithras.third.ep.service.EpChangeInfoService;
import cn.zswltech.mithras.third.ep.service.EpCourtAnnounceService;
import cn.zswltech.mithras.third.ep.service.EpCourtSessionService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 *  消费者Listener
 * @author ZHANGXIN
 */
@Service
@RocketMQMessageListener(
        topic = "opi_scan_data_message",
        consumerGroup = "${producer.group}"
)
@Slf4j
public class EpConsumerService implements RocketMQListener<String> {

    @Resource
    private EpChangeInfoService epChangeInfoService;
    @Resource
    private EpCaseInfoService epCaseInfoService;
    @Resource
    private EpCourtAnnounceService epCourtAnnounceService;
    @Resource
    private EpCourtSessionService epCourtSessionService;
    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;

    public static final String CHANGE_INFO = "CHANGE_INFO";
    public static final String COURT_ANNOUNCE = "COURT_ANNOUNCE";
    public static final String COURT_SESSION = "COURT_SESSION";
    public static final String CASE_INFO = "CASE_INFO";
    private final static String config_key = "FHC_OPINION_SWITCH";
    private final static String config_value = "CLOSE";

    @Override
    public void onMessage(String message) {
        log.info("收到工商信息舆情消息:{} ",message);
        try {
            // 通过开关控制是否接受金控舆情数据
            if (getSwitchClose()){
                log.info("金控舆情开关已关闭,不在接收金控数据");
                return;
            }
            JSONObject jsonObject = JSON.parseObject(message);
            String model = jsonObject.getString("model");
            // 提取 data 模块下的数据
            JSONObject data = JSON.parseObject(message).getJSONObject("data");
            // 写死只消费上线后的数据   1704358982
            long consumeStartTime = 1704970800000L;
            long insertTime = data.getLong("insertTime");
            if (insertTime < consumeStartTime) {
                log.info("收到工商信息舆情消息，判定为历史数据，不进行处理");
                return;
            }
            switch (model) {
                //企业变更信息
                case CHANGE_INFO:
                    this.saveEpChangeInfo(data);
                    break;
                //法院公告
                case COURT_ANNOUNCE:
                    this.saveEpCourtAnnounce(data);
                    break;
                //开庭公告
                case COURT_SESSION:
                    this.saveEpCourtSession(data);
                    break;
                //立案信息
                case CASE_INFO:
                    this.saveEpCaseInfo(data);
                    break;
                default: {
                    log.error("未知的model:{}",model);
                }
            }
        }catch (Exception e) {
            log.error("工商信息舆情消息处理异常：{}",message, e);
        }
    }

    /**
     * 保存开庭公告
     * @param data
     */
    private void saveEpCourtSession(JSONObject data) {
        // 将 data 模块的数据映射到 EpCourtSession 对象
        EpCourtSession epCourtSession = JSON.toJavaObject(data, EpCourtSession.class);
        if(epCourtSession == null){
            log.error("message 解析失败：{}",JSON.toJSONString(data));
            return;
        }
        epCourtSession.setMsgId(data.getString("id"));
        epCourtSession.setMsgUpdateTime(data.getDate("updateTime"));
        //根据id和jsid查询是否存在，做消息的幂等
        if (epCourtSessionService.checkExistsByJsIdAndMsgId(epCourtSession.getJsid(),epCourtSession.getMsgId())){
            log.warn("开庭公告已经存在，不做处理:{}",JSON.toJSONString(epCourtSession));
            return;
        }
        log.info("保存开庭公告：{}",JSON.toJSONString(epCourtSession));
        epCourtSessionService.add(epCourtSession);
        //工商舆情存入租赁舆情
        this.saveRiskControlOpinionMonitor(data, RiskControlOpinionEnum.COURT_SESSION);
    }

    /***
     * 保存法院公告
     */
    private void saveEpCourtAnnounce(JSONObject data) {
        // 将 data 模块的数据映射到 EpCourtAnnounce 对象
        EpCourtAnnounce epCourtAnnounce = JSON.toJavaObject(data, EpCourtAnnounce.class);
        if(epCourtAnnounce == null){
            log.error("message 解析失败：{}",JSON.toJSONString(data));
            return;
        }
        epCourtAnnounce.setMsgId(data.getString("id"));
        epCourtAnnounce.setMsgUpdateTime(data.getDate("updateTime"));
        //根据id和jsid查询是否存在，做消息的幂等
        if (epCourtAnnounceService.checkExistsByJsIdAndMsgId(epCourtAnnounce.getMsgId(), epCourtAnnounce.getJsid())) {
            log.warn("法院公告信息已经存在:{}",JSON.toJSONString(epCourtAnnounce));
            return;
        }
        log.info("保存法院公告：{}",JSON.toJSONString(epCourtAnnounce));
        epCourtAnnounceService.add(epCourtAnnounce);
        //工商舆情存入租赁舆情
        this.saveRiskControlOpinionMonitor(data,RiskControlOpinionEnum.COURT_ANNOUNCE);
    }

    /***
     * 保存立案信息
     */
    private void saveEpCaseInfo(JSONObject data) {
        // 将 data 模块的数据映射到 EpCaseInfo 对象
        EpCaseInfo epCaseInfo = JSON.toJavaObject(data, EpCaseInfo.class);
        if(epCaseInfo == null){
            log.error("message 解析失败：{}",JSON.toJSONString(data));
            return;
        }
        epCaseInfo.setMsgId(data.getString("id"));
        epCaseInfo.setMsgUpdateTime(data.getDate("updateTime"));
        //根据id和jsid查询是否存在，做消息的幂等
        if(epCaseInfoService.checkExistsByJsIdAndMsgId(epCaseInfo.getJsid(),epCaseInfo.getMsgId())){
            log.warn("立案信息已存在，不再处理：{}",JSON.toJSONString(epCaseInfo));
            return;
        }
        log.info("保存立案信息：{}",JSON.toJSONString(epCaseInfo));
        epCaseInfoService.add(epCaseInfo);
        //工商舆情存入租赁舆情
        this.saveRiskControlOpinionMonitor(data,RiskControlOpinionEnum.CASE_INFO);
    }


    /***
     * 保存企业变更信息
     */
    public void saveEpChangeInfo(JSONObject data){
        // 将 data 模块的数据映射到 EpChangeInfo 对象
        EpChangeInfo epChangeInfo = JSON.toJavaObject(data, EpChangeInfo.class);
        if(epChangeInfo == null){
            log.error("message 解析失败：{}",JSON.toJSONString(data));
            return;
        }
        epChangeInfo.setMsgId(data.getString("id"));


        epChangeInfo.setMsgUpdateTime(data.getDate("updateTime"));
        //根据id和jsid查询是否存在，做消息的幂等
        if (epChangeInfoService.checkExistsByJsIdAndMsgId(epChangeInfo.getJsid(),epChangeInfo.getMsgId())){
           log.warn("企业变更信息已经存在，不做处理:{}",JSON.toJSONString(epChangeInfo));
           return;
        }
        epChangeInfo.setChangeItem(data.getInteger("change"));
        log.info("保存企业变更信息：{}",JSON.toJSONString(epChangeInfo));
        epChangeInfoService.add(epChangeInfo);
        //工商舆情存入租赁舆情
        this.saveRiskControlOpinionMonitor(data,RiskControlOpinionEnum.CHANGE_INFO);
     }

    /***
     * 工商舆情存入租赁舆情
     * @param data
     */
    private void saveRiskControlOpinionMonitor(JSONObject data,RiskControlOpinionEnum opinionEnum){
        RiskControlOpinionMonitorAddREQ req = new RiskControlOpinionMonitorAddREQ();
        req.setId(data.getLong("id"));
        req.setCreditCode(data.getString("creditCode"));
        req.setChiName(data.getString("companyName"));
        req.setTitle(opinionEnum.getLable());
        Date insertTime = data.getDate("insertTime");
        LocalDateTime infoPublDate = dateConvertLocalDateTime(insertTime);
        req.setInfoPublDate(infoPublDate);
        req.setLinkAddress(String.format( "https://www.baidu.com/s?wd=%s", Optional.ofNullable(req.getChiName()).map(URLUtil::encode).orElse("")));
        req.setRiskType(2);
        req.setHandleStatus(String.valueOf(RiskControlOpinionHandleStatus.IGNORED));
        req.setNewTypeOpinion(opinionEnum.getValue());
        riskControlOpinionMonitorService.add(req);
     }


    public static LocalDateTime dateConvertLocalDateTime(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private boolean getSwitchClose(){
        SystemConfig config = SpringUtil.getBean(SystemConfigMapper.class).selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getConfigValue)
                .eq(SystemConfig::getConfigKey, config_key)
                .eq(SystemConfig::getStatus, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));
        String configValue = config.getConfigValue();
        return StrUtil.isNotEmpty(configValue) && configValue.equals(config_value);
    }
}