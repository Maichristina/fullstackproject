// src/context/AuthContext.js
import { createContext, useContext, useState } from "react"
import PropTypes from "prop-types"

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token,    setToken]    = useState(localStorage.getItem("token"))
  const [role,     setRole]     = useState(localStorage.getItem("role"))
  const [username, setUsername] = useState(localStorage.getItem("username"))

  const login = (newToken, newRole, newUsername) => {
    localStorage.setItem("token",    newToken)
    localStorage.setItem("role",     newRole)
    localStorage.setItem("username", newUsername)
    setToken(newToken)
    setRole(newRole)
    setUsername(newUsername)
  }

  const logout = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("role")
    localStorage.removeItem("username")
    setToken(null)
    setRole(null)
    setUsername(null)
  }

  return (
    <AuthContext.Provider value={{ token, role, username, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
}

export const useAuth = () => useContext(AuthContext)