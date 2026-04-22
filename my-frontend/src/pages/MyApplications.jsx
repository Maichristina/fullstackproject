import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { FaBriefcase, FaCalendarAlt, FaMapMarkerAlt } from 'react-icons/fa';

function MyApplications() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();

  useEffect(() => {
    const fetchMyApplications = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/applications/my-applications", {
          headers: { "Authorization": `Bearer ${token}` }
        });
        if (res.ok) {
          const data = await res.json();
          setApplications(data);
        }
      } catch (err) {
        console.error("Failed to fetch applications", err);
      } finally {
        setLoading(false);
      }
    };

    fetchMyApplications();
  }, [token]);

  return (
    <div className="app-wrapper">
      <main className="main-content">
        <div className="section-title">
          <h2>My Applications</h2>
          <p>Track the status of your job searches</p>
        </div>

        {loading ? (
          <p>Loading your applications...</p>
        ) : applications.length === 0 ? (
          <div className="no-data-view">
            <p>You haven't applied to any jobs yet.</p>
          </div>
        ) : (
          <div className="jobs-grid">
            {applications.map((app) => (
              <div className="job-card-glass" key={app.id}>
                <div className="card-header">
                  <span className="badge-location">
                    <FaMapMarkerAlt /> {app.job?.location || "Remote"}
                  </span>
                  <h3>{app.job?.title}</h3>
                </div>
                
                <div className="application-details" style={{ margin: "1rem 0", fontSize: "0.9rem" }}>
                  <p><FaCalendarAlt /> Applied on: {new Date(app.appliedAt).toLocaleDateString()}</p>
                </div>

                <div className="card-footer">
                  <span className="status-badge" style={{
                    padding: "5px 12px",
                    borderRadius: "15px",
                    background: "#4a90e2",
                    color: "white",
                    fontSize: "0.8rem"
                  }}>
                    Status: Pending
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

export default MyApplications;