package cn.zswltech.mithras.others;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.DataMinerClient;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.req.QueryDmIndicatorReq;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.resp.DataMinerRsp;
import cn.zswltech.mithras.third.dataminer.infrastructure.client.resp.QueryDmIndicatorRsp;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2024/12/12
 * @description
 */
public class DataMinerTest extends ApplicationTest {
    @Resource
    private DataMinerClient dataMinerClient;

    @Test
    public void queryDmIndicatorTest() {
        QueryDmIndicatorReq req = new QueryDmIndicatorReq();
        req.setYear(2024);
        req.setAreaUniCode(401201129L);
        DataMinerRsp<QueryDmIndicatorRsp> result = dataMinerClient.doRequest(req, QueryDmIndicatorRsp.class);
        System.out.println(JSONUtil.toJsonStr(result));
    }
}
