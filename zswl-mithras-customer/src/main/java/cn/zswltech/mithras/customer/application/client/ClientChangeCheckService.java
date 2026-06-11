package cn.zswltech.mithras.customer.application.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.versioning.dto.CorpCommerceInfoCompareDTO;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.enums.client.DomesticOrAbroad;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.foundation.annotation.BirCompareColumn;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.foundation.util.BirCompareUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 工商信息必填变更才做流程审批。
 */
@Service
@Slf4j
public class ClientChangeCheckService {

    private static final String CLIENT_MODULE = "CLIENT";

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    protected CommonVersionMapper commonVersionMapper;

    /**
     * @return 2 无需设置按以前的逻辑走；1 需要审批；0 无需审批
     */
    public int checkCommerceInfoChange(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        if (Objects.isNull(client)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ClientType.CORPORATION.name().equals(client.getClientType())) {
            return 2;
        }
        CommonVersion newestVersion = findNewestVersion(clientId);
        if (Objects.isNull(newestVersion)) {
            return 2;
        }

        LambdaQueryWrapper<CorpCommerceInfoLib> libQuery = Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                .eq(CorpCommerceInfoLib::getVersion, newestVersion.getVersion())
                .eq(CorpCommerceInfoLib::getClientId, newestVersion.getMainId());
        List<CorpCommerceInfoLib> versionList = corpCommerceInfoLibMapper.selectList(libQuery);
        CorpCommerceInfoLib corpCommerceInfoLib = versionList.get(0);
        CorpCommerceInfoCompareDTO infoLibDto = new CorpCommerceInfoCompareDTO();
        BeanUtil.copyProperties(corpCommerceInfoLib, infoLibDto);

        LambdaQueryWrapper<CorpCommerceInfo> query = Wrappers.<CorpCommerceInfo>lambdaQuery()
                .eq(CorpCommerceInfo::getClientId, newestVersion.getMainId());
        List<CorpCommerceInfo> draftList = commerceInfoMapper.selectList(query);
        CorpCommerceInfo draft = draftList.get(0);
        CorpCommerceInfoCompareDTO draftDTO = new CorpCommerceInfoCompareDTO();
        BeanUtil.copyProperties(draft, draftDTO);

        BirCompareColumn.CompareLv compareLv = BirCompareColumn.CompareLv.ABROAD;
        if (DomesticOrAbroad.DOMESTIC.name().equals(client.getDomesticOrAbroad())) {
            compareLv = BirCompareColumn.CompareLv.DOMESTIC;
            if (!Objects.equals(1, draftDTO.getGroupFlag())) {
                compareLv = BirCompareColumn.CompareLv.DOMESTIC_UNGROUP;
            }
        }
        ChangeDTO changeDTO = BirCompareUtil.checkActualChange(infoLibDto, draftDTO, compareLv);
        if (Boolean.TRUE.equals(changeDTO.getNeedApprovalChangeFlag())) {
            return 1;
        }
        return 0;
    }

    public CommonVersion findNewestVersion(Long mainId) {
        return commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, CLIENT_MODULE)
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
    }
}
