import { App, Button, Form, Select } from '@zswl/components'
import { amountFormat, formatPercent } from '@/utils'
import IconFont from '@/components/Icon'
import { Col, message, Row } from 'antd'
import styles from './index.less'
import { history, observer } from '@zswl/admin'
import _ from 'lodash'
import Api from '@/api/financial/orgManage'
import { useEffect, useMemo, useState } from 'react'
import { FormAmount } from '@/components/Form'

const FormGuarantee = ({ listName, addText = '请添加担保方', method, value, disabled }) => {
  const [init, setInit] = useState(true)

  const guaranteeVaild = {
    validator: async (rule, val) => {
      if (val.some((item) => item.remainingGuaranteeAmount < 0)) {
        throw new Error('剩余担保额度不能小于 0')
      }
    },
  }

  return (
    <Form.List name={listName} initialValue={value} rules={[guaranteeVaild]}>
      {(fields, { add, remove }) => {
        const addField = (id) => {
          if (!fields.some((item) => item.guaranteeAgencyId === id)) {
            add({ guaranteeAgencyId: id })
            setInit(false)
          }
        }

        method?.includes('DB') && init && fields.length === 0 && addField(1)

        return (
          <FormListItem
            fields={fields}
            fieldKey={listName}
            add={add}
            remove={remove}
            addText={addText}
            disabled={disabled}
          />
        )
      }}
    </Form.List>
  )
}

const FormListItem = ({ fields, add, remove, addText, required, fieldKey, disabled }) => {
  const [list, setList] = useState([])
  const form = Form.useFormInstance()

  const getList = _.debounce(async (val) => {
    const res = await Api.postAgencyPulldown({ guaranteeAgencyName: val })
    setList(res)
  }, 500)

  const amountList = useMemo(() => {
    const res = form.getFieldsValue(true)
    return (
      res.guaranteeDetail?.reduce((acc, v) => {
        acc[v.guaranteeAgencyId] = v.guaranteeAmount
        return acc
      }, {}) || {}
    )
  }, [form])

  useEffect(() => {
    getList()
  }, [])

  const guaranteeChange = (val, name) => {
    form.setFieldValue([fieldKey, name, 'guaranteeAmount'], 0)
  }

  const addButton = (
    <div className={styles.add} onClick={() => add()}>
      <IconFont type="icon-icon_add" style={{ marginRight: 8 }} />
      {addText}
    </div>
  )

  return (
    <div style={{ width: '100%' }}>
      {fields?.map(({ key, name, ...restField }, index) => (
        <Row
          key={key}
          gutter={12}
          style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}
        >
          <Col span={6}>
            <Form.Item
              {...restField}
              name={[name, 'guaranteeAgencyId']}
              rules={[{ required, message: '请选择!' }]}
              style={{ margin: 0 }}
            >
              <Select
                allowClear
                options={list}
                style={{ width: '100%' }}
                placeholder="请选择！"
                fieldNames={{ label: 'guaranteeAgencyName', value: 'id' }}
                onChange={(val) => guaranteeChange(val, name)}
                filterOption={(input, option) => option.guaranteeAgencyName.indexOf(input) >= 0}
                disabled={disabled}
              />
            </Form.Item>
          </Col>

          <Col span={7} style={{ display: 'flex', marginLeft: 12 }}>
            <div className={styles.label}>担保金额(元)</div>
            <Form.Item {...restField} name={[name, 'guaranteeAmount']} style={{ margin: 0 }}>
              <FormAmount style={{ width: '100%' }} min={0} disabled={disabled} />
            </Form.Item>
          </Col>

          <Col span={8} style={{ display: 'flex' }}>
            <div className={styles.label}>剩余担保额度(元)</div>
            <Form.Item dependencies={[[fieldKey, name, 'guaranteeAgencyId']]} noStyle>
              {({ getFieldValue, setFieldValue }) => {
                const id = getFieldValue([fieldKey, name, 'guaranteeAgencyId'])
                const findList = list.find((item) => item.id === id) || {}
                const { remainingGuaranteeLimit = 0 } = findList
                const remainingGuaranteeAmount =
                  (remainingGuaranteeLimit / 10000).toFixed(0) * 10000
                setFieldValue(
                  [fieldKey, name, 'remainingGuaranteeAmount'],
                  Number(remainingGuaranteeAmount)
                )
                return (
                  <Form.Item
                    {...restField}
                    name={[name, 'remainingGuaranteeAmount']}
                    style={{ margin: 0, width: '300px' }}
                  >
                    <FormAmount style={{ width: '100%' }} disabled />
                  </Form.Item>
                )
              }}
            </Form.Item>
          </Col>
          {!disabled && (
            <Col>
              <Row>
                <div className={styles.add} onClick={() => remove(name)}>
                  <IconFont type="icon-icon_delete" />
                </div>
              </Row>
            </Col>
          )}
        </Row>
      ))}
      {fields.length < 1 && !disabled && addButton}
    </div>
  )
}

const GuaranteeDetail = ({ value }) => {
  const toDetail = (id) => {
    history.push(`/financial/guarantee/detail/${id}`)
  }

  return (
    <div style={{ display: 'block', width: '100%' }}>
      {value?.map((v, index) => (
        <Row gutter={12} key={index} style={{ width: '100%', alignItems: 'center' }}>
          <Col span={6}>
            <a type="link" onClick={() => toDetail(v.guaranteeAgencyId)}>
              {v.guaranteeAgencyName}
            </a>
          </Col>
          <Col span={8}>
            <span className={styles.itemLabel}>担保金额(元)：</span>
            {amountFormat(formatPercent(v.guaranteeAmount))}
          </Col>
          <Col span={8}>
            <span className={styles.itemLabel}>剩余担保额度(元)：</span>
            {amountFormat(formatPercent(v?.remainingLimit || 0))}
          </Col>
        </Row>
      ))}
    </div>
  )
}

FormGuarantee.Detail = GuaranteeDetail

export default observer(FormGuarantee)
