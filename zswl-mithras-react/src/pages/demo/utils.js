import { SearchBar } from '@zswl/components'
import _ from 'lodash'
import { cloneElement } from 'react'

const { Item } = SearchBar

export function getTableColumns(allColumns, nameColumns, isSearchBar = false) {
  const arr = []
  nameColumns.forEach((column) => {
    let name
    let newColumn
    if (typeof column === 'string') {
      name = column
      const findColumn = allColumns.find((allColumn) => allColumn.title === name)
      if (findColumn) {
        const title = findColumn.rename ?? name
        newColumn = {
          ...findColumn,
          title,
          resizable: 'true',
          search: isSearchBar ? findColumn?.search : undefined,
        }
      }
    } else if (_.isObject(column)) {
      name = column.title
      if (column.children) {
        const newChildren = getTableColumns(allColumns, column.children)
        newColumn = {
          ...column,
          children: newChildren,
          resizable: 'true',
        }
      } else {
        const findColumn = allColumns.find((allColumn) => allColumn.title === name)
        if (findColumn) {
          const title = column.rename ?? findColumn.rename ?? name
          newColumn = {
            ...findColumn,
            ...column,
            title,
            resizable: 'true',
            search: isSearchBar ? findColumn?.search : undefined,
          }
        }
      }
    }
    if (newColumn) {
      arr.push(newColumn)
    } else {
      console.error(`${name}未找到`)
    }
  })
  return arr
}

export function getFormColumns(allColumns, nameColumns) {
  const arr = getTableColumns(allColumns, nameColumns)

  return arr.map(
    ({
      title: label,
      dataIndex: name,
      action,
      matchOption,
      dateFormat,
      tooltip,
      render,
      itemProps,
      editable,
      ...rest
    }) => {
      const options = matchOption === true ? name : matchOption
      if (editable) {
        const { element, ...editRest } = _.isFunction(editable) ? editable({}) : editable
        const newElement = cloneElement(element, editRest)
        return (
          <Item name={name} label={label} {...itemProps}>
            {newElement}
          </Item>
        )
      }

      return { label, name, options, itemProps, ...rest }
    }
  )
}

export const getSearchColumns = (allColumns, nameColumns) => {
  const arr = getTableColumns(allColumns, nameColumns, true)
  return arr.map(
    ({ title, dataIndex, matchOption, dateFormat, itemProps, search, type, ...rest }) => {
      return { title, dataIndex, matchOption, dateFormat, itemProps, type, ...search }
    }
  )
}

export const getNewFormColumns = (allColumns, nameColumns) => {
  const arr = getTableColumns(allColumns, nameColumns)
  return arr.map(
    ({ title, dataIndex, matchOption, dateFormat, itemProps, type, editable, search, ...rest }) => {
      return {
        title,
        dataIndex,
        matchOption,
        dateFormat,
        itemProps,
        type,
        ...rest,
      }
    }
  )
}

export function getDescColumns(allColumns, nameColumns) {
  const arr = getTableColumns(allColumns, nameColumns)
  return arr.map(({ width, formTooltip, tooltip, ...rest }) => {
    const newTooltip = formTooltip ? { title: formTooltip } : undefined

    return { tooltip: newTooltip, ...rest }
  })
}
