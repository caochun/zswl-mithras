package cn.zswltech.mithras.service.gendoc;

import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2022/8/5
 * @description
 */
public interface IDocumentRender<T> {
    /**
     * 渲染文档
     *
     * @param outputStream 渲染完成的文档写入的输出流
     * @param t            原始数据
     * @return 文件名称
     * @throws Exception 异常
     */
    String render(OutputStream outputStream, T t) throws Exception;
}
