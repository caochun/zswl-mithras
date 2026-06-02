package cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.impl;

import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycConsumptionRestrictionResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 限制消费令
 * http://open.tianyancha.com/open/1014
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycConsumptionRestrictionApiHandler extends AbstractTycApiHandler<TycBaseReq, TycConsumptionRestrictionResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/jr/consumptionRestriction/2.0";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_CONSUMPTION_RESTRICTION;
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
    public TycConsumptionRestrictionResp analyResponseResult(String response) {
        // response = "{\"result\":{\"total\":1,\"items\":[{\"caseCode\":\"(2021)京01执376号\",\"filePath\":\"https://static1.tianyancha.com/pan_zhixing/restrict_pdf/981489a096084577937490990b182c63.pdf\",\"publishDate\":1621440000000,\"xname\":\"刘延峰\",\"hcgid\":null,\"applicant\":\"北京国昊天诚知识产权代理有限公司\",\"applicantCid\":\"12193975\",\"qyinfoAlias\":\"乐视网\",\"qyinfo\":\"乐视网信息技术（北京）股份有限公司\",\"caseCreateTime\":1615305600000,\"alias\":\"刘\",\"id\":32894273,\"cid\":14427175}]},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycConsumptionRestrictionResp.class);
    }

}
