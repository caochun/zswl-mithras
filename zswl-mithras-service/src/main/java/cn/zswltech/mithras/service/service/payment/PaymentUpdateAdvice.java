package cn.zswltech.mithras.service.service.payment;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @description Payment更新后执行的切面操作
 * @author zhaozhengkang
 * @date 2022-07-19
 */
public interface PaymentUpdateAdvice {

    default void saveCheck(PaymentBaseInfo baseInfo) {
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException("付款申请不存在");
        }
        if(PaymentStatusEnum.TAKE_EFFECT.name().equals(baseInfo.getPaymentStatus())){
            throw new MithrasException("付款申请生效后不可变更");
        }
    }

    default void recordStatus(Long paymentId) {
        PaymentService paymentService = SpringContextHolder.getBean(PaymentService.class);
        PaymentBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(PaymentBaseInfoMapper.class);
        ProcessResp processResp = paymentService.findRelatedProcess(paymentId);
        PaymentBaseInfo baseInfo = baseInfoMapper.selectById(paymentId);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或客户状态为新建时 只更新最后更新时间 不更新客户状态
        if (PaymentStatusEnum.NEW.name().equals(baseInfo.getPaymentStatus()) || Objects.nonNull(processResp)) {
            // 只更新 最后更新时间
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", baseInfo.getId());
            updateWrapper.set("update_time", LocalDateTime.now());
            baseInfoMapper.update(null, updateWrapper);
            return;
        }
        paymentService.recordPaymentStatus(paymentId, null, ProcessStatus.UN_SUBMIT);
    }

}
