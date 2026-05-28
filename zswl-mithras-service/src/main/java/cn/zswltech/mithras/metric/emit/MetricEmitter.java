package cn.zswltech.mithras.metric.emit;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.metric.emit.config.EmitRemoteCfg;
import cn.zswltech.mithras.metric.emit.model.req.EmitReq;
import cn.zswltech.mithras.metric.emit.model.req.concentration.ConcentrationAddReqBody;
import cn.zswltech.mithras.metric.emit.model.req.regime.RegimeInfoReqBody;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListREQ;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListRSP;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelationTradeReqBody;
import cn.zswltech.mithras.metric.emit.model.req.risk.event.RiskEventReqBody;
import cn.zswltech.mithras.metric.emit.model.req.risk.event.update.RiskEventUpdateReqBody;
import cn.zswltech.mithras.metric.emit.model.req.risk.index.RiskIndexReqBody;
import cn.zswltech.mithras.metric.emit.model.rsp.EmitRsp;
import cn.zswltech.mithras.metric.emit.model.rsp.relation.trade.RelationTradeRspData;
import cn.zswltech.mithras.metric.emit.model.rsp.required.RequireMetricRsp;
import cn.zswltech.mithras.metric.emit.model.rsp.required.RequireMetricSingleBody;
import cn.zswltech.mithras.metric.emit.model.rsp.risk.event.RiskEventRspData;
import cn.zswltech.mithras.metric.emit.model.rsp.risk.index.RiskIndexRspSingleBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

/**
 * @author yibin
 */
@Slf4j
@Component
public class MetricEmitter {
    private static final String URI_RISK_INDEX = "/openApi/riskIndex/system/submit";
    private static final String URI_RELATION_TRADE = "/openApi/relation/trade/batchCreateTrade";
    private static final String URI_RISK_EVENT = "/openApi/riskEvent/external/batchAdd";
    private static final String URI_RISK_EVENT_UPDATE = "/openApi/riskEvent/external/batchUpdate";
    private static final String URI_REGIME_INFO = "/openApi/remote/repository/add";
    private static final String URI_METRIC_REQUIRED = "/openApi/riskIndex/system/task";
    private static final String URI_CONCENTRATION_ADD = "/openApi/concentration/add";
    private static final String URI_RELATED_CLIENT_FETCH = "/openApi/relation/trade/client/party";


    @Resource
    private EmitRemoteCfg emitRemoteCfg;
    @Resource
    private RequestBuilder requestBuilder;

    public Set<String> requiredMetrics(LocalDate dataTime) {
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("orgCode", emitRemoteCfg.getAuthOrg());
        paramMap.put("dataTime", LocalDateTimeUtil.format(dataTime, "yyyyMM"));
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(paramMap);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_METRIC_REQUIRED)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .execute().body();

