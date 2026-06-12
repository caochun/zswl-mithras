package cn.zswltech.mithras.blackgray.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.DictionaryService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.gruul.dao.dal.entity.DictionaryDO;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.query.DictionaryQuery;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.excel.model.BlackGrayBatchQueryModel;
import cn.zswltech.mithras.blackgray.dto.external.AffiliatedEnterpriseSearchREQ;
import cn.zswltech.mithras.blackgray.dto.external.AssociatedEnterpriseBatchSearchRSP;
import cn.zswltech.mithras.blackgray.dto.external.AssociatedEnterpriseSearchREQ;
import cn.zswltech.mithras.blackgray.dto.external.AssociatedEnterpriseSearchRSP;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.*;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayBusinessTypeEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayTypeEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayLibraryMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRuleConfig;
import cn.zswltech.mithras.blackgray.port.BlackGrayCustomerPort;
import cn.zswltech.mithras.blackgray.service.*;
import cn.zswltech.mithras.blackgray.external.JKBlackGrayCollisionLibraryHandle;
import cn.zswltech.mithras.blackgray.external.dto.JKBlackGrayCollisionLibraryREQ;
import cn.zswltech.mithras.blackgray.external.dto.JKBlackGrayCollisionLibraryRSP;
import cn.zswltech.mithras.blackgray.util.BlackDesensitizeUtil;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserOrgResolver;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 黑灰名单库
* @author
* @date 2023-11-28
*/
@Service
@Slf4j
public class BlackGrayLibraryServiceImpl implements BlackGrayLibraryService {

