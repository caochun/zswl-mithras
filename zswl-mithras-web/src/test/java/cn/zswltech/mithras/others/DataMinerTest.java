package cn.zswltech.mithras.others;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.third.dataminer.DataMinerClient;
import cn.zswltech.mithras.service.service.third.dataminer.req.QueryDmIndicatorReq;
import cn.zswltech.mithras.service.service.third.dataminer.rsp.DataMinerRsp;
import cn.zswltech.mithras.service.service.third.dataminer.rsp.QueryDmIndicatorRsp;
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
