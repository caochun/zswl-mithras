package cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.impl;

import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycPunishmentInfoResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 行政处罚
 * http://open.tianyancha.com/open/1124
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycPunishmentInfoApiHandler extends AbstractTycApiHandler<TycBaseReq, TycPunishmentInfoResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/mr/punishmentInfo/3.0";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_PUNISHMENT_INFO;
    }

    @Override
    public String getUrl(TycBaseReq req) {
        try {
            return new StringBuilder(url)
                    .append("?pageSize=").append(req.getPageSize())
                    .append("&pageNum=").append(req.getPageNum())
                    .append("&keyword=").append(URLEncoder.encode(req.getKeyword(), "UTF-8"))
                    .toString();
        } catch (UnsupportedEncodingException e) {
            log.error("天眼查接口参数encode失败, reqData:{}", JSON.toJSONString(req));
            throw new MithrasException("天眼查接口参数encode失败");
        }
    }

    @Override
    public TycPunishmentInfoResp analyResponseResult(String response) {
        // response = "{\"result\":{\"total\":1,\"items\":[{\"departmentName\":\"北京市文化和旅游局\",\"reason\":\"现已查实,该(单位)于2016年01月与2020年11月期间,经营的“百度阅读”网(yuedu.baidu.com)传播的《宝贝,无你不欢》(作者:戎客骨)、《魔化武装》(作者:易辟)、《辰起之国》(作者:子酣)、《娇妻求饶:霍少请节制》(作者:风小筝)、《豪门首席嫩妻》(作者:灵猫香)、《甜妻难养:嗜血总裁7日情》(作者:叫兽萌)、《将门悍妻》(作者:不倒先生)、《撩夫成瘾:契约娇妻乖一点》(作者:于牧然)等8部小说含有“轻轻的扳开苏忆的双腿”、“女子的媚态,使得男人更加激动,不禁加快了抽送的速度,在激烈的冲刺之后,男人终于一声低吼,倾囊而泄”、“已经迫不及待地将要爆炸的rou棒挺进了聂含璋的身体内”等内容,属含有禁止内容的网络出版物。违法经营额310元,无违法所得。,该(单位)的上述行为违反了《网络出版服务管理规定》第二十四条第(七)项的规定。\",\"evidence\":\"《网络出版服务管理规定》第五十二条第一款\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"信用中国\",\"type\":\"罚款\",\"content\":\"罚款人民币贰万元。\",\"decisionDate\":\"2021-03-29\",\"legalPersonName\":\"\",\"punishName\":\"经营的“百度阅读”网(yuedu.baidu.com)传播的《宝贝,无你不欢》(作者:戎客骨)、《魔化武装》(作者:易辟)、《辰起之国》(作者:子酣)、《娇妻求饶:霍少请节制》(作者:风小筝)、《豪门首席嫩妻》(作者:灵猫香)、《甜妻难养:嗜血总裁7日情》(作者:叫兽萌)、《将门悍妻》(作者:不倒先生)、《撩夫成瘾:契约娇妻乖一点》(作者:于牧然)等8部小说含有“轻轻的扳开苏忆的双腿”、“女子的媚态,使得男人更加激动,不禁加快了抽送的速度,在激烈的冲刺之后,男人终于一声低吼,倾囊而泄”、“已经迫不及待地将要爆炸的rou棒挺进了聂含璋的身体内”等内容,属含有禁止内容的网络出版物。违法经营额310元,无违法所得。\",\"punishNumber\":\"（京）文执罚〔2021〕第400154号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市海淀区市场监督管理局\",\"reason\":\"《中华人民共和国广告法》（2018）第三十四条第二款\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款30000元。\",\"decisionDate\":\"2021-01-11\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"京海市监罚字〔2021〕18号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市海淀区市场监督管理局\",\"reason\":\"《中华人民共和国广告法》（2018）第二十四条第一款第一项\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"1.没收广告费1552.95元，2.罚款1552.95元。\",\"decisionDate\":\"2021-01-11\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"京海市监罚字〔2021〕19号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市海淀区市场监督管理局\",\"reason\":\"违反了《中华人民共和国广告法》第三十四条第二款的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款30000元。\",\"decisionDate\":\"2020-10-28\",\"legalPersonName\":\"梁志祥\",\"punishName\":\"\",\"punishNumber\":\"京海市监处字〔2020〕第444号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市海淀区市场监督管理局\",\"reason\":\"违反了《中华人民共和国广告法》第三十四条第二款的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款30000元。\",\"decisionDate\":\"2020-10-28\",\"legalPersonName\":\"梁志祥\",\"punishName\":\"\",\"punishNumber\":\"京海市监处字〔2020〕第445号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市海淀区市场监督管理局\",\"reason\":\"违反了《中华人民共和国广告法》第九条第（七）项的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款200000元。\",\"decisionDate\":\"2020-07-21\",\"legalPersonName\":\"梁志祥\",\"punishName\":\"\",\"punishNumber\":\"京海市监处字〔2020〕第303号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市海淀区市场监督管理局\",\"reason\":\"依据《中华人民共和国电子商务法》第七十六条第二款、第八十条第一款第（三）项之规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款10000元。\",\"decisionDate\":\"2020-07-16\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"京海市监工罚（2020）299号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"《网络出版服务管理规定》第五十二条第一款\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币壹万元。\",\"decisionDate\":\"2020-06-04\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2020〕第400184号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"《网络出版服务管理规定》第二十五条\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币肆万捌仟元。\",\"decisionDate\":\"2019-12-20\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2019〕第400135号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市工商行政管理局海淀分局\",\"reason\":\"违反了《中华人民共和国广告法》第三十四条第二款的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"——\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款30000元。\",\"decisionDate\":\"2019-12-03\",\"legalPersonName\":\"梁志祥\",\"punishName\":\"\",\"punishNumber\":\"京海市监工罚（2019）986号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"《网络出版服务管理规定》第二十四条\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币贰万元。\",\"decisionDate\":\"2019-09-27\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2019〕第41073号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"《中华人民共和国著作权法》第四十八条第（一）项\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币叁万元。\",\"decisionDate\":\"2019-08-06\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2019〕第50122号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"《网络出版服务管理规定》第二十四条\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币贰万元。\",\"decisionDate\":\"2019-07-31\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2019〕第40824号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"《网络出版服务管理规定》第二十四条\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币肆万伍仟元。\",\"decisionDate\":\"2019-05-13\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2019〕第40656号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"《网络出版服务管理规定》第二十四条\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币贰万元。\",\"decisionDate\":\"2019-02-13\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2019〕第40068号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市工商行政管理局海淀分局\",\"reason\":\"违反了《中华人民共和国广告法》第九条第（八）项的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"——\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"1，没收广告费用26220.46元2，罚款600000元\",\"decisionDate\":\"2018-11-20\",\"legalPersonName\":\"梁志祥\",\"punishName\":\"\",\"punishNumber\":\"京工商海处字〔2018〕第2286号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市工商行政管理局海淀分局\",\"reason\":\"违反了《中华人民共和国广告法》第四十六条的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"1.没收广告费用142393.15元2.罚款427179.45元\",\"decisionDate\":\"2018-08-17\",\"legalPersonName\":\"梁志祥\",\"punishName\":\"\",\"punishNumber\":\"京工商海处字〔2018〕第1583号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市文化市场行政执法总队\",\"reason\":\"出版、传播含有第二十四条所述禁止内容的网络出版物\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款人民币贰万元。\",\"decisionDate\":\"2018-07-11\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"（京）文执罚〔2018〕第40622号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市工商行政管理局海淀分局\",\"reason\":\"违反了《中华人民共和国广告法》第八条第一款的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款20000元。\",\"decisionDate\":\"2018-05-28\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"京工商海不予处字〔2018〕第162号\",\"typeSecond\":\"\"},{\"departmentName\":\"北京市工商行政管理局海淀分局\",\"reason\":\"违反了《中华人民共和国广告法》第八条第一款的规定\",\"evidence\":\"\",\"punishStatus\":\"\",\"remark\":\"\",\"source\":\"国家市场监督管理总局\",\"type\":\"\",\"content\":\"罚款15000元。\",\"decisionDate\":\"2018-05-28\",\"legalPersonName\":\"\",\"punishName\":\"\",\"punishNumber\":\"京工商海不予处字〔2018〕第163号\",\"typeSecond\":\"\"}]},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycPunishmentInfoResp.class);
    }

}
