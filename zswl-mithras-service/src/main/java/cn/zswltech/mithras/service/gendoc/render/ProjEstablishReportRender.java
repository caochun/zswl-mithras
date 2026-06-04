package cn.zswltech.mithras.service.gendoc.render;
import cn.zswltech.mithras.customer.domain.enums.SubjectItemDisplayDimension;
import cn.zswltech.mithras.customer.domain.enums.SubjectQuarterType;
import cn.zswltech.mithras.customer.domain.enums.RelationshipType;
import cn.zswltech.mithras.customer.domain.enums.ShareholderType;
import cn.zswltech.mithras.customer.domain.enums.SubjectReportType;
import cn.zswltech.mithras.customer.domain.enums.MarriageType;
import cn.zswltech.mithras.customer.domain.enums.GenderType;
import cn.zswltech.mithras.customer.domain.enums.OrgScaleType;
import cn.zswltech.mithras.customer.domain.enums.GovernmentSubjectItemType;
import cn.zswltech.mithras.customer.domain.enums.SubjectItemType;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListRSP;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRSP;
import cn.zswltech.mithras.dto.client.external.tyc.*;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRSP;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListRSP;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListREQ;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.controller.client.CorpSubjectItemController;
import cn.zswltech.mithras.service.controller.client.EnvironmentPenaltyController;
import cn.zswltech.mithras.service.controller.client.TycController;
import cn.zswltech.mithras.service.controller.client.ZhongdengInfoController;
import cn.zswltech.mithras.service.controller.projestablish.ProjEstablishBaseInfoController;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.*;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.*;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.service.others.Const;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.system.service.DictService;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.*;
import cn.zswltech.mithras.customer.application.lib.client.impl.ClientVersionServiceImpl;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishLeasePriceService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.Tables;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 立项报告 word 文档生成
 * 立项数据使用编辑区数据
 * 客户数据使用最新版本数据
 *
 * @author wangchuanhao
 * @date 2022/7/24 12:29 PM
 */
@Component
@Slf4j
public class ProjEstablishReportRender extends AbstractBasicRender<Long> {

    private static final String TEMPLATE_FILE_PATH = "/doc/模板-租赁立项报告.docx";

    private static final String PROJ_NAME = "projName";
    private static final String REPORT_CREATE_DATE = "reportCreateDate";

    private static final String LESSEE_INFO_WITH_ORDER = "lesseeInfoWithOrder";
    private static final String BUSINESS_TYPE = "businessType";
    private static final String CREDIT_AMOUNT = "creditAmount";
    private static final String PROJECT_MANAGER = "projectManager";
    private static final String BUSINESS_DEPT_MASTER = "businessDeptMaster";
    private static final String BUSINESS_DEPT_LEADER = "businessDeptLeader";
    private static final String RISK_CONTROL_MANAGER = "riskControlManager";

    private static final String PROJ_SOURCE = "projSource";
    private static final String PROJ_BACKGROUD = "projBackgroud";
    private static final String BUSINESS_DEPT = "businessDept";
    private static final String PROJ_SPONSOR_USER = "projSponsorUser";
    private static final String PROJ_COSPONSOR_USER = "projCosponsorUser";

    private static final String LEASE_MONTH_COUNT = "leaseMonthCount";
    private static final String CREDIT_AMOUNT_LOOP = "creditAmountLoop";
    private static final String LESSEE_INFO_WITH_COMMA = "lesseeInfoWithComma";
    private static final String LEASE_TYPES_WITH_COMMA = "leaseTypesWithComma";
    private static final String FUNDS_PURPOSE = "fundsPurpose";
    private static final String EARNEST_MONEY = "earnestMoney";
    private static final String CONSULTING_FEE = "consultingFee";
    private static final String NOMINAL_PRICE = "nominalPrice";
    private static final String RATE_TYPE = "rateType";
    private static final String LEASE_RATE_PERCENT = "leaseRatePercent";
    private static final String REPAY_TIMES_TOTAL = "repayTimesTotal";
    private static final String REPAY_TIMES_YEARLY = "repayTimesYearly";
    private static final String GUARANTEE_INFO_WITH_ORDER = "guaranteeInfoWithOrder";
    private static final String RENTAL_CALC_TYPE = "rentalCalcType";
    private static final String PLEDGOR_MORTGAGE = "pledgorMortgage";
    private static final String IRR_PERCENT = "irrPercent";
    private static final String DOWN_PAYMENT = "downPayment";

//    private static final String PUBLIC_INFORMATION_LIST_SECTION = "publicInformationListSection";
//    private static final String PI_CILENT_NAME = "pi_cilentName";
//    private static final String PI_LAW_SUIT_TABLE = "pi_lawSuitTable";
//    private static final String PI_LAW_SUIT_TEXT = "pi_lawSuitText";
//    private static final String PI_ZHIXING_TABLE = "pi_zhixingTable";
//    private static final String PI_ZHIXING_TEXT = "pi_zhixingText";
//    private static final String PI_ZHONGDENG_TABLE = "pi_zhongdengTable";
//    private static final String PI_ZHONGDENG_TEXT = "pi_zhongdengText";
//    private static final String KEY_MANAGER_TABLE = "keyManagerTable";

    private static final String CLIENT_ANALYZE_LIST_SECTION = "clientAnalyzeListSection";
    private static final String CA_CLIENT_ANALYZE_TITLE = "ca_clientAnalyzeTitle";

    private static final String CA_CORP_SECTION = "ca_corpSection";
    private static final String CA_C_TITLE = "ca_c_title";
    private static final String CA_C_CLIENT_NAME = "ca_c_clientName";
    private static final String CA_C_INDUSTRY_TYPE = "ca_c_industryType";
    private static final String CA_C_ORG_SCALE = "ca_c_orgScale";
    private static final String CA_C_ESTABLISH_DATE = "ca_c_establishDate";
    private static final String CA_C_BIZ_LICENSE_CODE = "ca_c_bizLicenseCode";
    private static final String CA_C_REGISTER_CAPITAL = "ca_c_registerCapital";
    private static final String CA_C_REAL_CAPITAL = "ca_c_realCapital";
    private static final String CA_C_REGISTER_ADDRESS = "ca_c_registerAddress";
    private static final String CA_C_WORK_ADDRESS = "ca_c_workAddress";
    private static final String CA_C_BUSINESS_SCOPE = "ca_c_businessScope";
    private static final String CA_C_KEY_MANAGER_TABLE = "ca_c_keyManagerTable";
    private static final String CA_C_SHAREHOLDER_TABLE = "ca_c_shareholderTable";
    private static final String CA_C_RELATED_ENTERPRISE_TABLE = "ca_c_relatedEnterpriseTable";
    private static final String CA_C_MORTGAGE_TABLE = "ca_c_mortgageTable";
    private static final String CA_C_EQUITY_TABLE = "ca_c_equityTable";
    private static final String CA_C_PUNISHMENT_TABLE = "ca_c_punishmentTable";
    private static final String CA_C_ENVIRONMENT_PENALTY_TABLE = "ca_c_environmentPenaltyTable";
    private static final String CA_C_ABNORMAL_TABLE = "ca_c_abnormalTable";
    private static final String CA_C_JUDICIAL_TABLE = "ca_c_judicialTable";
    private static final String CA_C_CONSUMPTION_RESTRICTION_TABLE = "ca_c_consumptionRestrictionTable";
    private static final String CA_C_ZHIXING_TABLE = "ca_c_zhixingTable";
    private static final String CA_C_DISHONEST_TABLE = "ca_c_dishonestTable";
    private static final String CA_C_ZHONGDENG_TABLE = "ca_c_zhongdengTable";

