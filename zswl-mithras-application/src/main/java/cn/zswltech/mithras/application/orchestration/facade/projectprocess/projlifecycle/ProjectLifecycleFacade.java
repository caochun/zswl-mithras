package cn.zswltech.mithras.application.orchestration.facade.projectprocess.projlifecycle;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projlifecycle.*;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.ProjectLifecycleApplicationService;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.ProjLifecycleService;
import cn.zswltech.mithras.application.orchestration.auth.rule.special.ProjEstablishAuthViewRule;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjStageEnum;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleListDO;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projlifecycle.ProjectLifecycleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @create: 2022-10-24
 **/

@Service
@Slf4j
public class ProjectLifecycleFacade implements ProjectLifecycleApplicationService {

    @Resource
    private ProjectLifecycleService projectLifecycleService;
    @Resource
    private ProjLifecycleService projLifecycleService;
    @Resource
    private ProjEstablishAuthViewRule projEstablishAuthViewRule;

    @Override
    public R<ProjStageTotalRSP> count() {
        return R.ok(projLifecycleService.countProj());
    }

    @Override
    public R<PageR<ProjectLifecycleListRSP>> list(@Valid ProjectLifecycleListREQ req) {
        Page<ProjLifecycleListDO> pageData = projLifecycleService.list(req);
        List<ProjLifecycleListDO> records = pageData.getRecords();
        List<ProjectLifecycleListRSP> rspList = new ArrayList<>();
        //id转名称
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> sysClientIds = new HashSet<>();
        Set<Long> sysDeptIds = new HashSet<>();
        records.forEach(e -> {
            String assignees = e.getAssignees();
            if (StrUtil.isNotEmpty(assignees)) {
                sysUserIds.addAll(JSONUtil.toList("[" + assignees + "]", Long.class));
            }
            sysUserIds.add(e.getProjSponsorUserId());
            sysClientIds.add(e.getClientId());
            sysDeptIds.add(e.getBizDeptId());
        });
        Map<Long, String> sysUserMap = getBean(Id2NameService.class).sysUserId2Name(sysUserIds);
        Map<Long, ClientInfo> sysClientMap = getBean(Id2NameService.class).clientId2CLient(sysClientIds);
        Map<Long, String> sysDeptMap = getBean(Id2NameService.class).deptId2Name(sysDeptIds);
        //
        for (ProjLifecycleListDO record : records) {
            ProjectLifecycleListRSP rsp = new ProjectLifecycleListRSP();
            rsp.setEstablishId(record.getEstablishId());
            rsp.setReviewId(record.getReviewId());
            rsp.setProjectName(record.getProjName());
            rsp.setClientId(record.getClientId());
            rsp.setClientType(sysClientMap.get(record.getClientId()).getClientType());
            rsp.setDomesticOrAbroad(sysClientMap.get(record.getClientId()).getDomesticOrAbroad());
            rsp.setClientName(sysClientMap.get(record.getClientId()).getClientName());
            rsp.setBizType(record.getBizType());
            rsp.setProjStage(
                    record.getReviewId() == null ? ProjStageEnum.PROJESTABLISH_STAGE.name() :
                            record.getSettled() == null ? ProjStageEnum.PROJREVIEW_STAGE.name() :
                                    record.getSettled().equals(1) ? ProjStageEnum.CONTRACTSETTLE_STAGE.name() :
                                            ProjStageEnum.CONTRACT_STAGE.name()
            );
            rsp.setProjSponsorUserId(record.getProjSponsorUserId());
            rsp.setProjSponsorUserName(sysUserMap.get(record.getProjSponsorUserId()));
            rsp.setBizDeptName(sysDeptMap.get(record.getBizDeptId()));
            if (null != record.getFlowType()) {
                rsp.setProcessType(ProcessModelTypeEnum.getByName(record.getFlowType()).getDisplay());
            }
            if (null != record.getAssignees()) {
                List<Long> list = JSONUtil.toList("[" + record.getAssignees() + "]", Long.class);
                String currentNode = list.stream().map(sysUserMap::get).collect(Collectors.joining(","));
                rsp.setCurrentNode(currentNode);
            }
            if (record.getStartTime() != null) {
                rsp.setApplyTime(record.getStartTime().toLocalDate());
            }
            rsp.setApplyCreditAmount(record.getApplyCreditAmount());
            rsp.setContractAmountApplied(record.getContractAmountApplied());
            rsp.setContractAmountEffected(record.getContractAmountEffected());
            rsp.setPaymentAmountApplied(record.getPaymentAmountApplied());
            rsp.setPaymentAmountEffected(record.getPaymentAmountEffected());
            rsp.setPaymentAmountWrittenOff(record.getPaymentAmountWrittenOff());
            rsp.setRemainingPrincipal(record.getRemainingPrincipal());

            rsp.setEstablishStatus(record.getEstablishStatus());
            rsp.setEstablishProcessStatus(record.getEstablishProcessStatus());
            rsp.setReviewStatus(record.getReviewStatus());
            rsp.setReviewProcessStatus(record.getReviewProcessStatus());
            rsp.setEstablishApplyCreditAmount(record.getEstablishApplyCreditAmount());
            rsp.setProjLifecycleStatus(record.getProjLifecycleStatus());

            rspList.add(rsp);
        }
        return R.ok(PageR.of(rspList, pageData.getTotal(), pageData.getCurrent(), pageData.getSize()));
    }


    @Override
    public R<ProjectLifecycleDetailRSP> detail(@Valid ProjectLifecycleDetailREQ req) {
        authCheck(req.getEstablishId(), req.getReviewId());
        return projectLifecycleService.detail(req);
    }

    @Override
    public R<ProjectLifecycleProjestablishCardRSP> projestablishCard(@Valid ProjectLifecycleCardREQ req) {
        authCheck(req.getEstablishId(), req.getReviewId());
        return projectLifecycleService.projestablishCard(req);
    }

    @Override
    public R<ProjectLifecycleProjreviewCardRSP> projreviewCard(@Valid ProjectLifecycleCardREQ req) {
        authCheck(req.getEstablishId(), req.getReviewId());
        return projectLifecycleService.projreviewCard(req);
    }

    @Override
    public R<List<ProjectLifecycleContractCardRSP>> contractCard(@Valid ProjectLifecycleCardREQ req) {
        authCheck(req.getEstablishId(), req.getReviewId());
        return projectLifecycleService.contractCard2(req);
    }

    @Override
    public R<ProjectLifecycleAfterLeaseCheckCardRSP> afterLeaseCheckCard(@Valid ProjectLifecycleCardREQ req) {
        authCheck(req.getEstablishId(), req.getReviewId());
        return R.ok(projectLifecycleService.afterLeaseCheckCard(req));
    }

    @Override
    public R<List<RentCollectionListRSP>> rentCollectionListCard(@Valid ProjectLifecycleCardREQ req) {
        authCheck(req.getEstablishId(), req.getReviewId());
        return projectLifecycleService.rentCollectionListCard(req);
    }

    @Override
    public R<PageR<ProjectLifecycleMilestoneRSP>> milestone(@Valid ProjectLifecycleEventREQ req) {
        authCheck(req.getEstablishId(), req.getReviewId());
        return R.ok(projectLifecycleService.milestone(req));
    }

    private void authCheck(Long establishId, Long reviewId) {
        if (establishId != null) {
            projEstablishAuthViewRule.checkEstablish(establishId);
        } else if (null != reviewId) {
            projEstablishAuthViewRule.checkReview(reviewId);
        }
    }
}
