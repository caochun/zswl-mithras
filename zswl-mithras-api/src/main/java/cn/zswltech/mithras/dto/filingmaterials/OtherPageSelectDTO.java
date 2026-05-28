package cn.zswltech.mithras.dto.filingmaterials;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class OtherPageSelectDTO {
    private List<Long> ids;
    private String projName;
    private String projCode;
    private Long clientId;
    private String materialsDesc;
    private Long bizDeptId;
    private Long createById;
    private String approveStatus;
    private List<String> approveStatusList;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;
    private LocalDateTime endDateFrom;
    private LocalDateTime endDateTo;
    private String filingType;
    private String objectType;
    private List<Long> deptIdList;
    private Boolean isBizUser;
    private Long currentUserId;
}
