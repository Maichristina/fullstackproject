import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { FaBookmark, FaTrash } from "react-icons/fa"
import Navbar from "../components/Navbar"
import { useAuth } from "../context/AuthContext"

function SavedJobs() {
  const [savedJobs, setSavedJobs] = useState([])
  const [applying,  setApplying]  = useState(null)
  const [success,   setSuccess]   = useState("")
  const [error,     setError]     = useState("")
  const navigate = useNavigate()
  const { token } = useAuth()

  // Load saved jobs from localStorage when page opens
  useEffect(() => {
    const data = localStorage.getItem("savedJobs")
    if (data) setSavedJobs(JSON.parse(data))
  }, [])

  // Remove job from saved list
  const handleRemove = (jobId) => {
    const updated = savedJobs.filter(j => j.id !== jobId)
    setSavedJobs(updated)
    localStorage.setItem("savedJobs", JSON.stringify(updated))
  }

  // Apply directly from saved jobs
  const handleApply = async (jobId) => {
    setApplying(jobId)
    setError("")
    try {
      const res = await fetch("/api/applications", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(token && { Authorization: `Bearer ${token}` })
      },
      body: JSON.stringify({ jobId })
    })

    if (!res.ok) {
      const data = await res.json().catch(() => null)
      setError(data?.message || "Could not apply. Maybe already applied?")
      return
    }

      // Remove from saved after applying
    handleRemove(jobId)
    setSuccess("Applied successfully! ✓")
    setTimeout(() => setSuccess(""), 3000)

    } catch {
      setError("Could not connect to server!")
    } finally {
      setApplying(null)
    }
  }

  return (
    <div className="app-wrapper">
      <main className="main-content">
        <div className="back-navigation">
          <button
            className="menu-dropdown-btn"
            onClick={() => navigate("/jobs")}
            style={{ marginLeft: "1rem" }}>
            ← Back to Jobs
          </button>
        </div>
   
        <div className="section-title">
          <h2>
            <FaBookmark color="#4a90e2" style={{ marginRight: "0.5rem" }} />
            Saved Jobs
          </h2>
          <p>{savedJobs.length} job{savedJobs.length !== 1 ? "s" : ""} saved</p>
        </div>

        {error   && <p className="error-banner">{error}</p>}
        {success && <p className="success-banner">{success}</p>}

        {savedJobs.length === 0 ? (
          <div className="no-data-view">
            <p style={{ fontSize: "2.5rem" }}>🔖</p>
            <p style={{ marginTop: "1rem" }}>No saved jobs yet!</p>
            <button
              className="create-btn"
              onClick={() => navigate("/jobs")}
              style={{ marginTop: "1rem" }}>
              Browse Jobs
            </button>
          </div>
        ) : (
          <div className="jobs-grid">
            {savedJobs.map(job => (
              <div className="job-card-glass" key={job.id}>

                {/* Remove from saved button */}
                <button
                  className="save-job-btn"
                  onClick={() => handleRemove(job.id)}
                  title="Remove from saved">
                  <FaBookmark color="#4a90e2" />
                </button>

                <div className="card-header">
                  <span className="badge-location">
                    📍 {job.location || "Remote"}
                  </span>
                  <h3>{job.title}</h3>
                </div>

                <p className="description">
                  {job.description?.length > 120
                    ? job.description.slice(0, 120) + "..."
                    : job.description}
                </p>

                <div className="card-footer">
                  <div className="salary-info">
                    <span className="label">Salary</span>
                    <span className="amount">
                      €{job.salary?.toLocaleString() || "—"}
                    </span>
                  </div>

                  <div style={{ display: "flex", gap: "0.5rem" }}>
                    {/* Remove button */}
                    <button
                      onClick={() => handleRemove(job.id)}
                      style={{
                        padding: "0.5rem 0.8rem",
                        borderRadius: "8px",
                        border: "1px solid #ffcccc",
                        background: "#fff5f5",
                        color: "#e53935",
                        cursor: "pointer",
                        display: "flex",
                        alignItems: "center",
                        gap: "0.3rem",
                        fontSize: "0.82rem"
                      }}>
                      <FaTrash size={12} /> Remove
                    </button>

                    {/* Apply button */}
                    <button
                      className="apply-btn-modern"
                      onClick={() => handleApply(job.id)}
                      disabled={applying === job.id}>
                      {applying === job.id ? "Applying..." : "Apply Now"}
                    </button>
                  </div>
                </div>

              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}

export default SavedJobs