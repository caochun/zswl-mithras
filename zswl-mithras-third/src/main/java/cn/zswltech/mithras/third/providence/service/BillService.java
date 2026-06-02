package cn.zswltech.mithras.third.providence.service;

import cn.zswltech.mithras.third.providence.entity.BillOverdue;
import cn.zswltech.mithras.third.providence.req.BillOverdueReq;
import cn.zswltech.mithras.third.providence.rsp.BillOverdueRsp;
import com.zswltec.providence.dto.base.PageR;
import com.zswltec.providence.dto.base.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 14:21
 */
public interface BillService {
    R overdueListImport(MultipartFile multipartFile, String busiDate);

    void refreshData(List<BillOverdue> billOverdueList, String busiDate);

    PageR<BillOverdueRsp> overdueList(BillOverdueReq req);

    /**
     * 最新的逾期名单
     * @param busiDate 业务日期
     * @return
     */
    List<BillOverdue> latestOverdueList(String busiDate);
}
