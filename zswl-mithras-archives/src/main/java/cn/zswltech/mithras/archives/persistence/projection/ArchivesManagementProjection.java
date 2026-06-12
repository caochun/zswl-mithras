package cn.zswltech.mithras.archives.persistence.projection;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ArchivesManagementProjection {

    private Long id;

    private Long projId;

    private String projName;

    private Long projSponsorUserId;

    private Long bizDeptId;

    private Long clientId;

    private Long templateId;

    private String bizType;

    private String status;

    private String flowStatus;

    private Integer type;

    private String archivesCode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
