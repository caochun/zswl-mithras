package cn.zswltech.mithras.third.tianyancha.client.handler.impl;

import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycEquityInfoResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 股权出质
 * http://open.tianyancha.com/open/845
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycEquityInfoApiHandler extends AbstractTycApiHandler<TycBaseReq, TycEquityInfoResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/mr/equityInfo/2.0";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_EQUITY_INFO;
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
    public TycEquityInfoResp analyResponseResult(String response) {
        // response = "{\"result\":{\"total\":1,\"items\":[{\"pledgeeList\":[{\"name\":\"中信银行股份有限公司呼和浩特分行\",\"id\":\"211058937\"}],\"regDate\":1543248000000,\"pledgor\":\"王凤龙\",\"certifNumberR\":\"非公示项\",\"pledgee\":\"中信银行股份有限公司呼和浩特分行\",\"regNumber\":\"A1800010107\",\"certifNumber\":\"非公示项\",\"companyList\":[{\"name\":\"中信银行股份有限公司呼和浩特分行\",\"id\":\"211058937\"},{\"name\":\"内蒙古大草原生态产业投资有限公司\",\"id\":\"236499922\"}],\"targetCompany\":{\"name\":\"内蒙古大草原生态产业投资有限公司\",\"id\":\"236499922\"},\"pledgorList\":[{\"name\":\"王凤龙\",\"id\":\"2066606298-c236499922\"}],\"equityAmount\":\"5400万元\",\"id\":24204120,\"state\":\"有效\",\"putDate\":1543248000000},{\"pledgeeList\":[{\"name\":\"中信银行股份有限公司呼和浩特分行\",\"id\":\"211058937\"}],\"regDate\":1543248000000,\"pledgor\":\"鄂尔多斯市山湾投资有限公司\",\"certifNumberR\":\"非公示项\",\"pledgee\":\"中信银行股份有限公司呼和浩特分行\",\"regNumber\":\"A1800010105\",\"certifNumber\":\"非公示项\",\"companyList\":[{\"name\":\"中信银行股份有限公司呼和浩特分行\",\"id\":\"211058937\"},{\"name\":\"鄂尔多斯市山湾投资有限公司\",\"id\":\"288006849\"},{\"name\":\"内蒙古大草原生态产业投资有限公司\",\"id\":\"236499922\"}],\"targetCompany\":{\"name\":\"内蒙古大草原生态产业投资有限公司\",\"id\":\"236499922\"},\"pledgorList\":[{\"name\":\"鄂尔多斯市山湾投资有限公司\",\"id\":\"288006849\"}],\"equityAmount\":\"3900万元\",\"id\":24204119,\"state\":\"有效\",\"putDate\":1543248000000},{\"pledgeeList\":[{\"name\":\"内蒙古大草原生态产业投资有限公司\",\"id\":\"236499922\"}],\"regDate\":1517414400000,\"pledgor\":\"王凤龙\",\"certifNumberR\":\"非公示项\",\"pledgee\":\"内蒙古大草原生态产业投资有限公司\",\"regNumber\":\"A1800004825\",\"certifNumber\":\"非公示项\",\"companyList\":[{\"name\":\"内蒙古大草原生态产业投资有限公司\",\"id\":\"236499922\"},{\"name\":\"通辽余粮畜业开发有限公司\",\"id\":\"246358157\"}],\"targetCompany\":{\"name\":\"通辽余粮畜业开发有限公司\",\"id\":\"246358157\"},\"pledgorList\":[{\"name\":\"王凤龙\",\"id\":\"2066606298-c246358157\"}],\"equityAmount\":\"158.3949万元\",\"id\":24235836,\"state\":\"有效\",\"putDate\":1517414400000}]},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycEquityInfoResp.class);
    }

}
