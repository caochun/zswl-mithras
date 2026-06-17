package cn.zswltech.mithras.application.orchestration.adapter.ftp;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.customer.enums.client.CustomerEntityClassify;
import cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum;
import cn.zswltech.mithras.customer.enums.client.OwnershipTypeEnum;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.ftp.newftp.application.port.NewFtpCustomerFactPort;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpBusinessVersion;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class NewFtpCustomerFactPortAdapter implements NewFtpCustomerFactPort {

    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;

    @Override
    public boolean isRelatedClient(Long clientId) {
        List<CorpCommerceInfo> corpCommerceInfoList = corpCommerceInfoService.findByClientId(clientId);
        if (CollectionUtil.isEmpty(corpCommerceInfoList)) {
            return false;
        }
        return Objects.equals(corpCommerceInfoList.get(0).getIsRelated(), YesOrNoNumberEnum.YES.getCode());
    }

    @Override
    public String getCustomerEntityClassify(Long mainTenantryId, List<Long> guarantorIdList, String ftpBusinessVersion) {
        return getCEntityClassifyByMainTenantryId(mainTenantryId, guarantorIdList, ftpBusinessVersion).name();
    }

    private CustomerEntityClassify getCEntityClassifyByMainTenantryId(Long mainTenantryId, List<Long> guarantorIdList, String ftpBusinessVersionName) {
        CorpCommerceInfoLib commerceInfoLib = queryLatestCommerceInfo(mainTenantryId);
        if (Objects.isNull(commerceInfoLib)) {
            throw MithrasException.newException("客户工商信息不存在");
        }
        if (CharSequenceUtil.isBlank(commerceInfoLib.getEnterpriseNature())) {
            throw MithrasException.newException("企业性质信息为空");
        }
        EnterpriseNatureEnum enterpriseNatureEnum = EnterpriseNatureEnum.of(commerceInfoLib.getEnterpriseNature());
        if (Objects.isNull(enterpriseNatureEnum)) {
            throw MithrasException.newException("企业性质信息错误");
        }
        FtpBusinessVersion ftpBusinessVersion = FtpBusinessVersion.getByName(ftpBusinessVersionName);
        switch (enterpriseNatureEnum) {
            case myqtss:
                return CustomerEntityClassify.CUSTOMER_OTHER_LISTED;
            case myss:
            case gyss:
                if (ftpBusinessVersion == FtpBusinessVersion.V3) {
                    return CustomerEntityClassify.CUSTOMER_LISTED_STATE_OWNED;
                } else {
                    return CustomerEntityClassify.LISTED_COMPANY;
                }
            case gyfss:
                if (ftpBusinessVersion == FtpBusinessVersion.V3) {
                    return CustomerEntityClassify.CUSTOMER_LISTED_STATE_OWNED;
                }
            case myfss:
            case other:
                if (CharSequenceUtil.isBlank(commerceInfoLib.getOwnershipType())) {
                    throw new MithrasException("客户的控股类型为空");
                }
                OwnershipTypeEnum ownershipTypeEnum = OwnershipTypeEnum.ofName(commerceInfoLib.getOwnershipType());
                if (Objects.isNull(ownershipTypeEnum)) {
                    throw MithrasException.newException("客户的控股类型错误");
                }
                switch (ownershipTypeEnum) {
                    case DIRECT:
                        return CustomerEntityClassify.LISTED_COMPANY;
                    case NON:
                        if (enterpriseNatureEnum.equals(EnterpriseNatureEnum.gyfss)) {
                            return CustomerEntityClassify.STATE_OWNED_ENTERPRISE;
                        }
                        return CustomerEntityClassify.OTHER;
                    case INDIRECT:
                        return getEntityClassify(guarantorIdList, enterpriseNatureEnum);
                }
                break;
            default:
                break;
        }
        throw MithrasException.newException("客户类型错误");
    }

    private CustomerEntityClassify getEntityClassify(List<Long> guarantorIdList, EnterpriseNatureEnum enterpriseNatureEnum) {
        if (CollectionUtils.isEmpty(guarantorIdList)) {
            switch (enterpriseNatureEnum) {
                case gyss:
                    return CustomerEntityClassify.LISTED_COMPANY;
                case gyfss:
                    return CustomerEntityClassify.STATE_OWNED_ENTERPRISE;
                case myfss:
                    return CustomerEntityClassify.OTHER;
                default:
                    return CustomerEntityClassify.OTHER;
            }
        }
        for (Long guarantorId : guarantorIdList) {
            CorpCommerceInfoLib corpCommerceInfoLib = queryLatestCommerceInfo(guarantorId);
            if (Objects.isNull(corpCommerceInfoLib)) {
                throw MithrasException.newException("客户工商信息不存在");
            }
            if (CharSequenceUtil.isBlank(corpCommerceInfoLib.getEnterpriseNature())) {
                throw MithrasException.newException("企业性质信息为空");
            }
            EnterpriseNatureEnum enterpriseNature = EnterpriseNatureEnum.of(corpCommerceInfoLib.getEnterpriseNature());
            if (Objects.isNull(enterpriseNature)) {
                throw MithrasException.newException("担保人企业性质信息错误");
            }
            switch (enterpriseNature) {
                case myss:
                case gyss:
                    return CustomerEntityClassify.LISTED_COMPANY;
                case gyfss:
                case myfss:
                case other:
                    if (enterpriseNatureEnum.equals(EnterpriseNatureEnum.gyfss)) {
                        return CustomerEntityClassify.STATE_OWNED_ENTERPRISE;
                    }
                    return CustomerEntityClassify.OTHER;
                default:
                    break;
            }
        }
        throw MithrasException.newException("担保人的企业性质信息错误");
    }

    private CorpCommerceInfoLib queryLatestCommerceInfo(Long clientId) {
        return corpCommerceInfoLibMapper.selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                .eq(ClientBaseModel::getClientId, clientId)
                .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CorpCommerceInfoLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
    }
}
