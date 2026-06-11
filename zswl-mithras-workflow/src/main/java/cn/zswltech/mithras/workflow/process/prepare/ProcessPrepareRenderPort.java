package cn.zswltech.mithras.workflow.process.prepare;

import java.io.OutputStream;

public interface ProcessPrepareRenderPort {

    byte[] render(Long detailId);

    void downDocx(OutputStream outputStream, Long detailId);
}
