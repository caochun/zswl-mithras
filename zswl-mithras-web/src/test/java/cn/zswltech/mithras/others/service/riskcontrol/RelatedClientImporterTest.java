package cn.zswltech.mithras.others.service.riskcontrol;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/9 10:21
 */
public class RelatedClientImporterTest extends ApplicationTest {

    @Resource
    private RiskControlRelatedClientService service;

    @Test
    public void testImport() {
        File file = new File("/Users/zhaozhengkang/Downloads/关联方名录查询列表.xlsx");
        service.importFile(FileUtil.getInputStream(file));
        System.out.println();
    }

    @Test
    public void testPaymentList(){
        RiskControlRelatedTransactionPageReq req = new RiskControlRelatedTransactionPageReq();
        req.setPage(1);
        req.setPageSize(10);
        PageR<RiskControlRelatedTransactionRsp> rsp = service.paymentList(req);
        System.out.println();
    }

    @Test
    public void testCollectionList(){
        RiskControlRelatedTransactionPageReq req = new RiskControlRelatedTransactionPageReq();
        req.setPage(1);
        req.setPageSize(10);
        PageR<RiskControlRelatedTransactionRsp> rsp = service.collectionList(req);
        System.out.println();
    }
}
