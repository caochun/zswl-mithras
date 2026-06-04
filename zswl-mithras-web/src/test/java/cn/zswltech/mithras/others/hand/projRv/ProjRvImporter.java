package cn.zswltech.mithras.others.hand.projRv;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.dao.UserOrgRoleDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishLeasePriceMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.impl.ProjReviewVersionServiceImpl;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projreview.ProjReviewCashFlowPlanService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.ListUtil.toList;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.service.others.Util.toMithrasUnit;

/**
 * @author junke
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjRvImporter {

    //    private static final String prefix = "（汉得导入勿用）";
    private static final String prefix = "";

    @Resource
    private ProjReviewBaseInfoMapper baseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoMapper etbBaseInfoMapper;
    @Resource
    private ProjEstablishLeasePriceMapper etbPriceMapper;
    @Resource
    private ProjReviewLeasePriceMapper leasePriceMapper;
    @Resource
    private ProjReviewCashFlowPlanService cashFlowPlanService;
    @Resource
    private UserService userService;
    @Resource
    private UserOrgRoleDOMapper userOrgRoleDOMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjReviewVersionServiceImpl projReviewVersionService;
    @Resource
    private TransactionTemplate transactionTemplate;

    private List<ProjRv> rvList = new ArrayList<>();
    private Set<String> unknownClientSet = new HashSet<>();
    private static final Path filePath = Paths.get(System.getProperty("user.home"), "DeskTop", "评审.xls");

    public Client getOrCreateClient(String name) {
        String clientName = prefix + name.replaceAll("\\s", "");
        Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, clientName));
        if (null == client) {
            unknownClientSet.add(clientName);
            log.info("get client by name is null.{}", name);
            throw new RuntimeException("NULL");
        }
        return client;
    }

    public UserDO getOrCreateProjManager(String name) {
        if (StrUtil.isBlank(name)) {
            return new UserDO();
        }
        UserQuery q = new UserQuery();
        q.setUserName(name);
        Response<Map<String, Object>> rsp = userService.queryUser(q);
        List<UserVO> list = (List<UserVO>) rsp.getData().get("userList");
        if (list.isEmpty()) {
            UserDO administrator = getOrCreateProjManager("administrator");
            if (null == administrator) {
                log.info("get user by name is null. {}", name);
                throw new RuntimeException("NULL");
            }
            return administrator;
        }
        return list.get(0);
    }

    public Long getOrgIdByUserId(Long id) {
        List<Long> list = userOrgRoleDOMapper.selectOrgIdsByUserId(id);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        log.info("get org by user is null. {}", id);
        throw new RuntimeException("NULL");
    }

    @BeforeEach
    public void before() {
        ExcelReader reader = new ExcelReader(filePath.toFile(), 0);
        reader.setIgnoreEmptyRow(true);
        for (int i = 1; i < reader.getRowCount(); i++) {
            List<Object> cellValues = reader.readRow(i);
            ProjRv projRv = JSONUtil.toBean(cellValues.get(cellValues.size() - 1).toString(), ProjRv.class);
            rvList.add(projRv);
        }
    }

    @Test
    @Transactional(rollbackFor = Throwable.class)
    public void upload() {

    }

    @Test
    @Transactional(rollbackFor = Throwable.class)
    public void checkClient() {
        for (ProjRv projRv : rvList) {
            for (ProjRv.客户信息 j : projRv.get客户信息List()) {
                try {
                    getOrCreateClient(j.get客户名称());
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
        log.info("unknownClientSet：{}", toJsonStr(unknownClientSet));
    }

    /*@Test
    @Rollback(false)
    public void fillPlanStartDate() {
        for (ProjRv rv : rvList) {
            String rvCode = rv.get列表信息().get项目编号();
            String etbCode = rv.get基本信息().get立项编号();
            ProjReviewBaseInfo toUpdate = new ProjReviewBaseInfo();
            toUpdate.setProjCode(etbCode);
            baseInfoMapper.update(toUpdate, Wrappers.<ProjReviewBaseInfo>lambdaUpdate().eq(ProjReviewBaseInfo::getProjCode, rvCode));
        }
    }*/


    @Test
