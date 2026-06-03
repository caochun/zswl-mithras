package cn.zswltech.mithras.service.service.share.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.service.service.share.req.CQ2AttachmentSaveReq;
import cn.zswltech.mithras.service.service.share.rsp.CQ2AcchmentSaveRsp;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQ2WithdrawReq;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQReceiveRentREQ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName
 * @Description 苍穹 外部系统附件补充
 **/
@Component
public class CQ2AttachmentSaveHandle extends FinancialApiHandler<CQ2AttachmentSaveReq, CQ2AcchmentSaveRsp> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_ATTACHMENT_SAVE;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_ATTACHMENT_SAVE.url);
        }

//        TODO  上线需要调整
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_ATTACHMENT_SAVE.url);
    }

    @Override
    public CQ2AcchmentSaveRsp analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2AcchmentSaveRsp.class);
    }

    @Override
    public CQ2AcchmentSaveRsp execute(CQ2AttachmentSaveReq reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam2(FinancialUrlENUM.CQ2_ATTACHMENT_SAVE);
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(() -> execute(JSON.parseObject(base.getReqData(), CQ2AttachmentSaveReq.class)), threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(CQ2AttachmentSaveReq reqData){
        if(reqData == null){
            return null;
        }
        return reqData.getCico_billno();
    }

}
