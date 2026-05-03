import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./components/Navbar"; // Εισαγωγή του Navbar
import Home from "./pages/Home";           // Η νέα σελίδα Home
import Jobs from "./pages/Jobs";
import Login from "./pages/Login";
import Register from "./pages/Register";
import SavedJobs from "./pages/SavedJobs";
import ApplicationForm from "./pages/ApplicationForm";
import MyApplications from "./pages/MyApplications";
import AdminJobs from "./pages/admin/AdminJobs";
import AdminApplications from "./pages/admin/AdminApplications";
import { useAuth } from "./context/AuthContext";
import MyProfile from "./pages/MyProfile";
import PropTypes from "prop-types";



// Προστατευμένο Route για Admins
function AdminRoute({ children }) {
  const { token, role } = useAuth()
  if (!token) return <Navigate to="/login" />
  if (role !== "ROLE_ADMIN") return <Navigate to="/jobs" />
  return children
}

AdminRoute.propTypes = {
  children: PropTypes.node.isRequired,
};


function App() {
  return (
    <BrowserRouter>
      {/* Το Navbar μένει ΕΞΩ από τα Routes για να φαίνεται ΠΑΝΤΑ */}
      <div className="app-wrapper"> 
        <Navbar />
        <Routes>
          {/* Η αρχική σελίδα πλέον δείχνει το Home component */}
          <Route path="/" element={<Home />} />
          <Route path="/admin/jobs" element={<AdminRoute><AdminJobs /></AdminRoute>} />
          <Route path="/admin/applications" element={<AdminRoute><AdminApplications /></AdminRoute>} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/jobs" element={<Jobs />} />
          <Route path="/profile" element={<MyProfile />} />
          <Route path="/saved-jobs" element={<SavedJobs />} />
          <Route path="/apply/:jobId" element={<ApplicationForm />} />
          <Route path="/my-applications" element={<MyApplications />} />
          
          {/* Fallback: Αν κάποιος γράψει λάθος URL, στείλε τον στο Home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;