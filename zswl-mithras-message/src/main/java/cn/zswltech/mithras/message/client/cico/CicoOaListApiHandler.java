package cn.zswltech.mithras.message.client.cico;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.message.client.cico.dto.CicoOaListRSP;
import cn.zswltech.mithras.message.client.cico.dto.CicoOaListReq;
import cn.zswltech.mithras.foundation.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交投oa
 *
 * @date 2022/7/20 2:14 PM
 */
@Component
public class CicoOaListApiHandler implements PlatformApiHandler<CicoOaListReq, CicoOaListRSP> {

    @Value("${remote.publishNotice.esbUrl}")
    private String esbUrl;

    private static final int MAX_ATTEMPTS = 50;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CICO_OA_DATA_LIST;
    }

    @Override
    public CicoOaListRSP response(String data) {
        try {
            return JSONObject.parseObject(data, CicoOaListRSP.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CicoOaListRSP execute(CicoOaListReq reqData) {
        CicoOaListRSP oaListRSP = new CicoOaListRSP();
        this.queryWithSplit(reqData, 0, oaListRSP);
        return oaListRSP;
    }

    private boolean queryWithSplit(CicoOaListReq reqData, int depth, CicoOaListRSP oaListRSP) {
        CicoOaListRSP data = fetchData(reqData);
        if (ObjectUtil.isEmpty(data)) {
            log.info("消息拉取接口失败 {}", data);
            return false;
        }
        if(depth > MAX_ATTEMPTS) {
            //最多拉取 50000条数据，防止无限递归
            return true;
        }
        if (ObjectUtil.isEmpty(data.getData()) || ObjectUtil.isEmpty(data.getData())) {
            return true; // 如果数据为空，认为是成功的，因为没有超出限制
        } else {
            //入库
            oaListRSP.setSum(oaListRSP.getSum()+data.getSum());
            List<CicoOaListRSP.CicoOaListBody> data1 = oaListRSP.getData();
            if (data1 == null) {
                data1 = new ArrayList<>();
                oaListRSP.setData(data1);
            }
            data1.addAll(data.getData());
            //保存流水 自定义保存
            reqData.setPagenum(reqData.getPagenum() + 1);
            return queryWithSplit(reqData, depth + 1, oaListRSP);
        }
    }

    //拉取集团数据
    private CicoOaListRSP fetchData(CicoOaListReq reqData) {
        String url = String.format("%s/bpm-http/portal/ofs/getOfsData", esbUrl);
        long startTime = System.currentTimeMillis();
        try {
            String responseData = HttpUtil.httpGetRequest(getAddParam(url, reqData), getHttpHeadParam());
            /*responseData = "{\"data\":[{\"flowid\":\"4922044\",\"receiver\":\"13456147060\",\"syscode\":\"ztzl\",\"isremark\":\"0\",\"appurl\":\"https://mobile.cncico.com:9443/spa/custom/static/index" +
                    ".html#/main/cs/app/bae5a76d5f094e93bc14472537b203e7_page?type=2&appid=1&syscode=ztzl&url=http%3A%2F%2F10.158.33.204%2Fprocess%2Fdetail%2F4922044%3Fauth%3Doa%26mithrasMsgId%3D1744978%26mithrasClientId%3D184\",\"pcurl\":\"http://newportal.cncico.com/mobilemode/apps/zjjt/toWorkflow.jsp?type=1&appid=1&syscode=ztzl&url=http%3A%2F%2Frzy.zsrzzl.com.cn%2Fprocess%2Freceive%2Fdetail%2F4922044%3FtypeId%3Dapproval%26businessKey%3D1738%26diff%3DtaskId%26clientType%3DSTART_RENT%26flag%3Dinfo%26mithrasMsgId%3D1744978%26mithrasClientId%3D184\",\"createdate\":\"2025-03-11\",\"createtime\":\"18:00:00\",\"requestlevel\":\"0\"},{\"flowid\":\"4922124\",\"receiver\":\"13456147060\",\"syscode\":\"ztzl\",\"isremark\":\"0\",\"appurl\":\"https://mobile.cncico.com:9443/spa/custom/static/index.html#/main/cs/app/bae5a76d5f094e93bc14472537b203e7_page?type=2&appid=1&syscode=ztzl&url=http%3A%2F%2F10.158.33.204%2Fprocess%2Fdetail%2F4922124%3Fauth%3Doa%26mithrasMsgId%3D1744985%26mithrasClientId%3D184\",\"pcurl\":\"http://newportal.cncico.com/mobilemode/apps/zjjt/toWorkflow.jsp?type=1&appid=1&syscode=ztzl&url=http%3A%2F%2Frzy.zsrzzl.com.cn%2Fprocess%2Freceive%2Fdetail%2F4922124%3FtypeId%3Dapproval%26businessKey%3D1739%26diff%3DtaskId%26clientType%3DSTART_RENT%26flag%3Dinfo%26mithrasMsgId%3D1744985%26mithrasClientId%3D184\",\"createdate\":\"2025-03-11\",\"createtime\":\"18:00:01\",\"requestlevel\":\"0\"},{\"flowid\":\"4922940\",\"receiver\":\"13456147060\",\"syscode\":\"ztzl\",\"isremark\":\"0\",\"appurl\":\"https://mobile.cncico.com:9443/spa/custom/static/index.html#/main/cs/app/bae5a76d5f094e93bc14472537b203e7_page?type=2&appid=1&syscode=ztzl&url=http%3A%2F%2F10.158.33.204%2Fprocess%2Fdetail%2F4922940%3Fauth%3Doa%26mithrasMsgId%3D1745012%26mithrasClientId%3D184\",\"pcurl\":\"http://newportal.cncico.com/mobilemode/apps/zjjt/toWorkflow.jsp?type=1&appid=1&syscode=ztzl&url=http%3A%2F%2Frzy.zsrzzl.com.cn%2Fprocess%2Freceive%2Fdetail%2F4922940%3FtypeId%3Dapproval%26businessKey%3D1611%26diff%3DtaskId%26clientType%3DSTART_RENT%26flag%3Dinfo%26mithrasMsgId%3D1745012%26mithrasClientId%3D184\",\"createdate\":\"2025-03-11\",\"createtime\":\"18:42:46\",\"requestlevel\":\"0\"},{\"flowid\":\"4923034\",\"receiver\":\"13456147060\",\"syscode\":\"ztzl\",\"isremark\":\"0\",\"appurl\":\"https://mobile.cncico.com:9443/spa/custom/static/index.html#/main/cs/app/bae5a76d5f094e93bc14472537b203e7_page?type=2&appid=1&syscode=ztzl&url=http%3A%2F%2F10.158.33.204%2Fprocess%2Fdetail%2F4923034%3Fauth%3Doa%26mithrasMsgId%3D1745020%26mithrasClientId%3D184\",\"pcurl\":\"http://newportal.cncico.com/mobilemode/apps/zjjt/toWorkflow.jsp?type=1&appid=1&syscode=ztzl&url=http%3A%2F%2Frzy.zsrzzl.com.cn%2Fprocess%2Freceive%2Fdetail%2F4923034%3FtypeId%3Dapproval%26businessKey%3D1611%26diff%3DtaskId%26clientType%3DSTART_RENT%26flag%3Dinfo%26mithrasMsgId%3D1745020%26mithrasClientId%3D184\",\"createdate\":\"2025-03-11\",\"createtime\":\"18:43:00\",\"requestlevel\":\"0\"}],\"pagesize\":100,\"hasRight\":true,\"sum\":4,\"api_status\":true,\"hasnext\":false,\"pagenum\":1,\"allpage\":1}";
             */
            long endTime = System.currentTimeMillis();
            log.info("request http,url:{},time:{}, responseData:{}", url, endTime - startTime, responseData);

            CicoOaListRSP result = this.response(responseData);
            return result;
        } catch (Exception e) {
            log.error("oa调用异常", e);
        }
        return null;
    }



    public Map<String, String> getHttpHeadParam() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ClientId", "com.cncico.esb.opr.zszl");
        map.put("OperationCode", "com.cncico.esb.bpm.protal.ReceiveRequestInfoByJson.get");
        return map;
    }

    public String getAddParam(String url, CicoOaListReq reqData) {
        try {
            String param = "syscode=" + reqData.getSyscode() +
                    //"&flowid=" + reqData.getFlowid() +
                    "&receiver=" + reqData.getReceiver() +
                    "&createdates=" + URLEncoder.encode(reqData.getCreatedates(), "UTF-8") +
                    "&createdatee=" + URLEncoder.encode(reqData.getCreatedatee(), "UTF-8") +
                    "&pagenum=" + reqData.getPagenum() +
                    "&pagesize=" + reqData.getPagesize() +
                    "&isremark=" + reqData.getIsremark();
            return url + "?" + param;
        } catch (UnsupportedEncodingException e) {
            log.warn("uelEncode  error", e);
            return null;
        }
    }

}
