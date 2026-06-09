package cn.zswltech.mithras.workbench.application.cardcal.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkbenchCollectionAmount {
    private String writeOffStatus;
    private Long planCollectionAmount;
    private Long collectionAmount;
}
