const BASE_URL = process.env.REACT_APP_API_URL || "https://ai-resume-analyzer-backend-b7qh.onrender.com/api";
export const resumeApi = {
  /**
   * Upload a PDF resume and get parsed data back
   */
  upload: async (file) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(`${BASE_URL}/resume/upload`, {
      method: "POST",
      body: formData,
    });

    if (!response.ok) throw new Error("Upload failed");
    return response.json();
  },

  /**
   * Get resume by ID
   */
  getResume: async (id) => {
    const response = await fetch(`${BASE_URL}/resume/${id}`);
    if (!response.ok) throw new Error("Resume not found");
    return response.json();
  },

  /**
   * Analyze resume against a job description
   */
  analyze: async (id, jobDescription, requiredSkills) => {
    const response = await fetch(`${BASE_URL}/resume/${id}/analyze`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ jobDescription, requiredSkills }),
    });

    if (!response.ok) throw new Error("Analysis failed");
    return response.json();
  },

  /**
   * Get AI suggestions for a resume
   */
  getSuggestions: async (id) => {
    const response = await fetch(`${BASE_URL}/resume/${id}/suggestions`);
    if (!response.ok) throw new Error("Failed to get suggestions");
    return response.json();
  },
};
