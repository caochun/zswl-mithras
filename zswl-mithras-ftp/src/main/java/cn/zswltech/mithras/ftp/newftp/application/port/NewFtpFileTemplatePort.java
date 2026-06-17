package cn.zswltech.mithras.ftp.newftp.application.port;

import java.io.InputStream;

public interface NewFtpFileTemplatePort {

    InputStream getTemplate(String templateType, String filename);
}
