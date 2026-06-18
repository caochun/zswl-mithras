import { Form } from '@zswl/components'
import { amountFormat, formatPercent } from '@/utils'
import IconFont from '@/components/Icon'
import { Col, Input, Row } from 'antd'
import styles from './index.less'
import { history, observer } from '@zswl/admin'
import _ from 'lodash'
import { FormAmount } from '@/components/Form'
import { OrgListSelect } from '@/components/Select'

function Index(props) {
  const { listName, addText = '请添加融资机构', value } = props
  return (
    <Form.List name={listName} initialValue={value}>
      {(fields, { add, remove }) => {
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

  const form = Form.useFormInstance()

  const onGuarantChange = (val, list, name) => {
    const { remainingCreditAmount, label } = list.find((item) => item.value === val) ?? {}

    form.setFieldValue([fieldKey, name, 'remainingCreditAmount'], remainingCreditAmount)
    form.setFieldValue([fieldKey, name, 'organizationName'], label)
    form.setFieldValue([fieldKey, name, 'organizationAmount'], 0)
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
                name={[name, 'organizationId']}
                rules={[{ required, message: '请选择!' }]}
                style={{ margin: 0 }}
              >
                <OrgListSelect
                  onChange={(val, list) => onGuarantChange(val, list, name)}
                  allowClear={false}
                  style={{ width: '100%' }}
                  placeholder="请选择！"
                  apiParams={{
                    pageSize: 1000,
                    organizationType: 'BANK',
                  }}
                />
              </Form.Item>
            </Col>
            <Form.Item hidden {...restField} name={[name, 'organizationName']}>
              <Input />
            </Form.Item>
            <Col span={7} style={{ display: 'flex', marginLeft: 12 }}>
              <div className={styles.label}>可用额度</div>
              <Form.Item
                {...restField}
                name={[name, 'remainingCreditAmount']}
                style={{ margin: 0 }}
              >
                <FormAmount style={{ width: '100%' }} min={0} disabled addonAfter="元" />
              </Form.Item>
            </Col>
            <Col span={8} style={{ display: 'flex' }}>
              <div className={styles.label}>融资金额</div>
              <Form.Item dependencies={[name, 'remainingCreditAmount']} noStyle>
                {({ getFieldValue }) => {
                  const amount = getFieldValue([fieldKey, name, 'remainingCreditAmount']) ?? 0
                  return (
                    <Form.Item
                      {...restField}
                      name={[name, 'organizationAmount']}
                      style={{ margin: 0, width: '300px' }}
                    >
                      <FormAmount
                        style={{ width: '100%' }}
                        min={-Infinity}
                        max={amount}
                        addonAfter="元"
                      />
                    </Form.Item>
                  )
                }}
              </Form.Item>
            </Col>
            <Col>
              <Row>
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
      {addButton}
    </div>
  )
}

Index.Detail = ({ value }) => {
  const toDetail = (id) => {
    history.push(`/financial/guarantee/detail/${id}`)
  }
  return (
    <div style={{ display: 'block', width: '100%' }}>
      {value?.map((v, index) => {
        return (
          <Row gutter={12} key={index} style={{ width: '100%', alignItems: 'center' }}>
            <Col span={6}>
              <span className={styles.itemLabel}>融资机构：</span>
              {v.organizationName}
            </Col>
            <Col span={8}>
              <span className={styles.itemLabel}>可用额度(元)：</span>
              {amountFormat(formatPercent(v.remainingCreditAmount))}
            </Col>
            <Col span={8}>
              <span className={styles.itemLabel}>融资金额(元)：</span>
              {amountFormat(formatPercent(v?.organizationAmount || 0))}
            </Col>
          </Row>
        )
      })}
    </div>
  )
}
export default observer(Index)
