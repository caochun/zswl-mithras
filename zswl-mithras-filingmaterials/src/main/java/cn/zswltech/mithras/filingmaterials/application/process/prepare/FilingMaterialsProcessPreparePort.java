package cn.zswltech.mithras.filingmaterials.application.process.prepare;

public interface FilingMaterialsProcessPreparePort {

    String startProcess(String processType, Long businessId);

    void close(String processType, Long businessId);
}
