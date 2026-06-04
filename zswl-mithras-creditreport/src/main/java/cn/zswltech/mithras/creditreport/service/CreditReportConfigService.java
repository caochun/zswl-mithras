
package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.creditreport.enums.CreditApplyXJUrlENUM;
import cn.zswltech.mithras.creditreport.service.vo.CreditReportAppAuthConfig;
import cn.zswltech.mithras.service.util.MD5MsgDigest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @ClassName CreditReportConfigService
 * @Description ClientId com.cncico.esb.opr.zszl
 * @Author jackerhe
 * @Date 2022/10/20 3:33 下午
 * @Version 1.0
 **/

@Service
@Slf4j
public class CreditReportConfigService {

    @Resource
    private CreditReportAppAuthConfig creditReportAppAuthConfig;



    public String getUrl(String url){
        return creditReportAppAuthConfig.getBaseUrl() + url;
    }

    public Map<String, String> getHttpHeadParam(CreditApplyXJUrlENUM receivverInfo) {
        Map<String, String> map = new HashMap<>();
        if(ObjectUtil.isNotEmpty(receivverInfo) || ObjectUtil.equals(receivverInfo, CreditApplyXJUrlENUM.CREDIT_REPORT_QUERY_ENT_FOUR_ELE_AUTH)){
            map.put("Content-Type", "application/json");
            map.put("Accept", MediaType.APPLICATION_JSON.toString());
        }else {
            map.put("Content-Type", "application/json");
            map.put("Accept", MediaType.APPLICATION_JSON.toString());
        }
        return map;
    }


    public String getSignature() {
        try {
            return MD5MsgDigest.MD5Bit32(creditReportAppAuthConfig.getPassword()) + Base64.encode(creditReportAppAuthConfig.getAccount());
        } catch (Exception e) {
            log.warn("md5加密失败");
            return null;
        }
    }


}

