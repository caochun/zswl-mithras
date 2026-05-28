package cn.zswltech.mithras.service.service.third.financial.impl.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.third.FinancialDevUrlENUM;
import cn.zswltech.mithras.service.enums.third.FinancialUrlENUM;
import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.ExceptionRequestInfoService;
import cn.zswltech.mithras.service.service.third.financial.FinancialApiHandler;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialConfigService;
import cn.zswltech.mithras.service.service.third.financial.req.CQ2AccountAgeDeleteREQ;
import cn.zswltech.mithras.service.service.third.financial.resp.CQ2FinancialBaseRSP;
import cn.zswltech.mithras.service.service.third.financial.resp.FinancialBaseRSP;
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
public class CQ2AccountAgeDeleteHandle extends FinancialApiHandler<List<CQ2AccountAgeDeleteREQ>, FinancialBaseRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestInfoService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_ACCOUNT_AGE_DELETE;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_ACCOUNT_AGE_DELETE.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_ACCOUNT_AGE_DELETE.url);
    }

    @Override
    public CQ2FinancialBaseRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2FinancialBaseRSP.class);
    }

    @Override
    public FinancialBaseRSP execute(List<CQ2AccountAgeDeleteREQ> reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_ACCOUNT_AGE_DELETE);
    }

    @Override
    public void cqRelatedMithras(FinancialBaseRSP result){
        //todo 这里目前没有明确的关系要对应
        //financialConfigService.cqRelatedMithras(result, RecordSourceEnum.CQ2_PAYMENT.name());
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQ2AccountAgeDeleteREQ.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQ2AccountAgeDeleteREQ> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return String.valueOf(reqData.get(0).getBillno());
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(List<CQ2AccountAgeDeleteREQ> reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if(CollectionUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            CQ2AccountAgeDeleteREQ req = reqData.get(0);
            exceptionRequestInfo.setSource(req.getSource());
            exceptionRequestInfo.setBusinessKey(req.getBusinessKey());
            exceptionRequestInfo.setBusinessTitle(req.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public List<CQ2AccountAgeDeleteREQ> getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(reqString, CQ2AccountAgeDeleteREQ.class);
    }

}
