// src/context/AuthContext.js
import { createContext, useContext, useState } from "react"
import PropTypes from "prop-types";

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
AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};
export const useAuth = () => useContext(AuthContext)