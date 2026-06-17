package cn.zswltech.mithras.filingmaterials.application.port;

public interface FilingMaterialInitJobPort {

    void initAfterFilingMaterial();

    void returnAfterFilingMaterial(String jobParam);

    void initFundFilingMaterial(String jobParam);

    void closeProjectFilingMaterial(String jobParam);
}
