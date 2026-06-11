package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorListRSP;
import cn.zswltech.mithras.dto.contract.guarantor.GuaranteeAmountMultipleInfo;
import cn.zswltech.mithras.contract.convert.contract.ContractGuarantorConverter;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.foundation.port.ClientInfoResolver;
import cn.zswltech.mithras.contract.versioning.handler.ContractLibAbstractHandler;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractGuarantorLibHandler
        extends ContractLibAbstractHandler<ContractGuarantorLib, ContractGuarantor, ContractGuarantorListRSP> {

    @Resource
    private ContractGuarantorConverter converter;

    @Resource
    private ClientInfoResolver clientInfoResolver;

    @Override
    protected ContractGuarantorLib entity2Lib(ContractGuarantor f) {
        return BeanUtil.copyProperties(f, ContractGuarantorLib.class);
    }

    @Override
    protected ContractGuarantor lib2Entity(ContractGuarantorLib t) {
        return BeanUtil.copyProperties(t, ContractGuarantor.class);
    }

    @Override
    protected ContractGuarantorListRSP lib2Rsp(ContractGuarantorLib f) {
        return lib2RspList(ListUtil.toList(f)).get(0);
    }

    @Override
    protected List<ContractGuarantorListRSP> lib2RspList(List<ContractGuarantorLib> fList) {
        List<ContractGuarantorListRSP> rspList = fList.stream().map(f -> converter.entityToRSP(f)).collect(Collectors.toList());
        Map<Long, ClientInfo> clientMap = clientInfoResolver.clientId2Client(fList.stream().map(g -> {
            Set<Long> cIdSet = new HashSet<>();
            if (StringUtils.isNotBlank(g.getGuarantorIds())) {
                cIdSet.addAll(JSONArray.parseArray(g.getGuarantorIds(), Long.class));
            }
            if (StringUtils.isNotBlank(g.getGuaranteeAmountMultiple())) {
                cIdSet.addAll(JSONArray.parseArray(g.getGuaranteeAmountMultiple(), ContractGuarantor.GuaranteeMultipleJsonWrapper.class).stream().map(ContractGuarantor.GuaranteeMultipleJsonWrapper::getClientId).collect(Collectors.toSet()));
            }
            return cIdSet;
        }).flatMap(Collection::stream).collect(Collectors.toSet()));
        Map<Long, ContractGuarantorLib> libMap = fList.stream().collect(Collectors.toMap(ContractGuarantorLib::getId, c -> c));
        for (ContractGuarantorListRSP rsp : rspList) {
            ContractGuarantorLib lib = libMap.get(rsp.getId());
            if (rsp.getGuarantorIds() != null && !rsp.getGuarantorIds().isEmpty()) {
                //担保人信息
                rsp.setGuarantorInfo(rsp.getGuarantorIds().stream().map(gId -> clientMap.get(gId)).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            // 处理担保金额
            if (Objects.nonNull(lib.getGuaranteeAmountSingle())) {
                rsp.setAmountSingle(lib.getGuaranteeAmountSingle());
            }
            if (StrUtil.isNotBlank(lib.getGuaranteeAmountMultiple())) {
                List<ContractGuarantor.GuaranteeMultipleJsonWrapper> list = JSONUtil.toList(lib.getGuaranteeAmountMultiple(), ContractGuarantor.GuaranteeMultipleJsonWrapper.class);
                if (CollectionUtil.isNotEmpty(list)) {
                    List<GuaranteeAmountMultipleInfo> infoList = new ArrayList<>(list.size());
                    for (ContractGuarantor.GuaranteeMultipleJsonWrapper jsonWrapper : list) {
                        GuaranteeAmountMultipleInfo info = new GuaranteeAmountMultipleInfo();
                        info.setClientId(jsonWrapper.getClientId());
                        info.setClientName(Optional.ofNullable(clientMap.get(jsonWrapper.getClientId())).map(ClientInfo::getClientName).orElse(""));
                        info.setAmount(jsonWrapper.getAmount());
                        infoList.add(info);
                    }
                    rsp.setAmountMultiple(infoList);
                }
            }
        }
        return rspList;
    }

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.GUARANTOR;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
