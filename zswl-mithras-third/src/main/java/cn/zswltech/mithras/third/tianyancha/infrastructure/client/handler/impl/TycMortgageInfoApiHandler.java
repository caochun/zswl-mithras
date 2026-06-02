package cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.impl;

import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.handler.AbstractTycApiHandler;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp.TycMortgageInfoResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 动产抵押
 * http://open.tianyancha.com/open/844
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:07 PM
 */
@Component
@Slf4j
public class TycMortgageInfoApiHandler extends AbstractTycApiHandler<TycBaseReq, TycMortgageInfoResp> {

    private static final String url = "http://open.api.tianyancha.com/services/open/mr/mortgageInfo/2.0";

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.TYC_MORTGAGE_INFO;
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
    public TycMortgageInfoResp analyResponseResult(String response) {
        // response = "{\"result\":{\"total\":1,\"items\":[{\"baseInfo\":{\"amount\":\"100万元\",\"cancelDate\":null,\"publishDate\":1464278400000,\"regDate\":\"2016-05-27\",\"remark\":\"\",\"type\":\"借款\",\"overviewRemark\":\"\",\"overviewTerm\":\"自2016-05-27至2017-05-10\",\"overviewAmount\":\"\",\"regDepartment\":\"讷河市市场监督管理局\",\"regNum\":\"讷市监抵登字（2016）03018号\",\"overviewScope\":\"\",\"scope\":\"主债权本金、利息、罚息、复利、违约金、损害赔偿金，以及诉讼仲裁费、律师费、处置费、过户费、过户费等抵押权人实现债权和抵押权的一切费用。\",\"overviewType\":\"\",\"term\":\"自2016-05-27至2017-05-10\",\"id\":10901942,\"cancelReason\":\"\",\"status\":\"有效\",\"base\":\"hlj\"},\"peopleInfo\":[{\"liceseType\":\"其他\",\"peopleName\":\"讷河市农村信用合作联社\",\"licenseNum\":\"非公示项\"}],\"pawnInfoList\":[{\"pawnName\":\"农机具\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"28台套、良好、老莱镇丰盛村\"}],\"changeInfoList\":[]},{\"baseInfo\":{\"amount\":\"100万元\",\"cancelDate\":null,\"publishDate\":1431532800000,\"regDate\":\"2015-05-14\",\"remark\":\"\",\"type\":\"借款\",\"overviewRemark\":\"\",\"overviewTerm\":\"自2015-05-14至2016-05-13\",\"overviewAmount\":\"\",\"regDepartment\":\"讷河市市场监督管理局\",\"regNum\":\"黑讷动抵2015第21号\",\"overviewScope\":\"\",\"scope\":\"主债权本金、利息、罚息复利、违约金、损害赔偿金以及诉讼（仲裁）费、律师费、处置费、过户费等抵押权人实现债权和抵押权的一切费用\",\"overviewType\":\"\",\"term\":\"自2015-05-14至2016-05-13\",\"id\":11973700,\"cancelReason\":\"\",\"status\":\"有效\",\"base\":\"hlj\"},\"peopleInfo\":[{\"liceseType\":\"其他\",\"peopleName\":\"讷河市农村信用合作联社\",\"licenseNum\":\"非公示项\"}],\"pawnInfoList\":[{\"pawnName\":\"大垄双行整形机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"液压折叠镇压器\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"马铃薯收获机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"马铃薯收获机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"2台、良好、院内\"},{\"pawnName\":\"拖拉机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"马铃薯2行播种机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"松耙联合整地机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"3台、良好、院内\"},{\"pawnName\":\"履带拖拉机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"马铃薯4行播种机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"2台、良好、院内\"},{\"pawnName\":\"免耕播种机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"轮式拖拉机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"3880A喷药机带水泵机组\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"2台、良好、院内\"},{\"pawnName\":\"马铃薯收获机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"折叠重耙\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"中耕机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"联合整地机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"马铃薯收获机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"拖拉机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"2台、良好、院内\"},{\"pawnName\":\"播种机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"玉米籽粒收获机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"拖拉机精确导航仪\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"玉米收获机割台\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"免耕播种机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"轮式拖拉机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"2台、良好、院内\"},{\"pawnName\":\"3879A喷药机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"高速灭茬缺口圆盘耙\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"},{\"pawnName\":\"马铃薯中耕机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"2台、良好、院内\"},{\"pawnName\":\"轮式拖拉机\",\"ownership\":\"讷河市丰盛现代农业农机专业合作社\",\"remark\":\"\",\"detail\":\"1台、良好、院内\"}],\"changeInfoList\":[]}]},\"reason\":\"ok\",\"error_code\":0}";
        return JSONObject.parseObject(response, TycMortgageInfoResp.class);
    }

}
