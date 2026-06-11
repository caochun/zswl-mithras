package cn.zswltech.mithras.customer.application.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListRSP;
import cn.zswltech.mithras.customer.mapper.model.client.NormalBankAccountLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface NormalBankAccountLibService extends IService<NormalBankAccountLib> {

    PageR<NormalBankAccountListRSP> list(NormalBankAccountListREQ req);

}
