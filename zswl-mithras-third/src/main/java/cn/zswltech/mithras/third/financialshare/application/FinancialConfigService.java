
package cn.zswltech.mithras.third.financialshare.application;

import cn.hutool.core.util.ObjectUtil;

import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.financialshare.model.CQRelatedMithrasInfo;
import cn.zswltech.mithras.third.financialshare.client.handle.AccessTokenHandle;
import cn.zswltech.mithras.third.financialshare.client.handle.AppTokenHandle;
import cn.zswltech.mithras.third.financialshare.client.req.AccessTokenREQ;
import cn.zswltech.mithras.third.financialshare.client.req.AppTokenREQ;
import cn.zswltech.mithras.third.financialshare.client.resp.AccessTokenRSP;
import cn.zswltech.mithras.third.financialshare.client.resp.AppTokenRSP;
import cn.zswltech.mithras.third.financialshare.client.resp.FinancialCommonRSP;
import cn.zswltech.mithras.third.financialshare.client.config.AppAuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ClassName FinancialShareService
 * @Description ClientId com.cncico.esb.opr.zszl
 * @Author jackerhe
 * @Date 2022/10/20 3:33 下午
 * @Version 1.0
 **/

@Service
@Slf4j
public class FinancialConfigService {

    @Resource
    private AppAuthConfig appAuthConfig;

    /*@Resource
    private StringRedisTemplate stringRedisTemplate;*/

    @Resource
    private AccessTokenHandle accessTokenHandle;

    @Resource
    private AppTokenHandle appTokenHandle;

    @Resource
    private CQRelatedMithrasInfoService cqRelatedMithrasInfoService;


/*    private static final String FINANCIAL_ACCESS_TOKEN_KEY = "FINANCIAL_ACCESS_TOKEN_KEY";*/

    /*public Boolean sendPostToFinancial(FinancialUrlENUM financialUrlENUM, Object param){
        if(ObjectUtil.isEmpty(financialUrlENUM)){
            throw new MithrasException("接口信息不能为空");
        }
        String errorMessage;
        try {
            String rsp = postFinancialExec(financialUrlENUM.operate, appBaseUrl + financialUrlENUM.url, param, getAccessToken());
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if(!ObjectUtil.isEmpty(jsonObject) && "success".equals(jsonObject.getString("state"))){
                return Boolean.TRUE;
            }
            errorMessage = jsonObject.toJSONString();
        }catch (Exception e){
            errorMessage = e.getMessage();
            log.warn("FinancialShareService sendPostToFinancial error", e);
        }
        //错误记录，等待重试
        ExternalExceptionInfo externalExceptionInfo = new ExternalExceptionInfo();
        externalExceptionInfo.setBizModel(financialUrlENUM.name());
        externalExceptionInfo.setBizInfo(JSONObject.toJSONString(param));
        externalExceptionInfo.setStatus(0);
        externalExceptionInfo.setException(errorMessage);
        externalExceptionInfoService.save(externalExceptionInfo);
        return Boolean.FALSE;
    }*/

    public String getUrl(String url){
        return appAuthConfig.getAppBaseUrl() + url;
    }

    public Map<String, String> getHttpHeadParam(FinancialUrlENUM receivverInfo) {
        Map<String, String> map = new HashMap<>();
        //不过esb可能没有操作码
        if(ObjectUtil.isNotEmpty(receivverInfo.operate)){
            map.put("OperationCode", receivverInfo.operate);
        }
        map.put("ClientId", appAuthConfig.getClientId());
        String accessToken = getAccessToken(receivverInfo);
        if(ObjectUtil.isNotEmpty(accessToken)){
            map.put("access_token", accessToken);
        }
        return map;
    }

