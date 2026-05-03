// src/context/AuthContext.js
import { createContext, useContext, useState, useEffect } from "react"
import PropTypes from "prop-types"

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token,    setToken]    = useState(localStorage.getItem("token"))
  const [role,     setRole]     = useState(localStorage.getItem("role"))
  
  const [username, setUsername] = useState(localStorage.getItem("username"))



  useEffect(() => {
    // Check if token is expired on app load
    const token = localStorage.getItem("token")
    if (token) {
      try {
        // JWT has 3 parts: header.payload.signature
        // Payload is base64 encoded — decode it to read expiry
        const payload = JSON.parse(atob(token.split(".")[1]))
        const expiry  = payload.exp * 1000  // convert to milliseconds

        if (Date.now() > expiry) {
          // Token is expired → clear everything!
          console.log("Token expired — logging out")
          localStorage.clear()
          setToken(null)
          setRole(null)
          setUsername(null)
        }
      } catch {
        // Invalid token format → clear it
        localStorage.clear()
      }
    }
  }, [])

  const login = (newToken, newRole, newUsername) => {

    localStorage.setItem("token",    newToken)
    localStorage.setItem("role",     newRole)
    localStorage.setItem("username", newUsername)
    setToken(newToken)
    setRole(newRole)
    setUsername(newUsername)
  }

  const saveUser = (data) => {
  localStorage.setItem("token",    data.token)
  localStorage.setItem("role",     data.role)
  localStorage.setItem("username", data.username)
  
  setToken(data.token)
  setRole(data.role)
  setUsername(data.username)
  }
  

  const logout = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("role")
    localStorage.removeItem("username")
    setToken(null)
    setRole(null)
    setUsername(null)
  }


  const isAdmin = () => role === "ROLE_ADMIN"
  const isUser  = () => role === "ROLE_USER"  

  return (
    <AuthContext.Provider value={{ token, role, username, login, logout,isAdmin, isUser }}>
    
      {children}
    </AuthContext.Provider>
  )
}

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
}

export const useAuth = () => useContext(AuthContext)