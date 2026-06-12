package cn.zswltech.mithras.archives.persistence.projection;

import lombok.Data;


@Data
public class ArchivesMustFileCountProjection {

    private Long archiveId;

    private Long count;
}