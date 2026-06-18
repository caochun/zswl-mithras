import { observer } from '@zswl/admin'
import { Form, Modal, Select, App } from '@zswl/components'
import { DatePicker, Input } from 'antd'
import { ApiSelect } from '@/components/Select'
import Api from '@/api/afterLease/checkPlan'

const { Item } = Form

function CreateModal({ store }) {
  const { optionsType } = App.getData()

  return (
    <Modal title={'编辑'} store={store.$editModal} okText={'确定'} destroyOnClose width={400}>
      <Form labelCol={{ span: 8 }} preserve={false}>
        <Item name="planId" hidden>
          <Input></Input>
        </Item>
        <Item label={'跟进频率'} name={'term'} rules={[{ required: true, message: '请选择' }]}>
          <Select
            placeholder="请输入"
            allowClear
            options={'afterLeaseCheckTermEnum'}
            style={{ maxWidth: '100%' }}
          />
        </Item>
        <Item
          label={'下次跟进时间'}
          name={'deadLine'}
          rules={[{ required: true, message: '请选择' }]}
        >
          <DatePicker style={{ width: '100%' }}></DatePicker>
        </Item>
        <Item
          label={'下次跟进形式'}
          name={'checkWay'}
          rules={[{ required: true, message: '请选择' }]}
        >
          <Select
            placeholder="请输入"
            allowClear
            options={optionsType.afterLeaseCheckWayEnum.filter(
              (item) => item.value !== 'WITHOUT_CHECK'
            )}
            style={{ maxWidth: '100%' }}
          />
        </Item>
        <Item dependencies={['checkWay']} noStyle>
          {({ getFieldValue }) => {
            const checkWay = getFieldValue('checkWay')
            if (checkWay === 'SITE') {
              return (
                <Item
                  label={'协查风控经理'}
                  name="riskManagerId"
                  rules={[{ required: true, message: '请选择' }]}
                >
                  <ApiSelect api={Api.postRiskManagerList} labelInValue />
                </Item>
              )
            }
          }}
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CreateModal)
