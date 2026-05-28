package cn.zswltech.mithras.service.factory.onlyoffice;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.api.dto.onlyoffice.DocDetailRSP;

/**
 * onlyoffice业务权限校验
 *
 * @author wangchuanhao
 * @date 2022/8/23 4:52 PM
 */
public interface OoBizHandler {

    void handle(DocDetailRSP docDetailRSP, MaterialsList materialsList);

    BusinessModuleEnum getBusinessModule();

}
