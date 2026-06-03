package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.model.TycDishonest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 失信人
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycDishonestService extends IService<TycDishonest> {

    List<TycDishonest> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycDishonest> newDataList);

}
