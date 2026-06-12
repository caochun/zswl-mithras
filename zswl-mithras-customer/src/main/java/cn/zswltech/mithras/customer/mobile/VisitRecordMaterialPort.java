package cn.zswltech.mithras.customer.mobile;

import lombok.Data;

import java.util.Collection;
import java.util.List;

public interface VisitRecordMaterialPort {

    List<VisitRecordMaterial> listByVisitRecordIds(Collection<Long> visitRecordIds);

    @Data
    class VisitRecordMaterial {
        private Long belongId;
        private String ossFilename;
        private String filename;
    }
}
