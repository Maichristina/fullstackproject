import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"

function Login() {
  const [email, setEmail] = useState("")  
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const navigate = useNavigate()

  const handleLogin = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      })

        if (!res.ok) {
        const data = await res.json().catch(() => null)
        setError(data?.message || "Wrong email or password!")
        return
      }

   

      const data = await res.json()
      localStorage.setItem("token",    data.token)
      localStorage.setItem("username", data.username)  // ← save these too
      localStorage.setItem("role",     data.role)
      navigate("/jobs")

    } catch  {
      setError("Could not connect to server!")
    }
  }

  return (
    <div className="container">
      <div className="box">
        <h2>Login</h2>
        {error && <p className="error">{error}</p>}
        <input
          placeholder="email"
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