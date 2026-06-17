import { NoEnumFileTable } from '@/components'
import { getLocalStorage, observer } from '@zswl/admin'
import { Button, Form, Input, Modal, Select } from '@zswl/components'
import Api from '@/api/process/flowFile'

const moduleType = 'RATING_CLIENT'
const Index = ({ store, disabled }) => {
  const { businessKey: mainId, processInstanceId, taskId } = store.detailData
  const userInfo = getLocalStorage('userInfo')
  return (
    <div>
      <Button onClick={() => store.overturnModal.open()} disabled={disabled}>
        下迁
      </Button>
      <Modal
        title={'下迁'}
        store={store.overturnModal}
        destroyOnClose
        width={1000}
        okText="确认提交"
      >
        <Form>
          <Form.Item label="系统评级结果" name="score">
            <Input disabled></Input>
          </Form.Item>
          <Form.Item label="业务调整结果" name="adjustScore">
            <Input disabled></Input>
          </Form.Item>

          <Form.Item
            label="评级调整说明"
            name={'overturnOpinion'}
            required
            rules={[{ required: true, message: '请输入评级调整说明' }]}
          >
            <Input.TextArea rows={3} />
          </Form.Item>

          <Form.Item label="审查结果" name="finalScore" required>
            <Select options={'ratingLevelEnum'} getPopupContainer={() => document.body}></Select>
          </Form.Item>
          <NoEnumFileTable
            title="补充说明资料"
            tableApi={async () =>
              await Api.getApprovalList({
                processInstanceId,
                taskId,
                businessKey: mainId,
                moduleType,
              })
            }
            canEdit
            params={{ mainId, moduleType, materialsType: 'RATING_CLIENT_SUPPLEMENT_FILE' }}
            columns={[
              { title: '资料名称', dataIndex: 'fileName', width: 200 },
              { title: '上传人', dataIndex: 'creator' },
              { title: '上传时间', dataIndex: 'createTime' },
            ]}
          />
        </Form>
      </Modal>
    </div>
  )
}

export default observer(Index)
