package cn.zswltech.mithras.third.baorong.client.handle;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.retry.model.ExceptionRequestInfo;
import cn.zswltech.mithras.third.baorong.persistence.model.BrFlowRecord;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.baorong.application.BrFlowRecordService;
import cn.zswltech.mithras.third.baorong.client.BRApiHandler;
import cn.zswltech.mithras.third.baorong.client.req.BRFlowHistoryReq;
import cn.zswltech.mithras.third.baorong.client.rsp.BRFlowHistoryRsp;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName PaymentHandle
 * @Description 苍穹应付单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
@Slf4j
public class BRFlowQueryHandle extends BRApiHandler<BRFlowHistoryReq, BRFlowHistoryRsp> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    @Resource
    private BrFlowRecordService brFlowRecordService;

    @Value(value = "${br.address:}")
    private String brBaseUrl;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.BR_FLOW_QUERY;
    }

    @Override
    public String getUrl() {
        return brBaseUrl + FinancialUrlENUM.BR_FLOW_QUERY.url;
    }

    @Override
    public BRFlowHistoryRsp analyResponseResult(String response) {
        return JSONObject.parseObject(response, BRFlowHistoryRsp.class);
    }

    //这里返回批次号
    @Override
    public BRFlowHistoryRsp execute(BRFlowHistoryReq reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.BR_FLOW_QUERY);
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> execute(JSON.parseObject(base.getReqData(), BRFlowHistoryReq.class)));
    }

    @Override
    public void relatedMithras(BRFlowHistoryRsp result) {
        if (ObjectUtil.isEmpty(result) || ObjectUtil.isEmpty(result.getBody()) || ObjectUtil.isEmpty(result.getBody().getList())) {
            return;
        }
        List<BrFlowRecord> flowRecords = new ArrayList<>();
        BrFlowRecord brFlowRecord;
        List<String> bruids = result.getBody().getList().stream().map(BRFlowHistoryRsp.Transaction::getBRUID).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(bruids)) {
            return;
        }
        log.info("保融流水bruids {}", bruids);
        Set<String> bruidSet = brFlowRecordService.list(Wrappers.<BrFlowRecord>lambdaQuery().in(BrFlowRecord::getBruid, bruids)).stream().map(BrFlowRecord::getBruid).collect(Collectors.toSet());
        log.info("保融流水bruidSet {}", bruidSet);

        for (BRFlowHistoryRsp.Transaction body : result.getBody().getList()) {
            if (!bruidSet.contains(body.getBRUID())) {
                brFlowRecord = new BrFlowRecord();
                BeanUtil.copyProperties(body, brFlowRecord, true);
                brFlowRecord.setOrgName(body.getORG_NAME());
                brFlowRecord.setAmount(LongUtil.other2Long(body.getAMOUNT() == null ? "0" : body.getAMOUNT().toPlainString()));
                brFlowRecord.setCurrentbalance(LongUtil.other2Long(body.getCURRENTBALANCE() == null ? "0" : body.getCURRENTBALANCE().toPlainString()));
                flowRecords.add(brFlowRecord);
            }
        }
        //保存至数据库
        if (!flowRecords.isEmpty()) {
            log.info("保融流水新增 {}", flowRecords);
            brFlowRecordService.saveBatch(flowRecords);
            brFlowRecordService.doSyncCqFlow();
        }
    }

}
