import IconFont from '@/components/Icon'
import { history, observer } from '@zswl/admin'
import { Col, Form, Input, InputNumber, Row, Select, Tooltip } from 'antd'
import { useRef } from 'react'
import styles from './index.less'
import { App } from '@zswl/components'
import { OrgSelect } from '@/components'

const FormListItem = ({ fields, add, remove, required, isEdit }) => {
  return (
    <>
      {fields?.map(({ key, name, ...restField }, index) => {
        return (
          <Row
            key={key}
            style={{ display: 'flex', alignItems: 'flex-start', marginBottom: '8px' }}
            gutter={12}
          >
            <Col span={9}>
              <Form.Item
                {...restField}
                name={[name, 'assessDept']}
                style={{ width: '100%' }}
                rules={[{ required, message: '请选择!' }]}
                noStyle
              >
                <OrgSelect
                  placeholder="请选择部门"
                  style={{ width: '100%' }}
                  disabled={!isEdit}
                  isNumber={false}
                />
              </Form.Item>
            </Col>
            <Col span={9}>
              <Form.Item
                {...restField}
                name={[name, 'expenseRadio']}
                style={{ width: '100%' }}
                rules={[{ required, message: '请输入费用计提比例!' }]}
                noStyle
              >
                <InputNumber placeholder="请输入费用计提比例" addonAfter="%" disabled={!isEdit} />
              </Form.Item>
            </Col>
            {isEdit && (
              <Col span={4}>
                {index === 0 ? (
                  <div className={styles.add} onClick={() => add({ expenseRadio: 20 })}>
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
            )}
          </Row>
        )
      })}
    </>
  )
}
FormListItem.Detail = observer(({ values, isDebtor }) => {
  const options = App.getData().optionsType
  return (
    <div className={styles.list}>
      {values?.map((item, index) => {
        const { clientId, clientName, stockRiskExposure, clientType } = item

        return (
          <Row key={index} className={styles.item}>
            <Col span={10}></Col>
            <Col span={5} style={{ marginLeft: '-8px' }}>
              <div className={styles.label}>存量风险敞口(元)</div>
            </Col>
            <Col span={6} style={{ marginLeft: '8px' }}></Col>
          </Row>
        )
      })}
    </div>
  )
})
export default observer(FormListItem)
