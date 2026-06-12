package cn.zswltech.mithras.archives.persistence.projection;

import lombok.Data;


@Data
public class ArchivesMustFileTypeCountProjection {

    private Long templateId;

    private Long count;
}