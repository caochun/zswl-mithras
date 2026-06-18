import { observer } from '@zswl/admin'
import styles from './style.less'
import { Col, Row, Tooltip } from 'antd'
import { Form } from '@zswl/components'
import React from 'react'
import { QuestionCircleOutlined } from '@ant-design/icons'

const RowSpan = ({ subTitle, required = true, children, name, tooltip, rules = [] }) => {
  return (
    <Row className={styles.row}>
      <Col span={8} className={styles.subTitle}>
        {subTitle}
        {tooltip && (
          <Tooltip className={styles.tooltip} title={tooltip}>
            <QuestionCircleOutlined />
          </Tooltip>
        )}
      </Col>
      <Col span={16} className={styles.right}>
        <Form.Item
          colon={false}
          name={name}
          required={false}
          messageVariables={{
            label: subTitle,
          }}
          rules={[{ required }, ...rules]}
        >
          {React.cloneElement(children, {})}
        </Form.Item>
      </Col>
    </Row>
  )
}
export default observer(RowSpan)
