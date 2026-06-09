package cn.zswltech.mithras.fund.application.process.prepare;

import java.util.List;

public interface FundProcessPrepareMaterialPort {

    boolean hasMaterials(String businessType, List<String> materialTypes, Long belongId);
}
