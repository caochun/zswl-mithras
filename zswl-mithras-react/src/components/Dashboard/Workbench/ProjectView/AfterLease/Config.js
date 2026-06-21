import ProjectAfterLeaseCheckTable from './AfterLeaseListDrawer/AfterLeaseCheck/ProjectAfterLeaseCheckTable'

export const initFieldsConfig = [
  {
    group: '租后检查',
    groupCode: 'CLIENT_AFTER_LEASE',
    iconType: 'icon-zuhoujiancha',
    component: <ProjectAfterLeaseCheckTable></ProjectAfterLeaseCheckTable>,
  },
]

export const getNameColumns = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current?.component ?? <div></div>
}

export const columnsFilterKey = '工作台_业务部_租后检查'
