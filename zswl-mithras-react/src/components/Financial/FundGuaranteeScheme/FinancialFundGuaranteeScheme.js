import { Form, Select } from '@zswl/components'
import { amountFormat, formatPercent } from '@/utils'
import { Col, Input, Row } from 'antd'
import { history, observer } from '@zswl/admin'
import _ from 'lodash'
import Api from '@/api/financial/orgManage'
import FundApi from '@/api/financial/fundApi'
import { useEffect, useMemo, useState } from 'react'
import { FormAmount } from '@/components/Form'
import useForceUpdate from '@/utils/hooks/useForceUpdate'
import styles from './index.less'

function FinancialFundGuaranteeScheme(props) {
  const { listName, addText = '请添加担保方', value, financingAmount, financingMonth } = props
  const form = Form.useFormInstance()

  const getInitValue = useMemo(() => {
    const result = value?.map((item) => {
      return {
        ...item,
        guaranteeFeeRate: item.guaranteeFeeRate ?? 0.45 * 10000,
      }
    })

    form.setFieldValue(listName, result)
    return result
  }, [JSON.stringify(value)])

  return (
    <Form.List name={listName} initialValue={getInitValue}>
      {(fields, { add, remove }) => {
        return (
          <FormListItem
            fields={fields}
            fieldKey={listName}
            add={add}
            remove={remove}
            addText={addText}
            financingAmount={financingAmount}
            financingMonth={financingMonth}
          />
        )
      }}
    </Form.List>
  )
}

const FormListItem = ({ fields, required, fieldKey, financingAmount, financingMonth }) => {
  const forceUpdate = useForceUpdate()

  const [list, setList] = useState([])
  const getList = _.debounce(async (val) => {
    const res = await Api.postAgencyPulldown({
      guaranteeAgencyName: val,
    })
    setList(res)
  }, 500)
  const form = Form.useFormInstance()

  const onAmountChange = async (name) => {
    const guaranteeFeeRate = form.getFieldValue([fieldKey, name, 'guaranteeFeeRate']) ?? 0
    const guaranteeFeeAmount = await FundApi.postPlanCalc({
      financingAmount: Number(financingAmount) * 10000,
      financingMonth: Number(financingMonth),
      guaranteeRate: guaranteeFeeRate,
    })

    form.setFieldValue([fieldKey, name, 'guaranteeFeeAmount'], guaranteeFeeAmount)
  }

  useEffect(() => {
    forceUpdate()
  }, [financingAmount, financingMonth])

  useEffect(() => {
    getList()
  }, [])

  const guaranteeChange = (val, name) => {
    form.setFieldValue([fieldKey, name, 'guaranteeFeeRate'], 0)
  }
  return (
    <div style={{ width: '100%' }}>
      {fields?.map(({ key, name, ...restField }, index) => {
        return (
          <Row
            key={key}
            gutter={12}
            style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}
          >
            <Form.Item hidden {...restField} name={[name, 'organizationId']}>
              <Input />
            </Form.Item>
            <Form.Item hidden {...restField} name={[name, 'organizationName']}>
              <Input />
            </Form.Item>
            <Col span={6}>
              <Form.Item
                {...restField}
                name={[name, 'guaranteeAgencyId']}
                rules={[{ required, message: '请选择!' }]}
                style={{ margin: 0 }}
              >
                <Select
                  disabled
                  allowClear
                  options={list}
                  style={{ width: '100%' }}
                  placeholder="请选择！"
                  fieldNames={{ label: 'guaranteeAgencyName', value: 'id' }}
                  onChange={(val) => guaranteeChange(val, name)}
                  filterOption={(input, option) => {
                    // 搜索
                    return option.guaranteeAgencyName.indexOf(input) >= 0
                  }}
                />
              </Form.Item>
            </Col>
            <Col span={7} style={{ display: 'flex', marginLeft: 12 }}>
              <div className={styles.label}>担保费率</div>
              <Form.Item
                {...restField}
                name={[name, 'guaranteeFeeRate']}
                style={{ margin: 0 }}
                rules={[{ required: true, message: '请输入担保费率' }]}
              >
                <FormAmount
                  style={{ width: '100%' }}
                  min={0}
                  placeholder="担保费率"
                  addonAfter="%"
                />
              </Form.Item>
            </Col>
            <Col span={8} style={{ display: 'flex' }}>
              <div className={styles.label}>担保费</div>
              <Form.Item
                dependencies={[
                  [fieldKey, name, 'guaranteeFeeRate'],
                  [fieldKey, name, 'guaranteeAgencyId'],
                ]}
                noStyle
              >
                {({ getFieldValue, setFieldValue, getFieldsValue }) => {
                  onAmountChange(name)

                  return (
                    <Form.Item
                      {...restField}
                      name={[name, 'guaranteeFeeAmount']}
                      style={{ margin: 0, width: '300px' }}
                    >
                      <FormAmount
                        style={{ width: '100%' }}
                        disabled
                        // value={guaranteeFeeAmount}
                        addonAfter="元"
                      />
                    </Form.Item>
                  )
                }}
              </Form.Item>
            </Col>
          </Row>
        )
      })}
    </div>
  )
}

FinancialFundGuaranteeScheme.Detail = ({ value }) => {
  const toDetail = (id) => {
    history.push(`/financial/guarantee/detail/${id}`)
  }
  return (
    <div style={{ display: 'block', width: '100%' }}>
      {value?.map((v, index) => {
        return (
          <Row gutter={12} key={index} style={{ width: '100%', alignItems: 'center' }}>
            <Col span={6}>
              <a type="link" onClick={() => toDetail(v.guaranteeAgencyId)}>
                {v.guaranteeAgencyName}
              </a>
            </Col>
            <Col span={8}>
              <span className={styles.itemLabel}>担保费率：</span>
              {v.guaranteeFeeRate ? amountFormat(formatPercent(v.guaranteeFeeRate)) + '%' : '-'}
            </Col>
            <Col span={8}>
              <span className={styles.itemLabel}>担保费(元)：</span>
              {amountFormat(formatPercent(v?.guaranteeFeeAmount || 0))}
            </Col>
          </Row>
        )
      })}
    </div>
  )
}
export default observer(FinancialFundGuaranteeScheme)
