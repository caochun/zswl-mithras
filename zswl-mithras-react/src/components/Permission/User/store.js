import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, Modal } from '@zswl/components'
import Api from '@/api/permission/user'
import { message } from 'antd'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  list = new TableStore({
    request: async (params) => {
      const { page, type, name, ...rest } = params
      const { userList, total } = await Api.getList({
        curPage: page,
        [type]: name,
        ...rest,
      })
      return { list: userList, total }
    },
  })
  getJobList = async () => {
    const res = await Api.getJobList({ dictKey: 'job' })
    return res || []
  }
  orgList = []
  getOrgList = async () => {
    this.orgList = await Api.getOrg()
  }
  setRowId = (data) => {
    return data.map((v) => {
      v.title = v.groupName ?? v.menuName ?? v.funcName ?? v.fgroupName ?? v.name
      v.id = v.userId || v.id || v.menuId || v.fgroupId || v.funcId
      if (v.children) {
        v.children = this.setRowId(v.children)
      }
      return v
    })
  }
  permissionModal = new ModalStore({
    onOpen: async ({ id }) => {
      const data = await Api.getUserPermissions({ userId: id })
      return this.setRowId(data)
    },
  })
  editModal = new ModalStore({
    onOpen: (values) => {
      if (values) {
        const { expiration, jobsName, orgRolesName, ...rest } = values

        return {
          ...rest,
          orgJobs: jobsName?.map((item) => {
            const { orgId, jobNames } = item
            return {
              org: orgId,
              jobs:
                jobNames && jobNames.length ? jobNames.map(({ jobCode }) => jobCode) : undefined,
            }
          }),
          orgRoles: orgRolesName?.map((item) => {
            const { orgId, roles } = item
            return {
              org: orgId,
              roles: roles && roles.length ? roles.map(({ roleId }) => roleId) : undefined,
            }
          }),

          expiration: moment(expiration),
        }
      }
    },
    onFinish: async (values, initialValues) => {
      const { expiration, orgJobs, orgRoles, ...rest } = values
      const arr = []
      const jobArr = []
      orgRoles?.forEach((item) => {
        const { roles, org } = item
        roles.forEach((node) => {
          arr.push({ orgId: org, roleId: node })
        })
      })
      orgJobs?.forEach((item) => {
        const { jobs, org } = item
        jobs.forEach((node) => {
          jobArr.push({ orgId: org, jobCode: node })
        })
      })
      const data = {
        orgRoles: arr,
        expiration: expiration.valueOf(),
        orgJobs: jobArr,
        ...rest,
      }
      if (initialValues) {
        data.id = initialValues.id
        await Api.update(data)
        message.success('编辑成功')
      } else {
        await Api.create(data)
        message.success('创建成功')
      }
      this.editModal.close()
      this.list.search()
    },
  })
  resetPassword = ({ id }) => {
    Modal.confirm({
      title: '确认重置密码吗？',
      onOk: async () => {
        await Api.resetPassword({ id })
        message.success('重置用户密码成功')
        this.list.search()
      },
    })
  }
  remove = ({ id }) => {
    Modal.confirm({
      title: '确认删除用户吗？',
      onOk: async () => {
        await Api.remove({ id })
        message.success('删除用户成功')
        this.list.search()
      },
    })
  }
  changeStatus = async (id, status) => {
    await Api.changeStatus({ id, status: status ? 0 : 1 })
    message.success('修改用户状态成功')
    this.list.search()
  }
  dataSync = async (id) => {
    const res = await Api.sync({
      userId: this.editModal.getInitialValues().id,
    })
    if (res) {
      this.editModal.getFormStore().setFieldsValue({
        mainCode: res.mainCode,
      })
    }
  }
}

export default new Store()
