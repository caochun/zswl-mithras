import { SearchBar, Form, Input } from '@zswl/components'
import _ from 'lodash'
import { cloneElement } from 'react'

const FormItem = Form.Item
const SearchBarItem = SearchBar.Item

export function getTableColumns(allColumns, nameColumns, isSearchBar = false) {
  const arr = []
  if (!nameColumns) nameColumns = allColumns
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
          const search = column.search === true ? findColumn?.search : column.search
          newColumn = {
            ...findColumn,
            ...column,
            resizable: 'true',
            search: isSearchBar ? search : undefined,
            title,
          }
          // newColumn = { ..._.assignIn({}, findColumn, column), title, resizable: true }
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

/**
 *
 * @param {*} allColumns 所有配置
 * @param {*} nameColumns 选取部分
 * @param {*} type 'search'|'form', search: 搜索栏，form curd表单。区别是Col 样式
 * @returns
 */
export function getFormColumns(allColumns, nameColumns, type) {
  const arr = getTableColumns(allColumns, nameColumns)

  return arr.map(
    ({
      formComponent,
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
      if (formComponent) return formComponent
      const options = matchOption === true ? name : matchOption
      if (editable) {
        const { element, ...editRest } = _.isFunction(editable) ? editable({}) : editable
        const newElement = cloneElement(element, editRest)
        return (
          <SearchBarItem name={name} label={label} {...itemProps}>
            {newElement}
          </SearchBarItem>
        )
      }

      return { label, name, options, itemProps, ...rest }
    }
  )
}

export function getDescColumns(allColumns, nameColumns) {
  if (!nameColumns) nameColumns = allColumns.map((item) => item.title)
  const arr = getTableColumns(allColumns, nameColumns)
  return arr.map(({ width, formTooltip, tooltip, ...rest }) => {
    const newTooltip = formTooltip ? { title: formTooltip } : undefined

    return { tooltip: newTooltip, ...rest }
  })
}
export function getSearchColumns(allColumns, nameColumns) {
  const arr = getTableColumns(allColumns, nameColumns, true)
  return arr.map(({ search, ...rest }) => {
    return { ...rest, ...search }
  })
}
export function getFormItemProps(allColumns, nameColumns) {
  const arr = getTableColumns(allColumns, nameColumns)
  return arr.map(({ element, title, dataIndex, itemProps, editable, search, ...rest }) => {
    const newElement = cloneElement(element ?? <Input />, rest)
    return { title, dataIndex, element: newElement, ...itemProps }
  })
}
export function formScrollToField(e, form) {
  form.scrollToField(e.errorFields[0]?.name, {
    behavior(actions) {
      actions.forEach(({ el, top, left }) => {
        el.scrollTop = top + 100
        el.scrollLeft = left
      })
    },
  })
  return Promise.reject(e)
}
