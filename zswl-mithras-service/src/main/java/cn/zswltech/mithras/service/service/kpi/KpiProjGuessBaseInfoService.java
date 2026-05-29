package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectClassifyEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.kpi.*;
import cn.zswltech.mithras.service.mapper.kpi.KpiProjGuessBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjGuessBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjGuessDivide;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionWeightLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.lib.kpi.KpiProjectDistributionWeightLibService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 绩效-项目测算表
* @author jackerhe
* @date 2023-06-15
*/
@Service
public class KpiProjGuessBaseInfoService extends ServiceImpl<KpiProjGuessBaseInfoMapper, KpiProjGuessBaseInfo> {

    @Resource
    private KpiProjGuessBaseInfoMapper kpiProjGuessBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private KpiProjGuessDivideService kpiProjGuessDivideService;
    @Resource
    private KpiProjectDistributionWeightLibService kpiProjectDistributionWeightLibService;

    private static final String XMJL = "projmanager";

    private static final String BMC = "部门池";

    public KpiProjGuessBaseInfo getSpecificDateOne(Long contractId, int year, int month) {
        LambdaQueryWrapper<KpiProjGuessBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(KpiProjGuessBaseInfo::getContractId, contractId);
        query.eq(KpiProjGuessBaseInfo::getCalculateDateYear, year);
        query.eq(KpiProjGuessBaseInfo::getCalculateDateMonth, month);
        return this.getOne(query);
    }

    public List<KpiProjGuessBaseInfo> getSpecificDateList(List<Long> contractIds, int year, int month) {
        LambdaQueryWrapper<KpiProjGuessBaseInfo> query = Wrappers.lambdaQuery();
        query.in(ObjectUtil.isNotEmpty(contractIds), KpiProjGuessBaseInfo::getContractId, contractIds);
        query.eq(KpiProjGuessBaseInfo::getCalculateDateYear, year);
        query.eq(KpiProjGuessBaseInfo::getCalculateDateMonth, month);
        return this.list(query);
    }

