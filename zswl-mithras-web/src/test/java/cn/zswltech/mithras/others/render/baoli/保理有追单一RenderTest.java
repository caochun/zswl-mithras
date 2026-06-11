package cn.zswltech.mithras.others.render.baoli;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzdy.YzDyBaoLiBizRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzdy.YzDyBaoLiTransferConfirmRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzdy.YzDyBaoLiTransferNotifyRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzdy.YzDyBaoLiTransferRegisterProtocolRender;
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

public class 保理有追单一RenderTest extends ApplicationTest {
    private static final Path basePath = Paths.get(System.getProperty("user.home"), "Desktop");


    @SneakyThrows
    @Test
    public void test4() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("应收账款转让登记协议（有追单一卖方）", ".docx", basePath.toFile()));
        getBean(YzDyBaoLiTransferRegisterProtocolRender.class).render(fos, baseInfo);
    }

    @SneakyThrows
    @Test
    public void test3() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("应收账款转让通知书及回执（有追单一卖方", ".docx", basePath.toFile()));
        getBean(YzDyBaoLiTransferNotifyRender.class).render(fos, baseInfo);
    }


    @SneakyThrows
    @Test
    public void test2() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("应收账款转让申请暨确认书（有追单一卖方）", ".docx", basePath.toFile()));
        getBean(YzDyBaoLiTransferConfirmRender.class).render(fos, baseInfo);
    }

    @SneakyThrows
    @Test
    public void test1() {
        ContractBaseInfo baseInfo = getBean(ContractBaseInfoService.class).getOne(
                Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, "浙商租【2023】保理字第(B-0008)号"));
        FileOutputStream fos = new FileOutputStream(File.createTempFile("1-1国内保理合同（公开型有追索权）-单一卖方", ".docx", basePath.toFile()));
        getBean(YzDyBaoLiBizRender.class).render(fos, baseInfo);
    }
}