    private static final String CA_NORMAL_SECTION = "ca_normalSection";
    private static final String CA_N_CLIENT_NAME = "ca_n_clientName";
    private static final String CA_N_CERT_NUMBER = "ca_n_certNumber";
    private static final String CA_N_GENDER = "ca_n_gender";
    private static final String CA_N_MARRIAGE_TYPE = "ca_n_marriageType";

    private static final String SUBJECT_LIST_SECTION = "subjectListSection";
    private static final String S_SUBJECT_TABLE_LIST_SECTION = "s_subjectTableListSection";
    private static final String S_TITLE = "s_title";
    private static final String S_ST_TABLE_NAME = "s_st_tableName";
    private static final String S_ST_SUBJECT_TABLE = "s_st_subjectTable";


    @Resource
    private ProjEstablishBaseInfoController projEstablishBaseInfoController;
    @Resource
    private ProjEstablishLeasePriceService leasePriceService;
    @Resource
    private TycController tycController;
    @Resource
    private ZhongdengInfoController zhongdengInfoController;
    @Resource
    private EnvironmentPenaltyController environmentPenaltyController;
    @Resource
    private ClientVersionServiceImpl clientVersionService;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private CorpCommerceInfoLibHandlerImpl corpCommerceInfoLibHandler;
    @Resource
    private CorpShareholderInfoLibMapper corpShareholderInfoLibMapper;
    @Resource
    private CorpShareholderInfoLibHandlerImpl corpShareholderInfoLibHandler;
    @Resource
    private CorpRelatedEnterpriseLibMapper corpRelatedEnterpriseLibMapper;
    @Resource
    private CorpRelatedEnterpriseLibHandlerImpl corpRelatedEnterpriseLibHandler;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
    @Resource
    private CorpAddressInfoLibHandlerImpl corpAddressInfoLibHandler;
    @Resource
    private CorpContactInfoLibMapper corpContactInfoLibMapper;
    @Resource
    private CorpContactInfoLibHandlerImpl corpContactInfoLibHandler;
    @Resource
    private NormalBaseInfoLibMapper normalBaseInfoLibMapper;
    @Resource
    private NormalBaseInfoLibHandlerImpl normalBaseInfoLibHandler;
    @Resource
    private CorpSubjectItemController corpSubjectItemController;
    @Resource
    private DictService dictService;
    @Resource
    @Qualifier("reportThreadPool")
    private ThreadPoolTaskExecutor reportThreadPool;

