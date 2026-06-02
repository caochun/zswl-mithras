package cn.zswltech.mithras.third.repository.tyc.handler.impl;

import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.model.TycMockData;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.repository.tyc.TycMockDataType;
import cn.zswltech.mithras.third.repository.tyc.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.repository.tyc.req.TycLawSuitDetailReq;
import cn.zswltech.mithras.third.repository.tyc.resp.TycLawSuitDetailResp;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * 法律诉讼详情
 * http://open.tianyancha.com/open/1073
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycLawSuitDetailApiHandler extends AbstractTycApiHandler<TycLawSuitDetailReq, TycLawSuitDetailResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/jr/lawSuit/detail";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_LAW_SUIT_DETAIL;
    }

    @Override
    public String getUrl(TycLawSuitDetailReq req) {
        return new StringBuilder(url)
                .append("?uuid=").append(req.getUuid())
                .toString();
    }

    @Override
    public TycLawSuitDetailResp analyResponseResult(String response) {
        // response = "{\"result\":{\"judgeDate\":\"二〇一五年四月二十八日\",\"judgeResult\":\"一、被告廖文泉于本判决生效之日起五日内清偿原告肖泽华借款本金80000元及利息（利息按年利率24%自2012年9月20日起计算至还清之日止）。\\n二、驳回原告肖泽华的其他诉讼请求。\\n三、如果被告未按本判决指定的期限履行给付金钱义务，应当加倍支付迟延履行期间的债务利息。\\n案件受理费1809元（已减半收取，原告肖泽华已预交），由原告肖泽华负担509元，被告廖文泉负担1300元。\\n如不服本判决，可在判决书送达之日起十五日内，向本院递交上诉状，并按对方当事人的人数提出副本，上诉于江西省赣州市中级人民法院。\",\"title\":\"(2015)赣民二初字第87号原告肖泽华与被告廖文泉民间借贷纠纷一审民事判决书\",\"caseno\":\"（2015）赣民二初字第87号\",\"uuid\":\"929dd3cc1cb511e6b554008cfae40dc0\",\"courtConsider\":\"\",\"companies\":[],\"plaintiffRequest\":\"原告肖泽华诉称：2012年2月4日，被告廖文泉因投资绿化工程，向原告借款80000元，并向原告出具借条一份。约定：借款期限半年；到2012年8月3日连本带利归还90000元。半年期满，被告并未按约还款，自2012年8月至12月，被告陆续向原告支付利息13500元。在原告多次催收下，被告于2013年2月9日重新向原告出具借条一份，收回第一份借条。2013年7月10日，被告向原告出具2013年元月至2013年10月利息欠条一份，注明每月利息2700元；并再向原告出具借条一份，注明2013年10月30日前付清。2014年3月2日，被告向原告出具承诺书一份，承诺按月利0.3%至归还日一起结算本金及利息。此后，原告多次催收，被告连原告的电话也不接，发信息也不回。为此，原告诉至法院，请求判令被告廖文泉归还原告借款本金90000元及利息70200元；被告廖文泉偿还原告误工费2400元、交通费3000元、电话费300元；并承担本案诉讼费用。\",\"courtInspect\":\"\",\"trialProcedure\":\"原告肖泽华诉被告廖文泉民间借贷纠纷一案，本院于2015年2月12日立案受理后，依法适用简易程序公开开庭进行了审理。原告肖泽华到庭参加诉讼，被告廖文泉经本院合法传唤无正当理由拒不到庭参加诉讼。本案现已审理终结。\",\"lawFirms\":[],\"plaintiffRequestOfFirst\":\"\",\"defendantReplyOfFirst\":\"\",\"trialPerson\":\"审　判　员　　刘译铃\",\"plaintext\":\"江西省赣县人民法院\\n\\r\\n\\n民事判决书\\n\\r\\n\\n（2015）赣民二初字第87号\\n\\r\\n\\n原告肖泽华，男，成年，汉族。\\n\\r\\n\\n被告廖文泉，男，成年，汉族。\\n\\r\\n\\n原告肖泽华诉被告廖文泉民间借贷纠纷一案，本院于2015年2月12日立案受理后，依法适用简易程序公开开庭进行了审理。原告肖泽华到庭参加诉讼，被告廖文泉经本院合法传唤无正当理由拒不到庭参加诉讼。本案现已审理终结。\\n\\r\\n\\n原告肖泽华诉称：2012年2月4日，被告廖文泉因投资绿化工程，向原告借款80000元，并向原告出具借条一份。约定：借款期限半年；到2012年8月3日连本带利归还90000元。半年期满，被告并未按约还款，自2012年8月至12月，被告陆续向原告支付利息13500元。在原告多次催收下，被告于2013年2月9日重新向原告出具借条一份，收回第一份借条。2013年7月10日，被告向原告出具2013年元月至2013年10月利息欠条一份，注明每月利息2700元；并再向原告出具借条一份，注明2013年10月30日前付清。2014年3月2日，被告向原告出具承诺书一份，承诺按月利0.3%至归还日一起结算本金及利息。此后，原告多次催收，被告连原告的电话也不接，发信息也不回。为此，原告诉至法院，请求判令被告廖文泉归还原告借款本金90000元及利息70200元；被告廖文泉偿还原告误工费2400元、交通费3000元、电话费300元；并承担本案诉讼费用。\\n\\r\\n\\n被告廖文泉拒不到庭参加诉讼进行举证并提出抗辩意见，其不利的法律后果应由被告承担。原告肖泽华提供了原告的身份证复印件及借条、欠条、承诺书、农村信用社回单等证据证实，事实清楚，证据充分。因此，对原告要求被告清偿借款本金及利息的诉讼请求，本院应予支持。借款本金应按实际借款金额予以认定。被告陆续支付的13500元利息，按双方约定核算为借款日起至2012年9月19日止的利息，9月20日起视为被告未支付利息。双方约定的利率过高，本院酌情予以支持，对已支付的利息，系被告自愿，本院从其自愿。对原告要求判令被告支付误工费、交通费、电话费的诉讼请求，其未提供证据，本院不予支持。依据《中华人民共和国民事诉讼法》第一百四十四条、《中华人民共和国合同法》第二百零五条、第二百零六条、第二百一十一条第二款之规定，判决如下：\\n\\r\\n\\n一、被告廖文泉于本判决生效之日起五日内清偿原告肖泽华借款本金80000元及利息（利息按年利率24%自2012年9月20日起计算至还清之日止）。\\n\\r\\n\\n二、驳回原告肖泽华的其他诉讼请求。\\n\\r\\n\\n三、如果被告未按本判决指定的期限履行给付金钱义务，应当加倍支付迟延履行期间的债务利息。\\n\\r\\n\\n案件受理费1809元（已减半收取，原告肖泽华已预交），由原告肖泽华负担509元，被告廖文泉负担1300元。\\n\\r\\n\\n如不服本判决，可在判决书送达之日起十五日内，向本院递交上诉状，并按对方当事人的人数提出副本，上诉于江西省赣州市中级人民法院。\\n\\r\\n\\n审　判　员　　刘译铃\\n\\r\\n\\n二〇一五年四月二十八日\\n\\r\\n\\n代理书记员　　刘　萍\",\"appellor\":\"原告肖泽华，男，成年，汉族。\\n被告廖文泉，男，成年，汉族。\",\"court\":\"江西省赣县人民法院\",\"url\":\"\",\"courtConsiderOfFirst\":\"\",\"doctype\":\"判决书\",\"trialAssistPerson\":\"代理书记员　　刘　萍\",\"casetype\":\"民事案件\",\"appellantRequest\":\"\",\"defendantReply\":\"被告廖文泉拒不到庭参加诉讼进行举证并提出抗辩意见，其不利的法律后果应由被告承担。原告肖泽华提供了原告的身份证复印件及借条、欠条、承诺书、农村信用社回单等证据证实，事实清楚，证据充分。因此，对原告要求被告清偿借款本金及利息的诉讼请求，本院应予支持。借款本金应按实际借款金额予以认定。被告陆续支付的13500元利息，按双方约定核算为借款日起至2012年9月19日止的利息，9月20日起视为被告未支付利息。双方约定的利率过高，本院酌情予以支持，对已支付的利息，系被告自愿，本院从其自愿。对原告要求判令被告支付误工费、交通费、电话费的诉讼请求，其未提供证据，本院不予支持。依据《中华人民共和国民事诉讼法》第一百四十四条、《中华人民共和国合同法》第二百零五条、第二百零六条、第二百一十一条第二款之规定，判决如下：\",\"courtInspectOfFirst\":\"\",\"appelleeArguing\":\"\"},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycLawSuitDetailResp.class);
    }

    @Override
    public String mockHandleBeforeResponse(TycLawSuitDetailReq reqData, String responseData) {
        TycMockData mockData = mockDataMapper.selectOne(Wrappers.<TycMockData>lambdaQuery()
                .eq(TycMockData::getDataType, TycMockDataType.LAW_SUIT_DETAIL.name())
                .eq(TycMockData::getKeyword, reqData.getUuid())
        );
        if (isNotNull(mockData)) {
            responseData = mockData.getJsonData();
        }
        return responseData;
    }

}
