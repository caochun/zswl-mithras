package cn.zswltech.mithras.projectprocess.mapper.dto;

import cn.zswltech.mithras.service.mapper.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaozhengkang
 */
@Data
public class ProjReviewListSelectDTO extends BaseAuthDTO {

    private Long clientId;

    private String projName;

    private String projCode;

    private String bizType;

    private Long bizDeptId;

    private Long projSponsorUserId;

    private Long projCosponsorUserId;

    private String projReviewStatus;

    private String projReviewProcessStatus;

    private LocalDateTime createFrom;

    private LocalDateTime createTo;

    private LocalDateTime updateFrom;

    private LocalDateTime updateTo;

}
