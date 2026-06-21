import { createContext, useContext } from 'react'

export const FilingMaterialContext = createContext({})

export function FilingMaterialProvider({ value, children }) {
  return <FilingMaterialContext.Provider value={value}>{children}</FilingMaterialContext.Provider>
}

export function useFilingMaterialContext() {
  const ctxValue = useContext(FilingMaterialContext)
  return {
    ...ctxValue,
  }
}