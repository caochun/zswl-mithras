package cn.zswltech.mithras.customer.application.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListRSP;
import cn.zswltech.mithras.customer.model.client.CorpContactInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface CorpContactInfoLibService extends IService<CorpContactInfoLib> {
    PageR<CorpContactInfoListRSP> list(CorpContactInfoListREQ req);
}
