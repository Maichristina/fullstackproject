import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { FaPlus } from "react-icons/fa";
import "../App.css";

function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [applying, setApplying] = useState(null);

  const { token, role } = useAuth();
  console.log("TOKEN:", token)
  console.log("ROLE:", role)
  const navigate = useNavigate();

  useEffect(() => { fetchJobs() }, []);

  const fetchJobs = async () => {
    try {
      const res = await fetch("/api/jobs", {
      headers: {
        ...(token && { Authorization: `Bearer ${token}` })
      }
      })
      if (!res.ok) throw new Error();
      const data = await res.json();
      setJobs(data);
    } catch {
      setError("Could not load jobs!");
    }
  };

  return (
    <main className="main-content">
      <div className="section-title">
        <h2>Available Jobs</h2>
        <p>Find your next career step</p>
      </div>

      {error && <p className="error-banner">{error}</p>}
      {success && <p className="success-banner">{success}</p>}

      {jobs.length === 0 ? (
        <div className="no-data-view">
          <p>📋 No jobs available right now.</p>
          {role === "ROLE_ADMIN" && (
            <button className="create-btn" onClick={() => navigate("/admin/jobs")}>
              <FaPlus /> Create First Job
            </button>
          )}
        </div>
      ) : (
        <div className="jobs-grid">
          {jobs.map(job => (
            <div className="job-card-glass" key={job.id}>
              <div className="card-header">
                <span className="badge-location">📍 {job.location || "Remote"}</span>
                <h3>{job.title}</h3>
              </div>
              <p className="description">{job.description}</p>
              <div className="card-footer">
                <div className="salary-info">
                  <span className="label">Salary</span>
                  <span className="amount">€{job.salary?.toLocaleString() || "—"}</span>
                </div>
                {role !== "ROLE_ADMIN" && (
                  <button
                    className="apply-btn-modern"
                    onClick={() => navigate(`/apply/${job.id}`)}
                    disabled={applying === job.id}
                  >
                    {applying === job.id ? "Applying..." : "View & Apply"}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </main>
  );
}

export default Jobs;