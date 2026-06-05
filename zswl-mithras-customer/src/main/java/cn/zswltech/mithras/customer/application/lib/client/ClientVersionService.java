package cn.zswltech.mithras.customer.application.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionListREQ;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionListRSP;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface ClientVersionService {

    /**
     * 寻找某个客户最新版本
     * @param clientId
     * @return
     */
    CommonVersion findNewestVersion(Long clientId);

    /**
     * 判断数据是否发生变动
     * @param clientId
     * @return
     */
    ChangeDTO checkActualChange(Long clientId);

    /**
     * 把版本表最新版本数据回写到临时表
     * @param clientId
     */
    void reset(Long clientId);

    /**
     * 数据校验
     * 直接抛出异常
     */
    void validateData(Long clientId);

    /**
     * 客户版本间数据比较
     * @param id
     * @return
     */
    CommonVersionDiffRSP comparePreVersion(Long id);

    CommonVersionDiffRSP compare(CommonVersion newVersion, CommonVersion oldVersion);

    /**
     * 把临时表数据全量copy到版本表
     * @param clientId
     * @param type
     */
    void recordVersion(Long clientId, VersionTypeEnum type, Long operatorId);

    /**
     * 查询
     * @param req
     * @return
     */
    PageR<ClientVersionListRSP> selectPage(ClientVersionListREQ req);

}
