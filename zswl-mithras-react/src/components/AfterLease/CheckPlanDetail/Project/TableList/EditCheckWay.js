import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { debounce as _debounce } from 'lodash'
import { Input } from 'antd'
import Api from '../../api'
import { ApiSelect } from '@/components'

const { Item } = Form

// 【任务调整】按钮，权限给资产管理，勾选单条未发起租后检查报告流程的任务可调整「检查形式」和「协查风控经理」
// 编辑按钮权限就只给对应协查风控经理，点击可以维护「检查日期」

function CreateModal({ store }) {
  const [form] = Form.useForm()
  const onCheckWayChange = () => {
    form.setFieldValue('riskManagerId', undefined)
  }
  return (
    <Modal title={'检查信息维护'} store={store} okText={'确定'} destroyOnClose width={580}>
      <Form
        form={form}
        labelCol={{ span: 6 }}
        preserve={false}
        initialValues={{
          scene: 'assetManager',
        }}
      >
        <Item name="id" hidden>
          <Input />
        </Item>
        <Item label={'协查形式'} name="checkWay" rules={[{ required: true, message: '请选择' }]}>
          <Select options={'afterLeaseCheckWayEnum'} onChange={onCheckWayChange}></Select>
        </Item>
        <Item name="scene" hidden>
          <Input />
        </Item>
        <Item dependencies={['checkWay']} noStyle>
          {({ getFieldValue }) => {
            // 现场检查、必填。  //"SITE"、"OFFSITE"
            const isSite = getFieldValue('checkWay') === 'SITE'
            // 重置
            return (
              <Item
                label={'协查风控经理'}
                name="riskManagerId"
                rules={[{ required: isSite, message: '请选择' }]}
              >
                <ApiSelect
                  api={Api.postRiskManagerList}
                  disabled={!isSite}
                  placeholder={isSite ? '请选择' : ' '}
                />
              </Item>
            )
          }}
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CreateModal)
