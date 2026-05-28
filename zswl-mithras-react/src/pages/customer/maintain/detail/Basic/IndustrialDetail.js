import React, { useEffect, useState } from 'react'
import { Descriptions, Input, DatePicker, InputNumber, Cascader, Tooltip, Space } from 'antd'
import { Select, Form } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import moment from 'moment'
import { debounce as _debounce } from 'lodash'
import Api from './api'
import styles from './index.less'
import Amount from '@/components/Amount'
import useGetRegion from './useGetRegion'
import IconFont from '@/components/Icon'
import { getRiskControlIndustryClassifySelectOptions } from '@/utils'

const { TextArea } = Input
const dateFormat = 'yyyy-MM-DD'

const clientInit = [{ clientName: '无', id: -1 }]
const IndustrialDetail = ({ id, clientType, save, businessVersion, startUserId, store }) => {
  const domesticOrAbroad = getQuery('domesticOrAbroad')
  const isFormApproval = getQuery('typeId') == 'approval'
  const flag = getQuery('flag')
  const [form] = Form.useForm()
  const { industry } = store
  const [compare, setCompare] = useState({})
  const { cannotEdit, setCannotEdit, isDomestic } = store
  const [clientList, setClientList] = useState([])
  const { region } = useGetRegion()
  const getClientList = _debounce(async (val) => {
    const res = await Api.getClientGroupList({ clientName: val, isGroup: 1 })
    const result = res.list?.filter((item) => item.id !== Number(id))
    setClientList([...clientInit, ...result] ?? clientInit)
  }, 500)

  useEffect(() => {
    getClientList()
  }, [])

  useEffect(() => {
    store.initIndustry()
  }, [])

  const onClientSearch = (value) => {
    getClientList(value)
  }

  const { compareData } = store

  const createcommerceInfo = async () => {
    const data = await Api.createcommerceInfo({ clientId: id })
    const {
      approvalDate,
      establishDate,
      industryTypeName,
      industryType,
      industryTypeWithParent,
      bizLicenseEndDate,
      ...rest
    } = data
    store.setBaseInfo(data)

    form.setFieldsValue({
      ...rest,
      clientType: clientType == 'CORPORATION' ? '法人' : '自然人',
      approvalDate: approvalDate && moment(approvalDate, dateFormat),
      establishDate: establishDate && moment(establishDate, dateFormat),
      bizLicenseEndDate: bizLicenseEndDate && moment(bizLicenseEndDate, dateFormat),
      industryType: industryTypeWithParent || [],
    })
    store.isDomestic = rest.domesticOrAbroad === 'DOMESTIC'
    if (rest.groupFlag === 1) {
      setCannotEdit(true)
      form.setFieldsValue({
        belongGroupClientId: {
          label: rest.clientName,
          value: id,
        },
      })
    } else {
      form.setFieldsValue({
        belongGroupClientId: {
          label: rest.clientName,
          value: rest.belongGroupClientId,
        },
      })
    }
  }
  useEffect(() => {
    setCompare(compareData)
  }, [compareData])
  // 对比接口
  useEffect(() => {
    if (getQuery('typeId') == 'approval') {
      store.getApprovalCommercedetail(id, clientType, startUserId, businessVersion)
    }
  }, [getQuery('typeId')])

  useEffect(() => {
    if (id) {
      if (isFormApproval || flag == 'info' || (flag == 'create' && domesticOrAbroad === 'ABROAD')) {
        store.initDetail(id, clientType)
      } else {
        createcommerceInfo()
      }
    }
  }, [id, flag])

  const onGroupChange = (value) => {
    if (value === 1) {
      setCannotEdit(true)
      const $clientName = form.getFieldValue('clientName')
      form.setFieldsValue({
        belongGroupClientId: {
          label: $clientName,
          value: id,
        },
      })
    } else {
      form.setFieldsValue({
        belongGroupClientId: undefined,
      })
      setCannotEdit(false)
    }
  }

  const starDom = (name, must, obj) => {
    return (
      <span className={styles.colorsWrap}>
        {must && <span className={styles.colors}>*</span>}
        <span style={{ color: obj?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>{name}</span>
      </span>
    )
  }
  return (
    <div>
      <div className={styles.basicWrap}></div>
      <Form
        store={store.industrialForm}
        cache="false"
        form={form}
        scrollToFirstError
        className={styles.industrialForm}
      >
        <Descriptions
          title="工商信息"
          bordered
          column={2}
          labelStyle={{ background: '#F5F6FA', height: '48px' }}
          size={'small'}
          className={styles.des}
        >
          <Descriptions.Item label={starDom('客户名称', true, compare.clientName)}>
            <Form.Item name="clientName">
              <Input disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('客户分类', false, compare.clientType)}>
            <Form.Item name="clientType">
              <Input disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('机构类型', false, compare.domesticOrAbroad)}>
            <Form.Item name="domesticOrAbroad">
              <Select options={'domesticOrAbroad'} disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('客户编号', false, compare.clientCode)}>
            <Form.Item name="clientCode">
              <Input disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('中征码', false, compare.zhongZhengCode)}>
            <Form.Item name="zhongZhengCode">
              <Input />
            </Form.Item>
          </Descriptions.Item>
          {isDomestic ? (
            <Descriptions.Item label={starDom('统一社会信用代码', false, compare.uscCode)}>
              <Form.Item name="uscCode">
                <Input disabled />
              </Form.Item>
            </Descriptions.Item>
          ) : (
            <Descriptions.Item label={starDom('特殊机构代码', false, compare.specialOrgCode)}>
              <Form.Item name="specialOrgCode">
                <Input />
              </Form.Item>
            </Descriptions.Item>
          )}
          <Descriptions.Item label={starDom('成立日期', isDomestic, compare.establishDate)}>
            <Form.Item
              name="establishDate"
              rules={[{ required: isDomestic, message: '请选择成立日期' }]}
            >
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('核准日期', isDomestic, compare.approvalDate)}>
            <Form.Item
              name="approvalDate"
              rules={[{ required: isDomestic, message: '请选择核准日期' }]}
            >
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Descriptions.Item>

          <Descriptions.Item
            label={starDom('营业许可证到期日', isDomestic, compare.bizLicenseEndDate)}
          >
            <Form.Item
              name="bizLicenseEndDate"
              rules={[{ required: isDomestic, message: '请选择营业许可证到期日' }]}
            >
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('存续状态', isDomestic, compare.continuousStatus)}>
            <Form.Item
              name="continuousStatus"
              rules={[{ required: isDomestic, message: '请选择存续状态' }]}
            >
              <Select style={{ width: '100%' }} options="continuousStatus" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('国标行业分类', true, compare.industryTypeName)}>
            <Form.Item name="industryType" rules={[{ required: true, message: '请选择行业分类' }]}>
              <Cascader style={{ maxWidth: '420px' }} options={industry} />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={starDom('风控行业分类', true, compare?.riskControlIndustryClassify)}
          >
            <Form.Item
              noStyle
              shouldUpdate={(prev, cur) =>
                prev?.riskControlIndustryClassify !== cur?.riskControlIndustryClassify
              }
            >
              {() => (
                <Form.Item
                  name="riskControlIndustryClassify"
                  rules={[{ required: true, message: '请选择风控行业分类' }]}
                >
                  <Select
                    style={{ maxWidth: '380px' }}
                    options={getRiskControlIndustryClassifySelectOptions(
                      form.getFieldValue('riskControlIndustryClassify')
                    )}
                  />
                </Form.Item>
              )}
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('经济类型', true, compare.economyType)}>
            <Form.Item name="economyType" rules={[{ required: true, message: '请选择经济类型' }]}>
              <Select style={{ width: '100%' }} options="economyType" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('组织机构类型', isDomestic, compare.orgType)}>
            <Form.Item
              name="orgType"
              rules={[{ required: isDomestic, message: '请选择组织机构类型' }]}
            >
              <Select style={{ width: '100%' }} options="orgType" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('企业规模', isDomestic, compare.orgScale)}>
            <Form.Item
              name="orgScale"
              rules={[{ required: isDomestic, message: '请选择企业规模' }]}
            >
              <Select style={{ width: '100%' }} options="orgScaleType" />
            </Form.Item>
          </Descriptions.Item>

          <Descriptions.Item label={starDom('注册资本(元)', isDomestic, compare.registerCapital)}>
            <Form.Item
              name="registerCapital"
              rules={[{ required: isDomestic, message: '请输入注册资本' }]}
            >
              <Amount>
                <InputNumber style={{ width: '100%' }} />
              </Amount>
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={starDom('注册资本币种', isDomestic, compare.registerCurrencyType)}
          >
            <Form.Item
              name="registerCurrencyType"
              rules={[{ required: isDomestic, message: '请选择注册资本币种' }]}
            >
              <Select style={{ width: '100%' }} options="currencyType" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('实收资本(元)', isDomestic, compare.realCapital)}>
            <Form.Item
              name="realCapital"
              rules={[{ required: isDomestic, message: '请输入实收资本' }]}
            >
              <Amount>
                <InputNumber style={{ width: '100%' }} />
              </Amount>
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('实收资本币种', false, compare.realCurrencyType)}>
            <Form.Item name="realCurrencyType">
              <Select style={{ width: '100%' }} options="currencyType" allowClear />
            </Form.Item>
          </Descriptions.Item>

          <Descriptions.Item
            label={starDom('法人代表', !cannotEdit && isDomestic, compare.corpRepresent)}
          >
            <Form.Item
              name="corpRepresent"
              rules={[{ required: !cannotEdit && isDomestic, message: '请输入法人代表' }]}
            >
              <Input />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={starDom('法人性别', !cannotEdit && isDomestic, compare.corpGender)}
          >
            <Form.Item
              name="corpGender"
              rules={[{ required: !cannotEdit && isDomestic, message: '请选择法人性别' }]}
            >
              <Select style={{ width: '100%' }} options="genderType" allowClear />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={starDom('法人证件类型', !cannotEdit && isDomestic, compare.corpCertType)}
          >
            <Form.Item
              name="corpCertType"
              rules={[{ required: !cannotEdit && isDomestic, message: '请选择法人证件类型' }]}
            >
              <Select style={{ width: '100%' }} options="certType" allowClear />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={starDom('法人证件号码', !cannotEdit && isDomestic, compare.corpCertCode)}
          >
            <Form.Item
              name="corpCertCode"
              rules={[{ required: !cannotEdit && isDomestic, message: '请选择法人证件号码' }]}
            >
              <Input />
            </Form.Item>
          </Descriptions.Item>

          <Descriptions.Item
            label={
              <Tooltip title="上市公司：仅包括 A 股（沪深主板、中小板、创业板）">
                {starDom('企业性质', isDomestic, compare.enterpriseNature)}
                <IconFont type="icon-icon_info"></IconFont>
              </Tooltip>
            }
          >
            <Form.Item
              name="enterpriseNature"
              rules={[{ required: isDomestic, message: '请选择' }]}
            >
              <Select style={{ width: '100%' }} options={'enterpriseNatureEnum'} />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={
              <Tooltip title="上市公司：仅包括 A 股（沪深主板、中小板、创业板）">
                {starDom('是否由上市公司控股', isDomestic, compare.ownershipType)}
                <IconFont type="icon-icon_info"></IconFont>
              </Tooltip>
            }
          >
            <Form.Item dependencies={['enterpriseNature']}>
              {({ getFieldValue }) => {
                const enterpriseNature = getFieldValue('enterpriseNature')
                const isRequired = ['gyfss', 'myfss', 'other'].includes(enterpriseNature)
                if (isRequired) {
                  return (
                    <Form.Item
                      name="ownershipType"
                      rules={[{ required: isDomestic, message: '请选择' }]}
                    >
                      <Select options={'ownershipTypeEnum'} />
                    </Form.Item>
                  )
                } else {
                  return '-'
                }
              }}
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('是否关联方', isDomestic, compare.isRelated)}>
            <Form.Item
              name="isRelated"
              rules={[{ required: isDomestic, message: '请选择是否关联方' }]}
            >
              <Select style={{ width: '100%' }} options={'yesOrNo'} />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('是否集团公司', isDomestic, compare.groupFlag)}>
            <Form.Item
              name="groupFlag"
              rules={[{ required: isDomestic, message: '请选择是否集团公司' }]}
            >
              <Select onChange={onGroupChange} style={{ width: '100%' }} options={'yesOrNo'} />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('所属集团', isDomestic, compare.belongGroupClientId)}>
            <Form.Item
              dependencies={['groupFlag']}
              name="belongGroupClientId"
              rules={[{ required: isDomestic, message: '请选择所属集团' }]}
            >
              <Select
                disabled={cannotEdit}
                labelInValue
                placeholder="请输入查询"
                allowClear
                options={clientList}
                filterOption={false}
                style={{ maxWidth: '100%' }}
                onSearch={onClientSearch}
                fieldNames={{ label: 'clientName', value: 'id' }}
              />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={starDom(
              '指标隶属省份',
              !cannotEdit && isDomestic,
              compare.provinceOfAffiliation
            )}
          >
            <Form.Item
              name="provinceOfAffiliation"
              rules={[{ required: !cannotEdit && isDomestic, message: '请选择指标隶属省份' }]}
            >
              <Select
                allowClear
                options={region}
                filterOption={false}
                style={{ maxWidth: '100%' }}
              />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item></Descriptions.Item>
          <Descriptions.Item label={starDom('业务范围', isDomestic, compare.bizScope)} span={2}>
            <Form.Item
              name="bizScope"
              rules={[{ required: isDomestic, message: '请输入业务范围' }]}
            >
              <TextArea autoSize={{ minRows: 4, maxRows: 5 }} maxLength={1000} showCount />
            </Form.Item>
          </Descriptions.Item>
        </Descriptions>
      </Form>
    </div>
  )
}

export default observer(IndustrialDetail)
