package cn.zswltech.mithras.workflow.application.process.prepare;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProcessPrepareCollectionInfo {

    private Long contractId;

    private Long planCollectionAmount;

    private Long principal;

    private LocalDate planCollectionDate;

    private Long interest;
}
