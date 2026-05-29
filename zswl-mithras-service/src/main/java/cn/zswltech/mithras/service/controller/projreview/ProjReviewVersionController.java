package cn.zswltech.mithras.service.controller.projreview;
import cn.zswltech.mithras.common.constant.MithrasConstants;
import cn.zswltech.mithras.common.constant.ResultMsg;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.projreview.ProjReviewVersionApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projreview.ClientMaterialsLackInfoRSP;
import cn.zswltech.mithras.dto.projreview.ProjReviewEffectREQ;
import cn.zswltech.mithras.dto.projreview.ProjReviewRatingCheckRSP;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewCompareREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.factory.service.RatingAmountService;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.ProcessModifyRemark;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.ProcessModifyRemarkService;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.client.ClientTransferService;
import cn.zswltech.mithras.service.service.contract.impl.ContractBaseInfoServiceImpl;
import cn.zswltech.mithras.service.service.lib.projreview.impl.ProjReviewVersionServiceImpl;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewCashFlowPlanService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.common.constant.MithrasConstants.ERR_IN_TRANSFER;
import static cn.zswltech.mithras.common.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.common.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.enums.contract.ProjItemStatus.TAKE_EFFECT;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * 项目评审-版本管理接口
 *
 * @description
 * @date 2022-07-19
 */
@RestController
public class ProjReviewVersionController implements ProjReviewVersionApi {

    @Resource
    private ProjReviewVersionServiceImpl projReviewVersionService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Autowired
    private ProjReviewBaseInfoService baseInfoService;

    @Resource
    private ProjReviewPriceService priceService;

    @Resource
    private ContractBaseInfoServiceImpl contractBaseInfoService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private RatingAmountService ratingAmountService;

    @Override
    public R<ClientMaterialsLackInfoRSP> checkClientMaterials(@Valid SinglePkREQ req) {
        return R.ok(projReviewService.checkClientMaterials(req.getId()));
    }

    @Override
    public R<ProjReviewRatingCheckRSP> checkRatingInfo(SinglePkREQ req) {
        return R.ok(projReviewService.checkRating(req.getId()));
    }

    /**
     * 评审信息生效（或提交审批）
     *
     * @param req
     * @return
     */
    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_REVIEW
    )
    public R<Void> effect(ProjReviewEffectREQ req) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.PROJ_REVIEW.name(), req.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            ProjReviewBaseInfo baseInfo = baseInfoService.getById(req.getId());
            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            if (clientTransferService.inTransfer(baseInfo.getClientId())) {
                err(ERR_IN_TRANSFER);
            }
            if (RecordStatus.CLOSED.name().equals(baseInfo.getProjReviewStatus()) || RecordStatus.EXPIRE.name().equals(baseInfo.getProjReviewStatus())) {
                throw new MithrasException("评审已关闭，不能提交审核");
            }
            if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(baseInfo.getClientId())) {
                throw new MithrasException("无所选客户管护权，无权进行操作");
            }
            projReviewService.effectCheck(baseInfo);
//            // 校验是否已经通过业务定价审批流程
//            ProcessPageReq processPageReq = new ProcessPageReq();
//            processPageReq.setModelKey(ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name());
//            processPageReq.setBusinessKey(req.getId().toString());
//            processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
//            Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
//            Assert.isTrue(CollectionUtil.isNotEmpty(processRespPage.getContents()), () -> MithrasException.newException("业务定价审批未通过"));
            List<ProcessResp> relatedProcess = projReviewService.findRelatedProcesses(req.getId());
            if (ObjectUtil.isNotEmpty(relatedProcess)) {
                for (ProcessResp process : relatedProcess) {
                    ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.valueOf(process.getModelKey());
                    if (ProcessModelTypeEnum.ProjReviewCreateFlow.equals(processModelTypeEnum)
                            || ProcessModelTypeEnum.ProjReviewModifyFlow.equals(processModelTypeEnum)) {
                        throw new MithrasException(String.format("项目评审已处于'%s'中，提交审批失败", processModelTypeEnum.getDisplay()));
                    }
                }
            }
            // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
//            ChangeDTO changeDTO = projReviewVersionService.checkActualChange(req.getId());
//            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
//                throw new MithrasException("数据未变动，无需提交数据");
//            }
            ratingClientService.effectClientCheck(baseInfo.getEvaluationSubjectId(), baseInfo.getClientId());
            ratingAmountService.effectProjCheck(baseInfo.getEvaluationSubjectId(),baseInfo.getClientId(),baseInfo.getId());
            if (!TAKE_EFFECT.name().equals(baseInfo.getProjReviewStatus())) {
                projReviewService.effect(req.getId());
            } else {
                if (req.getOnlyCheck()) {
                    // 变更流程需要校验是否有审批中的合同流程和付款流程
                    projReviewService.checkRelatedBusinessProcess(baseInfo.getId());
                } else {
                    req.getRemarkAddREQ().check();
                    getBean(ProcessModifyRemarkService.class).saveOrUpdateByKey(copyProperties(req.getRemarkAddREQ(), ProcessModifyRemark.class));
                    projReviewService.effect(req.getId());
                }
            }
        } finally {
            redisDistLock.unlock(lockKey);
        }

        return R.ok();
    }


    /**
     * 评审信息版本表列
     *
     * @param req
     * @return
     */
    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.PROJ_REVIEW.name());
        }
        PageR<CommonVersionListRSP> data = projReviewVersionService.selectPage(req);
        return R.ok(data);
    }

    /**
     * 评审信息版本比较详情（与上一版本比较）
     *
     * @param req
     * @return
     */
    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ProjReviewVersionDiffREQ req) {
        return R.ok(projReviewVersionService.comparePreVersion(req.getId()));
    }

    @Override
    public R<Map<String, DiffValue>> projReviewPricingBaseInfoCompare(ProjReviewCompareREQ req) {
        return R.ok(projReviewVersionService.projReviewPricingBaseInfoCompare(req));
    }

    @Override
    public R<Map<String, DiffValue>> projReviewPricingPriceCompare(ProjReviewCompareREQ req) {
        return R.ok(projReviewVersionService.projReviewPricingPriceCompare(req));
    }

    @Override
    public R<List<Map<String, DiffValue>>> projReviewPricingCashFlowPlanCompare(ProjReviewCompareREQ req) {
        return R.ok(projReviewVersionService.projReviewPricingCashFlowPlanCompare(req));
    }

}