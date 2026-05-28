const { App } = require('@zswl/components')

const useGetStatus = ({ table } = {}) => {
  const auditStatus = App.getData().optionsType?.auditStatus ?? []
  const { account } = App.getData().user
  const application = auditStatus.filter((item) => ![2].includes(item.value))
  const completed = auditStatus.filter((item) => ![2, 4].includes(item.value))
  const approval = auditStatus.filter((item) => ![0].includes(item.value))

  const { rows = [] } = table?.getSelected() ?? {}
  const canEdit =
    rows.length === 1 &&
    [0, 2, 3, 5].includes(rows[0].auditStatus) &&
    (!rows[0].currentOperator || rows[0].currentOperator === App.getData().user.account)
  const canDelete = rows.length >= 1 && rows.every((item) => [0].includes(item.auditStatus))
  return {
    application,
    approval,
    completed,
    canEdit,
    canDelete,
  }
}
export default useGetStatus
