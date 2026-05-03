import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import {
  FaUser, FaGraduationCap, FaLanguage,
  FaTools, FaBriefcase, FaFileUpload, FaSave
} from "react-icons/fa"

function MyProfile() {
  const { token, username } = useAuth()
  const navigate = useNavigate()

  const [msg,     setMsg]     = useState("")
  const [error,   setError]   = useState("")
  const [saving,  setSaving]  = useState(false)
  const [cvName,  setCvName]  = useState("")

  // Profile form state
  const [profile, setProfile] = useState({
    firstName:      "",
    lastName:       "",
    phone:          "",
    city:           "",
    birthDate:      "",
    education:      "",
    languages:      "",
    technicalSkills:"",
    softSkills:     "",
    experience:     "",
    about:          ""
  })

  // Load saved profile from localStorage on mount
  useEffect(() => {
    const saved = localStorage.getItem("userProfile")
    if (saved) {
      setProfile(JSON.parse(saved))
    }
    const savedCv = localStorage.getItem("cvName")
    if (savedCv) setCvName(savedCv)
  }, [])

  const handle = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value })
  }

  const handleSave = (e) => {
    e.preventDefault()
    setSaving(true)

    // Save to localStorage (no backend needed for profile)
    localStorage.setItem("userProfile", JSON.stringify(profile))

    setTimeout(() => {
      setSaving(false)
      setMsg("Profile saved successfully!")
      setTimeout(() => setMsg(""), 3000)
    }, 500)
  }

  const handleCvUpload = (e) => {
    const file = e.target.files[0]
    if (!file) return

    if (file.type !== "application/pdf") {
      setError("Only PDF files are accepted!")
      return
    }
    if (file.size > 5 * 1024 * 1024) {
      setError("File too large! Max 5MB.")
      return
    }

    setCvName(file.name)
    localStorage.setItem("cvName", file.name)
    setMsg("CV uploaded successfully!")
    setTimeout(() => setMsg(""), 3000)
  }

  return (
    <div className="app-wrapper">

        <div className="back-navigation">
          <button
            className="menu-dropdown-btn"
            onClick={() => navigate("/jobs")}
            style={{ marginLeft: "1rem" }}>
            ← Back to Jobs
          </button>
        </div>
       
      

      <main className="main-content" style={{ maxWidth: 800, margin: "0 auto" }}>

        <div className="section-title">
          <h2>My Profile</h2>
          <p>Fill in your details to apply faster to jobs</p>
        </div>

        {msg   && <p className="success-banner">{msg}</p>}
        {error && <p className="error-banner">{error}</p>}

        <form onSubmit={handleSave}>

          {/* ── CV UPLOAD ──────────────────────── */}
          <div className="profile-section">
            <h3 className="profile-section-title">
              <FaFileUpload color="#4a90e2" /> CV / Resume
            </h3>
            <div className="cv-upload-area">
              <label htmlFor="cv-input" className="cv-upload-label">
                {cvName ? (
                  <>
                    <span style={{ fontSize: "1.5rem" }}>📄</span>
                    <span style={{ fontWeight: 600, color: "#222" }}>{cvName}</span>
                    <span style={{ color: "#4a90e2", fontSize: "0.85rem" }}>
                      Click to replace
                    </span>
                  </>
                ) : (
                  <>
                    <span style={{ fontSize: "2rem" }}>☁️</span>
                    <span style={{ fontWeight: 600 }}>Upload your CV</span>
                    <span style={{ color: "#aaa", fontSize: "0.85rem" }}>
                      PDF only, max 5MB
                    </span>
                  </>
                )}
              </label>
              <input
                id="cv-input"
                type="file"
                accept=".pdf"
                onChange={handleCvUpload}
                style={{ display: "none" }}
              />
            </div>
          </div>

          {/* ── PERSONAL INFO ───────────────────── */}
          <div className="profile-section">
            <h3 className="profile-section-title">
              <FaUser color="#4a90e2" /> Personal Information
            </h3>
            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">First Name</label>
                <input name="firstName" value={profile.firstName}
                  onChange={handle} placeholder="" />
              </div>
              <div className="form-group">
                <label className="form-label">Last Name</label>
                <input name="lastName" value={profile.lastName}
                  onChange={handle} placeholder="" />
              </div>
              <div className="form-group">
                <label className="form-label">Phone</label>
                <input name="phone" value={profile.phone}
                  onChange={handle} placeholder="+30 " />
              </div>
              <div className="form-group">
                <label className="form-label">City</label>
                <input name="city" value={profile.city}
                  onChange={handle} placeholder="" />
              </div>
              <div className="form-group">
                <label className="form-label">Date of Birth</label>
                <input name="birthDate" type="date" value={profile.birthDate}
                  onChange={handle} />
              </div>
            </div>

           
          </div>

          {/* ── EDUCATION ───────────────────────── */}
          <div className="profile-section">
            <h3 className="profile-section-title">
              <FaGraduationCap color="#4a90e2" /> Education
            </h3>
            <div className="form-group">
              <label className="form-label">Education Background</label>
              <textarea name="education" value={profile.education}
                onChange={handle} rows={4}
                placeholder={
                  ""                 } />
            </div>
          </div>

          {/* ── LANGUAGES ───────────────────────── */}
          <div className="profile-section">
            <h3 className="profile-section-title">
              <FaLanguage color="#4a90e2" /> Languages
            </h3>
            <div className="form-group">
              <label className="form-label">Languages & Level</label>
              <textarea name="languages" value={profile.languages}
                onChange={handle} rows={3}
                placeholder={
                  "" 
                } />
            </div>
          </div>

          {/* ── SKILLS ──────────────────────────── */}
          <div className="profile-section">
            <h3 className="profile-section-title">
              <FaTools color="#4a90e2" /> Skills
            </h3>
            <div className="form-group">
              <label className="form-label">Technical Skills</label>
              <textarea name="technicalSkills" value={profile.technicalSkills}
                onChange={handle} rows={3}
                placeholder="Java, Spring Boot, React, Git..." />
            </div>
            <div className="form-group">
              <label className="form-label">Soft Skills</label>
              <textarea name="softSkills" value={profile.softSkills}
                onChange={handle} rows={3}
                placeholder="Team collaboration, Problem solving, Communication..." />
            </div>
          </div>

          {/* ── EXPERIENCE ──────────────────────── */}
          <div className="profile-section">
            <h3 className="profile-section-title">
              <FaBriefcase color="#4a90e2" /> Work Experience
            </h3>
            <div className="form-group">
              <label className="form-label">Work Experience</label>
              <textarea name="experience" value={profile.experience}
                onChange={handle} rows={5}
                placeholder={
                 ""
                } />
            </div>
          </div>

          {/* ── SAVE BUTTON ─────────────────────── */}
          <div style={{ display: "flex", justifyContent: "flex-end",
                        gap: "1rem", marginTop: "1rem" }}>
            <button type="button" className="btn-ghost"
              onClick={() => navigate("/jobs")}>
              Cancel
            </button>
            <button type="submit" className="btn-filled"
              disabled={saving}>
              <FaSave style={{ marginRight: "0.4rem" }} />
              {saving ? "Saving..." : "Save Profile"}
            </button>
          </div>

        </form>
      </main>
    </div>
  )
}

export default MyProfile