import { App, Button, Form, Select } from '@zswl/components'
import { getInputNumberAmountProps, rules } from '@/utils'
import IconFont from '@/components/Icon'
import { Col, Input, InputNumber, Row, Tooltip } from 'antd'
import styles from './index.less'
import { history, observer } from '@zswl/admin'
import _ from 'lodash'
import { useCallback } from 'react'
import { ReadOnly } from '@/components'

export const toDetail = (id, clientType) => {
  if (id) {
    history.push(`/customer/maintain/detail/${id}?clientType=${clientType}&flag=info&typeId=create`)
  }
}
function Index(props) {
  const { name, onlyRead } = props
  return (
    <Form.List name={name}>
      {(fields, { add, remove }) => {
        return (
          <>
            <FormListItem fields={fields} fieldKey={name} remove={remove} onlyRead={onlyRead} />
            {!onlyRead && (
              <Button
                block
                style={{ width: '100%', margin: '12px 0' }}
                onClick={() => add({})}
                icon={<IconFont type="icon-icon_add" />}
              >
                新增账户
              </Button>
            )}
          </>
        )
      }}
    </Form.List>
  )
}

const FormListItem = ({ fields, remove, required, onlyRead }) => {
  const InputEditable = (props) => {
    return onlyRead ? <ReadOnly {...props} /> : <Input placeholder="请输入" {...props} />
  }
  const SelectEditable = ({ value, options, ...rest }) => {
    const newValue = App.matchOption(options, value).label
    return onlyRead ? (
      <ReadOnly value={newValue} />
    ) : (
      <Select placeholder="请选择" options={options} value={value} {...rest} />
    )
  }
  return (
    <div style={{ width: '100%' }}>
      {fields?.map(({ key, name, ...restField }, index) => {
        return (
          <div key={index}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <div className={styles.title}>账户{index + 1}</div>
              {!onlyRead && <Button.Delete onClick={() => remove(name)}>删除</Button.Delete>}
            </div>
            <Row key={key} gutter={40}>
              <Col span={12}>
                <Form.Item {...restField} name={[name, 'accountName']} label="银行账户名称">
                  <InputEditable />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item {...restField} name={[name, 'depositBank']} label="开户行">
                  <InputEditable />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  {...restField}
                  name={[name, 'account']}
                  label="银行账号"
                  // rules={[rules.bankCode()]}
                >
                  <InputEditable />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item {...restField} name={[name, 'mainAccount']} label="是否主账号">
                  <SelectEditable options="trueOrFalse" />
                </Form.Item>
              </Col>
            </Row>
          </div>
        )
      })}
    </div>
  )
}

export default observer(Index)
