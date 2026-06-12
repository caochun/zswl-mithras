package cn.zswltech.mithras.third.tianyancha.application.convert;

import cn.zswltech.mithras.third.externaldata.tianyancha.persistence.model.TycLawSuit;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycLawSuitResp;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;

import java.util.Optional;

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

}
