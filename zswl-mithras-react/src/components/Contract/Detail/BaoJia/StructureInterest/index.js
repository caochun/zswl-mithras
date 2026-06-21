import { Form } from '@zswl/components'
import { rules } from '@/utils'
import IconFont from '@/components/Icon'
import { Row, Select, Space } from 'antd'
import { observer } from '@zswl/admin'

import styles from './index.less'

const { Item } = Form

const phaseOptions = [
  {
    label: '第1期',
    value: 1,
  },
  {
    label: '第n期',
    value: 2,
  },
]

function ContractStructureInterest(props) {
  const { listName, addText = '', value } = props
  return (
    <Form.List name={listName} initialValue={value ?? []}>
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

const FormListItem = ({ fields, add, remove, addText }) => {
  const addButton = (
    <div onClick={() => add()}>
      <div className={styles.icon}>
        <IconFont type="icon-icon_add" />
      </div>
      {addText}
    </div>
  )

  return (
    <div style={{ width: '100%' }}>
      {fields?.map(({ key, name, ...restField }, index) => {
        return (
          <Row key={key} style={{ marginBottom: '8px', marginTop: 10 }}>
            <Space>
              <Item
                {...restField}
                name={[name, 'phase']}
                rules={[rules.required('请选择')]}
                style={{ margin: 0 }}
              >
                <Select options={phaseOptions}></Select>
              </Item>
              <Item
                {...restField}
                name={[name, 'amount']}
                style={{ margin: 0 }}
                rules={[rules.required('请输入')]}
              >
                <FormAmount style={{ width: 120 }} />
              </Item>
              <>{index === fields.length - 1 && index < phaseOptions.length - 1 && addButton}</>
              <div onClick={() => remove(name)}>
                <div className={styles.icon}>
                  <IconFont type="icon-icon_delete" />
                </div>
              </div>
            </Space>
          </Row>
        )
      })}
      {!fields.length && addButton}
    </div>
  )
}

ContractStructureInterest.Detail = ({ value }) => {
  return (
    <div style={{ display: 'block', width: '100%' }}>
      {value?.map((v, index) => {
        return (
          <Row gutter={12} key={index} style={{ width: '100%', alignItems: 'center' }}>
            <Space>
              <span>{`第${v.phase === 2 ? 'n' : v.phase}期`}</span>
              <span>{FormAmount.Format({ value: v.amount, suffix: '元' })}</span>
            </Space>
          </Row>
        )
      })}
    </div>
  )
}
export default observer(ContractStructureInterest)
