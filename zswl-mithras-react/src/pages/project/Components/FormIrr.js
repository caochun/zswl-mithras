import { Modal, ModalStore, Form } from '@zswl/components'
import { useMemo, useState } from 'react'
import { Button, InputNumber } from 'antd'
import classNames from 'classnames'
import styles from './index.less'
import { hasValue } from '@/utils'

const Index = ({
  value,
  onChange,
  handleOpen,
  disabled,
  canEdit = true,
  onBlur,
  isChange,
  ...rest
}) => {
  const [IRR, setIRR] = useState('')
  const [fileId, setFileId] = useState('')
  const handleDetail = async () => {
    window.open(`/preview/reportPreview/${fileId}`)
  }

  const modalStore = useMemo(() => {
    return new ModalStore({
      onOpen: async () => {
        // const data = await Api.postIrrCalculate({ id })
        // const { irr, fileId: newId } = data
        // setFileId(newId)
        // setIRR(irr)
        // return data
      },
    })
  }, [])
  const handleChange = (number) => {
    onChange?.(hasValue(number) ? Number(number).toFixed(2) : number)
  }
  const onFinish = async () => {
    handleChange?.((IRR * 100).toFixed(2))
    onBlur?.()
    modalStore.close()
  }
  const [loading, setLoading] = useState(false)
  const handleIRR = async () => {
    setLoading(true)
    try {
      const data = await handleOpen?.()
      const { irr, fileId: newId } = data
      setFileId(newId)
      setIRR(irr)
      setLoading(false)
      modalStore.open()
    } catch (e) {
      setLoading(false)
    }
  }
  const footer = (
    <>
      <Button onClick={modalStore.close}>取消</Button>
      <Button onClick={onFinish} type="primary">
        确认
      </Button>
    </>
  )
  return (
    <div
      style={{ display: 'flex', width: 200 }}
      className={classNames({
        [styles.inputChange]: !!isChange,
      })}
    >
      <InputNumber
        {...rest}
        suffix={<div>%</div>}
        value={value}
        onChange={handleChange}
        step="0.01"
        disabled={!canEdit}
        addonAfter="%"
        onBlur={onBlur}
      />
      <Button type="primary" onClick={handleIRR} style={{ marginLeft: 8 }} loading={loading}>
        IRR测算
      </Button>
      <Modal
        store={modalStore}
        title={'IRR测算结果'}
        // onOk={onFinish}
        width={300}
        footer={canEdit && footer}
      >
        <div>
          IRR = {(IRR * 100).toFixed(2)} %
          <Button type="link" onClick={handleDetail}>
            查看详情
          </Button>
        </div>
      </Modal>
    </div>
  )
}
Index.Item = ({ name, label, rules, ...rest }) => {
  return (
    <Form.Item
      name={name}
      label={label}
      rules={[
        ...rules,
        {
          validator(r, value) {
            if (hasValue(value)) {
              if (value > 1000) {
                return Promise.reject('不能大于1000')
              }
            }
            return Promise.resolve()
          },
        },
      ]}
    >
      <Index {...rest}></Index>
    </Form.Item>
  )
}

export default Index
