import React, { useEffect, useState } from 'react'
import { Descriptions, Space, Tooltip } from 'antd'
import { observer, getQuery } from '@zswl/admin'
import { PureAmountFormat } from '@/components/Format'
import Api from '@/api/customer/maintainApi'
import styles from './index.less'
import {
  certTypeList,
  currencyTypeList,
  orgScaleTypeList,
  orgTypeList,
  economyTypeList,
  continuousStatusList,
  genderTypeList,
  clientTypeList,
  domesticOrAbroadObj,
} from '../general'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import { App } from '@zswl/components'
import useGetRegion from './useGetRegion'
import IconFont from '@/components/Icon'
import { getIsClientDetailParams } from '@/utils/domains/customer/CustomerUtils'
import { BlackInfo } from '@/components/BlackInfo/BlackInfoEntries'

export { BlackInfo }
const IndustrialDetail = ({ id, save, num, startUserId, businessVersion, store }) => {
  const flag = getQuery('flag')
  const isFormApproval = getQuery('typeId') == 'approval'
  const domesticOrAbroad = getQuery('domesticOrAbroad')
  const [result, setResult] = useState({})
  const [compare, setCompare] = useState({})
  const [isDomestic, setIsDomestic] = useState(true)
  const initDetail = async () => {
    const data = await Api.commerceDetail({
      clientId: id,
      businessVersion,
      startUserId,
      ...getIsClientDetailParams(),
    })
    const newIsDomestic = data?.domesticOrAbroad === 'DOMESTIC'
    setIsDomestic(newIsDomestic)
    setResult(data)
    store.setBaseInfo(data)
  }
  const createcommerceInfo = async () => {
    const data = await Api.createcommerceInfo({ clientId: id })
    const newIsDomestic = data?.domesticOrAbroad === 'DOMESTIC'
    setIsDomestic(newIsDomestic)
    setResult(data)
  }
  // 对比接口
  const getApprovalCommercedetail = async () => {
    const data = await Api.getApprovalCommercedetail({ clientId: id, startUserId, businessVersion })
    setCompare(data)
  }
  const { region } = useGetRegion()
  // 如果是 查看详情（ flag == 'info'）、法人客户保存以后（num == 2， save == true ），新建跳过来的境外客户（flag == 'create' && domesticOrAbroad === 'ABROAD'）
  // 调用详情接口，否则就调用天眼查接口
  useEffect(() => {
    if (id) {
      if (
        isFormApproval ||
        flag == 'info' ||
        num == 2 ||
        save == true ||
        (flag == 'create' && domesticOrAbroad === 'ABROAD')
      ) {
        initDetail()
      } else {
        createcommerceInfo()
      }
    }
  }, [id, save, flag, num])

  useEffect(() => {
    if (getQuery('typeId') == 'approval') {
      getApprovalCommercedetail()
    }
  }, [getQuery('typeId')])
  const starDom = (name, must, obj) => {
    return (
      <span className={styles.colorsWrap} style={{ position: 'relative' }}>
        {must && (
          <span
            className={styles.colors}
            style={{ color: ' #eb2222', padding: ' 4px', position: 'absolute', left: ' -13px' }}
          >
            *
          </span>
        )}
        <span style={{ color: obj?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>{name}</span>
      </span>
    )
  }

  return (
    <div>
      <Descriptions
        title="工商信息"
        bordered
        column={2}
        labelStyle={{ background: '#F5F6FA', height: '48px' }}
        size={'small'}
        className={styles.des}
      >
        <Descriptions.Item
          label={starDom('客户名称', true, compare.clientName)}
          labelStyle={{ width: '180px' }}
          contentStyle={{ width: '400px' }}
        >
          <Space>
            <Tooltip title={result.clientName} placement="topLeft">
              <div className={styles.bizScope}>
                <span
                  style={{ color: compare?.clientName?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}
                >
                  {result.clientName}
                </span>
              </div>
            </Tooltip>
            <BlackInfo params={{ unifiedSocialCreditCode: result.uscCode, clientId: id }} />
          </Space>
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom('客户分类', false, compare.clientType)}
          labelStyle={{ width: '180px' }}
          contentStyle={{ width: '400px' }}
        >
          <span style={{ color: compare?.clientType?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
            {clientTypeList[result.clientType]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom('机构类型', false, compare.domesticOrAbroad)}
          labelStyle={{ width: '180px' }}
          contentStyle={{ width: '400px' }}
        >
          <span
            style={{ color: compare?.domesticOrAbroad?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}
          >
            {domesticOrAbroadObj[result.domesticOrAbroad]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('客户编号', false, compare.clientCode)}>
          <span style={{ color: compare?.clientCode?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
            {result.clientCode}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('中征码', false, compare?.zhongZhengCode)}>
          <span
            style={{ color: compare?.zhongZhengCode?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}
          >
            {result.zhongZhengCode}
          </span>
        </Descriptions.Item>
        {isDomestic ? (
          <Descriptions.Item label={starDom('统一社会信用代码', false, compare?.uscCode)}>
            <span style={{ color: compare?.uscCode?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
              {result.uscCode}
            </span>
          </Descriptions.Item>
        ) : (
          <Descriptions.Item label={starDom('特殊机构代码', false, compare?.specialOrgCode)}>
            <span
              style={{ color: compare?.specialOrgCode?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}
            >
              {result.specialOrgCode}
            </span>
          </Descriptions.Item>
        )}
        <Descriptions.Item label={starDom('成立日期', isDomestic, compare.establishDate)}>
          <span style={{ color: compare?.establishDate?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
            {result.establishDate}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('核准日期', isDomestic, compare.approvalDate)}>
          <span style={{ color: compare?.approvalDate?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
            {result.approvalDate}
          </span>
        </Descriptions.Item>
        {/* <Descriptions.Item
          label={starDom('营业许可证是否为长期', isDomestic, compare.bizLicenceLongTerm)}
        >
          {result.bizLicenceLongTerm == null ? '' : result.bizLicenceLongTerm ? '是' : '否'}
        </Descriptions.Item> */}
        <Descriptions.Item
          label={starDom('营业许可证到期日', isDomestic, compare.bizLicenseEndDate)}
        >
          <span
            style={{ color: compare?.bizLicenseEndDate?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}
          >
            {result.bizLicenseEndDate}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('存续状态', isDomestic, compare.continuousStatus)}>
          <span
            style={{ color: compare?.continuousStatus?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}
          >
            {continuousStatusList[result.continuousStatus]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('国标行业分类', true, compare.industryTypeName)}>
          <span
            style={{ color: compare?.industryTypeName?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}
          >
            {result.industryTypeName}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('风控行业分类', true, compare.industryTypeName)}>
          <span
            style={{
              color: compare?.riskControlIndustryClassify?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {App.matchOption('riskControlIndustryClassify', result.riskControlIndustryClassify)
              ?.label ?? '-'}
          </span>
        </Descriptions.Item>

        <Descriptions.Item label={starDom('经济类型', true, compare.economyType)}>
          <span style={{ color: compare?.economyType?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
            {economyTypeList[result.economyType]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('组织机构类型', isDomestic, compare.orgType)}>
          <span style={{ color: compare?.orgType?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
            {orgTypeList[result.orgType]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('企业规模', isDomestic, compare.orgScale)}>
          <span style={{ color: compare?.orgScale?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>
            {App.matchOption('orgScaleType', result.orgScale).label}
          </span>
        </Descriptions.Item>

        <Descriptions.Item label={starDom('注册资本(元)', isDomestic, compare.registerCapital)}>
          <span
            style={{
              color: compare?.registerCapital?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {PureAmountFormat(result.registerCapital)}
          </span>
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom('注册资本币种', isDomestic, compare.registerCurrencyType)}
        >
          <span
            style={{
              color: compare?.registerCurrencyType?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {currencyTypeList[result.registerCurrencyType]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('实收资本(元)', isDomestic, compare.realCapital)}>
          <span
            style={{
              color: compare?.realCapital?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {PureAmountFormat(result.realCapital)}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('实收资本币种', false, compare.realCurrencyType)}>
          <span
            style={{
              color: compare?.realCurrencyType?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {currencyTypeList[result.realCurrencyType]}
          </span>
        </Descriptions.Item>

        <Descriptions.Item
          label={starDom('法人代表', !result.groupFlag && isDomestic, compare.corpRepresent)}
        >
          <span
            style={{
              color: compare?.corpRepresent?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {result.corpRepresent}
          </span>
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom('法人性别', !result.groupFlag && isDomestic, compare.corpGender)}
        >
          <span
            style={{
              color: compare?.corpGender?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {genderTypeList[result.corpGender]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom('法人证件类型', !result.groupFlag && isDomestic, compare.corpCertType)}
        >
          <span
            style={{
              color: compare?.corpCertType?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {certTypeList[result.corpCertType]}
          </span>
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom('法人证件号码', !result.groupFlag && isDomestic, compare.corpCertCode)}
        >
          <span
            style={{
              color: compare?.corpCertCode?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {result.corpCertCode}
          </span>
        </Descriptions.Item>

        <Descriptions.Item
          label={
            <Tooltip title="上市公司：仅包括 A 股（沪深主板、中小板、创业板）">
              {starDom('企业性质', isDomestic, compare.enterpriseNature)}
              <IconFont type="icon-icon_info"></IconFont>
            </Tooltip>
          }
        >
          <span
            style={{
              color: compare?.enterpriseNature?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {getKeyOptionsLabelMapPlus('enterpriseNatureEnum')[result.enterpriseNature] || '-'}
          </span>
        </Descriptions.Item>
        <Descriptions.Item
          label={
            <Tooltip title="上市公司：仅包括 A 股（沪深主板、中小板、创业板）">
              {starDom('是否由上市公司控股', isDomestic, compare.ownershipType)}
              <IconFont type="icon-icon_info"></IconFont>
            </Tooltip>
          }
        >
          <span
            style={{
              color: compare?.ownershipType?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {getKeyOptionsLabelMapPlus('ownershipTypeEnum')[result.ownershipType] || '-'}
          </span>
        </Descriptions.Item>

        <Descriptions.Item label={starDom('是否关联方', isDomestic, compare.isRelated)}>
          <span
            style={{
              color: compare?.isRelated?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {result.isRelated == null ? '' : result.isRelated == 1 ? '是' : '否'}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('是否集团公司', isDomestic, compare.groupFlag)}>
          <span
            style={{
              color: compare?.groupFlag?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {result.groupFlag == null ? '-' : result.groupFlag == 1 ? '是' : '否'}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('所属集团', isDomestic, compare.belongGroupClientName)}>
          <span
            style={{
              color: compare?.belongGroupClientName?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {result.belongGroupClientName || '-'}
          </span>
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom(
            '指标隶属省份',
            !result.groupFlag && isDomestic,
            compare.provinceOfAffiliation
          )}
        >
          <span
            style={{
              color: compare?.provinceOfAffiliation?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            {App.matchOption(region, result?.provinceOfAffiliation)?.label ?? '-'}
          </span>
        </Descriptions.Item>
        <Descriptions.Item label={starDom('业务范围', isDomestic, compare.bizScope)} span={2}>
          {/* <Tooltip title={result.bizScope} placement="topLeft"> */}
          <span
            style={{
              color: compare?.bizScope?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)',
            }}
          >
            <div className={styles.yewu}>{result.bizScope}</div>
          </span>
          {/* </Tooltip> */}
        </Descriptions.Item>
      </Descriptions>
    </div>
  )
}

export default observer(IndustrialDetail)
