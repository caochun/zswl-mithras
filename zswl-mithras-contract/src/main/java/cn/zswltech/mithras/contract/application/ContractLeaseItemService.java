package cn.zswltech.mithras.contract.application;

import cn.zswltech.mithras.contract.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractChooseLeaseItemREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemExportREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemListREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemListRSP;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractPreChooseLeaseItemREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractPreChooseLeaseItemRSP;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface ContractLeaseItemService extends IService<ContractLeaseItem> {
    void importExcel(InputStream inputStream, Long contractId);

    void exportExcel(OutputStream outputStream, ContractLeaseItemExportREQ req);

    List<ContractLeaseItem> listByContractId(Long contractId);

    void removeByContractId(Long contractId);

    ContractLeaseItemListRSP pageList(ContractLeaseItemListREQ req);

    ContractPreChooseLeaseItemRSP getPreChooseLeaseItem(ContractPreChooseLeaseItemREQ req);

    void chooseLeaseItem(ContractChooseLeaseItemREQ req);

    void unbindLeaseItem(Long contractId);
}
