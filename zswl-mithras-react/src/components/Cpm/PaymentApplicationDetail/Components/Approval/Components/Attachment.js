import IconFont from '@/components/Icon'
import { Row, Col, Button, Upload } from 'antd'
import { useState } from 'react'
import styles from '../index.less'
const Attachment = () => {
  const [fileList, setFileList] = useState([])
  const uploadProps = {
    name: 'file',
    beforeUpload: (val) => {
      setFileList([val])
      return false
    },
    onRemove: () => {
      setFileList([])
    },
    fileList,
  }
  return (
    <>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.title}>审批附件</div>
      </div>
      <div className={styles.attachmentContent}>
        <Row>
          <Col span={6}>
            <div className={styles.center}>附件：</div>
          </Col>
          <Col span={18}>
            <Upload {...uploadProps}>
              <Button style={{ marginRight: 8 }}>
                <IconFont type="icon-icon_upload" /> 点击上传
              </Button>
            </Upload>
          </Col>
        </Row>
      </div>
    </>
  )
}

export default Attachment
