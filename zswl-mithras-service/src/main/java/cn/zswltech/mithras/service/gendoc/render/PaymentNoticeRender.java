package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.service.gendoc.context.PaymentNoticeContext;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractBaseInfoLibHandler;
import com.deepoove.poi.XWPFTemplate;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * 租后 租金催收
 * 支付通知书渲染
 *
 * @author wangchuanhao
 * @date 2022/11/17 5:16 PM
 */
public class PaymentNoticeRender extends AbstractBasicRender<PaymentNoticeContext> {

    private static final String TEMPLATE_FILE_PATH = "/doc/支付通知书.docx";

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;

    @Override
    public String render(OutputStream outputStream, PaymentNoticeContext paymentNoticeContext) throws Exception {
        // 捞数据
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(paymentNoticeContext.getCollectionBaseInfoId());
        if (Objects.isNull(collectionBaseInfo)) {
            throw new MithrasException("收款信息不存在");
        }
        Client client = clientMapper.selectById(collectionBaseInfo.getClientId());
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(collectionBaseInfo.getContractId());
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfoLib.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfoLib.getProjSponsorUserId());

        // 填充渲染map
        HashMap<String, Object> renderMap = new HashMap<>();
        renderMap.put("clientName", client.getClientName());
        renderMap.put("contractCode", collectionBaseInfo.getContractCode());
        renderMap.put("phase", collectionBaseInfo.getPhase());
        renderMap.put("planCollectionDate", LocalDateTimeUtil.format(collectionBaseInfo.getPlanCollectionDate(), "yyyy/MM/dd"));
        renderMap.put("planCollectionDateFormal", LocalDateTimeUtil.format(collectionBaseInfo.getPlanCollectionDate(), "【yyyy】年【MM】月【dd】日"));
        renderMap.put("planCollectionAmount", Optional.ofNullable(collectionBaseInfo.getPlanCollectionAmount()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        renderMap.put("principal", Optional.ofNullable(collectionBaseInfo.getPrincipal()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        renderMap.put("interest", Optional.ofNullable(collectionBaseInfo.getInterest()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        renderMap.put("comment", StringUtils.isNotBlank(paymentNoticeContext.getComment()) ? paymentNoticeContext.getComment() : "/");
        // TODO 客户名称
        renderMap.put("accountName", "accountName");
        renderMap.put("accountBank", "accountBank");
        renderMap.put("accountNumber", "accountNumber");

        renderMap.put("sponsorUserName", userVO.getUserName());
        renderMap.put("sponsorTelephone", sponsorPhone);
        renderMap.put("noticeDateFormal", LocalDateTimeUtil.format(LocalDate.now(), "【yyyy】年【MM】月【dd】日"));


        // 渲染
        try (InputStream templateIs = ProjEstablishReportRender.class.getResourceAsStream(TEMPLATE_FILE_PATH)) {
            XWPFTemplate template = XWPFTemplate.compile(templateIs).render(renderMap);
            template.write(outputStream);
        }
        return "支付通知书" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

}
