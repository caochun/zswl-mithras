package cn.zswltech.mithras.third.tianyancha.client.handler.impl;

import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycJudicialResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 司法协助
 * http://open.tianyancha.com/open/756
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycJudicialApiHandler extends AbstractTycApiHandler<TycBaseReq, TycJudicialResp> {

    private static final String url = "http://open.api.tianyancha.com/services/v4/open/judicial";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_JUDICIAL;
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
    public TycJudicialResp analyResponseResult(String response) {
        // response = "{\"result\":{\"total\":1,\"historyCount\":null,\"items\":[{\"executeNoticeNum\":\"（2015）沪二中法执字第242号\",\"executedPersonCid\":412811649,\"publicityDate\":\"-\",\"stockExecutedCompany\":\"上海东浩环保装备有限公司\",\"executedPersonHid\":null,\"stockExecutedCid\":978740860,\"executedPerson\":\"中国纺织机械股份有限公司\",\"assId\":\"749a20044\",\"equityAmount\":\"9957.14万人民币\",\"id\":20044,\"typeState\":\"股权变更\",\"executedPersonType\":\"1\",\"executiveCourt\":\"上海市第二中级人民法院\"}]},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycJudicialResp.class);
    }

}
