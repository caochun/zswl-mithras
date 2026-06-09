package cn.zswltech.mithras.archives.infrastructure.persistence.mapper.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ArchivesFlatTempalteDTO {

    private Long archiveId;

    private Long templateId;

    private Long groupId;

    private String groupName;

    private String typeName;

    private Long typeId;

    private Long fileId;

    private String fileName;

    private Integer sort;

    private Long uploadUser;

    private LocalDateTime uploadTime;

}