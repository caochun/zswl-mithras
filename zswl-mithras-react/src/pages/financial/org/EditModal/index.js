import { observer } from '@zswl/admin'
import { App, Form, Modal, Select, Button } from '@zswl/components'
import { Col, Divider, Input, Row, Tooltip, Space, message, DatePicker } from 'antd'
import { useCallback } from 'react'
import FormAccount from './FormAccount'
import { ReadOnly } from '@/components'
import IconFont from '@/components/Icon'
import styles from './index.less'
import { rules } from '@/utils'
import CountryCascader from './CountryCascader'
import Api from './api'
import { FormAmount } from '@/components/Form'

const { Item } = Form
function EditModal({ store }) {
  const form = Form.useStore()
  const { editMode } = store
  const handleFinish = async () => {
    const values = await form.validateFields()
    const data = form.getFieldsFormatValue(true)
    store.finish(data)
  }
  const footer = [
    editMode === 1 && (
      <Button
        key="edit"
        type="primary"
        onClick={() => {
          store.editMode = 2
        }}
      >
        编辑
      </Button>
    ),
    <Button key="cancel" onClick={() => store.createModal.close()}>
      取消
    </Button>,
    editMode !== 1 && (
      <Button key="submit" type="primary" onClick={handleFinish}>
        确定
      </Button>
    ),
  ]
  const isRead = [1, 3].includes(editMode)
  const InputEditable = useCallback(
    (props) => {
      return isRead ? <ReadOnly {...props} /> : <Input placeholder="请输入" {...props} />
    },
    [editMode]
  )
  const SelectRead = ({ value, options }) => {
    const newValue = App.matchOption(options, value).label
    return <ReadOnly value={newValue} />
  }
  const DatePickerEditable = useCallback(
    (props) => {
      return isRead ? (
        <ReadOnly value={props.value && moment(props.value).format('yyyy-MM-DD')} />
      ) : (
        <DatePicker placeholder="请选择" {...props} />
      )
    },
    [editMode]
  )
  const setInstitutionCode = async () => {
    const { getFieldValue, setFieldValue } = form
    const organizationType = getFieldValue('organizationType')
    const uscCode = getFieldValue('uscCode')
    if (organizationType && uscCode) {
      const res = await Api.getFundInstitutionCode({ uscCode })
      setFieldValue('institutionCode', res.institutionCode)
    } else {
      message.info('请填写机构类型、统一社会信用代码')
    }
  }

  return (
    <Modal
      title={isRead ? '机构详情' : '机构新增'}
      store={store.createModal}
      footer={editMode !== 3 && footer}
      destroyOnClose
      width={520}
    >
      <Form
        store={form}
        preserve={true}
        layout={isRead ? 'horizontal' : 'vertical'}
        initialValues={{
          organizationType: 'BANK',
          accountsInfo: [
            {
              accountName: '',
            },
          ],
        }}
        className={styles.modalForm}
      >
        <p className={'z-sub-title'}>基本信息</p>
        <Row gutter={40}>
          <Col span={12}>
            <Item
              label={'机构名称'}
              name={'organizationName'}
              rules={[{ required: !isRead, message: '请输入机构名称！' }]}
            >
              <InputEditable />
            </Item>
          </Col>
          <Col span={12}>
            <Item
              label={'机构简称'}
              name={'abbreviation'}
              rules={[{ required: !isRead, message: '请输入机构简称！' }]}
            >
              <InputEditable />
            </Item>
          </Col>
          <Col span={12}>
            <Item
              label={'机构类型'}
              name={'organizationType'}
              rules={[{ required: !isRead, message: '请选择机构类型！' }]}
            >
              {isRead ? (
                <SelectRead options={'organizationType'} />
              ) : (
                <Select
                  placeholder={'请选择'}
                  options="organizationType"
                  onChange={() => {
                    form.setFieldValue('institutionCode', undefined)
                  }}
                />
              )}
            </Item>
          </Col>
          <Item dependencies={['organizationType']} noStyle>
            {({ getFieldValue }) => {
              const organizationType = getFieldValue('organizationType')
              return (
                <>
                  {organizationType === 'BANK' ? (
                    <Col span={24}>
                      <Item
                        label={'银行联行号'}
                        name={'interBankNo'}
                        rules={[{ required: !isRead, message: '请输入银行联行号！' }]}
                      >
                        <InputEditable />
                      </Item>
                    </Col>
                  ) : (
                    <Col span={24}>
                      <Item
                        label={'统一社会信用代码'}
                        name={'uscCode'}
                        rules={[
                          { required: !isRead, message: '请输入统一社会信用代码！' },
                          rules.creditCode(),
                        ]}
                      >
                        <InputEditable />
                      </Item>
                    </Col>
                  )}
                </>
              )
            }}
          </Item>
          <Item dependencies={['organizationType', 'uscCode']} noStyle>
            {({ getFieldValue }) => {
              const organizationType = getFieldValue('organizationType')
              const isRequired = organizationType === 'BANK'

              return (
                <>
                  <Col span={24}>
                    <Item
                      label={
                        <Space>
                          <div>
                            <Tooltip
                              title={
                                <div>
                                  <div>1）当机构类型=“银行”时，请填写财务系统的金融机构代码；</div>
                                  <div>
                                    2）当机构类型=“租赁公司”或“集团公司”时，请填写客商系统的客户编码；
                                  </div>
                                  <div>3）其他机构类型</div>
                                </div>
                              }
                            >
                              机构代码
                              <IconFont className={styles.header_icon} type={'icon-icon_info'} />
                            </Tooltip>
                          </div>
                          {['ZL', 'JT'].includes(organizationType) && !isRead && (
                            <Button type="primary" size="small" onClick={setInstitutionCode}>
                              系统获取
                            </Button>
                          )}
                        </Space>
                      }
                      name={'institutionCode'}
                      rules={[{ required: !isRead && isRequired, message: '请输入机构代码！' }]}
                    >
                      <InputEditable />
                    </Item>
                  </Col>
                </>
              )
            }}
          </Item>
          <CountryCascader form={form} onlyRead={isRead} />
          <Item dependencies={['organizationType']} noStyle>
            {({ getFieldValue }) => {
              const organizationType = getFieldValue('organizationType')
              const isRequired = organizationType === 'BANK'

              return (
                <>
                  <Col span={12}>
                    <Item
                      label={'活期存款利率'}
                      name={'currentDepositRate'}
                      rules={[{ required: !isRead && isRequired, message: '请输入活期存款利率！' }]}
                    >
                      {isRead ? <FormAmount.Format suffix="%" /> : <FormAmount addonAfter="%" />}
                    </Item>
                  </Col>
                  <Col span={12}>
                    <Item
                      label={'协定存款利率'}
                      name={'agreementDepositRate'}
                      rules={[{ required: !isRead && isRequired, message: '请输入协定存款利率！' }]}
                    >
                      {isRead ? <FormAmount.Format suffix="%" /> : <FormAmount addonAfter="%" />}
                    </Item>
                  </Col>
                  <Col span={24}>
                    <Item
                      label={'协定存款利率到期日'}
                      name={'agreementDepositRateDueTime'}
                      rules={[
                        { required: !isRead && isRequired, message: '请输入协定存款利率到期日！' },
                      ]}
                      transform={(value) => value && moment(value).format('YYYY-MM-DD')}
                    >
                      <DatePickerEditable />
                    </Item>
                  </Col>
                </>
              )
            }}
          </Item>
        </Row>
        <Divider />
        <p className={'z-sub-title'}>联系人信息</p>
        <Row gutter={40}>
          <Col span={12}>
            <Item label={'联系人姓名'} name={['contactInfo', 'name']}>
              <InputEditable />
            </Item>
          </Col>
          <Col span={12}>
            <Item label={'职务'} name={['contactInfo', 'job']}>
              <InputEditable />
            </Item>
          </Col>
          <Col span={12}>
            <Item label={'手机号码'} name={['contactInfo', 'tel']} rules={[rules.phoneCode()]}>
              <InputEditable />
            </Item>
          </Col>
          <Col span={12}>
            <Item
              label={'邮箱'}
              name={['contactInfo', 'email']}
              rules={[{ type: 'email', message: '请输入正确的邮箱！' }]}
            >
              <InputEditable />
            </Item>
          </Col>
        </Row>
        <Item dependencies={['organizationType']} noStyle>
          {({ getFieldValue }) => {
            const organizationType = getFieldValue('organizationType')
            const isBank = organizationType === 'BANK'
            if (isBank) return null
            return (
              <>
                <Divider />
                <p className={'z-sub-title'}>账户信息</p>
                <FormAccount name="accountsInfo" onlyRead={isRead} />
              </>
            )
          }}
        </Item>
        <Divider />
        <p className={'z-sub-title'}>备注 </p>

        <Row>
          <Col span={24}>
            <Item label={'备注'} name={'remark'}>
              {isRead ? <ReadOnly /> : <Input.TextArea placeholder={'请输入'} />}
            </Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
