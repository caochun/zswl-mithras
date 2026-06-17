package cn.zswltech.mithras.filingmaterials.application.port;

public interface FilingMaterialEmailJobPort {

    void sendFilingMaterialEmail(String jobParam);

    void initProjectFilingMaterial(String jobParam);
}
