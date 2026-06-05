package cn.zswltech.mithras.service.application.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.afterleasecheck.AfterLeaseCheckPlanAddMainChecker;
import cn.zswltech.mithras.service.auth.checker.afterleasecheck.AfterLeaseCheckPlanModifyMainChecker;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.afterlease.application.convert.AfterLeaseCheckPlanConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.afterlease.domain.enums.NewAfterLeaseCheckMaterialsEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import cn.zswltech.mithras.message.mapper.message.NoticeMessageBody;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckPlanVersionService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
@Slf4j
@Service
public class AfterLeaseCheckPlanBaseFacade implements AfterLeaseCheckPlanBaseApplicationService {
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private MessageService messageService;
    @Resource
    private AfterLeaseCheckPlanVersionService afterLeaseCheckPlanVersionService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ClientService clientService;

    @Override
    public R<PageR<AfterLeaseCheckPlanListRSP>> listCheckPlanWithPage(AfterLeaseCheckPlanListREQ req) {
        return R.ok(afterLeaseCheckPlanBaseService.pageList(req));
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = AfterLeaseCheckPlanModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN)
    @Override
    public R<Void> close(@Valid SinglePkREQ req) {
        afterLeaseCheckPlanBaseService.close(req.getId());
        return R.ok();
    }

    @DataAuthCheck(checkerClass = AfterLeaseCheckPlanAddMainChecker.class, paramType = DataAuthCheck.ParamType.NO, businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN)
    @Override
    public R<Long> add(@Valid AfterLeaseCheckPlanBaseAddREQ req) {
        Long id = afterLeaseCheckPlanBaseService.add(req);
        return R.ok(id);
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = AfterLeaseCheckPlanModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN)
    @Override
    public R<Long> modify(@Valid AfterLeaseCheckPlanBaseModifyREQ req) {
        return R.ok(afterLeaseCheckPlanBaseService.modify(req));
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = AfterLeaseCheckPlanModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN)
    @Override
    public R<Void> cancel(@Valid SinglePkREQ req) {
        NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(req.getId());
        Assert.notNull(newAfterLeaseCheckPlanBase, () -> MithrasException.newException("检查计划不存在"));
        afterLeaseCheckPlanVersionService.reset(newAfterLeaseCheckPlanBase.getId());
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "planId", checkerClass = AfterLeaseCheckPlanModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN)
    @Override
    public R<Void> publish(@Valid AfterLeaseCheckPlanPublishREQ req) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN.name(), req.getPlanId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            afterLeaseCheckPlanBaseService.publish(req);
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<AfterLeaseCheckPlanDetailRSP> detail(@Valid SinglePkREQ req) {
        return R.ok(afterLeaseCheckPlanBaseService.detail(req.getId(), req.getVersion()));
    }

    @Override
    public R<AfterLeaseCheckPlanCommonlyDetailRSP> commonlyDetail(@Valid AfterLeaseCheckPlanCommonlyDetailREQ req) {
        return R.ok(afterLeaseCheckPlanBaseService.commonlyDetail(req));
    }

