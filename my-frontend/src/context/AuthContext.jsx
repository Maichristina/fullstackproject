// src/context/AuthContext.js
import { createContext, useContext, useState } from "react"

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token] = useState(localStorage.getItem("token"))
  const [role]  = useState(localStorage.getItem("role"))
  const [username] = useState(localStorage.getItem("username"))

  const logout = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("role")
    localStorage.removeItem("username")
  }

  return (
    <AuthContext.Provider value={{ token, role, username, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)