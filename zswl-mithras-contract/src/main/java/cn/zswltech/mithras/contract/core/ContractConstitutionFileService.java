package cn.zswltech.mithras.contract.core;

import cn.zswltech.mithras.contract.model.contract.ContractConstitutionFile;
import cn.zswltech.mithras.contract.core.dto.ContractConstitutionFileCommand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/4/18 14:17
 */
public interface ContractConstitutionFileService extends IService<ContractConstitutionFile> {

    void saveConstitutionFiles(ContractConstitutionFileCommand constitutionFileBO) throws IOException;

    List<Long> getConstitutionFileList(ContractConstitutionFileCommand constitutionFileBO);

    void deleteByFileIdAndContractId(ContractConstitutionFileCommand constitutionFileBO);
}
