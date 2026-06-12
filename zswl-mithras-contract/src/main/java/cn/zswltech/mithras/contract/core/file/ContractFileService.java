package cn.zswltech.mithras.contract.core.file;

import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/8/17
 * @description
 */
public interface ContractFileService {
    List<MaterialsList> listExchangeMaterial(Long contractId);

    Map<String, List<MaterialsList>> getContractFileMap(Long contractId);

    Long upload(MultipartFile file, String contractType, Long contractId);

    void generate(ContractSingleIdREQ contractSingleIdREQ) throws Exception;

    void removeContractFile(Long fileId);
}
