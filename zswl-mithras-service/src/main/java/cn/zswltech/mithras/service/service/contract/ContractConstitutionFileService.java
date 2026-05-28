package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.service.mapper.model.contract.ContractConstitutionFile;
import cn.zswltech.mithras.service.service.bo.ContractConstitutionFileBO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/4/18 14:17
 */
public interface ContractConstitutionFileService extends IService<ContractConstitutionFile> {

    void saveConstitutionFiles(ContractConstitutionFileBO constitutionFileBO) throws IOException;

    List<Long> getConstitutionFileList(ContractConstitutionFileBO constitutionFileBO);

    void deleteByFileIdAndContractId(ContractConstitutionFileBO constitutionFileBO);
}
