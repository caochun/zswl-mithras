package cn.zswltech.mithras.service.providence;

import cn.zswltech.mithras.factory.feign.ProvidenceRelationApiClient;
import com.zswltec.providence.api.RelationApi;

import com.zswltec.providence.dto.RelationReq;
import com.zswltec.providence.dto.RelationRsp;
import com.zswltec.providence.dto.base.PageR;
import com.zswltec.providence.dto.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/18 16:30
 */
@RestController
@Slf4j
public class RelationProxyController implements RelationApi {

    @Resource
    private ProvidenceRelationApiClient providenceRelationApiClient;

    @Override
    public R<PageR<RelationRsp>> relation(RelationReq req) {
        return providenceRelationApiClient.relation(req);
    }
}
