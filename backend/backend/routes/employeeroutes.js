import express from "express";
import { createEmployee, getEmployees } from "../controllers/employeeController.js";

const router = express.Router();

router.post("/", createEmployee);
router.get("/", getEmployees);

export default router;