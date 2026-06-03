package cn.zswltech.mithras.archives.interfaces.template;

import cn.zswltech.mithras.api.archives.ArchiveTemplateManageApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.archives.*;
import cn.zswltech.mithras.archives.application.template.ArchiveTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author wwj
 * @create: 2023-02-27
 **/
@RestController
@Slf4j
public class ArchiveTemplateManageController implements ArchiveTemplateManageApi {

    @Resource
    private ArchiveTemplateService archiveTemplateService;

    @Override
    public R<PageR<ArchiveTemplateListRSP>> list(@Valid ArchiveTemplateListREQ req) {
        return R.ok(archiveTemplateService.list(req));
    }

    @Override
    public R<Void> add(@Valid ArchiveTemplateAddREQ req) {
        return R.ok(archiveTemplateService.addTemplate(req));
    }

    @Override
    public R<ArchiveTemplateInfoRSP> info(@Valid ArchiveTemplateInfoREQ req) {
        return R.ok(archiveTemplateService.info(req));
    }

    @Override
    public R<Void> updateStatus(@Valid ArchiveTemplateUpdateREQ req) {
        return R.ok(archiveTemplateService.updateStatus(req));
    }
}
