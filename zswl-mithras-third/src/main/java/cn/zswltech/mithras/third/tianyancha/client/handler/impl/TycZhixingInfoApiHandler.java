package cn.zswltech.mithras.third.tianyancha.client.handler.impl;

import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycZhixingInfoResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 被执行人
 * http://open.tianyancha.com/open/839
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycZhixingInfoApiHandler extends AbstractTycApiHandler<TycBaseReq, TycZhixingInfoResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/jr/zhixinginfo/2.0";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_ZHIXING_INFO;
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
    public TycZhixingInfoResp analyResponseResult(String response) {
        // response = "{\"result\":{\"total\":1,\"items\":[{\"caseCode\":\"(2021)冀0903执847号\",\"partyCardNum\":\"91130900566****6791\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"沧州市运河区人民法院\",\"caseCreateTime\":1618243200000,\"execMoney\":\"227837\"},{\"caseCode\":\"(2021)冀0929执327号\",\"partyCardNum\":\"56619567-9\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1612108800000,\"execMoney\":\"200000\"},{\"caseCode\":\"(2021)冀0929执188号\",\"partyCardNum\":\"91130900566****6791\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1611158400000,\"execMoney\":\"37366\"},{\"caseCode\":\"(2019)冀0929执恢242号\",\"partyCardNum\":\"56619567-9\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1573574400000,\"execMoney\":\"19221000\"},{\"caseCode\":\"(2019)冀0929执恢243号\",\"partyCardNum\":\"56619567-9\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1573574400000,\"execMoney\":\"6850352\"},{\"caseCode\":\"(2019)冀0929执恢241号\",\"partyCardNum\":\"9113090056****6791\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1573574400000,\"execMoney\":\"528895\"},{\"caseCode\":\"(2019)冀0929执恢240号\",\"partyCardNum\":\"9113090056****6791\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1573574400000,\"execMoney\":\"656822\"},{\"caseCode\":\"(2019)冀0929执恢244号\",\"partyCardNum\":\"9113090056****6791\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1573574400000,\"execMoney\":\"7791824\"},{\"caseCode\":\"(2019)冀0929执恢247号\",\"partyCardNum\":\"1309031984****1557\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1573574400000,\"execMoney\":\"4058238\"},{\"caseCode\":\"(2017)冀0929执31号\",\"partyCardNum\":\"91130900566****6791\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1483459200000,\"execMoney\":\"3300000\"},{\"caseCode\":\"(2017)冀0929执32号\",\"partyCardNum\":\"28738412-9\",\"pname\":\"河北展发房地产开发有限公司\",\"execCourtName\":\"献县人民法院\",\"caseCreateTime\":1483459200000,\"execMoney\":\"5000000\"}]},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycZhixingInfoResp.class);
    }

}
