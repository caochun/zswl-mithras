import { App, Button, Form, Select } from '@zswl/components'
import { amountFormat, formatPercent } from '@/utils'
import IconFont from '@/components/Icon'
import { Col, Row } from 'antd'
import styles from './index.less'
import { history, observer } from '@zswl/admin'
import _ from 'lodash'
import Api from '@/api/financial/orgManage'
import { useEffect, useMemo, useState } from 'react'
import { FormAmount } from '@/components/Form'

function FinancialPaymentColumnsFormGuarantee(props) {
  const { listName, addText = '请添加担保方', value } = props
  const [init, setInit] = useState(true)

  return (
    <Form.List name={listName} initialValue={value}>
      {(fields, { add, remove }) => {
        const addFiled = (id) => {
          const isFilter = fields.filter((item) => item.guaranteeAgencyId === id).length > 0
          if (!isFilter) {
            add({
              guaranteeAgencyId: id,
            })
            setInit(false)
          }
        }

        init && fields.length === 0 && addFiled(1)
        return (
          <FormListItem
            fields={fields}
            fieldKey={listName}
            add={add}
            remove={remove}
            addText={addText}
          />
        )
      }}
    </Form.List>
  )
}

const FormListItem = ({ fields, add, remove, addText, required, fieldKey }) => {
  const addButton = (
    <div className={styles.add} onClick={() => add()}>
      <div
        className={styles.icon}
        style={{
          marginRight: 8,
        }}
      >
        <IconFont type="icon-icon_add" />
      </div>
      {addText}
    </div>
  )

  const [list, setList] = useState([])
  const getList = _.debounce(async (val) => {
    const res = await Api.postAgencyPulldown({
      guaranteeAgencyName: val,
    })
    setList(res)
  }, 500)
  const form = Form.useFormInstance()
  const amountList = useMemo(() => {
    const res = form.getFieldsValue(true)
    const obj = {}
    res.guaranteeInfoList?.forEach((v) => {
      obj[v.guaranteeAgencyId] = v.guaranteeAmount
    })
    return obj
  }, [])

  useEffect(() => {
    getList()
  }, [])

  const guaranteeChange = (val, name) => {
    form.setFieldValue([fieldKey, name, 'guaranteeAmount'], 0)
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
                  filterOption={(input, option) => {
                    // 搜索
                    return option.guaranteeAgencyName.indexOf(input) >= 0
                  }}
                />
              </Form.Item>
            </Col>

            <Col span={7} style={{ display: 'flex', marginLeft: 12 }}>
              <div className={styles.label}>担保金额(元)</div>
              <Form.Item {...restField} name={[name, 'guaranteeAmount']} style={{ margin: 0 }}>
                <FormAmount style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>

            <Col span={8} style={{ display: 'flex' }}>
              <div className={styles.label}>剩余担保额度(元)</div>
              <Form.Item
                dependencies={[
                  [fieldKey, name, 'guaranteeAmount'],
                  [fieldKey, name, 'guaranteeAgencyId'],
                ]}
                noStyle
              >
                {({ getFieldValue, setFieldValue, getFieldsValue }) => {
                  const id = getFieldValue([fieldKey, name, 'guaranteeAgencyId'])
                  const guaranteeAmount = getFieldValue([fieldKey, name, 'guaranteeAmount']) ?? 0
                  const currentUsed = amountList?.[id] ?? 0
                  const findList = list.find((item) => item.id === id) || {}
                  const { totalGuaranteeLimit = 0, usedGuaranteeLimit } = findList
                  // 计算公式：剩余担保额度=该担保方总担保额度-已使用额度+在本次授信已使用额度-本次填写担保额度。
                  const remainingGuaranteeAmount =
                    totalGuaranteeLimit - usedGuaranteeLimit + currentUsed - guaranteeAmount
                  setFieldValue(
                    [fieldKey, name, 'remainingGuaranteeAmount'],
                    remainingGuaranteeAmount
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
            <Col>
              <Row>
                {addButton}
                <div className={styles.add} onClick={() => remove(name)}>
                  <div className={styles.icon}>
                    <IconFont type="icon-icon_delete" />
                  </div>
                </div>
              </Row>
            </Col>
          </Row>
        )
      })}
      {!fields.length && addButton}
    </div>
  )
}

FinancialPaymentColumnsFormGuarantee.Detail = ({ value }) => {
  const toDetail = (id) => {
    history.push(`/financial/guarantee/detail/${id}`)
  }
  return (
    <div style={{ display: 'block', width: '100%' }}>
      {value?.map((v, index) => {
        return (
          <Row gutter={12} key={index} style={{ width: '100%', alignItems: 'center' }}>
            <Col span={12}>
              <a type="link" onClick={() => toDetail(v.guaranteeAgencyId)}>
                {v.guaranteeAgencyName}
              </a>
            </Col>
            <Col span={12}>
              <span className={styles.itemLabel}>担保金额(元)：</span>
              {amountFormat(formatPercent(v.guaranteeAmount))}
            </Col>
          </Row>
        )
      })}
    </div>
  )
}
export default observer(FinancialPaymentColumnsFormGuarantee)
