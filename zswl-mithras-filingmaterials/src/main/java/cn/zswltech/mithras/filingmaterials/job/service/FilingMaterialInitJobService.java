package cn.zswltech.mithras.filingmaterials.job.service;

public interface FilingMaterialInitJobService {

    void initAfterFilingMaterial();

    void returnAfterFilingMaterial(String jobParam);

    void initFundFilingMaterial(String jobParam);

    void closeProjectFilingMaterial(String jobParam);
}
