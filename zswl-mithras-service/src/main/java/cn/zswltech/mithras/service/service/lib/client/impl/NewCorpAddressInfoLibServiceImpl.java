package cn.zswltech.mithras.service.service.lib.client.impl;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.service.enums.CorpAddressType;
import cn.zswltech.mithras.service.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.client.NewCorpAddressInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpAddressInfoLib;
import cn.zswltech.mithras.service.service.lib.client.CorpAddressInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.NewCorpAddressInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpAddressInfoLibHandlerImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.CorpAddressInfoLibDto;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NewCorpAddressInfoLibServiceImpl extends ServiceImpl<NewCorpAddressInfoLibMapper, NewCorpAddressInfoLib> implements NewCorpAddressInfoLibService {


}
