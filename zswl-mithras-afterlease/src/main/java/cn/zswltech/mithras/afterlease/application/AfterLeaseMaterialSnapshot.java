package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AfterLeaseMaterialSnapshot {
    Long id;
    Long belongId;
    Long createBy;
    String materialsType;
    String filename;
    String ossFilename;
    LocalDateTime createTime;
}
