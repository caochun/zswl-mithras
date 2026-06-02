package cn.zswltech.mithras.others.render;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.gendoc.render.ContractMainShipTradeZLHZRender;
import cn.zswltech.mithras.service.gendoc.render.ContractMainShipZLHZRender;
import cn.zswltech.mithras.service.gendoc.render.ContractShipPromiseRender;
import cn.zswltech.mithras.service.gendoc.render.contractzlzz.ContractZLZZShipMainRender;
import cn.zswltech.mithras.service.gendoc.render.contractzlzz.ContractZLZZShipTradeRender;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2024/10/15
 * @description
 */
public class ContractShipRenderTest extends ApplicationTest {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractMainShipZLHZRender contractMainShipZLHZRender;
    @Resource
    private ContractMainShipTradeZLHZRender contractMainShipTradeZLHZRender;
    @Resource
    private ContractZLZZShipMainRender contractZLZZShipMainRender;
    @Resource
    private ContractZLZZShipTradeRender contractZLZZShipTradeRender;
    @Resource
    private ContractShipPromiseRender contractShipPromiseRender;

    @Test
    public void zlhzTest() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/result.docx");
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(4746L);
        contractMainShipZLHZRender.render(outputStream, contractBaseInfo);
    }

    @Test
    public void zlhzmmTest() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/result.docx");
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(4746L);
        contractMainShipTradeZLHZRender.render(outputStream, contractBaseInfo);
    }

    @Test
    public void zlzzTest() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/result.docx");
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(4734L);
        contractZLZZShipMainRender.render(outputStream, contractBaseInfo);
    }

    @Test
    public void zlzzmmTest() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/result.docx");
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(4734L);
        contractZLZZShipTradeRender.render(outputStream, contractBaseInfo);
    }

    @Test
    public void cnhTest() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/result.docx");
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(4734L);
        contractShipPromiseRender.render(outputStream, contractBaseInfo);
    }
}
