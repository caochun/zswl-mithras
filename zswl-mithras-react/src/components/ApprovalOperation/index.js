import { Input, Space, Row, Col, message } from 'antd'
import { useEffect, useState } from 'react'
import moment from 'moment'
import styles from './style.less'
import { Select, Form, App, Table, Button, Modal } from '@zswl/components'
import { http } from '@zswl/admin'
import { saveServer } from '@/utils'
// onFilter={(key,val) => saveServer('components_ApprovalOperation_1',val)}

const emojiRegex =
  /[\uD800-\uDBFF][\uDC00-\uDFFF]|\uD83C[\uDC00-\uDFFF]|\uD83D[\uDC00-\uDE4F\uDE80-\uDEFF]/g

const validateEmoji = (_, value, callback) => {
  if (emojiRegex.test(value)) {
    callback('不支持输入表情')
  } else {
    callback()
  }
}
const { Item } = Form
const getCurrentDate = () => moment().format('YYYY-MM-DD HH:mm:ss')
const Date = () => {
  const [date, setDate] = useState(getCurrentDate)
  useEffect(() => {
    const timer = setInterval(() => {
      setDate(getCurrentDate)
    }, 1000)
    return () => {
      clearInterval(timer)
    }
  }, [])
  return date
}
function Index({ operation, form, style, showTitle = true, disabled }) {
  const { userName, orgRolesName, account } = App.useData().user
  const [tags, setTags] = useState([])
  const getTags = () => http.get('/audit/common/comment/get', { params: { account } })
  const listStore = Table.useStore({
    pagination: false,
    async request() {
      const res = await getTags()
      return (res || []).map((item, index) => ({ id: index, title: item }))
    },
  })
  const modalStore = Modal.useStore()
  const handleOk = async () => {
    const { list } = await listStore.submit()
    const comments = list.map((item) => item.title)
    await http.post('/audit/common/comment/save', {
      account,
      comments,
    })
    setTags(comments)
    modalStore.close()
  }
  useEffect(() => {
    if (operation) {
      getTags().then((res) => {
        setTags(res)
      })
    }
  }, [operation, account])
  return (
    <div className={styles.container} style={style}>
      {operation && (
        <>
          <Row>
            <Col span={8}>
              <Item label={'审批意见'} name={'audit'} rules={[{ required: true }]}>
                <Select
                  disabled={disabled}
                  getPopupContainer={() => document.body}
                  options={[
                    { label: '同意', value: 1 },
                    { label: '退回', value: 2 },
                  ]}
                />
              </Item>
            </Col>
          </Row>
          <Row gutter={20}>
            <Col span={12}>
              <Item noStyle dependencies={['audit']}>
                {({ getFieldValue }) => {
                  const val = getFieldValue('audit')
                  return (
                    <Item
                      className={styles.suggest}
                      label={'审批详情'}
                      name={'suggest'}
                      rules={[
                        { required: val === 2 },
                        {
                          validator: validateEmoji,
                        },
                      ]}
                    >
                      <Input.TextArea disabled={disabled} rows={4} />
                    </Item>
                  )
                }}
              </Item>
            </Col>
            <Col span={12}>
              <Item noStyle dependencies={['audit']}>
                {({ getFieldValue }) => {
                  const val = getFieldValue('audit')
                  return (
                    val === 2 && (
                      <Item
                        label={
                          <Space>
                            <Col>选择并填充常用词</Col>
                            <Col>
                              <a onClick={modalStore.open}>编辑</a>
                            </Col>
                          </Space>
                        }
                      >
                        <div className={styles.action}>
                          <Space>
                            {tags.map((name) => {
                              return (
                                <Button
                                  size={'small'}
                                  key={name}
                                  disabled={disabled}
                                  onClick={() => form?.setFieldsValue({ suggest: name })}
                                  icon={null}
                                >
                                  {name}
                                </Button>
                              )
                            })}
                          </Space>
                        </div>
                      </Item>
                    )
                  )
                }}
              </Item>
            </Col>
          </Row>
        </>
      )}
      {showTitle && <div className="z-sub-title">操作人信息</div>}
      <Table
        pagination={false}
        rowKey={'userName'}
        dataSource={[{ userName, org: orgRolesName?.map((item) => item.orgName).join(',') }]}
        columnsFilter={'components_ApprovalOperation_1'}
        onFilter={(key,val) => saveServer('components_ApprovalOperation_1',val)}

        columns={[
          { title: '操作人', dataIndex: 'userName' },
          {
            title: '操作时间',
            dataIndex: 'date',
            render() {
              return <Date />
            },
          },
          { title: '所属机构', dataIndex: 'org' },
        ]}
      />
      <Modal
        title={'编辑审批常用词'}
        store={modalStore}
        onCancel={modalStore.close}
        onOk={handleOk}
        destroyOnClose
      >
        <Table
          store={listStore}
          serial
          scroll={{}}
          columnsFilter={'components_ApprovalOperation_2'}
          onFilter={(key,val) => saveServer('components_ApprovalOperation_2',val)}
          actions={[
            <Button.Add
              key={'add'}
              onClick={() => {
                if (listStore.getList().length < 5) {
                  listStore.addRow()
                } else {
                  message.warn('最多添加5个常用词')
                }
              }}
            >
              添加常用词
            </Button.Add>,
          ]}
          columns={[
            {
              title: '常用词',
              dataIndex: 'title',
              editable: {
                rules: [
                  { required: true, message: '请输入常用词' },
                  { max: 10, type: 'string', message: '最多10个字' },
                ],
              },
            },
            {
              title: '操作',
              width: 80,
              render(data) {
                return <a onClick={() => listStore.deleteRow(data)}>删除</a>
              },
            },
          ]}
        />
      </Modal>
    </div>
  )
}

export default Index