    /**
     * 渲染并输出报告
     *
     * @param outputStream
     * @param projEstablishId
     */
    @Override
    @SneakyThrows
    public String render(OutputStream outputStream, Long projEstablishId) {
        StopWatch sw = new StopWatch();
        // 收集数据
        // 字典数据
        Map<String, Map<String, String>> dictCacheMap = new HashMap<>();
        // 1.立项数据
        sw.start("projEstablishInfo");
        ProjEstablishBaseInfoDetailREQ projEstablishBaseInfoDetailREQ = new ProjEstablishBaseInfoDetailREQ();
        projEstablishBaseInfoDetailREQ.setId(projEstablishId);
        ProjEstablishBaseInfoListRSP projEstablishInfo = projEstablishBaseInfoController.detail(projEstablishBaseInfoDetailREQ).getData();
        if (Objects.isNull(projEstablishInfo)) {
            throw new MithrasException("立项数据存在");
        }
        // 补数据防npe
        projEstablishInfo.setLesseeInfo(Optional.ofNullable(projEstablishInfo.getLesseeInfo()).orElse(new ArrayList<>()));
        projEstablishInfo.setGuaranteeInfo(Optional.ofNullable(projEstablishInfo.getGuaranteeInfo()).orElse(new ArrayList<>()));
        projEstablishInfo.setMortgagorInfo(Optional.ofNullable(projEstablishInfo.getMortgagorInfo()).orElse(new ArrayList<>()));
        projEstablishInfo.setPledgorInfo(Optional.ofNullable(projEstablishInfo.getPledgorInfo()).orElse(new ArrayList<>()));
        projEstablishInfo.getLesseeInfo().forEach(s -> s.setClientType(Optional.ofNullable(s.getClientType()).orElse(ClientType.CORPORATION.name())));
        sw.stop();

        // 2.立项数据 租赁 报价方案
//        ProjEstablishLeasePriceListREQ req = new ProjEstablishLeasePriceListREQ();
//        req.setProjEstablishId(projEstablishId);
//        req.setPageSize(1);
//        ProjEstablishLeasePriceRSP leasePriceInfo = projEstablishLeasePriceController.list(req).getData().getList().stream().findFirst().orElse(new ProjEstablishLeasePriceRSP());
        ProjEstablishLeasePriceRSP leasePriceInfo = leasePriceService.detail(projEstablishId);

        // 3.承租人、担保人信息
        // 承租人法人、承租人自然人、担保人法人、担保人自然人
        List<Long> lesseeCorpIdList = projEstablishInfo.getLesseeInfo().stream().filter(l -> ClientType.CORPORATION.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> lesseeNormalIdList = projEstablishInfo.getLesseeInfo().stream().filter(l -> ClientType.NORMAL.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> guaranteeCorpIdList = projEstablishInfo.getGuaranteeInfo().stream().filter(l -> ClientType.CORPORATION.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> guaranteeNormalIdList = projEstablishInfo.getGuaranteeInfo().stream().filter(l -> ClientType.NORMAL.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> mortgagorCorpIdList = projEstablishInfo.getMortgagorInfo().stream().filter(l -> ClientType.CORPORATION.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> mortgagorNormalIdList = projEstablishInfo.getMortgagorInfo().stream().filter(l -> ClientType.NORMAL.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> pledgorCorpIdList = projEstablishInfo.getPledgorInfo().stream().filter(l -> ClientType.CORPORATION.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> pledgorNormalIdList = projEstablishInfo.getPledgorInfo().stream().filter(l -> ClientType.NORMAL.name().equals(l.getClientType())).map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList());
        List<Long> allCorpIdList = Stream.of(lesseeCorpIdList, guaranteeCorpIdList, mortgagorCorpIdList, pledgorCorpIdList).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        List<Long> allNormalIdList = Stream.of(lesseeNormalIdList, guaranteeNormalIdList, mortgagorNormalIdList, pledgorNormalIdList).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        Map<Long, ProjEstablishPersonInfo> personMap = Stream.of(projEstablishInfo.getLesseeInfo(), projEstablishInfo.getGuaranteeInfo(), projEstablishInfo.getMortgagorInfo(), projEstablishInfo.getPledgorInfo()).flatMap(Collection::stream).collect(Collectors.toMap(ProjEstablishPersonInfo::getClientId, p -> p, (k1, k2) -> k1));

        sw.start("piData");
        // 3.1 法人公开信息查询
        // 动产抵押、股权出质、行政处罚、环保处罚、经营异常、司法协助、限制消费令、被执行人、失信人、中登网、法律诉讼
        CompletableFuture<Map<Long, List<TycMortgageInfoRSP>>> piMortgateFuture = CompletableFuture.supplyAsync(() -> mortgageSearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<TycEquityInfoRSP>>> piEquityFuture = CompletableFuture.supplyAsync(() -> equitySearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<TycPunishmentInfoRSP>>> piPunishmentFuture = CompletableFuture.supplyAsync(() -> punishmentSearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<EnvironmentPenaltyRSP>>> piEnvironmentFuture = CompletableFuture.supplyAsync(() -> environmentPenaltySearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<TycAbnormalRSP>>> piAbnormalFuture = CompletableFuture.supplyAsync(() -> abnormalSearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<TycJudicialRSP>>> piJudicialFuture = CompletableFuture.supplyAsync(() -> judicialSearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<TycConsumptionRestrictionRSP>>> piConsumptionRestrictionFuture = CompletableFuture.supplyAsync(() -> consumptionRestrictionSearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<TycZhixingInfoRSP>>> piZhixingFuture = CompletableFuture.supplyAsync(() -> zhixingSearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<TycDishonestRSP>>> piDishonestFuture = CompletableFuture.supplyAsync(() -> dishonestSearch(allCorpIdList), reportThreadPool);
        CompletableFuture<Map<Long, List<ZhongdengInfoRSP>>> piZhongdengFuture = CompletableFuture.supplyAsync(() -> zhongdengSearch(allCorpIdList), reportThreadPool);

        Map<Long, List<TycMortgageInfoRSP>> piMortgateDataMap = piMortgateFuture.get();
        Map<Long, List<TycEquityInfoRSP>> piEquityDataMap = piEquityFuture.get();
        Map<Long, List<TycPunishmentInfoRSP>> piPunishmentDataMap = piPunishmentFuture.get();
        Map<Long, List<EnvironmentPenaltyRSP>> piEnvironmentPenaltyDataMap = piEnvironmentFuture.get();
        Map<Long, List<TycAbnormalRSP>> piAbnormalDataMap = piAbnormalFuture.get();
        Map<Long, List<TycJudicialRSP>> piJudicialDataMap = piJudicialFuture.get();
        Map<Long, List<TycConsumptionRestrictionRSP>> piConsumptionRestrictionDataMap = piConsumptionRestrictionFuture.get();
        Map<Long, List<TycZhixingInfoRSP>> piZhixingDataMap = piZhixingFuture.get();
        Map<Long, List<TycDishonestRSP>> piDishonestDataMap = piDishonestFuture.get();
        Map<Long, List<ZhongdengInfoRSP>> piZhongdengDataMap = piZhongdengFuture.get();
        // Map<Long, List<TycLawSuitRSP>> piLawSuitDataMap = lawSuitSearch(allCorpIdList);
        sw.stop();

        sw.start("clientBaseInfo");
        // 3.2 法人工商信息、法人股东信息、法人关联企业、法人地址信息、法人联系人信息、自然人基本信息  需要找到最新版本数据
        Map<Long, CorpCommerceInfoDetailRSP> corpCommerceMap = new HashMap<>();
        Map<Long, List<CorpShareholderInfoListRSP>> corpShareholderMap = new HashMap<>();
        Map<Long, List<CorpRelatedEnterpriseListRSP>> corpRelatedEnterpriseMap = new HashMap<>();
        Map<Long, List<CorpAddressInfoListRSP>> corpAddressMap = new HashMap<>();
        Map<Long, List<CorpContactInfoListRSP>> corpContactMap = new HashMap<>();
        Map<Long, NormalBaseInfoDetailRSP> normalBaseMap = new HashMap<>();
        for (Long corpId : allCorpIdList) {
            CommonVersion newestVersion = clientVersionService.findNewestVersion(corpId);
            if (Objects.isNull(newestVersion)) {
                log.error("客户版本数据为空，clientId:{}", corpId);
                // 不跳过，理论上就是需要客户有版本数据才能选
            }
            CompletableFuture commerceFuture = CompletableFuture.runAsync(() -> {
                CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibMapper.selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                        .eq(CorpCommerceInfoLib::getClientId, corpId)
                        .eq(CorpCommerceInfoLib::getVersion, newestVersion.getVersion())
                        .last("LIMIT 1")
                );
                corpCommerceMap.put(corpId, corpCommerceInfoLibHandler.actualLib2Rsp(corpCommerceInfoLib));
            }, reportThreadPool);

            CompletableFuture shareholderFuture = CompletableFuture.runAsync(() -> {
                List<CorpShareholderInfoLib> shareholderInfoLibList = corpShareholderInfoLibMapper.selectList(Wrappers.<CorpShareholderInfoLib>lambdaQuery()
                        .eq(CorpShareholderInfoLib::getClientId, corpId)
                        .eq(CorpShareholderInfoLib::getVersion, newestVersion.getVersion())
                );
                corpShareholderMap.put(corpId, shareholderInfoLibList.stream().map(corpShareholderInfoLibHandler::actualLib2Rsp).collect(Collectors.toList()));
            }, reportThreadPool);

            CompletableFuture relatedEnterpriseFuture = CompletableFuture.runAsync(() -> {
                List<CorpRelatedEnterpriseLib> relatedEnterpriseLibList = corpRelatedEnterpriseLibMapper.selectList(Wrappers.<CorpRelatedEnterpriseLib>lambdaQuery()
                        .eq(CorpRelatedEnterpriseLib::getClientId, corpId)
                        .eq(CorpRelatedEnterpriseLib::getVersion, newestVersion.getVersion())
                );
                corpRelatedEnterpriseMap.put(corpId, relatedEnterpriseLibList.stream().map(corpRelatedEnterpriseLibHandler::actualLib2Rsp).collect(Collectors.toList()));
            }, reportThreadPool);

            CompletableFuture addressFuture = CompletableFuture.runAsync(() -> {
                List<CorpAddressInfoLib> addressInfoLibList = corpAddressInfoLibMapper.selectList(Wrappers.<CorpAddressInfoLib>lambdaQuery()
                        .eq(CorpAddressInfoLib::getClientId, corpId)
                        .eq(CorpAddressInfoLib::getVersion, newestVersion.getVersion())
                );
                corpAddressMap.put(corpId, addressInfoLibList.stream().map(corpAddressInfoLibHandler::actualLib2Rsp).collect(Collectors.toList()));
            }, reportThreadPool);

            CompletableFuture contactFuture = CompletableFuture.runAsync(() -> {
                List<CorpContactInfoLib> contactInfoLibList = corpContactInfoLibMapper.selectList(Wrappers.<CorpContactInfoLib>lambdaQuery()
                        .eq(CorpContactInfoLib::getClientId, corpId)
                        .eq(CorpContactInfoLib::getVersion, newestVersion.getVersion())
                );
                corpContactMap.put(corpId, contactInfoLibList.stream().map(corpContactInfoLibHandler::actualLib2Rsp).collect(Collectors.toList()));
            }, reportThreadPool);

            CompletableFuture.allOf(commerceFuture, shareholderFuture, relatedEnterpriseFuture, addressFuture, contactFuture).get();
        }
        for (Long normalId : allNormalIdList) {
            CommonVersion newestVersion = clientVersionService.findNewestVersion(normalId);
            if (Objects.isNull(newestVersion)) {
                log.error("客户版本数据为空，clientId:{}", normalId);
                // 不跳过，理论上就是需要客户有版本数据才能选
            }
            NormalBaseInfoLib normalBaseInfoLib = normalBaseInfoLibMapper.selectOne(Wrappers.<NormalBaseInfoLib>lambdaQuery()
                    .eq(NormalBaseInfoLib::getClientId, normalId)
                    .eq(NormalBaseInfoLib::getVersion, newestVersion.getVersion())
                    .last("LIMIT 1")
            );
            normalBaseMap.put(normalId, normalBaseInfoLibHandler.actualLib2Rsp(normalBaseInfoLib));
        }
        sw.stop();

        // 处理数据并填充
        int level1TitleIndex = 2;
        HashMap<String, Object> renderMap = new HashMap<>();
        renderMap.put(PROJ_NAME, projEstablishInfo.getProjName());
        renderMap.put(REPORT_CREATE_DATE, LocalDateTimeUtil.format(LocalDate.now(), "yyyy年MM月dd日"));
        renderMap.put(LESSEE_INFO_WITH_ORDER, genLesseeInfoWithOrder(projEstablishInfo.getLesseeInfo()));
        renderMap.put(CREDIT_AMOUNT, Optional.ofNullable(leasePriceInfo.getApplyCreditAmount()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000))).map(BigDecimal::toPlainString).map(bs -> bs + "万元").orElse(""));
        renderMap.put(BUSINESS_TYPE, Optional.ofNullable(ProjectBizType.of(projEstablishInfo.getBizType())).map(p -> p.display).orElse(""));
        renderMap.put(PROJECT_MANAGER, Optional.ofNullable(projEstablishInfo.getProjSponsorUserName()).orElse(""));
        renderMap.put(BUSINESS_DEPT_MASTER, Optional.ofNullable(projEstablishInfo.getBizDeptLeaderName()).orElse(""));
        renderMap.put(BUSINESS_DEPT_LEADER, Optional.ofNullable(projEstablishInfo.getBizDivisionLeaderName()).orElse(""));
        if (ObjectUtil.isNotEmpty(projEstablishInfo.getRiskControlManagerName())) {
            renderMap.put(RISK_CONTROL_MANAGER, Optional.of(String.join(",", projEstablishInfo.getRiskControlManagerName())));
        } else {
            renderMap.put(RISK_CONTROL_MANAGER, "");
        }
        renderMap.put(PROJ_SOURCE, Optional.ofNullable(projEstablishInfo.getProjSource()).map(ProjSourceType::of).map(pst -> pst.display).orElse(""));
        renderMap.put(PROJ_BACKGROUD, Optional.ofNullable(projEstablishInfo.getProjBackground()).orElse(""));
        renderMap.put(BUSINESS_DEPT, Optional.ofNullable(projEstablishInfo.getBizDeptName()).orElse(""));
        renderMap.put(PROJ_SPONSOR_USER, Optional.ofNullable(projEstablishInfo.getProjSponsorUserName()).orElse(""));
        renderMap.put(PROJ_COSPONSOR_USER, Optional.ofNullable(projEstablishInfo.getProjCosponsorUserNames()).map(cun -> CollUtil.join(cun, "、")).orElse(""));

        renderMap.put(LEASE_MONTH_COUNT, Optional.ofNullable(leasePriceInfo.getLeaseMonthCount()).map(lmc -> lmc + "月").orElse(""));
        renderMap.put(CREDIT_AMOUNT_LOOP, Optional.ofNullable(leasePriceInfo.getCreditAmountLoop()).map(cal -> Objects.equals(1, cal) ? "是" : "否").orElse(""));
        renderMap.put(LESSEE_INFO_WITH_COMMA, Optional.ofNullable(projEstablishInfo.getLesseeInfo()).map(list -> list.stream().map(ProjEstablishPersonInfo::getClientName).collect(Collectors.joining("、"))).orElse(""));
        renderMap.put(LEASE_TYPES_WITH_COMMA, Optional.ofNullable(projEstablishInfo.getLeaseTypes()).map(list -> list.stream().map(l -> Optional.ofNullable(LeaseType.of(l)).map(lt -> lt.display).orElse(l)).collect(Collectors.joining("、"))).orElse(""));
        renderMap.put(FUNDS_PURPOSE, Optional.ofNullable(projEstablishInfo.getFundsPurpose()).orElse(""));
        renderMap.put(EARNEST_MONEY, Optional.ofNullable(leasePriceInfo.getEarnestMoney()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).map(bs -> bs + "万元").orElse(""));
        renderMap.put(CONSULTING_FEE, Optional.ofNullable(leasePriceInfo.getConsultingFee()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).map(bs -> bs + "万元").orElse(""));
        renderMap.put(NOMINAL_PRICE, Optional.ofNullable(leasePriceInfo.getNominalPrice()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).map(bs -> bs + "万元").orElse(""));
        renderMap.put(RATE_TYPE, Optional.ofNullable(leasePriceInfo.getRateType()).map(RateType::of).map(rte -> rte.display).orElse(""));
        renderMap.put(LEASE_RATE_PERCENT, Optional.ofNullable(leasePriceInfo.getLeaseRatePercent()).map(Util::mithrasInteger2BigDecimal).map(BigDecimal::toPlainString).map(bs -> bs + "%").orElse(""));
        renderMap.put(REPAY_TIMES_TOTAL, Optional.ofNullable(leasePriceInfo.getRepayTimesTotal()).map(String::valueOf).orElse(""));
        Integer n = this.calculateN(leasePriceInfo.getRepayRate(), leasePriceInfo.getLeaseMonthCount(), leasePriceInfo.getRepayTimesTotal());
        renderMap.put(REPAY_TIMES_YEARLY, Optional.ofNullable(n).map(s -> "T+" + s).orElse("T+_"));
        renderMap.put(GUARANTEE_INFO_WITH_ORDER, genGuaranteeInfoWithOrder(projEstablishInfo.getGuaranteeInfo()));
        renderMap.put(RENTAL_CALC_TYPE, Optional.ofNullable(leasePriceInfo.getRentalCalcType()).map(RepayCalcType::find).map(RepayCalcType::display).orElse(""));
        renderMap.put(PLEDGOR_MORTGAGE, genPledgorMortgage(projEstablishInfo.getMortgagorInfo(), projEstablishInfo.getPledgorInfo()));
        renderMap.put(IRR_PERCENT, Optional.ofNullable(leasePriceInfo.getIrrPercent()).map(Util::mithrasInteger2BigDecimal).map(BigDecimal::toPlainString).map(irr -> irr + "%").orElse(""));
        renderMap.put(DOWN_PAYMENT, Optional.ofNullable(leasePriceInfo.getDownPayment()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).map(bs -> bs + "万元").orElse(""));

        // 产品说这部分数据挪到客户分析了 但是以防万一先不删
//        List<Map<String, Object>> publicInformationListSection = new ArrayList<>();
//        renderMap.put(PUBLIC_INFORMATION_LIST_SECTION, publicInformationListSection);
//        int piIndex = 0;
//        for (Long corpId : allCorpIdList) {
//            Map<String, Object> piMap = new HashMap<>();
//            piMap.put(PI_CILENT_NAME, String.format("%s）%s", ++piIndex, personMap.get(corpId).getClientName()));
//            if (CollectionUtils.isEmpty(piLawSuitDataMap.get(corpId))) {
//                piMap.put(PI_LAW_SUIT_TABLE, Tables.of().create());
//                piMap.put(PI_LAW_SUIT_TEXT, "无涉诉信息。");
//            } else {
//                piMap.put(PI_LAW_SUIT_TABLE, renderLawSuitTable(piLawSuitDataMap.get(corpId)));
//                piMap.put(PI_LAW_SUIT_TEXT, "");
//            }
//            if (CollectionUtils.isEmpty(piZhixingDataMap.get(corpId))) {
//                piMap.put(PI_ZHIXING_TABLE, Tables.of().create());
//                piMap.put(PI_ZHIXING_TEXT, "无被执行信息。");
//            } else {
//                piMap.put(PI_ZHIXING_TABLE, renderZhixingTable(piZhixingDataMap.get(corpId)));
//                piMap.put(PI_ZHIXING_TEXT, "");
//            }
//            if (CollectionUtils.isEmpty(piZhongdengDataMap.get(corpId))) {
//                piMap.put(PI_ZHONGDENG_TABLE, Tables.of().create());
//                piMap.put(PI_ZHONGDENG_TEXT, "无登记信息。");
//            } else {
//                piMap.put(PI_ZHONGDENG_TABLE, renderZhongdengTable(piZhongdengDataMap.get(corpId)));
//                piMap.put(PI_ZHONGDENG_TEXT, "");
//            }
//            publicInformationListSection.add(piMap);
//        }
//
//        renderMap.put(KEY_MANAGER_TABLE, renderKeyManagerTable());

        List<Map<String, Object>> clientAnalyzeListSection = new ArrayList<>();
        renderMap.put(CLIENT_ANALYZE_LIST_SECTION, clientAnalyzeListSection);
        int lesseeStartIndex = 0,
                guaranteeStartIndex = projEstablishInfo.getLesseeInfo().size(),
                mortgagorStartIndex = guaranteeStartIndex + projEstablishInfo.getGuaranteeInfo().size(),
                pledgorStartIndex = mortgagorStartIndex + projEstablishInfo.getMortgagorInfo().size();
        List<ProjEstablishPersonInfo> allNeedAnalyzePersonList = Stream.of(projEstablishInfo.getLesseeInfo(), projEstablishInfo.getGuaranteeInfo(), projEstablishInfo.getMortgagorInfo(), projEstablishInfo.getPledgorInfo())
                .flatMap(Collection::stream).collect(Collectors.toList());
        for (int i = 0; i < allNeedAnalyzePersonList.size(); i++) {
            Map<String, Object> caMap = new HashMap<>();
            clientAnalyzeListSection.add(caMap);
            ProjEstablishPersonInfo person = allNeedAnalyzePersonList.get(i);
            caMap.put(CA_CLIENT_ANALYZE_TITLE, String.format("%s、%s%s（%s）分析：%s",
                    NumberChineseFormatter.formatThousand(++level1TitleIndex, false),
                    genClientAnalyzePersonType(i, lesseeStartIndex, guaranteeStartIndex, mortgagorStartIndex, pledgorStartIndex),
                    NumberChineseFormatter.formatThousand(genClientAnalyzePersonIndex(i, lesseeStartIndex, guaranteeStartIndex, mortgagorStartIndex, pledgorStartIndex), false),
                    ClientType.CORPORATION.name().equals(person.getClientType()) ? "企业" : "自然人",
                    person.getClientName()));

            if (ClientType.CORPORATION.name().equals(person.getClientType())) {
                Map<String, Object> corpMap = new HashMap<>();
                caMap.put(CA_CORP_SECTION, corpMap);
                caMap.put(CA_NORMAL_SECTION, false);

                CorpCommerceInfoDetailRSP corpCommerce = corpCommerceMap.get(person.getClientId());
                corpMap.put(CA_C_TITLE, String.format("%s%s名称",
                        genClientAnalyzePersonType(i, lesseeStartIndex, guaranteeStartIndex, mortgagorStartIndex, pledgorStartIndex),
                        NumberChineseFormatter.formatThousand(genClientAnalyzePersonIndex(i, lesseeStartIndex, guaranteeStartIndex, mortgagorStartIndex, pledgorStartIndex), false)));

                corpMap.put(CA_C_CLIENT_NAME, person.getClientName());
                corpMap.put(CA_C_INDUSTRY_TYPE, Optional.ofNullable(corpCommerce.getIndustryTypeName()).orElse(""));
                corpMap.put(CA_C_ORG_SCALE, Optional.ofNullable(corpCommerce.getOrgScale()).map(OrgScaleType::of).map(ost -> ost.display).orElse(""));
                corpMap.put(CA_C_ESTABLISH_DATE, Optional.ofNullable(corpCommerce.getEstablishDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
                corpMap.put(CA_C_BIZ_LICENSE_CODE, Optional.ofNullable(corpCommerce.getUscCode()).orElse(""));
                corpMap.put(CA_C_REGISTER_CAPITAL, Optional.ofNullable(corpCommerce.getRegisterCapital()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).map(bs -> bs + "万元").orElse(""));
                corpMap.put(CA_C_REAL_CAPITAL, Optional.ofNullable(corpCommerce.getRealCapital()).map(Long::valueOf).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).map(bs -> bs + "万元").orElse(""));
                corpMap.put(CA_C_REGISTER_ADDRESS, genFullAddress(corpAddressMap.get(person.getClientId()).stream().filter(a -> CorpAddressType.REGISTRY_ADDRESS.name().equals(a.getAddressType())).findFirst().orElse(null)));
                corpMap.put(CA_C_WORK_ADDRESS, genFullAddress(corpAddressMap.get(person.getClientId()).stream().filter(a -> CorpAddressType.WORK_ADDRESS.name().equals(a.getAddressType())).findFirst().orElse(null)));
                corpMap.put(CA_C_BUSINESS_SCOPE, Optional.ofNullable(corpCommerce.getBizScope()).orElse(""));
                corpMap.put(CA_C_KEY_MANAGER_TABLE, renderKeyManagerTable(corpContactMap.get(person.getClientId())));
                corpMap.put(CA_C_SHAREHOLDER_TABLE, renderShareholderTable(corpShareholderMap.get(person.getClientId())));
                corpMap.put(CA_C_RELATED_ENTERPRISE_TABLE, renderRelatedEnterpriseTable(corpRelatedEnterpriseMap.get(person.getClientId()), dictCacheMap));
                corpMap.put(CA_C_MORTGAGE_TABLE, renderMortgateTable(piMortgateDataMap.get(person.getClientId())));
                corpMap.put(CA_C_EQUITY_TABLE, renderEquityTable(piEquityDataMap.get(person.getClientId())));
                corpMap.put(CA_C_PUNISHMENT_TABLE, renderPunishmentTable(piPunishmentDataMap.get(person.getClientId())));
                corpMap.put(CA_C_ENVIRONMENT_PENALTY_TABLE, renderEnvironmentPenaltyTable(piEnvironmentPenaltyDataMap.get(person.getClientId())));
                corpMap.put(CA_C_ABNORMAL_TABLE, renderAbnormalTable(piAbnormalDataMap.get(person.getClientId())));
                corpMap.put(CA_C_JUDICIAL_TABLE, renderJudicialTable(piJudicialDataMap.get(person.getClientId())));
                corpMap.put(CA_C_CONSUMPTION_RESTRICTION_TABLE, renderConsumptionRestrictionTable(piConsumptionRestrictionDataMap.get(person.getClientId())));
                corpMap.put(CA_C_ZHIXING_TABLE, renderZhixingTable(piZhixingDataMap.get(person.getClientId())));
                corpMap.put(CA_C_DISHONEST_TABLE, renderDishonestTable(piDishonestDataMap.get(person.getClientId())));
                corpMap.put(CA_C_ZHONGDENG_TABLE, renderZhongdengTable(piZhongdengDataMap.get(person.getClientId())));
            } else if (ClientType.NORMAL.name().equals(person.getClientType())) {
                Map<String, Object> normalMap = new HashMap<>();
                caMap.put(CA_CORP_SECTION, false);
                caMap.put(CA_NORMAL_SECTION, normalMap);

                NormalBaseInfoDetailRSP normalBase = normalBaseMap.get(person.getClientId());
                normalMap.put(CA_N_CLIENT_NAME, person.getClientName());
                normalMap.put(CA_N_CERT_NUMBER, Optional.ofNullable(normalBase.getCertNumber()).orElse(""));
                normalMap.put(CA_N_GENDER, Optional.ofNullable(normalBase.getGender()).map(g -> Optional.ofNullable(GenderType.of(g)).map(gt -> gt.display).orElse(g)).orElse(""));
                normalMap.put(CA_N_MARRIAGE_TYPE, Optional.ofNullable(normalBase.getMarriageType()).map(g -> Optional.ofNullable(MarriageType.of(g)).map(gt -> gt.display).orElse(g)).orElse(""));
            }
        }

        // 财报 查询 渲染
        sw.start("subject");
        List<Map<String, Object>> subjectListSection = new ArrayList<>();
        renderMap.put(SUBJECT_LIST_SECTION, subjectListSection);
        // 两倍法人列表 用于处理合并、单个报表
        List<Long> allNeedSubjectCorpIdList = Stream.of(allCorpIdList, allCorpIdList).flatMap(Collection::stream).collect(Collectors.toList());

        List<CompletableFuture> subjectFutureList = new ArrayList<>();
        for (int i = 0; i < allNeedSubjectCorpIdList.size(); i++) {
            Map<String, Object> sMap = new HashMap<>();
            subjectListSection.add(sMap);
            int curIndex = i;
            CompletableFuture subjectFuture = CompletableFuture.runAsync(() -> {
                Long corpId = allNeedSubjectCorpIdList.get(curIndex);
                SubjectReportType curReportType = curIndex < allCorpIdList.size() ? SubjectReportType.MERGED : SubjectReportType.LOCAL;
                CorpCommerceInfoDetailRSP corpCommerce = corpCommerceMap.get(corpId);
                sMap.put(S_TITLE, String.format("%s  %s报表", personMap.get(corpId).getClientName(), SubjectReportType.MERGED.equals(curReportType) ? "合并" : "单体"));
                List<Map<String, Object>> subjectTableListSection = new ArrayList<>();
                sMap.put(S_SUBJECT_TABLE_LIST_SECTION, subjectTableListSection);
                if (!"1".equals(corpCommerce.getOrgType())) {
                    for (GovernmentSubjectItemType govItemType : GovernmentSubjectItemType.values()) {
                        List<CorpSubjectItemListRSP> subjectItemList = subjectItemSearch(corpId, curReportType.name(), govItemType.name());
                        if (CollectionUtils.isNotEmpty(subjectItemList)) {
                            Map<String, Object> sstlMap = new HashMap<>();
                            sstlMap.put(S_ST_TABLE_NAME, String.format("%s%s", SubjectReportType.MERGED.equals(curReportType) ? "合并" : "", govItemType.sheetName));
                            sstlMap.put(S_ST_SUBJECT_TABLE, renderSubjectTable(subjectItemList));
                            subjectTableListSection.add(sstlMap);
                        }
                    }
                } else {
                    for (SubjectItemType subjectItemType : SubjectItemType.values()) {
                        List<CorpSubjectItemListRSP> subjectItemList = subjectItemSearch(corpId, curReportType.name(), subjectItemType.name());
                        if (CollectionUtils.isNotEmpty(subjectItemList)) {
                            Map<String, Object> sstlMap = new HashMap<>();
                            sstlMap.put(S_ST_TABLE_NAME, String.format("%s%s", SubjectReportType.MERGED.equals(curReportType) ? "合并" : "", subjectItemType.sheetName));
                            sstlMap.put(S_ST_SUBJECT_TABLE, renderSubjectTable(subjectItemList));
                            subjectTableListSection.add(sstlMap);
                        }
                    }
                }
            }, reportThreadPool);
            subjectFutureList.add(subjectFuture);
        }
        CompletableFuture.allOf(ArrayUtil.toArray(subjectFutureList, CompletableFuture.class)).get();
        // 把没有报表的请空 把序号重新整理一下
        subjectListSection.removeIf(m -> CollectionUtils.isEmpty((List) m.get(S_SUBJECT_TABLE_LIST_SECTION)));
        for (int i = 0; i < subjectListSection.size(); i++) {
            Map<String, Object> sMap = subjectListSection.get(i);
            sMap.put(S_TITLE, String.format("附件%s：%s", i + 1, sMap.get(S_TITLE)));
        }
        sw.stop();

        log.info("计算方法执行耗时:{}", sw.prettyPrint());
        // 渲染
        try (InputStream templateIs = ProjEstablishReportRender.class.getResourceAsStream(TEMPLATE_FILE_PATH)) {
            XWPFTemplate template = XWPFTemplate.compile(templateIs).render(renderMap);
            template.write(outputStream);
        }
        return projEstablishInfo.getProjName() + "-立项报告" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    // ------------------------------------- 法人公开信息查询 开始 --------------------------------------

    public Map<Long, List<TycMortgageInfoRSP>> mortgageSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.mortgageInfoList(req).getData().getList();
        }));
    }

    public Map<Long, List<TycEquityInfoRSP>> equitySearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.equityInfo(req).getData().getList();
        }));
    }

    public Map<Long, List<TycPunishmentInfoRSP>> punishmentSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.punishmentInfoList(req).getData().getList();
        }));
    }

    public Map<Long, List<TycAbnormalRSP>> abnormalSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.abnormalList(req).getData().getList();
        }));
    }

    public Map<Long, List<TycJudicialRSP>> judicialSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.judicialList(req).getData().getList();
        }));
    }

    public Map<Long, List<TycLawSuitRSP>> lawSuitSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.lawSuitList(req).getData().getList();
        }));
    }

    public Map<Long, List<TycConsumptionRestrictionRSP>> consumptionRestrictionSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.consumptionRestrictionList(req).getData().getList();
        }));
    }

    public Map<Long, List<TycZhixingInfoRSP>> zhixingSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.zhixingInfoList(req).getData().getList();
        }));
    }

    public Map<Long, List<TycDishonestRSP>> dishonestSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return tycController.dishonestList(req).getData().getList();
        }));
    }

    public Map<Long, List<ZhongdengInfoRSP>> zhongdengSearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return zhongdengInfoController.list(req).getData().getList();
        }));
    }

    public Map<Long, List<EnvironmentPenaltyRSP>> environmentPenaltySearch(List<Long> clientIdList) {
        return clientIdList.stream().distinct().collect(Collectors.toMap(id -> id, id -> {
            ExternalPageREQ req = new ExternalPageREQ();
            req.setClientId(id);
            req.setPageSize(Integer.MAX_VALUE);
            return environmentPenaltyController.list(req).getData().getList();
        }));
    }

    // ------------------------------------- 法人公开信息查询 结束---------------------------------------

    // ------------------------------------- 表格数据渲染 开始---------------------------------------

    public TableRenderData renderKeyManagerTable() {
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("姓名", "职务", "主要履历").rowAtleastHeight(0.73D).center().create());
        tableDataList.add(Rows.of("", "", "").rowAtleastHeight(2.65D).center().create());
        tableDataList.add(Rows.of("", "", "").rowAtleastHeight(2.65D).center().create());
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).width(14.63D, new double[]{2.04D, 3.34D, 14.63D - 2.04D - 3.34D}).create();
    }

    public TableRenderData renderMortgateTable(List<TycMortgageInfoRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "登记日期", "登记编号", "抵押权人", "所有权或使用归属权", "被担保债权类型", "被担保债权数额", "债务人履行债务的期限", "登记机关").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycMortgageInfoRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getRegDate()).orElse(""),
                            Optional.ofNullable(info.getRegNum()).orElse(""),
                            Optional.ofNullable(info.getPeopleInfo()).orElse(""),
                            Optional.ofNullable(info.getBelongTo()).orElse(""),
                            Optional.ofNullable(info.getType()).orElse(""),
                            Optional.ofNullable(info.getAmount()).orElse(""),
                            Optional.ofNullable(info.getTerm()).orElse(""),
                            Optional.ofNullable(info.getRegDepartment()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderEquityTable(List<TycEquityInfoRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "股权出质设立登记日期", "登记编号", "出质人", "出质股权标的企业", "出质股权数额", "状态").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycEquityInfoRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getRegDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getRegNumber()).orElse(""),
                            Optional.ofNullable(info.getPledgor()).orElse(""),
                            Optional.ofNullable(info.getTargetCompany()).orElse(""),
                            Optional.ofNullable(info.getEquityAmount()).orElse(""),
                            Optional.ofNullable(info.getState()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderPunishmentTable(List<TycPunishmentInfoRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "处罚日期", "决定文书号", "处罚事由/违法行为类型", "处罚结果/内容", "处罚单位", "数据来源").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycPunishmentInfoRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getDecisionDate()).orElse(""),
                            Optional.ofNullable(info.getPunishNumber()).orElse(""),
                            Optional.ofNullable(info.getReason()).orElse(""),
                            Optional.ofNullable(info.getContent()).orElse(""),
                            Optional.ofNullable(info.getDepartmentName()).orElse(""),
                            Optional.ofNullable(info.getSource()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderEnvironmentPenaltyTable(List<EnvironmentPenaltyRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "处罚日期", "决定文书号", "处罚事由", "处罚结果", "处罚金额(万元)", "处罚单位", "数据来源", "执行情况").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            EnvironmentPenaltyRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getPenaltyTime()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getPunishNumber()).orElse(""),
                            Optional.ofNullable(info.getReason()).orElse(""),
                            Optional.ofNullable(info.getResult()).orElse(""),
                            Optional.ofNullable(info.getAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getDepartmentName()).orElse(""),
                            Optional.ofNullable(info.getSource()).orElse(""),
                            Optional.ofNullable(info.getInfo()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderAbnormalTable(List<TycAbnormalRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "列入日期", "列入原因", "作出决定机关", "移出日期", "移出原因").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycAbnormalRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getPutDate()).orElse(""),
                            Optional.ofNullable(info.getPutReason()).orElse(""),
                            Optional.ofNullable(info.getPutDepartment()).orElse(""),
                            Optional.ofNullable(info.getRemoveDate()).orElse(""),
                            Optional.ofNullable(info.getRemoveReason()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderJudicialTable(List<TycJudicialRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "公示日期", "执行通知文书号", "被执行人", "股权被执行的企业", "股权数额(万元)", "执行法院", "类型", "状态").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycJudicialRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getPublicityDate()).orElse(""),
                            Optional.ofNullable(info.getExecuteNoticeNum()).orElse(""),
                            Optional.ofNullable(info.getExecutedPerson()).orElse(""),
                            Optional.ofNullable(info.getStockExecutedCompany()).orElse(""),
                            Optional.ofNullable(info.getEquityAmount()).orElse(""),
                            Optional.ofNullable(info.getExecutiveCourt()).orElse(""),
                            Optional.ofNullable(info.getTypeState()).orElse(""),
                            Optional.ofNullable(info.getStatus()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderConsumptionRestrictionTable(List<TycConsumptionRestrictionRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "立案日期", "案号", "限制消费对象", "关联限制消费对象", "申请人信息", "发布日期", "详情").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycConsumptionRestrictionRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getPublishDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getCaseCode()).orElse(""),
                            Optional.ofNullable(info.getXname()).orElse(""),
                            Optional.ofNullable(info.getQyinfoAlias()).orElse(""),
                            Optional.ofNullable(info.getApplicant()).orElse(""),
                            Optional.ofNullable(info.getPublishDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getDetailUrl()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderZhixingTable(List<TycZhixingInfoRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "立案日期", "案号", "执行标的(元)", "执行法院").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycZhixingInfoRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getCaseCreateTime()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getCaseCode()).orElse(""),
                            Optional.ofNullable(info.getExecMoney()).orElse(""),
                            Optional.ofNullable(info.getExecCourtName()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderDishonestTable(List<TycDishonestRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "立案日期", "案号", "执行依据文号", "执行法院", "失信行为", "履行情况", "发布日期").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycDishonestRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getRegDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getCaseCode()).orElse(""),
                            Optional.ofNullable(info.getGistId()).orElse(""),
                            Optional.ofNullable(info.getCourtName()).orElse(""),
                            Optional.ofNullable(info.getDisruptTypeName()).orElse(""),
                            Optional.ofNullable(info.getPerformance()).orElse(""),
                            Optional.ofNullable(info.getPublishDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderZhongdengTable(List<ZhongdengInfoRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "交易业务类型", "授信机构", "金额（亿元）", "登记日期", "登记到期日", "期限（年）").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            ZhongdengInfoRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getTradeBusinessType()).orElse(""),
                            Optional.ofNullable(info.getCreditOrg()).orElse(""),
                            Optional.ofNullable(info.getAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getRegDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getRegExpireDate()).map(d -> LocalDateTimeUtil.format(d, "yyyy-MM-dd")).orElse(""),
                            Optional.ofNullable(info.getTerm()).map(String::valueOf).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderLawSuitTable(List<TycLawSuitRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "案件名称", "案由", "在本案中身份", "裁判结果", "结果标签", "案件金额(元)", "详情").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            TycLawSuitRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getTitle()).orElse(""),
                            Optional.ofNullable(info.getCaseReason()).orElse(""),
                            Optional.ofNullable(info.getIdentity()).orElse(""),
                            Optional.ofNullable(info.getJudgeResult()).orElse(""),
                            Optional.ofNullable(info.getResultTag()).orElse(""),
                            Optional.ofNullable(info.getCaseMoney()).orElse(""),
                            Optional.ofNullable(info.getDetailUrl()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderShareholderTable(List<CorpShareholderInfoListRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "股东类型", "股东名称", "认缴金额（万元）", "实缴金额(万)", "认缴出资方式", "认缴出资占比(%)", "是否实际控制人").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            CorpShareholderInfoListRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getShareholderType()).map(st -> Optional.ofNullable(ShareholderType.of(st)).map(ste -> ste.display).orElse(st)).orElse(""),
                            Optional.ofNullable(info.getShareholderName()).orElse(""),
                            Optional.ofNullable(info.getPaidTotal()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000L), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getActualPaidTotal()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000L), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getCapitalWay()).orElse(""),
                            Optional.ofNullable(info.getCapitalPercent()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getRealController()).map(t -> t ? "是" : "否").orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderRelatedEnterpriseTable(List<CorpRelatedEnterpriseListRSP> infoList, Map<String, Map<String, String>> dictCacheMap) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "关联企业名称", "成立年份", "关联关系", "注册资本(万元)", "持股比例(%)", "存续状态", "投资金额(万元)", "行业").center().create());
        for (int i = 0; i < infoList.size(); i++) {
            CorpRelatedEnterpriseListRSP info = infoList.get(i);
            tableDataList.add(Rows.of(String.valueOf(i + 1),
                            Optional.ofNullable(info.getEnterpriseName()).orElse(""),
                            Optional.ofNullable(info.getEstablishDate()).map(LocalDateTimeUtil::formatNormal).orElse(""),
                            Optional.ofNullable(info.getRelationship()).map(r -> Optional.ofNullable(RelationshipType.of(r)).map(rt -> rt.display).orElse(r)).orElse(""),
                            Optional.ofNullable(info.getRegisterCapital()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000L), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getShareholdingRatio()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getContinuousStatus()).map(c -> dictLabel2Display(dictCacheMap, Const.ENUM_CONTINUOUS, c)).orElse(""),
                            Optional.ofNullable(info.getInvestAmount()).map(Util::mithrasLong2BigDecimal).map(b -> b.divide(new BigDecimal(10000L), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse(""),
                            Optional.ofNullable(info.getIndustryTypeName()).orElse(""))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }

    public TableRenderData renderKeyManagerTable(List<CorpContactInfoListRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return renderKeyManagerTable();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("姓名", "职务", "主要履历").rowAtleastHeight(0.73D).center().create());
        for (int i = 0; i < infoList.size(); i++) {
            CorpContactInfoListRSP info = infoList.get(i);
            tableDataList.add(Rows.of(
                            Optional.ofNullable(info.getName()).orElse(""),
                            Optional.ofNullable(info.getPosition()).orElse(""),
                            "")
                    .center().rowAtleastHeight(2.65D).create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).width(14.63D, new double[]{2.04D, 3.34D, 14.63D - 2.04D - 3.34D}).create();
    }

    public TableRenderData renderSubjectTable(List<CorpSubjectItemListRSP> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return Tables.of().create();
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.add("项目");
        for (CorpSubjectItemListRSP item : infoList) {
            if (SubjectQuarterType.TWELFTH.value.equals(item.getQuarter())) {
                titleList.add(item.getYear() + "年");
            } else {
                titleList.add(item.getYear() + "年" + SubjectQuarterType.getByValue(item.getQuarter()).display);
            }
        }
        tableDataList.add(Rows.of(ArrayUtil.toArray(titleList, String.class)).center().create());
        for (int i = 0; i < infoList.get(0).getItemList().size(); i++) {
            List<String> dataList = new ArrayList<>();
            for (int j = 0; j < infoList.size(); j++) {
                CorpSubjectItemListRSP info = infoList.get(j);
                if (j == 0) {
                    dataList.add(info.getItemList().get(i).getSubjectName());
                }
                dataList.add(i >= info.getItemList().size() ? ""
                        : Optional.ofNullable(info.getItemList().get(i).getSubjectValueStr()).orElse(""));
            }
            tableDataList.add(Rows.of(ArrayUtil.toArray(dataList, String.class))
                    .center().create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).create();
    }


    // ------------------------------------- 表格数据渲染 结束---------------------------------------

    // ------------------------------------- 财报渲染 开始---------------------------------------

    public List<CorpSubjectItemListRSP> subjectItemSearch(Long corpId, String reportType, String subjectType) {
        CorpSubjectItemListREQ corpSubjectItemListREQ = CorpSubjectItemListREQ.builder()
                .clientId(corpId)
                .subjectType(subjectType)
                .quarter(12)
                .latest(true)
                .reportType(reportType)
                .displayDimensions(Collections.singletonList(SubjectItemDisplayDimension.BASE.name()))
                .unit(1L)
                .yearTo(LocalDateTime.now().getYear())
                .yearFrom(LocalDateTime.now().getYear() - 3)
                .build();
        return corpSubjectItemController.list(corpSubjectItemListREQ).getData();
    }

    // ------------------------------------- 财报渲染 结束---------------------------------------

    public String genLesseeInfoWithOrder(List<ProjEstablishPersonInfo> lesseeInfo) {
        if (lesseeInfo.size() == 1) {
            return lesseeInfo.get(0).getClientName();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lesseeInfo.size(); i++) {
            sb.append(String.format("%s）%s\n", i + 1, lesseeInfo.get(i).getClientName()));
        }
        return sb.toString();
    }

    public String genGuaranteeInfoWithOrder(List<ProjEstablishPersonInfo> guaranteeInfo) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guaranteeInfo.size(); i++) {
            sb.append(String.format("%s.%s；\n", i + 1, guaranteeInfo.get(i).getClientName()));
        }
        return sb.toString();
    }

    public String genPledgorMortgage(List<ProjEstablishPersonInfo> mortgagorInfo, List<ProjEstablishPersonInfo> pledgorInfo) {
        if (CollectionUtils.isEmpty(mortgagorInfo) && CollectionUtils.isEmpty(pledgorInfo)) {
            return "无";
        }
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(mortgagorInfo)) {
            sb.append("抵押人：\n");
            for (int i = 0; i < mortgagorInfo.size(); i++) {
                sb.append(String.format("%s.%s；\n", i + 1, mortgagorInfo.get(i).getClientName()));
            }
        }
        if (CollectionUtils.isNotEmpty(pledgorInfo)) {
            sb.append("质押人：\n");
            for (int i = 0; i < pledgorInfo.size(); i++) {
                sb.append(String.format("%s.%s；\n", i + 1, pledgorInfo.get(i).getClientName()));
            }
        }
        return sb.toString();
    }

    public String genFullAddress(CorpAddressInfoListRSP addressInfoListRSP) {
        if (Objects.isNull(addressInfoListRSP)) {
            return "";
        }
        return new StringBuilder()
                .append(Optional.ofNullable(addressInfoListRSP.getProvinceName()).orElse(""))
                .append(Optional.ofNullable(addressInfoListRSP.getCityName()).orElse(""))
                .append(Optional.ofNullable(addressInfoListRSP.getDistrictName()).orElse(""))
                .append(Optional.ofNullable(addressInfoListRSP.getDetail()).orElse(""))
                .toString();
    }

    public String genClientAnalyzePersonType(int curIndex, int lesseeStartIndex, int guaranteeStartIndex, int mortgagorStartIndex, int pledgorStartIndex) {
        if (curIndex >= lesseeStartIndex && curIndex < guaranteeStartIndex) {
            return "承租人";
        } else if (curIndex >= guaranteeStartIndex && curIndex < mortgagorStartIndex) {
            return "担保人";
        } else if (curIndex >= mortgagorStartIndex && curIndex < pledgorStartIndex) {
            return "抵押人";
        } else {
            return "质押人";
        }
    }

    public int genClientAnalyzePersonIndex(int curIndex, int lesseeStartIndex, int guaranteeStartIndex, int mortgagorStartIndex, int pledgorStartIndex) {
        if (curIndex >= lesseeStartIndex && curIndex < guaranteeStartIndex) {
            return curIndex - lesseeStartIndex + 1;
        } else if (curIndex >= guaranteeStartIndex && curIndex < mortgagorStartIndex) {
            return curIndex - guaranteeStartIndex + 1;
        } else if (curIndex >= mortgagorStartIndex && curIndex < pledgorStartIndex) {
            return curIndex - mortgagorStartIndex + 1;
        } else {
            return curIndex - pledgorStartIndex + 1;
        }
    }

    public String dictLabel2Display(Map<String, Map<String, String>> cacheMap, String dictKey, String code) {
        Map<String, String> singleDictCacheMap = cacheMap.computeIfAbsent(dictKey, k -> new HashMap<>());
        if (singleDictCacheMap.containsKey(code)) {
            return singleDictCacheMap.get(code);
        }
        String display = dictService.label2DisplayWithDefault(dictKey, code);
        singleDictCacheMap.put(code, display);
        return display;
    }

}
