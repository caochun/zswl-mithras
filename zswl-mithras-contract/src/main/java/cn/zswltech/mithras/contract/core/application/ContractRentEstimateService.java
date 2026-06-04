package cn.zswltech.mithras.contract.core.application;

import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateExportREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateGenerateREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
public interface ContractRentEstimateService extends IService<ContractRentEstimate> {
    void importExcel(MultipartFile file, Long contractId, String planStartDate, String scene) throws Exception;

    List<ContractRentEstimate> listByContractId(Long contractId, String version);

    void exportExcel(ContractRentEstimateExportREQ req, OutputStream outputStream) throws Exception;

    void exportRichExcel(ContractRentEstimateExportREQ req, OutputStream outputStream) throws Exception;

    void generate(ContractRentEstimateGenerateREQ req);

    IRRCalculateResultRSP calculateIRR(SinglePkREQ req);

    void doOverwriteRentEstimate(Map<Integer, ContractRentEstimate> importMap, Long contractId, String planStartDate, String scene);

    ContractRentEstimate getLastOne(Long contractId);
}
