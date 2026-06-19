import { observer } from '@zswl/admin'
import { Access, Button, SearchBar, Table, Page } from '@zswl/components'
import store from './store'
import { OrgTreeSelect } from './components'
import { Input, Select, Space, Switch, Tooltip } from 'antd'
import moment from 'moment'
import Edit from './Edit'
import { useEffect } from 'react'
import PermissionModal from './PermissionModal'
import { saveServer } from '@/utils'

const { Item } = SearchBar
function Index() {
  useEffect(() => {
    store.getOrgList()
  }, [])

  return (
    <Page>
      <Table
        columnsFilter={'permission_user_1'}
        onFilter={(key, val) => saveServer('permission_user_1', val)}
        store={store.list}
        searchbar={
          <SearchBar
            initialValues={{ type: 'userName' }}
            trigger={['orgId', 'name']}
            extra={<Button.Add onClick={store.editModal.open} />}
          >
            <Item name={'orgId'}>
              <OrgTreeSelect />
            </Item>
            <Item name={'name'}>
              <Input
                addonBefore={
                  <Item noStyle name={'type'}>
                    <Select
                      style={{ width: 120 }}
                      allowClear={false}
                      options={[
                        { label: '用户名', value: 'userName' },
                        { label: '登录账号', value: 'account' },
                      ]}
                    />
                  </Item>
                }
              />
            </Item>
          </SearchBar>
        }
        columns={[
          {
            title: '用户名/登录名',
            render({ account, userName, avatar }) {
              return (
                <Space size={4}>
                  <img
                    style={{ width: 48, height: 48, borderRadius: 4 }}
                    src={`/public/avatar/${avatar || 'empty'}.png`}
                    alt="avatar"
                  />
                  <Space direction={'vertical'} size={0}>
                    <h4>{userName}</h4>
                    <span>登录名：{account}</span>
                  </Space>
                </Space>
              )
            },
          },
          {
            title: '所在机构/角色',
            render({ orgRolesName }) {
              const allRoles = orgRolesName?.map(({ roles }) => {
                return roles.map((node, index) => {
                  return node.roleName
                })
              })
              const title = (
                <div>
                  {orgRolesName?.map((item) => {
                    const { orgId, orgName, roles = [] } = item
                    return (
                      <div key={orgId}>
                        <Space direction={'vertical'} size={2}>
                          <span>{orgName}:</span>
                          <Space style={{ marginLeft: 20 }} size={8}>
                            {roles.map((node, index) => (
                              <span key={index}>{node.roleName}</span>
                            ))}
                          </Space>
                        </Space>
                      </div>
                    )
                  })}
                </div>
              )
              return (
                <Tooltip placement={'topLeft'} title={orgRolesName?.length > 0 && title}>
                  {allRoles?.join(',') || '-'}
                </Tooltip>
              )
            },
          },
          {
            title: '所在机构/岗位',
            dataIndex: 'jobsName',
            render(jobsName) {
              const title = (
                <div>
                  {jobsName?.map((item, t) => {
                    const { orgId, orgName, jobNames } = item
                    return (
                      <div key={t}>
                        <Space direction={'vertical'} size={2}>
                          <span>{orgName}:</span>
                          <Space style={{ marginLeft: 20 }} size={8}>
                            {jobNames?.map((i) => {
                              return i.jobName
                            })}
                          </Space>
                        </Space>
                      </div>
                    )
                  })}
                </div>
              )
              return (
                <Tooltip placement={'topLeft'} title={jobsName?.length > 0 && title}>
                  {jobsName
                    ? jobsName
                      ?.map((item, t) => {
                        const { orgName, jobNames } = item
                        return `${orgName}:${jobNames
                          ?.map((i) => {
                            return i.jobName
                          })
                          .join(',')}`
                      })
                      .join(';')
                    : '-'}
                </Tooltip>
              )
            },
          },
          {
            title: '过期时间',
            width: 120,
            render({ expiration }) {
              if (!expiration) {
                return null
              }
              return (
                <Tooltip title={expiration}>
                  {moment(expiration).startOf('hours').fromNow().replace('内', '后')}
                </Tooltip>
              )
            },
          },
          // {
          //   title: '最后登录时间',
          //   dataIndex: 'loginTime',
          // },
          // {
          //   title: '最后登录IP',
          //   dataIndex: 'ipAddr',
          // },
          {
            title: '状态',
            width: 100,
            render: ({ id, status }) => {
              return (
                <Switch
                  disabled={!Access.validate('userLockUnlock')}
                  // checkedChildren={'正常'}
                  // unCheckedChildren={'禁用'}
                  checked={status === 0}
                  onChange={(checked) => store.changeStatus(id, checked)}
                />
              )
            },
          },
          {
            title: '操作',
            fixed: 'right',
            width: 230,
            actions() {
              return [
                { name: '重置密码', onClick: store.resetPassword, access: 'resetPassword' },
                {
                  name: '编辑',
                  onClick: store.editModal.open,
                  access: 'modifyUser',
                },
                {
                  name: '查看权限',
                  onClick: store.permissionModal.open,
                },
                { name: '删除', onClick: store.remove, access: 'deleteUser' },
              ]
            },
          },
        ]}
      />
      <Edit />
      <PermissionModal store={store.permissionModal} />
    </Page>
  )
}

export default observer(Index)
