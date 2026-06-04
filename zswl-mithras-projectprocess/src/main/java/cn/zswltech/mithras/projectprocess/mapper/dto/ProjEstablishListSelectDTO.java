package cn.zswltech.mithras.projectprocess.mapper.dto;

import cn.zswltech.mithras.service.mapper.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author junke
 */
@Data
public class ProjEstablishListSelectDTO extends BaseAuthDTO {

    private Long clientId;

    private String projName;

    private String projCode;

    private String bizType;

    private Long bizDeptId;

    private Long projSponsorUserId;

    private Long projCosponsorUserId;

    private String projEstablishStatus;

    private String projEstablishProcessStatus;

    private LocalDateTime createFrom;

    private LocalDateTime createTo;

    private LocalDateTime updateFrom;

    private LocalDateTime updateTo;

}
