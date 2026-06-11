package cn.zswltech.mithras.third.tianyancha.client.handler.impl;

import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycHolderResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 企业股东
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycHolderApiHandler extends AbstractTycApiHandler<TycBaseReq, TycHolderResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/ic/holder/2.0";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_HOLDER;
    }

    @Override
    public String getUrl(TycBaseReq req) {
        try {
            return new StringBuilder(url)
                    .append("?pageSize=").append(req.getPageSize())
                    .append("&pageNum=").append(req.getPageNum())
                    .append("&keyword=").append(URLEncoder.encode(req.getKeyword(), "UTF-8"))
                    .toString();
        } catch (UnsupportedEncodingException e) {
            log.error("天眼查接口参数encode失败, reqData:{}", JSON.toJSONString(req));
            throw new MithrasException("天眼查接口参数encode失败");
        }
    }

    @Override
    public TycHolderResp analyResponseResult(String response) {
        return JSONObject.parseObject(response, TycHolderResp.class);
    }

}
