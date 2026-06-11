package cn.zswltech.mithras.third.financialshare.client;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.MD5Util;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.financialshare.enums.FinancialRSPENUM;
import cn.zswltech.mithras.third.retry.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiRequestInspector;
import cn.zswltech.mithras.foundation.thirdparty.RequestModeEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.client.resp.FinancialBaseRSP;
import cn.zswltech.mithras.foundation.util.HttpUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

public abstract class FinancialApiHandler<T, F extends FinancialBaseRSP> implements PlatformApiHandler<T, F>, PlatformApiRequestInspector {

    protected static final String DATA = "data";

    @Value("${spring.profiles.active}")
    protected String active;

    protected static final String PRE = "pre";

    protected static final String PROD = "prod";


    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public F execute(T reqData) {
        //保存结果
        ExceptionRequestInfo exceptionRequestInfo = respSave(getRetryCount(), reqData, getBusinessId(reqData));
        syncCqRecord(reqData);
        RequestModeEnum requestMode = platformApi().requestModeEnum;
        String url = getUrl();

        //此处有个特殊处理，需注意
        String requestData = this.request(reqData);
        String responseData = null;
        try {
            long startTime = System.currentTimeMillis();
            // 构造请求地址
            log.info("FinancialApiHandler send cq url {}, requestData {}", url, requestData);
            // 根据请求类型发起请求

                try {
                    if (RequestModeEnum.GET.requestMode.equals(requestMode.requestMode)) {
                        responseData = HttpUtil.httpGetRequest(getAddParam(reqData, url), getHttpHeadParam());
                    } else {
                        responseData = HttpUtil.httpPostJsonRequest(url, requestData, getHttpHeadParam());
                    }
                } catch (IOException e) {
                    log.info("FinancialApiHandler end http,url:{},time:{} error", url, e);
                }
            long endTime = System.currentTimeMillis();
            log.info("FinancialApiHandler end http,url:{},time:{}, responseData : {}", url, endTime - startTime, responseData);
        } catch (Exception e) {
            log.error("FinancialApiHandler connection fail, url:{}, request params:{}, error:{}", url, requestData, e);
        }
        F result = null;
        if (ObjectUtil.isNotEmpty(responseData)) {
            result = this.response(responseData);
            //回写状态
            if(ObjectUtil.isNotEmpty(exceptionRequestInfo)){
                exceptionRequestInfo.setRetryFlag(isExecuteSuccess(result) ? 1 : 0);
                exceptionRequestInfo.setResponse(responseData);
                getBean(ExceptionRequestRecordService.class).updateById(exceptionRequestInfo);
            }
            //保存请求返回
            if (isExecuteSuccess(result))
                //苍穹建立映射关系
                cqRelatedMithras(result);
        }
        return result;
    }

    @Override
    public boolean isExecuteSuccess(F resData) {
        // 权限验证成功
        if(ObjectUtil.isNotEmpty(resData)){
            if (FinancialRSPENUM.SUCCESS.getResult().equals(resData.getState()) || ObjectUtil.equals(resData.getSuccess(), Boolean.TRUE) || ObjectUtil.equals(Boolean.TRUE, resData.getStatus())) {
                return Boolean.TRUE;
            } else {
                log.info("FinancialApiHandler {} execute error errorCode : {} message : {}", platformApi().apiName, resData.getErrorCode(), resData.getMessage());
            }
        }
        return Boolean.FALSE;
    }

    public abstract String getUrl();

    public String getAddParam(T reqData, String url){
        return url;
    }

    public void cqRelatedMithras(F result){
    }

    public int getRetryCount(){
        return 3;
    }

    /**
     * 保存与苍穹交互联系
     * 按需调用
     **/
    public void syncCqRecord(T reqData){

    }

    //请求头携带参数
    public abstract Map<String,String> getHttpHeadParam();

    @Override
    public String request(T reqData) {
        return this.wrapRequestParam(reqData);
    }

    @Override
    public F response(String data) {
        return this.analyResponseResult(data);
    }

    public String wrapRequestParam(T reqData) {
        if(PlatformApiEnum.CQ_APP_TOKEN.equals(platformApi()) || PlatformApiEnum.CQ_ACCESS_TOKEN.equals(platformApi())){
            return JSONObject.toJSONString(reqData);
        }
        HashMap<String , T> hashMap = new HashMap();
        hashMap.put(DATA ,reqData);
        //统一处理为苍穹格式
        return JSONObject.toJSONString(hashMap);
    }

    public abstract F analyResponseResult(String response);

    public String getBusinessId(T reqData){
        return null;
    }


    /**
     * 失败记录是否需要保存重试，调用请求前保存，调用后修改状态
     * @param count 最大重试次数
     * @param reqData  请求参数
     * @param businessId 业务ID,标识此条消息唯一，防止保存相同失败数据
     * @return 默认不需要
     */
    // @Transactional(rollbackFor = Throwable.class)
    public ExceptionRequestInfo respSave(Integer count, T reqData, String businessId) {
        if (!isFailureSave()) {
            return null;
        }
        String reqDataString = JSON.toJSONString(reqData);
        String reqDataMd5 = MD5Util.MD5(String.valueOf(reqData));
        ExceptionRequestRecordService requestInfoService = SpringContextHolder.getBean(ExceptionRequestRecordService.class);
        ExceptionRequestInfo exceptionRequestInfo = requestInfoService.getOne(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(platformApi().name()), ExceptionRequestInfo::getPlatform, platformApi().name())
                .eq(ObjectUtil.isNotEmpty(businessId), ExceptionRequestInfo::getBusinessId, businessId)
//                    .eq(ExceptionRequestInfo::getReqDataMd5, reqDataMd5)
                .eq(ExceptionRequestInfo::getRetryFlag, YesOrNoNumberEnum.NO.getCode())
                .orderByDesc(ExceptionRequestInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(exceptionRequestInfo)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            exceptionRequestInfo.setPlatform(platformApi().name());
            exceptionRequestInfo.setMaxRetryAmount(count);
            exceptionRequestInfo.setReqData(reqDataString);
            exceptionRequestInfo.setReqDataMd5(reqDataMd5);
            exceptionRequestInfo.setBusinessId(businessId);
        } else {
            exceptionRequestInfo.setReqDataMd5(reqDataMd5);
            exceptionRequestInfo.setReqData(reqDataString);
            exceptionRequestInfo.setRetryAmount(exceptionRequestInfo.getRetryAmount() + 1);
        }
        exceptionRequestInfo.setRetryFlag(YesOrNoNumberEnum.NO.getCode());
        ExceptionRequestInfo exceptionBase = getExceptionBase(reqData);
        if(ObjectUtil.isNotEmpty(exceptionBase)) {
            exceptionRequestInfo.setSource(exceptionBase.getSource());
            exceptionRequestInfo.setBusinessKey(exceptionBase.getBusinessKey());
            exceptionRequestInfo.setBusinessTitle(exceptionBase.getBusinessTitle());
        }
        requestInfoService.saveOrUpdate(exceptionRequestInfo);
        return exceptionRequestInfo;
    }

    /**
     * 这里用来填充 source， businessKey， businessTitle
     **/
    public ExceptionRequestInfo getExceptionBase(T reqData) {
        return null;
    }

    public T getReqFromString(String reqString) {
        return null;
    }

    public String getSituationDescription(String reqString) {
        return null;
    }

    public List<String> getBillNo(String reqString){
        return Collections.emptyList();
    }

}
