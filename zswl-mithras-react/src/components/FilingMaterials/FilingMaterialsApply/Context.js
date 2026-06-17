import { createContext, useContext } from 'react'

export const FillingMaterialContext = createContext({})

export function FillingMaterialProvider({ value, children }) {
  return <FillingMaterialContext.Provider value={value}>{children}</FillingMaterialContext.Provider>
}

export function useFillingMaterialContext() {
  const ctxValue = useContext(FillingMaterialContext)
  return {
    ...ctxValue,
  }
}