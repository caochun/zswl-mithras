package cn.zswltech.mithras.blackgray.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.external.AffiliatedEnterpriseSearchREQ;
import cn.zswltech.mithras.blackgray.dto.external.AffiliatedEnterpriseSearchRSP;
import cn.zswltech.mithras.blackgray.dto.external.AssociatedEnterpriseBatchSearchRSP;
import cn.zswltech.mithras.blackgray.dto.req.*;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBusinessTypeRsp;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRecordDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseRecordListRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGraySourceEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayBusinessDictMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRuleConfigMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseTaskMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRecord;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRuleConfig;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseTask;
import cn.zswltech.mithras.blackgray.service.BlackGrayExternalDataService;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRecordService;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRuleConfigService;
import cn.zswltech.mithras.blackgray.service.GruulAuthService;
import cn.zswltech.mithras.blackgray.utils.StringUtils;
import cn.zswltech.mithras.blackgray.vo.BlackGrayApplyReasonVo;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.CurrentUserOrgResolver;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
* @description 黑灰名单记录表
* @author
* @date 2023-11-28
*/

@Service
@Slf4j
public class BlackGrayWarehouseRecordServiceImpl implements BlackGrayWarehouseRecordService {

    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;
    @Resource
    private BlackGrayBusinessDictMapper blackGrayBusinessDictMapper;
    @Resource
    private BlackGrayWarehouseRuleConfigMapper blackGrayWarehouseRuleConfigMapper;
    @Resource
    private BlackGrayWarehouseRuleConfigService blackGrayWarehouseRuleConfigService;
    @Resource
    private BlackGrayWarehouseTaskMapper blackGrayWarehouseTaskMapper;
    @Resource(name = "blackGrayHSExternalDataService")
    private BlackGrayExternalDataService blackGrayExternalDataService;
    @Resource
    private GruulAuthService gruulAuthService;
    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private final static String REDIS_CACHE_APPLY_REASON_KEY = "REDIS_CACHE_APPLY_REASON_KEY";
    private final static String BUSINESS_TYPE = "BREAK_BUSINESS";
    private final static String APPLY_REASON = "APPLY_REASON";



    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long add(BlackGrayWarehouseRecordAddREQ req) {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if(ObjectUtil.isEmpty(loginInfo)){
            throw new MithrasException("登录信息不存在");
        }
        //填充部门
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if (rootOrg == null) {
            throw new MithrasException("当前用户无机构");
        }
        List<OrgDO> deptDos = currentUserOrgResolver.getUserDeptList();
        Map<String, BlackGrayWarehouseRecord> oldMap = new HashMap<>();
        //判断是否需要去重
        if(ObjectUtil.isNotEmpty(req.getTaskNum())){
            Example example = new Example(BlackGrayWarehouseRecord.class);
            example.createCriteria().andEqualTo(BlackGrayWarehouseRecord.TASK_NUM, req.getTaskNum());
            List<BlackGrayWarehouseRecord> blackGrayWarehouseRecord = blackGrayWarehouseRecordMapper.selectByExample(example);
            if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord)){
                oldMap = blackGrayWarehouseRecord.stream().collect(Collectors.toMap(this::getBusinessKey, e -> e, (a, b) -> b));
            }
            LambdaUpdateWrapper<BlackGrayWarehouseTask> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(BlackGrayWarehouseTask::getGmtUpdate, new Date());
            updateWrapper.eq(BlackGrayWarehouseTask::getTaskNum, req.getTaskNum());
            blackGrayWarehouseTaskMapper.update(null, updateWrapper);
        }
        BlackGrayWarehouseRecord info = buildBlackGrayWarehouseRecord(req, loginInfo, rootOrg, CollectionUtil.isEmpty(deptDos) ? null : deptDos.get(0), oldMap);
        if(ObjectUtil.isNotEmpty(info.getId())){
            blackGrayWarehouseRecordMapper.updateByPrimaryKey(info);
        } else {
            blackGrayWarehouseRecordMapper.insertSelective(info);
        }

        return info.getId();
    }

    @Override
    public void batchAdd(List<BlackGrayWarehouseRecordAddREQ> reqs) {
        if(CollectionUtil.isEmpty(reqs)){
            return;
        }
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if(ObjectUtil.isEmpty(loginInfo)){
            throw new MithrasException("登录信息不存在");
        }
        //填充部门
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if (rootOrg == null) {
            throw new MithrasException("当前用户无机构");
        }
        List<OrgDO> deptDos = currentUserOrgResolver.getUserDeptList();
        List<BlackGrayWarehouseRecord> list = new ArrayList<>();
        reqs.forEach(req -> {
            Map<String, BlackGrayWarehouseRecord> oldMap = new HashMap<>();
            //判断是否需要去重
            if(ObjectUtil.isNotEmpty(req.getTaskNum())){
                Example example = new Example(BlackGrayWarehouseRecord.class);
                example.createCriteria().andEqualTo(BlackGrayWarehouseRecord.TASK_NUM, req.getTaskNum());
                List<BlackGrayWarehouseRecord> blackGrayWarehouseRecord = blackGrayWarehouseRecordMapper.selectByExample(example);
                if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord)){
                    oldMap = blackGrayWarehouseRecord.stream().collect(Collectors.toMap(this::getBusinessKey, e -> e, (a, b) -> b));
                }
                LambdaUpdateWrapper<BlackGrayWarehouseTask> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(BlackGrayWarehouseTask::getGmtUpdate, new Date());
                updateWrapper.eq(BlackGrayWarehouseTask::getTaskNum, req.getTaskNum());
                blackGrayWarehouseTaskMapper.update(null, updateWrapper);
            }
            BlackGrayWarehouseRecord blackGrayWarehouseRecord = buildBlackGrayWarehouseRecord(req, loginInfo, rootOrg, CollectionUtil.isEmpty(deptDos) ? null : deptDos.get(0), oldMap);
            if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord.getId())){
                blackGrayWarehouseRecordMapper.updateByPrimaryKey(blackGrayWarehouseRecord);
            } else {
                list.add(blackGrayWarehouseRecord);
            }
        });
        if(ObjectUtil.isNotEmpty(list)){
            blackGrayWarehouseRecordMapper.insertList(list);
        }
    }

    @Override
    public void batchModify(BlackGrayWarehouseRecordSupplyREQ req) {
        BlackGrayWarehouseTask blackGrayWarehouseTask = blackGrayWarehouseTaskMapper.queryByTaskNum(req.getTaskNum());
        if (blackGrayWarehouseTask == null) {
            throw new MithrasException("任务不存在，请检查任务编号");
        }
        List<BlackGrayWarehouseRecord> supplyRecords = null;
        if (CollectionUtils.isNotEmpty(req.getModifyItems())) {
            List<Long> recordIds = req.getModifyItems().stream().map(item -> item.getId()).collect(Collectors.toList());
            Example example = new Example(BlackGrayWarehouseRecord.class);
            example.createCriteria().andIn(BlackGrayWarehouseRecord.ID, recordIds);
            supplyRecords = blackGrayWarehouseRecordMapper.selectByExample(example);
        } else {
            Example example = new Example(BlackGrayWarehouseRecord.class);
            example.createCriteria().andEqualTo(BlackGrayWarehouseRecord.TASK_NUM, req.getTaskNum());
            supplyRecords = blackGrayWarehouseRecordMapper.selectByExample(example);
        }
        List<AffiliatedEnterpriseSearchREQ> supplyReqs = BeanUtil.copyToList(supplyRecords, AffiliatedEnterpriseSearchREQ.class);
        List<AssociatedEnterpriseBatchSearchRSP> associatedRSPList = blackGrayExternalDataService.batchAffiliatedEnterpriseSearch(supplyReqs);
        Map<String, AssociatedEnterpriseBatchSearchRSP> searchMap = associatedRSPList.stream().collect(Collectors.toMap(
                AssociatedEnterpriseBatchSearchRSP::getUnifiedSocialCreditCode, Function.identity(), (a, b) -> b));
        for (BlackGrayWarehouseRecord supplyRecord : supplyRecords) {
            AssociatedEnterpriseBatchSearchRSP searchRSP = searchMap.get(supplyRecord.getUnifiedSocialCreditCode());
            if (searchRSP == null) {
                continue;
            }
            BlackGrayWarehouseRecord updateRecord = new BlackGrayWarehouseRecord();
            updateRecord.setId(supplyRecord.getId());
            updateRecord.setBlacklistStatus(searchRSP.getIsAffiliated());
            //集团黑灰标识
            if (ObjectUtil.equals(updateRecord.getBlacklistStatus(), 1)) {
                updateRecord.setGroupBlackGrayType(supplyRecord.getBlackGrayType());
            }
            updateRecord.setMembershipGroup(searchRSP.getGroupEnterpriseName());
            updateRecord.setGroupCreditCode(searchRSP.getGroupCreditCode());
            updateRecord.setGroupLeaderFlag(searchRSP.getIsAffiliated() != null && searchRSP.getIsAffiliated().equals(1));
            blackGrayWarehouseRecordMapper.updateByPrimaryKeySelective(updateRecord);
        }
        // 更新任务集团信息补充状态
        blackGrayWarehouseTaskMapper.updateIsSupplyGroupInfoInt(blackGrayWarehouseTask.getId(), true);
    }

    private BlackGrayWarehouseRecord buildBlackGrayWarehouseRecord(BlackGrayWarehouseRecordAddREQ req, AccountVO loginInfo, OrgDO orgDo, OrgDO deptDo, Map<String, BlackGrayWarehouseRecord> oldMap){
        Date date = new Date();
        BlackGrayWarehouseRecord info = BeanUtil.copyProperties(req, BlackGrayWarehouseRecord.class, "warehouseFileKeys", "rectifyFileKeys", "applyReasonType", "reportFlag");
        info.setAuditStatus((int) AuditStatusEnum.WAIT.getCode());
        info.setApplyReasonType(JSONUtil.toJsonStr(req.getApplyReasonType()));
        info.setCreateBy(loginInfo.getId());
        info.setUpdateBy(loginInfo.getId());
        info.setCreateTime(date);
        info.setUpdateTime(date);
        info.setApplyTime(date);
        info.setWarehouseFileKeys(JSONUtil.toJsonStr(req.getWarehouseFileKeys()));
        info.setRectifyFileKeys(JSONUtil.toJsonStr(req.getRectifyFileKeys()));
        if(ObjectUtil.isNotEmpty(orgDo)){
            info.setApplyOrganization(orgDo.getCode());
        }
        if(ObjectUtil.isNotEmpty(deptDo)){
            info.setApplyDept(deptDo.getCode());
        }
        //集团黑灰标识
        if(ObjectUtil.equals(info.getBlacklistStatus(), 1)){
            info.setGroupBlackGrayType(info.getBlackGrayType());
        }
        //填充id
        BlackGrayWarehouseRecord blackGrayWarehouseRecord = oldMap.get(getBusinessKey(info));
        if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord)){
            info.setId(blackGrayWarehouseRecord.getId());
        }
        info.setGroupLeaderFlag(false); // 非空字段，先设置false，识别集团后填充
        info.setReportFlag(StrUtil.equalsAny(req.getReportFlag(), "上报", "1") ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
        return info;
    }

    private String getBusinessKey(BlackGrayWarehouseRecord blackGrayWarehouseRecord){
        return String.join("-", blackGrayWarehouseRecord.getTaskNum(), blackGrayWarehouseRecord.getUnifiedSocialCreditCode(), blackGrayWarehouseRecord.getBusinessType(), blackGrayWarehouseRecord.getBlackGrayType(), blackGrayWarehouseRecord.getApplyReasonType());
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveBatch(List<BlackGrayWarehouseRecord> records) {
        //todo 校验
        if(records != null) {
            blackGrayWarehouseRecordMapper.insertList(records);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(BlackGrayWarehouseRecordModifyREQ req) {
        BlackGrayWarehouseRecord info = blackGrayWarehouseRecordMapper.selectByPrimaryKey(req.getId());
        if (ObjectUtil.isNull(info)) {
            throw new MithrasException("编辑的黑灰名单记录不存在，请刷新页面后重试");
        }
        copyPropertiesIgnoreNull(req, info, "warehouseFileKeys", "rectifyFileKeys", "applyReasonType");

        info.setApplyReasonType(JSONUtil.toJsonStr(req.getApplyReasonType()));
        info.setWarehouseFileKeys(JSONUtil.toJsonStr(req.getWarehouseFileKeys()));
        info.setRectifyFileKeys(JSONUtil.toJsonStr(req.getRectifyFileKeys()));
        info.setUpdateTime(new Date());


        AffiliatedEnterpriseSearchREQ groupReq = new AffiliatedEnterpriseSearchREQ();
        groupReq.setEnterpriseName(req.getEnterpriseName());
        AffiliatedEnterpriseSearchRSP groupResp = blackGrayExternalDataService.affiliatedEnterpriseSearch(groupReq);

        // 判断是否是集团主企业
        if (org.apache.commons.lang3.StringUtils.isNotBlank(groupResp.getGroupEnterpriseName()) && groupResp.getGroupEnterpriseName().equals(req.getEnterpriseName())) {
            info.setGroupLeaderFlag(true);
            info.setGroupBlackGrayType(info.getBlackGrayType());
        } else {
            info.setGroupLeaderFlag(false);
            info.setGroupBlackGrayType(null);
        }
        info.setMembershipGroup(groupResp.getGroupEnterpriseName());
        info.setGroupCreditCode(groupResp.getGroupCreditCode());
        info.setReportFlag(req.getReportFlag());

//        //集团黑灰标识
//        if(ObjectUtil.equals(info.getBlacklistStatus(), 1)){
//            info.setGroupBlackGrayType(info.getBlackGrayType());
//        }

        if(ObjectUtil.isNotEmpty(info.getTaskNum())){
            LambdaUpdateWrapper<BlackGrayWarehouseTask> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(BlackGrayWarehouseTask::getGmtUpdate, new Date());
            updateWrapper.eq(BlackGrayWarehouseTask::getTaskNum, info.getTaskNum());
            blackGrayWarehouseTaskMapper.update(null, updateWrapper);
        }
        blackGrayWarehouseRecordMapper.updateByPrimaryKey(info);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreFields) {
        List<String> ignoreFiledsList = ignoreFields == null ? Collections.emptyList() : Arrays.asList(ignoreFields);
        BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        for (java.beans.PropertyDescriptor pd : pds) {
            if (ignoreFiledsList.contains(pd.getName()) || "class".equals(pd.getName())) {
                continue;
            }
            if (src.getPropertyValue(pd.getName()) != null) {
                BeanWrapper trg = new BeanWrapperImpl(target);
                trg.setPropertyValue(pd.getName(), src.getPropertyValue(pd.getName()));
            }
        }
    }

    @Override
    public PageR<BlackGrayWarehouseRecordListRSP> list(BlackGrayWarehouseRecordListREQ req) {
        Example example = getListExample(req);
        List<BlackGrayWarehouseRecord> blackGrayWarehouseRecords;
        if(ObjectUtil.equals(req.getIsHistory(), 0)){
            example.orderBy(" updateTime ").desc();
            example.orderBy(" id ").desc();
            PageHelper.startPage(req.getPage(), req.getPageSize());
            blackGrayWarehouseRecords = blackGrayWarehouseRecordMapper.selectByExample(example);
            //这里又不让去重了，保留，防止反复
           /* if(CollectionUtil.isNotEmpty(blackGrayWarehouseRecords)){
                //保留最新
                //todo 方式不优雅，待优化
                Example newExample = new Example(BlackGrayWarehouseRecord.class);
                newExample.createCriteria()
                        .andIn(BlackGrayWarehouseRecord.ID, blackGrayWarehouseRecords.stream().collect(Collectors.toMap(this::getBlackGrayCode, e -> e, (a, b) -> a)).values().stream().map(BlackGrayWarehouseRecord::getId).collect(Collectors.toList()));
                newExample.orderBy("warehouseTime").desc();
                newExample.orderBy("id").desc();
                PageHelper.startPage(req.getPage(), req.getPageSize());
                blackGrayWarehouseRecords = blackGrayWarehouseRecordMapper.selectByExample(newExample);
            }*/
        } else if (StrUtil.equalsAny(req.getSource(), BlackGraySourceEnum.INTERNAL_UPLOAD.name(), BlackGraySourceEnum.EXTERNAL_UPLOAD.name())) {
            example.orderBy(" updateTime ").desc();
            blackGrayWarehouseRecords = blackGrayWarehouseRecordMapper.selectByExample(example);
            if(CollectionUtil.isNotEmpty(blackGrayWarehouseRecords)){
                //保留最新
                //todo 方式不优雅，待优化
                Example newExample = new Example(BlackGrayWarehouseRecord.class);
                newExample.createCriteria()
                        .andIn(BlackGrayWarehouseRecord.ID, blackGrayWarehouseRecords.stream().collect(Collectors.toMap(this::getBlackGrayCode, e -> e, (a, b) -> a)).values().stream().map(BlackGrayWarehouseRecord::getId).collect(Collectors.toList()));
                newExample.orderBy("warehouseTime").desc();
                newExample.orderBy("id").desc();
                PageHelper.startPage(req.getPage(), req.getPageSize());
                blackGrayWarehouseRecords = blackGrayWarehouseRecordMapper.selectByExample(newExample);
            }
        } else {
            example.orderBy(" updateTime ").desc();
            PageHelper.startPage(req.getPage(), req.getPageSize());
            blackGrayWarehouseRecords = blackGrayWarehouseRecordMapper.selectByExample(example);
        }
        List<BlackGrayWarehouseRecordListRSP> blackGrayWarehouseRecordListRSPS = new ArrayList<>();
        List<String> ruleNums = new ArrayList<>();
        blackGrayWarehouseRecords.forEach(record -> {
            BlackGrayWarehouseRecordListRSP blackGrayWarehouseRecordListRSP = BeanUtil.copyProperties(record, BlackGrayWarehouseRecordListRSP.class, "applyReasonType");
            if(ObjectUtil.isNotEmpty(record.getApplyReasonType())){
                blackGrayWarehouseRecordListRSP.setApplyReasonType(JSONUtil.toList(record.getApplyReasonType(), String.class));
                ruleNums.addAll(blackGrayWarehouseRecordListRSP.getApplyReasonType());
            }
            blackGrayWarehouseRecordListRSPS.add(blackGrayWarehouseRecordListRSP);
        });
        Map<String, String> map = blackGrayWarehouseRuleConfigService.num2NameBatch(ruleNums);
        PageInfo<BlackGrayWarehouseRecord> blackGrayWarehouseRecordPageInfo = new PageInfo<>(blackGrayWarehouseRecords);
        //填充审批信息
        //Map<Long, String> currentOperator = getCurrentOperator(blackGrayWarehouseRecordListRSPS.stream().map(BlackGrayWarehouseRecordListRSP::getId).collect(Collectors.toList()));
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
        return PageR.of(blackGrayWarehouseRecordListRSPS, blackGrayWarehouseRecordPageInfo.getTotal());
    }



    private Example getListExample(BlackGrayWarehouseRecordListREQ req){
        //填充部门
        Example example = new Example(BlackGrayWarehouseRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if(ObjectUtil.equals(req.getIsHistory(), 0)){
            criteria.andEqualTo(BlackGrayWarehouseRecord.RECORD_STATUS, AuditStatusEnum.FINISH.getCode());
        }
        if(ObjectUtil.equals(req.getIsPendingProcess(), 0)){
            criteria.andNotEqualTo(BlackGrayWarehouseRecord.RECORD_STATUS, AuditStatusEnum.WAIT.getCode());
        }
        if (ObjectUtil.isNotEmpty(req.getApplyOrganization())){
            criteria.andEqualTo(BlackGrayWarehouseRecord.APPLY_ORGANIZATION, req.getApplyOrganization());
        }
        if(req.getEnterpriseName() != null){
            criteria.andLike(BlackGrayWarehouseRecord.ENTERPRISE_NAME, "%" + req.getEnterpriseName() + "%");
        }
        if(req.getUnifiedSocialCreditCode() != null){
            criteria.andEqualTo(BlackGrayWarehouseRecord.UNIFIED_SOCIAL_CREDIT_CODE, req.getUnifiedSocialCreditCode());
        }
        if(req.getBusinessType() != null){
            criteria.andEqualTo(BlackGrayWarehouseRecord.BUSINESS_TYPE, req.getBusinessType());
        }
        if(req.getBlackGrayType() != null){
            criteria.andEqualTo(BlackGrayWarehouseRecord.BLACK_GRAY_TYPE, req.getBlackGrayType());
        }
        if(req.getSource() != null){
            criteria.andEqualTo(BlackGrayWarehouseRecord.SOURCE, req.getSource());
        }
        if(req.getIds() != null){
            criteria.andIn(BlackGrayWarehouseRecord.ID, req.getIds());
        }
        if(req.getApplyTimeFrom() != null){
            criteria.andBetween(BlackGrayWarehouseRecord.APPLY_TIME, req.getApplyTimeFrom(), req.getApplyTimeTo());
        }
        if(req.getAuditStatus() != null){
            criteria.andEqualTo(BlackGrayWarehouseRecord.RECORD_STATUS, req.getAuditStatus());
        }
        if(req.getWarehouseTimeFrom() != null){
            criteria.andBetween(BlackGrayWarehouseRecord.WAREHOUSE_TIME, req.getWarehouseTimeFrom(), req.getWarehouseTimeTo());
        }
        if(req.getPlanOutboundTimeFrom() != null){
            criteria.andBetween(BlackGrayWarehouseRecord.PLAN_OUTBOUND_TIME, req.getPlanOutboundTimeFrom(), req.getPlanOutboundTimeTo());
        }
        if(req.getTaskNum() != null){
            criteria.andEqualTo(BlackGrayWarehouseRecord.TASK_NUM, req.getTaskNum());
        }
        return example;
    }

    private String getBlackGrayCode(BlackGrayWarehouseRecord record){
        return String.join("-", record.getEnterpriseName(), record.getUnifiedSocialCreditCode(), record.getBusinessType());
    }

    @Override
    public BlackGrayWarehouseRecordDetailRSP detail(Long id) {
        BlackGrayWarehouseRecord blackGrayWarehouseRecord = blackGrayWarehouseRecordMapper.selectByPrimaryKey(id);
        if(blackGrayWarehouseRecord == null){
            return null;
        }
        BlackGrayWarehouseRecordDetailRSP blackGrayWarehouseRecordDetailRSP = BeanUtil.copyProperties(blackGrayWarehouseRecord, BlackGrayWarehouseRecordDetailRSP.class, "warehouseFileKeys", "rectifyFileKeys", "applyReasonType");
        blackGrayWarehouseRecordDetailRSP.setWarehouseFileKeys(JSONUtil.toList(blackGrayWarehouseRecord.getWarehouseFileKeys(), String.class));
        blackGrayWarehouseRecordDetailRSP.setRectifyFileKeys(JSONUtil.toList(blackGrayWarehouseRecord.getRectifyFileKeys(), String.class));
        blackGrayWarehouseRecordDetailRSP.setCreateByCode(gruulAuthService.getAccountById(blackGrayWarehouseRecordDetailRSP.getCreateBy()));
        //AuditTask auditTasksByBizId = blackGrayWarehouseAuditService.getAuditTasksByBizId(AuditBizTypeEnum.BLACK_GRAY_WAREHOUSE.getType(), id);
        //blackGrayWarehouseRecordDetailRSP.setAuditTaskId(auditTasksByBizId == null ? null : auditTasksByBizId.getId());
        if(ObjectUtil.isNotEmpty(blackGrayWarehouseRecord.getApplyReasonType())){
            blackGrayWarehouseRecordDetailRSP.setApplyReasonType((JSONUtil.toList(blackGrayWarehouseRecord.getApplyReasonType(), String.class)));
            List<String> applyReasonTypeName = new ArrayList<>();
            Map<String, String> map = blackGrayWarehouseRuleConfigService.num2NameBatch(blackGrayWarehouseRecordDetailRSP.getApplyReasonType());
            for(int i = 0; i < blackGrayWarehouseRecordDetailRSP.getApplyReasonType().size(); i++){
                applyReasonTypeName.add(map.get(blackGrayWarehouseRecordDetailRSP.getApplyReasonType().get(i)));
            }
            blackGrayWarehouseRecordDetailRSP.setApplyReasonName(applyReasonTypeName);
        }
        return blackGrayWarehouseRecordDetailRSP;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(BlackGrayWarehouseRecordRemoveREQ req) {
        Example example = new Example(BlackGrayWarehouseRecord.class);
        example.createCriteria().andIn(BlackGrayWarehouseRecord.ID, req.getIds());
        blackGrayWarehouseRecordMapper.deleteByExample(example);
    }

    @Override
    public List<BlackGrayBusinessTypeRsp> listBusinessType(BlackGrayBusinessTypeReq req) {
        List<BlackGrayBusinessTypeRsp> baseDict = blackGrayBusinessDictMapper.getBaseDict(BUSINESS_TYPE);
        if(ObjectUtil.isEmpty(baseDict)){
            throw new MithrasException("当前无相关业务类型");
        }
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if (rootOrg == null) {
            throw new MithrasException("当前用户无机构");
        }
        //非金控，补充各自
        Map<Long, List<BlackGrayBusinessTypeRsp>> businessTypeMap = blackGrayBusinessDictMapper.getTree(rootOrg.getId(), baseDict.stream().map(BlackGrayBusinessTypeRsp::getId).collect(Collectors.toList()), BUSINESS_TYPE).stream().collect(Collectors.groupingBy(BlackGrayBusinessTypeRsp::getParentId));
        baseDict.forEach(base -> {
            base.setChild(businessTypeMap.get(base.getId()));
        });
        return baseDict;
    }

    @Override
    public void updateStatue(Long id, Integer status) {
        BlackGrayWarehouseRecord blackGrayWarehouseRecord = new BlackGrayWarehouseRecord();
        blackGrayWarehouseRecord.setId(id);
        blackGrayWarehouseRecord.setAuditStatus(status);
        blackGrayWarehouseRecordMapper.updateByPrimaryKeySelective(blackGrayWarehouseRecord);
    }

    @Override
    public List<BlackGrayApplyReasonVo> getApplyReason() {
        String redisValue = stringRedisTemplate.opsForValue().get(REDIS_CACHE_APPLY_REASON_KEY);
        if(StrUtil.isNotBlank(redisValue)){
            return JSONUtil.toList(redisValue, BlackGrayApplyReasonVo.class);
        }
        List<BlackGrayBusinessTypeRsp> baseDictList = blackGrayBusinessDictMapper.getBaseDict(APPLY_REASON);
        if(ObjectUtil.isEmpty(baseDictList)){
            return null;
        }
        Map<Long, List<BlackGrayBusinessTypeRsp>> businessTypeMap = blackGrayBusinessDictMapper.getTree(null, baseDictList.stream().map(BlackGrayBusinessTypeRsp::getId).collect(Collectors.toList()), APPLY_REASON).stream().collect(Collectors.groupingBy(BlackGrayBusinessTypeRsp::getParentId));
        baseDictList.forEach(base -> {
            base.setChild(businessTypeMap.get(base.getId()));
        });
        List<BlackGrayApplyReasonVo> vos = new ArrayList<>();
        circulateBuildApplyReasonVo(baseDictList, vos, null);
        //缓存2小时
        stringRedisTemplate.opsForValue().set(REDIS_CACHE_APPLY_REASON_KEY, JSONUtil.toJsonStr(vos), 2, TimeUnit.HOURS);
        return vos;
    }


    @Override
    public BlackGrayWarehouseRuleConfig getApplyReasonLeave(String applyReasonCode) {
        if(ObjectUtil.isEmpty(applyReasonCode)){
            return null;
        }
        BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig = blackGrayWarehouseRuleConfigMapper.selectOne(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .eq(BlackGrayWarehouseRuleConfig::getStatus, 1)
                .eq(BlackGrayWarehouseRuleConfig::getRuleNumber, applyReasonCode)
                .last(StringUtils.mysqlLimitOne()));
        return blackGrayWarehouseRuleConfig ;
    }

    @Override
    public String num2Name(String applyReasonCode) {
        if(ObjectUtil.isEmpty(applyReasonCode)){
            return null;
        }
        BlackGrayWarehouseRuleConfig blackGrayWarehouseRuleConfig = blackGrayWarehouseRuleConfigMapper.selectOne(Wrappers.<BlackGrayWarehouseRuleConfig>lambdaQuery()
                .eq(BlackGrayWarehouseRuleConfig::getStatus, 1)
                .eq(BlackGrayWarehouseRuleConfig::getRuleNumber, applyReasonCode)
                .last(StringUtils.mysqlLimitOne()));
        return blackGrayWarehouseRuleConfig == null ? null : blackGrayWarehouseRuleConfig.getRuleName();

    }

    private void circulateBuildList(Map<String, BlackGrayApplyReasonVo> map, List<String> applyReasonList, String applyReasonCode){
        applyReasonList.set(0, applyReasonCode);
        BlackGrayApplyReasonVo vo = map.get(applyReasonCode);
        if(ObjectUtil.isNotEmpty(vo) && ObjectUtil.isNotEmpty(vo.getParentBusinessName())){
            circulateBuildList(map, applyReasonList, vo.getParentBusinessName());
        }
    }

    private void circulateApplyReason2Map(List<BlackGrayApplyReasonVo> applyReasons, Map<String, BlackGrayApplyReasonVo> map){
        if(ObjectUtil.isNotEmpty(applyReasons)){
            applyReasons.forEach(applyReason -> {
                map.putIfAbsent(applyReason.getBusinessName(), applyReason);
                if(ObjectUtil.isNotEmpty(applyReason.getChildren())){
                    circulateApplyReason2Map(applyReason.getChildren(), map);
                }
            });
        }
    }

    private void circulateBuildApplyReasonVo(List<BlackGrayBusinessTypeRsp> rsps, List<BlackGrayApplyReasonVo> vos, BlackGrayApplyReasonVo parentVo) {
        //构建申请原因
        if (ObjectUtil.isEmpty(rsps)) {
            return;
        }
        rsps.forEach(rsp -> {
            BlackGrayApplyReasonVo vo = new BlackGrayApplyReasonVo();
            vo.setBusinessName(rsp.getBusinessName());
            vo.setBusinessDesc(rsp.getBusinessDesc());
            vo.setLevel(rsp.getLevel());
            vo.setMainId(rsp.getMainId());
            if (ObjectUtil.isNotEmpty(parentVo)) {
                vo.setParentBusinessName(parentVo.getBusinessName());
                if(ObjectUtil.isEmpty(parentVo.getChildren())){
                    parentVo.setChildren(new ArrayList<>());
                }
                parentVo.getChildren().add(vo);
            } else {
                vos.add(vo);
            }
            if(ObjectUtil.isNotEmpty(rsp.getChild())){
                circulateBuildApplyReasonVo(rsp.getChild(), vos, vo);
            }
        });
    }
}