    @Resource
    private BlackGrayLibraryMapper blackGrayLibraryMapper;
    @Resource(name = "blackGrayHSExternalDataService")
    private BlackGrayExternalDataService blackGrayExternalDataService;
    @Resource
    private BlackGrayWarehouseRecordService blackGrayWarehouseRecordService;
    @Resource
    private BlackGrayWarehouseRuleConfigService blackGrayWarehouseRuleConfigService;
    @Resource
    private GruulAuthService gruulAuthService;
    @Resource
    DictionaryService dictionaryService;

    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;
    @Resource
    private BlackGrayCustomerPort blackGrayCustomerPort;
    @Resource
    private JKBlackGrayCollisionLibraryHandle jkBlackGrayCollisionLibraryHandle;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(BlackGrayLibraryAddREQ req) {
        BlackGrayLibrary info = BeanUtil.copyProperties(req, BlackGrayLibrary.class);
        info.setBlackGrayTypeNum(BlackGrayTypeEnum.name2Num(req.getBlackGrayType()));
        blackGrayLibraryMapper.insert(info);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(BlackGrayLibraryModifyREQ req) {
        BlackGrayLibrary originalInfo = blackGrayLibraryMapper.selectByPrimaryKey(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            //throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BlackGrayLibrary info = BeanUtil.copyProperties(req, BlackGrayLibrary.class);
        blackGrayLibraryMapper.updateByPrimaryKeySelective(info);
    }

    @Override
    public PageR<BlackGrayLibraryListRSP> list(BlackGrayLibraryListREQ req) {
        //填充部门
        OrgDO userOrg = currentUserOrgResolver.getUserDept();
        // 数字摘要同步接口专用字段，做增量同步
        if(req.getUpdateTime() == null){
            if(CollectionUtil.isEmpty(currentUserOrgResolver.getUserDeptList())){
                throw new MithrasException("当前用户无机构");
            }
        }
        Example example = buildExample(req);
        if (ObjectUtil.isNotEmpty(req.getOrderByList())) {
            req.getOrderByList().forEach(orderBy -> {
                if (ObjectUtil.equals(1, orderBy.getDescFlag())) {
                    example.orderBy(orderBy.getOrderByField()).desc();
                } else {
                    example.orderBy(orderBy.getOrderByField()).asc();
                }
            });
        } else {
            example.orderBy(" blackGrayType").asc();
        }
        if (req.isSingleton()) {
            // 名单识别时查所有，需要全量比较选出最严重的一条
            PageHelper.startPage(1, Integer.MAX_VALUE);
        } else {
            PageHelper.startPage(req.getPage(), req.getPageSize());
        }
        List<BlackGrayLibrary> blackGrayLibraries = blackGrayLibraryMapper.selectByExample(example);
        PageInfo<BlackGrayLibrary> pageInfo = new PageInfo<>(blackGrayLibraries);
        if(CollectionUtil.isEmpty(blackGrayLibraries)){
            return null;
        }
        List<BlackGrayLibraryListRSP> rsps = new ArrayList<>();

        // 在名单识别菜单中调用时，不管金控还是金融企业，都只返回最严重的一条
        if(req.isSingleton()){
            //金融企业只返回最严重一条
            BlackGrayLibrary one = blackGrayLibraries.get(0);
            for (BlackGrayLibrary blackGrayLibrary : blackGrayLibraries) {
                //等级第一
                if (blackGrayLibrary.getBlackGraySort() < one.getBlackGraySort()) {
                    one = blackGrayLibrary;
                } else if (ObjectUtil.equals(blackGrayLibrary.getBlackGraySort(), one.getBlackGraySort()) && (ObjectUtil.equals(blackGrayLibrary.getShareType(), 1) || (ObjectUtil.isNotEmpty(blackGrayLibrary.getPlanOutboundTime()) && blackGrayLibrary.getPlanOutboundTime().after(one.getPlanOutboundTime() == null ? new Date() : one.getPlanOutboundTime())))) {
                    //等级相同取金控的
                    one = blackGrayLibrary;
                } else if ((ObjectUtil.equals(blackGrayLibrary.getBlackGraySort(), one.getBlackGraySort()) && ObjectUtil.isNotEmpty(userOrg) && ObjectUtil.equals(blackGrayLibrary.getApplyOrganization(), userOrg.getCode()))){
                    //最后本机构的
                    one = blackGrayLibrary;
                }
            }
            BlackGrayLibraryListRSP rsp = BeanUtil.copyProperties(one, BlackGrayLibraryListRSP.class);
            rsp.setRiskScale(one.getRiskScale() == null ? "" : one.getRiskScale().setScale(2, RoundingMode.HALF_UP).toPlainString());
            rsps.add(rsp);
        } else {
            blackGrayLibraries.forEach(blackGrayLibrary -> {
                BlackGrayLibraryListRSP rsp = BeanUtil.copyProperties(blackGrayLibrary, BlackGrayLibraryListRSP.class);
                rsp.setRiskScale(blackGrayLibrary.getRiskScale() == null ? "" : blackGrayLibrary.getRiskScale().setScale(2, RoundingMode.HALF_UP).toPlainString());
                rsps.add(rsp);
            });
        }
        rsps.forEach(rsp -> {
            if (ObjectUtil.isNotEmpty(rsp.getApplyReasonType())) {
                List<String> nums = JSONUtil.toList(rsp.getApplyReasonType(), String.class);
                Map<String, String> map = blackGrayWarehouseRuleConfigService.num2NameBatch(nums);
                StringBuilder st = new StringBuilder();
                String temp;
                for (int i = 0; i < nums.size(); i++) {
                    temp = map.get(nums.get(i));
                    if (ObjectUtil.isNotEmpty(temp)) {
                        st.append(temp);
                        if (!ObjectUtil.equals(i, nums.size() - 1)) {
                            st.append(",");
                        }
                    }
                }
                rsp.setApplyReasonType(st.toString());
            }
        });
        return PageR.of(rsps, pageInfo.getTotal());
    }

    @Override
    public PageR<BlackGrayLibraryDistinctListRSP> distinctList(BlackGrayLibraryDistinctListREQ req, boolean needPage) {
        boolean queryAllOrg = false;
        Set<String> orgCodes = new HashSet<>();
        // 没有选中卡片的情况，根据当前用户身份来决定查什么机构，金控查所有，非金控查角色范围内机构
        if (StringUtils.isBlank(req.getCardOrgCode())) {
            queryAllOrg = true;
        } else {
            // 卡片选中了企业的情况，金控用户判断一下是不是选中的ALL，如果是就标记为查所有，不是ALL就查这一家企业

            if ("ALL".equals(req.getCardOrgCode())) {
                queryAllOrg = true;
            } else {
                orgCodes.add(req.getCardOrgCode());
            }
        }

        // 不查所有机构，又没有有效机构，直接返回空
        if (!queryAllOrg && orgCodes.isEmpty()) {
            return null;
        }
        if (needPage) {
            PageHelper.startPage(req.getPage(), req.getPageSize());
        }
        List<BlackGrayLibrary> blackGrayLibraries = blackGrayLibraryMapper.singleEntList(req, queryAllOrg, orgCodes);
        PageInfo<BlackGrayLibrary> pageInfo = new PageInfo<>(blackGrayLibraries);

        List<BlackGrayLibraryDistinctListRSP> rspList = blackGrayLibraries.stream().map(libDO -> {
            BlackGrayLibraryDistinctListRSP rsp = new BlackGrayLibraryDistinctListRSP();
            BeanUtils.copyProperties(libDO, rsp);
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(rspList, needPage ? pageInfo.getTotal() : 0L);
    }

    @Override
    public BlackGrayLibraryRSP libraryRecord(BlackGrayLibraryREQ req) {
        //补充客户信息
        if(ObjectUtil.isNotEmpty(req.getClientId())) {
            BlackGrayCustomerPort.CustomerInfo customerInfo = blackGrayCustomerPort.getById(req.getClientId());
            if (ObjectUtil.isEmpty(customerInfo)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            req.setEnterpriseName(customerInfo.getName());
            req.setUnifiedSocialCreditCode(customerInfo.getUnifiedSocialCreditCode());
        }
        if (ObjectUtil.isEmpty(req.getEnterpriseName()) || ObjectUtil.isEmpty(req.getUnifiedSocialCreditCode())) {
            return null;
        }
        //查询本地库
        BlackGrayLibraryRSP localLibraryRecord = this.getLocalLibraryRecord(req);
        if (ObjectUtil.isNotEmpty(localLibraryRecord) && ObjectUtil.equals(localLibraryRecord.getBlackGrayType(), BlackGrayTypeEnum.BLACK_LIST.name())) {
            return localLibraryRecord;
        }
        List<BlackGrayLibraryRSP> rsps = new ArrayList<>();
        rsps.add(localLibraryRecord);
        //查询金控库
        JKBlackGrayCollisionLibraryREQ jkReq = new JKBlackGrayCollisionLibraryREQ();
        jkReq.setEnterpriseName(req.getEnterpriseName());
        jkReq.setUnifiedSocialCreditCode(req.getUnifiedSocialCreditCode());
        jkReq.setBusinessType(BlackGrayBusinessTypeEnum.INFORMATION_RELATED.name());
        jkReq.setOrgCode(FinancialConstants.RZZL_CODE);
        try {
            rsps.add(this.jkRsp2BlackGrayLibraryRSP(jkBlackGrayCollisionLibraryHandle.execute(jkReq)));

            jkReq.setBusinessType("INSURANCE_CATEGORY");
            rsps.add(this.jkRsp2BlackGrayLibraryRSP(jkBlackGrayCollisionLibraryHandle.execute(jkReq)));

            jkReq.setBusinessType("OTHER");
            rsps.add(this.jkRsp2BlackGrayLibraryRSP(jkBlackGrayCollisionLibraryHandle.execute(jkReq))); rsps.add(this.jkRsp2BlackGrayLibraryRSP(jkBlackGrayCollisionLibraryHandle.execute(jkReq)));

            jkReq.setBusinessType("INSURANCE_CATEGORY");
            rsps.add(this.jkRsp2BlackGrayLibraryRSP(jkBlackGrayCollisionLibraryHandle.execute(jkReq)));

            jkReq.setBusinessType("OTHER");
            rsps.add(this.jkRsp2BlackGrayLibraryRSP(jkBlackGrayCollisionLibraryHandle.execute(jkReq)));
        } catch (Exception e) {
            log.error("查询金控黑灰名单错误", e);
        }

        List<BlackGrayLibraryRSP> collect = rsps.stream().filter(ObjectUtil::isNotEmpty).filter(e -> ObjectUtil.isNotEmpty(e.getBlackGrayType())).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(collect)) {
            return null;
        }
        collect.sort(Comparator.comparing(BlackGrayLibraryRSP::getWarehouseTime));
        for (BlackGrayLibraryRSP e : collect) {
            if (e.getBlackGrayType().equals(BlackGrayTypeEnum.BLACK_LIST.name())) {
                return e;
            }
        }
        return collect.get(0);
    }

    private BlackGrayLibraryRSP jkRsp2BlackGrayLibraryRSP(JKBlackGrayCollisionLibraryRSP rsp) {
        if (ObjectUtil.isEmpty(rsp) || ObjectUtil.isEmpty(rsp.getData())) {
            return null;
        }
        BlackGrayLibraryRSP blackGrayLibraryRSP = new BlackGrayLibraryRSP();
        JKBlackGrayCollisionLibraryRSP.JKBlackGrayCollisionLibraryBody data = rsp.getData();
        blackGrayLibraryRSP.setEnterpriseName(data.getEnterpriseName());


        BlackGrayTypeEnum blackGrayTypeEnum = BlackGrayTypeEnum.jkOf(data.getEnterpriseStatusCode());
        if (ObjectUtil.isNotEmpty(blackGrayTypeEnum)) {
            blackGrayLibraryRSP.setBlackGrayType(blackGrayTypeEnum.name());
        }
        blackGrayLibraryRSP.setApplyReasonType(data.getApplyReasonType());
        blackGrayLibraryRSP.setApplyReasonNames(data.getApplyReasonTypeName());
        blackGrayLibraryRSP.setApplyReason(data.getApplyReason());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        blackGrayLibraryRSP.setWarehouseTime(data.getWarehouseTime() == null ? null : LocalDate.parse(data.getWarehouseTime(), formatter));
        return blackGrayLibraryRSP;
    }

    private BlackGrayLibraryRSP getLocalLibraryRecord(BlackGrayLibraryREQ req) {
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        criteria.andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, req.getUnifiedSocialCreditCode());
        example.orderBy(BlackGrayLibrary.BLACK_GRAY_SORT).asc();
        List<BlackGrayLibrary> blackGrayLibraries = blackGrayLibraryMapper.selectByExample(example);
        if (ObjectUtil.isEmpty(blackGrayLibraries)) {
            return null;
        }
        BlackGrayLibrary blackGrayLibrary = blackGrayLibraries.get(0);
        BlackGrayLibraryRSP blackGrayLibraryRSP = BeanUtil.copyProperties(blackGrayLibrary, BlackGrayLibraryRSP.class, "applyReasonType");
        blackGrayLibraryRSP.setApplyReasonType(JSONUtil.toList(blackGrayLibrary.getApplyReasonType(), String.class));
        blackGrayLibraryRSP.setApplyReasonNames(new ArrayList<>());
        Map<String, BlackGrayWarehouseRuleConfig> stringBlackGrayWarehouseRuleConfigMap = blackGrayWarehouseRuleConfigService.num2BeanBatch(blackGrayLibraryRSP.getApplyReasonType());
        for (String num : blackGrayLibraryRSP.getApplyReasonType()) {
            BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig = stringBlackGrayWarehouseRuleConfigMap.get(num);
            if (ObjectUtil.isNotEmpty(blackGrayWarehouseRuleConfig) && ObjectUtil.isNotEmpty(blackGrayWarehouseRuleConfig.getRuleName())) {
                blackGrayLibraryRSP.getApplyReasonNames().add(blackGrayWarehouseRuleConfig.getRuleName());
            }
        }
        return blackGrayLibraryRSP;
    }

    @Override
    public PageR<BlackGrayLibraryOrgListRSP> orgList(BlackGrayLibraryListREQ req) {
        //填充部门
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if(ObjectUtil.isEmpty(rootOrg)){
            throw new MithrasException("用户所属机构信息不存在");
        }
        req.setApplyOrganization(rootOrg.getCode());
        Example example = buildExample(req);
        example.orderBy(BlackGrayLibrary.PLAN_OUTBOUND_TIME).desc();
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayLibrary> blackGrayLibraries = blackGrayLibraryMapper.selectByExample(example);
        PageInfo<BlackGrayLibrary> pageInfo = new PageInfo<>(blackGrayLibraries);
        List<BlackGrayLibraryOrgListRSP> rsps = new ArrayList<>();
        List<String> numList = new ArrayList<>();
        blackGrayLibraries.forEach(blackGrayLibrary -> {
            BlackGrayLibraryOrgListRSP rsp = BeanUtil.copyProperties(blackGrayLibrary, BlackGrayLibraryOrgListRSP.class, "applyReasonType");
            if(ObjectUtil.isNotEmpty(blackGrayLibrary.getApplyReasonType())){
                rsp.setApplyReasonType(JSONUtil.toList(blackGrayLibrary.getApplyReasonType(), String.class));
                numList.addAll(rsp.getApplyReasonType());
            }
            rsp.setRiskScale(blackGrayLibrary.getRiskScale() == null ? "" : blackGrayLibrary.getRiskScale().setScale(2, RoundingMode.HALF_UP).toPlainString());
            rsps.add(rsp);
        });
        Map<String, String> map = blackGrayWarehouseRuleConfigService.num2NameBatch(numList);
        rsps.forEach(rsp -> {
            if(CollectionUtil.isNotEmpty(rsp.getApplyReasonType())){
                List<String> names = new ArrayList<>();
                rsp.getApplyReasonType().forEach(type -> {
                    names.add(map.get(type));
                });
                rsp.setApplyReasonName(names);
            }
        });
        return PageR.of(rsps, pageInfo.getTotal());
    }

    private Example buildExample(BlackGrayLibraryListREQ req){
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        if(ObjectUtil.isNotEmpty(req.getEnterpriseName())){
            criteria.andLike(BlackGrayLibrary.ENTERPRISE_NAME,  "%" + req.getEnterpriseName() + "%");
        }
        if(ObjectUtil.isNotEmpty(req.getUnifiedSocialCreditCode())){
            criteria.andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, req.getUnifiedSocialCreditCode());
        }
        if (req.getApplyOrganization() != null && !req.getApplyOrganization().equals("ALL")){
            criteria.andEqualTo(BlackGrayLibrary.APPLY_ORGANIZATION, req.getApplyOrganization());
        }
        if(req.getBlackGrayType() != null){
            criteria.andEqualTo(BlackGrayLibrary.BLACK_GRAY_TYPE, req.getBlackGrayType());
        }
        if(req.getBusinessType() != null){
            criteria.andEqualTo(BlackGrayLibrary.BUSINESS_TYPE, req.getBusinessType());
        }
        if (!CollectionUtils.isEmpty(req.getBusinessTypeList())) {
            criteria.andIn(BlackGrayLibrary.BUSINESS_TYPE, req.getBusinessTypeList());
        }
        if(req.getUpdateTime()!=null){
            criteria.andGreaterThanOrEqualTo(BlackGrayLibrary.UPDATE_TIME, req.getUpdateTime());
        }
        if(req.getListType() != null){
            criteria.andEqualTo(BlackGrayLibrary.BLACK_GRAY_TYPE, req.getListType());
        }
        if(req.getWarehouseTimeFrom() != null){
            criteria.andBetween(BlackGrayLibrary.WAREHOUSE_TIME, req.getWarehouseTimeFrom(), req.getWarehouseTimeTo());
        }
        if(req.getPlanOutboundTimeFrom() != null){
            criteria.andBetween(BlackGrayLibrary.PLAN_OUTBOUND_TIME, req.getPlanOutboundTimeFrom(), req.getPlanOutboundTimeTo());
        }
        if(ObjectUtil.isNotEmpty(req.getIds())){
            criteria.andIn(BlackGrayLibrary.ID, req.getIds());
        }
        if(ObjectUtil.isNotEmpty(req.getSource())){
            criteria.andEqualTo(BlackGrayLibrary.SOURCE, req.getSource());
        }
        // 外部提前上传的名单，不管share_type。 内部提交入库等方式的数据，只看share_type=0的。如保险入库一条企业灰名单，会在library中产生两条数据，share_type分别是0(金融企业灰名单)和1(金控黑名单)， 这里只查share_type=0的原始入库记录，去重
        criteria.andCondition("(source = 'EXTERNAL_UPLOAD' or (source !='EXTERNAL_UPLOAD' AND share_type = 0))");
        return example;
    }

    @Override
    public BlackGrayLibraryDetailRSP detail(Long id) {
        BlackGrayLibrary blackGrayLibrary = blackGrayLibraryMapper.selectByPrimaryKey(id);
        if(ObjectUtil.isEmpty(blackGrayLibrary)){
            throw new MithrasException("记录不存在");
        }
        BlackGrayLibraryDetailRSP blackGrayLibraryDetailRSP = BeanUtil.copyProperties(blackGrayLibrary, BlackGrayLibraryDetailRSP.class);
        blackGrayLibraryDetailRSP.setRectifyFileKeys(JSONUtil.toList(blackGrayLibrary.getRectifyFileKeys(), String.class));
        blackGrayLibraryDetailRSP.setCreateByCode(gruulAuthService.getAccountById(blackGrayLibraryDetailRSP.getCreateBy()));
        return blackGrayLibraryDetailRSP;
    }

    @Override
    public PageR<BlackGrayWarehouseRecordListRSP> recordStock(BlackGrayWarehouseRecordListREQ req) {
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        if (req.getApplyOrganization() != null) {
            criteria.andEqualTo(BlackGrayLibrary.APPLY_ORGANIZATION, req.getApplyOrganization());
        }
        if(req.getEnterpriseName() != null){
            criteria.andLike(BlackGrayLibrary.ENTERPRISE_NAME, "%" + req.getEnterpriseName() + "%");
        }
        if(req.getUnifiedSocialCreditCode() != null){
            criteria.andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, req.getUnifiedSocialCreditCode());
        }
        if(req.getBusinessType() != null){
            criteria.andEqualTo(BlackGrayLibrary.BUSINESS_TYPE, req.getBusinessType());
        }
        if(req.getBlackGrayType() != null){
            criteria.andEqualTo(BlackGrayLibrary.BLACK_GRAY_TYPE, req.getBlackGrayType());
        }
        if(req.getSource() != null){
            criteria.andEqualTo(BlackGrayLibrary.SOURCE, req.getSource());
        }
        if(req.getIds() != null){
            criteria.andIn(BlackGrayLibrary.ID, req.getIds());
        }
        if(req.getApplyTimeFrom() != null){
            criteria.andBetween(BlackGrayLibrary.APPLY_TIME, req.getApplyTimeFrom(), req.getApplyTimeTo());
        }
        if(req.getAuditStatus() != null){
            criteria.andEqualTo(BlackGrayLibrary.AUDIT_STATUS, req.getAuditStatus());
        }
        /*if(ObjectUtil.equals(req.getIsStock(), 0)){
            criteria.andIsNotNull(BlackGrayLibrary.RECORD_ID);
        }*/

        if(req.getWarehouseTimeFrom() != null){
            criteria.andBetween(BlackGrayLibrary.WAREHOUSE_TIME, req.getWarehouseTimeFrom(), req.getWarehouseTimeTo());
        }
        if(req.getPlanOutboundTimeFrom() != null){
            criteria.andBetween(BlackGrayLibrary.PLAN_OUTBOUND_TIME, req.getPlanOutboundTimeFrom(), req.getPlanOutboundTimeTo());
        }
        example.orderBy(" updateTime ").desc();
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayLibrary> blackGrayLibraryList = blackGrayLibraryMapper.selectByExample(example);
        PageInfo<BlackGrayLibrary> pageInfo = new PageInfo<>(blackGrayLibraryList);
        List<BlackGrayWarehouseRecordListRSP> blackGrayWarehouseRecordListRSPS = new ArrayList<>();
        List<String> ruleNums = new ArrayList<>();
        blackGrayLibraryList.forEach(record -> {
            BlackGrayWarehouseRecordListRSP blackGrayWarehouseRecordListRSP = BeanUtil.copyProperties(record, BlackGrayWarehouseRecordListRSP.class, "applyReasonType");
            if(ObjectUtil.isNotEmpty(record.getApplyReasonType())){
                blackGrayWarehouseRecordListRSP.setApplyReasonType(JSONUtil.toList(record.getApplyReasonType(), String.class));
                ruleNums.addAll(blackGrayWarehouseRecordListRSP.getApplyReasonType());
            }
            blackGrayWarehouseRecordListRSPS.add(blackGrayWarehouseRecordListRSP);
        });
        Map<String, String> map = blackGrayWarehouseRuleConfigService.num2NameBatch(ruleNums);
        blackGrayWarehouseRecordListRSPS.forEach(blackGrayWarehouseRecordListRSP -> {
            //String s = currentOperator.get(blackGrayWarehouseRecordListRSP.getId());
            //blackGrayWarehouseRecordListRSP.setCurrentOperator(s == null ? gruulAuthService.getAccountById(blackGrayWarehouseRecordListRSP.getCreateBy()) : s);
            //填充申请原因
            if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecordListRSP.getApplyReasonType())){
                for(int i = 0; i < blackGrayWarehouseRecordListRSP.getApplyReasonType().size(); i++){
                    if(ObjectUtil.isEmpty(blackGrayWarehouseRecordListRSP.getApplyReasonName())){
                        blackGrayWarehouseRecordListRSP.setApplyReasonName(new ArrayList<>());
                    }
                    blackGrayWarehouseRecordListRSP.getApplyReasonName().add(map.get(blackGrayWarehouseRecordListRSP.getApplyReasonType().get(i)));
                }
            }
        });

