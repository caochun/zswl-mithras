package cn.zswltech.mithras.customer.versioning;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.contactinfo.NewCorpContactInfoListRSP;
import cn.zswltech.mithras.customer.model.client.NewCorpContactInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface NewCorpContactInfoLibService extends IService<NewCorpContactInfoLib> {
    PageR<NewCorpContactInfoListRSP> list(CorpContactInfoListREQ req);
}
