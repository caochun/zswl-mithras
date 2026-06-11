package cn.zswltech.mithras.others.hand.projEtb;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.dao.UserOrgRoleDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.*;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishLeasePriceMapper;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.impl.ProjEstablishVersionServiceImpl;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.ListUtil.toList;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.foundation.util.Util.toMithrasUnit;

/**
 * @author junke
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjEstablishImporter {
    //    private static final String prefix = "（汉得导入勿用）";
    private static final String prefix = "";


    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Value("${mithras.job.divisionLeader}")
    private String divisionLeaderJob;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjEstablishBaseInfoMapper baseInfoMapper;
    @Resource
    private ProjEstablishLeasePriceMapper leasePriceMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserOrgRoleDOMapper userOrgRoleDOMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjEstablishVersionServiceImpl projEstablishVersionService;
    @Resource
    private TransactionTemplate transactionTemplate;

    //    private static final List<String> inSet = JSONUtil.toList("[\"ZTZLCHA202208014\",\"ZTZLCHA202207008\",\"ZTZLCHA202205015\",\"ZTZLCHA202204014\",\"ZTZLCHA202204011\",\"ZTZLCHA202202013\",\"ZTZLCHA202111014\",\"ZTZLCHA202110009\",\"ZTZLCHA202110008\",\"ZTZLCHA202108006\",\"ZTZLCHA202108005\",\"ZTZLCHA202105004\",\"ZTZLCHA202104009\",\"ZTZLCHA202104004\",\"ZTZLCHA202104002\",\"ZTZLCHA202103008\"]", String.class);

    private List<ProjEtb> etbList = new ArrayList<>();
    private Map<String, String> projSourceTypeMap = new HashMap<>();
    private Set<String> unknownClientSet = new HashSet<>();
    private static final Path filePath = Paths.get(System.getProperty("user.home"), "DeskTop", "立项.xls");


    @BeforeEach
    public void before() {
        ExcelReader reader = new ExcelReader(filePath.toFile(), 0);
        reader.setIgnoreEmptyRow(true);
        for (int i = 1; i < reader.getRowCount(); i++) {
            List<Object> cellValues = reader.readRow(i);
            ProjEtb projEtb = JSONUtil.toBean(cellValues.get(cellValues.size() - 1).toString(), ProjEtb.class);
            etbList.add(projEtb);
        }
        //立项背景
        ExcelReader reader2 = new ExcelReader(Paths.get(System.getProperty("user.home"), "DeskTop", "立项-项目背景.xls").toFile(), 0);
        reader.setIgnoreEmptyRow(true);
        for (int i = 1; i < reader2.getRowCount(); i++) {
            List<Object> cellValues = reader2.readRow(i);
            String code = cellValues.get(0).toString();
            String typeKey = cellValues.get(9).toString().trim();
            ProjSourceType type = "A".equals(typeKey) ? ProjSourceType.clfd : "B".equals(typeKey) ? ProjSourceType.qdjs : ProjSourceType.zzkf;
            projSourceTypeMap.put(code, type.name());
        }
        System.out.println(projSourceTypeMap);
    }

    @Test
    public void failure() {
        Path path2 = Paths.get(System.getProperty("user.home"), "DeskTop", "立项2.xls");
        ExcelReader reader = new ExcelReader(path2.toFile(), 0);
        reader.setIgnoreEmptyRow(true);
        List<ProjEtb> list = new ArrayList<>();
        for (int i = 1; i < reader.getRowCount(); i++) {
            List<Object> cellValues = reader.readRow(i);
            ProjEtb projEtb = JSONUtil.toBean(cellValues.get(cellValues.size() - 1).toString(), ProjEtb.class);
            list.add(projEtb);
        }
        Set<String> largeSet = list.stream().map(e -> e.get列表信息().get立项编号()).collect(Collectors.toSet());
        Set<String> smallSet = etbList.stream().map(e -> e.get列表信息().get立项编号()).collect(Collectors.toSet());
        for (String s : largeSet) {
            if (!smallSet.contains(s)) {
                System.out.println(s);
            }

        }


    }

    @Test
    @Transactional(rollbackFor = Throwable.class)
    public void upload() {
        //todo
    }

    @Test
    public void projSrcExcel() {
        List<String> type1Keywords = ListUtil.toList("存量", "翻单");
        List<String> type2Keywords = ListUtil.toList("渠道", "介绍");
        List<String> type3Keywords = toList("自行", "自主");
        List<List<Object>> data = new ArrayList<>();
        for (ProjEtb etb : etbList) {
            List<Object> lineData = new ArrayList<>();
            String code = etb.get基本信息().get立项编号();
            String name = etb.get基本信息().get立项名称();
            String src = etb.get基本信息().get项目来源();
            String dept = etb.get基本信息().get部门();
            String bizManager = etb.get列表信息().get业务经理();


            Integer count1 = 0;
            Integer count2 = 0;
            Integer count3 = 0;
            if (StrUtil.isNotBlank(src)) {
                for (String t1 : type1Keywords) {
                    if (src.contains(t1)) {
                        count1++;
                    }
                }
                for (String t2 : type2Keywords) {
                    if (src.contains(t2)) {
                        count2++;
                    }
                }
                for (String t3 : type3Keywords) {
                    if (src.contains(t3)) {
                        count3++;
                    }
                }
            }
            lineData.add(code);
            lineData.add(name);
            lineData.add(dept);
            lineData.add(bizManager);
            lineData.add(src);
            lineData.add(count1);
            lineData.add(count2);
            lineData.add(count3);
            data.add(lineData);
        }
        File file = Paths.get(System.getProperty("user.home"), "Desktop", "立项-项目背景.xls").toFile();
        FileUtil.del(file);
        ExcelWriter writer = new ExcelWriter(file);
        writer.writeHeadRow(ListUtil.toList("立项编号", "立项名称", "部门", "业务经理", "项目来源", "存量/翻单匹配数量", "渠道/介绍匹配数量", "自行/自主匹配数量", "确认类型；A存量翻单，B渠道介绍，C自主开发 "));
        writer.write(data);
        writer.close();
        System.out.println("done");

    }

    @Test
    @Transactional(rollbackFor = Throwable.class)
    public void checkClient() {
        for (ProjEtb projEtb : etbList) {
            for (ProjEtb.客户信息 j : projEtb.get客户信息列表()) {
                try {
                    getOrCreateClient(j.get客户名称());
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
        log.info("unknownClientSet：{}", toJsonStr(unknownClientSet));
    }


    @Test
//    @Transactional
    @Rollback(false)
    public void ipt() {
        List<String> doneList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        /*List<String> inList = ListUtil.toList("ZTZLCHA202111014", "ZTZLCHA202110009", "ZTZLCHA202108006", "ZTZLCHA202108005", "ZTZLCHA202105004", "ZTZLCHA202104009", "ZTZLCHA202104004", "ZTZLCHA202103008");
        etbList = etbList.stream().filter(
                e -> inList.contains(e.get列表信息().get立项编号())
        ).collect(Collectors.toList());*/
        log.info("总共{}条立项", etbList.size());
        AtomicInteger index = new AtomicInteger(1);
        for (ProjEtb projEtb : etbList) {
            transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus transactionStatus) {
                    log.info("正在导入第{}条", index.incrementAndGet());
                    if (projEtb.get列表信息().get状态().equals("新建")) {
                        return null;
                    }
                    try {
                        projEtb.get列表信息().set立项名称(prefix + projEtb.get列表信息().get立项名称());
                        projEtb.get基本信息().set立项名称(prefix + projEtb.get基本信息().get立项名称());
                        iptBaseInfo(projEtb);
                        doneList.add(projEtb.get列表信息().get立项编号());
                    } catch (Exception e) {
                        errorList.add(projEtb.get列表信息().get立项编号());
                        log.error("{} ipt error", projEtb.get列表信息().get立项编号(), e);
                        transactionStatus.setRollbackOnly();
                    }
                    return null;
                }
            });


        }
        log.info("doneList：{}", toJsonStr(doneList));
        log.info("errorList：{}", toJsonStr(errorList));
        log.info("unknownClientSet：{}", toJsonStr(unknownClientSet));
    }

    private void iptBaseInfo(ProjEtb etb) {
        ProjEstablishBaseInfo existInfo = baseInfoMapper.selectOne(new QueryWrapper<ProjEstablishBaseInfo>().eq("proj_code", etb.get列表信息().get立项编号()));
//        ProjEstablishBaseInfo existInfo = baseInfoMapper.selectOne(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getProjCode, etb.get列表信息().get立项编号()));
        if (null != existInfo) {
            log.info("已经导入");
            return;
        }
        ProjEstablishBaseInfo baseInfo = new ProjEstablishBaseInfo();
        baseInfo.setProjName(etb.get列表信息().get立项名称());
        baseInfo.setProjCode(etb.get列表信息().get立项编号());
        baseInfo.setBizType(ProjectBizType.ZL.name());
        baseInfo.setLeaseTypes(toJsonStr(toList(mapLeaseType(etb).name())));
        baseInfo.setProjSource(projSourceTypeMap.get(baseInfo.getProjCode()));//置空
        baseInfo.setFundsPurpose(etb.get其他风险情况().get资金用途());
        baseInfo.setProjBackground(etb.get项目概述() + "\n项目来源：" + etb.get基本信息().get项目来源());
        //承租人
        List<ProjEstablishPersonInfo> 承租人列表 = mapPersonInfo(etb, "承租人");
        if (!承租人列表.isEmpty()) {
            baseInfo.setClientId(承租人列表.get(0).getClientId());
        }
        baseInfo.setLesseeInfo(toJsonStr(承租人列表));
        baseInfo.setGuaranteeInfo(toJsonStr(mapPersonInfo(etb, "担保人")));
        baseInfo.setMortgagorInfo(toJsonStr(mapPersonInfo(etb, "抵押人")));
        baseInfo.setPledgorInfo(toJsonStr(mapPersonInfo(etb, "质押人")));
        //
        UserDO user = getOrCreateProjManager(etb.get基本信息().get业务经理());
        UserDO user2 = getOrCreateProjManager(etb.get基本信息().get协办经理());
        baseInfo.setProjSponsorUserId(user.getId());
        baseInfo.setProjCosponsorUserIds(toJsonStr(toList(user2.getId())));
        baseInfo.setBizDeptId(getOrgIdByUserId(user.getId()));
        baseInfo.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(baseInfo.getBizDeptId(), deptLeaderJob));
        baseInfo.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(baseInfo.getBizDeptId(), divisionLeaderJob));
        baseInfo.setProjEstablishStatus(mapStatus(etb));
        ProjProcessState projProcessState = mapProcessStatus(etb);
        if (null != projProcessState) {
            baseInfo.setProcessStatus(projProcessState);
        }
        baseInfo.setCreateBy(baseInfo.getProjSponsorUserId());
        baseInfoMapper.insert(baseInfo);

        //for debug
        if (baseInfo.getProjCode().equals("")) {
            System.out.println(1);
        }
        //报价方案
        ProjEstablishLeasePrice leasePrice = new ProjEstablishLeasePrice();
        leasePrice.setProjEstablishId(baseInfo.getId());
        leasePrice.setApplyCreditAmount(safeNumber(etb.get租赁方案().get融资金额()));
        leasePrice.setCreditAmountLoop(null);
        leasePrice.setLeaseMonthCount(safeInt(etb.get租赁方案().get租赁期限()) == null ? null : safeInt(etb.get租赁方案().get租赁期限()) * 12);
        leasePrice.setEarnestMoney(safeNumber(etb.get租赁方案().get保证金()));
        leasePrice.setRepayTimesYearly(null);
        leasePrice.setDownPayment(safeNumber(etb.get租赁方案().get首期租金()));
        leasePrice.setRepayTimesTotal(safeInt(etb.get租赁方案().get还租期数()));
        leasePrice.setConsultingFee(safeNumber(etb.get租赁方案().get咨询费()));
        leasePrice.setPayType("先付".equals(etb.get租赁方案().get租金偿还方式()) ? PayType.ADVANCED.name() : PayType.AFTERWARD.name());
        leasePrice.setNominalPrice(safeNumber(etb.get租赁方案().get留购价款()));
        RepayCalcType repayCalcType = mapRentalCalcType(etb);
        if (null != repayCalcType) {
            leasePrice.setRentalCalcType(repayCalcType.name());
        }
        leasePrice.setRateType("固定利率".equals(etb.get租赁方案().get租赁利率类型()) ? RateType.FIXED.name() : RateType.FLOAT.name());
        Long aLong = safeNumber(etb.get租赁方案().get租赁利率());
        leasePrice.setLeaseRatePercent(aLong == null ? null : aLong.intValue());
        Long bLong = safeNumber(etb.get租赁方案().get内涵报酬率XIRR());
        leasePrice.setIrrPercent(null == bLong ? null : bLong.intValue());
        RepayRateEnum repayRateEnum = mapRepayRate(etb);
        if (null != repayRateEnum) {
            leasePrice.setRepayRate(repayRateEnum.name());
        }
        leasePriceMapper.insert(leasePrice);
        //上传资料

        //生成版本
        projEstablishVersionService.recordVersion(baseInfo.getId(), VersionTypeEnum.EFFECT, baseInfo.getProjSponsorUserId(), null, VersionTypeConstants.NORMAL);

    }

    private ProjProcessState mapProcessStatus(ProjEtb etb) {
        String sta = etb.get列表信息().get状态();
        if (StrUtil.equals(sta, "已创建项目") || StrUtil.equals(sta, "通过")) {
            return ProjProcessState.NEW_APPROVAL_PASS;
        }
        if (StrUtil.equals(sta, "审批退回") || StrUtil.equals(sta, "关闭")) {
            return ProjProcessState.CANCEL_NEW;
        }
        return null;
    }

    private String mapStatus(ProjEtb etb) {
        String sta = etb.get列表信息().get状态();
        if (StrUtil.equals(sta, "已创建项目") || StrUtil.equals(sta, "通过")) {
            return RecordStatus.TAKE_EFFECT.name();
        }
        if (StrUtil.equals(sta, "审批退回") || StrUtil.equals(sta, "关闭")) {
            return RecordStatus.CLOSED.name();
        }
        return null;
    }

    private Integer safeInt(String str) {
        if (null == str) {
            return null;
        }
        return Integer.parseInt(str);
    }

    private Long safeNumber(String str) {
        if (null == str) {
            return null;
        }
        return toMithrasUnit(new BigDecimal(str.replace(",", "")));
    }

    private RepayRateEnum mapRepayRate(ProjEtb etb) {
        String freq = etb.get租赁方案().get支付频率();
        if ("年".equals(freq)) {
            return RepayRateEnum.YEAR;
        }
        if ("月".equals(freq)) {
            return RepayRateEnum.MONTH;
        }
        if ("半年".equals(freq)) {
            return RepayRateEnum.HALF_YEAR;
        }
        if ("双月".equals(freq)) {
            return RepayRateEnum.DOUBLE_MONTH;
        }
        if ("不规则还款".equals(freq)) {
            return RepayRateEnum.LRREGULAR;
        }
        if ("季".equals(freq)) {
            return RepayRateEnum.QUARTER;
        }
        return null;

    }


    private RepayCalcType mapRentalCalcType(ProjEtb etb) {
        String a = etb.get租赁方案().get价目表();
        if (null == a) {
            return null;
        }
        if ("等额本金".equals(a)) {
            return RepayCalcType.DEBJ;
        }
        if ("等额本息".equals(a)) {
            return RepayCalcType.DEBX;
        }
        if ("固定本金".equals(a)) {
            return RepayCalcType.DEBX;
        }
        if ("不等额租金".equals(a)) {
            return RepayCalcType.BGZHK;
        }
        return null;

    }

    private List<ProjEstablishPersonInfo> mapPersonInfo(ProjEtb etb, String type) {
        List<ProjEtb.客户信息> list = etb.get客户信息列表().stream().filter(e -> type.equals(e.get客户类型())).collect(Collectors.toList());
        List<ProjEstablishPersonInfo> result = new ArrayList<>();
        for (ProjEtb.客户信息 e : list) {
            //特殊处理，
            List<String> expList = toList("李鑫夫人", "吴少杰之妻", "中国阳光纸业控股有限公司", "李亚", "林剑华", "林剑华之妻", "李亚丈夫");
            if (expList.contains(e.get客户名称()) && !type.equals("承租人")) {
                continue;
            }

            Client client = getOrCreateClient(e.get客户名称());
            if (null == client) {
                continue;
            }
            ProjEstablishPersonInfo personInfo = new ProjEstablishPersonInfo();
            personInfo.setClientName(client.getClientName());
            personInfo.setClientId(client.getId());
            personInfo.setClientType(client.getClientType());
            personInfo.setStockRiskExposure(null);
            result.add(personInfo);
        }
        return result;
    }

    private LeaseType mapLeaseType(ProjEtb etb) {
        if ("售后回租".equals(etb.get基本信息().get业务类型())) {
            return LeaseType.hui_zu;
        }
        if ("直接租赁".equals(etb.get基本信息().get业务类型())) {
            return LeaseType.zhi_zu;
        }
        log.error("租赁类型映射失败." + etb.get列表信息().get立项编号());
        return null;
    }

    public Client getOrCreateClient(String name) {
        String clientName = prefix + name.replaceAll("\\s", "");

        //特殊映射
        Map<String, String> mapping = MapUtil.of(
                Pair.of("江苏九鼎新材料股份有限公司", "江苏正威新材料股份有限公司"), Pair.of("上海鼎衡船务有限责任公司", "上海鼎衡航运科技有限公司"),
                Pair.of("开玙供应链（无锡）有限公司", "开玙供应链管理（无锡）有限公司"), Pair.of("嵊州市交通发展有限公司", "嵊州市交通投资发展集团有限公司")
        );

        if (mapping.containsKey(clientName)) {
            clientName = mapping.get(clientName);
        }

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
}
