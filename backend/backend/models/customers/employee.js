import mongoose from "mongoose";

const employeeSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, unique: true },
  role: { type: String, default: "Cleaner" },
  phone: { type: String }
}, { timestamps: true });

export default mongoose.model("Employee", employeeSchema);