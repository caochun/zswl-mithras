package cn.zswltech.mithras.application.orchestration.facade.contract;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.contract.ContractVersionApplicationService;
import cn.zswltech.mithras.dto.contract.*;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.contract.application.auth.ContractBaseAddMainAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrepayment;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractPrepaymentService;
import cn.zswltech.mithras.application.orchestration.contract.ContractService;
import cn.zswltech.mithras.contract.core.ContractSettlePlanService;
import cn.zswltech.mithras.contract.versioning.application.ContractVersionService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum.CONTRACT;
import static cn.zswltech.mithras.contract.enums.contract.ContractExtraFileTypeEnum.START_RENT;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@Slf4j
@Service
public class ContractVersionFacade implements ContractVersionApplicationService {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractService contractService;
    @Resource
    private ContractVersionService contractVersionService;
    @Resource
    private MaterialsListService materialsListService;
    @Autowired
    private ContractBaseInfoService baseInfoService;
    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private ContractPrepaymentService contractPrepaymentService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;


    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> effect(@Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(CONTRACT.name(), contractFlowBasicREQ.getContractId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            contractService.effect(contractFlowBasicREQ);
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> addNewReceipt(@Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(CONTRACT.name(), contractFlowBasicREQ.getContractId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            contractService.addNewReceipt(contractFlowBasicREQ.getContractId());
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    @Transactional
    @Override
    public R<Void> addNewReceiptCancel(@Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        // 更新状态
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.NEW_RECEIPT_CANCEL.name(), null, contractFlowBasicREQ.getContractId());
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractFlowBasicREQ.getContractId());
        if (!Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.NEW_RECEIPT_UNCOMMIT.name())) {
            // 回退数据版本
            contractVersionService.reset(contractFlowBasicREQ.getContractId());
        }
        // 取消关联付款申请
        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            paymentBaseInfoService.cancelJoinReceiptByContractId(contractFlowBasicREQ.getContractId());
        }
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Transactional
    @Override
    public R<Void> startRent(@Valid ContractFlowStartRentREQ contractFlowStartRentREQ) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(CONTRACT.name(), contractFlowStartRentREQ.getContractId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            // 更新基本信息表
            contractBaseInfoService.updateActualLeaseDate(contractFlowStartRentREQ.getActualLeaseDate(), contractFlowStartRentREQ.getContractId());
            contractService.startRent(contractFlowStartRentREQ);
        } finally {
            redisDistLock.unlock(lockKey);
        }
        try {
            // 清空缓存
            AccountVO accountVO = AccountUtil.getLoginInfo();
            if (Objects.nonNull(accountVO)) {
                String cacheKey = CacheEnum.getCacheKey(CacheEnum.GLOBAL_TOAST_TEXT, accountVO.getId().toString());
                stringRedisTemplate.delete(cacheKey);
            }
        } catch (Exception e) {
            log.error("合同未起租提示文案缓存删除异常[contractId: {}]", contractFlowStartRentREQ.getContractId(), e);
        }
        List<MaterialsList> list = materialsListService.getList(
                contractFlowStartRentREQ.getContractId(), CONTRACT.name(), START_RENT.name(), null);
        err(list.isEmpty(), "请上传起租附件");
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Transactional
    @Override
    public R<Void> startRentCancel(@Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractFlowBasicREQ.getContractId());
        if (!Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_UNCOMMIT.name())) {
            return R.ok();
        }
        // 更新状态
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.START_RENT_CANCEL.name(), null, contractFlowBasicREQ.getContractId());
        // 回退数据版本
        contractVersionService.reset(contractFlowBasicREQ.getContractId());
        // 取消关联付款申请
        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            paymentBaseInfoService.cancelJoinReceiptByContractId(contractFlowBasicREQ.getContractId());
        }
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<String> change(@Valid ContractFlowChangeREQ req) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(CONTRACT.name(), req.getContractId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            String change = contractService.change(req);
            return R.ok(change);
        } finally {
            redisDistLock.unlock(lockKey);
        }
      //  return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> changeCancel(@Valid ContractFlowChangeREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (!Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.CHANGE_UNCOMMIT.name())) {
            return R.ok();
        }
        // 更新状态
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_CANCEL.name(), null, req.getContractId());
        // 回退数据版本
        contractVersionService.reset(req.getContractId());
        if (ContractChangeTypeEnum.EARLY_REPAYMENT.name().equals(req.getChangeType())) {
            contractPrepaymentService.remove(Wrappers.<ContractPrepayment>lambdaQuery()
                    .eq(ContractPrepayment::getContractId, req.getContractId()));
        }
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", paramType = DataAuthCheck.ParamType.DIRECT, checkerClass = ContractBaseAddMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> upload(Long contractId, MultipartFile[] fileArray, String changeType) {
        for (MultipartFile file : fileArray) {
            try {
                materialsListService.add(file.getInputStream(), file.getOriginalFilename(), contractId, ContractExtraFileTypeEnum.CHANGE.name(), changeType, CONTRACT.name());
            } catch (IOException e) {
                log.warn("ContractVersionController upload error", e);
                throw new MithrasException("补充协议上传失败");
            }
        }
        // 状态维护统一到ContractOperationPrepare中了