    //合同维度列表
    public PageR<KpiProjGuessContractIndexRSP> contractList(KpiProjGuessIndexREQ req) {
        //查询数据
        //主办需要判断权限
        KpiProjGuessParam kpiProjGuessParam = buildKpiProjGuessParam(req);
        kpiProjGuessParam.setSponsorUserId(authCheck());
        Page<KpiProjGuessContractDTO> contractGuess = kpiProjGuessBaseInfoMapper.getContractGuess(new Page<>(req.getPage(), req.getPageSize()), kpiProjGuessParam);
        List<KpiProjGuessContractIndexRSP> rsps = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(contractGuess.getRecords())){
            //id2name
            Map<Long, String> receiptId2Name = id2NameService.receiptId2Name(contractGuess.getRecords().stream().map(KpiProjGuessContractDTO::getReceiptId).collect(Collectors.toSet()));
            rsps = BeanUtil.copyToList(contractGuess.getRecords(), KpiProjGuessContractIndexRSP.class);
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(rsps.stream().map(KpiProjGuessContractIndexRSP::getBelongDeptId).collect(Collectors.toList()));
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(rsps.stream().map(KpiProjGuessContractIndexRSP::getSponsorUserId).collect(Collectors.toList()));
            rsps.forEach(base -> {
                base.setBelongDeptName(deptId2Name.get(base.getBelongDeptId()));
                base.setSponsorUserName(userId2Name.get(base.getSponsorUserId()));
                base.setCalculateDate(getCalculateDate(base.getCalculateDateYear(), base.getCalculateDateMonth()));
                base.setReceiptCode(receiptId2Name.get(base.getReceiptId()));
            });
        }
        return PageR.of(rsps, contractGuess.getTotal(), contractGuess.getCurrent(), contractGuess.getSize());
    }

    private Long authCheck(){
       return sysUserService.currentUserIsSpecificJob(XMJL) ? AccountUtil.getLoginInfo().getId() : null;
    }

    //时间列表 一月一条
    public List<KpiProjGuessContractIndexRSP> timeList(KpiProjGuessIndexREQ req) {
        //查询数据
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        PageR<KpiProjGuessContractIndexRSP> kpiProjGuessContractIndexRSPPageR = contractList(req);
        List<KpiProjGuessContractIndexRSP> list = kpiProjGuessContractIndexRSPPageR.getList();
        List<KpiProjGuessContractIndexRSP> rsps = null;
        if(ObjectUtil.isNotEmpty(list)){
            //按月聚合
            Map<String, List<KpiProjGuessContractIndexRSP>> timeMap = list.stream().collect(Collectors.groupingBy(KpiProjGuessContractIndexRSP::getCalculateDate));
            rsps = calculateMap(timeMap);
        }
        return rsps;
    }

    //部门列表 一月一条
    public List<KpiProjGuessContractIndexRSP> deptList(KpiProjGuessIndexREQ req) {
        //查询数据
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        PageR<KpiProjGuessContractIndexRSP> kpiProjGuessContractIndexRSPPageR = contractList(req);
        List<KpiProjGuessContractIndexRSP> list = kpiProjGuessContractIndexRSPPageR.getList();
        List<KpiProjGuessContractIndexRSP> rsps = null;
        if(ObjectUtil.isNotEmpty(list)){
            //按部门月聚合
            Map<String, List<KpiProjGuessContractIndexRSP>> timeMap = new HashMap<>();
            list.forEach(base -> {
                String key = String.join("-", String.valueOf(base.getBelongDeptId()), String.valueOf(base.getCalculateDateYear()),
                        String.valueOf(base.getCalculateDateMonth()));
                List<KpiProjGuessContractIndexRSP> orDefault = timeMap.getOrDefault(key, new ArrayList<>());
                orDefault.add(base);
                timeMap.put(key, orDefault);
            });
            rsps = calculateMap(timeMap);
        }
        return rsps;
    }


    //人员列表 取分配表数据 一月一条
    public List<KpiProjGuessPeopleIndexRSP> peopleList(KpiProjGuessIndexREQ req) {
        //查询数据
        Long userId = authCheck();
        List<KpiProjGuessDivide> kpiProjGuessDividePage = kpiProjGuessDivideService.list(
                Wrappers.<KpiProjGuessDivide>lambdaQuery().eq(ObjectUtil.isNotEmpty(req.getDivideType()), KpiProjGuessDivide::getDivideType, req.getDivideType())
                        .eq(ObjectUtil.isNotEmpty(req.getDivideTargetId()), KpiProjGuessDivide::getDivideTarget, req.getDivideTargetId())
                        .eq(ObjectUtil.isNotEmpty(userId), KpiProjGuessDivide::getDivideTarget, userId)
                        .in(ObjectUtil.isNotEmpty(userId), KpiProjGuessDivide::getDivideType,
                                ListUtil.toList(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name(), KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name(), KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name()))
        );
        List<KpiProjGuessPeopleIndexRSP> rsps = null;
        if(ObjectUtil.isNotEmpty(kpiProjGuessDividePage)){
            rsps = BeanUtil.copyToList(kpiProjGuessDividePage, KpiProjGuessPeopleIndexRSP.class);
            Set<Long> deptSet = new HashSet<>();
            Set<Long> userSet = new HashSet<>();
            //人月
            Map<String, KpiProjGuessPeopleIndexRSP> deptRspMap = new HashMap<>();
            Map<String, KpiProjGuessPeopleIndexRSP> peopleRspMap = new HashMap<>();
            rsps.forEach(rsp -> {
                if(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(rsp.getDivideType())){
                    deptSet.add(rsp.getDivideTarget());
                    KpiProjGuessPeopleIndexRSP kpi = deptRspMap.get(String.join("-", String.valueOf(rsp.getDivideTarget()), String.valueOf(rsp.getDivideMonth())));
                    if(ObjectUtil.isEmpty(kpi)){
                        kpi = BeanUtil.copyProperties(rsp, KpiProjGuessPeopleIndexRSP.class);
                        deptRspMap.put(String.join("-", String.valueOf(rsp.getDivideTarget()), String.valueOf(rsp.getDivideMonth())), kpi);
                    }else {
                        kpi.setProfitCurrent(LongUtil.null2zero(kpi.getProfitCurrent()) + LongUtil.null2zero(rsp.getProfitCurrent()));
                        kpi.setProfitTotal(LongUtil.null2zero(kpi.getProfitTotal()) + LongUtil.null2zero(rsp.getProfitTotal()));
                        kpi.setBonusCurrent(LongUtil.null2zero(kpi.getBonusCurrent()) + LongUtil.null2zero(rsp.getBonusCurrent()));
                        kpi.setBonusTotal(LongUtil.null2zero(kpi.getBonusTotal()) + LongUtil.null2zero(rsp.getBonusTotal()));
                    }
                }else {
                    userSet.add(rsp.getDivideTarget());
                    //填充人员
                    KpiProjGuessPeopleIndexRSP kpi = peopleRspMap.get(String.join("-", String.valueOf(rsp.getDivideTarget()), String.valueOf(rsp.getDivideMonth())));
                    if(ObjectUtil.isEmpty(kpi)){
                        kpi = BeanUtil.copyProperties(rsp, KpiProjGuessPeopleIndexRSP.class);
                        kpi.setDivideType(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
                        peopleRspMap.put(String.join("-", String.valueOf(rsp.getDivideTarget()), String.valueOf(rsp.getDivideMonth())), kpi);
                    } else {
                        kpi.setProfitCurrent(LongUtil.null2zero(kpi.getProfitCurrent()) + LongUtil.null2zero(rsp.getProfitCurrent()));
                        kpi.setProfitTotal(LongUtil.null2zero(kpi.getProfitTotal()) + LongUtil.null2zero(rsp.getProfitTotal()));
                        kpi.setBonusCurrent(LongUtil.null2zero(kpi.getBonusCurrent()) + LongUtil.null2zero(rsp.getBonusCurrent()));
                        kpi.setBonusTotal(LongUtil.null2zero(kpi.getBonusTotal()) + LongUtil.null2zero(rsp.getBonusTotal()));
                    }
                }
            });
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptSet);
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userSet);
            rsps = new ArrayList<>();
            if(ObjectUtil.isNotEmpty(deptRspMap.values())){
                rsps.addAll(deptRspMap.values());
            }
            if(ObjectUtil.isNotEmpty(peopleRspMap.values())){
                rsps.addAll(peopleRspMap.values());
            }
            rsps.forEach(rsp -> {
                if(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(rsp.getDivideType())){
                    rsp.setDivideTargetName(deptId2Name.get(rsp.getDivideTarget()));
                }else {
                    rsp.setDivideTargetName(userId2Name.get(rsp.getDivideTarget()));
                }
                rsp.setCalculateDate(getCalculateDate(rsp.getDivideYear(), rsp.getDivideMonth()));
                KpiProjectWeightTypeEnum kpiProjectWeightTypeEnum = KpiProjectWeightTypeEnum.find(rsp.getDivideType());
                if(kpiProjectWeightTypeEnum != null){
                    rsp.setDivideTypeName(kpiProjectWeightTypeEnum.display());
                }
            });
        }
        return rsps;
    }

    //不同维度计算
    private List<KpiProjGuessContractIndexRSP> calculateMap(Map<String, List<KpiProjGuessContractIndexRSP>> timeMap){
        List<KpiProjGuessContractIndexRSP> map = new ArrayList<>();
        List<KpiProjGuessContractIndexRSP> rsps;
        KpiProjGuessContractIndexRSP kpi;
        for(String key : timeMap.keySet()){
            rsps = timeMap.get(key);
            if(ObjectUtil.isNotEmpty(rsps)){
                kpi = null;
                for(KpiProjGuessContractIndexRSP rsp : rsps){
                    if(kpi == null){
                        kpi = BeanUtil.copyProperties(rsp, KpiProjGuessContractIndexRSP.class);
                    } else {
                        kpi.setProfitCurrent(LongUtil.null2zero(kpi.getProfitCurrent()) + LongUtil.null2zero(rsp.getProfitCurrent()));
                        kpi.setProfitTotal(LongUtil.null2zero(kpi.getProfitTotal()) + LongUtil.null2zero(rsp.getProfitTotal()));
                        kpi.setBonusCurrent(LongUtil.null2zero(kpi.getBonusCurrent()) + LongUtil.null2zero(rsp.getBonusCurrent()));
                        kpi.setBonusTotal(LongUtil.null2zero(kpi.getBonusTotal()) + LongUtil.null2zero(rsp.getBonusTotal()));
                    }
                }
                map.add(kpi);
            }
        }
        return map;
    }

    private KpiProjGuessParam buildKpiProjGuessParam(KpiProjGuessIndexREQ req){
        KpiProjGuessParam kpiProjGuessParam = BeanUtil.copyProperties(req, KpiProjGuessParam.class);
        if(ObjectUtil.isNotEmpty(req.getCalculateDate())){
            kpiProjGuessParam.setCalculateDateYear(req.getCalculateDate().getYear());
            kpiProjGuessParam.setCalculateDateMonth(req.getCalculateDate().getMonthValue());
        }
        return kpiProjGuessParam;
    }

    private String getCalculateDate(int year, int month){
        StringBuilder sb = new StringBuilder();
        if(ObjectUtil.isNotEmpty(year) && year > 0){
            sb.append(year).append("年");
        }
        if(ObjectUtil.isNotEmpty(month) && month > 0){
            sb.append(month).append("月");
        }
        return sb.toString();
    }

    //合同详情
   public PageR<KpiProjGuessDetailRSP> contractDetail(KpiProjGuessDetailREQ req){
       KpiProjDetailParam kpiProjDetailParam = new KpiProjDetailParam();
       kpiProjDetailParam.setContractId(req.getContractId());
       kpiProjDetailParam.setCalculateDateYear(req.getCalculateDateYear());
       kpiProjDetailParam.setCalculateDateMonth(req.getCalculateDateMonth());
       kpiProjDetailParam.setDeptId(req.getDeptId());
       kpiProjDetailParam.setSponsorUserId(authCheck());
       Page<KpiProjGuessDetailDTO> detailGuessList = kpiProjGuessBaseInfoMapper.getDetailGuess(new Page<>(req.getPage(), req.getPageSize()), kpiProjDetailParam);
       List<KpiProjGuessDetailRSP> rsps = buildKpiProjGuessDetailRSP(detailGuessList.getRecords());
       //id2name
       Map<Long, String> receiptId2Name = id2NameService.receiptId2Name(rsps.stream().map(KpiProjGuessDetailRSP::getReceiptId).collect(Collectors.toSet()));
       rsps.forEach(e -> {
           e.setReceiptCode(receiptId2Name.get(e.getReceiptId()));
       });
       return PageR.of(rsps, detailGuessList.getTotal(), detailGuessList.getCurrent(), detailGuessList.getSize());
    }

    public PageR<KpiProjGuessPeopleDetailRSP> peopleDetail(KpiProjGuessDetailREQ req){
        //查询数据
        Long userId = authCheck();
        List<String> divideTypes = new ArrayList<>();
        if(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(req.getDivideType())){
            divideTypes.add(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name());
        } else {
            divideTypes.add(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
            divideTypes.add(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
            divideTypes.add(KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name());
        }
        Page<KpiProjGuessDivide> kpiProjGuessDivideList = kpiProjGuessDivideService.page(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<KpiProjGuessDivide>lambdaQuery()
                        .in(ObjectUtil.isNotEmpty(divideTypes), KpiProjGuessDivide::getDivideType, divideTypes)
                        .eq(ObjectUtil.isNotEmpty(req.getDivideTargetId()), KpiProjGuessDivide::getDivideTarget, req.getDivideTargetId())
                        .eq(ObjectUtil.isNotEmpty(userId), KpiProjGuessDivide::getDivideTarget, userId)
                        .in(ObjectUtil.isNotEmpty(userId), KpiProjGuessDivide::getDivideType,
                                ListUtil.toList(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name(), KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name(), KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name()))
        );
        List<KpiProjGuessPeopleDetailRSP> kpiProjGuessPeopleDetailRSPS = null;
        if(ObjectUtil.isNotEmpty(kpiProjGuessDivideList.getRecords())){
            kpiProjGuessPeopleDetailRSPS = BeanUtil.copyToList(kpiProjGuessDivideList.getRecords(), KpiProjGuessPeopleDetailRSP.class);
            Set<Long> deptSet = new HashSet<>();
            Set<Long> userSet = new HashSet<>();
            Map<String, List<KpiProjGuessPeopleDetailRSP>> calculateMap = new HashMap<>();
            kpiProjGuessPeopleDetailRSPS.forEach(rsp -> {
                if(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(rsp.getDivideType())){
                    deptSet.add(rsp.getDivideTarget());
                }else {
                    userSet.add(rsp.getDivideTarget());
                }
            });
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptSet);
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userSet);
            kpiProjGuessPeopleDetailRSPS.forEach(rsp -> {
                if(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(rsp.getDivideType())){
                    rsp.setDivideTargetName(deptId2Name.get(rsp.getDivideTarget()));
                }else {
                    rsp.setDivideTargetName(userId2Name.get(rsp.getDivideTarget()));
                }
                rsp.setCalculateDate(getCalculateDate(rsp.getDivideYear(), rsp.getDivideMonth()));
                KpiProjectWeightTypeEnum kpiProjectWeightTypeEnum = KpiProjectWeightTypeEnum.find(rsp.getDivideType());
                if(kpiProjectWeightTypeEnum != null){
                    rsp.setDivideTypeName(kpiProjectWeightTypeEnum.display());
                }
                String key = String.join("_", rsp.getDivideType(), String.valueOf(rsp.getDivideTarget()), rsp.getCalculateDate());
                if(ObjectUtil.isEmpty(calculateMap.get(key))){
                    calculateMap.put(key, new ArrayList<>());
                }
                calculateMap.get(key).add(rsp);
            });
            kpiProjGuessPeopleDetailRSPS = calculatePeopleMap(calculateMap);
        }
        return PageR.of(kpiProjGuessPeopleDetailRSPS, kpiProjGuessDivideList.getTotal(), kpiProjGuessDivideList.getCurrent(),
                kpiProjGuessDivideList.getSize());
    }
    //不同维度计算
    private List<KpiProjGuessPeopleDetailRSP> calculatePeopleMap(Map<String, List<KpiProjGuessPeopleDetailRSP>> timeMap){
        List<KpiProjGuessPeopleDetailRSP> map = new ArrayList<>();
        List<KpiProjGuessPeopleDetailRSP> rsps;
        KpiProjGuessPeopleDetailRSP kpi;
        for(String key : timeMap.keySet()){
            rsps = timeMap.get(key);
            if(ObjectUtil.isNotEmpty(rsps)){
                kpi = null;
                for(KpiProjGuessPeopleDetailRSP rsp : rsps){
                    if(kpi == null){
                        kpi = BeanUtil.copyProperties(rsp, KpiProjGuessPeopleDetailRSP.class);
                    } else {
                        kpi.setProfitCurrent(LongUtil.null2zero(kpi.getProfitCurrent()) + LongUtil.null2zero(rsp.getProfitCurrent()));
                        kpi.setProfitTotal(LongUtil.null2zero(kpi.getProfitTotal()) + LongUtil.null2zero(rsp.getProfitTotal()));
                        kpi.setBonusCurrent(LongUtil.null2zero(kpi.getBonusCurrent()) + LongUtil.null2zero(rsp.getBonusCurrent()));
                        kpi.setBonusTotal(LongUtil.null2zero(kpi.getBonusTotal()) + LongUtil.null2zero(rsp.getBonusTotal()));
                    }
                }
                map.add(kpi);
            }
        }
        return map;
    }

    private List<KpiProjGuessDetailRSP> buildKpiProjGuessDetailRSP(List<KpiProjGuessDetailDTO> detailGuessList){
        List<KpiProjGuessDetailRSP> rsps = new ArrayList<>();
        Set<Long> deptIdSet = new HashSet<>();
        Set<Long> userSet = new HashSet<>();
        Map<String, List<Long>> versionProjDistributionIdMap = new HashMap<>();
        if(ObjectUtil.isNotEmpty(detailGuessList)){
            detailGuessList.forEach(datail -> {
                deptIdSet.add(datail.getBelongDeptId());
                userSet.add(datail.getSponsorUserId());
                if(ObjectUtil.isNotEmpty(datail.getKpiProjGuessDivides())){
                    datail.getKpiProjGuessDivides().forEach(divide -> {
                        if(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(divide.getDivideType())){
                            deptIdSet.add(divide.getDivideTarget());
                        } else {
                            userSet.add(divide.getDivideTarget());
                        }
                    });
                }
                List<Long> longs = versionProjDistributionIdMap.getOrDefault(datail.getVersion(), new ArrayList<>());
                longs.add(datail.getProjectDistributionId());
                versionProjDistributionIdMap.put(datail.getVersion(), longs);
            });
            Map<Long, String> deptId2NameMap = id2NameService.deptId2Name(deptIdSet);
            Map<Long, String> userId2NameMap = id2NameService.sysUserId2Name(userSet);
            //查询占比数据
            Map<String, Integer> weightMap = new HashMap<>();
            List<KpiProjectDistributionWeightLib> weights = new ArrayList<>();
            versionProjDistributionIdMap.forEach((key, value) -> {
                weights.addAll(kpiProjectDistributionWeightLibService.list(Wrappers.<KpiProjectDistributionWeightLib>lambdaQuery()
                        .eq(KpiProjectDistributionWeightLib::getVersion, key)
                        .in(ObjectUtil.isNotEmpty(value), KpiProjectDistributionWeightLib::getProjectDistributionId, value)));
            });
            if(ObjectUtil.isNotEmpty(weights)){
                weights.forEach(base -> {
                    weightMap.put(String.join("-", String.valueOf(base.getProjectDistributionId()), base.getWeightType(), base.getWeightTarget()),
                            base.getWeightValue());
                });
            }
            //封装
            detailGuessList.forEach(detailGuess -> {

                KpiProjGuessDetailRSP rsp = BeanUtil.copyProperties(detailGuess, KpiProjGuessDetailRSP.class);
                rsp.setBelongDeptName(deptId2NameMap.get(detailGuess.getBelongDeptId()));
                rsp.setCalculateDate(getCalculateDate(detailGuess.getCalculateDateYear(), detailGuess.getCalculateDateMonth()));
                List<KpiProjectGuessWeightInfo> kpiProjectGuessWeightInfos = BeanUtil.copyToList(detailGuess.getKpiProjGuessDivides(), KpiProjectGuessWeightInfo.class);
                kpiProjectGuessWeightInfos.forEach(kpiProjectGuessWeightInfo -> {
                    KpiProjectWeightTypeEnum kpiProjectWeightTypeEnum = KpiProjectWeightTypeEnum.find(kpiProjectGuessWeightInfo.getDivideType());
                    if(kpiProjectWeightTypeEnum != null){
                        kpiProjectGuessWeightInfo.setDivideTypeName(kpiProjectWeightTypeEnum.getDisplay());
                    }
                    if(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(kpiProjectGuessWeightInfo.getDivideType())){
                        kpiProjectGuessWeightInfo.setDivideTargetName(deptId2NameMap.get(kpiProjectGuessWeightInfo.getDivideTarget()));
                    } else {
                        kpiProjectGuessWeightInfo.setDivideTargetName(userId2NameMap.get(kpiProjectGuessWeightInfo.getDivideTarget()));
                    }
                    kpiProjectGuessWeightInfo.setProjectRadioConfig(stringDecimalTwo(kpiProjectGuessWeightInfo.getProjectRadioConfig()));
                    kpiProjectGuessWeightInfo.setScaleRadioConfig(stringDecimalTwo(kpiProjectGuessWeightInfo.getScaleRadioConfig()));
                    kpiProjectGuessWeightInfo.setTypeRadioConfig(stringDecimalTwo(kpiProjectGuessWeightInfo.getTypeRadioConfig()));
                    kpiProjectGuessWeightInfo.setPaymentBonusRadioConfig(stringDecimalTwo(kpiProjectGuessWeightInfo.getPaymentBonusRadioConfig()));
                    kpiProjectGuessWeightInfo.setWeightValue(kpiProjectGuessWeightInfo.getDivideWeight());
                });
                rsp.setWeightInfoList(kpiProjectGuessWeightInfos);
                rsps.add(rsp);
            });
        }
        return rsps;
    }

    private String stringDecimalTwo(String num) {
        return new BigDecimal(StringUtil.null2Zero(num)).setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    public List<KpiProjGuessProjManageIndexRSP> projManager(KpiProjGuessProjManagerREQ req){
        KpiProjGuessParam kpiProjGuessParam = BeanUtil.copyProperties(req, KpiProjGuessParam.class);
        kpiProjGuessParam.setSponsorUserId(authCheck());
        List<KpiProjGuessProjDTO> kpiProjGuessProjDTOS = kpiProjGuessBaseInfoMapper.projManager(kpiProjGuessParam);
        return BeanUtil.copyToList(kpiProjGuessProjDTOS, KpiProjGuessProjManageIndexRSP.class);
    }

    public List<KpiProjGuessProjManageDetailRSP> projManagerDetail(KpiProjGuessProjManagerDetailREQ req) {
        //拆分查询
        List<KpiProjGuessProjManageDetailRSP> kpiProjGuessProjManageDetailRSPS;
        Map<String, KpiProjGuessProjManageDetailRSP> detailRSPMap = new HashMap<>();
        //主办
        req.setDivideType(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
        req.setSponsorUserId(authCheck());
        List<KpiProjGuessProjDetailDTO> sponsorList = kpiProjGuessBaseInfoMapper.projManagerDetail(BeanUtil.copyProperties(req, KpiProjGuessProjDetailParam.class));
        if (ObjectUtil.isNotEmpty(sponsorList)) {
            sponsorList.forEach(e -> {
                KpiProjGuessProjManageDetailRSP kpiProjGuessProjManageDetailRSP = detailRSPMap.get(this.getKey(e));
                if (ObjectUtil.isEmpty(kpiProjGuessProjManageDetailRSP)) {
                    kpiProjGuessProjManageDetailRSP = BeanUtil.copyProperties(e, KpiProjGuessProjManageDetailRSP.class);
                    detailRSPMap.put(this.getKey(e), kpiProjGuessProjManageDetailRSP);
                }
            });
        }
        //协办
        req.setDivideType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
        List<KpiProjGuessProjDetailDTO> cosponsor = kpiProjGuessBaseInfoMapper.projManagerDetail(BeanUtil.copyProperties(req, KpiProjGuessProjDetailParam.class));
        if (ObjectUtil.isNotEmpty(cosponsor)) {
            cosponsor.stream().filter(ObjectUtil::isNotEmpty).forEach(e -> {
                KpiProjGuessProjManageDetailRSP kpiProjGuessProjManageDetailRSP = detailRSPMap.get(this.getKey(e));
                if (ObjectUtil.isEmpty(kpiProjGuessProjManageDetailRSP)) {
                    kpiProjGuessProjManageDetailRSP = BeanUtil.copyProperties(e, KpiProjGuessProjManageDetailRSP.class, "bonusCurrent", "paymentCurrent");
                    detailRSPMap.put(this.getKey(e), kpiProjGuessProjManageDetailRSP);
                }
                kpiProjGuessProjManageDetailRSP.setBonusCurrentDeputy(e.getBonusCurrent());
                kpiProjGuessProjManageDetailRSP.setPaymentCurrentDeputy(e.getPaymentCurrent());
            });
        }
        //跨部门推荐人
        req.setDivideType(KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name());
        List<KpiProjGuessProjDetailDTO> recommend = kpiProjGuessBaseInfoMapper.projManagerDetail(BeanUtil.copyProperties(req, KpiProjGuessProjDetailParam.class));
        if (ObjectUtil.isNotEmpty(recommend)) {
            recommend.stream().filter(ObjectUtil::isNotEmpty).forEach(e -> {
                KpiProjGuessProjManageDetailRSP kpiProjGuessProjManageDetailRSP = detailRSPMap.get(this.getKey(e));
                if (ObjectUtil.isEmpty(kpiProjGuessProjManageDetailRSP)) {
                    kpiProjGuessProjManageDetailRSP = BeanUtil.copyProperties(e, KpiProjGuessProjManageDetailRSP.class, "bonusCurrent", "paymentCurrent");
                    detailRSPMap.put(this.getKey(e), kpiProjGuessProjManageDetailRSP);
                }
                kpiProjGuessProjManageDetailRSP.setBonusCurrentReference(e.getBonusCurrent());
                kpiProjGuessProjManageDetailRSP.setPaymentCurrentReference(e.getPaymentCurrent());
            });
        }
        kpiProjGuessProjManageDetailRSPS = new ArrayList<>(detailRSPMap.values());
        //部门
        req.setDivideType(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name());
        List<KpiProjGuessProjManageDetailRSP> detailDeptRSPS = BeanUtil.copyToList(kpiProjGuessBaseInfoMapper.projManagerDivideTypeDetail(BeanUtil.copyProperties(req, KpiProjGuessProjDetailParam.class)), KpiProjGuessProjManageDetailRSP.class);
        Map<Long, List<KpiProjGuessProjManageDetailRSP>> deptMap = new HashMap<>();
        if(ObjectUtil.isNotEmpty(detailDeptRSPS)) {
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(detailDeptRSPS.stream().map(KpiProjGuessProjManageDetailRSP::getDeptId).collect(Collectors.toList()));
            detailDeptRSPS.stream().filter(ObjectUtil::isNotEmpty).forEach( e -> {
                e.setDeptName(deptId2Name.get(e.getDeptId()));
                e.setDivideTargetName(BMC);
            });
            deptMap.putAll(detailDeptRSPS.stream().collect(Collectors.groupingBy(KpiProjGuessProjManageDetailRSP::getDeptId)));
        }

        if (ObjectUtil.isNotEmpty(kpiProjGuessProjManageDetailRSPS)) {
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(kpiProjGuessProjManageDetailRSPS.stream().map(KpiProjGuessProjManageDetailRSP::getDeptId).collect(Collectors.toList()));
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(kpiProjGuessProjManageDetailRSPS.stream().map(KpiProjGuessProjManageDetailRSP::getDivideTargetId).collect(Collectors.toList()));
            kpiProjGuessProjManageDetailRSPS.stream().filter(ObjectUtil::isNotEmpty).forEach(e -> {
                e.setDeptName(deptId2Name.get(e.getDeptId()));
                e.setDivideTargetName(userId2Name.get(e.getDivideTargetId()));
            });
        }
        //补充部门池信息

        //计算合计值
        List<KpiProjGuessProjManageDetailRSP> rsps = new ArrayList<>();
        Map<Long, List<KpiProjGuessProjManageDetailRSP>> collect = kpiProjGuessProjManageDetailRSPS.stream().filter(e -> ObjectUtil.isNotEmpty(e.getDeptId())).collect(Collectors.groupingBy(KpiProjGuessProjManageDetailRSP::getDeptId));
        collect.forEach((k,v) -> {
            rsps.addAll(v);
            List<KpiProjGuessProjManageDetailRSP> deptDetail = deptMap.get(k);
            if(ObjectUtil.isNotEmpty(deptDetail)) {
                rsps.addAll(deptDetail);
                v.addAll(deptDetail);
            }
            KpiProjGuessProjManageDetailRSP temp = new KpiProjGuessProjManageDetailRSP();
            temp.setDeptId(k);
            temp.setDeptName(v.get(0).getDeptName());
            temp.setDivideTargetName("合计");
            v.forEach(e -> {
                temp.setBonusCurrent(LongUtil.null2zero(temp.getBonusCurrent()) + LongUtil.null2zero(e.getBonusCurrent()));
                temp.setPaymentCurrent(LongUtil.null2zero(temp.getPaymentCurrent()) + LongUtil.null2zero(e.getPaymentCurrent()));
                temp.setBonusCurrentDeputy(LongUtil.null2zero(temp.getBonusCurrentDeputy()) + LongUtil.null2zero(e.getBonusCurrentDeputy()));
                temp.setPaymentCurrentDeputy(LongUtil.null2zero(temp.getPaymentCurrentDeputy()) + LongUtil.null2zero(e.getPaymentCurrentDeputy()));
                temp.setBonusCurrentReference(LongUtil.null2zero(temp.getBonusCurrentReference()) + LongUtil.null2zero(e.getBonusCurrentReference()));
                temp.setPaymentCurrentReference(LongUtil.null2zero(temp.getPaymentCurrentReference()) + LongUtil.null2zero(e.getPaymentCurrentReference()));
                temp.setAmount(LongUtil.null2zero(temp.getAmount()) + LongUtil.null2zero(e.getAmount()));
            });
            rsps.add(temp);
        });
        //求和
        rsps.forEach(e -> {
            e.setAmount(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(e.getPaymentCurrent()) +
                    LongUtil.null2zero(e.getBonusCurrentDeputy()) + LongUtil.null2zero(e.getPaymentCurrentDeputy()) +
                    LongUtil.null2zero(e.getBonusCurrentReference()) + LongUtil.null2zero(e.getPaymentCurrentReference()));
        });
        return rsps;
    }

    private String getKey(KpiProjGuessProjDetailDTO rsp) {
        return String.join("-", String.valueOf(rsp.getDeptId()), String.valueOf(rsp.getDivideTargetId()));
    }

    public List<KpiProjGuessProjManageCompletionIndexRSP> projManagerCompletion(KpiProjGuessProjManagerREQ req){
        KpiProjGuessProjDetailParam kpiProjGuessProjDetailParam = BeanUtil.copyProperties(req, KpiProjGuessProjDetailParam.class);
        kpiProjGuessProjDetailParam.setSponsorUserId(authCheck());
        return BeanUtil.copyToList(kpiProjGuessBaseInfoMapper.projManagerCompletion(kpiProjGuessProjDetailParam), KpiProjGuessProjManageCompletionIndexRSP.class);
    }

    public List<KpiProjGuessProjManagerCompletionDetailRSP> projManagerCompletionDetail(KpiProjGuessProjManagerDetailREQ req){
        //分情况查询
        Map<String, KpiProjGuessProjManagerCompletionDetailRSP> rspMap = new HashMap<>();
        Set<Long> deptIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        //存量-公用事业类-主办
        List<KpiProjGuessProjCompletionDetailDTO> hpps = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.PROJECT_SPONSOR);
        this.getOther(deptIds, clientIds, hpps);
        //存量-公用事业类-协办
        List<KpiProjGuessProjCompletionDetailDTO> hppc = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.PROJECT_COSPONSOR);
        this.getOther(deptIds, clientIds, hppc);
        //存量-公用事业类-推荐人
        List<KpiProjGuessProjCompletionDetailDTO> hppodr = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND);
        this.getOther(deptIds, clientIds, hppodr);
        //存量-公用事业类-部门池
        List<KpiProjGuessProjCompletionDetailDTO> hpbp = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.BUSINESS_DEPT);
        this.getOther(deptIds, clientIds, hpbp);

        //存量-产业类-主办
        List<KpiProjGuessProjCompletionDetailDTO> hips = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.PROJECT_SPONSOR);
        this.getOther(deptIds, clientIds, hips);
        //存量-产业类-协办
        List<KpiProjGuessProjCompletionDetailDTO> hipc = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.PROJECT_COSPONSOR);
        this.getOther(deptIds, clientIds, hipc);
        //存量-产业类-推荐人
        List<KpiProjGuessProjCompletionDetailDTO> hipodr = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND);
        this.getOther(deptIds, clientIds, hipodr);
        //存量-产业类-部门池
        List<KpiProjGuessProjCompletionDetailDTO> hibp = this.getCompletionDetailDTO(req, null, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.BUSINESS_DEPT);
        this.getOther(deptIds, clientIds, hibp);



        //本年新增-公用事业类-主办
        List<KpiProjGuessProjCompletionDetailDTO> npps = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.PROJECT_SPONSOR);
        this.getOther(deptIds, clientIds, npps);
        //本年新增-公用事业类-协办
        List<KpiProjGuessProjCompletionDetailDTO> nppc = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.PROJECT_COSPONSOR);
        this.getOther(deptIds, clientIds, nppc);
        //本年新增-公用事业类-推荐人
        List<KpiProjGuessProjCompletionDetailDTO> nppodr = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND);
        this.getOther(deptIds, clientIds, nppodr);
        //本年新增-公用事业类-部门池
        List<KpiProjGuessProjCompletionDetailDTO> npbp = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.PUBLIC, KpiProjectWeightTypeEnum.BUSINESS_DEPT);
        this.getOther(deptIds, clientIds, npbp);


        //本年新增-产业类-主办
        List<KpiProjGuessProjCompletionDetailDTO> nips = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.PROJECT_SPONSOR);
        this.getOther(deptIds, clientIds, nips);
        //本年新增-产业类-协办
        List<KpiProjGuessProjCompletionDetailDTO> nipc = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.PROJECT_COSPONSOR);
        this.getOther(deptIds, clientIds, nipc);
        //本年新增-产业类-推荐人
        List<KpiProjGuessProjCompletionDetailDTO> nipodr = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND);
        this.getOther(deptIds, clientIds, nipodr);
        //本年新增-产业类-部门池
        List<KpiProjGuessProjCompletionDetailDTO> nibp = this.getCompletionDetailDTO(req, KpiProjectSourceDistributionEnum.NEW, KpiProjectClassifyEnum.INDUSTRY, KpiProjectWeightTypeEnum.BUSINESS_DEPT);
        this.getOther(deptIds, clientIds, nibp);


        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
        //填充本年存量公共事业类
        if(ObjectUtil.isNotEmpty(hpps)) {
            hpps.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicStock(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrent()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrent()));
            });
        }

        if(ObjectUtil.isNotEmpty(hppc)) {
            hppc.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicStock(publicStock);
                }
                publicStock.setBonusCurrentDeputy(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentDeputy()));
                publicStock.setPaymentCurrentDeputy(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentDeputy()));
            });
        }

        if(ObjectUtil.isNotEmpty(hppodr)) {
            hppodr.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicStock(publicStock);
                }
                publicStock.setBonusCurrentReference(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrentReference(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }

        if(ObjectUtil.isNotEmpty(hpbp)) {
            hpbp.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(e.getDeptId() + "-" + e.getDivideType());
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(e.getDeptId() + "-" + e.getDivideType(), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicStock(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }

        //本年存量产业类
        if(ObjectUtil.isNotEmpty(hips)) {
            hips.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getIndustryStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryStock(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrent()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrent()));
            });
        }

        if(ObjectUtil.isNotEmpty(hipc)) {
            hipc.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getIndustryStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryStock(publicStock);
                }
                publicStock.setBonusCurrentDeputy(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentDeputy()));
                publicStock.setPaymentCurrentDeputy(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentDeputy()));
            });
        }

        if(ObjectUtil.isNotEmpty(hipodr)) {
            hipodr.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getIndustryStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryStock(publicStock);
                }
                publicStock.setBonusCurrentReference(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrentReference(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }

        if(ObjectUtil.isNotEmpty(hibp)) {
            hibp.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(e.getDeptId() + "-" + e.getDivideType());
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(e.getDeptId() + "-" + e.getDivideType(), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryStock(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }

        //本年新增产业类
        if(ObjectUtil.isNotEmpty(nips)) {
            nips.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getIndustryAdd();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryAdd(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrent()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrent()));
            });
        }

        if(ObjectUtil.isNotEmpty(nipc)) {
            nipc.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getIndustryAdd();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryAdd(publicStock);
                }
                publicStock.setBonusCurrentDeputy(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentDeputy()));
                publicStock.setPaymentCurrentDeputy(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentDeputy()));
            });
        }

        if(ObjectUtil.isNotEmpty(nipodr)) {
            nipodr.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getIndustryAdd();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryAdd(publicStock);
                }
                publicStock.setBonusCurrentReference(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrentReference(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }

        if(ObjectUtil.isNotEmpty(nibp)) {
            nibp.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(e.getDeptId() + "-" + e.getDivideType());
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(e.getDeptId() + "-" + e.getDivideType(), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setIndustryAdd(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }

        //本年新增公共事业类
        if(ObjectUtil.isNotEmpty(npps)) {
            npps.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicAdd();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicAdd(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrent()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrent()));
            });
        }

        if(ObjectUtil.isNotEmpty(nppc)) {
            nppc.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicAdd();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicAdd(publicStock);
                }
                publicStock.setBonusCurrentDeputy(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentDeputy()));
                publicStock.setPaymentCurrentDeputy(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentDeputy()));
            });
        }

        if(ObjectUtil.isNotEmpty(nppodr)) {
            nppodr.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(this.getUnique(e));
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(this.getUnique(e), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicAdd();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicAdd(publicStock);
                }
                publicStock.setBonusCurrentReference(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrentReference(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }
        if(ObjectUtil.isNotEmpty(npbp)) {
            npbp.forEach(e -> {
                KpiProjGuessProjManagerCompletionDetailRSP rsp = rspMap.get(e.getDeptId() + "-" + e.getDivideType());
                if(ObjectUtil.isEmpty(rsp)) {
                    rsp = this.getKpiProjGuessProjManagerCompletionDetailRSP(e, deptId2Name, clientId2Name);
                    rspMap.put(e.getDeptId() + "-" + e.getDivideType(), rsp);
                }
                KpiProjGuessProjManagerCompletionDetailRSP.Body publicStock = rsp.getPublicStock();
                if(publicStock == null) {
                    publicStock = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
                    rsp.setPublicAdd(publicStock);
                }
                publicStock.setBonusCurrent(LongUtil.null2zero(e.getBonusCurrent()) + LongUtil.null2zero(publicStock.getBonusCurrentReference()));
                publicStock.setPaymentCurrent(LongUtil.null2zero(e.getPaymentCurrent()) + LongUtil.null2zero(publicStock.getPaymentCurrentReference()));
            });
        }

        //求和
        List<KpiProjGuessProjManagerCompletionDetailRSP> resultRsps = new ArrayList<>();
        //添加合并
        Map<Long, List<KpiProjGuessProjManagerCompletionDetailRSP>> allMap = rspMap.values().stream().collect(Collectors.groupingBy(KpiProjGuessProjManagerCompletionDetailRSP::getDeptId));
        allMap.forEach((deptId, details) -> {
            resultRsps.addAll(details);
            resultRsps.add(addKpiProjGuessProjManagerCompletionDetailRSP(details));
        });
        this.sumKpiProjGuessProjManagerCompletionDetailRSP(resultRsps);
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(resultRsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getDivideTarget).collect(Collectors.toList()));
        resultRsps.forEach(e -> {
            e.setDivideTargetName(userId2Name.get(e.getDivideTarget()));
        });
        return resultRsps;
    }

    private KpiProjGuessProjManagerCompletionDetailRSP addKpiProjGuessProjManagerCompletionDetailRSP(List<KpiProjGuessProjManagerCompletionDetailRSP> rsps) {
        if (ObjectUtil.isEmpty(rsps)) {
            return null;
        }
        KpiProjGuessProjManagerCompletionDetailRSP rsp = new KpiProjGuessProjManagerCompletionDetailRSP();
        rsp.setDivideTypeName("合计");
        //
        rsp.setDeptId(rsps.get(0).getDeptId());
        rsp.setDeptName(rsps.get(0).getDeptName());
        rsp.setBonusAmountStock(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getBonusAmountStock).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        rsp.setPaymentAmountStock(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getPaymentAmountStock).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        rsp.setBonusAmountAdd(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getBonusAmountAdd).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        rsp.setPaymentAmountAdd(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getPaymentAmountAdd).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        //
        rsp.setIndustryStock(bodyAdd(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getIndustryStock).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList())));
        rsp.setPublicStock(bodyAdd(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getPublicStock).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList())));
        rsp.setIndustryAdd(bodyAdd(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getIndustryAdd).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList())));
        rsp.setPublicAdd(bodyAdd(rsps.stream().map(KpiProjGuessProjManagerCompletionDetailRSP::getPublicAdd).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList())));
        return rsp;
    }

    private KpiProjGuessProjManagerCompletionDetailRSP.Body bodyAdd(List<KpiProjGuessProjManagerCompletionDetailRSP.Body> bodies) {
        KpiProjGuessProjManagerCompletionDetailRSP.Body body = new KpiProjGuessProjManagerCompletionDetailRSP.Body();
        body.setBonusCurrent(bodies.stream().map(KpiProjGuessProjManagerCompletionDetailRSP.Body::getBonusCurrent).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        body.setPaymentCurrent(bodies.stream().map(KpiProjGuessProjManagerCompletionDetailRSP.Body::getPaymentCurrent).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        body.setBonusCurrentDeputy(bodies.stream().map(KpiProjGuessProjManagerCompletionDetailRSP.Body::getBonusCurrentDeputy).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        body.setPaymentCurrentDeputy(bodies.stream().map(KpiProjGuessProjManagerCompletionDetailRSP.Body::getPaymentCurrentDeputy).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        body.setBonusCurrentReference(bodies.stream().map(KpiProjGuessProjManagerCompletionDetailRSP.Body::getBonusCurrentReference).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        body.setPaymentCurrentReference(bodies.stream().map(KpiProjGuessProjManagerCompletionDetailRSP.Body::getPaymentCurrentReference).map(LongUtil::null2zero).reduce(Long::sum).orElse(0L));
        return body;
    }

    public List<KpiProjGuessDeptPoolIndexRSP> deptPool(KpiProjGuessDeptPoolREQ req){
        KpiProjGuessProjDetailParam kpiProjGuessProjDetailParam = BeanUtil.copyProperties(req, KpiProjGuessProjDetailParam.class);
        kpiProjGuessProjDetailParam.setSponsorUserId(authCheck());
        return BeanUtil.copyToList(kpiProjGuessBaseInfoMapper.deptPool(kpiProjGuessProjDetailParam), KpiProjGuessDeptPoolIndexRSP.class);
    }

    //主办对应合同下部门池的合计
    public List<KpiProjGuessDeptPooleDetailRSP> deptPoolDetail(KpiProjGuessDeptPoolREQ req){
        //1.查询部门池的所有数据
        List<KpiProjGuessDivide> guessDivides = kpiProjGuessDivideService.list(Wrappers.<KpiProjGuessDivide>lambdaQuery()
                .eq(KpiProjGuessDivide::getDivideType, KpiProjectWeightTypeEnum.BUSINESS_DEPT.name())
                .eq(ObjectUtil.isNotEmpty(req.getCalculateDateYear()), KpiProjGuessDivide::getDivideYear, req.getCalculateDateYear())
                .eq(ObjectUtil.isNotEmpty(req.getCalculateDateMonth()), KpiProjGuessDivide::getDivideMonth, req.getCalculateDateMonth())
                .eq(ObjectUtil.isNotEmpty(req.getDeptId()), KpiProjGuessDivide::getDeptId, req.getDeptId()));
        //2.查询这些合同的主办，由于可能存在移交，使用当时的主办
        List<KpiProjGuessDivide> sponsorDivides = kpiProjGuessDivideService.list(Wrappers.<KpiProjGuessDivide>lambdaQuery()
                .eq(KpiProjGuessDivide::getDivideType, KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name())
                .eq(ObjectUtil.isNotEmpty(req.getCalculateDateYear()), KpiProjGuessDivide::getDivideYear, req.getCalculateDateYear())
                .eq(ObjectUtil.isNotEmpty(req.getCalculateDateMonth()), KpiProjGuessDivide::getDivideMonth, req.getCalculateDateMonth())
                .eq(ObjectUtil.isNotEmpty(req.getDeptId()), KpiProjGuessDivide::getDeptId, req.getDeptId()));
        if(ObjectUtil.isEmpty(guessDivides) || ObjectUtil.isEmpty(sponsorDivides)){
            return null;
        }
        //按照月份合同分开
        Map<String, KpiProjGuessDivide> deptAmountMap = guessDivides.stream().collect(Collectors.toMap(this::getDeptUnique, e -> e, (a, b) -> a));
        Map<String, KpiProjGuessDeptPooleDetailRSP> sponsorMap = new HashMap<>();
        sponsorDivides.forEach(sponsor -> {
            KpiProjGuessDeptPooleDetailRSP kpiProjGuessDivide = sponsorMap.get(this.getSponsorUnique(sponsor));
            if (ObjectUtil.isEmpty(kpiProjGuessDivide)) {
                kpiProjGuessDivide = BeanUtil.copyProperties(sponsor, KpiProjGuessDeptPooleDetailRSP.class, "profitTotal", "bonusTotal", "paymentTotal", "paymentAwardTotal");
                sponsorMap.put(this.getSponsorUnique(sponsor), kpiProjGuessDivide);
            }
            KpiProjGuessDivide deptDivide = deptAmountMap.get(this.getDeptUnique(sponsor));
            if (ObjectUtil.isNotEmpty(deptDivide)) {
                kpiProjGuessDivide.setProfitTotal(LongUtil.null2zero(kpiProjGuessDivide.getProfitTotal()) + LongUtil.null2zero(deptDivide.getProfitTotal()));
                kpiProjGuessDivide.setBonusTotal(LongUtil.null2zero(kpiProjGuessDivide.getBonusTotal()) + LongUtil.null2zero(deptDivide.getBonusTotal()));
                kpiProjGuessDivide.setPaymentTotal(LongUtil.null2zero(kpiProjGuessDivide.getPaymentTotal()) + LongUtil.null2zero(deptDivide.getPaymentTotal()));
                kpiProjGuessDivide.setPaymentAwardTotal(LongUtil.null2zero(kpiProjGuessDivide.getPaymentAwardTotal()) + LongUtil.null2zero(deptDivide.getPaymentAwardTotal()));
            }
        });
        List<KpiProjGuessDeptPooleDetailRSP> kpiProjGuessDeptPooleDetailRSPS = new ArrayList<>(sponsorMap.values());
        if (ObjectUtil.isNotEmpty(kpiProjGuessDeptPooleDetailRSPS)) {
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(kpiProjGuessDeptPooleDetailRSPS.stream().map(KpiProjGuessDeptPooleDetailRSP::getDeptId).collect(Collectors.toList()));
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(kpiProjGuessDeptPooleDetailRSPS.stream().map(KpiProjGuessDeptPooleDetailRSP::getDivideTarget).collect(Collectors.toList()));
            kpiProjGuessDeptPooleDetailRSPS.forEach(e -> {
                e.setDeptName(deptId2Name.get(e.getDeptId()));
                e.setDivideTargetName(userId2Name.get(e.getDivideTarget()));
                e.setAmount(LongUtil.null2zero(e.getProfitTotal()) + LongUtil.null2zero(e.getBonusTotal()) + LongUtil.null2zero(e.getPaymentTotal()) + LongUtil.null2zero(e.getPaymentAwardTotal()));
            });
        }
        return kpiProjGuessDeptPooleDetailRSPS;
    }

    private String getDeptUnique(KpiProjGuessDivide divide) {
        return String.join("-", String.valueOf(divide.getDivideYear()), String.valueOf(divide.getDivideMonth()), String.valueOf(divide.getContractId()));
    }

    private String getSponsorUnique(KpiProjGuessDivide divide) {
        return String.join("-", String.valueOf(divide.getDivideYear()), String.valueOf(divide.getDivideMonth()), String.valueOf(divide.getDeptId()), String.valueOf(divide.getDivideTarget()));
    }

    private List<KpiProjGuessProjCompletionDetailDTO> getCompletionDetailDTO (KpiProjGuessProjManagerDetailREQ req, KpiProjectSourceDistributionEnum source, KpiProjectClassifyEnum classifyEnum, KpiProjectWeightTypeEnum target){
        KpiProjGuessProjCompletionDetailParam param = BeanUtil.copyProperties(req, KpiProjGuessProjCompletionDetailParam.class);
        if(ObjectUtil.isNotEmpty(source)) {
            param.setProjSource(source.name());
        }
        param.setProjClassify(classifyEnum.name());
        param.setDivideType(target.name());
        param.setSponsorUserId(authCheck());
        return kpiProjGuessBaseInfoMapper.projManagerCompletionDetail(param);
    }

    private String getUnique(KpiProjGuessProjCompletionDetailDTO detailDTO) {
        return String.join("-", String.valueOf(detailDTO.getDeptId()), String.valueOf(detailDTO.getDivideTarget()));
    }

    private void getOther(Set<Long> deptIds, Set<Long> clientIds, List<KpiProjGuessProjCompletionDetailDTO> dtos) {
        if(ObjectUtil.isEmpty(dtos)) {
            return;
        }
        dtos.forEach(e -> {
            deptIds.add(e.getDeptId());
            clientIds.add(e.getDivideTarget());
        });
    }

    private KpiProjGuessProjManagerCompletionDetailRSP getKpiProjGuessProjManagerCompletionDetailRSP(KpiProjGuessProjCompletionDetailDTO e, Map<Long, String> deptId2Name, Map<Long, String> clientId2Name) {
        KpiProjGuessProjManagerCompletionDetailRSP rsp =new KpiProjGuessProjManagerCompletionDetailRSP();
        rsp.setDeptId(e.getDeptId());
        rsp.setDeptName(deptId2Name.get(e.getDeptId()));
        rsp.setDivideType(e.getDivideType());
        rsp.setDivideTypeName(Optional.ofNullable(KpiProjectWeightTypeEnum.find(e.getDivideType())).map(KpiProjectWeightTypeEnum::getDisplay).orElse(null));
        if (ObjectUtil.equals(rsp.getDivideTypeName(), KpiProjectWeightTypeEnum.BUSINESS_DEPT.display())) {
            rsp.setDivideTypeName("部门池");
        }
        rsp.setDivideTarget(e.getDivideTarget());
        rsp.setDivideTargetName(clientId2Name.get(e.getDivideTarget()));
        return rsp;
    }

    private void sumKpiProjGuessProjManagerCompletionDetailRSP(List<KpiProjGuessProjManagerCompletionDetailRSP> resultRsps) {
        if (ObjectUtil.isEmpty(resultRsps)) {
            return;
        }
        //
        resultRsps.forEach(e -> {
            if (ObjectUtil.isNotEmpty(e.getIndustryStock())) {
                e.setBonusAmountStock(LongUtil.null2zero(e.getBonusAmountStock()) + LongUtil.null2zero(e.getIndustryStock().getBonusCurrent()) + LongUtil.null2zero(e.getIndustryStock().getBonusCurrentDeputy()) + LongUtil.null2zero(e.getIndustryStock().getBonusCurrentReference()));
                e.setPaymentAmountStock(LongUtil.null2zero(e.getPaymentAmountStock()) + LongUtil.null2zero(e.getIndustryStock().getPaymentCurrent()) + LongUtil.null2zero(e.getIndustryStock().getPaymentCurrentDeputy()) + LongUtil.null2zero(e.getIndustryStock().getPaymentCurrentReference()));
            }
            if (ObjectUtil.isNotEmpty(e.getPublicStock())) {
                e.setBonusAmountStock(LongUtil.null2zero(e.getBonusAmountStock()) + LongUtil.null2zero(e.getPublicStock().getBonusCurrent()) + LongUtil.null2zero(e.getPublicStock().getBonusCurrentDeputy()) + LongUtil.null2zero(e.getPublicStock().getBonusCurrentReference()));
                e.setPaymentAmountStock(LongUtil.null2zero(e.getPaymentAmountStock()) + LongUtil.null2zero(e.getPublicStock().getPaymentCurrent()) + LongUtil.null2zero(e.getPublicStock().getPaymentCurrentDeputy()) + LongUtil.null2zero(e.getPublicStock().getPaymentCurrentReference()));
            }

            if (ObjectUtil.isNotEmpty(e.getIndustryAdd())) {
                e.setBonusAmountAdd(LongUtil.null2zero(e.getBonusAmountAdd()) + LongUtil.null2zero(e.getIndustryAdd().getBonusCurrent()) + LongUtil.null2zero(e.getIndustryAdd().getBonusCurrentDeputy()) + LongUtil.null2zero(e.getIndustryAdd().getBonusCurrentReference()));
                e.setPaymentAmountAdd(LongUtil.null2zero(e.getPaymentAmountAdd()) + LongUtil.null2zero(e.getIndustryAdd().getPaymentCurrent()) + LongUtil.null2zero(e.getIndustryAdd().getPaymentCurrentDeputy()) + LongUtil.null2zero(e.getIndustryAdd().getPaymentCurrentReference()));
            }
            if (ObjectUtil.isNotEmpty(e.getPublicAdd())) {
                e.setBonusAmountAdd(LongUtil.null2zero(e.getBonusAmountAdd()) + LongUtil.null2zero(e.getPublicAdd().getBonusCurrent()) + LongUtil.null2zero(e.getPublicAdd().getBonusCurrentDeputy()) + LongUtil.null2zero(e.getPublicAdd().getBonusCurrentReference()));
                e.setPaymentAmountAdd(LongUtil.null2zero(e.getPaymentAmountAdd()) + LongUtil.null2zero(e.getPublicAdd().getPaymentCurrent()) + LongUtil.null2zero(e.getPublicAdd().getPaymentCurrentDeputy()) + LongUtil.null2zero(e.getPublicAdd().getPaymentCurrentReference()));
            }
        });
    }



}