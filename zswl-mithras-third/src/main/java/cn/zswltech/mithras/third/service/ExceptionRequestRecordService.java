package cn.zswltech.mithras.third.service;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.mapper.ExceptionRequestInfoMapper;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base persistence service for third-party request retry records.
 */
@Service
@Primary
public class ExceptionRequestRecordService extends ServiceImpl<ExceptionRequestInfoMapper, ExceptionRequestInfo> {

    public List<ExceptionRequestInfo> listNeedRetry(String platform) {
        return baseMapper.selectList(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                        .eq(ExceptionRequestInfo::getPlatform, platform)
                        .eq(ExceptionRequestInfo::getRetryFlag, YesOrNoNumberEnum.NO.getCode()))
                .stream()
                .filter(base -> base.getMaxRetryAmount() > base.getRetryAmount())
                .collect(Collectors.toList());
    }

    public ExceptionRequestInfo getLastRequest(String platform, String businessId) {
        return baseMapper.selectOne(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                .eq(ExceptionRequestInfo::getPlatform, platform)
                .eq(ExceptionRequestInfo::getBusinessId, businessId)
                .last(StringUtil.mysqlLimitOne()));
    }
}
