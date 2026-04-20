import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"

function Register() {
  const [username, setUsername] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const navigate = useNavigate()

  const handleRegister = async () => {
    setError("")

    // Frontend validation BEFORE sending to Spring Boot
    if (username.length < 3) {
      setError("Username must be at least 3 characters!")
      return
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters!")
      return
    }

    try {
      const res = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password })  // ← fixed!
      })

      if (!res.ok) {
        const data = await res.json().catch(() => null)
        setError(data?.message || "Registration failed. Try again!")
        return
      }

      const data = await res.json()
      // Save token — user is logged in immediately after register!
      localStorage.setItem("token",    data.token)
      localStorage.setItem("username", data.username)
      localStorage.setItem("role",     data.role)
      navigate("/jobs")     // ← go to jobs, not login (already logged in!)

    } catch (_err) {        // ← underscore fixes ESLint warning
      setError("Could not connect to server!")
    }
  }

  return (
    <div className="container">
      <div className="box">
        <h2>Register</h2>
        {error && <p className="error">{error}</p>}

        <input
          placeholder="Username"       // ← was Full Name
          value={username}
          onChange={e => setUsername(e.target.value)}
        />
        <input
          placeholder="Email"
          type="email"                 // ← added type email
          value={email}
          onChange={e => setEmail(e.target.value)}
        />
        <input
          placeholder="Password"
          type="password"
          value={password}
          onChange={e => setPassword(e.target.value)}
        />
        <button onClick={handleRegister}>Register</button>
        <p>Already have an account? <Link to="/login">Login</Link></p>
      </div>
    </div>
  )
}

export default Register