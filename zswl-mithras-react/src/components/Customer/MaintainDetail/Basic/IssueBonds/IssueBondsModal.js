import { Amount } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { Input, DatePicker, InputNumber } from 'antd'
import moment from 'moment'

const { Item } = Form
const disabledDate = (current) => {
  return current && current > moment().endOf('day')
}
//新增发债和评级
function Index({ store }) {
  return (
    <Modal
      title={'发债及评级'}
      store={store.issueBondsModal}
      okText={'确定'}
      width={520}
      destroyOnClose
    >
      <Form labelCol={{ span: 7 }} wrapperCol={{ span: 16 }} preserve={false}>
        <Item label={'评级时间'} name={'rateDate'}>
          <DatePicker style={{ width: '100%' }} disabledDate={disabledDate} />
        </Item>
        <Item label={'评级公司'} name={'rateCompany'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'评级'} name={'rate'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'评级展望'} name={'rateFuture'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'发行总额(亿元)'} name={'issueTotal'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'发行数量（只）'} name={'issueAmount'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'存量规模(亿元)'} name={'stockScale'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'存量只数'} name={'stockAmount'}>
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'到期规模(亿元)'} name={'maturityScale'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'到期只数'} name={'maturityAmount'}>
          <Input placeholder={'请输入'} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
