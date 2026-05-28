package cn.zswltech.mithras.service.controller.payment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentVersionApi;
import cn.zswltech.mithras.api.payment.version.PaymentEffectREQ;
import cn.zswltech.mithras.api.payment.version.PaymentVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.payment.PaymentModifyAuthChecker;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionService;
import cn.zswltech.mithras.service.service.lib.payment.libservice.impl.PaymentVersionServiceImpl;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentPlanedDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 10:45
 */
@RestController
public class PaymentVersionController implements PaymentVersionApi {
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
    @DataAuthCheck(keyFieldName = "id", checkerClass = PaymentModifyAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT, mapperClass = PaymentBaseInfoMapper.class)
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
