import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import { TEMPLATE_LIST } from './enum'
import { Radio, Space } from 'antd'
import IconFont from '@/components/Icon'
import styles from './style.less'

const { Item } = Form
function AfterLeaseCheckPlanTemplateModal({ store }) {
  return (
    <Modal
      title={'请选择报告模板'}
      store={store.submitModal}
      bodyStyle={{ padding: '40px 95px' }}
      width={480}
      destroyOnClose
    >
      <Form cache={false}>
        <Item name="active" style={{ margin: 0 }}>
          <Radio.Group>
            <Space direction="vertical">
              {TEMPLATE_LIST.map(({ name }, index) => (
                <Radio.Button value={index} key={name} className={styles.modalButton}>
                  <IconFont type="icon-icon_template" /> {name}
                </Radio.Button>
              ))}
            </Space>
          </Radio.Group>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(AfterLeaseCheckPlanTemplateModal)
