package cn.zswltech.mithras.service.service.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListREQ;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListRSP;
import cn.zswltech.mithras.service.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpShareholderInfoLib;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface CorpShareholderInfoLibService extends IService<CorpShareholderInfoLib> {
    PageR<CorpShareholderInfoListRSP> list(CorpShareholderInfoListREQ req, SFunction<CorpShareholderInfoLib, ?> orderBy, String orderType);
}
