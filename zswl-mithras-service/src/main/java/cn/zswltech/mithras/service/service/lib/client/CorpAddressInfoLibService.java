package cn.zswltech.mithras.service.service.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.service.riskcontrol.dto.CorpAddressInfoLibDto;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface CorpAddressInfoLibService extends IService<CorpAddressInfoLib> {
    PageR<CorpAddressInfoListRSP> list(CorpAddressInfoListREQ req);

    List<Long> getClientIdsByProvince(CorpAddressInfoLibDto dto);

    List<CorpAddressInfoLib> listNewestAddressByClientId(Set<Long> clientIds);
}