//    @Transactional(rollbackFor = Throwable.class)
    @Rollback(false)
    public void ipt() {
        List<String> doneList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        List<String> notExistEtbList = new ArrayList<>();
        //

        for (ProjRv projRv : rvList) {
            transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus transactionStatus) {
                    try {
                        projRv.get列表信息().set项目名称(prefix + projRv.get列表信息().get项目名称());
                        projRv.get基本信息().set项目名称(prefix + projRv.get基本信息().get项目名称());
                        iptBaseInfo(projRv);
                        doneList.add(projRv.get列表信息().get项目编号());
                    } catch (Exception e) {
                        if (null != e.getMessage() && e.getMessage().contains("对应的立项不存在")) {
                            notExistEtbList.add(projRv.get基本信息().get立项编号());
                        }
                        errorList.add(projRv.get列表信息().get项目编号());
                        log.error("{} ipt error", projRv.get列表信息().get项目编号(), e);
                        transactionStatus.setRollbackOnly();
                    }
                    return null;
                }
            });

        }
        log.info("doneList：{}", toJsonStr(doneList));
        log.info("errorList：{}", toJsonStr(errorList));
        log.info("unknownClientSet：{}", toJsonStr(unknownClientSet));
        log.info("notExistEtbList：{}", toJsonStr(notExistEtbList));

    }

    private void iptBaseInfo(ProjRv rv) {
        ProjReviewBaseInfo existInfo = baseInfoMapper.selectOne(new QueryWrapper<ProjReviewBaseInfo>().eq("proj_code", rv.get列表信息().get项目编号()));
//        ProjEstablishBaseInfo existInfo = baseInfoMapper.selectOne(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getProjCode, etb.get列表信息().get立项编号()));
        if (null != existInfo) {
            log.info("已经导入");
            return;
        }
        String etbCode = rv.get基本信息().get立项编号();
        ProjEstablishBaseInfo etb = etbBaseInfoMapper.selectOne(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getProjCode, etbCode));
        if (etb == null) {
            throw new RuntimeException("对应的立项不存在");
        }
        ProjEstablishLeasePrice etbPrice = etbPriceMapper.selectOne(Wrappers.<ProjEstablishLeasePrice>lambdaQuery().eq(ProjEstablishLeasePrice::getProjEstablishId, etb.getId()));


        ProjReviewBaseInfo baseInfo = new ProjReviewBaseInfo();
        baseInfo.setProjName(rv.get列表信息().get项目名称());
        baseInfo.setProjCode(rv.get列表信息().get项目编号());
        baseInfo.setBizType(ProjectBizType.ZL.name());
        baseInfo.setLeaseTypes(toJsonStr(toList(mapLeaseType(rv))));
        baseInfo.setProjSource(etb.getProjSource());//置空
        baseInfo.setProjectClassify(null);
        baseInfo.setProjBackground(etb.getProjBackground());
        baseInfo.setRiskControlManagerId(null);
        baseInfo.setLegalManagerUserId(null);

        baseInfo.setProjectType(mapProjType(rv));
        baseInfo.setFundsPurpose(rv.get基本信息().get资金用途());
        //承租人
        List<ProjEstablishPersonInfo> 承租人列表 = mapPersonInfo(rv, "承租人");
        if (!承租人列表.isEmpty()) {
            baseInfo.setClientId(承租人列表.get(0).getClientId());
        }
        baseInfo.setLesseeInfo(toJsonStr(承租人列表));
        baseInfo.setGuaranteeInfo(toJsonStr(mapPersonInfo(rv, "担保人")));
        baseInfo.setMortgagorInfo(toJsonStr(mapPersonInfo(rv, "抵押人")));
        baseInfo.setPledgorInfo(toJsonStr(mapPersonInfo(rv, "质押人")));
        //
        UserDO user = getOrCreateProjManager(rv.get列表信息().get项目经理());
        UserDO user2 = getOrCreateProjManager(rv.get基本信息().get业务协办一());
        baseInfo.setProjSponsorUserId(user.getId());
        baseInfo.setProjCosponsorUserIds(toJsonStr(toList(user2.getId())));
        baseInfo.setBizDeptId(getOrgIdByUserId(user.getId()));
        baseInfo.setProjReviewStatus(mapStatus(rv));
        ProjProcessState projProcessState = mapProcessStatus(rv);
        if (null != projProcessState) {
            baseInfo.setProcessStatus(projProcessState);
        }
        baseInfo.setDeclaredAmount(safeNumber(rv.get报价信息().get融资金额_元()));
        baseInfo.setCreateBy(baseInfo.getProjSponsorUserId());
        baseInfoMapper.insert(baseInfo);
        //报价方案
        ProjReviewLeasePrice leasePrice = new ProjReviewLeasePrice();
        leasePrice.setPayType(etbPrice.getPayType());//从立项带入
        ProjRv.现金流信息 xjlxx = rv.get现金流信息List().stream().filter(e -> "首期租金".equals(e.get现金流项目())).findFirst().orElse(null);
        if (xjlxx != null && StrUtil.isNotBlank(xjlxx.get应收金额())) {
            leasePrice.setDownPayment(safeNumber(xjlxx.get应收金额()));
        } else {
            leasePrice.setDownPayment(0L);
        }
        leasePrice.setRepayTimesTotal(safeInt(rv.get报价信息().get租赁期数()));
        leasePrice.setProjectId(baseInfo.getId());
        leasePrice.setApplyCreditAmount(safeNumber(rv.get报价信息().get融资金额_元()));
        leasePrice.setRentalCalcType(mapRentalCalcType(rv));
        leasePrice.setLeaseMonthCount(safeInt(rv.get报价信息().get租赁期限_月()));
        leasePrice.setRateType("固定利率".equals(rv.get报价信息().get利率类型()) ? RateType.FIXED.name() : RateType.FLOAT.name());
        Long l = safeNumber(rv.get报价信息().get租赁利率());
        leasePrice.setLeaseRatePercent(null == l ? null : l.intValue());
        leasePrice.setEarnestMoney(safeNumber(rv.get报价信息().get保证金_元()));
        leasePrice.setConsultingFee(safeNumber(rv.get报价信息().get租赁咨询费_元()));
        leasePrice.setNominalPrice(safeNumber(rv.get报价信息().get名义货价_元()));
        leasePrice.setRepayRate(mapRepayRate(rv));
        Long irr = safeNumber(rv.get报价信息().getIRR());
        leasePrice.setIrrPercent(null == irr ? null : irr.intValue());
        leasePrice.setCreditAmountLoop("循环".equals(rv.get报价信息().get额度类型()) ? 1 : 0);
        leasePrice.setPlannedStartingDate(parsePlanStartDate(rv));
        leasePriceMapper.insert(leasePrice);
