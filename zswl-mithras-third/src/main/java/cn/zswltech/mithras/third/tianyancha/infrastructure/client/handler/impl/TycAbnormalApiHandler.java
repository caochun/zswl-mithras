package cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.impl;

import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycAbnormalResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 经营异常
 * http://open.tianyancha.com/open/848
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycAbnormalApiHandler extends AbstractTycApiHandler<TycBaseReq, TycAbnormalResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/mr/abnormal/2.0";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_ABNORMAL;
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
    public TycAbnormalResp analyResponseResult(String response) {
        // response = "{\"result\":{\"total\":1,\"items\":[{\"removeDate\":\"\",\"putReason\":\"通过登记的住所或者经营场所无法联系的\",\"putDepartment\":\"银川市市场监督管理局金凤分局\",\"removeDepartment\":\"\",\"removeReason\":\"\",\"putDate\":\"2021-07-12\"},{\"removeDate\":\"\",\"putReason\":\"未依照《企业信息公示暂行条例》第八条规定的期限公示年度报告的\",\"putDepartment\":\"银川市市场监督管理局金凤分局\",\"removeDepartment\":\"\",\"removeReason\":\"\",\"putDate\":\"2021-07-08\"}]},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycAbnormalResp.class);
    }

}
