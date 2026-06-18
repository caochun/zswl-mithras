import { Modal, Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import { message } from 'antd'
import Content from '../DetailContent'
import Api from '@/api/kpi/pmAssess'
import styles from './index.less'

const Index = ({ store }) => {
  const { currentItem } = store
  const { id: businessKey, isEdit: hasAuth } = currentItem
  const submitFlow = async () => {
    await Api.submit({ id: businessKey })
    message.success('提交成功')
  }
  return (
    <Modal
      propsBy={(data) => {
        const { isEdit, deptName } = data
        const title = `项目经理考评${'详情'}-${deptName}`
        return {
          title,
          footer: null,
        }
      }}
      store={store.$editModal}
      destroyOnClose
      onCancel={() => {
        store.$editModal.close()
        if (hasAuth) {
          store.$table.search()
        }
      }}
      okText={'确定'}
      width={1320}
      footer={null}
    >
      <div className={styles.header}>
        {hasAuth && (
          <Button type="primary" onClick={submitFlow}>
            提交审批
          </Button>
        )}
      </div>
      <Content businessKey={businessKey} canEditFlags={hasAuth ? 'true' : 'false'}></Content>
    </Modal>
  )
}

export default observer(Index)
