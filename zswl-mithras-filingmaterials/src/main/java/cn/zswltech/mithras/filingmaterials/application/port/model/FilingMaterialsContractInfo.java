package cn.zswltech.mithras.filingmaterials.application.port.model;

import lombok.Data;

@Data
public class FilingMaterialsContractInfo {

    private String contractCode;

    private String projName;

    private Long projSponsorUserId;
}
