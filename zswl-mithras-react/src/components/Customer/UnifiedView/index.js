import styles from './index.less'
import CardHeader from './CardHeader'
import { Avatar, Card, Checkbox, Input, List, Pagination, Radio, Space, Tag } from 'antd'
import { useEffect, useState } from 'react'
import { App, Button, Form, Page } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import CustomerViewApi from '@/api/customerView/customerDetailApi'
import { CheckGroup } from '@/components/Form'
import { getOrgList2 } from '@/components/Select'
import { AmountFormat, PercentageRender } from '@/components/Format'

const { Search } = Input
const CheckboxGroup = Checkbox.Group

const ContentList = ({ path, orgList, ...props }) => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState({ page: 1, pageSize: 10 })
  const { clientStatus } = App.getData().optionsType
  const [listData, setListData] = useState([])

  const [customerType, setCustomerType] = useState('all')
  // 内外请求list
  const loadAsync = async (e) => {
    const { type, ...params } = await form.validateFields()
    setCustomerType(type)
    const isAll = type === 'all'
    if (!isAll && !params?.enterpriseName) {
      message.error('请输入客户名称')
      return
    }
    if (isAll) {
      params.clientName = params?.enterpriseName
    }
    const requestParams = { ...params, ...page }
    setLoading(true)
    try {
      const data = await CustomerViewApi.getCustomerViewSearchList({
        isAll,
        params: requestParams,
      })
      setListData(data?.list ?? [])
      setTotal(data?.total)
      setLoading(false)
    } catch {
      setLoading(false)
    }
  }
  const pageChange = (page, pageSize) => {
    setPage({ page, pageSize })
    setTimeout(() => {
      loadAsync()
    }, 0)
  }
  useEffect(() => {
    loadAsync()
  }, [])
  const typeChange = (e) => {
    setCustomerType(e.target.value)
    setTotal(0)
    setListData([])
    setPage({ page: 1, pageSize: 10 })
    if (e.target.value === 'all') {
      loadAsync()
    }
  }
  return (
    <div>
      {/* 筛选区域 */}
      <Form
        initialValues={{
          type: 'all',
          orgCodeList: orgList.map((v) => v.value),
          clientStatus: ['NEW', 'TAKE_EFFECT'],
        }}
        form={form}
      >
        <div className={styles.filter}>
          <div className={styles.search}>
            <div className={styles.box}>
              <Form.Item name="type">
                <Radio.Group size="small" className={styles.radio} onChange={typeChange}>
                  <Radio.Button value="all">公司客户</Radio.Button>
                  <Radio.Button value="personal">外部客户</Radio.Button>
                </Radio.Group>
              </Form.Item>
              <Form.Item name="enterpriseName">
                <Search
                  placeholder="请输入客户名称"
                  loading={loading}
                  enterButton="查一下"
                  onSearch={loadAsync}
                />
              </Form.Item>
            </div>
          </div>
          <div style={{ width: '100%', display: customerType === 'all' ? 'block' : 'none' }}>
            <Form.Item name="orgCodeList" label="所属部门">
              <CheckGroup options={orgList} />
            </Form.Item>
            <Form.Item name="clientStatus" label="客户状态">
              <CheckboxGroup options={clientStatus} />
            </Form.Item>
          </div>
        </div>
      </Form>

      {/* 列表区域 */}
      <Card
        title={
          <div>
            共<span className={styles.total}>{total ?? 0}</span>个企业
          </div>
        }
      >
        <List
          itemLayout="horizontal"
          dataSource={listData}
          className={styles.list}
          loading={loading}
          renderItem={(item) => (
            <List.Item
              actions={[
                <Button
                  type="primary"
                  onClick={() => {
                    history.push(
                      `${path}/detail${item.clientId ? `/${item.clientId}` : ''}?enterpriseName=${
                        item?.clientName
                      }&uscc=${item?.uscCode}`
                    )
                  }}
                >
                  查看
                </Button>,
              ]}
            >
              <List.Item.Meta
                title={
                  <Space>
                    <div>{item.clientName}</div>
                    {item.clientStatus && (
                      <Tag color="#e5f5e7" style={{ color: '#24A631' }}>
                        {App.matchOption('clientStatus', item.clientStatus).label}
                      </Tag>
                    )}
                    {item.status && (
                      <Tag color="#e5f5e7" style={{ color: '#24A631' }}>
                        {item.status}
                      </Tag>
                    )}
                    {item.belongDeptName && (
                      <Tag color="#f3eafe" style={{ color: '#9147FF' }}>
                        {item.belongDeptName}
                      </Tag>
                    )}
                    {item.province && (
                      <Tag color="#f3eafe" style={{ color: '#9147FF' }}>
                        {item.province}
                      </Tag>
                    )}
                  </Space>
                }
                avatar={
                  <div className={styles.avatar}>
                    <Avatar
                      shape="square"
                      size={50}
                      style={{
                        color: '#2c63f4',
                        backgroundColor: '#f2f5fd',
                      }}
                    >
                      {item?.chinameabbr ?? item?.clientName.slice(0, 3)}
                    </Avatar>
                  </div>
                }
                description={
                  <div className={styles.description}>
                    <Space className={styles.info}>
                      <div>
                        法定代表人:
                        <span className={styles.title}>{item?.corpRepresent ?? '-'}</span>
                      </div>
                      <div>
                        注册资本:
                        <span className={styles.title}>
                          {PercentageRender(
                            item.registerCapital,
                            customerType === 'all' ? 10000 : 1
                          )}
                        </span>
                      </div>
                      <div>
                        成立日期: <span className={styles.title}>{item.establishDate ?? '-'}</span>
                      </div>
                      <div>
                        统一社会信用代码:
                        <span className={styles.title}>{item.uscCode ?? '-'}</span>
                      </div>
                    </Space>
                    <div className={styles.bizScope}>{item?.bizScope ?? item?.workRange}</div>
                  </div>
                }
              />
            </List.Item>
          )}
        />

        {/* 分页组件 */}
        <Pagination
          total={total}
          current={page.page}
          pageSize={page.pageSize}
          showQuickJumper
          onChange={pageChange}
          showSizeChanger
          style={{ marginTop: '16px', textAlign: 'right' }}
        />
      </Card>
    </div>
  )
}

const Index = ({ path }) => {
  // history.push(`${path}/detail/1`)
  const store = Page.useStore({
    request: async () => {
      const orgList = await getOrgList2({ name: '' }, 'userselectorg')
      return { orgList: orgList.filter((v) => v.state === 1) }
    },
  })
  const { orgList } = store.getData()
  return (
    <Page noStyle className={styles.page} store={store}>
      <div className={styles.title}>客户统一视图</div>
      <CardHeader />
      <ContentList path={path} orgList={orgList} />
    </Page>
  )
}
export default observer(Index)
