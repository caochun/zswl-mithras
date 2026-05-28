import { App, Button, Form, Select } from '@zswl/components'
import { amountFormat, formatPercent } from '@/utils'
import IconFont from '@/components/Icon'
import { Col, Row } from 'antd'
import styles from './index.less'
import { history, observer, getQuery, matchRoute } from '@zswl/admin'
import _ from 'lodash'
import Api from '@/api/financial/orgManage'
import FundApi from '@/pages/financial/fund/api'
import { useEffect, useMemo, useState } from 'react'
import FormAmount from '@/components/Form/FormAmount'

function Index(props) {
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

        // init && fields.length === 0 && addFiled(1)
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

  useEffect(() => {
    getList()
  }, [])

  const onGuarantChange = (val, name) => {
    const INIT_AMOUNT = 0
    form.setFieldValue([fieldKey, name, 'guaranteeAmount'], INIT_AMOUNT)
    // form.setFieldValue([fieldKey, name, 'remainingGuaranteeAmount'], 0)
    onAmountChange(INIT_AMOUNT, name)
  }

  const getParamsId = () => {
    const pathInfo = matchRoute(window.location.pathname)
    return pathInfo.params?.id
  }

  const onAmountChange = async (val, name) => {
    const guaranteeAgencyIdVal = form.getFieldValue([fieldKey, name, 'guaranteeAgencyId'])
    if (!guaranteeAgencyIdVal) return
    const remainingGuaranteeAmount = await FundApi.postCalcRemainingamount({
      guaranteeAgencyId: guaranteeAgencyIdVal,
      guaranteeAmount: val,
      financingId: getParamsId(),
    })
    form.setFieldValue([fieldKey, name, 'remainingGuaranteeAmount'], remainingGuaranteeAmount)
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
                  onChange={(val) => onGuarantChange(val, name)}
                  allowClear={false}
                  options={list}
                  style={{ width: '100%' }}
                  placeholder="请选择！"
                  fieldNames={{ label: 'guaranteeAgencyName', value: 'id' }}
                  filterOption={(input, option) => {
                    // 搜索
                    return option.guaranteeAgencyName.indexOf(input) >= 0
                  }}
                />
              </Form.Item>
            </Col>
            <Col span={7} style={{ display: 'flex', marginLeft: 12 }}>
              <div className={styles.label}>担保金额</div>
              <Form.Item {...restField} name={[name, 'guaranteeAmount']} style={{ margin: 0 }}>
                <FormAmount
                  style={{ width: '100%' }}
                  min={0}
                  onChange={(val) => onAmountChange(val, name)}
                  addonAfter="元"
                />
              </Form.Item>
            </Col>
            <Col span={8} style={{ display: 'flex' }}>
              <div className={styles.label}>剩余担保额度</div>
              <Form.Item
                {...restField}
                name={[name, 'remainingGuaranteeAmount']}
                style={{ margin: 0, width: '300px' }}
              >
                <FormAmount style={{ width: '100%' }} disabled min={-Infinity} addonAfter="元" />
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

Index.Detail = ({ value, isYT }) => {
  const toDetail = (id) => {
    history.push(`/financial/guarantee/detail/${id}`)
  }
  return (
    <div style={{ display: 'block', width: '100%' }}>
      {value?.length > 0
        ? value?.map((v, index) => {
            return (
              <Row gutter={12} key={index} style={{ width: '100%', alignItems: 'center' }}>
                {isYT && (
                  <Col span={6}>
                    <span className={styles.itemLabel}>融资机构：</span>
                    {v.organizationName}
                  </Col>
                )}
                <Col span={6}>
                  <a type="link" onClick={() => toDetail(v.guaranteeAgencyId)}>
                    {v.guaranteeAgencyName}
                  </a>
                </Col>
                <Col span={6}>
                  <span className={styles.itemLabel}>担保金额(元)：</span>
                  {amountFormat(formatPercent(v.guaranteeAmount))}
                </Col>
                <Col span={6}>
                  <span className={styles.itemLabel}>剩余担保额度(元)：</span>
                  {amountFormat(formatPercent(v?.remainingGuaranteeAmount || 0))}
                </Col>
              </Row>
            )
          })
        : '无'}
    </div>
  )
}
export default observer(Index)