    public Map<String, String> getHttpHeadParam2(FinancialUrlENUM receivverInfo) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        String accessToken = getAccessToken(receivverInfo);
        if(ObjectUtil.isNotEmpty(accessToken)){
            map.put("accesstoken", accessToken);
        }
        return map;
    }

    //配置苍穹与租赁付款编号联系
    @Transactional(rollbackFor = Throwable.class)
    public void cqRelatedMithras(FinancialCommonRSP result, String recordSource) {
        List<CQRelatedMithrasInfo> list = new ArrayList<>();
        List<FinancialCommonRSP.FinancialRSPBody> data = result.getData();
        if (ObjectUtil.isEmpty(data) || ObjectUtil.equals(data.size(), 0)) {
            return;
        }
        data.forEach(rsp -> {
            CQRelatedMithrasInfo cqRelatedMithrasInfo = new CQRelatedMithrasInfo();
            cqRelatedMithrasInfo.setBillno(rsp.getBillno());
            cqRelatedMithrasInfo.setCollectionCode(rsp.getSourcebillno());
            cqRelatedMithrasInfo.setRecordSource(recordSource);
            list.add(cqRelatedMithrasInfo);
        });
        cqRelatedMithrasInfoService.saveBatch(list);
    }

    /**
     *获取苍穹tocken
     **/
    private AccessTokenRSP.AccessTokenData financialGetTocken(FinancialUrlENUM receivverInfo) {
        AppTokenREQ appTokenREQ = buildAppToken();
        AppTokenRSP appTokenRSP = appTokenHandle.execute(appTokenREQ);
        log.info("FinancialShareService merchantGetTocken RSP {}", appTokenRSP);
        if (appTokenHandle.isExecuteSuccess(appTokenRSP)) {
            AccessTokenREQ accessTokenREQ = buildAccessToken(ObjectUtil.isEmpty(appTokenRSP.getData()) ? null : appTokenRSP.getData().getApp_token(), receivverInfo);
            AccessTokenRSP accessTokenRSP = accessTokenHandle.execute(accessTokenREQ);
            //log.info("FinancialShareService merchantGetTocken accessRsp {}", accessTokenRSP);
            if(accessTokenHandle.isExecuteSuccess(accessTokenRSP)){
                return ObjectUtil.isEmpty(accessTokenRSP.getData()) ? null : accessTokenRSP.getData();
            }
            //log.info("FinancialShareService merchantGetTocken faild get accessToken {}", accessTokenRSP);
        }
        return null;
    }
   /* private String postFinancialExec(String operate, String url, Object param, String token) {
        // header填充
        HttpHeaders httpHeaders = buildFinancialHead(operate, token);
        ResponseEntity<String> response = null;
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(param), httpHeaders);
        // 发送请求
        try {
            log.info("FinancialShareService postFinancialExec url {}, param {}", url, param);
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            log.warn("DataShareServiceImpl postMerchants has error url : {}, o : {}", url, param);
        }
        return response == null ? null : response.getBody();
    }

    private HttpHeaders buildFinancialHead(String operate, String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON.toString());
        httpHeaders.add("OperationCode", operate);
        httpHeaders.add("ClientId", appAuthConfig.getClientId());
        if(ObjectUtil.isNotEmpty(token)){
            httpHeaders.add("token", token);
        }
        return httpHeaders;
    }*/

    private AppTokenREQ buildAppToken(){
        AppTokenREQ appTokenREQ = new AppTokenREQ();
        appTokenREQ.setAppId(appAuthConfig.getAppId());
        appTokenREQ.setAppSecuret(appAuthConfig.getAppSecuret());
        appTokenREQ.setAccountId(appAuthConfig.getAccountId());
        appTokenREQ.setTenantid(appAuthConfig.getTenantid());
        appTokenREQ.setLanguage("china");
        return appTokenREQ;
    }

    public AccessTokenREQ buildAccessToken(String appToken, FinancialUrlENUM receivverInfo){
        AccessTokenREQ tokenREQ = new AccessTokenREQ();
        tokenREQ.setAccountId(appAuthConfig.getAccountId());
        tokenREQ.setTenantid(appAuthConfig.getTenantid());
        tokenREQ.setApptoken(appToken);
        if (ObjectUtil.isNotEmpty(appAuthConfig.getUser2()) && ObjectUtil.isNotEmpty(receivverInfo) && (FinancialUrlENUM.CQ2_ACCOUNT_APPLICATION.equals(receivverInfo) || FinancialUrlENUM.CQ2_PAYMENT.equals(receivverInfo) || FinancialUrlENUM.CQ2_COLLECTION.equals(receivverInfo))) {
            tokenREQ.setUser(appAuthConfig.getUser2());
        } else if (ObjectUtil.isNotEmpty(appAuthConfig.getUser3()) && ObjectUtil.isNotEmpty(receivverInfo) && ObjectUtil.equals(FinancialUrlENUM.CQ2_ACCOUNT_AGE_ADD, receivverInfo)  ) {
            tokenREQ.setUser(appAuthConfig.getUser3());
        } else {
            tokenREQ.setUser(appAuthConfig.getUser());
        }
        tokenREQ.setUsertype(appAuthConfig.getUsertype());
        return tokenREQ;
    }

   /* private void setAccessToken(String token, long timeout){
        stringRedisTemplate.opsForValue().set(FINANCIAL_ACCESS_TOKEN_KEY, token, timeout, TimeUnit.SECONDS);
    }*/

    public String getAccessToken(FinancialUrlENUM receivverInfo){

        //重新获取tocken
        //测试用本地取tocken
        AccessTokenRSP.AccessTokenData  tokenData = financialGetTocken(receivverInfo);
        if(tokenData != null && ObjectUtil.isNotNull(tokenData)){
            return tokenData.getAccess_token();
        }
        return null;


        /*String token = stringRedisTemplate.opsForValue().get(FINANCIAL_ACCESS_TOKEN_KEY);
        if(ObjectUtil.isNull(token)){

            long expireTime = Long.parseLong(tokenData.getExpire_time());

            setAccessToken(token, expireTime);
            *//*setAccessToken(cqRelatedMithrasInfoService.getOne(Wrappers.<CQRelatedMithrasInfo>lambdaQuery()
            .eq(CQRelatedMithrasInfo::getCollectionCode, FINANCIAL_ACCESS_TOKEN_KEY)).getBillno(), 60*30);*//*
        }
        return token;*/
    }
}

