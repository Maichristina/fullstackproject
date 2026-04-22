import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import { FaCalendarAlt, FaTrash } from "react-icons/fa"

function MyApplications() {
  const [applications, setApplications] = useState([])
  const [loading,      setLoading]      = useState(true)
  const [msg,          setMsg]          = useState("")
  const { token } = useAuth()
  const navigate  = useNavigate()

  useEffect(() => { fetchMyApplications() }, [])

  const fetchMyApplications = async () => {
    try {
      // ✅ CORRECT URL: /api/applications/my
      const res = await fetch("http://localhost:8080/api/applications/my", {
        headers: { "Authorization": `Bearer ${token}` }
      })
      if (!res.ok) throw new Error()
      const data = await res.json()
      setApplications(data)
    } catch {
      console.error("Failed to fetch applications")
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm("Withdraw this application?")) return
    try {
      const res = await fetch(`http://localhost:8080/api/applications/${id}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${token}` }
      })
      if (!res.ok) throw new Error()
      setApplications(applications.filter(a => a.id !== id))
      setMsg("Application withdrawn!")
      setTimeout(() => setMsg(""), 3000)
    } catch {
      setMsg("Could not withdraw application!")
    }
  }

  // Status badge using correct CSS classes
  const statusBadge = (status) => {
    const map = {
      PENDING:  "status-pending",
      ACCEPTED: "status-accepted",
      REJECTED: "status-rejected"
    }
    return (
      <span className={`status-badge ${map[status] || "status-pending"}`}>
        {status}
      </span>
    )
  }

  return (
    <div className="app-wrapper">

      {/* Header */}
      <header className="main-header">
        <div className="logo" onClick={() => navigate("/jobs")}>
          Career<span>Stream</span>
        </div>
        <button className="menu-dropdown-btn" onClick={() => navigate("/jobs")}>
          ← Back to Jobs
        </button>
      </header>

      <main className="main-content">

        <div className="section-title">
          <h2>My Applications</h2>
          <p>Track the status of your job applications</p>
        </div>

        {msg && <p className="success-banner">{msg}</p>}

        {loading ? (
          <p>Loading your applications...</p>

        ) : applications.length === 0 ? (
          <div className="no-data-view">
            <p style={{ fontSize: "2.5rem" }}>📨</p>
            <p>You haven't applied to any jobs yet!</p>
            <button className="create-btn" onClick={() => navigate("/jobs")}>
              Browse Jobs
            </button>
          </div>

        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Job Title</th>
                  <th>Status</th>
                  <th>Applied Date</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {applications.map(app => (
                  <tr key={app.id}>

                    <td><strong>{app.jobTitle}</strong></td>

                    <td>{statusBadge(app.status)}</td>

                    <td style={{ color: "#999", fontSize: "0.85rem" }}>
                      <FaCalendarAlt style={{ marginRight: "0.3rem" }} />
                      {new Date(app.appliedDate).toLocaleDateString()}
                    </td>

                    <td>
                      {/* Only PENDING can be withdrawn */}
                      {app.status === "PENDING" && (
                        <button className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(app.id)}>
                          <FaTrash /> Withdraw
                        </button>
                      )}
                    </td>

                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  )
}

export default MyApplications