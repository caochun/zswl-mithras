package cn.zswltech.mithras.third.service.dataminer;

import cn.hutool.crypto.SmUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.third.service.dataminer.req.DataMinerBasicReq;
import cn.zswltech.mithras.third.service.dataminer.rsp.DataMinerRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description 数据管理平台调用客户端
 */
@Slf4j
@Component
public class DataMinerClient {
    @Resource
    private DataMinerConfig dataMinerConfig;

    public <REQ extends DataMinerBasicReq, RSP> DataMinerRsp<RSP> doRequest(REQ request, Class<RSP> clz) {
        String authSubject = dataMinerConfig.getAuthSubject();
        long timestamp = System.currentTimeMillis();
        String secret = dataMinerConfig.getAuthSecret();
        String sign = SmUtil.sm3(authSubject + timestamp + secret);
        String url = this.getUrl(request.dataMinerApiInfo());
        Map<String, Object> map = new HashMap<>();
        map.put("params", request);
        HttpRequest httpRequest = HttpUtil.createPost(url);
        httpRequest.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON.getValue());
        httpRequest.header("Auth-Subject", authSubject);
        httpRequest.header("Auth-Time", String.valueOf(timestamp));
        httpRequest.header("Auth-Sign", sign);
        String reqBody = JSONUtil.toJsonStr(map);
        log.info("调用dataminer开始，请求参数:{}", reqBody);
        try (HttpResponse httpResponse = httpRequest.body(reqBody).execute()) {
            String rspBody = httpResponse.body();
            log.info("调用dataminer结束，返回参数:{}", rspBody);
            JSONObject jsonObject = JSONUtil.parseObj(rspBody);
            DataMinerRsp<RSP> rsp = new DataMinerRsp<>();
            rsp.setSuccess(jsonObject.getBool("success"));
            rsp.setCode(jsonObject.getInt("code"));
            rsp.setMessage(jsonObject.getStr("message"));
            // 封装
            if (request.dataMinerApiInfo().isPageable()) {
                JSONObject obj = jsonObject.getJSONObject("data");
                if (Objects.nonNull(obj)) {
                    DataMinerRsp.PageListResult<RSP> pageListResult = new DataMinerRsp.PageListResult<>();
                    pageListResult.setTotal(obj.getInt("total"));
                    pageListResult.setPages(obj.getInt("pages"));
                    pageListResult.setPageSize(obj.getInt("pageSize"));
                    pageListResult.setCurrentPage(obj.getInt("currentPage"));
                    JSONArray arr = obj.getJSONArray("list");
                    if (Objects.nonNull(arr)) {
                        pageListResult.setList(JSONUtil.toList(arr, clz));
                    } else {
                        pageListResult.setList(Collections.emptyList());
                    }
                    rsp.setPageResult(pageListResult);
                }
            } else {
                JSONArray arr = jsonObject.getJSONArray("data");
                if (Objects.nonNull(arr)) {
                    rsp.setDataList(JSONUtil.toList(arr, clz));
                } else {
                    rsp.setDataList(Collections.emptyList());
                }
            }
            return rsp;
        } catch (Exception e) {
            log.error("调用dataminer发生异常", e);
            throw new MithrasException("请求异常");
        }
    }

    private String getUrl(DataMinerApiInfoEnum dataMinerApiInfoEnum) {
        return dataMinerConfig.getProtocol() + "://" + dataMinerConfig.getHost() + dataMinerApiInfoEnum.getUri();
    }
}
