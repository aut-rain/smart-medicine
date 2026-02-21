import { createContext, useContext, ReactNode } from 'react'

interface AuthContextType {
  openLoginModal: () => void
  openRegisterModal: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}

export const AuthContext_Provider = AuthContext.Provider
