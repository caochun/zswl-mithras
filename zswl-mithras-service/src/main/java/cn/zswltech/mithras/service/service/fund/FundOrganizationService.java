package cn.zswltech.mithras.service.service.fund;
import cn.zswltech.mithras.common.util.StringUtil;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.convert.fund.FundOrganizationConverter;
import cn.zswltech.mithras.service.enums.fund.OrganizationType;
import cn.zswltech.mithras.service.mapper.datashare.DataShareMerchantsMapper;
import cn.zswltech.mithras.service.mapper.fund.FundOrganizationMapper;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareMerchants;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.common.util.StringUtil.mysqlLimit;

/**
 * @author zhaozhengkang
 * @description 资金管理-机构表
 * @date 2022-12-13
 */
@Service
public class FundOrganizationService extends ServiceImpl<FundOrganizationMapper, FundOrganization> {
    @Resource
    private FundOrganizationConverter fundOrganizationConverter;
    @Resource
    private TypeConversionWorker conversionWorker;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;

    @Resource
    private DataShareMerchantsMapper dataShareMerchantsMapper;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<String> listDistinctAbbreviation(String abbreviation) {
        LambdaQueryWrapper<FundOrganization> query = Wrappers.lambdaQuery();
        query.like(FundOrganization::getAbbreviation, abbreviation);
        query.select(FundOrganization::getAbbreviation);
        return this.list(query).stream().map(FundOrganization::getAbbreviation).distinct().collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundOrganizationAddREQ req) {
        FundOrganization insertEntity = fundOrganizationConverter.addReq2Entity(req);
        OrganizationType organizationType = OrganizationType.valueOf(req.getOrganizationType());
        // 生成机构编号
        Integer totalNow = baseMapper.selectCount(
                Wrappers.<FundOrganization>lambdaQuery().isNotNull(FundOrganization::getId));
        StringBuilder sb = new StringBuilder(LocalDate.now().format(df)).append(String.format("%04d", totalNow + 1));
        switch (organizationType) {
            case BANK:
                sb.insert(0, "BA");
                break;
            case ZL:
                sb.insert(0, "ZL");
                break;
            case OTHER:
                sb.insert(0, "NB");
                break;
            case JT:
                sb.insert(0, "JT");
                break;
            default:
                break;
        }
        insertEntity.setOrganizationCode(sb.toString());
        baseMapper.insert(insertEntity);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundOrganizationModifyREQ req) {
        FundOrganization info = fundOrganizationConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
    }

    public List<FundOrganization> listByOrganizationName(String organizationName) {
        return this.list(Wrappers.<FundOrganization>lambdaQuery()
        .like(ObjectUtil.isNotEmpty(organizationName), FundOrganization::getOrganizationName, organizationName));
    }

