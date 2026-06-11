package cn.zswltech.mithras.contract.adapter.document;

import cn.zswltech.mithras.contract.mapper.contract.ContractSignInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.document.file.template.FileTemplateContractSignPort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FileTemplateContractSignPortAdapter implements FileTemplateContractSignPort {

    @Resource
    private ContractSignInfoMapper contractSignInfoMapper;

    @Override
    public void updateFaceSignShowFlag(String fileTemplateKey, Integer faceSignShowFlag) {
        ContractSignInfo contractSignInfo = new ContractSignInfo();
        contractSignInfo.setFaceSignShowFlag(faceSignShowFlag);
        contractSignInfoMapper.update(contractSignInfo,
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<ContractSignInfo>lambdaUpdate()
                        .eq(ContractSignInfo::getFileTemplateKey, fileTemplateKey));
    }
}
