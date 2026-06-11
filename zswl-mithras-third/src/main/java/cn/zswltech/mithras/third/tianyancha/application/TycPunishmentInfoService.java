package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.customer.externaldata.tianyancha.model.TycPunishmentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 行政处罚
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycPunishmentInfoService extends IService<TycPunishmentInfo> {

    List<TycPunishmentInfo> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycPunishmentInfo> newDataList);


}
