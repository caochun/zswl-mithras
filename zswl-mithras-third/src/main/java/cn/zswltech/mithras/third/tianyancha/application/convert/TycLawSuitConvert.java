package cn.zswltech.mithras.third.tianyancha.application.convert;

import cn.hutool.http.HtmlUtil;
import cn.zswltech.mithras.dto.client.external.tyc.TycLawSuitRSP;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.model.TycLawSuit;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycLawSuitDetailResp;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycLawSuitResp;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 法律诉讼
 *
 * @author wangchuanhao
 * @date 2022/6/21 12:52 PM
 */
public class TycLawSuitConvert {

    public static TycLawSuit tycResp2Entity(TycLawSuitResp.ItemsDTO resp) {
        TycLawSuit tycLawSuit = new TycLawSuit();
        tycLawSuit.setDocType(resp.getDocType());
        tycLawSuit.setLawsuitUrl(resp.getLawsuitUrl());
        tycLawSuit.setLawsuitH5Url(resp.getLawsuitH5Url());
        tycLawSuit.setTitle(resp.getTitle());
        tycLawSuit.setCourt(resp.getCourt());
        tycLawSuit.setJudgeTime(resp.getJudgeTime());
        tycLawSuit.setUuid(resp.getUuid());
        tycLawSuit.setCaseNo(resp.getCaseNo());
        tycLawSuit.setCaseType(resp.getCaseType());
        tycLawSuit.setCaseReason(resp.getCaseReason());
        tycLawSuit.setCasePersonsJson(Optional.ofNullable(resp.getCasePersons()).map(JSON::toJSONString).orElse(null));
        tycLawSuit.setCaseMoney(resp.getCaseMoney());
        tycLawSuit.setSubmitTime(Optional.ofNullable(resp.getSubmitTime()).map(LocalDateTimeUtil::of).orElse(null));
        tycLawSuit.setTycId(resp.getId());
        return tycLawSuit;

    }

    public static TycLawSuitRSP entity2RSP(TycLawSuit entity, Client client) {
        TycLawSuitRSP tycLawSuitRSP = new TycLawSuitRSP();
        tycLawSuitRSP.setId(entity.getId());
        tycLawSuitRSP.setTitle(entity.getTitle());
        tycLawSuitRSP.setCaseReason(entity.getCaseReason());
        tycLawSuitRSP.setCaseMoney(entity.getCaseMoney());
        tycLawSuitRSP.setDetailUrl(entity.getLawsuitUrl());
        if (StringUtils.isNotBlank(entity.getCasePersonsJson())) {
            String clientTycName = StringUtils.isNotBlank(client.getTycName()) ? client.getTycName() : client.getClientName();
            List<TycLawSuitResp.CasePersonsDTO> clientCasePersonList = JSONArray.parseArray(entity.getCasePersonsJson())
                    .toJavaList(TycLawSuitResp.CasePersonsDTO.class)
                    .stream()
                    .filter(p -> Objects.equals(clientTycName, p.getName()))
                    .collect(Collectors.toList());
            tycLawSuitRSP.setIdentity(clientCasePersonList.stream()
                    .map(TycLawSuitResp.CasePersonsDTO::getRole)
                    .distinct()
                    .collect(Collectors.joining(";")));
            tycLawSuitRSP.setResultTag(clientCasePersonList.stream()
                    .map(TycLawSuitResp.CasePersonsDTO::getResult)
                    .distinct()
                    .collect(Collectors.joining(";")));
        }
        if (StringUtils.isNotBlank(entity.getDetailJson())) {
            TycLawSuitDetailResp.Result detail = JSONObject.parseObject(entity.getDetailJson(), TycLawSuitDetailResp.Result.class);
            tycLawSuitRSP.setJudgeResult(StringUtils.isNotBlank(detail.getJudgeResult()) ? HtmlUtil.unescape(HtmlUtil.cleanHtmlTag(detail.getJudgeResult())) : "");
        }
        return tycLawSuitRSP;


    }

}
