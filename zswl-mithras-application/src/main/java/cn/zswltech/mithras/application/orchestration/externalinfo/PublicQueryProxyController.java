package cn.zswltech.mithras.application.orchestration.externalinfo;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.rating.feign.PublicInfoApiClient;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.payment.mapper.model.pubinfo.PublicInfoQuery;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.api.payment.dto.pubinfo.OuterPublicQueryReq;
import cn.zswltech.mithras.third.providence.entity.OuterInfoRecord;
import cn.zswltech.mithras.third.providence.service.impl.OuterInfoRecordService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoQueryService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zswltec.providence.dto.PublicInfoQueryReq;
import com.zswltec.providence.dto.PublicInfoRsp;
import com.zswltec.providence.dto.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/18 16:30
 */
@RestController
@Slf4j
@Api(tags = "外部公开信息查询")
public class PublicQueryProxyController{

    @Resource
    private PublicInfoApiClient publicInfoApiClient;
    @Resource
    private OuterInfoRecordService outerInfoRecordService;
    @Resource
    private PublicInfoQueryService publicInfoQueryService;
    @Resource
    private ClientService clientService;

    @PostMapping("/outer/public/query")
    @ApiOperation("外部公开信息查询")
    public R<Boolean> query(@RequestBody OuterPublicQueryReq req) {
        PublicInfoQuery byId = publicInfoQueryService.getById(req.getPublicInfoQueryId());
        if(ObjectUtil.isEmpty(byId)){
            throw new MithrasException("查询id对应数据不存在");
        }
        if(ObjectUtil.isEmpty(byId.getClientId())){
            throw new MithrasException("没有指定查询客户");
        }
        Client client = clientService.getById(byId.getClientId());
        if(ObjectUtil.isEmpty(client) || !ClientType.CORPORATION.name().equals(client.getClientType())){
            throw new MithrasException("没有指定查询客户");
        }
        PublicInfoQueryReq publicInfoQueryReq = new PublicInfoQueryReq();
        publicInfoQueryReq.setEnterpriseName(client.getClientName());
        publicInfoQueryReq.setUscc(client.getUscCode());

        R<List<PublicInfoRsp>> rsp = publicInfoApiClient.query(publicInfoQueryReq);
        if(!rsp.isSuccess()){
            log.error("调用providence-service失败,{}",rsp.getMsg());
            return R.ok(false);
        }
        OuterInfoRecord pre = outerInfoRecordService.getOne(Wrappers.<OuterInfoRecord>lambdaQuery()
                .eq(OuterInfoRecord::getPublicInfoQueryId, req.getPublicInfoQueryId())
                .orderByDesc(OuterInfoRecord::getVersion)
                .last("limit 1"));
        final int version = ObjectUtil.isEmpty(pre) ? 1 : pre.getVersion() + 1;
        List<PublicInfoRsp> publicInfoRsps = rsp.getData();
        List<OuterInfoRecord> outerInfoRecords = new ArrayList<>();
        for (PublicInfoRsp publicInfoRsp : publicInfoRsps) {
            publicInfoRsp.getResult().forEach((idx,result)->{
                OuterInfoRecord one = new OuterInfoRecord();
                one.setPublicInfoQueryId(req.getPublicInfoQueryId());
                one.setConfigKey(publicInfoRsp.getKey());
                one.setIndex(idx);
                one.setQueryResult(result);
                one.setVersion(version);
                outerInfoRecords.add(one);
            });
        }
        outerInfoRecordService.saveBatch(outerInfoRecords);
        return R.ok(true);
    }
}
