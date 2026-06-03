package cn.zswltech.mithras.service.job;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.capital.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareManager;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.service.service.capital.FinanceFlowAutoWriteOffService;
import cn.zswltech.mithras.service.service.share.DataShareManagerService;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.third.baorong.infrastructure.client.handle.BRFlowQueryHandle;
import cn.zswltech.mithras.third.baorong.infrastructure.client.req.BRFlowHistoryReq;
import cn.zswltech.mithras.third.baorong.infrastructure.client.req.CwgsApiAppUser;
import cn.zswltech.mithras.third.baorong.infrastructure.client.req.CwgsHead;
import cn.zswltech.mithras.third.baorong.infrastructure.client.rsp.BRFlowHistoryRsp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 *  维护苍穹推送拉取数据job
 * @author: jackerhe
 * @date: 2024/6/4 3:40 下午
 **/
@Slf4j
@Component
public class CQFinanceJob {

    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private FinanceFlowAutoWriteOffService financeFlowAutoWriteOffService;
    @Resource
    private DataShareManagerService dataShareManagerService;
    @Resource
    private BRFlowQueryHandle brFlowQueryHandle;

    private Integer MAX = 5;

    private final static String BR_FLOW_FULL = "br_flow_full";

    //每日晚上9点 银行流水还未核销完毕，也触发收款单的统一推送
    @XxlJob("sendWriteOffNotice")
    public void sendWriteOffNotice() {
          try {
              List<FinanceFlowRecord> list = financeFlowRecordService.list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                      .eq(FinanceFlowRecord::getSendCqFlag, YesOrNoNumberEnum.NO.getCode())
                      .eq(FinanceFlowRecord::getWriteOffStatus, FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name()));
              if (ObjectUtil.isNotEmpty(list)) {
                  list.forEach(record -> {
                      financeFlowAutoWriteOffService.writeOffNotice(Collections.singletonList(record.getId()));
                  });
              }
          } catch (Exception e) {
            log.error("银行流水核销发生异常", e);
        }
    }

    //每日拉取本月银行流水
    @XxlJob("fullFlowRecord")
    public void fullFlowRecord() {
        try {
            financeFlowRecordService.fullSync(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        } catch (Exception e) {
            log.error("每日拉取本月银行流水发生异常", e);
        }
    }

    //每日拉取保融银行流水
    @XxlJob("fullBRFlowRecord")
    public void fullBRFlowRecord() {
        try {
            //获取管理信息信息
            DataShareManager dataShareManager = dataShareManagerService.getOne(Wrappers.<DataShareManager>lambdaQuery()
                    .eq(DataShareManager::getModelName, BR_FLOW_FULL)
                    .last(cn.zswltech.mithras.service.util.StringUtil.mysqlLimitOne()));
            LocalDateTime now = LocalDateTime.now();
            if (ObjectUtil.isEmpty(dataShareManager)) {
                dataShareManager = new DataShareManager();
                dataShareManager.setEndTime(now.withDayOfMonth(1));
                dataShareManager.setPageNum(1);
                dataShareManager.setPageSize(100);
                dataShareManager.setModelName(BR_FLOW_FULL);
                dataShareManagerService.save(dataShareManager);
            }
            BRFlowHistoryReq req = buildBRFlowHistoryReq(now, dataShareManager.getPageNum(), dataShareManager.getPageSize());
            BRFlowHistoryRsp rsp = brFlowQueryHandle.execute(req);
            while (checkRspResult(rsp) && req.getBody().get_pageSize() < MAX && req.getBody().get_pageIndex() < MAX) {
                req.getBody().set_pageSize(req.getBody().get_pageSize() + 1);
                rsp = brFlowQueryHandle.execute(req);
            }
        } catch (Exception e) {
            log.error("拉取保融银行流水发生异常", e);
        }
    }

    private boolean checkRspResult(BRFlowHistoryRsp rsp) {
        return rsp != null && rsp.getBody() != null && rsp.getBody().getList() != null && rsp.getBody().getList().size() > 0;
    }

    private BRFlowHistoryReq buildBRFlowHistoryReq(LocalDateTime startTime, Integer page, Integer pageSize) {
        BRFlowHistoryReq req = new BRFlowHistoryReq();
        CwgsHead head = new CwgsHead();
        BRFlowHistoryReq.BRFlowHistoryReqBody body = req.new BRFlowHistoryReqBody();
        CwgsApiAppUser cwgsApiAppUser = new CwgsApiAppUser();
        head.setServiceCode("CWGS001");
        head.setServiceNo("10001001");
        head.setConsumerCode("10001");
        head.setConsumerId("001");
        head.setChannelType("ESB");
        //服务编号+消费端编码+时间戳
        head.setReqSequence(String.join(head.getServiceNo(), head.getConsumerCode(), String.valueOf(System.currentTimeMillis())));
        head.setTrandate(startTime.format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN)));
        head.setTrantime(startTime.format(DateTimeFormatter.ofPattern(DatePattern.PURE_TIME_PATTERN)));

        cwgsApiAppUser.setOperator("ZJZSRZZLYXGS");
        cwgsApiAppUser.setOrgan("ZJZSRZZL");

        body.setTradedate(startTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        body.set_pageSize(page);
        body.set_pageIndex(pageSize);

        req.setCwgsHead(head);
        req.setCwgsApiAppUser(cwgsApiAppUser);
        req.setBody(body);
        return req;
    }

}
