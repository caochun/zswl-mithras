package cn.zswltech.mithras.service.application.payment;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.*;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailWriteoffReq;
import cn.zswltech.mithras.payment.application.PaymentWriteOffApplicationService;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffHistoryListReq;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffHistoryListRsp;
import cn.zswltech.mithras.api.payment.writeoff.*;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.payment.PaymentActualDetailRemoveAuthChecker;
import cn.zswltech.mithras.service.auth.checker.payment.PaymentActualDetailOperationAuthChecker;
import cn.zswltech.mithras.service.application.payment.checker.PaymentWriteOffAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentCollectionInfo;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.payment.application.PaymentWriteOffHistoryService;
import cn.zswltech.mithras.service.service.payment.PaymentWriteOffService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 10:15
 */
@Service
public class PaymentWriteOffFacade implements PaymentWriteOffApplicationService {
    @Resource
    private PaymentWriteOffService writeOffService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private PaymentActualDetailUnconfirmedService actualDetailUnconfirmedService;
    @Resource
    private PaymentWriteOffHistoryService historyService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<PageR<PaymentWriteOffListRsp>> listWriteOff(PaymentWriteOffListReq req) {
        return R.ok(writeOffService.list(req));
    }

    @Override
    public R<PaymentWriteOffDetailRsp> detailWriteOff(PaymentWriteOffDetailReq req) {
        return R.ok(writeOffService.detail(req));
    }

    @Deprecated
    @Override
    @DataAuthCheck(checkerClass = PaymentWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<ActualDetailAddRsp> addActual(MultipartFile[] enclosures, ActualDetailPostReq req) {
        ActualDetailAddRsp rsp = new ActualDetailAddRsp();
        rsp.setId(actualDetailService.add(enclosures,req));
        return R.ok(rsp);
    }

    @Override
    public R<ActualDetailListRsp> listActual(ActualDetailListReq req) {
        return R.ok(actualDetailService.list(req));
    }

    @Override
    public R<ActualDetailDto> detailActual(ActualDetailOperateReq req) {
        if (Objects.equals(req.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode())) {
            return R.ok(actualDetailService.detail(req.getId()));
        } else {
            return R.ok(actualDetailUnconfirmedService.detail(req.getId()));
        }
    }

    @Deprecated
    @Override
    @DataAuthCheck(checkerClass = PaymentWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<Void> modifyActual(MultipartFile[] enclosures, ActualDetailPostReq req) {
        Long id = AccountUtil.getLoginInfo().getId();
        List<String> jobs = sysUserService.queryUserJobList(id);
        actualDetailService.modify(enclosures, req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<Void> writeOffActual(ActualDetailWriteoffReq req) {
        actualDetailService.writeOff(req.getId(), req.getWriteOffStatus());
        return R.ok();
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentActualDetailRemoveAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT, keyFieldName = "id", paramType = DataAuthCheck.ParamType.OBJECT)
    public R<Void> removeActual(ActualDetailOperateReq req) {
        actualDetailUnconfirmedService.remove(req.getId());
        return R.ok();
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<Void> off(PaymentWriteOffReq req) {
        writeOffService.writeOff(req.getId());
        return R.ok();
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
    public R<Void> undooff(PaymentWriteOffReq req) {
        writeOffService.undoWriteOff(req.getId());
        return R.ok();
    }

    @Override
    public R<PageR<PaymentWriteOffHistoryListRsp>> list(PaymentWriteOffHistoryListReq req) {
        return R.ok( historyService.list(req));
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentActualDetailOperationAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT, keyFieldName = "paymentId", paramType = DataAuthCheck.ParamType.OBJECT)
    public R<Long> create(@Valid PaymentActualDetailAddReq req) {
        return R.ok(actualDetailUnconfirmedService.save(req));
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentActualDetailOperationAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT, keyFieldName = "paymentId", paramType = DataAuthCheck.ParamType.OBJECT)
    public R<Void> modify(@Valid PaymentActualDetailModifyReq req) {
        actualDetailUnconfirmedService.save(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(checkerClass = PaymentActualDetailOperationAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT, keyFieldName = "paymentId", paramType = DataAuthCheck.ParamType.OBJECT)
    public R<String> submit(@Valid PaymentActualDetailSubmitReq req) {
        return R.ok(actualDetailUnconfirmedService.submit(req));
    }

    @Override
    public R<Long> collectionAdd(PaymentCollectionAddReq req) {
        return R.ok(actualDetailUnconfirmedService.collectionAdd(req));
    }

    @Override
    public R<PaymentCollectionRsp> collectionDetail(Long paymentId) {
        return R.ok(actualDetailUnconfirmedService.collectionDetail(paymentId));
    }

    @Override
    public R<String> submitReviewInAdvanced(SinglePkREQ req) {
        return R.ok(actualDetailUnconfirmedService.submitReviewInAdvanced(req.getId()));
    }
}
