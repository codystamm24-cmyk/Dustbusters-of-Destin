// server/routes/jobs.js
import express from "express";
import Job from "../models/Job.js";
import auth from "../middleware/auth.js";
import admin from "../middleware/admin.js";

const router = express.Router();

// GET /api/jobs/         -> list public jobs/services
router.get("/", async (req, res) => {
  try {
    const jobs = await Job.find({}).sort({ createdAt: -1 });
    res.json({ success: true, data: jobs });
  } catch (err) {
    console.error("Jobs list error:", err);
    res.status(500).json({ success: false, message: "Server error" });
  }
});

// GET /api/jobs/:id      -> single job
router.get("/:id", async (req, res) => {
  try {
    const job = await Job.findById(req.params.id);
    if (!job) return res.status(404).json({ success: false, message: "Job not found" });
    res.json({ success: true, data: job });
  } catch (err) {
    console.error("Jobs get error:", err);
    res.status(500).json({ success: false, message: "Server error" });
  }
});

// POST /api/jobs/        -> admin create job/service
router.post("/", auth, admin, async (req, res) => {
  try {
    const job = await Job.create(req.body);
    res.status(201).json({ success: true, data: job });
  } catch (err) {
    console.error("Jobs create error:", err);
    res.status(500).json({ success: false, message: "Server error" });
  }
});

// PUT /api/jobs/:id      -> admin update
router.put("/:id", auth, admin, async (req, res) => {
  try {
    const job = await Job.findByIdAndUpdate(req.params.id, req.body, { new: true });
    res.json({ success: true, data: job });
  } catch (err) {
    console.error("Jobs update error:", err);
    res.status(500).json({ success: false, message: "Server error" });
  }
});

// DELETE /api/jobs/:id
router.delete("/:id", auth, admin, async (req, res) => {
  try {
    await Job.findByIdAndDelete(req.params.id);
    res.json({ success: true, message: "Job removed" });
  } catch (err) {
    console.error("Jobs delete error:", err);
    res.status(500).json({ success: false, message: "Server error" });
  }
});

export default router;