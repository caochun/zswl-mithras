package cn.zswltech.mithras.customer.application.client;


import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckInterface;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoAddREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoModifyREQ;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.NormalBaseInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.NormalBaseInfo;
import cn.zswltech.mithras.customer.model.client.NormalBaseInfoLib;
import cn.zswltech.mithras.customer.mapper.normal.NormalBaseInfoMapper;
import cn.zswltech.mithras.customer.versioning.handler.impl.NormalBaseInfoLibHandlerImpl;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;

/**
 * @author junke
 */
@Service
@Slf4j
public class NormalBaseInfoService implements ClientDataSaveCheckInterface<NormalBaseInfo> {

    @Resource
    private NormalBaseInfoMapper baseInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private NormalBaseInfoLibMapper baseInfoLibMapper;
    @Resource
    private NormalBaseInfoLibHandlerImpl baseInfoLibHandler;
    @Resource
    private CustomerDictionaryPort customerDictionaryPort;


    @Transactional(rollbackFor = Throwable.class)
    public void add(NormalBaseInfoAddREQ req) {
        NormalBaseInfo info = copyProperties(req, NormalBaseInfo.class);
        check(info);
        baseInfoMapper.insert(info);
        recordClientStatus(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(NormalBaseInfoModifyREQ req) {
        NormalBaseInfo info = copyProperties(req, NormalBaseInfo.class);
        check(info);
        NormalBaseInfo detail = Optional.ofNullable(baseInfoMapper.selectOne(Wrappers.<NormalBaseInfo>lambdaQuery().eq(NormalBaseInfo::getClientId, req.getClientId()).last("LIMIT 1"))).orElse(new NormalBaseInfo());
        if (isNotNull(detail) && isNotNull(detail.getId())) {
            info.setId(detail.getId());
            baseInfoMapper.updateAnnotationIncludeNullById(info);
        } else {
            baseInfoMapper.insert(info);
        }
        LambdaUpdateWrapper<Client> clientLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        clientLambdaUpdateWrapper.eq(Client::getId, req.getClientId());
        //clientLambdaUpdateWrapper.set(Client::getClientName, req.getClientName());
        clientLambdaUpdateWrapper.set(Client::getClientCode, req.getClientCode());
        clientMapper.update(null, clientLambdaUpdateWrapper);
        recordClientStatus(info);
    }

    public NormalBaseInfoDetailRSP detail(Long clientId, String version) {
        if (StringUtils.isBlank(version)) {
            NormalBaseInfo info = Optional.ofNullable(baseInfoMapper.selectOne(Wrappers.<NormalBaseInfo>lambdaQuery().eq(NormalBaseInfo::getClientId, clientId).last("LIMIT 1"))).orElse(new NormalBaseInfo());
            NormalBaseInfoDetailRSP rsp = copyProperties(info, NormalBaseInfoDetailRSP.class);
            Client client = clientMapper.selectById(clientId);
            if (isNotNull(client)) {
                rsp.setClientCode(client.getClientCode());
                rsp.setClientType(client.getClientType());
                rsp.setClientName(client.getClientName());
                rsp.setCertNumber(client.getCertNumber());
                rsp.setCertType(client.getCertType());
            }
            rsp.setCountryName(customerDictionaryPort.addressCode2Display(rsp.getCountry()));
            return rsp;
        } else {
            NormalBaseInfoLib versionLib = Optional.ofNullable(baseInfoLibMapper.selectOne(Wrappers.<NormalBaseInfoLib>lambdaQuery().eq(NormalBaseInfoLib::getClientId, clientId).eq(NormalBaseInfoLib::getVersion, version).last("LIMIT 1")))
                    .orElse(new NormalBaseInfoLib());
            return Objects.isNull(versionLib) ? new NormalBaseInfoDetailRSP() : baseInfoLibHandler.actualLib2Rsp(versionLib);
        }
    }
}
