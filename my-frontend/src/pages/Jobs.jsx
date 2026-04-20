import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { 
  FaBars, FaUserCircle, FaBriefcase, FaFileAlt, 
  FaSignOutAlt, FaTimes, FaBookmark, FaPlus 
} from 'react-icons/fa';
import '../App.css';

function Jobs() {
  const [view, setView] = useState("home"); // "home" = κενή σελίδα, "jobs" = λίστα δουλειών
  const [jobs, setJobs] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [applying, setApplying] = useState(null);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [savedJobs, setSavedJobs] = useState(() => {
    const localData = localStorage.getItem("savedJobs");
    return localData ? JSON.parse(localData) : [];
  });

  const { token, role, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    fetchJobs();
  }, []);

  const fetchJobs = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/jobs", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (!res.ok) throw new Error("Could not load jobs!");
      const data = await res.json();
      setJobs(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleApply = async (jobId) => {
    try {
      setApplying(jobId);
      const res = await fetch("http://localhost:8080/api/applications", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ jobId })
      });
      if (!res.ok) throw new Error("Could not apply!");
      setSuccess("Applied successfully!");
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(err.message);
    } finally {
      setApplying(null);
    }
  };

  const handleSaveJob = (job) => {
    let updatedSaved;
    if (savedJobs.some(j => j.id === job.id)) {
      updatedSaved = savedJobs.filter(j => j.id !== job.id);
    } else {
      updatedSaved = [...savedJobs, job];
    }
    setSavedJobs(updatedSaved);
    localStorage.setItem("savedJobs", JSON.stringify(updatedSaved));
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="app-wrapper">
      <header className="main-header">
        <div className="left-section">
          <button className="menu-toggle-btn" onClick={() => setIsMenuOpen(!isMenuOpen)}>
            {isMenuOpen ? <FaTimes size={24} /> : <FaBars size={24} />}
          </button>
          <div className="logo" onClick={() => navigate("/jobs")}>
            Career<span>Stream</span>
          </div>
        </div>

        <div className="header-actions">
          <div className="user-info">
            <span className="role-badge">{role === "ROLE_ADMIN" ? "Admin" : "User"}</span>
            <FaUserCircle size={28} color="#4a90e2" />
            <button onClick={handleLogout} className="icon-btn">
              <FaSignOutAlt size={20} />
            </button>
          </div>
        </div>

        <nav className={`side-menu ${isMenuOpen ? "open" : ""}`}>
          <div className="side-menu-content">
            <button onClick={() => { setView("/jobs"); setIsMenuOpen(false); }}>
              <FaBriefcase /> Jobs
            </button>
            {role === "ROLE_USER" && (
              <>
                <button onClick={() => { navigate("/my-applications"); setIsMenuOpen(false); }}>
                  <FaFileAlt /> My Applications
                </button>
                <button onClick={() => { navigate("/saved-jobs"); setIsMenuOpen(false); }}>
                  <FaBookmark /> Αποθηκευμένα
                </button>
              </>
            )}
            {role === "ROLE_ADMIN" && (
              <>
                <div className="admin-divider">Admin Tools</div>
                <button onClick={() => { navigate("/admin/jobs"); setIsMenuOpen(false); }}>Manage Jobs</button>
                <button onClick={() => { navigate("/admin/applications"); setIsMenuOpen(false); }}>Applications</button>
              </>
            )}
          </div>
        </nav>
      </header>

      <main className="main-content">
        {view === "home" ? (
    /* 1. Τι βλέπει ο χρήστης όταν πρωτομπαίνει (Home View) */
        <div className="welcome-view" style={{ textAlign: 'center', marginTop: '50px' }}>
          <h1>Καλώς ήρθατε στο CareerStream</h1>
          <p>Επιλέξτε "Jobs" από το μενού αριστερά για να δείτε τις διαθέσιμες θέσεις εργασίας.</p>
        </div>
      ) : (
    /* 2. Τι βλέπει ο χρήστης όταν πατάει Jobs (Jobs View) */
        <>
          <div className="section-title">
            <h2>Available Jobs</h2>
            <p>Βρείτε το επόμενο βήμα στην καριέρα σας</p>
        </div>

        {error && <p className="error-banner">{error}</p>}
        {success && <p className="success-banner">{success}</p>}

        <div className="jobs-grid">
          {jobs.length === 0 ? (
            <div className="no-data-view">
              <p>📋 No jobs available right now.</p>
              {role === "ROLE_ADMIN" && (
                <button className="create-btn" onClick={() => navigate("/admin/jobs")}>
                  <FaPlus /> Create Job
                </button>
              )}
            </div>
          ) : (
            jobs.map(job => (
              <div className="job-card-glass" key={job.id}>
                {role === "ROLE_USER" && (
                  <button className="save-job-btn" onClick={() => handleSaveJob(job)}>
                    <FaBookmark color={savedJobs.some(j => j.id === job.id) ? "#4a90e2" : "#ccc"} />
                  </button>
                )}
              
                <div className="card-header">
                  <span className="badge-location">📍 {job.location || 'Remote'}</span>
                  <h3>{job.title}</h3>
                </div>
              
                <p className="description">{job.description}</p>
              
                <div className="card-footer">
                  <div className="salary-info">
                    <span className="label">Salary</span>
                    <span className="amount">€{job.salary?.toLocaleString()}</span>
                  </div>
                
                  {role === "ROLE_USER" && (
                    <button 
                      className="apply-btn-modern" 
                      onClick={() => handleApply(job.id)}
                      disabled={applying === job.id}
                    >
                      {applying === job.id ? "Applying..." : "Apply Now"}
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </> /* Κλείσιμο του Fragment */
    )}
  </main>
      </div>
  );
}

export default Jobs;