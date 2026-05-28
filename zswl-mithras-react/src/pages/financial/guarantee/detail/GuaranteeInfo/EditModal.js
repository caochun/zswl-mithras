import { observer } from '@zswl/admin'
import { App, Button, Form, Modal } from '@zswl/components'
import { DatePicker, Input, InputNumber, Radio } from 'antd'
import DataUpload from '@/components/DataUpload'
import moment from 'moment'
import { rangePresets } from '@/utils/date'
import { ReadOnly,  } from '@/components'
import { amountFormat, formatPercent } from '@/utils'
import _ from 'lodash'
import numeral from 'numeral'

const { Item } = Form
function EditModal({ store }) {
  const form = Form.useStore()
  const { editMode } = store
  const handleFinish = async () => {
    const values = await form.validateFields()
    const data = form.getFieldsFormatValue()
    store.finish({ ...data, agencyId: +store.id })
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
    <Button key="cancel" onClick={store.createModal.close}>
      取消
    </Button>,
    editMode !== 1 && (
      <Button key="submit" type="primary" onClick={handleFinish} loading={store.okLoading}>
        确定
      </Button>
    ),
  ]
  const InputEditable = (props) => {
    return editMode === 1 ? <ReadOnly {...props} /> : <Input placeholder="请输入" {...props} />
  }
  const TextAreaEditable = (props) => {
    return editMode === 1 ? (
      <ReadOnly {...props} />
    ) : (
      <Input.TextArea placeholder="请输入" autoSize={{ minRows: 4, maxRows: 20 }} {...props} />
    )
  }
  const InputNumberEditable = (props) => {
    const newValue = amountFormat(props.value)
    return editMode === 1 ? (
      <ReadOnly value={newValue} />
    ) : (
      <InputNumber
        placeholder="请输入"
        style={{ width: '100%' }}
        formatter={(value) => {
          return numeral(value).format('0,0.[0000]')
          // 导致小数点后千分位
          // return `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
        }}
        parser={(value) => value.replace(/\$\s?|(,*)/g, '')}
        {...props}
      />
    )
  }
  const RadioEditable = ({ value, options, onlyRead, ...rest }) => {
    const newValue = App.matchOption(options, value).label
    const { optionsType } = App.getData()
    const newOptions = (_.isArray(options) ? options : optionsType[options]) || []
    return onlyRead ? (
      <ReadOnly value={newValue} />
    ) : (
      <Radio.Group options={newOptions} value={value} {...rest} />
    )
  }
  const DatePickerEditable = (props) => {
    const newValue = (props?.value || []).map((v) => v?.format('yyyy-MM-DD')).join(' - ')
    return editMode === 1 ? <ReadOnly value={newValue} /> : <DatePicker.RangePicker {...props} />
  }
  const FileEditable = (props) => {
    return editMode === 1 ? (
      <DataUpload.List
        value={props.value}
        params={{
          moduleType: 'FUND_GUARANTEE_AGENCY',
          mainId: +store.id,
        }}
      />
    ) : (
      <DataUpload accept="*" {...props} />
    )
  }
  return (
    <Modal
      title={`${editMode === 0 ? '新增' : '编辑'}担保`}
      store={store.createModal}
      okText={'确定'}
      footer={footer}
      destroyOnClose
    >
      <Form store={form} labelCol={{ span: 10 }} preserve={true}>
        <Item
          label={'总担保额度（元）'}
          name={'totalGuaranteeLimit'}
          rules={[{ required: true, message: '请输入总担保额度（元）！' }]}
          transform={(value) => ({
            ['totalGuaranteeLimit']: value * 10000,
          })}
        >
          <InputNumberEditable />
        </Item>
        {editMode === 1 && (
          <>
            <Item
              label={'已使用担保额度（元）'}
              name={'usedGuaranteeLimit'}
              rules={[{ required: true, message: '请输入已使用担保额度（元）！' }]}
              transform={(value) => ({
                ['usedGuaranteeLimit']: value * 10000,
              })}
            >
              <InputNumberEditable />
            </Item>
            <Item
              label={'剩余担保额度（元）'}
              name={'remainingGuaranteeLimit'}
              rules={[{ required: true, message: '请输入剩余担保额度（元）！' }]}
              transform={(value) => ({
                ['remainingGuaranteeLimit']: value * 10000,
              })}
            >
              <InputNumberEditable />
            </Item>
          </>
        )}

        {editMode === 2 && (
          <Item
            label={'担保状态'}
            name={'effective'}
            rules={[{ required: true, message: '请输入担保状态！' }]}
          >
            <RadioEditable options="effective" onlyRead={editMode === 1} />
          </Item>
        )}
        <Item
          label={'担保生效时间'}
          name={'effectiveTime'}
          transform={(values) => {
            return {
              ['effectiveTime']: undefined,
              ['effectiveTimeFrom']: values?.[0] && moment(values?.[0]).format('YYYY-MM-DD'),
              ['effectiveTimeTo']: values?.[0] && moment(values?.[1]).format('YYYY-MM-DD'),
            }
          }}
          rules={[{ required: true, message: '请输入担保生效时间！', type: 'array' }]}
        >
          <DatePickerEditable placeholder={['开始时间', '结束时间']} ranges={rangePresets} />
        </Item>
        <Item label={'资料文件'} name={'files'}>
          <FileEditable accept="*" />
        </Item>
        <Item label={'备注'} name={'remark'}>
          <TextAreaEditable />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
