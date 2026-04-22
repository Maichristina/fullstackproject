import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { 
  FaUserCircle, FaBriefcase, FaFileAlt, 
  FaSignOutAlt, FaBookmark, FaPlus, FaChevronDown, FaChevronUp
} from 'react-icons/fa';
import '../App.css';

function Jobs() {
  const [view, setView]           = useState("home");
  const [jobs, setJobs]           = useState([]);
  const [error, setError]         = useState("");
  const [success, setSuccess]     = useState("");
  const [applying, setApplying]   = useState(null);
  const [menuOpen, setMenuOpen]   = useState(false);
  const [savedJobs, setSavedJobs] = useState(() => {
    const localData = localStorage.getItem("savedJobs");
    return localData ? JSON.parse(localData) : [];
  });

  const { token, role, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => { fetchJobs(); }, []);

  const fetchJobs = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/jobs", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (!res.ok) throw new Error("Could not load jobs!");
      const data = await res.json();
      setJobs(data);
    } catch (_err) {
      setError("Could not load jobs!");
    }
  };



  const handleSaveJob = (job) => {
    const updated = savedJobs.some(j => j.id === job.id)
      ? savedJobs.filter(j => j.id !== job.id)
      : [...savedJobs, job];
    setSavedJobs(updated);
    localStorage.setItem("savedJobs", JSON.stringify(updated));
  };

  const handleLogout = () => { logout(); navigate("/login"); };

  // Menu items based on role
  const menuItems = role === "ROLE_ADMIN"
    ? [
        { label: "Browse Jobs",    icon: <FaBriefcase />, action: () => { setView("jobs");  setMenuOpen(false); } },
        { label: "Manage Jobs",    icon: <FaPlus />,      action: () => { navigate("/admin/jobs");         setMenuOpen(false); } },
        { label: "Applications",   icon: <FaFileAlt />,   action: () => { navigate("/admin/applications"); setMenuOpen(false); } },
      ]
    : [
        { label: "Browse Jobs",       icon: <FaBriefcase />, action: () => { setView("jobs");               setMenuOpen(false); } },
        { label: "My Applications",   icon: <FaFileAlt />,   action: () => { navigate("/my-applications");  setMenuOpen(false); } },
        { label: "Saved Jobs",        icon: <FaBookmark />,  action: () => { navigate("/saved-jobs");        setMenuOpen(false); } },
      ];

  return (
    <div className="app-wrapper">

      {/* ── HEADER ───────────────────────────────── */}
      <header className="main-header">

        {/* Logo */}
        <div className="logo" onClick={() => { setView("home"); setMenuOpen(false); }}>
          Career<span>Stream</span>
        </div>

        {/* Menu Button — sits below logo */}
        <div className="menu-section">
          <button
            className="menu-dropdown-btn"
            onClick={() => setMenuOpen(!menuOpen)}
          >
            Menu {menuOpen ? <FaChevronUp size={12}/> : <FaChevronDown size={12}/>}
          </button>

          {/* Dropdown */}
          {menuOpen && (
            <div className="dropdown-menu">
              {menuItems.map((item, i) => (
                <button key={i} className="dropdown-item" onClick={item.action}>
                  <span className="dropdown-icon">{item.icon}</span>
                  {item.label}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Right side — user info + logout */}
        <div className="header-right">
          <span className="role-badge">
            {role === "ROLE_ADMIN" ? "Admin" : "User"}
          </span>
          <FaUserCircle size={26} color="#4a90e2" />
          <button className="icon-btn" onClick={handleLogout} title="Logout">
            <FaSignOutAlt size={20} />
          </button>
        </div>

      </header>

      {/* ── MAIN CONTENT ─────────────────────────── */}
      <main className="main-content">

        {/* HOME VIEW */}
        {view === "home" && (
          <div className="welcome-view">
            <h1>Welcome to CareerStream</h1>
            <p>Click <strong>Menu</strong> to browse available jobs.</p>
          </div>
        )}

        {/* JOBS VIEW */}
        {view === "jobs" && (
          <>
            <div className="section-title">
              <h2>Available Jobs</h2>
              <p>Find your next career step</p>
            </div>

            {error   && <p className="error-banner">{error}</p>}
            {success && <p className="success-banner">{success}</p>}

            {jobs.length === 0 ? (
              <div className="no-data-view">
                <p>📋 No jobs available right now.</p>
                {role === "ROLE_ADMIN" && (
                  <button className="create-btn"
                    onClick={() => navigate("/admin/jobs")}>
                    <FaPlus /> Create First Job
                  </button>
                )}
              </div>
            ) : (
              <div className="jobs-grid">
                {jobs.map(job => (
                  <div className="job-card-glass" key={job.id}>

                    {/* Save button — USER only */}
                    {role === "ROLE_USER" && (
                      <button 
                        className="apply-btn-modern" 
                        onClick={() => navigate(`/apply/${job.id}`)}
                      >
                        View & Apply
                      </button>
                    )}

                    <div className="card-header">
                      <span className="badge-location">
                        📍 {job.location || "Remote"}
                      </span>
                      <h3>{job.title}</h3>
                    </div>

                    <p className="description">{job.description}</p>

                    <div className="card-footer">
                      <div className="salary-info">
                        <span className="label">Salary</span>
                        <span className="amount">
                          €{job.salary?.toLocaleString() || "—"}
                        </span>
                      </div>

                    
                    </div>

                  </div>
                ))}
              </div>
            )}
          </>
        )}

      </main>
    </div>
  );
}

export default Jobs;