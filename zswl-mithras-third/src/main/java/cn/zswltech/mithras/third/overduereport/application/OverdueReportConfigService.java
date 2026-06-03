
package cn.zswltech.mithras.third.overduereport.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.enums.OverdueReportUrlENUM;
import cn.zswltech.mithras.third.mapper.model.CQRelatedMithrasInfo;
import cn.zswltech.mithras.third.financialshare.application.CQRelatedMithrasInfoService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.FinancialCommonRSP;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.handle.OverdueReportAccessTokenHandle;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.handle.OverdueReportAppTokenHandle;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.req.OverdueReportAccessTokenREQ;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.req.OverdueReportAppTokenREQ;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.rsp.OverdueReportAccessTokenRSP;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.rsp.OverdueReportAppTokenRSP;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.config.OverdueReportAuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ClassName OverdueReportConfigService
 * @Description 这里重新获取，防止有变化
 * @Author jackerhe
 **/

@Service
@Slf4j
public class OverdueReportConfigService {

    @Resource
    private OverdueReportAuthConfig overdueReportAuthConfig;

    @Resource
    private OverdueReportAccessTokenHandle overdueReportAccessTokenHandle;

    @Resource
    private OverdueReportAppTokenHandle overdueReportAppTokenHandle;

    @Resource
    private CQRelatedMithrasInfoService cqRelatedMithrasInfoService;

    public String getUrl(String url){
        return overdueReportAuthConfig.getAppBaseUrl() + url;
    }

    public Map<String, String> getHttpHeadParam(OverdueReportUrlENUM receivverInfo) {
        Map<String, String> map = new HashMap<>();
        //不过esb可能没有操作码
        if(ObjectUtil.isNotEmpty(receivverInfo.operate)){
            map.put("OperationCode", receivverInfo.operate);
        }
        map.put("ClientId", overdueReportAuthConfig.getClientId());
        String accessToken = getAccessToken(receivverInfo);
        if(ObjectUtil.isNotEmpty(accessToken)){
            map.put("access_token", accessToken);
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
    private OverdueReportAccessTokenRSP.AccessTokenData financialGetTocken(OverdueReportUrlENUM receivverInfo) {
        OverdueReportAppTokenREQ appTokenREQ = buildAppToken();
        OverdueReportAppTokenRSP appTokenRSP = overdueReportAppTokenHandle.execute(appTokenREQ);
        log.info("OverdueReportConfigService merchantGetTocken RSP {}", appTokenRSP);
        if (overdueReportAppTokenHandle.isExecuteSuccess(appTokenRSP)) {
            OverdueReportAccessTokenREQ accessTokenREQ = buildAccessToken(ObjectUtil.isEmpty(appTokenRSP.getData()) ? null : appTokenRSP.getData().getApp_token(), receivverInfo);
            OverdueReportAccessTokenRSP accessTokenRSP = overdueReportAccessTokenHandle.execute(accessTokenREQ);
            //log.info("FinancialShareService merchantGetTocken accessRsp {}", accessTokenRSP);
            if(overdueReportAccessTokenHandle.isExecuteSuccess(accessTokenRSP)){
                return ObjectUtil.isEmpty(accessTokenRSP.getData()) ? null : accessTokenRSP.getData();
            }
        }
        return null;
    }

    private OverdueReportAppTokenREQ buildAppToken(){
        OverdueReportAppTokenREQ appTokenREQ = new OverdueReportAppTokenREQ();
        appTokenREQ.setAppId(overdueReportAuthConfig.getAppId());
        appTokenREQ.setAppSecuret(overdueReportAuthConfig.getAppSecuret());
        appTokenREQ.setAccountId(overdueReportAuthConfig.getAccountId());
        appTokenREQ.setTenantid(overdueReportAuthConfig.getTenantid());
        appTokenREQ.setLanguage("china");
        return appTokenREQ;
    }

    public OverdueReportAccessTokenREQ buildAccessToken(String appToken, OverdueReportUrlENUM receivverInfo){
        OverdueReportAccessTokenREQ tokenREQ = new OverdueReportAccessTokenREQ();
        tokenREQ.setAccountId(overdueReportAuthConfig.getAccountId());
        tokenREQ.setTenantid(overdueReportAuthConfig.getTenantid());
        tokenREQ.setApptoken(appToken);
        tokenREQ.setUser(overdueReportAuthConfig.getUser());
        tokenREQ.setUsertype(overdueReportAuthConfig.getUsertype());
        return tokenREQ;
    }


    public String getAccessToken(OverdueReportUrlENUM receivverInfo){
        //重新获取tocken
        //测试用本地取tocken
        OverdueReportAccessTokenRSP.AccessTokenData  tokenData = financialGetTocken(receivverInfo);
        if(tokenData != null && ObjectUtil.isNotNull(tokenData)){
            return tokenData.getAccess_token();
        }
        return null;
    }
}

