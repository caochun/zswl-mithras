package cn.zswltech.mithras.others.service.convert.projreview;

import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewBaseInfoConverter;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/8 11:15
 */
class ProjReviewBaseInfoConverterTest {

    @Test
    void entityToDetailRSP() {
        ProjReviewBaseInfo baseInfo = new ProjReviewBaseInfo();
        baseInfo.setLesseeInfo("[{\"clientId\":222,\"clientType\":null,\"stockRiskExposure\":10000,\"clientName\":\"云\"},{\"clientId\":290,\"clientType\":null,\"stockRiskExposure\":1000000000,\"clientName\":\"机械部门分管领导创建\"},{\"clientId\":186,\"clientType\":null,\"stockRiskExposure\":1000000000,\"clientName\":\"樱桃小丸子113\"}]\n");
        baseInfo.setLeaseTypes("[\"hui_zu\",\"zhi_zu\"]");
        baseInfo.setProjCosponsorUserIds("[1,2]");
        ProjReviewBaseInfoDetailRSP projReviewBaseInfoDetailRSP = ProjReviewBaseInfoConverter.INSTANCE.entityToDetailRSP(baseInfo);
        System.out.println();
    }
}