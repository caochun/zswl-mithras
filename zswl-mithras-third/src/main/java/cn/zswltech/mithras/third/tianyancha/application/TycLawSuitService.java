package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.third.externaldata.tianyancha.model.TycLawSuit;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 法律诉讼
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycLawSuitService extends IService<TycLawSuit> {

    List<TycLawSuit> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycLawSuit> newDataList);

    /**
     * 填充详情
     * @param tycLawSuit
     * @return
     */
    TycLawSuit fillDetail(TycLawSuit tycLawSuit);

}
