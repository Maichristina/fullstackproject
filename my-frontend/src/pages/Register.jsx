import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"

function Register() {
  const [name, setName] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const navigate = useNavigate()

  const handleRegister = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password })
      })

      if (!res.ok) {
        setError("Registration failed. Try again!")
        return
      }

      navigate("/login")

    } catch (err) {
      setError("Could not connect to server!")
    }
  }

  return (
    <div className="container">
      <div className="box">
        <h2>Register</h2>
        {error && <p className="error">{error}</p>}
        <input
          placeholder="Full Name"
          value={name}
          onChange={e => setName(e.target.value)}
        />
        <input
          placeholder="Email"
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