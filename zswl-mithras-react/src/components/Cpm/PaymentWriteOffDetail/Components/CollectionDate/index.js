import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import { rules } from '@/utils'
import SelectDayPanel from './SelectDayPanel'
import { DatePicker } from 'antd'
import { Select } from '@zswl/components'
import { useState } from 'react'

const { Item } = Form

const CpmPaymentWriteOffCollectionDate = (props) => {
  const [sameStartDate, setSameStartDate] = useState(true)
  const { store } = props

  const bankChange = (data) => {
    setSameStartDate(data.value)
  }
  return (
    <div>
      <Modal title="请维护租金表的收款日" store={store.collectionDateModal} width={420}>
        <Form
          initialValues={{
            sameStartDate: { label: '同起租日', value: true },
          }}
          preserve={false} style={{ height: 230 }}>
          <Item label={'是否同起租日'} name={'sameStartDate'} rules={[rules.required('请选择')]}>
            <Select
              onChange={bankChange}
              labelInValue
              options={[
                { label: '同起租日', value: true },
                { label: '指定收款日', value: false }
              ]}
            />
          </Item>
          {
            !sameStartDate &&
            <Item label={'收款日'} name={'defaultCollectionDay'} rules={[rules.required('请选择')]}>
              <SelectDayPanel />
            </Item>
          }
          <div style={{color:'red'}}>
          <p>1.是否匹配融资；</p>
          <p>2.若未匹配融资，除已与财务沟通确认外，收租日同起租日（晚于25日的收租日为25日）。</p>
        </div>
        </Form>

      </Modal>
    </div>
  )
}

export default observer(CpmPaymentWriteOffCollectionDate)