        return PageR.of(blackGrayWarehouseRecordListRSPS, pageInfo.getTotal());
    }

    @Override
    public List<BlackGrayCanBreakBusinessRSP> getCanBreakBusiness(BlackGrayCanBreakREQ req) {
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        criteria.andCondition("(source = 'EXTERNAL_UPLOAD' or (source !='EXTERNAL_UPLOAD' AND share_type = 0))");

        if(req.getUnifiedSocialCreditCode() != null){
            criteria.andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, req.getUnifiedSocialCreditCode());
        }
        example.orderBy("blackGrayType").asc();
        List<BlackGrayLibrary> blackGrayLibraries = blackGrayLibraryMapper.selectByExample(example);
        List<BlackGrayCanBreakBusinessRSP> blackGrayCanBreakBusinessRSPS = null;
        List<BlackGrayCanBreakBusinessRSP> blackGrayCanBreakBusinessList = BeanUtil.copyToList(blackGrayLibraries, BlackGrayCanBreakBusinessRSP.class);
        if (blackGrayCanBreakBusinessList != null) {
            blackGrayCanBreakBusinessList.forEach(rsp -> {
                rsp.setBusinessDesc(Optional.ofNullable(BlackGrayBusinessTypeEnum.of(rsp.getBusinessType())).map(BlackGrayBusinessTypeEnum::getDesc).orElse(rsp.getBusinessType()));
            });
            //去重
            blackGrayCanBreakBusinessRSPS = new ArrayList<>(blackGrayCanBreakBusinessList.stream().collect(Collectors.toMap(BlackGrayCanBreakBusinessRSP::getBusinessType, e -> e, (a, b) -> a)).values());
        }
        return blackGrayCanBreakBusinessRSPS;
    }

    @Override
    public BlackGrayEnterpriseRiskScaleRSP getEnterpriseRiskScale(BlackGrayEnterpriseRiskScaleREQ req) {
        /*CustomerQry customerQry = new CustomerQry();
        customerQry.setCustomerName(req.getEnterpriseName());
        BlackGrayEnterpriseRiskScaleRSP rsp = new BlackGrayEnterpriseRiskScaleRSP();
        List<ConcentrationCustomerDTO> concentrationCustomerDTOS = concentrationCustomerMapper.pageList(customerQry);
        if(concentrationCustomerDTOS != null && concentrationCustomerDTOS.size() > 0){
            List<ReportAllFieldsVO> reportAllFieldsVOS = concentrationStaticService.customerDetail(concentrationCustomerDTOS.get(0).getId());
            if(reportAllFieldsVOS != null && reportAllFieldsVOS.size() > 0 && reportAllFieldsVOS.get(0).getRisk() != null){
                rsp.setRiskExposure(reportAllFieldsVOS.get(reportAllFieldsVOS.size() - 1).getRisk().getRiskExposure());
            }
        }*/
        return null;
    }

    @Override
    //这里查询公司后，系统匹配
    public List<BlackGrayLibraryListRSP> batchQuery(BlackGrayBatchQueryFileREQ req) {
        List<BlackGrayLibraryListRSP> rsps = new ArrayList<>();
        try {
            List<BlackGrayBatchQueryModel> blackGrayBatchQueryModels = EasyExcel.read(req.getFile().getInputStream())
                    .headRowNumber(1)
                    .head(BlackGrayBatchQueryModel.class).sheet().doReadSync();
            if(blackGrayBatchQueryModels.size() > 500){
                throw new MithrasException("批量查询仅支持500条以内");
            }
            List<BlackGrayBatchQueryREQ> blackGrayBatchQueryREQS = BeanUtil.copyToList(blackGrayBatchQueryModels, BlackGrayBatchQueryREQ.class);
            if (StringUtils.isNotBlank(req.getBusinessType()) && req.getBusinessTypeList() == null) {
                req.setBusinessTypeList(Collections.singletonList(req.getBusinessType()));
            }
            return batchQuery(blackGrayBatchQueryREQS, req.getBusinessTypeList());
        } catch (Exception e) {
            log.error("批量查询失败", e);
        }
        return rsps;
    }

    @Override
    public List<BlackGrayLibraryListRSP> batchQuery(List<BlackGrayBatchQueryREQ> blackGrayBatchQueryREQS, List<String> businessTypeList) {
        List<BlackGrayLibraryListRSP> rsps = new ArrayList<>();
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if(ObjectUtil.isNull(rootOrg)){
            throw new MithrasException("无机构信息");
        }
        if (CollectionUtil.isNotEmpty(blackGrayBatchQueryREQS)) {
            //批量查询
            //批量精确组合查询。。。 目前只有两个条件，采用 只有公司名称 + 只有社会编号 + 两者都有取并集的方式，查询条件过多不适合使用
            List<BlackGrayLibrary> blackGrayLibraryList = new ArrayList<>();
            //两者都有
            Set<String> set = blackGrayBatchQueryREQS.stream().filter(e -> ObjectUtil.isNotEmpty(e.getEnterpriseName()) && ObjectUtil.isNotEmpty(e.getUnifiedSocialCreditCode())).map(BlackGrayBatchQueryREQ::getEnterpriseName).collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(set)) {
                Example example = new Example(BlackGrayLibrary.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0)
                        .andIn(BlackGrayLibrary.ENTERPRISE_NAME, set);
                if (CollectionUtil.isNotEmpty(businessTypeList)) {
                    criteria.andIn(BlackGrayLibrary.BUSINESS_TYPE, businessTypeList);
                }
                example.orderBy(" blackGrayType ").asc();
                blackGrayLibraryList.addAll(blackGrayLibraryMapper.selectByExample(example));
            }
            //只有公司
            Set<String> set1 = blackGrayBatchQueryREQS.stream().filter(e -> ObjectUtil.isNotEmpty(e.getEnterpriseName()) && ObjectUtil.isEmpty(e.getUnifiedSocialCreditCode())).map(BlackGrayBatchQueryREQ::getEnterpriseName).collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(set1)) {
                Example example1 = new Example(BlackGrayLibrary.class);
                Example.Criteria criteria = example1.createCriteria()
                        .andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0)
                        .andIn(BlackGrayLibrary.ENTERPRISE_NAME, set1);
                if (CollectionUtil.isNotEmpty(businessTypeList)) {
                    criteria.andIn(BlackGrayLibrary.BUSINESS_TYPE, businessTypeList);
                }
                example1.orderBy(" blackGrayType ").asc();
                blackGrayLibraryList.addAll(blackGrayLibraryMapper.selectByExample(example1));
            }
            //只有社会编号
            Set<String> set2 = blackGrayBatchQueryREQS.stream().filter(e -> ObjectUtil.isEmpty(e.getEnterpriseName()) && ObjectUtil.isNotEmpty(e.getUnifiedSocialCreditCode())).map(BlackGrayBatchQueryREQ::getEnterpriseName).collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(set2)) {
                Example example2 = new Example(BlackGrayLibrary.class);
                Example.Criteria criteria = example2.createCriteria()
                        .andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0)
                        .andIn(BlackGrayLibrary.ENTERPRISE_NAME, set2);
                if (CollectionUtil.isNotEmpty(businessTypeList)) {
                    criteria.andIn(BlackGrayLibrary.BUSINESS_TYPE, businessTypeList);
                }
                example2.orderBy(" blackGrayType ").asc();
                blackGrayLibraryList.addAll(blackGrayLibraryMapper.selectByExample(example2));
            }
            Map<String,BlackGrayLibrary> blackGrayLibraryRspMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(blackGrayLibraryList)) {
                Map<String, BlackGrayLibrary> blackGrayLibraryMap = blackGrayLibraryList.stream().collect(Collectors.toMap(this::getOnlyCode, e -> e, (a, b) -> ObjectUtil.equals(a.getBlackGrayType(), BlackGrayTypeEnum.GRAY_LIST.name()) ? b : a));
                Map<String, BlackGrayLibrary> blackGrayLibraryMap1 = blackGrayLibraryList.stream().collect(Collectors.toMap(BlackGrayLibrary::getEnterpriseName, e -> e, (a, b) -> ObjectUtil.equals(a.getBlackGrayType(), BlackGrayTypeEnum.GRAY_LIST.name()) ? b : a));
                Map<String, BlackGrayLibrary> blackGrayLibraryMap2 = blackGrayLibraryList.stream().collect(Collectors.toMap(BlackGrayLibrary::getUnifiedSocialCreditCode, e -> e, (a, b) -> ObjectUtil.equals(a.getBlackGrayType(), BlackGrayTypeEnum.GRAY_LIST.name()) ? b : a));
                blackGrayBatchQueryREQS.forEach(req -> {
                    BlackGrayLibrary blackGrayLibrary = null;
                    if(ObjectUtil.isNotEmpty(req.getEnterpriseName()) && ObjectUtil.isNotEmpty(req.getUnifiedSocialCreditCode())){
                        blackGrayLibrary = blackGrayLibraryMap.get(getOnlyCode(req));
                    } else if (ObjectUtil.isNotEmpty(req.getEnterpriseName())){
                        blackGrayLibrary = blackGrayLibraryMap1.get(req.getEnterpriseName());
                    } else {
                        blackGrayLibrary = blackGrayLibraryMap2.get(req.getUnifiedSocialCreditCode());
                    }
                    if (ObjectUtil.isNotEmpty(blackGrayLibrary) && ObjectUtil.isEmpty(blackGrayLibraryRspMap.get(getOnlyCode(blackGrayLibrary)))) {
                        blackGrayLibraryRspMap.put(getOnlyCode(blackGrayLibrary), blackGrayLibrary);
                    }
                });
            }
            rsps = BeanUtil.copyToList(blackGrayLibraryRspMap.values(), BlackGrayLibraryListRSP.class);
        }
        rsps.forEach(rsp -> rsp.setRiskScale(new BigDecimal(rsp.getRiskScale() == null ? "0" : rsp.getRiskScale()).setScale(2, RoundingMode.HALF_UP).toPlainString()));
        //脱敏
        /*if(ObjectUtil.isNotEmpty(rsps)){
            rsps.forEach(black -> {
                if(!rootOrg.getCode().equals(black.getApplyOrganization())){
                    BlackDesensitizeUtil.desensitize(black);
                    if(ObjectUtil.equals(0, black.getShareType())){
                        //降级
                        black.setBlackGrayType(BlackGrayTypeEnum.GRAY_LIST.name());
                    }
                }
            });
        }*/
        return rsps;
    }

    private String getOnlyCode(BlackGrayLibrary library){
        return String.join("-", library.getEnterpriseName(), library.getUnifiedSocialCreditCode());
    }

    private String getOnlyCode(BlackGrayBatchQueryREQ library){
        return String.join("-", library.getEnterpriseName(), library.getUnifiedSocialCreditCode());
    }



    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(BlackGrayLibraryRemoveREQ req) {
        BlackGrayLibrary originalInfo = blackGrayLibraryMapper.selectByPrimaryKey(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException("无此记录");
        }
        blackGrayLibraryMapper.deleteByPrimaryKey(req.getId());
    }

   /* @Override
    @Transactional(rollbackFor = Throwable.class)
    public void attemptWarehouse(BlackGrayLibrary blackGrayLibrary) {
        //查询是否有相同的黑灰名单，有更新，无插入
        blackGrayLibrary.setStockStatus(0);
        //判断类型
        getShareType(blackGrayLibrary);
        //填充sort
        getSort(blackGrayLibrary);
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.ENTERPRISE_NAME, blackGrayLibrary.getEnterpriseName());
        criteria.andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, blackGrayLibrary.getUnifiedSocialCreditCode());
        criteria.andEqualTo(BlackGrayLibrary.BUSINESS_TYPE, blackGrayLibrary.getBusinessType());
        //criteria.andEqualTo(BlackGrayLibrary.BLACK_GRAY_TYPE, blackGrayLibrary.getBlackGrayType());
        criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        example.orderBy(" id").desc();
        List<BlackGrayLibrary> blackGrayLibraryHistoryList = blackGrayLibraryMapper.selectByExample(example);
        if(blackGrayLibraryHistoryList != null) {
            BlackGrayTypeEnum blackGrayTypeEnum = BlackGrayTypeEnum.of(blackGrayLibrary.getBlackGrayType());
            if(blackGrayTypeEnum == null){
                throw new BusException("不合法的黑灰标识");
            }
            //这里判断是否需要更新
            BlackGrayLibrary canChange = null;
            BlackGrayTypeEnum canChangeEnum = null;
            for(BlackGrayLibrary blackGray : blackGrayLibraryHistoryList){
                BlackGrayTypeEnum of = BlackGrayTypeEnum.of(blackGray.getBlackGrayType());
                if(of == null){
                    continue;
                }
                if(of.getMonth() <= blackGrayTypeEnum.getMonth()){
                    if(canChange == null || canChangeEnum.getMonth() < of.getMonth()){
                        canChange = blackGray;
                        canChangeEnum = of;
                    }
                }
            }
            if(canChange == null){
                //此处说明有级别高的，不在入库
                blackGrayLibraryMapper.insert(blackGrayLibrary);
                return true;
            }
            //判断是否需要更新
            if(blackGrayTypeEnum.getMonth() > canChangeEnum.getMonth()){
                blackGrayLibraryMapper.insert(blackGrayLibrary);
            }else if (BlackGraySourceEnum.isExternal(canChange.getSource())){
                //已经为外部
                return false;
            }else if(BlackGraySourceEnum.isExternal(blackGrayLibrary.getSource())){
                //外部入库
                blackGrayLibrary.setId(canChange.getId());
                blackGrayLibrary.setPlanOutboundTime(null);
                blackGrayLibraryMapper.updateByPrimaryKey(blackGrayLibrary);
            } else if(blackGrayLibrary.getPlanOutboundTime().after(canChange.getPlanOutboundTime())){
                blackGrayLibrary.setId(canChange.getId());
                blackGrayLibraryMapper.updateByPrimaryKeySelective(blackGrayLibrary);
            } else
            {
                return false;
            }
            blackGrayLibraryMapper.updateByPrimaryKeySelective(blackGrayLibrary);
        } else {
            blackGrayLibraryMapper.insert(blackGrayLibrary);
        }
        return true;
    }*/

    @Override
    @Transactional(rollbackFor = Throwable.class) //不同入库原因产生多条记录
    public void attemptBatchWarehouse(List<BlackGrayLibrary> blackGrayLibrarys) {
        //批量查询
        if(blackGrayLibrarys == null){
            return;
        }
        Example example = new Example(BlackGrayLibrary.class);
        example.createCriteria()
                .andIn(BlackGrayLibrary.ENTERPRISE_NAME, blackGrayLibrarys.stream().map(BlackGrayLibrary::getEnterpriseName).collect(Collectors.toList()))
                .andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0)
                .andIn(BlackGrayLibrary.APPLY_REASON_TYPE, blackGrayLibrarys.stream().map(BlackGrayLibrary::getApplyReasonType).collect(Collectors.toList()));
        List<BlackGrayLibrary> blackGrayLibraryList = blackGrayLibraryMapper.selectByExample(example);
        if(ObjectUtil.isEmpty(blackGrayLibraryList)){
            blackGrayLibraryList = new ArrayList<>();
        }
        //金控企业
        Map<String, List<BlackGrayLibrary>> blackMap = blackGrayLibraryList.stream().filter(e -> ObjectUtil.equals(e.getShareType(), 1)).collect(Collectors.groupingBy(BlackGrayLibrary::getEnterpriseName));
        //自有企业记录
        Map<String, List<BlackGrayLibrary>> ownBlackMap = blackGrayLibraryList.stream().filter(e -> !ObjectUtil.equals(e.getShareType(), 1)).collect(Collectors.groupingBy(BlackGrayLibrary::getEnterpriseName));
        List<BlackGrayLibrary> addLibraryList = new ArrayList<>();
        List<BlackGrayLibrary> updateLibraryList = new ArrayList<>();
        blackGrayLibrarys.forEach(blackGrayLibrary -> {
            getShareType(blackGrayLibrary);
            //填充优先级
            getSort(blackGrayLibrary);

            // 先处理金融企业（包含金控本级和其他金融企业在内，统一处理）
            if (StringUtils.isNotBlank(blackGrayLibrary.getApplyOrganization())) {
                BlackGrayLibrary ownBlackGrayLibrary = BeanUtil.copyProperties(blackGrayLibrary, BlackGrayLibrary.class);
                ownBlackGrayLibrary.setShareType(0);
                List<BlackGrayLibrary> ownBlackGrayLibraryList = ownBlackMap.get(ownBlackGrayLibrary.getEnterpriseName());
                if(ObjectUtil.isNotEmpty(ownBlackGrayLibraryList)){
                    //查询本企业上报记录
                    ownBlackGrayLibraryList = ownBlackGrayLibraryList.stream().filter(e -> ObjectUtil.equals(e.getApplyOrganization(), ownBlackGrayLibrary.getApplyOrganization())).collect(Collectors.toList());
                }
                if (buildBlackGrayLibrary(ownBlackGrayLibrary, ownBlackGrayLibraryList)) {
                    if (ObjectUtil.isNotEmpty(ownBlackGrayLibrary.getId())) {
                        updateLibraryList.add(ownBlackGrayLibrary);
                    } else {
                        addLibraryList.add(ownBlackGrayLibrary);
                    }
                }
            }

            // 金融板块总库处理，看报送数据是否符合金控规则，要入总库，总库中只有一条记录
            if(ObjectUtil.equals(blackGrayLibrary.getShareType(), 1)){
                if(buildBlackGrayLibrary(blackGrayLibrary, blackMap.get(blackGrayLibrary.getEnterpriseName()))){
                    if(ObjectUtil.isNotEmpty(blackGrayLibrary.getId())){
                        updateLibraryList.add(blackGrayLibrary);
                    } else {
                        addLibraryList.add(blackGrayLibrary);
                    }
                }
            }

        });
        if(!addLibraryList.isEmpty()){
            log.info("attemptBatchWarehouse add size {}", addLibraryList.size());
            addLibraryList.forEach(record -> {
                record.setStockStatus(0);
                record.setGroupLeaderFlag(record.getEnterpriseName().equals(record.getMembershipGroup()));
                record.setBlackGrayTypeNum(BlackGrayTypeEnum.name2Num(record.getBlackGrayType()));
            });
            blackGrayLibraryMapper.insertList(addLibraryList);
        }
        if(!updateLibraryList.isEmpty()){
            //todo 这里需要改成批量
            log.info("attemptBatchWarehouse update size {}", updateLibraryList.size());
            updateLibraryList.forEach(record -> {
                record.setStockStatus(0);
                record.setGroupLeaderFlag(record.getEnterpriseName().equals(record.getMembershipGroup()));
                record.setBlackGrayTypeNum(BlackGrayTypeEnum.name2Num(record.getBlackGrayType()));
            });
            updateLibraryList.forEach(blackGrayLibraryMapper::updateByPrimaryKey);
        }
    }

    @Override
    public void completeWarehouse(List<CompleteWarehouseREQ> completeWarehouseREQS, String applyReasonType) {
        if (ObjectUtil.isEmpty(applyReasonType)) {
            return;
        }
        //出库此类型所有数据
        blackGrayLibraryMapper.completeWarehouseOut(applyReasonType);
        String applyReasonJson = JSONUtil.toJsonStr(ListUtil.toList(applyReasonType));
        //入库新数据
        List<BlackGrayLibrary> addLibraryList = BeanUtil.copyToList(completeWarehouseREQS, BlackGrayLibrary.class);
        addLibraryList.forEach(libraryRecord -> {
            libraryRecord.setApplyReasonType(applyReasonJson);
            libraryRecord.setStockStatus(YesOrNoNumberEnum.NO.getCode());
            libraryRecord.setReportFlag(YesOrNoNumberEnum.NO.getCode());
            libraryRecord.setBusinessType(BlackGrayBusinessTypeEnum.INFORMATION_RELATED.name());
            libraryRecord.setGroupLeaderFlag(true);
        });
        blackGrayLibraryMapper.insertList(addLibraryList);
    }

    //出库规则，金控只能出金控的，金融企业只能出自己的，如果金控库内有相同的可一起出，但不影响其他企业
    @Override
    public void outbound(BlackGrayLibrary blackGrayLibrary) {
        if (ObjectUtil.isEmpty(blackGrayLibrary) || StrUtil.hasBlank(blackGrayLibrary.getEnterpriseName(), blackGrayLibrary.getUnifiedSocialCreditCode(), blackGrayLibrary.getBusinessType(), blackGrayLibrary.getBlackGrayType(), blackGrayLibrary.getApplyOrganization(), blackGrayLibrary.getApplyReasonType())) {
            log.info("BlackGrayLibraryServiceImpl outbound param has null, {}", blackGrayLibrary);
            return;
        }
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        criteria.andEqualTo(BlackGrayLibrary.ENTERPRISE_NAME, blackGrayLibrary.getEnterpriseName());
        criteria.andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, blackGrayLibrary.getUnifiedSocialCreditCode());
        criteria.andEqualTo(BlackGrayLibrary.BLACK_GRAY_TYPE, blackGrayLibrary.getBlackGrayType());
        criteria.andEqualTo(BlackGrayLibrary.BUSINESS_TYPE, blackGrayLibrary.getBusinessType());
        criteria.andEqualTo(BlackGrayLibrary.APPLY_REASON_TYPE, blackGrayLibrary.getApplyReasonType());
        List<BlackGrayLibrary> blackGrayLibraryList = blackGrayLibraryMapper.selectByExample(example);
        if (ObjectUtil.isNotEmpty(blackGrayLibraryList)) {
            blackGrayLibraryList.forEach(bean -> {
                if (bean.getApplyOrganization().equals(blackGrayLibrary.getApplyOrganization()) || (!SystemSupportService.ZSJK_CODE.equals(blackGrayLibrary.getApplyOrganization()) && SystemSupportService.ZSJK_CODE.equals(blackGrayLibrary.getApplyOrganization()))) {
                    //出自己企业 金融企业出金控
                    blackGrayLibrary.setId(bean.getId());
                    blackGrayLibrary.setStockStatus(1);
                    blackGrayLibraryMapper.updateByPrimaryKeySelective(blackGrayLibrary);
                }
            });
        }
    }

    //false 丢弃
    private boolean buildBlackGrayLibrary(BlackGrayLibrary blackGrayLibrary, List<BlackGrayLibrary> blackGrayLibraryHistoryList){
        if (blackGrayLibraryHistoryList != null) {
            BlackGrayTypeEnum blackGrayTypeEnum = BlackGrayTypeEnum.of(blackGrayLibrary.getBlackGrayType());
            if (blackGrayTypeEnum == null) {
                throw new MithrasException("不合法的黑灰标识");
            }
            //这里判断是否需要更新
            BlackGrayLibrary canChange = null;
            BlackGrayTypeEnum canChangeEnum = null;
            for (BlackGrayLibrary blackGray : blackGrayLibraryHistoryList) {
                BlackGrayTypeEnum of = BlackGrayTypeEnum.of(blackGray.getBlackGrayType());
                if (of == null) {
                    continue;
                }
                if (of.getMonth() <= blackGrayTypeEnum.getMonth()) {
                    if (canChange == null || canChangeEnum.getMonth() < of.getMonth()) {
                        canChange = blackGray;
                        canChangeEnum = of;
                    }
                }
            }
            if (canChange == null) {
                //此处说明有级别高的，不在入库 -> 需求修改为直接入库
                return true;
            }
            //判断是否需要更新
            if (blackGrayTypeEnum.getMonth() > canChangeEnum.getMonth()) {
                return true;
            } else if (BlackGraySourceEnum.isExternal(blackGrayLibrary.getSource())) {
                //已经为外部
                Date warehouseTime = blackGrayLibrary.getWarehouseTime() == null ? new Date() : blackGrayLibrary.getWarehouseTime();
                if(ObjectUtil.isNotEmpty(canChange.getWarehouseTime()) && warehouseTime.after(canChange.getWarehouseTime())){
                    blackGrayLibrary.setId(canChange.getId());
                    return true;
                }
                return false;
            }  else if (blackGrayLibrary.getPlanOutboundTime().after(canChange.getPlanOutboundTime())) {
                blackGrayLibrary.setId(canChange.getId());
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void radiationSubsidiary(BlackGrayLibrary blackGrayLibrary) {
        AssociatedEnterpriseSearchREQ associatedEnterpriseSearchREQ = new AssociatedEnterpriseSearchREQ();
        associatedEnterpriseSearchREQ.setEnterpriseName(blackGrayLibrary.getEnterpriseName());
        associatedEnterpriseSearchREQ.setUnifiedSocialCreditCode(blackGrayLibrary.getUnifiedSocialCreditCode());
        List<AssociatedEnterpriseSearchRSP> associatedEnterpriseSearchRSPS = blackGrayExternalDataService.associatedEnterpriseSearch(associatedEnterpriseSearchREQ);
        List<BlackGrayLibrary> blackGrayLibraryList = new ArrayList<>();
        circulateBuildBlackGrayLibrary(blackGrayLibrary, blackGrayLibraryList, associatedEnterpriseSearchRSPS);
        if(!blackGrayLibraryList.isEmpty()){
            SpringContextUtil.getBean(BlackGrayLibraryService.class).attemptBatchWarehouse(blackGrayLibraryList);
        }
        //添加记录
        List<BlackGrayWarehouseRecord> records = BeanUtil.copyToList(blackGrayLibraryList, BlackGrayWarehouseRecord.class);
        records.forEach(record -> {
            record.setId(null);
            record.setGroupLeaderFlag(record.getEnterpriseName().equals(record.getMembershipGroup()));
            record.setAuditStatus((int) AuditStatusEnum.FINISH.getCode());
        });
        blackGrayWarehouseRecordService.saveBatch(records);
    }

    @Override
    public List<BlackGrayLibrary> listByNames(Set<String> enterpriseNames) {
        if (ObjectUtil.isEmpty(enterpriseNames)) {
            return new ArrayList<>();
        }
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn(BlackGrayLibrary.ENTERPRISE_NAME, enterpriseNames);
        example.orderBy(" id").desc();
        return blackGrayLibraryMapper.selectByExample(example);
    }

    @Override
    public BlackGrayLibrary getOne(BlackGrayCollisionLibraryREQ req) {
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(BlackGrayLibrary.ENTERPRISE_NAME, req.getEnterpriseName())
                .andEqualTo(BlackGrayLibrary.UNIFIED_SOCIAL_CREDIT_CODE, req.getUnifiedSocialCreditCode())
                .andEqualTo(BlackGrayLibrary.BUSINESS_TYPE, req.getBusinessType());
        List<BlackGrayLibrary> blackGrayLibraryList = blackGrayLibraryMapper.selectByExample(example);
        if(CollectionUtil.isNotEmpty(blackGrayLibraryList)){
            return blackGrayLibraryList.get(0);
        }
        return null;
    }

    @Override
    public List<String> businessType(List<String> orgCodeList) {
        if (CollectionUtil.isEmpty(orgCodeList)) {
            return Collections.emptyList();
        }
        DictionaryQuery query = new DictionaryQuery();
        query.setDictKey("黑灰名单-金融企业业务类型");
        Response<List<DictionaryDO>> dictResp = dictionaryService.getDictionaryListNoPage(query);
        if (CollectionUtil.isEmpty(dictResp.getData())) {
            return Collections.emptyList();
        }
        /*
            key=INFORMATION_RELATED, value={"10000393":"浙商证券","10000097":"浙商保险","10000396":"浙商租赁","zswl":"浙商未来科技","10000001":"省交通集团","10000079":"浙商金控"}转Map后提取KeySet
            key=INSURANCE_CATEGORY, value={"10000393":"浙商证券","10000097":"浙商保险","zswl":"浙商未来科技","10000001":"省交通集团","10000079":"浙商金控"}转Map后提取KeySet
            key=OTHER, value={"10000393":"浙商证券","10000097":"浙商保险","10000395":"浙商典当","10000396":"浙商租赁","zswl":"浙商未来科技","10000001":"省交通集团","10000079":"浙商金控"}转Map后提取KeySet
         */
        Map<String, Set<String>> dictMap = dictResp.getData().stream().collect(Collectors.toMap(DictionaryDO::getCode, e -> JSON.parseObject(e.getDisplay(), new TypeReference<HashMap<String, String>>() {}).keySet(), (k1, k2) -> k1));
        // 考虑所有机构，最终权限取并集
        Set<String> rootOrgCodes = currentUserOrgResolver.getUserDeptList().stream().map(OrgDO::getId).map(String::valueOf).collect(Collectors.toSet());

        List<String> finalBusinessTypes = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : dictMap.entrySet()) {
            String businessType = entry.getKey();
            Set<String> businessTypeOrgCodes = entry.getValue();
            businessTypeOrgCodes.retainAll(rootOrgCodes);
            if (CollectionUtil.isNotEmpty(businessTypeOrgCodes)) {
                finalBusinessTypes.add(businessType);
            }
        }
        return finalBusinessTypes;
    }

    /*@Override
    public List<BlackGrayLibCountDTO> countBlackGrayByApplyOrgAndSetCache(String businessType) {
        List<BlackGrayLibCountDTO> countDTOs = blackGrayLibraryMapper.blackGrayCountByApplyOrg(businessType);
        redisService.set(RedisKeyConstants.BLACK_GRAY_COUNT_BY_ORG_CACHE + StringUtils.defaultIfBlank(businessType, "all"), JSON.toJSONString(countDTOs), 60 * 5L); // 定时任务5分钟执行一次，缓存最多五分钟即可
        return countDTOs;
    }

    @Override
    public List<BlackGrayLibAllCountVo> countBlackGrayByAllAndSetCache(String businessType) {
        List<BlackGrayLibAllCountVo> allCountVos = blackGrayLibraryMapper.countAll(businessType);
        redisService.set(RedisKeyConstants.BLACK_GRAY_COUNT_BY_ALL_CACHE + StringUtils.defaultIfBlank(businessType, "all"), JSON.toJSONString(allCountVos), 60 * 5L); // 定时任务5分钟执行一次，缓存最多五分钟即可
        return allCountVos;
    }*/

    /*@Override
    public List<BlackGrayLibCountDTO> blackGrayCount(List<String> orgCodeList, String businessType) {
        Boolean ifJinkongAccount = ThreadContext.getUser().getIfJinkongAccount();
        Set<String> rootOrgCodes = gruulAuthService.batchGetRootOrgCode(orgCodeList);
        if (!ifJinkongAccount && CollectionUtil.isEmpty(orgCodeList)) {
            return Collections.emptyList();
        }

        List<BlackGrayLibCountDTO> countDTOs;
        // 优先读缓存，缓存拿不到再读库，sql较慢
        String countByOrgCache = redisService.get(RedisKeyConstants.BLACK_GRAY_COUNT_BY_ORG_CACHE + StringUtils.defaultIfBlank(businessType, "all"));
        if (StringUtils.isNotBlank(countByOrgCache)) {
            countDTOs = JSON.parseArray(countByOrgCache, BlackGrayLibCountDTO.class);
        } else {
            countDTOs = countBlackGrayByApplyOrgAndSetCache(businessType);
        }
        // 根据用户身份过滤数据
        if (!ifJinkongAccount) {
            // 金控用户能看到所有企业维度的数据，非金控用户要根据自己拥有的企业权限进行过滤
            countDTOs.removeIf(dto -> !rootOrgCodes.contains(dto.getOrgCode()));
        }

        // 需要展示哪些企业级别的卡片
        Set<String> cardOrgCodes = new HashSet<>(Arrays.asList(
                ConcentrationOrgEnum.ZSJK.getCode(),
                ConcentrationOrgEnum.ZSZQ.getCode(),
                ConcentrationOrgEnum.ZSBX.getCode(),
                ConcentrationOrgEnum.ZSZL.getCode(),
                ConcentrationOrgEnum.ZSHR.getCode(),
                ConcentrationOrgEnum.ZSDD.getCode()
        ));
        // 非金控用户取交集，根据自己拥有的机构权限过滤一下
        if (!ifJinkongAccount) {
            cardOrgCodes.retainAll(rootOrgCodes);
        }
        // 查出来如果没有对应企业的数据，也要留一个空卡片
        Map<String, BlackGrayLibCountDTO> showCardMap = new HashMap<>();
        cardOrgCodes.forEach(orgCode -> showCardMap.put(orgCode, new BlackGrayLibCountDTO().setOrgCode(orgCode).setOrgName(ConcentrationOrgEnum.findByCode(orgCode).getName())));
        // 把查出来的数据填充进卡片
        for (BlackGrayLibCountDTO countDTO : countDTOs) {
            if (showCardMap.containsKey(countDTO.getOrgCode())) {
                showCardMap.get(countDTO.getOrgCode())
                        .setBlackCount(countDTO.getBlackCount())
                        .setGrayCount(countDTO.getGrayCount());
            }
        }
        List<BlackGrayLibCountDTO> showCardList = Lists.newArrayList(showCardMap.values());

        if (ifJinkongAccount) {
                    *//*
            金控用户还要查一个全量，不同公司上报的黑灰名单企业可能有重复，因此不能直接通过上面的countDTOs累加，得重新查一遍
            如果遇到同一个企业不同机构上报的情况，要去重只算一次，黑灰等级按高的算，只要有黑算黑，只有灰算灰
         *//*
            List<BlackGrayLibAllCountVo> allCountVos;
            String countByAllCache = redisService.get(RedisKeyConstants.BLACK_GRAY_COUNT_BY_ALL_CACHE + StringUtils.defaultIfBlank(businessType, "all"));
            if (StringUtils.isNotBlank(countByAllCache)) {
                allCountVos = JSON.parseArray(countByAllCache, BlackGrayLibAllCountVo.class);
            } else {
                allCountVos = countBlackGrayByAllAndSetCache(businessType);
            }

            if (CollectionUtil.isNotEmpty(allCountVos)) {
                Map<String, Long> countMap = allCountVos.stream().collect(Collectors.toMap(BlackGrayLibAllCountVo::getBlackGrayType, BlackGrayLibAllCountVo::getCnt, (k1, k2) -> k1));
                if (countMap.containsKey(BlackGrayTypeEnum.BLACK_LIST.name()) || countMap.containsKey(BlackGrayTypeEnum.GRAY_LIST.name())) {
                    BlackGrayLibCountDTO allCountDTO = new BlackGrayLibCountDTO()
                            .setOrgCode("ALL")
                            .setOrgName("全量在库")
                            .setBlackCount(ObjectUtil.defaultIfNull(countMap.get(BlackGrayTypeEnum.BLACK_LIST.name()), 0L))
                            .setGrayCount(ObjectUtil.defaultIfNull(countMap.get(BlackGrayTypeEnum.GRAY_LIST.name()), 0L));
                    showCardList.add(allCountDTO);
                }
            }
        }

        // 金控名称特殊处理一下
        for (BlackGrayLibCountDTO countDTO : showCardList) {
            if (ConcentrationOrgEnum.ZSJK.getCode().equals(countDTO.getOrgCode())) {
                countDTO.setOrgName("浙商金控(本级)");
            }
        }
        return showCardList;
    }

    @Override
    public BasePage<BlackGrayGroupListRSP> blackGrayGroupList(BlackGrayGroupListREQ req, boolean needPage) {
        if (!ThreadContext.getUser().getIfJinkongAccount()) {
            return BasePage.emptyPage(0);
        }
        if (needPage) {
            PageHelper.startPage(req.getPage(), req.getPageSize());
        }
        // 先查集团列表中的数据
        List<BlackGrayGroupListRSP> list = blackGrayLibraryMapper.groupList(req);

        // 再单独统计下属企业在库数量，因为统计时需要排除集团主企业本身
        Set<String> groupNameSet = list.stream().map(BlackGrayGroupListRSP::getGroupName).collect(Collectors.toSet());
        Map<String, Integer> entCountMap;
        if (CollectionUtil.isEmpty(groupNameSet)) {
            entCountMap = Collections.emptyMap();
        } else {
            List<GroupCompanyInStockCountVo> countVos = blackGrayLibraryMapper.groupCompanyInStockCount(groupNameSet, req.getBusinessType());
            entCountMap = countVos.stream().collect(Collectors.toMap(GroupCompanyInStockCountVo::getMembershipGroup, GroupCompanyInStockCountVo::getEntCount, (k1,k2)->k1));
        }

        list.forEach(e->{
            if (entCountMap.containsKey(e.getGroupName())) {
                e.setEntCount(entCountMap.get(e.getGroupName()));
            }
        });

        PageInfo<BlackGrayGroupListRSP> pageInfo = new PageInfo<>(list);
        return new BasePage<>(pageInfo.getTotal(),list);
    }

    @Override
    public BlackGrayGroupDetailRSP blackGrayGroupDetail(String groupName) {
        // 集团主企业信息
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo(BlackGrayLibrary.ENTERPRISE_NAME, groupName)
                .andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0)
                .andEqualTo(BlackGrayLibrary.GROUP_LEADER_FLAG, true);
        // 外部提前上传的名单，不管share_type。 内部提交入库等方式的数据，只看share_type=0的。如保险入库一条企业灰名单，会在library中产生两条数据，share_type分别是0(金融企业灰名单)和1(金控黑名单)， 这里只查share_type=0的原始入库记录，去重
        criteria.andCondition("(source = 'EXTERNAL_UPLOAD' or (source !='EXTERNAL_UPLOAD' AND share_type = 0))");

        List<BlackGrayLibrary> list = blackGrayLibraryMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 1));
        if (CollectionUtil.isEmpty(list)) {
            log.warn("所选集团企业不存在，或非集团主企业");
            throw new BusException("所选集团不存在入库记录，请刷新页面后重试");
        }

        // 这里只获取集团主企业基本信息，随便哪条black_gray_library记录都一样，所以只查一条即可
        BlackGrayLibrary groupLeader = list.get(0);
        return new BlackGrayGroupDetailRSP()
                .setId(groupLeader.getId())
                .setGroupName(groupLeader.getEnterpriseName())
                .setGroupCreditCode(groupLeader.getUnifiedSocialCreditCode());
    }

    @Override
    public BasePage<GroupInStockListRSP> groupStockList(GroupStockListREQ req, boolean needPage) {
        Example example = new Example(BlackGrayLibrary.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo(BlackGrayLibrary.ENTERPRISE_NAME, req.getGroupName())
                .andEqualTo(BlackGrayLibrary.BUSINESS_TYPE, req.getBusinessType())
                .andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0);
        // 外部提前上传的名单，不管share_type。 内部提交入库等方式的数据，只看share_type=0的。如保险入库一条企业灰名单，会在library中产生两条数据，share_type分别是0(金融企业灰名单)和1(金控黑名单)， 这里只查share_type=0的原始入库记录，去重
        criteria.andCondition("(source = 'EXTERNAL_UPLOAD' or (source !='EXTERNAL_UPLOAD' AND share_type = 0))");
        example.orderBy(BlackGrayLibrary.UPDATE_TIME).desc();
        if (needPage) {
            PageHelper.startPage(req.getPage(), req.getPageSize());
        }
        List<BlackGrayLibrary> list = blackGrayLibraryMapper.selectByExample(example);
        PageInfo<BlackGrayLibrary> pageInfo = new PageInfo<>(list);

        List<String> applyReasonNums = new ArrayList<>();
        list.stream()
                .filter(e->StringUtils.isNotBlank(e.getApplyReason()))
                .forEach(e->applyReasonNums.addAll(JSONUtil.toList(e.getApplyReasonType(), String.class)));

        Map<String, String> applyReasonNameMap;
        if (CollectionUtil.isNotEmpty(applyReasonNums)) {
            applyReasonNameMap = blackGrayWarehouseRuleConfigService.num2NameBatch(applyReasonNums);
        } else {
            applyReasonNameMap = Collections.emptyMap();
        }

        List<GroupInStockListRSP> rspList = list.stream().map(e -> {
            GroupInStockListRSP rsp = new GroupInStockListRSP();
            BeanUtils.copyProperties(e, rsp);
            if (StringUtils.isNotBlank(e.getApplyReasonType())) {
                rsp.setApplyReason(this.convertApplyReasonTypes2Name(e.getApplyReasonType(), applyReasonNameMap));
            }
            return rsp;
        }).collect(Collectors.toList());

        return new BasePage<>(needPage ? pageInfo.getTotal() : 0, rspList);
    }

    @Override
    public BasePage<BlackGrayLibraryDistinctListRSP> groupCompanyStockList(GroupStockListREQ req, boolean needPage) {
        if (needPage) {
            PageHelper.startPage(req.getPage(), req.getPageSize());
        }
        List<BlackGrayLibraryDistinctListRSP> rspList = blackGrayLibraryMapper.groupCompanyStockList(req.getGroupName(), req.getBusinessType());
        PageInfo<BlackGrayLibraryDistinctListRSP> pageInfo = new PageInfo<>(rspList);
        return new BasePage<>(pageInfo.getTotal(), rspList);
    }*/

    private String convertApplyReasonTypes2Name(String applyReasonType, Map<String, String> applyReasonNameMap) {
        if (StringUtils.isBlank(applyReasonType) || CollectionUtil.isEmpty(applyReasonNameMap)) {
            return null;
        }
        List<String> types = JSONUtil.toList(applyReasonType, String.class);
        Set<String> names = new HashSet<>();
        types.stream().filter(applyReasonNameMap::containsKey).forEach(type -> names.add(applyReasonNameMap.get(type)));

        if (names.isEmpty()) {
            return null;
        }
        return String.join(",", names);
    }


    /**
     * 补充所属集团
     */
    private void supplyGroupInfo(BlackGrayLibrary blackGrayLibrary) {
        AffiliatedEnterpriseSearchREQ supplyReq = new AffiliatedEnterpriseSearchREQ()
                .setEnterpriseName(blackGrayLibrary.getEnterpriseName())
                .setUnifiedSocialCreditCode(blackGrayLibrary.getUnifiedSocialCreditCode());
        List<AssociatedEnterpriseBatchSearchRSP> associatedRSPList = blackGrayExternalDataService.batchAffiliatedEnterpriseSearch(Collections.singletonList(supplyReq));
        if (CollectionUtil.isEmpty(associatedRSPList)) {
            return;
        }
        AssociatedEnterpriseBatchSearchRSP searchRSP = associatedRSPList.get(0);
        //集团黑灰标识
        if (ObjectUtil.equals(searchRSP.getIsAffiliated(), 1)) {
            blackGrayLibrary.setBlacklistStatus(true);
            blackGrayLibrary.setGroupBlackGrayType(blackGrayLibrary.getBlackGrayType());
        } else {
            blackGrayLibrary.setBlacklistStatus(false);
        }
    }

    /**
     * 自动出库黑灰名单
     **/
    @XxlJob("blackGrayAutoOutboundJob")
    @Transactional(rollbackFor = Throwable.class)
    public void blackGrayAutoOutboundJob() {
        try {
            // 有出库时间的自动出库
            Example example = new Example(BlackGrayLibrary.class);
            example.createCriteria()
                    .andEqualTo(BlackGrayLibrary.STOCK_STATUS, 0)
                    .andIsNotNull(BlackGrayLibrary.PLAN_OUTBOUND_TIME)
                    .andLessThan(BlackGrayLibrary.PLAN_OUTBOUND_TIME, new Date());
            List<BlackGrayLibrary> blackGrayLibraryList = blackGrayLibraryMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(blackGrayLibraryList)) {
                //暂时无校验，不确定后边会不会
                blackGrayLibraryList.forEach(blackGrayLibrary -> blackGrayLibrary.setStockStatus(1));
                blackGrayLibraryList.forEach(blackGrayLibraryMapper::updateByPrimaryKeySelective);
            }
        } catch (Exception e) {
            log.error("blackGrayAutoOutboundJob error", e);
        }
    }


    private void circulateBuildBlackGrayLibrary(BlackGrayLibrary modelTemplate, List<BlackGrayLibrary> blackGrayLibraryList, List<AssociatedEnterpriseSearchRSP> associatedEnterprises){
        if(associatedEnterprises == null){
            return;
        }
        associatedEnterprises.forEach(associatedEnterpriseSearchRSP -> {
            if(associatedEnterpriseSearchRSP.getEnterpriseName() != null && associatedEnterpriseSearchRSP.getUnifiedSocialCreditCode() != null){
                BlackGrayLibrary blackGrayLibrary = BeanUtil.copyProperties(modelTemplate, BlackGrayLibrary.class, "id", "enterpriseName", "unifiedSocialCreditCode");
                blackGrayLibrary.setEnterpriseName(associatedEnterpriseSearchRSP.getEnterpriseName());
                blackGrayLibrary.setUnifiedSocialCreditCode(associatedEnterpriseSearchRSP.getUnifiedSocialCreditCode());
                blackGrayLibrary.setSource(BlackGraySourceEnum.GROUP_RADIATION.name());
                blackGrayLibrary.setGroupLeaderFlag(blackGrayLibrary.getEnterpriseName().equals(blackGrayLibrary.getMembershipGroup())); // 是否集团主企业，根据集团控制者名称和当前企业名称来区分
                blackGrayLibraryList.add(blackGrayLibrary);
            }
            circulateBuildBlackGrayLibrary(modelTemplate, blackGrayLibraryList, associatedEnterpriseSearchRSP.getChildren());
        });
    }

    //入库规则里有金控即认为是符合金控的
    private void getShareType(BlackGrayLibrary blackGrayLibrary){
        if(ObjectUtil.isNotEmpty(blackGrayLibrary.getApplyReasonType())){
            List<String> numList = JSONUtil.toList(blackGrayLibrary.getApplyReasonType(), String.class);
            Map<String, BlackGrayWarehouseRuleConfig> stringBlackGrayWarehouseRuleConfigMap = blackGrayWarehouseRuleConfigService.num2BeanBatch(numList);
            for (String num : numList) {
                BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig = stringBlackGrayWarehouseRuleConfigMap.get(num);
                if (ObjectUtil.isNotEmpty(blackGrayWarehouseRuleConfig) && ObjectUtil.isNotEmpty(blackGrayWarehouseRuleConfig.getSuitOrg())) {
                    List<String> orgs = JSONUtil.toList(blackGrayWarehouseRuleConfig.getSuitOrg(), String.class);
                    if (orgs.contains(SystemSupportService.ZSJK_CODE)) {
                        blackGrayLibrary.setShareType(1);
                        return;
                    }
                }
            }
        }
        blackGrayLibrary.setShareType(0);
    }

    //填充优先级
    private void getSort(BlackGrayLibrary blackGrayLibrary) {
        if (BlackGraySourceEnum.isExternal(blackGrayLibrary.getSource())) {
            //外部数据，优先级最高
            if (BlackGrayTypeEnum.BLACK_LIST.name().equals(blackGrayLibrary.getBlackGrayType())) {
                blackGrayLibrary.setBlackGraySort(10);
            } else {
                blackGrayLibrary.setBlackGraySort(40);
            }
        } else {
            if (ObjectUtil.equals(blackGrayLibrary.getShareType(), 1)) {
                //金控
                if (BlackGrayTypeEnum.BLACK_LIST.name().equals(blackGrayLibrary.getBlackGrayType())) {
                    blackGrayLibrary.setBlackGraySort(20);
                } else {
                    blackGrayLibrary.setBlackGraySort(50);
                }
            } else {
                //金融企业自有
                if (BlackGrayTypeEnum.BLACK_LIST.name().equals(blackGrayLibrary.getBlackGrayType())) {
                    blackGrayLibrary.setBlackGraySort(30);
                } else {
                    blackGrayLibrary.setBlackGraySort(60);
                }
            }
        }
    }
}
