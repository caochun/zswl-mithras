package cn.zswltech.mithras.third.aliyun.ocr.client.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.third.aliyun.ocr.client.req.OcrDetectReq;
import cn.zswltech.mithras.third.aliyun.ocr.client.resp.OcrDetectResp;
import cn.zswltech.mithras.foundation.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 阿里云ocr服务
 *
 * @author wangchuanhao
 * @date 2022/7/20 2:14 PM
 */
@Component
public class OcrApiHandler implements PlatformApiHandler<OcrDetectReq, OcrDetectResp> {

    @Value("${ocr.host}")
    private String ocrHost;
    @Value("${ocr.mock:false}")
    private Boolean mock;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.ALI_OCR;
    }

    @Override
    public OcrDetectResp response(String data) {
        return JSONObject.parseObject(data, OcrDetectResp.class);
    }

    @Override
    public OcrDetectResp execute(OcrDetectReq reqData) {
        String url = String.format("%s/ocrapidocker/ocrservice.json", ocrHost);
        long startTime = System.currentTimeMillis();

        try {

            String responseData = null;
            if (mock) {
                responseData = IoUtil.readUtf8(OcrApiHandler.class.getResourceAsStream("/mock/ocr/res1.json"));

            } else {
                responseData = HttpUtil.ocrRequest(url, BeanUtil.beanToMap(reqData));
            }
            long endTime = System.currentTimeMillis();
            log.info("request http,url:{},time:{}", url, endTime - startTime);

            OcrDetectResp result = this.response(responseData);
            return result;
        } catch (IOException e) {
            log.error("阿里云ocr调用超时", e);
            throw new MithrasException("ocr服务处理超时");
        }
    }

}
