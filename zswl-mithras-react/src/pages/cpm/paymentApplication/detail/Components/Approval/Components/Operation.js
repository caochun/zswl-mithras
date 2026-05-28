import IconFont from '@/components/Icon'
import { Row, Col, Button, Upload, Input } from 'antd'
import { useState } from 'react'
import styles from '../index.less'
const Operation = () => {
  return (
    <>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.title}>审批操作</div>
      </div>
      <div className={styles.attachmentContent}>
        <Row>
          <Col span={6}>
            <div className={styles.center} style={{ alignItems: 'flex-start' }}>
              备注：
            </div>
          </Col>
          <Col span={18}>
            <Input.TextArea placeholder="请输入" autoSize={{ minRows: 4, maxRows: 20 }} />
            <div className={styles.btnWrap} style={{ marginTop: 12 }}>
              <Button type="primary" style={{ marginRight: 12 }}>
                同意
              </Button>
              <Button>退回发起人</Button>
            </div>
          </Col>
        </Row>
      </div>
    </>
  )
}

export default Operation
