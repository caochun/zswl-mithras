package cn.zswltech.mithras.service.controller.payment.checker;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/5 11:36
 */
@Component
public class PaymentSubModifyChecker implements IDataAuthChecker {
    @Resource
    private PaymentBaseInfoService baseInfoService;
    @Resource
    private CommonModifySubAuthCheckerNew commonModifySubAuthChecker;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        commonModifySubAuthChecker.check(businessModule, helperMapperClass, keyId, args);
        if (ObjectUtil.isNotEmpty(keyId)) {
            PaymentBaseInfo baseInfo = baseInfoService.getById(keyId);
            if (PaymentStatusEnum.CLOSED.name().equals(baseInfo.getPaymentStatus())) {
                throw new MithrasException("该申请已关闭，不允许再修改有关信息");
            }
            if (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(baseInfo.getWriteOffStatus())) {
                throw new MithrasException("该申请已核销，不允许再修改有关信息");
            }
        }
        return true;
    }

    @Override
    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return commonModifySubAuthChecker.checkBatch(businessModule, helperMapperClass, keyIds, args);
    }
}