//现金流信息
        List<ProjReviewCashFlowPlan> planList = new ArrayList<>();
        for (ProjRv.现金流信息 xj : rv.get现金流信息List()) {
            if (StrUtil.isNotBlank(xj.get本金()) || StrUtil.isNotBlank(xj.get利息())) {
                ProjReviewCashFlowPlan plan = new ProjReviewCashFlowPlan();
                plan.setProjectId(baseInfo.getId());
                plan.setCashFlowDate(LocalDateTimeUtil.parseDate(xj.get应收日期(), "yyyy-MM-dd"));
                plan.setCashFlowPhase(safeInt(xj.get期数()));
                plan.setCashFlowAmount(safeNumber(xj.get应收金额()));
                plan.setPrincipal(safeNumber(xj.get本金()));
                plan.setInterest(safeNumber(xj.get利息()));
                plan.setRemainingPrincipal(safeNumber(xj.get当期剩余本金()));
                planList.add(plan);
            }
        }
        if (!planList.isEmpty()) {
            cashFlowPlanService.saveBatch(planList);
        }
        projReviewVersionService.recordVersion(baseInfo.getId(), VersionTypeEnum.EFFECT, baseInfo.getProjSponsorUserId(), null, VersionTypeConstants.NORMAL);
    }

    private LocalDate parsePlanStartDate(ProjRv rv) {
        String sd = rv.get报价信息().get起租日();
        if (StrUtil.isBlank(sd)) {
            return null;
        }
        return LocalDateTimeUtil.parseDate(rv.get报价信息().get起租日(), "yyyy-MM-dd");
    }

    private ProjProcessState mapProcessStatus(ProjRv rv) {
        String sta = rv.get列表信息().get项目状态();
        ProjProcessState rvStatus = null;
        if ("通过".equals(sta)) {
            rvStatus = ProjProcessState.NEW_APPROVAL_PASS;
        }
        if ("审批退回".equals(sta)) {
            rvStatus = ProjProcessState.NEW_REJECT;
        }

        if ("已创建至合同".equals(sta)) {
            rvStatus = ProjProcessState.NEW_APPROVAL_PASS;
        }
        if ("拒绝".equals(sta)) {
            rvStatus = ProjProcessState.NEW_REJECT;
        }
        return rvStatus;
    }

    private String mapStatus(ProjRv rv) {
        String sta = rv.get列表信息().get项目状态();
        String rvStatus = null;
        if ("通过".equals(sta)) {
            rvStatus = RecordStatus.TAKE_EFFECT.name();
        }
        if ("审批退回".equals(sta)) {
            rvStatus = RecordStatus.CLOSED.name();
        }
        if ("已创建至合同".equals(sta)) {
            rvStatus = RecordStatus.TAKE_EFFECT.name();
        }
        if ("拒绝".equals(sta)) {
            rvStatus = RecordStatus.CLOSED.name();
        }
        return rvStatus;

    }

    private Integer safeInt(String str) {
        if (null == str) {
            return null;
        }
        return Integer.parseInt(str);
    }

    private Long safeNumber(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        return toMithrasUnit(new BigDecimal(str.replace(",", "")));
    }

    private String mapRepayRate(ProjRv rv) {
        String freq = rv.get报价信息().get租金支付频率();
        if ("年".equals(freq)) {
            return RepayRateEnum.YEAR.name();
        }
        if ("月".equals(freq)) {
            return RepayRateEnum.MONTH.name();
        }
        if ("半年".equals(freq)) {
            return RepayRateEnum.HALF_YEAR.name();
        }
        if ("双月".equals(freq)) {
            return RepayRateEnum.DOUBLE_MONTH.name();
        }
        if ("不规则还款".equals(freq)) {
            return RepayRateEnum.LRREGULAR.name();
        }
        if ("季".equals(freq)) {
            return RepayRateEnum.QUARTER.name();
        }
        return null;

    }


    private String mapRentalCalcType(ProjRv rv) {
        String a = rv.get报价信息().get报价方案();
        if (null == a) {
            return null;
        }
        if ("等额本金".equals(a)) {
            return RepayCalcType.DEBJ.name();
        }
        if ("等额本息".equals(a)) {
            return RepayCalcType.DEBX.name();
        }
        if ("固定本金".equals(a)) {
            return RepayCalcType.DEBX.name();
        }
        if ("不等额租金".equals(a)) {
            return RepayCalcType.BGZHK.name();
        }
        return null;

    }

    private List<ProjEstablishPersonInfo> mapPersonInfo(ProjRv rv, String type) {

        List<ProjRv.客户信息> list = rv.get客户信息List().stream().filter(e -> type.equals(e.get客户类别())).collect(Collectors.toList());
        List<ProjEstablishPersonInfo> result = new ArrayList<>();
        for (ProjRv.客户信息 e : list) {
            //特殊处理，
            List<String> expList = toList("李鑫夫人", "吴少杰之妻", "中国阳光纸业控股有限公司", "李亚", "林剑华", "林剑华之妻", "李亚丈夫");
            if (expList.contains(e.get客户名称()) && !type.equals("承租人")) {
                continue;
            }

            Client client = getOrCreateClient(e.get客户名称());
            ProjEstablishPersonInfo personInfo = new ProjEstablishPersonInfo();
            personInfo.setClientName(client.getClientName());
            personInfo.setClientId(client.getId());
            personInfo.setClientType(client.getClientType());
            personInfo.setStockRiskExposure(null);
            result.add(personInfo);

        }
        return result;
    }

    private String mapLeaseType(ProjRv rv) {
        if ("售后回租".equals(rv.get基本信息().get业务类型())) {
            return LeaseType.hui_zu.name();
        }
        if ("直接租赁".equals(rv.get基本信息().get业务类型())) {
            return LeaseType.zhi_zu.name();
        }
        log.error("租赁类型映射失败." + rv.get列表信息().get项目编号());
        return null;
    }

    private String mapProjType(ProjRv rv) {
        if ("公共事业类".equals(rv.get基本信息().get项目类型())) {
            return ProjectType.PUBLIC_UTILITIES.name();
        }
        String display = rv.get基本信息().get项目类型();
        for (ProjectType value : ProjectType.values()) {
            if (value.display.equals(display)) {
                return value.name();
            }

        }
        return null;
    }
}
