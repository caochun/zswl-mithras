package cn.zswltech.mithras.contract.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ContractMaterialsPort {

    Long add(InputStream inputStream, String fileName, Long belongId, String materialsType, String materialsSubType, String businessType) throws IOException;

    void remove(List<Long> ids);
}
