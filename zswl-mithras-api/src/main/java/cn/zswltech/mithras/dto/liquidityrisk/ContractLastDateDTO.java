package cn.zswltech.mithras.dto.liquidityrisk;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContractLastDateDTO {
    private List<Long> contractIds;
    private List<Long> receiptIds;
    private LocalDateTime timeFrom;
    private LocalDateTime timeTo;
}