    @Override
    public R<List<AfterLeaseCheckSummaryReportRSP>> listSummaryReport(@Valid SinglePkREQ req) {
        // 判断是否业务部门
        // 是否业务部门用户 如果有任何非业务部门，都不是业务部门 取高
        boolean bizDeptFlag = sysUserService.getUserDeptList().stream().allMatch(o -> Objects.equals(OrgConstants.BUSINESS_DEPT, o.getType()));
        if (bizDeptFlag) {
            return R.ok(Collections.emptyList());
        }
        List<MaterialsList> materialsListList = materialsListService.list(
                BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN.name(),
                Collections.singletonList(NewAfterLeaseCheckMaterialsEnum.CHECK_PLAN_SUMMARY_REPORT.name()),
                Collections.singletonList(req.getId())
        );
        if (CollectionUtil.isEmpty(materialsListList)) {
            return R.ok(Collections.emptyList());
        }
        List<Long> creatorIdList = materialsListList.stream().map(BaseModel::getCreateBy).collect(Collectors.toList());
        Map<Long, String> creatorMap = id2NameService.sysUserId2Name(creatorIdList);
        List<AfterLeaseCheckSummaryReportRSP> result = new ArrayList<>(materialsListList.size());
        for (MaterialsList materialsList : materialsListList) {
            AfterLeaseCheckSummaryReportRSP rsp = AfterLeaseCheckPlanConvert.toAfterLeaseCheckSummaryReportRSP(materialsList);
            rsp.setCreator(creatorMap.get(materialsList.getCreateBy()));
            rsp.setCreateTimestamp(Optional.ofNullable(materialsList.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
            result.add(rsp);
        }
        result.sort(new CommonFileSortComparator());
        return R.ok(result);
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = AfterLeaseCheckPlanModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN)
    @Override
    public R<Void> finish(@Valid SinglePkREQ req) {
        afterLeaseCheckPlanBaseService.finish(req.getId());
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = AfterLeaseCheckPlanModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN)
    @Override
    public R<Void> cuiban(@Valid SinglePkREQ req) {
        NewAfterLeaseCheckPlanBase plan = afterLeaseCheckPlanBaseService.getById(req.getId());
        Assert.notNull(plan, () -> MithrasException.newException("检查计划不存在"));
        List<NewAfterLeaseCheckPlanClient> toCheckClientList = afterLeaseCheckPlanClientService.listToCheck(req.getId());
        if (CollectionUtil.isEmpty(toCheckClientList)) {
            throw new MithrasException("没有找到需催办的客户");
        }
        Set<Long> clientIds = toCheckClientList.stream().map(NewAfterLeaseCheckPlanClient::getClientId).collect(Collectors.toSet());
        List<Client> clientList = clientService.listByIds(clientIds);
        if (CollectionUtil.isEmpty(clientList)) {
            return R.ok();
        }
        Set<Long> sponsorIds = clientList.stream().map(Client::getBelongSponsorId).collect(Collectors.toSet());
        try {
            NoticeMessageBody noticeMessageBody = new NoticeMessageBody();
            noticeMessageBody.setTitle("租后检查计划催办通知");
            noticeMessageBody.setNodename(MessageTypeEnum.REMINDER_NOTICE.display());
            noticeMessageBody.setPcurl("/afterLease/checkPlan/planDetail/" + plan.getId());
            noticeMessageBody.setContent(plan.getPlanName());
            MessageModel messageModel = new MessageModel();
            messageModel.setFrom("system");
            messageModel.setTo(new ArrayList<>(sponsorIds));
            messageModel.setBodie(noticeMessageBody);
            messageService.sendMessageAsync(messageModel);
        } catch (Exception e) {
            log.error("【租后检查计划一键催办】发送通知发生异常[planId: {}]", plan.getId(), e);
            throw new MithrasException("发送催办通知失败");
        }
        return R.ok();
    }

    @Override
    public R<AfterLeaseAuditFlowPreRSP> afterLeaseAuditPreSelect(@Valid AfterLeaseAuditFlowPreREQ request) {
        return R.ok(afterLeaseCheckPlanBaseService.afterLeaseAuditPreSelect(request));
    }

    @Override
    public R<AfterLeaseClientPlanRSP> afterLeaseClientPlan(@Valid AfterLeaseAuditFlowPreREQ request) {
        List<AfterLeaseClientPlanRSP> clientPlan = afterLeaseCheckPlanBaseService.getNextCheckPlan(request.getClientId(), request.getTodoId());
        return R.ok(CollectionUtil.isEmpty(clientPlan) ? null : clientPlan.get(0));
    }

    @Override
    public R<Void> afterLeaseAssetStrategyModify(@Valid AfterLeaseAssetStrategyModifyREQ req) {
        afterLeaseCheckPlanBaseService.afterLeaseAssetStrategyModify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AfterLeaseAssetStrategyRSP>> afterLeaseAssetStrategys(AfterLeaseAssetStrategyREQ req) {
        return R.ok(afterLeaseCheckPlanBaseService.afterLeaseAssetStrategy(req));
    }
}
