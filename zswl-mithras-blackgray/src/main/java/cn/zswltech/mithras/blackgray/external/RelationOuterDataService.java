package cn.zswltech.mithras.blackgray.external;


import cn.zswltech.mithras.blackgray.constant.RelationRedisKey;
import cn.zswltech.mithras.blackgray.dto.external.EntDownHolderDTO;
import cn.zswltech.mithras.blackgray.dto.external.HsCompanyInfoDTO;
import cn.zswltech.mithras.blackgray.service.RedisService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

@Service
@Slf4j
public class RelationOuterDataService {

    @Resource
    RestTemplate restTemplate;

    @Value("${hengsheng.service.url}")
    private String hengshengAddress;
    @Value("${hengsheng.companyInfoUrl}")
    private String companyInfoUrl;
    @Resource
    HsAuthToken hsAuthToken;
    @Resource
    RedisService redisService;
    @Value("${relation.drill.useCache:true}")
    private Boolean relationDrillUseCache;

    @Value("${hengsheng.entDownHolderUrl}")
    private String entDownHolderUrl;

    private LongAdder longAdder = new LongAdder();


    public HsCompanyInfoDTO requestCompanyInfo(String creditCode, String companyName){
        String key = StringUtils.isBlank(creditCode) ? companyName : creditCode;
        String responseBody = relationDrillUseCache ? redisService.get(RelationRedisKey.DRILL_COMPANY_INFO_KEY + key) : null;
        Boolean doRequest = false;
        if (StringUtils.isBlank(responseBody)) {
            String url = hengshengAddress + companyInfoUrl + "?company_name=" + ObjectUtils.defaultIfNull(companyName, "") + "&credit_code=" + ObjectUtils.defaultIfNull(creditCode, "");
            ResponseEntity<String> responseEntity = doRequest(url, hsAuthToken.getAuthToken(), hsAuthToken.getAppKey());
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                if (responseEntity.getStatusCodeValue() == 401) {
                    log.warn("恒生接口token过期，刷新token重试请求");
                    responseEntity = doRequest(url, hsAuthToken.refreshAuthToken(), hsAuthToken.getAppKey());
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.error("请求恒生企业信息查询接口失败，statusCode={},body={}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
                        return null;
                    }
                }
            }
            responseBody = responseEntity.getBody();
            doRequest = true;
        }


        try {
            JSONObject responseObject = JSON.parseObject(responseBody);
            HsCompanyInfoDTO data = JSON.parseObject(responseObject.getString("data"), HsCompanyInfoDTO.class);
            if (relationDrillUseCache && data != null && doRequest) {
                redisService.set(RelationRedisKey.DRILL_COMPANY_INFO_KEY + data.getCompany_name(), responseBody, RelationRedisKey.DRILL_API_RESULT_CACHE_SEC);
                redisService.set(RelationRedisKey.DRILL_COMPANY_INFO_KEY + data.getCredit_code(), responseBody, RelationRedisKey.DRILL_API_RESULT_CACHE_SEC);
            }
            return data;
        } catch (Exception e) {
            log.error("恒生企业信息查询接口结果解析失败,body={}", responseBody, e);
        }
        return null;
    }



    private ResponseEntity<String> doRequest(String url, String token, String appKey) {
        final String encodedUrl = UriComponentsBuilder.fromHttpUrl(url).build().toString();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.add("app_key", appKey);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        final ResponseEntity<String> exchange = restTemplate.exchange(encodedUrl, HttpMethod.GET, httpEntity, String.class);
        longAdder.increment();
        log.info("恒生接口访问结果,url={},statusCode={},body={}", url, exchange.getStatusCodeValue(), exchange.getBody());
        return exchange;
    }

    /**
     * 根据企业信用代码，请求接口获取该企业下级控股的其他企业信息，
     * @param creditCode 企业社会信用代码
     * @param ratioLowestLimit 控股比例下限。 低于该下限的控股关系将被过滤掉，减少数据量
     */
    public List<EntDownHolderDTO> entDownHolder(String creditCode, String name, BigDecimal ratioLowestLimit) {
        String key = StringUtils.isNotBlank(creditCode) ? creditCode : name;
        if (StringUtils.isBlank(key)) {
            return Collections.emptyList();
        }
        String responseBody = relationDrillUseCache ? redisService.get(RelationRedisKey.DRILL_ENT_DOWN_HOLDER_API_KEY + key) : null;
        Boolean doRequest = false;
        if (StringUtils.isBlank(responseBody)) {
            // 默认将控股比例限制在5%，低于5%的不做查询，不然数据量太大，接口请求次数多。 如果指定了下限，则按指定的查询
            String ratioLimit = ObjectUtils.defaultIfNull(ratioLowestLimit, new BigDecimal("0.05")).toString();
            String url = hengshengAddress + entDownHolderUrl + "?level=1&shareholding_ratio=" + ratioLimit + "&enterprise_info=" + ObjectUtils.defaultIfNull(creditCode, "") + "&enterprise_name=" + ObjectUtils.defaultIfNull(name, "");
            ResponseEntity<String> responseEntity = doRequest(url, hsAuthToken.getAuthToken(), hsAuthToken.getAppKey());
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                if (responseEntity.getStatusCodeValue() == 401) {
                    log.warn("恒生接口token过期，刷新token重试请求");
                    responseEntity = doRequest(url, hsAuthToken.refreshAuthToken(), hsAuthToken.getAppKey());
                    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.error("请求恒生企业股权下穿接口失败，statusCode={},body={}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
                        return Collections.emptyList();
                    }
                }
            }
            responseBody = responseEntity.getBody();
            doRequest = true;
        }

        try {
            final JSONObject jsonObject = JSON.parseObject(responseBody);
            final JSONObject dataObj = jsonObject.getJSONObject("data");
            final List<EntDownHolderDTO> downHolderDTOList = JSON.parseObject(dataObj.getString("level_nodes"), new TypeReference<List<EntDownHolderDTO>>() {});
            // 删除没有控股比例、企业名称、信用代码的数据，无法参与后续钻取
            downHolderDTOList.removeIf(e -> e.getCapital_ratio() == null || e.getCapital_ratio().doubleValue() == 0d || StringUtils.isBlank(e.getInvest_name()) || StringUtils.isBlank(e.getEnterprise_info()));
            // 成功的结果进行缓存，失败的抛异常记录日志返回空
            if (relationDrillUseCache && doRequest) {
                if (dataObj.containsKey("enterprise_info")) {
                    redisService.set(RelationRedisKey.DRILL_ENT_DOWN_HOLDER_API_KEY + dataObj.getString("enterprise_info"), responseBody, RelationRedisKey.DRILL_API_RESULT_CACHE_SEC);
                }
                if (dataObj.containsKey("enterprise_name")) {
                    redisService.set(RelationRedisKey.DRILL_ENT_DOWN_HOLDER_API_KEY + dataObj.getString("enterprise_name"), responseBody, RelationRedisKey.DRILL_API_RESULT_CACHE_SEC);
                }
            }
            return downHolderDTOList;
        } catch (Exception e) {
            log.error("恒生企业股权下穿接口结果解析失败,body={}", responseBody, e);
        }
        return Collections.emptyList();
    }

}