//        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), changeType, contractId);
        return R.ok();
    }

    @Override
    public R<List<MaterialsListRsp>> downFile(@Valid ContractFlowChangeREQ req) {
        List<MaterialsList> materialsLists = materialsListService.getList(req.getContractId(), CONTRACT.name(), ContractExtraFileTypeEnum.CHANGE.name(), req.getChangeType());
        List<MaterialsListRsp> rsp = new ArrayList<>();
        materialsLists.forEach(list -> {
            MaterialsListRsp materialsListRsp = new MaterialsListRsp();
            materialsListRsp.setFileId(list.getId());
            materialsListRsp.setFileName(list.getFilename());
            materialsListRsp.setIsEdit(list.getIsEdit());
            rsp.add(materialsListRsp);
        });
        return R.ok(rsp);
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> changeDelete(@Valid ContractFileDeleteREQ req) {
        materialsListService.remove(Collections.singletonList(req.getFileId()));
        // 状态维护统一到ContractOperationPrepare中了
//        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.CHANGE_UNCOMMIT.name(), req.getChangeType(), req.getContractId());
        return R.ok();
    }

    @Override
    public R<Void> changeConserve(@Valid ContractFlowChangeConserveREQ req) {
        contractService.changeConserve(req);
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> settle(@Valid ContractFlowSettleREQ req) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(CONTRACT.name(), req.getContractId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            contractService.settle(req);
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Transactional
    @Override
    public R<Void> settleCancel(@Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractFlowBasicREQ.getContractId());
        if (contractService.isInProcess(contractFlowBasicREQ.getContractId())) {
            throw new MithrasException("该合同数据变动已处于流程中，无法取消");
        }
        if (!Objects.equals(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), contractBaseInfo.getContractProcessStatus())) {
            throw new MithrasException("数据无变动");
        }
        // 更新状态
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.SETTLE_CANCEL.name(), null, contractFlowBasicREQ.getContractId());
        // 回退数据版本
        contractVersionService.reset(contractFlowBasicREQ.getContractId());
        // 删除结清方案
        contractSettlePlanService.removeByContractId(contractFlowBasicREQ.getContractId());
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(@Valid CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(CONTRACT.name());
        }
        PageR<CommonVersionListRSP> data = contractVersionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(@Valid ProjReviewVersionDiffREQ req) {
        return R.ok(contractVersionService.comparePreVersion(req.getId()));
    }

    @Override
    //@DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseOperationAuthChecker.class, businessModule = "CONTRACT")
    public R<ContractCanChangeRSP> canChange(@Valid ContractCanChangeREQ req) {
        return R.ok(baseInfoService.canUpdateContractProcessStatus(req.getModuleType(), req.getContractId()));
    }

    @Override
    public R constraint(@Valid ContractConstraintREQ req) {
        baseInfoService.constraint(req);
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> confirmEffect(@Valid ContractFlowBasicREQ contractFlowBasicREQ) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(CONTRACT.name(), contractFlowBasicREQ.getContractId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            contractService.confirmEffect(contractFlowBasicREQ.getContractId());
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }


    @DataAuthCheck(keyFieldName = "contractId", paramType = DataAuthCheck.ParamType.DIRECT, checkerClass = ContractBaseAddMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> uploadStartRentFile(Long contractId, MultipartFile[] fileArray) {
        for (MultipartFile file : fileArray) {
            try {
                materialsListService.add(file.getInputStream(), file.getOriginalFilename(), contractId, START_RENT.name(), null, CONTRACT.name());
            } catch (IOException e) {
                log.warn("ContractVersionController upload error", e);
                throw new MithrasException("起租附件上传失败");
            }
        }
        return R.ok();
    }

    @Override
    public R<List<MaterialsListRsp>> downloadStartRentFile(@Valid ContractFlowBasicREQ req) {
        List<MaterialsList> materialsLists = materialsListService.getList(req.getContractId(), CONTRACT.name(), START_RENT.name(), null);
        List<MaterialsListRsp> rsp = new ArrayList<>();
        materialsLists.forEach(list -> {
            MaterialsListRsp materialsListRsp = new MaterialsListRsp();
            materialsListRsp.setFileId(list.getId());
            materialsListRsp.setFileName(list.getFilename());
            rsp.add(materialsListRsp);
        });
        return R.ok(rsp);
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> startRentFileDelete(@Valid ContractStartRentFileDeleteREQ req) {
        materialsListService.remove(Collections.singletonList(req.getFileId()));
        return R.ok();
    }
}
