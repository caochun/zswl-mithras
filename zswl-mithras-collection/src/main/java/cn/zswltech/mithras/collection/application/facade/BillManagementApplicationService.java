package cn.zswltech.mithras.collection.application.facade;

import cn.zswltech.mithras.dto.collection.BillManagementAddREQ;
import cn.zswltech.mithras.dto.collection.BillManagementListREQ;
import cn.zswltech.mithras.dto.collection.BillManagementModifyREQ;
import cn.zswltech.mithras.dto.collection.BillManagementRemoveREQ;
import cn.zswltech.mithras.collection.model.BillManagement;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface BillManagementApplicationService {

    void add(BillManagementAddREQ req);

    void modify(BillManagementModifyREQ req);

    Page<BillManagement> list(BillManagementListREQ req);

    void remove(BillManagementRemoveREQ req);
}
