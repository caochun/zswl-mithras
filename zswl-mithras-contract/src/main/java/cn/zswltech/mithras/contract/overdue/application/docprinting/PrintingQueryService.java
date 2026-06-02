package cn.zswltech.mithras.contract.overdue.application.docprinting;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.contract.overdue.application.dto.LitigationListDto;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.application.query.LitigationPageQuery;
import cn.zswltech.mithras.contract.overdue.application.query.PrintingPageQuery;

/**
 * @description: 依赖反转，application层定义的query接口，在infrastructure层做实现
 * @author: zhaozhengkang
 * @date: 2024/10/22 16:59
 */
public interface PrintingQueryService {

    PageR<PrintingListDto> page(PrintingPageQuery query);
}
