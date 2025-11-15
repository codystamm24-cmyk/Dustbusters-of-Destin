import mongoose from "mongoose";

const bookingSchema = new mongoose.Schema({
  customer: { type: mongoose.Schema.Types.ObjectId, ref: "Customer", required: true },
  employee: { type: mongoose.Schema.Types.ObjectId, ref: "Employee" },
  date: { type: Date, required: true },
  address: { type: String, required: true },
  status: { type: String, default: "Pending" }
}, { timestamps: true });

export default mongoose.model("Booking", bookingSchema);
