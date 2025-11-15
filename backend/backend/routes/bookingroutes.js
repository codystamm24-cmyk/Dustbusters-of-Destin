import express from "express";
import { createBooking, getBookings } from "../controllers/bookingController.js";

const router = express.Router();

router.post("/", createBooking);
router.get("/", getBookings);

export default router;