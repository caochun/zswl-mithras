import _ from 'lodash'
import userCustomConfigApi from '@/utils/api/userPreferenceApi'

export const CARD_COLUMNS_FILTER = 'z-card-columns-filter'
export const TABLE_COLUMNS_FILTER = 'z-table-columns-filter'

export function getLocalColumnsFilter(id, filterName = CARD_COLUMNS_FILTER) {
  if (typeof id === 'string' || [null, undefined].includes(id)) {
    let local = localStorage.getItem(filterName)
    if (local) {
      try {
        local = JSON.parse(local)
        return id ? local[id] || {} : local
      } catch {
        return {}
      }
    }
  }
  return {}
}
export function setLocalColumnsFilter(id, columns, filterName = CARD_COLUMNS_FILTER) {
  if (typeof id === 'string') {
    const local = getLocalColumnsFilter(null, filterName)
    local[id] = columns
    localStorage.setItem(filterName, JSON.stringify(local))
  }
}

export const saveServer = async (columnsFilterKey,value) => {
  await userCustomConfigApi.saveCustomConfig({
    configKey: columnsFilterKey,
    configValue: JSON.stringify(value),
  })
  setLocalColumnsFilter(columnsFilterKey, JSON.stringify(value))
}
export function setDefaultFilter(
  columnsFilterKey,
  defaultValue,
  filterName = 'z-table-columns-filter'
) {
  const local = getLocalColumnsFilter(columnsFilterKey, filterName)
  if (_.isEmpty(local)) {
    setLocalColumnsFilter(columnsFilterKey, defaultValue, filterName)
  }
}

export function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}
