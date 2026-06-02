package cn.zswltech.mithras.service.overdue.application.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.contract.overdue.application.query.CollectionPageQuery;

import java.util.List;

/**
 * @description: 依赖反转，application层定义的query接口，在infrastructure层做实现
 * @author: zhaozhengkang
 * @date: 2024/10/22 16:59
 */
public interface CollectionQueryService {

    List<CollectionListDto> page(CollectionPageQuery query);

}
