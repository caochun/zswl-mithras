package cn.zswltech.mithras.collection.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName CollectionContractSettleDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/25 11:10 上午
 * @Version 1.0
 **/
@Data
public class CollectionContractSettleDTO {
    private Long contractId;
    private LocalDateTime settleDate;
}
