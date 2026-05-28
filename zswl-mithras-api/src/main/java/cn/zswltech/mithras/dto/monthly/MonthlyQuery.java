package cn.zswltech.mithras.dto.monthly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyQuery {

    private LocalDate endMonth;

    private LocalDate beginMonth;

    private Integer isConfirmed;

    private Collection<Long> contractIdList;

    private String batchNumber;

    private Long receiptId;
}
