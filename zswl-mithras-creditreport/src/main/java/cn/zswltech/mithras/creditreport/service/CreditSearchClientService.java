package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchClientQuery;

public interface CreditSearchClientService {


    /**
     * 征信报告查询列表
     * @param req 征信报告查询请求参数
     * @return 征信报告查询列表
     */
    PageR<CreditReportListDTO> list(CreditSearchClientQuery req);

    /**
     * 删除征信报告
     * @param id
     * @return
     */
    void delete(Long id);
}
