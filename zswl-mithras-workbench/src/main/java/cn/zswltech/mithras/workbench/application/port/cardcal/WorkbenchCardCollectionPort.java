package cn.zswltech.mithras.workbench.application.port.cardcal;

import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchCollectionAmount;

import java.time.LocalDate;
import java.util.List;

public interface WorkbenchCardCollectionPort {
    List<WorkbenchCollectionAmount> listPlanCollections(LocalDate start, LocalDate end);
}
