//import cn.zswltech.mithras.service.mapper.model.client.TycAbnormal;
//import cn.zswltech.mithras.service.mapper.model.client.TycDishonest;
//import cn.zswltech.mithras.service.mapper.model.client.TycEquityInfo;
//import cn.zswltech.mithras.service.plugin.AutoAuditEntity;
//import cn.zswltech.mithras.service.repository.PlatformApiEnum;
//import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
//import cn.zswltech.mithras.service.repository.PlatformApiHandler;
//import cn.zswltech.mithras.service.repository.tyc.req.TycBaseReq;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycAbnormalResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycConsumptionRestrictionResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycDishonestResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycEquityInfoResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycHolderResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycJudicialResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycLawSuitResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycMortgageInfoResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycPunishmentInfoResp;
//import cn.zswltech.mithras.service.repository.tyc.resp.TycZhixingInfoResp;
//import cn.zswltech.mithras.service.service.tyc.TycAbnormalService;
//import cn.zswltech.mithras.service.service.tyc.TycDishonestService;
//import cn.zswltech.mithras.service.service.tyc.TycEquityInfoService;
//import cn.zswltech.mithras.service.service.tyc.impl.TycExecutionService;
//import com.alibaba.fastjson.JSON;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * 天眼查接口测试
// *
// * @author wangchuanhao
// * @date 2022/6/20 3:21 PM
// */
//public class TycTest extends cn.zswltech.mithras.service.ApplicationTest {
//
//    private static final Logger log = LoggerFactory.getLogger(TycTest.class);
//
//    @Resource
//    private PlatformApiHandleFactory platformApiHandleFactory;
//    @Resource
//    private TycExecutionService tycExecutionService;
//
//    @Test
//    public void abnormal() {
//        PlatformApiHandler<TycBaseReq, TycAbnormalResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_ABNORMAL);
//        TycBaseReq req = buildReq("宁夏凯捷建设工程有限公司");
//        TycAbnormalResp resp = apiHandler.execute(req);
//        log.info("天眼查查询经营异常返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void consumptionRestriction() {
//        PlatformApiHandler<TycBaseReq, TycConsumptionRestrictionResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_CONSUMPTION_RESTRICTION);
//        TycBaseReq req = buildReq("乐视控股（北京）有限公司");
//        TycConsumptionRestrictionResp resp = apiHandler.execute(req);
//        log.info("天眼查查询限制消费令返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void dishonest() {
//        PlatformApiHandler<TycBaseReq, TycDishonestResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_DISHONEST);
//        TycBaseReq req = buildReq("恩施鑫地源农业开发有限公司");
//        TycDishonestResp resp = apiHandler.execute(req);
//        log.info("天眼查查询失信人返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void equityInfo() {
//        PlatformApiHandler<TycBaseReq, TycEquityInfoResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_EQUITY_INFO);
//        TycBaseReq req = buildReq("内蒙古大草原生态产业投资有限公司");
//        TycEquityInfoResp resp = apiHandler.execute(req);
//        log.info("天眼查查询股权质押返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void holder() {
//        PlatformApiHandler<TycBaseReq, TycHolderResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_HOLDER);
//        TycBaseReq req = buildReq("91330000734530895W");
//        TycHolderResp resp = apiHandler.execute(req);
//        log.info("天眼查查询股东返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void judicial() {
//        PlatformApiHandler<TycBaseReq, TycJudicialResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_JUDICIAL);
//        TycBaseReq req = buildReq("978740860");
//        TycJudicialResp resp = apiHandler.execute(req);
//        log.info("天眼查查询司法协助返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void lawSuit() {
//        PlatformApiHandler<TycBaseReq, TycLawSuitResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_LAW_SUIT);
//        TycBaseReq req = buildReq("北京百度网讯科技有限公司");
//        TycLawSuitResp resp = apiHandler.execute(req);
//        log.info("天眼查查询法律诉讼返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void mortgageInfo() {
//        PlatformApiHandler<TycBaseReq, TycMortgageInfoResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_MORTGAGE_INFO);
//        TycBaseReq req = buildReq("讷河市丰盛现代农业农机专业合作社");
//        TycMortgageInfoResp resp = apiHandler.execute(req);
//        log.info("天眼查动产抵押返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void punishmentInfo() {
//        PlatformApiHandler<TycBaseReq, TycPunishmentInfoResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_PUNISHMENT_INFO);
//        TycBaseReq req = buildReq("北京百度网讯科技有限公司");
//        TycPunishmentInfoResp resp = apiHandler.execute(req);
//        log.info("天眼查行政处罚返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void zhixingInfo() {
//        PlatformApiHandler<TycBaseReq, TycZhixingInfoResp> apiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.TYC_ZHIXING_INFO);
//        TycBaseReq req = buildReq("河北展发房地产开发有限公司");
//        TycZhixingInfoResp resp = apiHandler.execute(req);
//        log.info("天眼查被执行人返回结果:{}", JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void syncAll() {
//        tycExecutionService.syncExternal(8L);
//        //List<TycAbnormal> newDataList = tycAbnormalService.queryAllFromTyc("宁夏凯捷建设工程有限公司");
//        //tycAbnormalService.flushData("宁夏凯捷建设工程有限公司", newDataList);
//    }
//
//    private TycBaseReq buildReq(String keyword) {
//        TycBaseReq req = new TycBaseReq();
//        req.setPageNum(1);
//        req.setPageSize(10);
//        req.setKeyword(keyword);
//        return req;
//    }
//
//}
