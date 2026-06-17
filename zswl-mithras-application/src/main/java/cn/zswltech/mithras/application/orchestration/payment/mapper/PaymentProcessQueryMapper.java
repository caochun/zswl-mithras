package cn.zswltech.mithras.application.orchestration.payment.mapper;

import cn.zswltech.mithras.payment.dto.PaymentListDto;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentProcessQueryMapper {

    Page<PaymentBaseInfo> listPayment(Page<PaymentBaseInfo> page, @Param("dto") PaymentListDto dto);

    List<PaymentBaseInfo> listApprovedPaymentCreateByContractId(@Param("contractId") Long contractId);
}
