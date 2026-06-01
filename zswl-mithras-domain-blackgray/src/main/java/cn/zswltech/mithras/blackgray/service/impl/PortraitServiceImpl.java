package cn.zswltech.mithras.blackgray.service.impl;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.consts.PortraitRedisKeyConstants;
import cn.zswltech.mithras.blackgray.dto.external.SearchEnterpriseDTO;
import cn.zswltech.mithras.blackgray.service.PortraitService;
import cn.zswltech.mithras.blackgray.service.RedisService;
import cn.zswltech.mithras.blackgray.service.external.HsAuthToken;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PortraitServiceImpl implements PortraitService {

    @Value("${hengsheng.service.url}")
    private String opiUrl;

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisService redisService;

    @Autowired
    private HsAuthToken hsAuthToken;


    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            50, 200, 1, TimeUnit.HOURS,
            new LinkedBlockingQueue<>(), new DefaultThreadFactory("opiInvoke"));

    static {
        executor.allowCoreThreadTimeOut(true);
    }

    @Override
    public R<List<SearchEnterpriseDTO.EnterpriseDTO>> outerSearch(String enterpriseName, String creditCode) {
        String key = StringUtils.isNotBlank(enterpriseName) ? enterpriseName : creditCode;
        String value = redisService.get(PortraitRedisKeyConstants.OUTER_SEARCH + key);
        SearchEnterpriseDTO result;
        if (StringUtils.isNotBlank(value)) {
            result = JSON.parseObject(value, SearchEnterpriseDTO.class);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + hsAuthToken.getAuthToken());
            HttpEntity httpEntity = new HttpEntity(headers);
            String url = opiUrl + "/rdm/v1/rdm-cloud-ods-corp/ods_basic_enterprise_search/query?";
            if (StringUtils.isNotBlank(enterpriseName)) {
                url += "enterpriseName=" + enterpriseName;
            } else {
                url += "enterpriseInfo=" + creditCode;
            }
            long start = System.currentTimeMillis();
            ResponseEntity<SearchEnterpriseDTO> httpResonse = null;
            try {
                httpResonse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, SearchEnterpriseDTO.class);
            } catch (Exception e) {
                log.warn("恒生接口token过期，刷新token");
                hsAuthToken.refreshAuthToken();
                httpResonse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, SearchEnterpriseDTO.class);
            }
            result = httpResonse.getBody();
            redisService.set(PortraitRedisKeyConstants.OUTER_SEARCH + key, JSON.toJSONString(result), PortraitRedisKeyConstants.SEC_OF_DAY);
            long cost = System.currentTimeMillis() - start;
            log.info("opi httpGet url:{}, cost:{}, res:{}", url, cost, JSONObject.toJSONString(result));
        }

        if (result != null && result.getData() != null) {
            return R.ok(result.getData().getList());
        }
        return R.ok(new ArrayList<>());
    }

}
