package cn.zswltech.mithras.payment.infrastructure.persistence.mapper;

import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.dto.ContractPayInfoDTO;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 实际付款记录表
 * @date 2022-08-16
 */
public interface PaymentActualDetailMapper extends CustomBaseMapper<PaymentActualDetail> {

    /**
     * 查询第一次投放在指定时间之后的实际付款记录
     *
     * @param startDateTime
     * @return
     */
    @Select("select * from payment_actual_detail as t where t.id in (select SUBSTRING_INDEX(GROUP_CONCAT(id ORDER BY paid_in_date ),',',1) from payment_actual_detail group by client_id ) AND paid_in_date >= #{startDateTime} and write_off_status = 'WRITTEN_OFF'")
    List<PaymentActualDetail> queryFirstLaunchClient(@Param("startDateTime") LocalDateTime startDateTime);

    @Select("select ifnull(sum(paid_in_amount),0) from payment_actual_detail where deleted = 0")
    long totalPay();

    @Select("select ifnull(sum(paid_in_amount),0) from payment_actual_detail where deleted = 0 and contract_id = #{contractId}")
    long totalPayByContractId(@Param("contractId") Long contractId);

    List<ContractPayInfoDTO> listContractPayInfoBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<ContractPayInfoDTO> listContractPayInfoBeforeTargetDate(@Param("targetDate") LocalDate targetDate);
}