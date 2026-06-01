package cn.zswltech.mithras.service.service.third.overduereport.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.third.OverdueReportDevUrlENUM;
import cn.zswltech.mithras.service.enums.third.OverdueReportUrlENUM;
import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.service.service.third.overduereport.OverdueReportApiHandler;
import cn.zswltech.mithras.service.service.third.overduereport.OverdueReportConfigService;
import cn.zswltech.mithras.service.service.third.overduereport.req.OverdueReportSetDelReq;
import cn.zswltech.mithras.service.service.third.overduereport.rsp.OverdueReportSetDelRSP;
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
 * @Description 应收逾期结算删除接口
 * @Author jackerhe
 * @Version 1.0
 **/
@Component
public class OverdueReportSetDelHandle extends OverdueReportApiHandler<List<OverdueReportSetDelReq>, OverdueReportSetDelRSP> {

    @Resource
    private OverdueReportConfigService overdueReportConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.OVERDUE_REPORT_SET_DEL;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return overdueReportConfigService.getUrl(OverdueReportDevUrlENUM.OVERDUE_REPORT_SET_DEL.url);
        }
        return overdueReportConfigService.getUrl(OverdueReportUrlENUM.OVERDUE_REPORT_SET_DEL.url);
    }

    @Override
    public OverdueReportSetDelRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, OverdueReportSetDelRSP.class);
    }

    @Override
    public OverdueReportSetDelRSP execute(List<OverdueReportSetDelReq> reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return overdueReportConfigService.getHttpHeadParam(OverdueReportUrlENUM.OVERDUE_REPORT_SET_DEL);
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
    public String getBusinessId(List<OverdueReportSetDelReq> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getBillno();
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(List<OverdueReportSetDelReq> reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if (CollectionUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            OverdueReportSetDelReq req = reqData.get(0);
            //exceptionRequestInfo.setSource(req.get);
            //exceptionRequestInfo.setBusinessKey(req.getBusinessKey());
            //exceptionRequestInfo.setBusinessTitle(req.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public List<OverdueReportSetDelReq> getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(reqString, OverdueReportSetDelReq.class);
    }


    @Override
    public List<String> getBillNo(String reqString){
        List<OverdueReportSetDelReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return Collections.emptyList();
        }
        return reqFromString.stream().map(OverdueReportSetDelReq::getBillno).collect(Collectors.toList());
    }
}
