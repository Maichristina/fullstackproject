import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext"

function Home() {
  const navigate = useNavigate();
  const { role }  = useAuth() 

  return (
    <div className="welcome-view">
      <h1>Welcome to CareerStream</h1>
      <p>Your journey to a new career starts here.</p>
      {role === "ROLE_USER" && (
        <button 
          className="create-btn" 
          onClick={() => navigate("/profile")}
        >
          My Profile
        </button>
      )}  

      {role === "ROLE_ADMIN" && (
          <button
            className="create-btn"
            onClick={() => navigate("/admin/jobs")}>
            Manage Jobs
          </button>
        )}
    </div>
  );
}

export default Home;