    public PageR<FundOrganizationListRSP> list(FundOrganizationListREQ req) {
        Page<FundOrganization> fundOrganizationPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundOrganization>lambdaQuery()
                        .like(StrUtil.isNotBlank(req.getOrganizationName()), FundOrganization::getOrganizationName, req.getOrganizationName())
                        .like(StrUtil.isNotBlank(req.getAbbreviation()), FundOrganization::getAbbreviation, req.getAbbreviation())
                        .eq(ObjectUtil.isNotEmpty(req.getOrganizationType()), FundOrganization::getOrganizationType,
                                req.getOrganizationType())
                        .eq(ObjectUtil.isNotEmpty(req.getOrganizationId()), FundOrganization::getId,
                                req.getOrganizationId())
                        .eq(ObjectUtil.isNotEmpty(req.getCreateBy()), BaseModel::getCreateBy, req.getCreateBy())
                        .ge(ObjectUtil.isNotEmpty(req.getCreateDateFrom()), BaseModel::getCreateTime,
                                conversionWorker.startOfDay(req.getCreateDateFrom()))
                        .le(ObjectUtil.isNotEmpty(req.getCreateDateTo()), BaseModel::getCreateTime,
                                conversionWorker.endOfDay(req.getCreateDateTo()))
                        .orderByDesc(BaseModel::getUpdateTime));
        List<Long> userIds = fundOrganizationPage.getRecords().stream().map(BaseModel::getCreateBy).collect(Collectors.toList());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        List<FundOrganizationListRSP> rspList = new ArrayList<>();
        Map<Long, Pair<Long, Long>> creditLimitPairMap = fundCreditService.queryCreditLimitBatch(fundOrganizationPage.getRecords().stream().map(FundOrganization::getId).collect(Collectors.toList()));
        for (FundOrganization record : fundOrganizationPage.getRecords()) {
            FundOrganizationListRSP rsp = fundOrganizationConverter.entity2ListRsp(record);
            Pair<Long, Long> longLongPair = creditLimitPairMap.getOrDefault(record.getId(), Pair.of(0L, 0L));
            rsp.setTotalCreditAmount(longLongPair.getKey());
            rsp.setUsedCreditAmount(longLongPair.getKey() - longLongPair.getValue());
            rsp.setRemainingCreditAmount(longLongPair.getValue());
            rsp.setCreateByName(userId2Name.get(record.getCreateBy()));
            if (ObjectUtil.isNotEmpty(record.getContactInfo())) {
                ContactInfo contactInfo = JSON.parseObject(record.getContactInfo(), ContactInfo.class);
                rsp.setContactName(contactInfo.getName());
            }
            rspList.add(rsp);
        }
        return PageR.of(fundOrganizationPage, rspList);
    }

    public FundOrganizationDetailRSP detail(Long id) {
        FundOrganization fundOrganization = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(fundOrganization)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundOrganizationDetailRSP detailRSP = new FundOrganizationDetailRSP();
        detailRSP.setInstitutionCode(fundOrganization.getInstitutionCode());
        return fundOrganizationConverter.entity2DetailRsp(fundOrganization);
    }

    public List<Long> getIdsByType(String type) {
        return baseMapper.selectList(Wrappers.<FundOrganization>lambdaQuery().eq(FundOrganization::getOrganizationType, type)).stream().map(FundOrganization::getId).collect(Collectors.toList());
    }


    public Map<Long, String> getNamesByIds(Collection<Long> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        return baseMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(FundOrganization::getId, FundOrganization::getOrganizationName));
    }

    public FundOrganizationCommonRSP getInstitutionCode(String uscCode) {
        DataShareMerchants dataShareMerchants = dataShareMerchantsMapper.selectOne(Wrappers.<DataShareMerchants>lambdaQuery()
                .eq(DataShareMerchants::getCreditCode, uscCode).last(mysqlLimit(0, 1)));
        if (ObjectUtil.isEmpty(dataShareMerchants)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundOrganizationCommonRSP rsp = new FundOrganizationCommonRSP();
        rsp.setInstitutionCode(String.valueOf(dataShareMerchants.getClientId()));
        return rsp;
    }


    public List<FundOrganization> getByFinancingId(Long financingId){
        List<FundFinancingCreditRef> financingCreditRefList = financingCreditRefService.queryByFinancingId(financingId);
        if(CollectionUtil.isNotEmpty(financingCreditRefList)){
            List<Long> orgIdList = financingCreditRefList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toList());
            return baseMapper.selectBatchIds(orgIdList);
        }
        return Collections.emptyList();
    }

    public Map<Long ,List<FundOrganization>> getBatchByFinancingId(Collection<Long> financingIdList){
        Map<Long ,List<FundOrganization>> resultMap = new HashMap<>();
        if(CollectionUtil.isEmpty(financingIdList)){
            return resultMap;
        }
        Map<Long ,List<FundFinancingCreditRef>> refMap = financingCreditRefService.queryBatchByFinancingId(financingIdList);
        if(CollectionUtil.isNotEmpty(refMap)){
            Set<Long> orgIdSet = refMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
            Map<Long, FundOrganization> orgMap = listByIds(orgIdSet).stream().collect(Collectors.toMap(FundOrganization::getId, Function.identity()));
            refMap.forEach((financingId, list) -> {
                List<FundOrganization> organizationList = list.stream().map(m -> orgMap.get(m.getOrganizationId())).filter(Objects::nonNull).collect(Collectors.toList());
                resultMap.put(financingId, organizationList);
            });
        }
        return resultMap;
    }

    public BigDecimal currentDepositRateAverage(){
        List<FundOrganization> organizationList = list(Wrappers.<FundOrganization>lambdaQuery()
                .eq(FundOrganization::getOrganizationType, OrganizationType.BANK.name()));
        if(CollectionUtil.isNotEmpty(organizationList)){
            long sum = organizationList.stream().filter(f -> f.getCurrentDepositRate() != null).mapToLong(FundOrganization::getCurrentDepositRate).sum();
            BigDecimal average = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(organizationList.size()), 10, RoundingMode.HALF_UP);
            // 转为可计算利率
            return average.divide(new BigDecimal("1000000"), 10, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}