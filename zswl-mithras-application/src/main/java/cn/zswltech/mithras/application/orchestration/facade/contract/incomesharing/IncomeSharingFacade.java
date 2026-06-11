package cn.zswltech.mithras.application.orchestration.facade.contract.incomesharing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.incomesharing.IncomeSharingApplicationService;
import cn.zswltech.mithras.dto.incomesharing.*;
import cn.zswltech.mithras.application.orchestration.contract.ContractIncomeSharingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

/**
 * @author yupengfei
 * @date 2024/6/7 19:19
 */
@Service
public class IncomeSharingFacade implements IncomeSharingApplicationService {


    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;

    @Override
    public R<PageR<IncomeSharingListRSP>> incomeSharingList(IncomeSharingListREQ req) {
        return R.ok(contractIncomeSharingService.incomeSharingQuery(req));
    }

    @Override
    public R<PageR<IncomeSharingRSP>> incomeSharingDetail(IncomeSharingDetailREQ req) {
        return contractIncomeSharingService.incomeSharingDetail(req);
    }

    @Override
    public void incomeSharingDownload(IncomeSharingListREQ req, ServletOutputStream outputStream) {
        contractIncomeSharingService.exportIncomeSharingList(req, outputStream);
    }

    @Override
    public void incomeSharingDetailDownload(IncomeSharingDetailREQ req, ServletOutputStream outputStream) {
        contractIncomeSharingService.exportIncomeSharingDetail(req, outputStream);
    }
}
