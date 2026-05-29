package cn.zswltech.mithras.service.service.client;
import cn.zswltech.mithras.common.constant.ResultMsg;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.NormalSpouseAddREQ;
import cn.zswltech.mithras.dto.client.normal.NormalSpouseListREQ;
import cn.zswltech.mithras.dto.client.normal.NormalSpouseListRSP;
import cn.zswltech.mithras.dto.client.normal.NormalSpouseModifyREQ;
import cn.zswltech.mithras.service.enums.GenderType;
import cn.zswltech.mithras.service.mapper.lib.client.NormalSpouseLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.NormalBaseInfo;
import cn.zswltech.mithras.service.mapper.model.client.NormalSpouse;
import cn.zswltech.mithras.service.mapper.model.client.NormalSpouseLib;
import cn.zswltech.mithras.service.mapper.normal.NormalBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.normal.NormalSpouseMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.NormalSpouseLibHandlerImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.common.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * @author junke
 */
@Service
public class NormalSpouseService implements ClientDataSaveCheckInterface<NormalSpouse> {
    @Resource
    private NormalSpouseMapper spouseMapper;
    @Resource
    private NormalBaseInfoMapper baseInfoMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private NormalSpouseLibMapper spouseLibMapper;
    @Resource
    private NormalSpouseLibHandlerImpl spouseLibHandler;

    public void add(NormalSpouseAddREQ req) {
        NormalSpouse info = copyProperties(req, NormalSpouse.class);
        check(info);
        spouseMapper.insert(info);
        recordClientStatus(info);
    }

    public void modify(NormalSpouseModifyREQ req) {
        NormalSpouse originalInfo = spouseMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        NormalSpouse info = copyProperties(req, NormalSpouse.class);
        check(info);
        spouseMapper.updateAnnotationIncludeNullById(info);
        recordClientStatus(info);
    }

    public void remove(Long id) {
        NormalSpouse originalInfo = spouseMapper.selectById(id);
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        check(originalInfo);
        spouseMapper.deleteById(id);
        recordClientStatus(originalInfo);
    }

    public NormalSpouse detail(Long clientId) {
        return spouseMapper.selectOne(Wrappers.<NormalSpouse>lambdaQuery().eq(NormalSpouse::getClientId, clientId));
    }

    public PageR<NormalSpouseListRSP> list(NormalSpouseListREQ req) {
        PageR<NormalSpouseListRSP> resPage = null;
        if (StringUtils.isBlank(req.getVersion())) {
            // 编辑区
            Page<NormalSpouse> dataPage = spouseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<NormalSpouse>lambdaQuery().eq(NormalSpouse::getClientId, req.getClientId())
                            .orderByDesc(NormalSpouse::getUpdateTime)
            );
            List<NormalSpouseListRSP> list = BeanUtil.copyToList(dataPage.getRecords(), NormalSpouseListRSP.class);
            resPage = PageR.of(list, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            // 版本
            Page<NormalSpouseLib> dataPage = spouseLibMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<NormalSpouseLib>lambdaQuery()
                            .eq(NormalSpouseLib::getClientId, req.getClientId())
                            .eq(NormalSpouseLib::getVersion, req.getVersion())
                            .orderByDesc(NormalSpouseLib::getUpdateTime)
            );
            List<NormalSpouseListRSP> list = dataPage.getRecords().stream().map(spouseLibHandler::actualLib2Rsp).collect(Collectors.toList());
            resPage = PageR.of(list, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        }

        Map<String, Long> idMap = clientService.getNormalByCertList(resPage.getList().stream().map(NormalSpouseListRSP::getCertNumber).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Client::getCertNumber, Client::getId));
        for (NormalSpouseListRSP rsp : resPage.getList()) {
            rsp.setSpouseClientId(idMap.get(rsp.getCertNumber()));
        }

        return resPage;
    }

    public List<NormalBaseInfo> selectList(Long clientId, String fuzzyName) {
        NormalBaseInfo baseInfo = baseInfoMapper.selectOne(Wrappers.<NormalBaseInfo>lambdaQuery().eq(NormalBaseInfo::getClientId, clientId));
        if (isNull(baseInfo)) {
            throw new MithrasException("未知性别");
        }
        String gender = baseInfo.getGender();
        String targetGender = null;
        if (GenderType.MALE.name().equals(gender)) {
            targetGender = GenderType.FEMALE.name();
        } else if (GenderType.FEMALE.name().equals(gender)) {
            targetGender = GenderType.MALE.name();
        }
        List<String> spouseCertNumberList = spouseMapper.selectList(Wrappers.<NormalSpouse>lambdaQuery().eq(NormalSpouse::getClientId, clientId))
                .stream().map(NormalSpouse::getCertNumber).collect(Collectors.toList());

        return baseInfoMapper.availableSpouseList(spouseCertNumberList, clientId, targetGender, fuzzyName);
    }
}
