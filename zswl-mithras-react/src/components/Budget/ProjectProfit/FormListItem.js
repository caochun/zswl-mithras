import IconFont from '@/components/Icon'
import { history, observer } from '@zswl/admin'
import { Col, Form, Input, InputNumber, Row, Tooltip } from 'antd'
import { useRef } from 'react'
import styles from './index.less'
import { App, Select } from '@zswl/components'

const FormListItem = ({ fields, add, remove, orgList, contractList, required }) => {
  const form = Form.useFormInstance()

  const { setFieldsValue, setFieldValue, getFieldValue: getValue } = form
  const clientRef = useRef({})

  return (
    <>
      {fields?.map(({ key, name, ...restField }, index) => {
        return (
          <Row
            key={key}
            style={{
              display: 'flex',
              alignItems: 'flex-start',
              alignContent: 'center',
              marginBottom: '8px',
            }}
            gutter={12}
          >
            <Col span={9}>
              <Form.Item
                {...restField}
                name={[name, 'contractId']}
                style={{ width: '100%' }}
                rules={[{ required, message: '请选择!' }]}
              >
                <Select options={contractList} allowClear placeholder="请选择!" />
              </Form.Item>
            </Col>
            <Col span={9}>
              <Form.Item
                {...restField}
                name={[name, 'assessDeptId']}
                style={{ width: '100%' }}
                rules={[{ required, message: '请选择!' }]}
              >
                <Select options={orgList} allowClear placeholder="请选择!" />
              </Form.Item>
            </Col>

            <Col span={4}>
              {index === 0 ? (
                <div className={styles.add} onClick={() => add()}>
                  <div
                    className={styles.icon}
                    style={{
                      marginRight: 8,
                    }}
                  >
                    <IconFont type="icon-icon_add" />
                  </div>
                </div>
              ) : (
                <div className={styles.add} onClick={() => remove(name)}>
                  <div className={styles.icon}>
                    <IconFont type="icon-icon_delete" />
                  </div>
                </div>
              )}
            </Col>
          </Row>
        )
      })}
    </>
  )
}

export default observer(FormListItem)
