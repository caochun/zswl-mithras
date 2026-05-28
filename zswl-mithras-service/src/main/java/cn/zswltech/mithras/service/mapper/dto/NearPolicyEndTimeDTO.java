package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NearPolicyEndTimeDTO {

    private Long projId;

    private LocalDate maxDate;
}
