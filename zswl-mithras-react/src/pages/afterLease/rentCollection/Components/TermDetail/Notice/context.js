const generateGroupList = (list) => {
  return list.reduce((acc, cur) => {
    if (!acc[cur.accountBank]) {
      acc[cur.accountBank] = {}
    }
    if (!acc[cur.accountBank][cur.accountNumber]) {
      acc[cur.accountBank][cur.accountNumber] = {}
    }
    acc[cur.accountBank][cur.accountNumber][cur.accountName] = null
    return acc
  }, {})
}

export const treeData = (list) => {
  const groupList = generateGroupList(list)
  const result = Object.keys(groupList).map((accountBank) => ({
    label: accountBank,
    value: list.find((item) => item.accountBank === accountBank)?.id,
    children: Object.keys(groupList[accountBank]).map((accountNumber) => ({
      label: accountNumber,
      value: accountNumber,
      children: Object.keys(groupList[accountBank][accountNumber]).map((accountName) => ({
        label: accountName,
        value: accountName,
      })),
    })),
  }))
  return result || []
}
