package cn.zswltech.mithras.collection.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentDetailExportREQ;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentDetailREQ;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentListREQ;
import cn.zswltech.mithras.dto.contractcp.ContractCollectionPaymentListRSP;
import cn.zswltech.mithras.dto.contractcp.ContractInfoRSP;
import cn.zswltech.mithras.dto.contractcp.ContractRentActualInfoRSP;
import cn.zswltech.mithras.dto.contractcp.ContractcpContractDetailREQ;

import javax.servlet.ServletOutputStream;
import java.util.List;

public interface ContractCollectionPaymentApplicationService {

    PageR<ContractCollectionPaymentListRSP> list(ContractCollectionPaymentListREQ req);

    void exportList(ContractCollectionPaymentListREQ req, ServletOutputStream outputStream);

    R<Void> pushRentNotify(ContractCollectionPaymentListREQ req);

    List<SelectRSP> contractList(ContractcpContractDetailREQ req);

    ContractInfoRSP contractDetail(ContractcpContractDetailREQ req);

    List<SelectRSP> cashList(ContractcpContractDetailREQ req);

    PageR<ContractRentActualInfoRSP> cashDetail(ContractCollectionPaymentDetailREQ req);

    void exportCashDetail(ContractCollectionPaymentDetailExportREQ req, ServletOutputStream outputStream);
}
