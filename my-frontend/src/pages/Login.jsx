import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"
import { useAuth } from "../context/AuthContext"  // ← import
import Navbar from "../components/Navbar";

function Login() {
  const [email,    setEmail]    = useState("")
  const [password, setPassword] = useState("")
  const [error,    setError]    = useState("")
  const navigate = useNavigate()
  const { login } = useAuth()  // ← get login function

  const handleLogin = async () => {
    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      })

      if (!res.ok) {
        const data = await res.json().catch(() => null)
        console.log("Status:", res.status, "Body:", data)
        setError(data?.message || "Wrong email or password!")
        return
      }

      const data = await res.json()
      console.log("Full data:", JSON.stringify(data))
      console.log("ROLE FROM BACK:", data.role) 
      console.log("token:",    data.token)
      console.log("role:",     data.role)
      console.log("username:", data.username)
      console.log("full response:", data)

      login(data.token, data.role, data.username)  // ← use this instead of localStorage directly
      if (data.role === "ROLE_ADMIN") {
        navigate("/admin")
      } else {
        navigate("/jobs")
      }
      

    } catch {
      setError("Could not connect to server!")
    }
  }

  return (
    <div className="container">
      <div className="box">
        <h2>Login</h2>
        {error && <p className="error">{error}</p>}
        <input
          placeholder="Email"
          type="email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          autoFocus
        />
        <input
          placeholder="Password"
          type="password"
          value={password}
          onChange={e => setPassword(e.target.value)}
        />
        <button onClick={handleLogin}>Login</button>
        <p>Don't have an account? <Link to="/register">Register</Link></p>
      </div>
    </div>
  )
}

export default Login