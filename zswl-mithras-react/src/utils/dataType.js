/* eslint-disable */

function isOO(obj) {
  return Object.prototype.toString.call(obj) === '[object Object]'
}
function isObj(obj) {
  return obj !== null && typeof obj === 'object' && !isArr(obj)
}
function isPlainObj(obj) {
  if (!isOO(obj)) {
    return false
  }
  const ctor = obj.constructor
  if (!isFunc(ctor)) {
    return false
  }
  const proto = ctor.prototype
  if (!isOO(proto)) {
    return false
  }
  return proto.hasOwnProperty('isPrototypeOf')
}
function isFunc(obj) {
  return typeof obj === 'function'
}
function isNum(value) {
  return value !== null && typeof value === 'number' && value - value + 1 === 1
}
function isStr(obj) {
  return typeof obj === 'string'
}
function isBool(obj) {
  return typeof obj === 'boolean'
}
function isArr(obj) {
  return Array.isArray(obj)
}
function isUNN(obj) {
  return obj === null || obj === undefined || Number.isNaN(obj)
}
function isEmptyObj(obj) {
  for (let key in obj) {
    return false
  }
  return true
}
function isPromise(obj) {
  return (isObj(obj) || isFunc(obj)) && isFunc(obj.then)
}

function isEvent(e) {
  return e && e.preventDefault && e.stopPropagation && Object.hasOwnProperty.call(e, 'target')
}

export const DataType = {
  isObj,
  isPlainObj,
  isFunc,
  isBool,
  isNum,
  isStr,
  isArr,
  isUNN,
  isEmptyObj,
  isPromise,
  isEvent,
}
