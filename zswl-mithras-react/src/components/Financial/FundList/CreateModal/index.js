import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { useState, useEffect } from 'react'
import { debounce as _debounce } from 'lodash'
import FormAmount from '@/components/Form/FormAmount'
import { OrgListSelect } from '@/components/Select'
import Api from '@/api/financial/fundApi'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()
  const [orgId, setOrgId] = useState('')
  const [projectList, setProjectList] = useState([])

  const onOrgSelectChange = (value) => {
    form.setFieldsValue({
      fundCreditId: undefined,
    })
    setOrgId(value)
  }

  useEffect(() => {
    orgId && searchProject()
  }, [orgId])

  const searchProject = _debounce(async (e) => {
    const res = await Api.postEffectList({ organizationId: orgId, effective: true })
    setProjectList(res ?? [])
  }, 500)

  const onChange = (val) => {
    if (!val) {
      form.setFieldsValue({
        totalCreditLimit: undefined,
        remainingTotalLimit: undefined,
      })
      return
    }
    const curProj = projectList.filter((item) => item.id === val)[0]
    form.setFieldsValue({
      totalCreditLimit: curProj.totalCreditLimit,
      remainingTotalLimit: curProj.remainingTotalLimit,
    })
  }

  return (
    <Modal title={'新增融资'} store={store.$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 8 }} preserve={false}>
        <Item label="业务类型" name="businessType" required>
          <Select placeholder="请输入" allowClear options={'fundFinancingBizTypeEnum'} />
        </Item>
        <Item dependencies={['businessType']} noStyle>
          {({ getFieldValue }) => {
            const businessTypeVal = getFieldValue('businessType')
            if (businessTypeVal === 'SYNDICATIONS') {
              return null
            }
            return (
              <>
                <Item
                  label="机构授信"
                  name="organizationId"
                  rules={[{ required: true, message: '请选择！' }]}
                >
                  <OrgListSelect onChange={onOrgSelectChange} />
                </Item>
                <Item dependencies={['organizationId']} noStyle>
                  {({ getFieldValue }) => {
                    const organizationIdVal = getFieldValue('organizationId')
                    if (!organizationIdVal) {
                      return null
                    }
                    return (
                      <Item
                        label="授信编号"
                        name="fundCreditId"
                        rules={[{ required: true, message: '请选择！' }]}
                      >
                        <Select
                          placeholder="请输入"
                          allowClear
                          options={projectList}
                          filterOption={false}
                          style={{ maxWidth: '100%' }}
                          getPopupContainer={() => document.body}
                          fieldNames={{ value: 'id', label: 'creditCode' }}
                          onChange={onChange}
                        />
                      </Item>
                    )
                  }}
                </Item>
                <Item dependencies={['fundCreditId']} noStyle>
                  {({ getFieldValue }) => {
                    const fundCreditIdVal = getFieldValue('fundCreditId')
                    if (!fundCreditIdVal) {
                      return null
                    }
                    return (
                      <>
                        <FormAmount.Item
                          disabled
                          label={'总授信额度(元)'}
                          name={'totalCreditLimit'}
                          style={{ width: '100%' }}
                          required={false}
                        />
                        <FormAmount.Item
                          disabled
                          label={'剩余总授信额度(元)'}
                          name={'remainingTotalLimit'}
                          style={{ width: '100%' }}
                          required={false}
                        />
                      </>
                    )
                  }}
                </Item>
                <FormAmount.Item
                  rules={[
                    {
                      required: true,
                      message: '',
                    },
                  ]}
                  label={'融资金额(元)'}
                  name={'financingAmount'}
                  style={{ width: '100%' }}
                />
              </>
            )
          }}
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
