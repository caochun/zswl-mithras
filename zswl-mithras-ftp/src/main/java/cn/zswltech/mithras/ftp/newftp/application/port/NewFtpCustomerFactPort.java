package cn.zswltech.mithras.ftp.newftp.application.port;

import java.util.List;

public interface NewFtpCustomerFactPort {

    boolean isRelatedClient(Long clientId);

    String getCustomerEntityClassify(Long mainTenantryId, List<Long> guarantorIdList, String ftpBusinessVersion);
}
