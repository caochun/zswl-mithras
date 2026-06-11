package cn.zswltech.mithras.customer.versioning;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListREQ;
import cn.zswltech.mithras.dto.client.bankaccount.NewCorpBankAccountListRSP;
import cn.zswltech.mithras.customer.model.client.NewCorpBankAccountLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface NewCorpBankAccountLibService extends IService<NewCorpBankAccountLib> {
    List<NewCorpBankAccountLib> listBy(Long clientId, String version);
    PageR<NewCorpBankAccountListRSP> list(CorpBankAccountListREQ req);
}
