package cn.zswltech.mithras.application.orchestration.facade.payment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.payment.application.PaymentVersionApplicationService;
import cn.zswltech.mithras.api.payment.version.PaymentEffectREQ;
import cn.zswltech.mithras.api.payment.version.PaymentVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.payment.application.checker.PaymentModifyAuthChecker;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentTypeEnum;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentPolicyInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.financeprojectdistribution.service.impl.FinanceProjectDistributionService;
import cn.zswltech.mithras.payment.application.PaymentPlanedDetailService;
import cn.zswltech.mithras.payment.application.lib.service.impl.PaymentVersionServiceImpl;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 10:45
 */
@Service
public class PaymentVersionFacade implements PaymentVersionApplicationService {
    @Resource
    private PaymentBaseInfoService baseInfoService;
    @Resource
    private PaymentPlanedDetailService planedDetailService;
    @Resource
    private PaymentVersionServiceImpl versionService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = PaymentModifyAuthChecker.class, businessModule = "PAYMENT", mapperClass = PaymentBaseInfoMapper.class)
    public R<Void> effect(PaymentEffectREQ req) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.PAYMENT.name(), req.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            PaymentBaseInfo baseInfo = baseInfoService.getById(req.getId());
//        if(baseInfo.getCreateBy().equals(AccountUtil.getLoginInfo().getId())){
//            throw new MithrasException("只能由项目主办发起审批");
//        }
            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            //  校验放款审核表是否存在
            List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.PAYMENT.name(), Collections.singletonList(PaymentTypeEnum.LOAN_REVIEW.name()), Collections.singletonList(baseInfo.getId()));
            if (CollectionUtils.isEmpty(materialsListList)) {
                throw new MithrasException("《放款审核表》不存在，请生成或上传《放款审核表》后再提交流程！");
            }
            if (PaymentStatusEnum.CLOSED.name().equals(baseInfo.getPaymentStatus())) {
                throw new MithrasException("付款申请已关闭，不能提交审核");
            }
            if (PaymentStatusEnum.TAKE_EFFECT.name().equals(baseInfo.getPaymentStatus())) {
                throw new MithrasException("付款申请已生效，不能再次提交审核");
            }
            // 是否可提交 简单校验
            if (Objects.nonNull(paymentService.findRelatedProcess(req.getId()))) {
                throw new MithrasException("该付款申请数据变动处于流程中，无法提交数据");
            }
            // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
            ChangeDTO changeDTO = versionService.checkActualChange(req.getId());
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            paymentService.effect(req.getId());
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.PAYMENT.name());
        }
        PageR<CommonVersionListRSP> data = versionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(PaymentVersionDiffREQ req) {
        return R.ok(versionService.comparePreVersion(req.getId()));
    }

    @Override
    public R<Boolean> checkClientOpinion(PaymentEffectREQ req) {
        return R.ok(baseInfoService.checkClientOpinion(req));
    }

    @Override
    public R<Boolean> checkProjectDistribution(@Valid PaymentEffectREQ req) {
        PaymentBaseInfo baseInfo = baseInfoService.getById(req.getId());
        return R.ok(financeProjectDistributionService.checkPassProcess(baseInfo.getContractId()));
    }
}
