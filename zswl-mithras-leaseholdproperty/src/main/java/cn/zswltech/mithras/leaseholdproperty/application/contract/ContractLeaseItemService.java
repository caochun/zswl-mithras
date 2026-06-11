package cn.zswltech.mithras.leaseholdproperty.application.contract;

import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.*;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
public interface ContractLeaseItemService extends IService<ContractLeaseItem> {
    void importExcel(InputStream inputStream, Long contractId);

    void exportExcel(OutputStream outputStream, ContractLeaseItemExportREQ req);

    List<ContractLeaseItem> listByContractId(Long contractId);

    void removeByContractId(Long contractId);

    ContractLeaseItemListRSP pageList(ContractLeaseItemListREQ req);

    LeaseItemInfo getNewestOne(ContractSingleIdREQ req);

    ContractPreChooseLeaseItemRSP getPreChooseLeaseItem(ContractPreChooseLeaseItemREQ req);

    void chooseLeaseItem(ContractChooseLeaseItemREQ req);

    void unbindLeaseItem(Long contractId);

    void initLeaseItem(Long contractId, Long projReviewId, LeaseItemInfo leaseItemInfo);

    void copyLeaseItemFile(Long contractId, LeaseItemInfo leaseItemInfo);
}
