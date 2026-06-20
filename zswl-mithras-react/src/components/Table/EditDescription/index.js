import { observer } from '@zswl/admin'
import { Descriptions, Form, FormStore } from '@zswl/components'
import { Button, message, Space } from 'antd'
import { forwardRef, useImperativeHandle, useMemo, useState } from 'react'
import styles from './index.less'

const labelStyle = { background: '#F5F6FA', width: 180 }
const contentStyle = { minWidth: 230, maxWidth: 320 }

const EditDescriptionInner = observer(
  ({
    styleBtn,
    columns,
    detail,
    saveData,
    isLog,
    canEdit = true,
    hiddenButton,
    initEdit = false,
    cancelText = '编辑',
    confirmText = '保存',
    editExtra,
    cancelExtra,
    onEditStatusChange = () => {},
    formProps,
    forwardedRef,
    editBtnType = 'primary',
    btnSuffix,
    ...rest
  }) => {
    const [baseEdit, setBaseEdit] = useState(initEdit)
    const [loading, setLoading] = useState(false)
    const form = useMemo(() => {
      return new FormStore()
    }, [])
    const validateFields = async () => {
      try {
        await form.validateFields()
        return form.getFieldsFormatValue()
      } catch (e) {
        form.scrollToField(e.errorFields[0]?.name, {
          behavior: (actions) =>
            actions.forEach(({ el, top, left }) => {
              el.scrollTop = top + 100
              el.scrollLeft = left
            }),
        })
        throw new Error('表单校验不通过')
      }
    }

    const saveBase = async () => {
      try {
        const data = await validateFields()
        setLoading(true)
        await saveData?.(data)
        message.success('保存成功')
        setBaseEdit(false)
        onEditStatusChange(false)
      } catch (err) {
        console.error('保存失败:', err)
      } finally {
        setLoading(false)
      }
    }

    const items = useMemo(() => {
      if (!isLog) return columns
      return columns.map((v) => ({
        ...v,
        ...(isLog[v.dataIndex] && { contentStyle: { color: 'red' }, labelStyle: { color: 'red' } }),
      }))
    }, [isLog, columns])

    useImperativeHandle(forwardedRef, () => ({
      form,
      setBaseEdit,
      baseEdit,
      dataSource: detail,
      validateFields,
    }))

    const renderExtra = () => {
      if (hiddenButton) return null
      return (
        <div style={styleBtn}>
          <Space key="edit">
            {editExtra}
            {canEdit && baseEdit && (
              <>
                {cancelExtra}
                <Button
                  onClick={() => {
                    setBaseEdit(false)
                    onEditStatusChange(false)
                    form.resetFields()
                  }}
                >
                  取消
                </Button>
                <Button type="primary" onClick={saveBase} loading={loading}>
                  {confirmText}
                </Button>
              </>
            )}
            {canEdit && !baseEdit && (
              <Button
                type={editBtnType}
                onClick={() => {
                  setBaseEdit(true)
                  onEditStatusChange(true)
                }}
              >
                {cancelText}
              </Button>
            )}
            {btnSuffix}
          </Space>
        </div>
      )
    }

    return (
      <Form store={form} scrollToFirstError {...formProps}>
        <Descriptions
          title="基本信息"
          bordered
          column={2}
          dataSource={detail}
          items={items}
          extra={renderExtra()}
          contentStyle={contentStyle}
          editable={baseEdit}
          labelStyle={labelStyle}
          className={styles.desc}
          {...rest}
        />
      </Form>
    )
  }
)

const EditDescription = forwardRef((props, ref) => <EditDescriptionInner {...props} forwardedRef={ref} />)

export default EditDescription
