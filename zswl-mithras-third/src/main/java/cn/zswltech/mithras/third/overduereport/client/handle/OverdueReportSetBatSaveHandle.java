package cn.zswltech.mithras.third.overduereport.client.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.overduereport.enums.OverdueReportDevUrlENUM;
import cn.zswltech.mithras.third.overduereport.enums.OverdueReportUrlENUM;
import cn.zswltech.mithras.third.retry.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.overduereport.client.OverdueReportApiHandler;
import cn.zswltech.mithras.third.overduereport.application.OverdueReportConfigService;
import cn.zswltech.mithras.third.overduereport.client.req.OverdueReportSetBatSaveReq;
import cn.zswltech.mithras.third.overduereport.client.rsp.OverdueReportSetBatSaveRSP;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName OverdueReportBatSaveHandle
 * @Description 应收逾期结算保存接口
 * @Author jackerhe
 * @Version 1.0
 **/
@Component
public class OverdueReportSetBatSaveHandle extends OverdueReportApiHandler<List<OverdueReportSetBatSaveReq>, OverdueReportSetBatSaveRSP> {

    @Resource
    private OverdueReportConfigService overdueReportConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.OVERDUE_REPORT_SET_BAT_SAVE;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return overdueReportConfigService.getUrl(OverdueReportDevUrlENUM.OVERDUE_REPORT_SET_BAT_SAVE.url);
        }
        return overdueReportConfigService.getUrl(OverdueReportUrlENUM.OVERDUE_REPORT_SET_BAT_SAVE.url);
    }

    @Override
    public OverdueReportSetBatSaveRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, OverdueReportSetBatSaveRSP.class);
    }

    @Override
    public OverdueReportSetBatSaveRSP execute(List<OverdueReportSetBatSaveReq> reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return overdueReportConfigService.getHttpHeadParam(OverdueReportUrlENUM.OVERDUE_REPORT_SET_BAT_SAVE);
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(getReqFromString(base.getReqData())),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<OverdueReportSetBatSaveReq> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getCico_origin_no();
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(List<OverdueReportSetBatSaveReq> reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if (CollectionUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            OverdueReportSetBatSaveReq req = reqData.get(0);
            //exceptionRequestInfo.setSource(req.get);
            //exceptionRequestInfo.setBusinessKey(req.getBusinessKey());
            //exceptionRequestInfo.setBusinessTitle(req.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public List<OverdueReportSetBatSaveReq> getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(reqString, OverdueReportSetBatSaveReq.class);
    }


    @Override
    public List<String> getBillNo(String reqString){
        List<OverdueReportSetBatSaveReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return Collections.emptyList();
        }
        return reqFromString.stream().map(OverdueReportSetBatSaveReq::getCico_origin_no).collect(Collectors.toList());
    }
}
