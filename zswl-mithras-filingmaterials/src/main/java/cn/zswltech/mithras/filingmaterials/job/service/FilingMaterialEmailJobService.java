package cn.zswltech.mithras.filingmaterials.job.service;

public interface FilingMaterialEmailJobService {

    void sendFilingMaterialEmail(String jobParam);

    void initProjectFilingMaterial(String jobParam);
}
