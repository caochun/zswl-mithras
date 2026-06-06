package cn.zswltech.mithras.filingmaterials.application.job;

public interface FilingMaterialEmailJobService {

    void sendFilingMaterialEmail(String jobParam);

    void initProjectFilingMaterial(String jobParam);
}