        EmitRsp<RequireMetricRsp> emitRsp = JSONUtil.toBean(rspStr, new TypeReference<EmitRsp<RequireMetricRsp>>() {
        }, true);
        log.info("获取必填指标列表完成. \n headerMap:{} \n jsonBody:{} \n emitRsp:{} ", headerMap, jsonBody, JSONUtil.toJsonPrettyStr(emitRsp));
        if (TRUE.equals(emitRsp.getSuccess())) {
            return emitRsp.getData().getIndexList().stream().map(RequireMetricSingleBody::getCode).collect(Collectors.toSet());
        } else {
            log.error("获取必填指标列表失败. error message:{}", JSONUtil.toJsonStr(emitRsp));
            return new HashSet<>();
        }

    }

    public String emitRelationTrade(RelationTradeReqBody req) {
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(req);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_RELATION_TRADE)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .execute().body();

        EmitRsp<RelationTradeRspData> emitRsp = JSONUtil.toBean(rspStr, new TypeReference<EmitRsp<RelationTradeRspData>>() {
        }, true);
        log.info("关联交易明细报送请求完成. \n headerMap:{} \n jsonBody:{} \n emitRsp:{} ", headerMap, jsonBody, JSONUtil.toJsonPrettyStr(emitRsp));
        if (TRUE.equals(emitRsp.getSuccess())) {
            return null;
        } else {
            log.error("关联交易明细报送失败. error message:{}", JSONUtil.toJsonStr(emitRsp));
            JSONObject jo = JSONUtil.parseObj(emitRsp);
            String error = (String) JSONUtil.getByPath(jo, "data.resultList[0].statusMsg");
            if (StrUtil.isBlank(error)) {
                error = emitRsp.getMessage();
            }
            return error;
        }
    }

    public boolean emitRiskIndex(RiskIndexReqBody body) {
        if (StrUtil.isBlank(body.getOrgCode())) {
            body.setOrgCode(emitRemoteCfg.getAuthOrg());
        }
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(body);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_RISK_INDEX)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .setConnectionTimeout((int) Duration.ofSeconds(5).toMillis())
                .execute().body();

        EmitRsp<List<RiskIndexRspSingleBody>> emitRsp = JSONUtil.toBean(rspStr, new TypeReference<EmitRsp<List<RiskIndexRspSingleBody>>>() {
        }, true);
        log.info("风险指标报送请求完成. \n headerMap:{} \n jsonBody:{} \n emitRsp:{} ", headerMap, jsonBody, JSONUtil.toJsonPrettyStr(emitRsp));
        if (TRUE.equals(emitRsp.getSuccess())) {
            return true;
        } else {
            log.error("风险指标报送失败. error message:{}", JSONUtil.toJsonStr(emitRsp));
            return false;
        }
    }

    public boolean emitRiskEvent(RiskEventReqBody body) {
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(body);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_RISK_EVENT)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .execute().body();

        EmitRsp<List<RiskEventRspData>> emitRsp = JSONUtil.toBean(rspStr, new TypeReference<EmitRsp<List<RiskEventRspData>>>() {
        }, true);
        log.info("风险事件报送请求完成. \n headerMap:{} \n jsonBody:{} \n emitRsp:{} ", headerMap, jsonBody, JSONUtil.toJsonPrettyStr(emitRsp));
        if (TRUE.equals(emitRsp.getSuccess())) {
            return true;
        } else {
            log.error("风险事件报送失败. error message:{}", JSONUtil.toJsonStr(emitRsp));
            return false;
        }
    }

    public boolean updateRiskEvent(RiskEventUpdateReqBody body) {
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(body);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_RISK_EVENT_UPDATE)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .execute().body();

        EmitRsp<List<RiskEventRspData>> emitRsp = JSONUtil.toBean(rspStr, new TypeReference<EmitRsp<List<RiskEventRspData>>>() {
        }, true);
        log.info("风险事件更新请求完成. \n headerMap:{} \n jsonBody:{} \n emitRsp:{} ", headerMap, jsonBody, JSONUtil.toJsonPrettyStr(emitRsp));
        if (TRUE.equals(emitRsp.getSuccess())) {
            return true;
        } else {
            log.error("风险事件更新失败. error message:{}", JSONUtil.toJsonStr(emitRsp));
            return false;
        }
    }

    public boolean syncRegimeInfo(RegimeInfoReqBody body) {
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(body);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_REGIME_INFO)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .execute().body();

        EmitRsp<List<RiskEventRspData>> emitRsp = JSONUtil.toBean(rspStr, new TypeReference<EmitRsp<List<RiskEventRspData>>>() {
        }, true);
        log.info("制度信息同步请求完成. \n headerMap:{} \n jsonBody:{} \n emitRsp:{} ", headerMap, jsonBody, JSONUtil.toJsonPrettyStr(emitRsp));
        if (TRUE.equals(emitRsp.getSuccess())) {
            return true;
        } else {
            log.error("制度信息同步失败. error message:{}", JSONUtil.toJsonStr(emitRsp));
            return false;
        }
    }

    /**
     * @param body
     * @return 错误信息；无则null
     */
    public String emitConcentration(ConcentrationAddReqBody body) {
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(body);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_CONCENTRATION_ADD)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .execute().body();

        log.info("集中度报送信息同步请求完成. \n headerMap:{} \n jsonBody:{} \n rspStr:{} ", headerMap, jsonBody, rspStr);
        EmitRsp rsp = JSONUtil.toBean(rspStr, EmitRsp.class);
        if (TRUE.equals(rsp.getSuccess())) {
            return null;
        } else {
            log.error("集中度报送同步失败. error message:{}", rspStr);
            return rsp.getMessage();
        }
    }

    /**
     * 获取关联交易客户名单
     * <p>
     * single one demo:
     * { "id":4438,
     * "partyCode":"1-91450103MA5KEQPY9X",
     * "relationCategoryCode":1301,
     * "name":"南宁明匠智能制造产业投资基金合伙企业(有限合伙)",
     * "creditCode":"91450103MA5KEQPY9X",
     * "entCategory":4,
     * "registeredCapital":10000,
     * "relationLevel":null,
     * "industry":"投资与资产管理",
     * "legalRepresentative":"珠江西江产业投资基金管理有限公司委派代表:饶珀",
     * "addressCountry":145772,
     * "addressProvince":20,
     * "addressCity":1715,
     * "addressDistrict":43117,
     * "addressDetail":"南宁市青秀区双拥路38号广西新谊金融投资大厦17层",
     * "source":2,
     * "createdBy":"20054367",
     * "updatedBy":"20054367",
     * "gmtCreate":"2023-03-08T15:12:21.000+00:00",
     * "gmtUpdate":"2023-03-08T15:45:10.000+00:00",
     * "natureCertCategory":null,
     * "natureCertNum":null,
     * "natureSex":null,
     * "natureNation":null,
     * "natureMarried":null,
     * "natureEdu":null,
     * "partyType":1,
     * "status":1,
     * "relationDesc":"【浙江省交通投资集团有限公司】股份/出资额占【浙江浙商金控有限公司】股本/资本总额【100.00%】；【浙江省交通投资集团有限公司】股份/出资额占【浙江沪杭甬高速公路股份有限公司】股本/资本总额【66.99%】；【浙江沪杭甬高速公路股份有限公司】股份/出资额占【浙江上三高速公路有限公司】股本/资本总额【51.00%】；【浙江上三高速公路有限公司】股份/出资额占【浙商证券股份有限公司】股本/资本总额【54.79%】；【浙商证券股份有限公司】股份/出资额占【浙江浙商证券资产管理有限公司】股本/资本总额【100.00%】；【浙江浙商证券资产管理有限公司】股份/出资额占【南宁明匠智能制造产业投资基金合伙企业(有限合伙)】股本/资本总额【64.00%】；【南宁明匠智能制造产业投资基金合伙企业(有限合伙)】被【浙江浙商金控有限公司】的控股股东控制",
     * "businessScope":"对智能制造产业的股权投资，股权投资业务，代理其他股权投资企业等机构或个人的股权投资业务，股权投资咨询业务，法律、行政法规允许的其他投资业务。（依法须经批准的项目，经相关部门批准后方可开展经营活动。）"
     * }
     *
     * @return
     */
    public List<RelatedClientListRSP> fetchRelatedClientList(RelatedClientListREQ req) {
        List<RelatedClientListRSP> result = new ArrayList<>();
        Map<String, String> headerMap = requestBuilder.buildHeaders(emitRemoteCfg);
        EmitReq emitReq = requestBuilder.buildBody(req);
        String jsonBody = JSONUtil.toJsonStr(emitReq);
        String rspStr = HttpUtil
                .createPost(emitRemoteCfg.getHostAddr() + URI_RELATED_CLIENT_FETCH)
                .headerMap(headerMap, true)
                .body(jsonBody)
                .execute().body();
        log.info("关联交易客户列表获取完成. \n headerMap:{} \n jsonBody:{} \n rspStr:{} ", headerMap, jsonBody, rspStr);
        EmitRsp rsp = JSONUtil.toBean(rspStr, EmitRsp.class);
        if (TRUE.equals(rsp.getSuccess())) {
            JSONArray dataList = JSONUtil.parseObj(rspStr).getJSONArray("data");
            result = JSONUtil.toList(dataList, RelatedClientListRSP.class);
        } else {
            log.error("制度信息同步失败. error message:{}", rspStr);
        }
        return result;
    }

}
