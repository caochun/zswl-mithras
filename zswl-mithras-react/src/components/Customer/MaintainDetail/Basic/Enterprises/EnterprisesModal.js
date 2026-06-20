import { Amount } from '@/components/Format'
import { observer, getSessionStorage, setSessionStorage } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import { Input, DatePicker, Cascader, InputNumber } from 'antd'

const { Item } = Form

//新增关联企业
function Index({ store }) {
  const { allIndustry } = App.getData().optionsType
  return (
    <Modal
      title={'关联企业'}
      store={store.enterprisesModal}
      okText={'确定'}
      width={480}
      destroyOnClose
    >
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} preserve={false}>
        <Item
          label={'关联企业名称'}
          name={'enterpriseName'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Input placeholder={'请输入'} />
        </Item>
        <Item label={'成立年份'} name={'establishDate'}>
          <DatePicker style={{ width: '100%' }} />
        </Item>
        <Item
          label={'关联关系'}
          name={'relationship'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Select placeholder={'请输入'} options={'relationshipType'} />
        </Item>
        <Item label={'注册资本(万元)'} name={'registerCapital'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item label={'持股比例(%)'} name={'shareholdingRatio'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item
          label={'存续状态'}
          name={'continuousStatus'}
          rules={[
            {
              required: true,
            },
          ]}
        >
          <Select placeholder={'请输入'} options={'continuousStatus'} />
        </Item>
        <Item label={'行业'} name={'industryTypeEnter'}>
          <Cascader style={{ width: '100%' }} options={allIndustry} />
        </Item>
        <Item label={'投资金额(万元)'} name={'investAmount'}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
