package cn.zswltech.mithras.application.orchestration.adapter.contract;

import cn.zswltech.mithras.contract.mapper.contract.ContractSignInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractSignInfo;
import cn.zswltech.mithras.document.application.port.FileTemplateContractSignPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
        contractSignInfoMapper.update(contractSignInfo, Wrappers.<ContractSignInfo>lambdaUpdate()
                .eq(ContractSignInfo::getFileTemplateKey, fileTemplateKey));
    }
}
