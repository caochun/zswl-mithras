package cn.zswltech.mithras.third.financialshare.infrastructure.client.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQ2AccountApplicationReq;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.CQ2FinancialBaseRSP;
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

/**
 * @ClassName PaymentHandle
 * @Description 苍穹应付单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class CQ2AccountApplicationHandle extends FinancialApiHandler<CQ2AccountApplicationReq, CQ2FinancialBaseRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_ACCOUNT_APPLICATION;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_ACCOUNT_APPLICATION.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_ACCOUNT_APPLICATION.url);
    }

    @Override
    public CQ2FinancialBaseRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2FinancialBaseRSP.class);
    }

    @Override
    public CQ2FinancialBaseRSP execute(CQ2AccountApplicationReq reqData) {
       return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_ACCOUNT_APPLICATION);
    }

    @Override
    public void cqRelatedMithras(CQ2FinancialBaseRSP result){
        //todo 这里目前没有明确的关系要对应
        //financialConfigService.cqRelatedMithras(result, RecordSourceEnum.PAYMENT.name());
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseObject(base.getReqData(), CQ2AccountApplicationReq.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(CQ2AccountApplicationReq reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.getCico_sourcebillno();
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(CQ2AccountApplicationReq reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if (ObjectUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            exceptionRequestInfo.setSource(reqData.getSource());
            exceptionRequestInfo.setBusinessKey(reqData.getBusinessKey());
            exceptionRequestInfo.setBusinessTitle(reqData.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public CQ2AccountApplicationReq getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return null;
        }
        return JSON.parseObject(reqString, CQ2AccountApplicationReq.class);
    }

    @Override
    public String getSituationDescription(String reqString) {
        CQ2AccountApplicationReq reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return null;
        }
        return reqFromString.getDescription();
    }

    @Override
    public List<String> getBillNo(String reqString){
        CQ2AccountApplicationReq reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(reqFromString.getCico_sourcebillno());
    }

}
