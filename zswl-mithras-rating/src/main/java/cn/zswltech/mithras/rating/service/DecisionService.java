package cn.zswltech.mithras.rating.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.decision.engine.api.common.dto.R;
import cn.zswltech.decision.engine.dto.decision.*;
import cn.zswltech.mithras.dto.kpi.KpiExpectedLossDecisionQuery;
import cn.zswltech.mithras.dto.rating.RatingAccessCheckRSP;
import cn.zswltech.mithras.dto.rating.RatingParamFieldApprovalRSP;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteEclResult;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteResult;
import cn.zswltech.mithras.rating.enums.RatingFetchMethodEnum;
import cn.zswltech.mithras.rating.feign.DecisionApiClient;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DecisionService {


    /**
     * 客户评级
     */

    private final String MODEL_SCORE = "card_result";
    private final String DEFAULT_RATE = "pd";
    private final String QUALITATIVE_SCORE = "qualitative_sc";
    private final String QUANTITATIVE_SCORE = "quantitative_sc";
    private final String FIRST_SCORE = "rank";
    private final String SCORE = "final_evaluation_result";
//    private final String MODEL_SCORE_MAP = "card_result";
//    private final String SCORE_MAP = "pre_evaluation_result";
//    private final String SCORE_MAP2 = "paramsValueMap";
//    private final String DEFAULT_MAP = "pd";
//    private final String FINAL_SCORE_MAP = "final_evaluation_result";
//    private final String FINAL_SCORE_MAP2 = "paramsValueMap";
//    private final String ADJUST_SCORE_MAP = "adjusted_level";
//
//
//    private final String ADJUST_SCORE = "adjustScore";
//    private final String VALUE = "value";
//    private final String FINAL_EVALUATION_RESULT = "final_evaluation_result";
//    private final String SCORE = "rank";
//    private final String QUALITATIVE_SCORE = "qualitative_sc";
//    private final String QUANTITATIVE_SCORE = "quantitative_sc";

    /**
     * 客户评级准入校验
     */
    private final String CLIENT_RANK_ACCESS = "rate_access_or_not";
    private final String CLIENT_AREA_ACCESS = "access_or_not";
    private final String CLIENT_ACCESS_VALUE = "paramsValueMap";
    private final String RANK_VALUE = "rate_access_or_not";
    private final String AREA_VALUE = "access_or_not";



    private final String FINAL_SCORE = "finalScore";


    /**
     * 债项评级
     */
    private final String CLIENT_QUOTA = "output_p_c_results_1";
    private final String CREDIT_MEASURES_PRICE = "enhancement_measures";

    private final List<String> RATING_ADJUST_FACTOR_LIST = Arrays.asList("zxzx_subject_score_coefficient","tyl_customer_rating","xmzzx_evaluation_subject");
//    private final String RATING_ADJUST_FACTOR = "zxzx_subject_score_coefficient";
//    private final String RATING_ADJUST_FACTOR_NORMAL = "tyl_customer_rating";
//    private final String RATING_ADJUST_FACTOR_PRJ = "xmzzx_evaluation_subject";
    private final String GROUP_QUOTA = "group_quota";


//    private final String CLIENT_QUOTA_MAP = "output_p_c_results_1";
//    private final String RATING_ADJUST_FACTOR = "zxzx_subject_score_coefficient";
//    private final String RATING_ADJUST_FACTOR_NORMAL = "tyl_customer_rating";
//    private final String RATING_ADJUST_FACTOR_PRJ = "xmzzx_evaluation_subject";
//    private final String GROUP_QUOTA = "group_quota";
//    private final String MEASURES = "enhancement_measures";


    private final String SCORE_CARD_NAME = "scoreCardName";
    private final String SCORE_CARD_VERSION = "scoreCardVersion";
    private final String SCORE_CARD_CODE = "scoreCardCode";

    /**
     * 额外字段
     */
    private final String FORMAL = "formal";
    private final String UNIT = "unit";


    @Resource
    private DecisionApiClient decisionApiClient;


    /**
     * 预期信用损失减值ECL服务
     * @param query
     * @return
     */
    public DecisionExecuteEclResult eclExecute(KpiExpectedLossDecisionQuery query){
        DecisionREQ decisionREQ = new DecisionREQ();
        decisionREQ.setServiceCode("new_zl_ecl");
        Map<String, Object> param = new HashMap<>();
        for (Field field : query.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                param.put(field.getName(), field.get(query));
            } catch (IllegalAccessException e) {
                log.warn("读取ECL决策字段失败:{}", field.getName(), e);
            }
        }
        decisionREQ.setParam(param);
        decisionREQ.setEnableDetail(true);
        try {
            R<DecisionRSP> rsp = decisionApiClient.execute(decisionREQ);
            log.info("决策服务API接口(zl_ecl):request:{}, response:{}", JSONObject.toJSONString(decisionREQ), JSONObject.toJSONString(rsp));
            if(rsp.getCode() != 200){
                log.error("调用失败 -> message:{},完整错误错误信息:{}",JSONObject.toJSONString(rsp.getMessage()),JSONObject.toJSONString(rsp));
                return null;
            }
            DecisionRSP data = rsp.getData();
            Map<String, Object> outputMap = data.getOutputMap();
            Map<String, Object> eclMap = (Map<String, Object>)outputMap.get("ecl");
            DecisionExecuteEclResult result = new DecisionExecuteEclResult();
            if(CollectionUtils.isNotEmpty(eclMap)) {
                result.setEcl(Optional.ofNullable(eclMap.get("value")).map(m -> new BigDecimal(m.toString())).orElse(null));
            }
            Map<String, DecisionExecuteEclResult.OutputValue> outputValueMap = new HashMap<>();
            outputMap.forEach((k, v) -> outputValueMap.put(k, BeanUtil.copyProperties(v, DecisionExecuteEclResult.OutputValue.class)));
            result.setOutputMap(outputValueMap);
            result.setTraceId(data.getTraceId());
            return result;
        }catch (Exception e){
            log.error("远程调用发生未知异常:",e);
        }
        return null;
    }

    /**
     * 评分
     * @param serviceCode
     * @param param
     * @return
     */
    public DecisionExecuteResult execute(String serviceCode,Map<String, Object> param){
        DecisionREQ decisionREQ = new DecisionREQ();
        decisionREQ.setServiceCode(serviceCode);
        decisionREQ.setParam(param);
        decisionREQ.setEnableDetail(true);
        try {
            R<DecisionRSP> rsp = decisionApiClient.execute(decisionREQ);
            log.info("决策服务API接口(评分):request:{}, response:{}", JSONObject.toJSONString(decisionREQ), JSONObject.toJSONString(rsp));
            if(rsp.getCode() != 200){
                log.error("调用失败 -> message:{},完整错误错误信息:{}",JSONObject.toJSONString(rsp.getMessage()),JSONObject.toJSONString(rsp));
                return null;
            }
            DecisionRSP data = rsp.getData();
            Map<String, Object> outputMap = data.getOutputMap();
            ContextRecord contextRecord = data.getContextRecord();
            List<ContextRecord.Param> resultParamList = contextRecord.getParamList();
            Map<String, Object> resultMap = resultParamList.stream().collect(Collectors.toMap(ContextRecord.Param::getCode, ContextRecord.Param::getValue, (m1,m2) -> m1));
            DecisionExecuteResult decisionExecuteResult = new DecisionExecuteResult();
            if(serviceCode.contains("client")) {
//                Map<String, Object> modelScoreMap = (Map<String, Object>) Optional.ofNullable(outputMap.get(MODEL_SCORE_MAP)).orElse(new HashMap<>());
//                Map<String, Object> modelScoreMap2 = (Map<String, Object>) Optional.ofNullable(modelScoreMap.get(SCORE_MAP2)).orElse(new HashMap<>());
//                Map<String, Object> scoreMap = (Map<String, Object>) Optional.ofNullable(outputMap.get(SCORE_MAP)).orElse(new HashMap<>());
//                Map<String, Object> scoreMap2 = (Map<String, Object>) Optional.ofNullable(scoreMap.get(SCORE_MAP2)).orElse(new HashMap<>());
//                Map<String, Object> defaultMap = (Map<String, Object>) Optional.ofNullable(outputMap.get(DEFAULT_MAP)).orElse(new HashMap<>());
//                Map<String, Object> finalScoreMap = (Map<String, Object>) Optional.ofNullable(outputMap.get(FINAL_SCORE_MAP)).orElse(new HashMap<>());
//                Map<String, Object> finalScoreMap2 = (Map<String, Object>) Optional.ofNullable(finalScoreMap.get(FINAL_SCORE_MAP2)).orElse(new HashMap<>());
//
//                decisionExecuteResult.setScore(Optional.ofNullable(finalScoreMap2.get(FINAL_EVALUATION_RESULT)).map(String::valueOf).orElse(null));
//                decisionExecuteResult.setFirstScore(Optional.ofNullable(scoreMap2.get(SCORE)).map(String::valueOf).orElse(null));
//                decisionExecuteResult.setQualitativeScore(Optional.ofNullable(modelScoreMap2.get(QUALITATIVE_SCORE)).map(String::valueOf).orElse(null));
//                decisionExecuteResult.setQuantitativeScore(Optional.ofNullable(modelScoreMap2.get(QUANTITATIVE_SCORE)).map(String::valueOf).orElse(null));
//                decisionExecuteResult.setModelScore(Optional.ofNullable(modelScoreMap.get(FINAL_SCORE)).map(String::valueOf).orElse(null));
//                decisionExecuteResult.setDefaultRate(Optional.ofNullable(defaultMap.get(VALUE)).map(String::valueOf).orElse(null));
                decisionExecuteResult.setModelScore(Optional.ofNullable(resultMap.get(MODEL_SCORE)).map(Object::toString).orElse(null));
                decisionExecuteResult.setDefaultRate(Optional.ofNullable(resultMap.get(DEFAULT_RATE)).map(Object::toString).orElse(null));
                decisionExecuteResult.setFirstScore(Optional.ofNullable(resultMap.get(FIRST_SCORE)).map(Object::toString).orElse(null));
                decisionExecuteResult.setScore(Optional.ofNullable(resultMap.get(SCORE)).map(String::valueOf).orElse(null));
                decisionExecuteResult.setQualitativeScore(Optional.ofNullable(resultMap.get(QUALITATIVE_SCORE)).map(Object::toString).orElse(null));
                decisionExecuteResult.setQuantitativeScore(Optional.ofNullable(resultMap.get(QUANTITATIVE_SCORE)).map(Object::toString).orElse(null));
            }
            if(serviceCode.contains("amount")) {
//                Map<String, Object> clientQuotaMap = (Map<String, Object>) Optional.ofNullable(outputMap.get(CLIENT_QUOTA_MAP)).orElse(new HashMap<>());
//                String factor = RATING_ADJUST_FACTOR;
//                Map<String, Object> ratingAdjustFactor = (Map<String, Object>) Optional.ofNullable(outputMap.get(factor)).orElse(new HashMap<>());
//                if(CollectionUtils.isEmpty(ratingAdjustFactor)){
//                    factor = RATING_ADJUST_FACTOR_NORMAL;
//                    ratingAdjustFactor = (Map<String, Object>) Optional.ofNullable(outputMap.get(factor)).orElse(new HashMap<>());
//                }
//                if(CollectionUtils.isEmpty(ratingAdjustFactor)){
//                    factor = RATING_ADJUST_FACTOR_PRJ;
//                    ratingAdjustFactor = (Map<String, Object>) Optional.ofNullable(outputMap.get(factor)).orElse(new HashMap<>());
//                }
//                Map<String, Object> ratingAdjustFactorMap2 = (Map<String, Object>) Optional.ofNullable(ratingAdjustFactor.get(FINAL_SCORE_MAP2)).orElse(new HashMap<>());
//                Map<String, Object> measuresMap = (Map<String, Object>) Optional.ofNullable(outputMap.get(MEASURES)).orElse(new HashMap<>());
//                decisionExecuteResult.setClientQuota(Optional.ofNullable(clientQuotaMap.get(VALUE)).map(String::valueOf).orElse(null));
//                decisionExecuteResult.setRatingAdjustFactor(Optional.ofNullable(ratingAdjustFactorMap2.get(factor)).map(String::valueOf).orElse(null));
//                decisionExecuteResult.setCreditMeasurePrice(Optional.ofNullable(measuresMap.get(VALUE)).map(String::valueOf).orElse(null));

                for (String str : RATING_ADJUST_FACTOR_LIST) {
                    if(Objects.nonNull(resultMap.get(str))) {
                        decisionExecuteResult.setRatingAdjustFactor(resultMap.get(str).toString());
                        break;
                    }
                }
                decisionExecuteResult.setClientQuota(Optional.ofNullable(resultMap.get(CLIENT_QUOTA)).map(Object::toString).orElse(null));
                decisionExecuteResult.setCreditMeasurePrice(Optional.ofNullable(resultMap.get(CREDIT_MEASURES_PRICE)).map(Object::toString).orElse(null));
            }
            List<ContextRecord.Var> resultVarList = contextRecord.getVarList();
            List<DecisionExecuteResult.Var> varList = BeanUtil.copyToList(resultVarList, DecisionExecuteResult.Var.class);
            List<DecisionExecuteResult.Param> paramList = BeanUtil.copyToList(resultParamList, DecisionExecuteResult.Param.class);
            decisionExecuteResult.setVarList(varList);
            decisionExecuteResult.setParamList(paramList);
            decisionExecuteResult.setScoreCardVersion(Optional.ofNullable(outputMap.get(SCORE_CARD_VERSION)).map(String::valueOf).orElse(null));
            decisionExecuteResult.setScoreCardName(Optional.ofNullable(outputMap.get(SCORE_CARD_NAME)).map(String::valueOf).orElse(null));
            decisionExecuteResult.setScoreCardCode(Optional.ofNullable(outputMap.get(SCORE_CARD_CODE)).map(String::valueOf).orElse(null));
            return decisionExecuteResult;
        }catch (Exception e){
            log.error("远程调用发生未知异常:",e);
        }
        return null;
    }

    /**
     * 获取指标页面信息
     * @param serviceCode
     * @return
     */
    public Map<String, List<RatingParamFieldApprovalRSP>> paramInfo(String serviceCode){
        DecisionParamREQ decisionREQ = new DecisionParamREQ();
        decisionREQ.setServiceCode(serviceCode);
        try {
            R<DecisionParamRSP> rsp = decisionApiClient.paramInfo(decisionREQ);
            log.info("页面指标信息API接口(获取指标):request:{}, response:{}", JSONObject.toJSONString(decisionREQ), JSONObject.toJSONString(rsp));
            if(rsp.getCode() != 200){
                log.error("调用失败 -> message:{},完整错误错误信息:{}",JSONObject.toJSONString(rsp.getMessage()),JSONObject.toJSONString(rsp));
                return null;
            }
            DecisionParamRSP data = rsp.getData();
            List<ParamItem> paramItemList = data.getParamItemList();
//            for (int i = 0; i < paramItemList.size(); i++) {
//                ParamItem paramItem = paramItemList.get(i);
//                if(!paramItem.getGroupName().contains("-")){
//                    if(("评级调整事项").equals(paramItem.getFieldComment())){
//                        paramItem.setGroupName("adjustEvent-" + paramItem.getGroupName());
//                        continue;
//                    }
//                    if(RatingDataTypeEnum.ENUM.getExtra().equals(paramItem.getDataType()) || RatingDataTypeEnum.ENUM_COLLECTION.getExtra().equals(paramItem.getDataType())) {
//                        paramItem.setGroupName("qualitative-" + paramItem.getGroupName());
//                    }else{
//                        paramItem.setGroupName("quantitative-" + paramItem.getGroupName());
//                    }
//                }
//            }

            List<RatingParamFieldApprovalRSP> fieldRSPList = new ArrayList<>();
            for (ParamItem paramItem : paramItemList) {
                RatingParamFieldApprovalRSP fieldRSP = BeanUtil.copyProperties(paramItem, RatingParamFieldApprovalRSP.class);
                fieldRSP.setFetchMethod(RatingFetchMethodEnum.IMPORT.name());
                String extraInfo = paramItem.getExtraInfo();
                if(StringUtils.isNotBlank(extraInfo)) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = JSON.parseObject(extraInfo);
                        if (Objects.nonNull(jsonObject)) {
                            String unit = String.valueOf(Optional.ofNullable(jsonObject.get(UNIT)).orElse(""));
                            String formal = String.valueOf(Optional.ofNullable(jsonObject.get(FORMAL)).orElse(""));
                            fieldRSP.setUnit(unit);
                            fieldRSP.setFormal(formal);
                        }
                    }catch (JSONException e){
                        log.warn("extraInfo的数据类型不正确:{}",extraInfo);
                    }
                }
                if(fieldRSP.getGroupName().contains("-")) {
                    fieldRSPList.add(fieldRSP);
                }
            }

            return fieldRSPList.stream().collect(Collectors.groupingBy(RatingParamFieldApprovalRSP::getDataType));
        }catch (Exception e){
            log.error("远程调用发生未知异常:",e);
        }
        return null;
    }

    /**
     * 获取模型
     * @return
     */
    public List<DecisionModelRSP> modelQuery(DecisionModelREQ req){
        try {
            R<List<DecisionModelRSP>> rsp = decisionApiClient.modelQuery(req);
            log.info("决策服务API接口(获取模型):request:{}, response:{}", JSONObject.toJSONString(req), JSONObject.toJSONString(rsp));
            if(rsp.getCode() != 200){
                log.error("调用失败 -> message:{},完整错误错误信息:{}",JSONObject.toJSONString(rsp.getMessage()),JSONObject.toJSONString(rsp));
                return null;
            }
            return rsp.getData();
        }catch (Exception e){
            log.error("远程调用发生未知异常:",e);
        }
        return null;
    }


    public String groupQuota(Map<String, Object> param){
        DecisionREQ groupREQ = new DecisionREQ();
        groupREQ.setServiceCode("zxkh_jtxe_service");
        groupREQ.setParam(param);
        R<DecisionRSP> groupRSP = decisionApiClient.execute(groupREQ);
        log.info("决策服务API接口(评分):request:{}, response:{}", JSONObject.toJSONString(groupREQ), JSONObject.toJSONString(groupRSP));
        if(groupRSP.getCode() != 200){
            log.error("集团限额-调用失败 -> message:{},完整错误错误信息:{}",JSONObject.toJSONString(groupRSP.getMessage()),JSONObject.toJSONString(groupRSP));
            return null;
        }
        DecisionRSP data = groupRSP.getData();
        Map<String, Object> groupQuotaMap = data.getOutputMap();
        Object groupQuota = groupQuotaMap.get(GROUP_QUOTA);
        return Optional.ofNullable(groupQuota).map(Object::toString).orElse(null);
    }

    public RatingAccessCheckRSP accessCheckClient(Map<String, Object> param){
        DecisionREQ groupREQ = new DecisionREQ();
        groupREQ.setServiceCode("zx_client_zr_service");
        groupREQ.setParam(param);
        R<DecisionRSP> groupRSP = decisionApiClient.execute(groupREQ);
        log.info("决策服务API接口(评分):request:{}, response:{}", JSONObject.toJSONString(groupREQ), JSONObject.toJSONString(groupRSP));
        if(groupRSP.getCode() != 200){
            log.error("客户评级准入校验-调用失败 -> message:{},完整错误错误信息:{}",JSONObject.toJSONString(groupRSP.getMessage()),JSONObject.toJSONString(groupRSP));
            throw new MithrasException("准入校验调用失败");
        }
        RatingAccessCheckRSP accessCheckRSP = new RatingAccessCheckRSP();

        DecisionRSP data = groupRSP.getData();
        Map<String, Object> checkMap = data.getOutputMap();
        Map<String, Object> areaAccess = (Map<String, Object>) Optional.ofNullable(checkMap.get(CLIENT_ACCESS_VALUE)).orElse(new HashMap<>());
        String area = Optional.ofNullable(areaAccess.get(AREA_VALUE)).map(Object::toString).orElse("N");
        accessCheckRSP.setClientAreaAccess("Y".equals(area));
        return accessCheckRSP;
    }


    public RatingAccessCheckRSP accessCheckAmount(Map<String, Object> param){
        DecisionREQ groupREQ = new DecisionREQ();
        groupREQ.setServiceCode("zx_zxpj_zr_service");
        groupREQ.setParam(param);
        R<DecisionRSP> groupRSP = decisionApiClient.execute(groupREQ);
        log.info("决策服务API接口(评分):request:{}, response:{}", JSONObject.toJSONString(groupREQ), JSONObject.toJSONString(groupRSP));
        if(groupRSP.getCode() != 200){
            log.error("客户评级准入校验-调用失败 -> message:{},完整错误错误信息:{}",JSONObject.toJSONString(groupRSP.getMessage()),JSONObject.toJSONString(groupRSP));
            throw new MithrasException("准入校验调用失败");
        }
        RatingAccessCheckRSP accessCheckRSP = new RatingAccessCheckRSP();

        DecisionRSP data = groupRSP.getData();
        Map<String, Object> checkMap = data.getOutputMap();

        Map<String, Object> rankAccess = (Map<String, Object>) Optional.ofNullable(checkMap.get(CLIENT_ACCESS_VALUE)).orElse(new HashMap<>());
        String rank = Optional.ofNullable(rankAccess.get(RANK_VALUE)).map(Object::toString).orElse("N");
        accessCheckRSP.setClientRankAccess("Y".equals(rank));
        return accessCheckRSP;
    }
}
