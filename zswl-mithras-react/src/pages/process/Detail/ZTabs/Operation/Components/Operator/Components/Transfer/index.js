// 转办
import { Row, Col } from 'antd'
import { ApiSelect } from '@/components/Select'
import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import Api from '@/api/process/detail/flowDetailApi'
import { rules } from '@/utils'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'

const Index = ({ store }) => {
  const { detailData } = useFlowData()

  return (
    <Modal
      store={store.transferModal}
      propsBy={() => ({
        title: '转办',
      })}
      destroyOnClose
    >
      <Form>
        <Row>
          <Col span={24}>
            <Form.Item name="user" label={'用户'} rules={[rules.required('请选择')]}>
              <ApiSelect
                api={Api.queryCanTransferUser}
                params={{
                  processModelType: detailData.modelKey,
                  activityId: detailData.taskActivityId,
                }}
                searchField="userName"
              ></ApiSelect>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default observer(Index)
