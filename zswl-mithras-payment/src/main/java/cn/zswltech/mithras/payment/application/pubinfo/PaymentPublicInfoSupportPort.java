package cn.zswltech.mithras.payment.application.pubinfo;

import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.payment.enums.pubinfo.PublicInfoFileTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PaymentPublicInfoSupportPort {
    String BUSINESS_TYPE_PUBLIC_INFO = "PUBLIC_INFO";

    Map<Long, String> clientId2Name(Collection<Long> clientIds);

    Map<Long, String> sysUserId2Name(Collection<Long> userIds);

    List<UserDO> getUserByDeptCode(String orgCode);

    List<PaymentPublicInfoMaterialSnapshot> listMaterialSnapshots(Collection<Long> belongIds);

    List<FileListRSP> listMaterialFiles(Collection<Long> belongIds, Collection<PublicInfoFileTypeEnum> fileTypes);

    Long addMaterial(InputStream inputStream, String fileName, Long belongId, String materialsType, String materialsSubType, YesOrNoNumberEnum systemGenerate) throws IOException;

    PaymentPublicInfoContractContextSnapshot getContractContextByContractId(Long contractId);

    PaymentPublicInfoContractParticipantSnapshot getContractParticipantSnapshot(Long contractId);

    List<PaymentPublicInfoContractSnapshot> listContractSnapshotsByProjReviewId(Long projReviewId);

    PublicInfoOuterQuerySnapshot getLatestOuterQuerySnapshot(Long publicInfoQueryId);
}
