package cn.zswltech.mithras.others.render.baoli;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzgt.YzGtBaoLiBizRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzgt.YzGtBaoLiTransferConfirmRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzgt.YzGtBaoLiTransferNotifyRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzgt.YzGtBaoLiTransferRegisterProtocolRender;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yibin
 */

public class 保理有追共同RenderTest extends ApplicationTest {
    private static final Path basePath = Paths.get(System.getProperty("user.home"), "Desktop");


    @SneakyThrows
    @Test
    public void test4() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0009)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("1-4应收账款转让登记协议", ".docx", basePath.toFile()));
        getBean(YzGtBaoLiTransferRegisterProtocolRender.class).render(fos, baseInfo);
    }

    @SneakyThrows
    @Test
    public void test3() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0009)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("1-3应收账款转让通知书及回执", ".docx", basePath.toFile()));
        getBean(YzGtBaoLiTransferNotifyRender.class).render(fos, baseInfo);
    }


    @SneakyThrows
    @Test
    public void test2() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0009)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("1-2应收账款转让申请暨确认书", ".docx", basePath.toFile()));
        getBean(YzGtBaoLiTransferConfirmRender.class).render(fos, baseInfo);
    }

    @SneakyThrows
    @Test
    public void test1() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0009)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("1-1国内保理合同（公开型有追索权", ".docx", basePath.toFile()));
        getBean(YzGtBaoLiBizRender.class).render(fos, baseInfo);
    }
}
