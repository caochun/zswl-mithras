package cn.zswltech.mithras.blackgray.service;


import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.external.SearchEnterpriseDTO;

import java.util.List;

/**
 * 风险画像服务
 */
public interface PortraitService {

    /**
     * 企e查 - 机构搜索
     * @param name
     * @param creditCode
     * @return
     */
    R<List<SearchEnterpriseDTO.EnterpriseDTO>> outerSearch(String name, String creditCode);


}
