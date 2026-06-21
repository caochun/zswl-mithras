import CustomerAfterLeaseCheckTable from './AfterLeaseListDrawer/AfterLeaseCheck/CustomerAfterLeaseCheckTable'

export const initFieldsConfig = [
  {
    group: '租后检查',
    groupCode: 'CLIENT_AFTER_LEASE',
    iconType: 'icon-zuhoujiancha',
    component: <CustomerAfterLeaseCheckTable></CustomerAfterLeaseCheckTable>,
  },
]

export const getNameColumns = (groupCode) => {
  const current = initFieldsConfig.find((item) => item.groupCode === groupCode)
  return current?.component ?? <div></div>
}

export const columnsFilterKey = '工作台_客户视图_租后管理'
