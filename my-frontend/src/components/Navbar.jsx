import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import {
  FaUserCircle, FaBriefcase, FaFileAlt,
  FaSignOutAlt, FaBookmark, FaPlus,
  FaChevronDown, FaChevronUp
} from "react-icons/fa"

function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false)
  const { token, role, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate("/login")
  }

  const handlePersonClick = () => {
    if (!token) {
      navigate("/login")  // not logged in → go to login
    }
    // if logged in → do nothing (already inside the app)
  }

  const menuItems = role === "ROLE_ADMIN"
    ? [
        { label: "Browse Jobs",  icon: <FaBriefcase />, path: "/jobs" },
        { label: "Manage Jobs",  icon: <FaPlus />,      path: "/admin/jobs" },
        { label: "Applications", icon: <FaFileAlt />,   path: "/admin/applications" },
      ]
    : [
        { label: "Browse Jobs",     icon: <FaBriefcase />, path: "/jobs" },
        { label: "My Applications", icon: <FaFileAlt />,   path: "/my-applications" },
        { label: "Saved Jobs",      icon: <FaBookmark />,  path: "/saved-jobs" },
        { label: "My Profile",      icon: <FaUserCircle />,  path: "/profile" }
      ]

  return (
    <header className="main-header">

      {/* Logo */}
      <div className="logo" onClick={() => navigate("/")}>
        Career<span>Stream</span>
      </div>

      {/* Menu */}
      <div className="menu-section">
        <button
          className="menu-dropdown-btn"
          onClick={() => setMenuOpen(!menuOpen)}
        >
          Menu {menuOpen ? <FaChevronUp size={12} /> : <FaChevronDown size={12} />}
        </button>

        {menuOpen && (
          <div className="dropdown-menu">
            {menuItems.map((item, i) => (
              <button
                key={i}
                className="dropdown-item"
                onClick={() => { navigate(item.path); setMenuOpen(false) }}
              >
                <span className="dropdown-icon">{item.icon}</span>
                {item.label}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Right side */}
      <div className="header-right">
        <span className="role-badge">
          {role === "ROLE_ADMIN" ? "Admin" : "User"}
        </span>

        {/* Person icon — click to login if not logged in */}
        <FaUserCircle
          size={26}
          color="#4a90e2"
          style={{ cursor: "pointer" }}
          onClick={handlePersonClick}
          title={token ? "Logged in" : "Click to login"}
        />

        {/* Logout — only show if logged in */}
        {token && (
          <button className="icon-btn" onClick={handleLogout} title="Logout">
            <FaSignOutAlt size={20} />
          </button>
        )}
      </div>

    </header>
  )
}

export default Navbar