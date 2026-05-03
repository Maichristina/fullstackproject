import { useState, useEffect } from "react"
import { useAuth } from "../../context/AuthContext"

function AdminApplications() {
  const [apps,    setApps]    = useState([])
  const [loading, setLoading] = useState(true)
  const [msg,     setMsg]     = useState("")
  const [filter,  setFilter]  = useState("ALL")
  const { token } = useAuth()

  useEffect(() => { fetchApps() }, [])

  const fetchApps = async () => {
    try {
      const res = await fetch("/api/applications", {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      if (!res.ok) throw new Error()
      const data = await res.json()
      setApps(data)
    } catch {
      setMsg("Could not load applications!")
    } finally {
      setLoading(false)
    }
  }

  const changeStatus = async (id, status) => {
    try {
       const res = await fetch(
        `/api/applications/${id}/status?status=${status}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      if (!res.ok) throw new Error()
      const updated = await res.json()
      setApps(apps.map(a => a.id === id ? updated : a))
      setMsg(`Status updated to ${status}!`)
      setTimeout(() => setMsg(""), 3000)
    } catch {
      setMsg("Could not update status!")
    }
  }

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

  const filtered = filter === "ALL"
    ? apps
    : apps.filter(a => a.status === filter)

  const count = (s) => s === "ALL"
    ? apps.length
    : apps.filter(a => a.status === s).length

  if (loading) return <p style={{ padding: "2rem" }}>Loading...</p>

  return (
    <main className="main-content">
      <div className="section-title">
        <h2>All Applications</h2>
        <p>{apps.length} total applications</p>
      </div>

      {msg && <p className="success-banner">{msg}</p>}

      {/* Filter buttons */}
      <div className="filter-bar">
        {["ALL", "PENDING", "ACCEPTED", "REJECTED"].map(s => (
          <button key={s}
            className={`filter-btn ${filter === s ? "active" : ""}`}
            onClick={() => setFilter(s)}>
            {s} ({count(s)})
          </button>
        ))}
      </div>

      {filtered.length === 0 ? (
        <div className="no-data-view">
          <p style={{ fontSize: "2rem" }}>📋</p>
          <p>No applications found!</p>
        </div>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Applicant</th>
                <th>Job</th>
                <th>Status</th>
                <th>Applied</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(app => (
                <tr key={app.id}>
                  <td>
                    {/* ✅ FIXED: applicantUsername not candidateName */}
                    <strong>{app.applicantUsername}</strong>
                  </td>
                  <td>{app.jobTitle}</td>
                  <td>{statusBadge(app.status)}</td>
                  <td style={{ color: "#999", fontSize: "0.85rem" }}>
                    {new Date(app.appliedDate).toLocaleDateString()}
                  </td>
                  <td>
                    <div style={{ display: "flex", gap: "0.5rem" }}>
                      {app.status === "PENDING" && (
                        <>
                          <button className="btn btn-success btn-sm"
                            onClick={() => changeStatus(app.id, "ACCEPTED")}>
                            Accept
                          </button>
                          <button className="btn btn-danger btn-sm"
                            onClick={() => changeStatus(app.id, "REJECTED")}>
                            Reject
                          </button>
                        </>
                      )}
                      {app.status !== "PENDING" && (
                        <button className="btn btn-outline btn-sm"
                          onClick={() => changeStatus(app.id, "PENDING")}>
                          Reset
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  )
}

export default AdminApplications