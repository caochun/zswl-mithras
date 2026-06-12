package cn.zswltech.mithras.archives.dto.persistence;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ArchivesFlatTemplateDTO {

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
