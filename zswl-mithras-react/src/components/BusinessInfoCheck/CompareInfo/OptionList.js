import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { Form, App } from '@zswl/components'
import { Input, Space, Empty } from 'antd'
import styles from '../index.less'

const { Item } = Form

const BusinessInfoCheckOptionList = ({ store }) => {
  const [form] = Form.useForm()
  const {
    optionList,
    taskActivityId,
    modelKey,
    flowId,
    formatNodeName,
    compareModal,
    canEditOpinion,
  } = store
  const hasOption = optionList?.length > 0

  useEffect(() => {
    if (compareModal?.visible) {
      store.getOpinionList()
    }
  }, [compareModal?.visible])

  useEffect(() => {
    if (hasOption) {
      form.setFieldValue('content', optionList)
    }
  }, [optionList, hasOption, form])

  return (
    <div style={{ marginTop: 20 }}>
      <h4>审批意见</h4>
      {hasOption ? (
        <Form
          layout="vertical"
          className={styles.form}
          form={form}
          preserve={false}
          autoComplete="off"
        >
          <Form.List name="content">
            {(fields) => {
              return fields?.map(({ key, name, ...restField }, index) => {
                const currentNode = optionList[index] ?? {}
                return (
                  <div key={key}>
                    <Item
                      rules={[{ required: true, message: '请输入' }]}
                      name={[name, 'opinion']}
                      label={
                        <Space>
                          <div>
                            {App.matchOption('processModelType', currentNode.moduleName)?.label}
                            阶段-
                            {formatNodeName(currentNode.nodeName)}意见
                          </div>
                          <div>{currentNode.createByName}</div>
                          <div>{currentNode.createTime}</div>
                        </Space>
                      }
                      {...restField}
                    >
                      <Input.TextArea
                        placeholder="请输入"
                        autoSize={{
                          minRows: 2,
                          maxRows: 6,
                        }}
                        // 1 7是可以编辑的
                        disabled={
                          !(
                            currentNode.moduleName === modelKey &&
                            currentNode.flowId === flowId &&
                            currentNode.nodeName === taskActivityId &&
                            canEditOpinion()
                          )
                        }
                      ></Input.TextArea>
                    </Item>
                  </div>
                )
              })
            }}
          </Form.List>
        </Form>
      ) : (
        <Empty></Empty>
      )}
    </div>
  )
}

export default observer(BusinessInfoCheckOptionList)
