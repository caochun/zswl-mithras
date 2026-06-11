package cn.zswltech.mithras.contract.dto.persistence;

import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName ClientMaxLeaseMonthDTO
 * @Description 用于接收客户对应最大租赁月数
 * @Author jackerhe
 * @Date 2023/7/3 5:00 下午
 * @Version 1.0
 **/
@Data
public class ContractRentLastTimeDTO {
    private Long contractId;

    private LocalDate endTime;
}
