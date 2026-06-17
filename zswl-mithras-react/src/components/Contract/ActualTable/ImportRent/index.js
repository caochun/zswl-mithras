import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import { DatePicker } from 'antd'

const { Item } = Form

function Index({ bizTypeRentTitle, store }) {
  const [form] = Form.useForm()
  const { contractStatus, scopeData } = store

  // 【516版本】实际利率法计算收益分摊增加变更逻辑 http://tw.zswltech.cn:8888/s/1/37965
  // 合同状态为“起租”且针对已有实际租金表进行覆盖导入的时候增加“变更日期”选择框，后端在进行收入确认变更逻辑时以该变更日期为切分点进行变更
  const showChangeDate = contractStatus === 'START_RENT' && scopeData?.receiptId

  return (
    <Modal title={`导入实际${bizTypeRentTitle}`} store={store.$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        {showChangeDate && (
          <Item
            label={'变更日期'}
            name={'changeDate'}
            rules={[{ required: true, message: '请选择日期!' }]}
          >
            <DatePicker placeholder={'请选择'} style={{ width: 160 }} />
          </Item>
        )}
        <Item
          label={`实际${bizTypeRentTitle}`}
          name={'file'}
          rules={[{ required: true, message: '请上传文件!' }]}
        >
          <DataUpload accept=".xlsx" maxCount={1}></DataUpload>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
