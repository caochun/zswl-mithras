// 处理后端数据，转换为图表所需格式
export const transformData = (response) => {
  if (!response?.peers?.length) return []

  const { peers, range } = response
  const {
    minRoa,
    maxRoa,
    minRoe,
    maxRoe,
    minTotalAssets,
    maxTotalAssets,
    minNetAssets,
    maxNetAssets,
    minNetProfit,
    maxNetProfit,
  } = range

  // 标准化函数：将值映射到 0-1 范围
  const normalize = (value, min, max) => {
    if (max === min) return 1
    return (Number(value) - Number(min)) / (Number(max) - Number(min))
  }

  const dataSource = []
  peers.forEach((peer) => {
    // ROA
    dataSource.push({
      company: peer.enterpriseName,
      indicator: 'ROA',
      value: normalize(peer.roa, minRoa, maxRoa),
      rawValue: peer.roa,
    })

    // ROE
    dataSource.push({
      company: peer.enterpriseName,
      indicator: 'ROE',
      value: normalize(peer.roe, minRoe, maxRoe),
      rawValue: peer.roe,
    })

    // 总资产
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '总资产',
      value: normalize(peer.totalAssets, minTotalAssets, maxTotalAssets),
      rawValue: peer.totalAssets,
    })

    // 净资产
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '净资产',
      value: normalize(peer.netAssets, minNetAssets, maxNetAssets),
      rawValue: peer.netAssets,
    })

    // 净利润
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '净利润',
      value: normalize(peer.netProfit, minNetProfit, maxNetProfit),
      rawValue: peer.netProfit,
    })

    // 杠杆率
    const leverageValues = peers.map((p) => Number(p.leverageRatio))
    const minLeverage = Math.min(...leverageValues)
    const maxLeverage = Math.max(...leverageValues)
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '杠杆率',
      value: normalize(peer.leverageRatio, minLeverage, maxLeverage),
      rawValue: peer.leverageRatio,
    })
  })

  return dataSource
}
