package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.kpi.KpiProvisionStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.third.CQAccountApplicationTypeENUM;
import cn.zswltech.mithras.service.enums.third.CQBusinessTypeENUM;
import cn.zswltech.mithras.service.enums.third.CQTaxRateENUM;
import cn.zswltech.mithras.service.enums.third.ExceptionSourceENUM;
import cn.zswltech.mithras.service.mapper.finance.ContractAssessDeptDetailMapper;
import cn.zswltech.mithras.service.mapper.kpi.KpiProvisionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.finance.ContractAssessDeptDetail;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProvisionBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.third.financial.SyncReceiveProvisionService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.service.service.third.financial.vo.CQ2AccountApplicationVO;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
* @description 绩效-拨备表
* @author vico
* @date 2023-06-20
*/
@Service
public class KpiProvisionBaseInfoService extends ServiceImpl<KpiProvisionBaseInfoMapper, KpiProvisionBaseInfo> {

    @Resource
    protected KpiProvisionDetailService kpiProvisionDetailService;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private SyncReceiveProvisionService syncReceiveProvisionService;

    @Resource
    private ContractAssessDeptDetailMapper contractAssessDeptDetailMapper;

    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private RedisDistLock redisDistLock;



    @Transactional(rollbackFor = Throwable.class)
    public Long add(KpiProvisionBaseInfoAddREQ req) {
        if(req.getProvisionDate().isAfter(LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()))){
            throw new MithrasException("不能创建未来月份数据");
        }
        if(baseMapper.selectCount(Wrappers.<KpiProvisionBaseInfo>lambdaQuery()
                .eq(KpiProvisionBaseInfo::getProvisionDate, req.getProvisionDate())) > 0){
            throw new MithrasException(req.getProvisionDate() + "已存在数据，请勿重复创建");
        }
        KpiProvisionBaseInfo info = BeanUtil.copyProperties(req, KpiProvisionBaseInfo.class);
        info.setProvisionStatus(KpiProvisionStatusEnum.UN_EFFECT.name());
        baseMapper.insert(info);
        String lockKey = CacheEnum.KPI_PROVISION_DETAIL_CALCULATE_LOCK.buildKey("ALL");
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(ResultMsg.KPI_PROVISION_DETAIL_LOCK);
        }
        try {
            kpiProvisionDetailService.add(req.getProvisionDate(), info.getId());
        }finally {
            redisDistLock.unlock(lockKey);
        }
        return info.getId();
    }

    public PageR<KpiProvisionBaseInfoListRSP> list(KpiProvisionBaseInfoListREQ req) {
        Page<KpiProvisionBaseInfo> kpiProvisionBaseInfoPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<KpiProvisionBaseInfo>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getProvisionDate()), KpiProvisionBaseInfo::getProvisionDate, req.getProvisionDate())
                .orderByDesc(KpiProvisionBaseInfo::getProvisionDate));
        List<KpiProvisionBaseInfo> records = kpiProvisionBaseInfoPage.getRecords();
        List<KpiProvisionBaseInfoListRSP> rsps = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(records)){
            rsps = BeanUtil.copyToList(records, KpiProvisionBaseInfoListRSP.class);
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(rsps.stream().map(KpiProvisionBaseInfoListRSP::getCreateBy).collect(Collectors.toList()));
            rsps.forEach(base -> base.setCreateByName(userId2Name.get(base.getCreateBy())));
        }
        return PageR.of(rsps, kpiProvisionBaseInfoPage.getTotal(), kpiProvisionBaseInfoPage.getCurrent(), kpiProvisionBaseInfoPage.getSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void effect(Long id){
        KpiProvisionBaseInfo kpiProvisionBaseInfo = baseMapper.selectById(id);
        if(ObjectUtil.isEmpty(kpiProvisionBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(KpiProvisionStatusEnum.EFFECT.name().equals(kpiProvisionBaseInfo.getProvisionStatus())){
            throw new MithrasException("已生效，请勿重复生效");
        }
        kpiProvisionBaseInfo.setProvisionStatus(KpiProvisionStatusEnum.EFFECT.name());
        baseMapper.updateById(kpiProvisionBaseInfo);
        syncReceiveProvisionService.syncReceiveProvision(id);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 异步
                new Thread(() -> {
                    try {
                        SpringContextHolder.getBean(KpiProvisionBaseInfoService.class).sendKpiNotice(kpiProvisionBaseInfo);
                    } catch (Exception e) {
                        log.error("拨备通知苍穹错误", e);
                    }
                }).start();
            }
        });
    }

    @Async
    public void sendKpiNotice(KpiProvisionBaseInfo kpiProvisionBaseInfo){
        KpiProvisionBaseInfoDetailREQ listREQ = new KpiProvisionBaseInfoDetailREQ();
        listREQ.setId(kpiProvisionBaseInfo.getId());
        listREQ.setPage(1);
        listREQ.setPageSize(Integer.MAX_VALUE);
        KpiProvisionBaseInfoDetailRSP detail = kpiProvisionDetailService.detail(listREQ);
        String yearAndMonth = kpiProvisionBaseInfo.getProvisionDate().format(DateTimeFormatter.ofPattern(DatePattern.SIMPLE_MONTH_PATTERN));
        if(ObjectUtil.isNotEmpty(detail) && ObjectUtil.isNotEmpty(detail.getProvisionBaseInfoList()) && ObjectUtil.isNotEmpty(detail.getProvisionBaseInfoList().getList())){
            Map<Long, Client> cliendId2Bean = getBean(ClientService.class).listByIds(detail.getProvisionBaseInfoList().getList().stream().map(KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody::getClientId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
            Map<Long, OrgDO> orgId2Bean = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(detail.getProvisionBaseInfoList().getList().stream().map(KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody::getProfitBelongDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
            for (KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody kpiBaseInfo : detail.getProvisionBaseInfoList().getList()) {
                sendKpiAccountApplication(kpiBaseInfo, orgId2Bean.get(kpiBaseInfo.getProfitBelongDeptId()), cliendId2Bean.get(kpiBaseInfo.getClientId()), yearAndMonth);
            }
        }

    }

    private void sendKpiAccountApplication(KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody kpiProvision, OrgDO orgDO, Client client, String yearAndMonth){
        if(ObjectUtil.hasEmpty(kpiProvision, orgDO, client, yearAndMonth)) {
            return;
        }
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        CQ2AccountApplicationVO vo = new CQ2AccountApplicationVO();
        vo.setDescription(CQAccountApplicationTypeENUM.PROVISION.getDisplay());
        vo.setBizdate(nowFormat);
        vo.setTallydate(vo.getBizdate());
        vo.setCico_customer(client.getClientCode());
        vo.setCico_taxcategory("normal");//不确定
        vo.setMainbiztype_number("JTZB001");//报账业务类型.编码
        vo.setDept_number(String.valueOf(orgDO.getMainOrgId()));
        vo.setCico_period_number(yearAndMonth.replaceAll("-", ""));
        vo.setCico_sourcebillno(String.join("-", kpiProvision.getContractCode() + UUIDUtil.genUuid()));//
        BigDecimal taxrate;
        //税率
        if (ProjectBizType.ZL.name().equals(kpiProvision.getBizType()) && LeaseType.zhi_zu.name().equals(kpiProvision.getLeaseType())) {
            taxrate = BigDecimal.valueOf(0.13);
        } else {
            taxrate = BigDecimal.valueOf(0.06);
        }
        if (ObjectUtil.isNotNull(kpiProvision.getContractId())) {
            List<ContractAssessDeptDetail> deptDetailList = contractAssessDeptDetailMapper.selectList(Wrappers.<ContractAssessDeptDetail>lambdaQuery()
                    .eq(ContractAssessDeptDetail::getContractId, kpiProvision.getContractId())
                    .eq(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.NO.getCode()));
            if (!deptDetailList.isEmpty()) {
                ContractAssessDeptDetail contractAssessDeptDetail = deptDetailList.get(0);
                Long deptId = contractAssessDeptDetail.getAssessDeptId();
                OrgDO newOrgDO = orgDOMapper.selectByPrimaryKey(deptId);
                if (newOrgDO != null && ObjectUtil.isNotNull(newOrgDO.getMainOrgId())) {
                    vo.setDept_number(String.valueOf(newOrgDO.getMainOrgId()));
                }
            }
        }

        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> bodys = new ArrayList<>();
        bodys.add(getKpiBody(vo, taxrate, kpiProvision, client, orgDO, yearAndMonth));
        vo.setTallyentryentity(bodys);
        //项目端kpi
        vo.setSource(ExceptionSourceENUM.ASSET_SIDE_KPI.name());
        vo.setBusinessKey(String.valueOf(kpiProvision.getId()));
        vo.setBusinessTitle(kpiProvision.getContractCode());
        SpringContextHolder.getBean(FinancialManagerServiceImpl2.class).cq2AccountApplicationExec(vo);
    }

    private CQ2AccountApplicationVO.CQ2AccountApplicationVOBody getKpiBody(CQ2AccountApplicationVO vo, BigDecimal taxrate, KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody kpiProvision, Client client,OrgDO orgDO, String yearAndMonth) {
        CQ2AccountApplicationVO.CQ2AccountApplicationVOBody body = vo.new CQ2AccountApplicationVOBody();
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        body.setBusinessdate(nowFormat);
        //记账期间 起租日的年月
        vo.setCico_period_number(yearAndMonth);
        //项目起租：租金总额
        body.setTallyexplanation(CQAccountApplicationTypeENUM.PROVISION.getDisplay());
        //含税利息收入
        body.setCico_amount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(kpiProvision.getBonusCurrent()))));
        //不含税金额
        body.setCico_bhsje(body.getCico_amount());
        //含税-不含税
        body.setCico_se(body.getCico_amount());
        body.setCico_contract_num(kpiProvision.getContractCode());
        body.setCico_custname(client.getClientCode());
        body.setCico_hsje(body.getCico_amount());
        body.setTallyamount(body.getCico_amount());
        body.setCico_financialins(client.getClientCode());
        body.setTallydeptid_number(String.valueOf(orgDO.getMainOrgId()));
        body.setCustomer_number(client.getClientCode());
        //body.setCico_project2_number(allMonthlyAir.get(0).getContractCode());
        body.setCico_project2_name(kpiProvision.getContractCode());
        body.setCico_sl_number(CQTaxRateENUM.getCqBusinessType(taxrate).getCode());
        body.setCico_ywlxtyoe_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(ProjectBizType.of(kpiProvision.getBizType()), LeaseType.of(kpiProvision.getLeaseType()))).map(CQBusinessTypeENUM::getCode).orElse(null));
        return body;
    }



    public KpiProvisionBaseInfo getEffectOneByYearMonth(int year, int month) {
        LocalDate startDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate endDayOfMonth = LocalDate.of(year, month, startDayOfMonth.lengthOfMonth());
        LambdaQueryWrapper<KpiProvisionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(KpiProvisionBaseInfo::getProvisionStatus, KpiProvisionStatusEnum.EFFECT.name());
        query.eq(KpiProvisionBaseInfo::getProvisionDate, endDayOfMonth);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public KpiProvisionBaseInfo getLastProvision(){
        return this.getOne(Wrappers.<KpiProvisionBaseInfo>lambdaQuery()
                .orderByDesc(KpiProvisionBaseInfo::getProvisionDate)
                .ne(KpiProvisionBaseInfo::getProvisionStatus, KpiProvisionStatusEnum.EFFECT.name())
                .last(StringUtil.mysqlLimitOne()));
    }

}