import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { FaPlus, FaTrash } from 'react-icons/fa';

function ApplicationForm() {
  const { jobId } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    birthDate: "",
    education: [""], 
    experience: [""]
  });

  // Διαχείριση Δυναμικών Πεδίων
  const handleAdd = (field) => {
    setFormData({ ...formData, [field]: [...formData[field], ""] });
  };

  const handleRemove = (field, index) => {
    const list = [...formData[field]];
    list.splice(index, 1);
    setFormData({ ...formData, [field]: list });
  };

  const handleChange = (field, index, value) => {
    const list = [...formData[field]];
    list[index] = value;
    setFormData({ ...formData, [field]: list });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Εδώ στέλνεις τα δεδομένα στο Backend
    console.log("Submit to Job ID:", jobId, formData);
    // Μετά το επιτυχές submit:
    navigate("/jobs");
  };

  return (
    <div className="app-wrapper">
      <main className="main-content">
        <div className="job-card-glass" style={{ maxWidth: "600px", margin: "2rem auto" }}>
          <h2>Job Application</h2>
          <form onSubmit={handleSubmit} className="auth-form">
            <input type="text" placeholder="First Name" required 
              onChange={e => setFormData({...formData, firstName: e.target.value})} />
            
            <input type="text" placeholder="Last Name" required 
              onChange={e => setFormData({...formData, lastName: e.target.value})} />

            <label style={{alignSelf: 'flex-start', marginLeft: '10%', fontSize: '0.9rem'}}>Birth Date</label>
            <input type="date" required 
              onChange={e => setFormData({...formData, birthDate: e.target.value})} />

            {/* EDUCATION SECTION */}
            <div className="dynamic-section" style={{width: '80%', margin: '1rem 0'}}>
              <h4>Education</h4>
              {formData.education.map((edu, idx) => (
                <div key={idx} style={{display: 'flex', gap: '5px', marginBottom: '10px'}}>
                  <input type="text" placeholder="Degree / Institution" value={edu} 
                    onChange={e => handleChange("education", idx, e.target.value)} />
                  {formData.education.length > 1 && (
                    <button type="button" onClick={() => handleRemove("education", idx)} className="icon-btn"><FaTrash color="red"/></button>
                  )}
                </div>
              ))}
              <button type="button" className="menu-dropdown-btn" onClick={() => handleAdd("education")}><FaPlus /> Add More</button>
            </div>

            {/* EXPERIENCE SECTION */}
            <div className="dynamic-section" style={{width: '80%', margin: '1rem 0'}}>
              <h4>Experience</h4>
              {formData.experience.map((exp, idx) => (
                <div key={idx} style={{display: 'flex', gap: '5px', marginBottom: '10px'}}>
                  <input type="text" placeholder="Job Title / Company" value={exp} 
                    onChange={e => handleChange("experience", idx, e.target.value)} />
                  {formData.experience.length > 1 && (
                    <button type="button" onClick={() => handleRemove("experience", idx)} className="icon-btn"><FaTrash color="red"/></button>
                  )}
                </div>
              ))}
              <button type="button" className="menu-dropdown-btn" onClick={() => handleAdd("experience")}><FaPlus /> Add More</button>
            </div>

            <button type="submit" className="apply-btn-modern" style={{width: '80%'}}>Submit Application</button>
          </form>
        </div>
      </main>
    </div>
  );
}

export default ApplicationForm;