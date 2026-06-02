package cn.zswltech.mithras.third.providence.controller;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.third.providence.feign.ProvidencePeerComparisonApiClient;
import com.zswltec.providence.api.PeerComparisonApi;
import com.zswltec.providence.dto.base.R;
import com.zswltec.providence.dto.req.*;
import com.zswltec.providence.dto.rsp.EnterpriseStateRsp;
import com.zswltec.providence.dto.rsp.PeerComparisonRsp;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2025/1/13 17:12
 */
@RestController
public class PeerComparisonController implements PeerComparisonApi {

    @Resource
    private ProvidencePeerComparisonApiClient peerComparisonApiClient;

    @Override
    public R<PeerComparisonRsp> peerComparison(PeerComparisonReq req) {
        req.setAccount(AccountUtil.getLoginInfo().getAccount());
        return peerComparisonApiClient.peerComparison(req);
    }

    @Override
    public R<List<String>> getLatestYear() {
        return peerComparisonApiClient.getLatestYear();
    }

    @Override
    public R<List<EnterpriseStateRsp>> enterpriseState(EnterpriseStateReq req) {
        req.setAccount(AccountUtil.getLoginInfo().getAccount());
        return peerComparisonApiClient.enterpriseState(req);
    }

    @Override
    public R refresh(RefreshReq req) {
        return peerComparisonApiClient.refresh(req);
    }

    @Override
    public R<List<PeerComparisonRsp.PeerComparisonInfo>> peerComparisonList(PeerComparisonListReq req) {
        return peerComparisonApiClient.peerComparisonList(req);
    }

    @Override
    public R peerComparisonConfig(PeerComparisonConfigReq req) {
        req.setAccount(AccountUtil.getLoginInfo().getAccount());
        return peerComparisonApiClient.peerComparisonConfig(req);
    }
}
