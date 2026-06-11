package cn.zswltech.mithras.ftp.newftp.adapter;

import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpFileTemplatePort;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

@Service
public class NewFtpFileTemplatePortAdapter implements NewFtpFileTemplatePort {

    @Resource
    private FileTemplateService fileTemplateService;

    @Override
    public InputStream getTemplate(String templateType, String filename) {
        return fileTemplateService.getTemplate(templateType, filename);
    }
}
